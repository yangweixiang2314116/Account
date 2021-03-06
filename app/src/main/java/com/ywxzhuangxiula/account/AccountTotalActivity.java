package com.ywxzhuangxiula.account;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.activeandroid.ActiveAndroid;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.ywxzhuangxiula.module.Account;
import com.ywxzhuangxiula.module.AccountRestClient;
import com.ywxzhuangxiula.module.AppManager;
import com.ywxzhuangxiula.module.CustomToast;
import com.ywxzhuangxiula.module.DialogHelp;
import com.ywxzhuangxiula.module.DoubleClickExitHelper;
import com.ywxzhuangxiula.module.ImageItem;
import com.ywxzhuangxiula.module.NetworkUtils;
import com.ywxzhuangxiula.module.NumberScrollTextView;
import com.ywxzhuangxiula.module.UpdateManager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.melnykov.fab.FloatingActionButton;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
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
public class AccountTotalActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener, BDLocationListener {

    private SwipeMenuListView m_TotalAllAccountList = null;
    private ListView m_SlideMenuList = null;
    private LayoutInflater mLayoutInflater = null;

    private DoubleClickExitHelper mDoubleClickExit = null;
    private FloatingActionButton m_AddNewAccountButton = null;
    private AccountTotalDetailListAdapter m_DetailListAdapter = null;
    private Context mContext = null;
    private NumberScrollTextView m_TotalCostText = null;
    private TextView m_CurrentTimeText = null;
    private TextView m_noAccountText = null;
    private Double m_CurrentTotalCost = 0.0;
    private Menu m_OptionsMenu = null;
    private AccountSyncService.AccountSyncServiceBinder mBinder = null;
    private ServiceConnection mConnection = null;
    private SlidingMenu m_Menu = null;
    private RelativeLayout m_LoginLayout = null;
    private Toolbar m_ToolBar = null;
    private SharedPreferences mSharedPreferences = null;
    private BroadcastReceiver mReceiver = null;
    private boolean m_bConnected = false;
    private boolean m_bFirstRefreshList = true;
    private boolean m_bIsServerNormal = false;
    protected ArrayList<Account> mDetailListDataSource = new ArrayList<Account>();
   //public static final long DAY = 1000L * 60 * 60 * 24;//TODO
    public static final long DAY = 1000L * 60 * 60 * 3;//TODO

