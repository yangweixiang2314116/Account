package com.example.account;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.activeandroid.ActiveAndroid;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.account.wxapi.WXEntryActivity;
import com.example.module.Account;
import com.example.module.AccountRestClient;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.melnykov.fab.FloatingActionButton;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;
import cz.msebera.android.httpclient.Header;

@SuppressWarnings("deprecation")
public class AccountTotalActivity extends AppCompatActivity  implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener
{

    private SwipeMenuListView m_TotalAllAccountList = null;
    private ListView m_SlideMenuList = null;

    private FloatingActionButton m_AddNewAccountButton = null;
    private AccountTotalDetailListAdapter m_DetailListAdapter = null;
    private Context mContext = null;
    private TextView m_TotalCostText = null;
    private TextView m_CurrentTimeText = null;
    private Double m_CurrentTotalCost = 0.0;
    private Menu m_OptionsMenu = null;
    private AccountSyncTask m_SyncTask = null;
    private   SlidingMenu m_Menu  = null;
    private  RelativeLayout m_LoginLayout = null;
    private Toolbar  m_ToolBar = null;
    private SharedPreferences mSharedPreferences = null;
    private BroadcastReceiver mReceiver = null;
    protected ArrayList<Account> mDetailListDataSource = new ArrayList<Account>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_total);
        m_ToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(m_ToolBar);

        mContext = this;
        m_SyncTask = new AccountSyncTask(mContext);

        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        Log.i(Constants.TAG, "------AccountTotalActivity----onCreate -----");

        SMSSDK.initSDK(this, Constants.ACCOUNT_LOGIN_SMS_APP_KEY, Constants.ACCOUNT_LOGIN_SMS_APP_SECRET);

        ActiveAndroid.setLoggingEnabled(false);

        m_InitSwipeMenuList();

        m_InitAddButton();

        //inital current time
        m_CurrentTimeText = (TextView) findViewById(R.id.total_date_title);
        String sCurrentDate = AccountCommonUtil.ConverDateToString(System.currentTimeMillis());
        m_CurrentTimeText.setText(sCurrentDate);

        m_InitSlidingMenu();

        m_InitSlidingMenuContent();

        m_InitReceiver();
        new PrepareTask().execute();

    }

    private  void m_InitReceiver()
    {
         mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.INTENT_NOTIFY_ACCOUNT_CHANGE)) {
                    Log.i(Constants.TAG, "The AccountTotalActivity---->m_InitReceiver");
                    //start load all account from DB
                    new PrepareTask().execute();
                }
            }
        };

        IntentFilter filter = new IntentFilter(Constants.INTENT_NOTIFY_ACCOUNT_CHANGE);
        registerReceiver(mReceiver, filter);

        return ;
    }

    protected void onStart(){
        super.onStart();
        Log.i(Constants.TAG, "The AccountTotalActivity---->onStart");
    }

    protected void onRestart(){
        super.onRestart();
        //start load all account from DB
        //new PrepareTask().execute();
        Log.i(Constants.TAG, "The AccountTotalActivity---->onReatart");
    }

    protected void onResume() {
        super.onResume();
        Log.i(Constants.TAG, "The AccountTotalActivity---->onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mReceiver = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        m_OptionsMenu = menu;
        getMenuInflater().inflate(R.menu.account_total, menu);
        Log.i(Constants.TAG, "------onCreateOptionsMenu--------");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.i(Constants.TAG, "------onOptionsItemSelected--------");
        switch (item.getItemId()) {
            case R.id.total_account_search: {
                Intent intent = new Intent();
                intent.setClass(mContext, AccountSearchActivity.class);

                Log.i(Constants.TAG, "------enter into AccountSearchActivity--------");

                startActivity(intent);
            }
            break;
            case R.id.total_account_sync:{
                Log.i(Constants.TAG, "------start to sync--------");
                if (AccountCommonUtil.IsLogin(mContext) ) {
                    m_SyncTask.sync(true, true, new SyncHandler());
                }
                else
                {
                    m_ShowQuestionLoginPoup();
                }
            }
            break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setRefreshActionButtonState(boolean refreshing) {
        if (m_OptionsMenu == null) {
            return;
        }

        final MenuItem refreshItem = m_OptionsMenu.findItem(R.id.total_account_sync);
        if (refreshItem != null) {
            if (refreshing) {
                MenuItemCompat.setActionView(refreshItem, R.layout.actionbar_indeterminate_progress);
            } else {
                MenuItemCompat.setActionView(refreshItem, null);
            }
        }
    }

    private void m_InitalTotalAccountList()
    {

        m_DetailListAdapter = new AccountTotalDetailListAdapter(this,mDetailListDataSource);
        m_TotalAllAccountList.setAdapter(m_DetailListAdapter);

        m_TotalAllAccountList.setOnItemClickListener(AccountTotalActivity.this);
        m_TotalAllAccountList.setOnItemLongClickListener(AccountTotalActivity.this);

        m_DetailListAdapter.updateUI();

        m_TotalCostText = (TextView) findViewById(R.id.total_value);

        DecimalFormat df= new DecimalFormat("#,##0.00");

        String formatCost = df.format(m_CurrentTotalCost);

        m_TotalCostText.setText(formatCost);

    }

    private void m_UpdateTotalCost()
    {
        m_TotalCostText = (TextView) findViewById(R.id.total_value);

        DecimalFormat df= new DecimalFormat("#,##0.00");

        String formatCost = df.format(m_CurrentTotalCost);

        m_TotalCostText.setText(formatCost);

        return ;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        Log.i(Constants.TAG, "------start AccountDetailActivity--------");
        Intent intent = new Intent(this, AccountDetailActivity.class);

        Account current = mDetailListDataSource.get(position);
        intent.putExtra("id", current.getId());
        this.startActivity(intent);
    }

    private void m_InitSlidingMenu()
    {
        // configure the SlidingMenu
        m_Menu = new SlidingMenu(this);
        m_Menu.setMode(SlidingMenu.LEFT);

        // m_Menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        m_Menu.setShadowWidthRes(R.dimen.shadow_width);
//        menu.setShadowDrawable(R.drawable.shadow);


        m_Menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);

        m_Menu.setFadeDegree(0.35f);
        /**
         * SLIDING_WINDOW will include the Title/ActionBar in the content
         * section of the SlidingMenu, while SLIDING_CONTENT does not.
         */
        m_Menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);

        m_Menu.setMenu(R.layout.slide_menu_frame);

        m_ToolBar.setNavigationIcon(R.mipmap.g_menu_white);
        m_ToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_Menu.isMenuShowing()) {
                    m_Menu.showContent();
                } else {
                    m_Menu.showMenu();
                }

            }
        });
    }

    private void m_InitSlidingMenuContent()
    {
        Log.i(Constants.TAG, "------start m_InitSlidingMenuContent--------");
         int[] icons = { R.mipmap.ic_ascending,  R.mipmap.ic_descending, R.mipmap.ic_refresh,
                R.mipmap.ic_search,R.mipmap.ic_comment_icon,R.mipmap.ic_drawer_settings};
        int[] titles = {R.string.account_sort_asc,R.string.account_sort_desc,R.string.account_sort_sync,
                R.string.account_search,R.string.account_comment,R.string.account_setting};

        List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < icons.length; i++) {
            Map<String, Object> listem = new HashMap<String, Object>();
            listem.put("icon", icons[i]);
            listem.put("title", getString(titles[i]));
            listems.add(listem);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, listems,
                R.layout.slide_menu_list_item, new String[] { "icon", "title"},
                new int[] { R.id.slidemenu_list_icon, R.id.slidemenu_list_title});

        m_SlideMenuList = (ListView) findViewById(R.id.account_slidemenu_list);
        m_SlideMenuList.setAdapter(adapter);
        m_SlideMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(Constants.TAG, "------onItemClick  position--------"+position);
                switch (position) {
                    case Constants.ACCOUNT_SLIDEING_MENU_ASCEND:
                    case Constants.ACCOUNT_SLIDEING_MENU_DESCEND:
                    {
                        Intent intent = new Intent();
                        intent.setClass(mContext, AccountSortActivity.class);

                        Log.i(Constants.TAG, "------enter into AccountSortActivity--------");

                        Bundle mBundle = new Bundle();
                        mBundle.putInt("value", position);
                        intent.putExtras(mBundle);
                        startActivity(intent);
                    }
                        break;
                    case Constants.ACCOUNT_SLIDEING_MENU_SYNC:
                        break;
                    case Constants.ACCOUNT_SLIDEING_MENU_SEARCH:
                    {
                        Intent intent = new Intent();
                        intent.setClass(mContext, AccountSearchActivity.class);

                        Log.i(Constants.TAG, "------enter into AccountSearchActivity--------");

                        startActivity(intent);
                    }
                        break;
                    case Constants.ACCOUNT_SLIDEING_MENU_COMMENT:
                        break;
                    case Constants.ACCOUNT_SLIDEING_MENU_SETTING:
                        break;
                    default:
                        break;
                }
            }
        });

         m_LoginLayout = (RelativeLayout) findViewById(R.id.account_start_login);
        m_LoginLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                        //TODO if not login , show text , indictor already login
                        Log.i(Constants.TAG, "------onClick  m_ShowLoginPoup--------");
                
                if (AccountCommonUtil.IsLogin(mContext) ) {
                    Toast.makeText(mContext, R.string.account_already_login_success, Toast.LENGTH_SHORT).show();
                    m_RegisterUser("86","15062256959"); // To be delete
                }
                else {
                  //  m_ShowLoginPoup();
                    m_ShowSMSLoginPoup();
                }
            }
        });

        if (AccountCommonUtil.IsLogin(mContext)) {
            String userName = mSharedPreferences.getString("user_name", "");
            Log.i(Constants.TAG, "------login user name --------"+userName);
            TextView userNameText = (TextView) findViewById(R.id.total_value_label);
            userNameText.setText(userName);
        }
    }

    private void m_InitAddButton()
    {

        m_AddNewAccountButton = (FloatingActionButton) findViewById(R.id.fab);
        m_AddNewAccountButton.attachToListView(m_TotalAllAccountList);

        m_AddNewAccountButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i(Constants.TAG, "------start AccountStartActivity--------");
                Intent intent = new Intent(mContext, AccountStartActivity.class);
                startActivity(intent);
            }

        });
    }

    private void m_InitSwipeMenuList()
    {
        m_TotalAllAccountList = (SwipeMenuListView) findViewById(R.id.account_detail_list);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem editItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                editItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                editItem.setWidth(AccountCommonUtil.dp2px(mContext, 90));
                // set item title
                editItem.setTitle(getResources().getText(R.string.menu_edit).toString());
                // set item title fontsize
                editItem.setTitleSize(18);
                // set item title font color
                editItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(editItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item title
                deleteItem.setTitle(getResources().getText(R.string.menu_delete).toString());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item title fontsize
                deleteItem.setTitleSize(18);
                // set item title font color
                deleteItem.setTitleColor(Color.WHITE);

                // set item width
                deleteItem.setWidth(AccountCommonUtil.dp2px(mContext,90));
                // set a icon
                //deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // set creator
        m_TotalAllAccountList.setMenuCreator(creator);

        m_TotalAllAccountList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // edit
                        m_editSelectedItem(position);
                        break;
                    case 1:
                        // delete
                        m_ShowDeletePoup(position);
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

        m_TotalAllAccountList.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
    }

    private class PrepareTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub

            Log.i(Constants.TAG, "------start get all account from DB--------");

            mDetailListDataSource.clear();
            mDetailListDataSource = (ArrayList<Account>) Account.getNormalAccounts();

            Log.i(Constants.TAG, "------mDetailListDataSource--------");

            Log.i(Constants.TAG, "------mDetailListDataSource.size()--------"+mDetailListDataSource.size());

            m_CurrentTotalCost = 0.0;
            for(int index = 0 ; index < mDetailListDataSource.size(); index++)
            {
                Log.i(Constants.TAG, "------mDetailListDataSource--index-"+index + "--Cost--"+mDetailListDataSource.get(index).Cost);
                m_CurrentTotalCost += mDetailListDataSource.get(index).Cost;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.i(Constants.TAG, "------onPostExecute--------");
            super.onPostExecute(result);
            m_InitalTotalAccountList();
        }
    }

    @SuppressLint("HandlerLeak")
    class SyncHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
			/*
			case AccountSyncTask.SYNC_START:
				setRefreshActionButtonState(true);
				break;
			case AccountSyncTask.SYNC_END:
				setRefreshActionButtonState(false);
				break;
				*/
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub


        m_ShowDeletePoup(position);

        return false;
    }

    private void m_editSelectedItem(int position)
    {
        Account current = mDetailListDataSource.get(position);

        Bundle mBundle = new Bundle();
        if (current == null) {
            Log.i(Constants.TAG, "--current == null--"+position);
        }
        Log.i(Constants.TAG, "-m_CurrentAccount-id--" + current.getId());

        mBundle.putLong("id", current.getId());

        Intent intent = new Intent(this, AccountStartActivity.class);
        intent.putExtras(mBundle);
        this.startActivity(intent);

    }
    private void m_ShowDeletePoup(int position)
    {
        final Account current = mDetailListDataSource.get(position);

        Log.i(Constants.TAG, "------onItemLongClick--------" + current.getId());

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.give_up_edit)
                .setTitle(R.string.give_up_title)
                .setPositiveButton(R.string.give_up_sure,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                m_DetailListAdapter.removeItem(current);
                                m_DetailListAdapter.updateUI();
                                current.SyncStatus = Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_DELETE;
                                current.save();

                                //update total cost
                                m_CurrentTotalCost -= current.Cost;
                                m_UpdateTotalCost();
                                Toast.makeText(mContext, R.string.give_up_success, Toast.LENGTH_SHORT)
                                        .show();

                            }
                        }).setNegativeButton(R.string.give_up_cancel, null)
                .create().show();
    }

    private void m_ShowQuestionLoginPoup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setTitle(R.string.account_login).setMessage(
                        R.string.account_login_notice_body);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.account_login_go_now, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_ShowSMSLoginPoup();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.account_login_wait, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }


    private void m_ShowSMSLoginPoup() {
        Log.i(Constants.TAG, "------start m_ShowSMSLoginPoup--------");
//打开注册页面
        RegisterPage registerPage = new RegisterPage();
        registerPage.setRegisterCallback(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                Log.i(Constants.TAG, "------afterEvent----event----"+event+"--result--"+result+"---data--"+data);
// 解析注册结果
                if (result == SMSSDK.RESULT_COMPLETE) {
                    switch(event)
                    {
                        case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
                        {
                            @SuppressWarnings("unchecked")
                            HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
                            String country = (String) phoneMap.get("country");
                            String phone = (String) phoneMap.get("phone");
                            Log.i(Constants.TAG, "------afterEvent----country----"+country+"--phone--"+phone);

                            mSharedPreferences.edit().putString("user_name", phone)
                                    .apply();

                            Log.i(Constants.TAG, "------login user name --------" + phone);
                            TextView userNameText = (TextView) findViewById(R.id.total_value_label);
                            userNameText.setText(phone);

                            // 提交用户信息到服务端获取TOKEN
                            m_RegisterUser(country, phone);
                        }
                            break;
                        default:
                            break;
                    }

                }
            }
        });
        registerPage.show(mContext);
    }

    private boolean m_RegisterUser(String country, String phone) {
        Log.i(Constants.TAG, "--start -m_RegisterUser---" + phone);
        AccountApiConnector.instance(mContext).registerMobile(phone, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.i(Constants.TAG, "---getToken--onSuccess--response---" + response);

                String token = null;
                try {
                    token = response.getString("token");
                    AccountRestClient.SetClientToken(token);

			AccountCommonUtil.SetToken(mContext,token);
			mSharedPreferences.edit().putBoolean("is_login", true)
                                    .apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                super.onFailure(statusCode, headers, throwable, response);
                Log.i(Constants.TAG, "---getToken--onFailure--statusCode---" + statusCode);
                Log.i(Constants.TAG, "---getToken--onFailure--responseString---" + response);

                Toast.makeText(mContext, R.string.account_login_failed, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
            	   Log.i(Constants.TAG, "---getToken--onFinish-----" );
                super.onFinish();
            }

        });
        return true;
    }
}