    /**
     * 定位端
     */
    private LocationClient mLocClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_total);
        m_ToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(m_ToolBar);

        mContext = this;
        mDoubleClickExit = new DoubleClickExitHelper(this);
        mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        Log.i(Constants.TAG, "------AccountTotalActivity----onCreate -----");

        MobclickAgent.onEvent(mContext, "enter_total");

        SMSSDK.initSDK(this, Constants.ACCOUNT_LOGIN_SMS_APP_KEY, Constants.ACCOUNT_LOGIN_SMS_APP_SECRET);

        ActiveAndroid.setLoggingEnabled(false);

        AppManager.getAppManager().addActivity(this);

        m_InitSwipeMenuList();

        m_InitAddButton();

        //inital current time
        m_CurrentTimeText = (TextView) findViewById(R.id.total_date_title);
        String sCurrentDate = AccountCommonUtil.ConverDateToString(System.currentTimeMillis());
        m_CurrentTimeText.setText(sCurrentDate);

        m_InitSlidingMenu();

        m_InitSlidingMenuContent();

        m_InitReceiver();

        m_InitBindler();

        m_InitCurrentyCity();

        new PrepareTask().execute();

        checkUpdate();

    }

    private void m_InitActionBar() {
        if (m_OptionsMenu == null) {
            Log.i(Constants.TAG, "------AccountTotalActivity----m_InitActionBar -----");
            return;
        }

        if (AccountCommonUtil.IsSupportSync(this)) {
            MenuItem plusItem = m_OptionsMenu.findItem(R.id.total_account_add);
            plusItem.setVisible(false);
            Log.i(Constants.TAG, "The AccountLoadActivity---->invisible plusItem");
        } else {
            MenuItem refreshItem = m_OptionsMenu.findItem(R.id.total_account_sync);
            refreshItem.setVisible(false);
            Log.i(Constants.TAG, "The AccountLoadActivity---->invisible refreshItem");
        }
    }

    private void m_InitBindler() {
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i(Constants.TAG, "onServiceConnected----namer" + name);
                mBinder = (AccountSyncService.AccountSyncServiceBinder) service;

                AccountSyncService Syncservice = mBinder.getService();

                Syncservice.setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(int progress) {

                        Log.i(Constants.TAG, "onProgress----progress--" + progress);

                        switch (progress) {
                            case Constants.ACCOUNT_SYNC_ERROR:
                                //CustomToast.showToast(AccountTotalActivity.this, R.string.account_sync_service_error, Toast.LENGTH_SHORT);
                                setRefreshActionButtonState(false);
                                break;
                            case Constants.ACCOUNT_SYNC_START:
                                //CustomToast.showToast(AccountTotalActivity.this, getString(R.string.account_sync_service_start), Toast.LENGTH_SHORT);
                                setRefreshActionButtonState(true);
                                break;
                            case Constants.ACCOUNT_SYNC_END:
                                //CustomToast.showToast(AccountTotalActivity.this, getString(R.string.account_sync_service_success), Toast.LENGTH_SHORT);
                                setRefreshActionButtonState(false);
                                break;
                        }
                    }
                });
                m_bConnected = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i(Constants.TAG, "onServiceDisconnected----namer" + name);
                m_bConnected = false;
            }
        };

        Intent intent = new Intent(this, AccountSyncService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);

    }

    private void m_InitReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.INTENT_NOTIFY_ACCOUNT_CHANGE)) {
                    Log.i(Constants.TAG, "The AccountTotalActivity---->m_InitReceiver---INTENT_NOTIFY_ACCOUNT_CHANGE");
                    //start load all account from DB
                    new PrepareTask().execute();

                } else if (intent.getAction().equals(Constants.INTENT_NOTIFY_INVALID_TOKEN)) {
                    Log.i(Constants.TAG, "The AccountTotalActivity---->m_InitReceiver---INTENT_NOTIFY_INVALID_TOKEN");
                    if (AccountCommonUtil.IsLogin(mContext)) {

                        AccountCommonUtil.SetLogin(mContext, false);
                        TextView userNameText = (TextView) findViewById(R.id.total_user_namel);
                        userNameText.setText(getString(R.string.account_not_log_in));
                        Toast.makeText(mContext, R.string.account_login_expire, Toast.LENGTH_SHORT).show();
                    }
                } else if (intent.getAction().equals(Constants.INTENT_NOTIFY_START_SYNC)) {
                    m_bIsServerNormal = true;
                    //m_ProcessSyncAction(true);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_NOTIFY_ACCOUNT_CHANGE);
        filter.addAction(Constants.INTENT_NOTIFY_INVALID_TOKEN);
        filter.addAction(Constants.INTENT_NOTIFY_START_SYNC);

        registerReceiver(mReceiver, filter);

        return;
    }

    private void m_InitCurrentyCity() {
        String city = AccountCommonUtil.GetCurrentCity(this);
        Log.i(Constants.TAG, "m_InitCurrentyCity---->" + city);
        if (city.equals("")) {
            RequestCurrentCity();
        } else {
            Log.i(Constants.TAG, "m_InitCurrentyCity----already load city-");
        }
    }

    protected void onStart() {
        super.onStart();
        Log.i(Constants.TAG, "The AccountTotalActivity---->onStart");
    }

    protected void onRestart() {
        super.onRestart();
        //start load all account from DB
        //new PrepareTask().execute();
        Log.i(Constants.TAG, "The AccountTotalActivity---->onReatart");
    }

    protected void onResume() {
        super.onResume();
        Log.i(Constants.TAG, "The AccountTotalActivity---->onResume");
        m_ProcessSyncAction(true);
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mReceiver = null;

        if (m_bConnected) {
            unbindService(mConnection);
        }

        if (mLocClient != null && mLocClient.isStarted()) {
            mLocClient.stop();
        }

        AccountRestClient.instance(mContext).stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        m_OptionsMenu = menu;
        getMenuInflater().inflate(R.menu.account_total, menu);
        Log.i(Constants.TAG, "------onCreateOptionsMenu--------");

        m_InitActionBar();

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
            case R.id.total_account_sync: {
                Log.i(Constants.TAG, "-------start to sync-------");
                m_ProcessSyncAction(false);
            }
            break;

            case R.id.total_account_add: {
                Log.i(Constants.TAG, "-------start to add-------");
                MobclickAgent.onEvent(mContext, "create_account");
                Intent intent = new Intent(mContext, AccountStartActivity.class);
                startActivity(intent);
            }
            break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setRefreshActionButtonState(boolean refreshing) {
        Log.i(Constants.TAG, "------setRefreshActionButtonState---refreshing-----" + refreshing);
        if (m_OptionsMenu == null) {
            Log.i(Constants.TAG, "------setRefreshActionButtonState---m_OptionsMenu == null-----");
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

    private void m_InitalTotalAccountList() {

        TextView m_noAccountText = (TextView) findViewById(R.id.account_empty_no_account);
        if (mDetailListDataSource.size() > 0) {
            m_noAccountText.setVisibility(View.INVISIBLE);
        } else {
            m_noAccountText.setVisibility(View.VISIBLE);
        }

        m_DetailListAdapter = new AccountTotalDetailListAdapter(this, mDetailListDataSource);
        m_TotalAllAccountList.setAdapter(m_DetailListAdapter);
        m_TotalAllAccountList.setOnItemClickListener(AccountTotalActivity.this);
        m_TotalAllAccountList.setOnItemLongClickListener(AccountTotalActivity.this);
        m_TotalAllAccountList.setOnMenuStateChangeListener(new SwipeMenuListView.OnMenuStateChangeListener() {
            @Override
            public void onMenuOpen(int position) {
                m_Menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
            }

            @Override
            public void onMenuClose(int position) {
                m_Menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
            }
        });
//        m_DetailListAdapter.updateUI();

        m_TotalCostText = (NumberScrollTextView) findViewById(R.id.total_value);

        //DecimalFormat df = new DecimalFormat("#,##0.00");

        //String formatCost = df.format(m_CurrentTotalCost);

        //m_TotalCostText.setText(formatCost);
        m_TotalCostText.setFromAndEndNumber((float) 0.0, m_CurrentTotalCost.floatValue());
        m_TotalCostText.setDuration(1000);
        m_TotalCostText.start();

        if (m_bFirstRefreshList && mDetailListDataSource.size() > Constants.ACCOUNT_FORCE_LOGIN_MAX &&
                AccountCommonUtil.IsLogin(mContext) == false) {
            m_ShowForceLoginPoup();
            m_bFirstRefreshList = false;
        }

        if (mDetailListDataSource.size() > Constants.ACCOUNT_FORCE_LOGIN_MAX*2  &&
                AccountCommonUtil.IsShowLikePopup(mContext) == false) {
            m_ShoRequestLikePopup();
            m_bFirstRefreshList = false;
        }

        if (m_bFirstRefreshList) {
            m_bFirstRefreshList = false;
        } else {
            //m_ProcessSyncAction(true);
        }

    }

    private void m_UpdateTotalCost(Double oldCost, Double newCost) {
        m_TotalCostText = (NumberScrollTextView) findViewById(R.id.total_value);

        m_TotalCostText.setFromAndEndNumber(oldCost.floatValue(), newCost.floatValue());
        m_TotalCostText.setDuration(1000);
        m_TotalCostText.start();

        //DecimalFormat df = new DecimalFormat("#,##0.00");

        //String formatCost = df.format(m_CurrentTotalCost);

        //m_TotalCostText.setText(formatCost);


        return;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        Intent intent = new Intent(this, AccountDetailActivity.class);
        MobclickAgent.onEvent(mContext, "browser_account");
        Account current = mDetailListDataSource.get(position);
        Log.i(Constants.TAG, "------start onItemClick----current.getId()----" + current.getId());
        intent.putExtra("id", current.getId());
        this.startActivity(intent);
    }

    private void m_InitSlidingMenu() {
        // configure the SlidingMenu
        m_Menu = new SlidingMenu(this);
        m_Menu.setMode(SlidingMenu.LEFT);

        m_Menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
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
                    MobclickAgent.onEvent(mContext, "slidemenu_close");
                } else {
                    m_Menu.showMenu();
                    MobclickAgent.onEvent(mContext, "slidemenu_open");
                }

            }
        });

        boolean mFirst = AccountCommonUtil.IsFirstEnter(this);
        if (mFirst) {
            m_Menu.showMenu();
            AccountCommonUtil.SetNotFirstEnter(mContext);
        }
    }

    private void m_InitSlidingMenuContent() {
        Log.i(Constants.TAG, "------start m_InitSlidingMenuContent--------");
        List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();
        if (AccountCommonUtil.IsSupportSync(this)) {
            int[] icons = {R.mipmap.ic_ascending, R.mipmap.ic_descending, R.mipmap.ic_browser,
                    R.mipmap.ic_search,
                    R.mipmap.ic_refresh, R.mipmap.ic_comment_icon, R.mipmap.ic_drawer_settings};
            int[] titles = {R.string.account_sort_asc, R.string.account_sort_desc, R.string.account_browser_picture,
                    R.string.account_search, R.string.account_sort_sync, R.string.account_comment, R.string.account_setting};


            for (int i = 0; i < icons.length; i++) {
                Map<String, Object> listem = new HashMap<String, Object>();
                listem.put("icon", icons[i]);
                listem.put("title", getString(titles[i]));
                listems.add(listem);
            }
        } else {
            int[] icons = {R.mipmap.ic_ascending, R.mipmap.ic_descending, R.mipmap.ic_browser, R.mipmap.ic_search};
            int[] titles = {R.string.account_sort_asc, R.string.account_sort_desc, R.string.account_browser_picture,
                    R.string.account_search};

            for (int i = 0; i < icons.length; i++) {
                Map<String, Object> listem = new HashMap<String, Object>();
                listem.put("icon", icons[i]);
                listem.put("title", getString(titles[i]));
                listems.add(listem);
            }
        }

        SimpleAdapter adapter = new SimpleAdapter(this, listems,
                R.layout.slide_menu_list_item, new String[]{"icon", "title"},
                new int[]{R.id.slidemenu_list_icon, R.id.slidemenu_list_title});

        m_SlideMenuList = (ListView) findViewById(R.id.account_slidemenu_list);
        m_SlideMenuList.setAdapter(adapter);
        m_SlideMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(Constants.TAG, "------onItemClick  position--------" + position);
                switch (position) {
                    case Constants.ACCOUNT_SLIDEING_MENU_ASCEND:
                    case Constants.ACCOUNT_SLIDEING_MENU_DESCEND: {
                        Intent intent = new Intent();
                        intent.setClass(mContext, AccountSortActivity.class);

                        Log.i(Constants.TAG, "------enter into AccountSortActivity--------");

                        MobclickAgent.onEvent(mContext, "slidemenu_enter_sort");
                        Bundle mBundle = new Bundle();
                        mBundle.putInt("value", position);
                        intent.putExtras(mBundle);
                        startActivity(intent);
                    }
                    break;
                    case Constants.ACCOUNT_SLIDEING_MENU_IMAGE: {
                        Intent intent = new Intent();
                        intent.setClass(mContext, AccountAllImageActivity.class);

                        Log.i(Constants.TAG, "------enter into AccountAllImageActivity--------");
                        MobclickAgent.onEvent(mContext, "slidemenu_enter_all_image");
                        startActivity(intent);
                    }
                    break;
                    case Constants.ACCOUNT_SLIDEING_MENU_SYNC: {
                        Log.i(Constants.TAG, "-------start to sync-------");
                        m_ProcessSyncAction(false);
                    }
                    break;
                    case Constants.ACCOUNT_SLIDEING_MENU_SEARCH: {
                        Intent intent = new Intent();
                        intent.setClass(mContext, AccountSearchActivity.class);

                        Log.i(Constants.TAG, "------enter into AccountSearchActivity--------");
                        MobclickAgent.onEvent(mContext, "slidemenu_enter_search");
                        startActivity(intent);
                    }
                    break;
                    case Constants.ACCOUNT_SLIDEING_MENU_COMMENT: {
                        Log.i(Constants.TAG, "-------start to feedback-------");
                        if (AccountCommonUtil.IsLogin(mContext)) {
                            MobclickAgent.onEvent(mContext, "slidemenu_enter_feedback");
                            Intent intent = new Intent();
                            intent.setClass(mContext, AccountFeedbackActivity.class);

                            Log.i(Constants.TAG, "------enter into AccountFeedbackActivity--------");

                            startActivity(intent);
                        } else {
                            m_ShowLoginForFeedBackPoup();
                        }
                    }
                    break;
                    case Constants.ACCOUNT_SLIDEING_MENU_SETTING: {
                        Log.i(Constants.TAG, "-------start to AccountSettingActivity-------");
                        // if (AccountCommonUtil.IsLogin(mContext) ) {
                        MobclickAgent.onEvent(mContext, "slidemenu_enter_setting");
                        Intent intent = new Intent();
                        intent.setClass(mContext, AccountSettingActivity.class);

                        Log.i(Constants.TAG, "------enter into AccountSettingActivity--------");

                        startActivity(intent);
                    }
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

                if (AccountCommonUtil.IsLogin(mContext)) {

                    Intent intent = new Intent();
                    intent.setClass(mContext, AccountUserInfoActivity.class);

                    Log.i(Constants.TAG, "------enter into AccountUserInfoActivity--------");

                    startActivity(intent);

                    //Toast.makeText(mContext, R.string.account_already_login_success, Toast.LENGTH_SHORT).show();
                } else {
                    m_ShowSMSLoginPoup();
                }
            }
        });

        if (AccountCommonUtil.IsLogin(mContext)) {
            String userName = mSharedPreferences.getString("user_name", "");
            Log.i(Constants.TAG, "------login user name --------" + userName);
            TextView userNameText = (TextView) findViewById(R.id.total_user_namel);

            String result = userName.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            Log.i(Constants.TAG, "------after replace user name --------" + userName + "----result-" + result);
            userNameText.setText(result);
        }
    }

    private void m_InitAddButton() {

        m_AddNewAccountButton = (FloatingActionButton) findViewById(R.id.fab);
        m_AddNewAccountButton.attachToListView(m_TotalAllAccountList);

        m_AddNewAccountButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i(Constants.TAG, "------start AccountStartActivity--------");
                MobclickAgent.onEvent(mContext, "create_account");
                Intent intent = new Intent(mContext, AccountStartActivity.class);
                startActivity(intent);
            }

        });
    }

    private void m_InitSwipeMenuList() {
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
                deleteItem.setWidth(AccountCommonUtil.dp2px(mContext, 90));
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

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        //Receive Location
        //经纬度
        double lati = bdLocation.getLatitude();
        double longa = bdLocation.getLongitude();
        //打印出当前位置
        Log.i(Constants.TAG, "location.getAddrStr()=" + bdLocation.getAddrStr());
        //打印出当前城市
        Log.i(Constants.TAG, "location.getCity()=" + bdLocation.getCity());
        //返回码
        int i = bdLocation.getLocType();

        AccountCommonUtil.SetCurrentCity(this, bdLocation.getCity());
    }

    private class PrepareTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub

            Log.i(Constants.TAG, "------start get all account from DB--------");

            mDetailListDataSource.clear();
            mDetailListDataSource = (ArrayList<Account>) Account.getNormalAccounts();

            Log.i(Constants.TAG, "------mDetailListDataSource--------");

            Log.i(Constants.TAG, "------mDetailListDataSource.size()--------" + mDetailListDataSource.size());

            m_CurrentTotalCost = 0.0;
            for (int index = 0; index < mDetailListDataSource.size(); index++) {
                Log.i(Constants.TAG, "------mDetailListDataSource--index-" + index + "--Cost--" + mDetailListDataSource.get(index).Cost);
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

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub


        m_ShowDeletePoup(position);

        return false;
    }

    private void m_editSelectedItem(int position) {
        Account current = mDetailListDataSource.get(position);

        Bundle mBundle = new Bundle();
        if (current == null) {
            Log.i(Constants.TAG, "--current == null--" + position);
        }
        Log.i(Constants.TAG, "-m_CurrentAccount-id--" + current.getId());

        MobclickAgent.onEvent(mContext, "edit_account");

        mBundle.putLong("id", current.getId());

        Intent intent = new Intent(this, AccountStartActivity.class);
        intent.putExtras(mBundle);
        this.startActivity(intent);

    }

    private void m_ShowDeletePoup(int position) {
        final Account current = mDetailListDataSource.get(position);

        Log.i(Constants.TAG, "------onItemLongClick--------" + current.getId());

        android.support.v7.app.AlertDialog.Builder dialog = DialogHelp.getConfirmDialog(mContext, getString(R.string.confirm_to_delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MobclickAgent.onEvent(mContext, "delete_account");
                m_DetailListAdapter.removeItem(current);
                m_DetailListAdapter.updateUI();

                current.setNeedSyncDelete();
                current.save();

                //delete images of this account
                ImageItem.deleteAll(current);

                //update total cost
                double oldCost = m_CurrentTotalCost;
                m_CurrentTotalCost -= current.Cost;
                m_UpdateTotalCost(oldCost, m_CurrentTotalCost);
                Toast.makeText(mContext, R.string.give_up_success, Toast.LENGTH_SHORT)
                        .show();

                //sync with server
                m_ProcessSyncAction(true);
            }
        });
        dialog.show();

    }

    private void m_ShowLoginForFeedBackPoup() {
        android.support.v7.app.AlertDialog.Builder dialog = DialogHelp.getSelfDefineConfirmDialog(mContext,
                getString(R.string.account_login_feedback_body),
                getString(R.string.btn_name_rightnow),
                getString(R.string.btn_name_quit),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                m_ShowSMSLoginPoup();
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    private void m_ShowQuestionLoginPoup() {
        android.support.v7.app.AlertDialog.Builder dialog = DialogHelp.getConfirmDialog(mContext, getString(R.string.account_login_notice_body), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                m_ShowSMSLoginPoup();
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }


    private void m_ShoRequestLikePopup() {
        android.support.v7.app.AlertDialog.Builder dialog = DialogHelp.getSelfDefineConfirmDialog(mContext,
                getString(R.string.account_like_unlike_body),
                getString(R.string.btn_name_like),
                getString(R.string.btn_name_dislike),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        m_ShowRatePoup();
                        dialogInterface.dismiss();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        m_ShowFeedBackPoup();
                        dialogInterface.dismiss();
                    }
                }
        );
        dialog.setCancelable(false);
        dialog.show();

        //like popup just show one time
        AccountCommonUtil.SetShowLikePopup(mContext, true);

    }

    private void m_ShowFeedBackPoup() {
        android.support.v7.app.AlertDialog.Builder dialog = DialogHelp.getSelfDefineConfirmDialog(mContext,
                getString(R.string.account_feedback_notice_body),
                getString(R.string.btn_name_rightnow),
                getString(R.string.btn_name_quit),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (AccountCommonUtil.IsLogin(mContext)) {
                            MobclickAgent.onEvent(mContext, "slidemenu_enter_feedback");
                            Intent intent = new Intent();
                            intent.setClass(mContext, AccountFeedbackActivity.class);

                            Log.i(Constants.TAG, "------enter into AccountFeedbackActivity--------");

                            startActivity(intent);
                        } else {
                            m_ShowLoginForFeedBackPoup();
                        }
                        dialogInterface.dismiss();
                    }
                });
        dialog.show();
    }


    private void m_ShowRatePoup() {
        android.support.v7.app.AlertDialog.Builder dialog = DialogHelp.getSelfDefineConfirmDialog(mContext,
                getString(R.string.account_rate_notice_body),
                getString(R.string.btn_name_rightnow),
                getString(R.string.btn_name_quit),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Uri uri = Uri.parse("market://details?id="
                                + mContext.getPackageName());
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        try {
                            startActivity(goToMarket);
                            //TODO
                            //MobclickAgent.onEvent(mContext, "rate");
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(mContext, R.string.can_not_open_market,
                                    Toast.LENGTH_SHORT).show();
                        }
                        dialogInterface.dismiss();
                    }
                });
        dialog.show();
    }

    private void m_ShowForceLoginPoup() {
        android.support.v7.app.AlertDialog.Builder dialog = DialogHelp.getConfirmDialog(mContext, getString(R.string.account_force_login_body), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                m_ShowSMSLoginPoup();
                dialogInterface.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void m_ShowSMSLoginPoup() {
        Log.i(Constants.TAG, "------start m_ShowSMSLoginPoup--------");
//打开注册页面
        RegisterPage registerPage = new RegisterPage();
        registerPage.setRegisterCallback(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                Log.i(Constants.TAG, "------afterEvent----event----" + event + "--result--" + result + "---data--" + data);
// 解析注册结果
                if (result == SMSSDK.RESULT_COMPLETE) {
                    switch (event) {
                        case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE: {
                            @SuppressWarnings("unchecked")
                            HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                            String country = (String) phoneMap.get("country");
                            String phone = (String) phoneMap.get("phone");
                            Log.i(Constants.TAG, "------afterEvent----country----" + country + "--phone--" + phone);

                            mSharedPreferences.edit().putString("user_name", phone)
                                    .apply();

                            // 提交用户信息到服务端获取TOKEN
                            if (AccountCommonUtil.IsSupportSync(mContext)) {
                                m_RegisterUser(country, phone);
                            }
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

                    String user = AccountCommonUtil.GetUserName(mContext);
                    Log.i(Constants.TAG, "------login user name --------" + user);
                    TextView userNameText = (TextView) findViewById(R.id.total_user_namel);

                    String repalceResult = user.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
                    userNameText.setText(repalceResult);

                    AccountCommonUtil.SetToken(mContext, token);
                    AccountCommonUtil.SetLogin(mContext, true);

                    if (AccountCommonUtil.GetUserProfileId(mContext) == 0) {
                        m_ProcessUserInfoContent();
                    }

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
                Log.i(Constants.TAG, "---getToken--onFinish-----");
                super.onFinish();
            }

        });

        return true;
    }

    public interface OnProgressListener {
        void onProgress(int progress);
    }

    public String RequestCurrentCity() {
        //初始化定位
        mLocClient = new LocationClient(this);
        //注册定位监听
        mLocClient.registerLocationListener(this);

        LocationClientOption option = new LocationClientOption();
        //就是这个方法设置为true，才能获取当前的位置信息
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        //int span = 1000;
        //option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        mLocClient.setLocOption(option);
        mLocClient.start();
        return "";
    }

    public boolean m_ProcessSyncAction(boolean auto) {
        if (NetworkUtils.isNetworkAvailable(mContext)) {
            if (AccountCommonUtil.IsLogin(mContext)) {
                if (AccountCommonUtil.IsOnlyWifi(mContext)) {
                    if (NetworkUtils.isWifiConnected(mContext)) {
                        if (m_bIsServerNormal) {
                            mBinder.startSync();
                        } else {
                            //retry to check server normal
                            checkUpdate();
                            /*
                            if(auto)
                            {
                                return false;
                            }
                            Toast.makeText(mContext, R.string.server_abnormal, Toast.LENGTH_SHORT).show();
                            */
                        }
                    } else {

                        if (auto) {
                            return false;
                        }
                        Toast.makeText(mContext, R.string.wifi_network_disconnect, Toast.LENGTH_SHORT).show();

                    }
                } else {
                    if (m_bIsServerNormal) {
                        mBinder.startSync();
                    } else {
                        //retry to check server normal
                        checkUpdate();
                        /*
                        if(auto)
                        {
                            return false;
                        }
                        Toast.makeText(mContext, R.string.server_abnormal, Toast.LENGTH_SHORT).show();
                        */
                    }
                }
            } else {
                if (auto) {
                    return false;
                }
                MobclickAgent.onEvent(mContext, "slidemenu_enter_login");
                m_ShowQuestionLoginPoup();
            }
        } else {
            if (auto) {
                return false;
            }
            Toast.makeText(mContext, R.string.network_disconnect, Toast.LENGTH_SHORT).show();
        }
        return true;
    }


    public boolean m_ProcessUserInfoContent() {
        Log.i(Constants.TAG, "---m_ProcessUserInfoContent-----");
        String city = AccountCommonUtil.GetCurrentCity(mContext);
        String style = AccountCommonUtil.GetGudieStyle(mContext);
        String budget = AccountCommonUtil.GetGudieBudget(mContext);
        String area = AccountCommonUtil.GetGudieArea(mContext);
        AccountApiConnector.instance(mContext).postUserInfo(city,
                style, budget, area, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // If the response is JSONObject instead of expected JSONArray
                        Log.i(Constants.TAG, "---postUserInfo--onSuccess--response---" + response);
                        try {
                            long profileId = response.isNull("id") ? 0 : response.getLong("id");
                            Log.i(Constants.TAG, "---postUserInfo--onSuccess--profileId---" + profileId);
                            AccountCommonUtil.SetUserProfileId(mContext, profileId);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            Log.i(Constants.TAG, "--postUserInfo--onSuccess-JSONException-" + e);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Log.i(Constants.TAG, "---postUserInfo--onFailure--statusCode---" + statusCode);
                        Log.i(Constants.TAG, "---postUserInfo--onFailure--responseString---" + responseString);
                        Toast.makeText(mContext, R.string.account_userprofile_failed, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinish() {
                        Log.i(Constants.TAG, "---postUserInfo--onFinish-----");
                        super.onFinish();
                    }

                });

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return mDoubleClickExit.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    private void checkUpdate() {

        if (NetworkUtils.isNetworkAvailable(mContext)) {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    new UpdateManager(AccountTotalActivity.this, false).checkUpdate();
                }
            }, 2000);
        }
    }

}
