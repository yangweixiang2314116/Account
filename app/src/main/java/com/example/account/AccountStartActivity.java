package com.example.account;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.activeandroid.ActiveAndroid;
import com.baidu.mapapi.search.core.PoiInfo;
import com.example.module.Account;
import com.example.module.ImageItem;
import com.example.module.MoreInfoItem;
import com.example.module.PoiItem;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class AccountStartActivity extends ActionBarActivity  {

    private EditText m_InputEditText = null;
    private LinearLayout m_MoreInfoList = null;
    private Account m_CurrentAccount = null;
    private TextView m_CurrentTimeText = null;
    private EditText m_CommentsText = null;
    private Boolean m_bCreateNewAccount = false;
    private Context mContext = null;
    private LinearLayout m_ImageContents = null;
    private AddView m_AddButton = null;
    private LayoutInflater mLayoutInflater = null;
    protected ArrayList<MoreInfoItem> mMoreInfoListDataSource = new ArrayList<MoreInfoItem>();
    private Button mFinishButton = null;

    private long m_LatestCreateTime;
    private String m_LatestComments = "";
    private String m_LatestCategory = "";
    private String m_LatestBrand = "";
    private String m_LatestPosition = "";
    private PoiInfo m_LatestPoi = null;
    private PoiItem m_CurrentPoi = null;
    private Double m_LatestCost;
    private ArrayList<String> m_LatestImageList = new ArrayList<String>();
    private boolean m_bImageListChange = false;
    private boolean m_bPoiInfoChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(Constants.TAG, "------ AccountStartActivity---onCreate-----");

        overridePendingTransition(R.anim.push_up, R.anim.push_down);

        setTheme(R.style.MIS_NO_ACTIONBAR);

        setContentView(R.layout.activity_account_start);

        Toolbar toolbar = (Toolbar) findViewById(R.id.start_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            Log.i(Constants.TAG, "------AccountDetailActivity----setSupportActionBar---");
        }
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            Log.i(Constants.TAG, "------AccountDetailActivity----ActionBar Setting---");
        }

        mContext = this;
        mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mFinishButton = (Button) findViewById(R.id.account_finish);
        mFinishButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                m_SaveAccount();
                //notify data change
                AccountCommonUtil.sendBroadcastForAccountDataChange(mContext);
                finish();
                overridePendingTransition(R.anim.out_push_up, R.anim.out_push_down);
            }
        });

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            Long id = bundle.getLong("id");
            Log.i(Constants.TAG, "-----AccountStartActivity- accountId-------" + id);
            m_CurrentAccount = Account.load(Account.class, id);
            m_bCreateNewAccount = false;

            m_LatestCreateTime = m_CurrentAccount.CreateTime;
            m_LatestComments = m_CurrentAccount.Comments;
            m_LatestCost = m_CurrentAccount.Cost;
            m_LatestCategory = m_CurrentAccount.Category;
            m_LatestBrand = m_CurrentAccount.Brand;
            m_CurrentPoi = PoiItem.GetPoiItem(m_CurrentAccount);
            m_LatestPosition = m_CurrentAccount.Position;
        } else {
            Log.i(Constants.TAG, "-----AccountStartActivity- new account-------");
            m_CurrentAccount = new Account();
            m_bCreateNewAccount = true;
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            m_LatestCreateTime = System.currentTimeMillis();

        }

        m_InitDateText();
        m_InitAddButton();
        m_InitCommentsText();
        m_InitEditText();
        m_InitMoreInfoList();
        m_InitImageList();

        MobclickAgent.onEvent(mContext, "enter_start");
    }

    private Handler mHandler = new Handler();
    private boolean m_InitCommentsText() {
        m_CommentsText = (EditText) findViewById(R.id.account_add_comments);
        m_CommentsText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final ScrollView scrollLayout = (ScrollView) findViewById(R.id.account_scrollview);
                Log.i(Constants.TAG, "-----AccountStartActivity- onTouch----scroll to bottom---");

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollLayout.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }, 100);
                return false;
            }
        });

        /*
          m_CommentsText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    final ScrollView scrollLayout = (ScrollView) findViewById(R.id.account_scrollview);
                    Log.i(Constants.TAG, "-----AccountStartActivity- onClick----scroll to bottom---");

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollLayout.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    }, 100);
                } else {

                }

            }
        });
        */

        if (!m_bCreateNewAccount) {
            m_CommentsText.setText(m_LatestComments);
        }
        return true;
    }

    private boolean m_InitDateText() {
        m_CurrentTimeText = (TextView) findViewById(R.id.start_date_title);
        String sCurrentDate = AccountCommonUtil.ConverDateToString(m_LatestCreateTime);
        m_CurrentTimeText.setText(sCurrentDate);
        return true;
    }

    private boolean m_InitAddButton() {

        m_AddButton = (AddView) findViewById(R.id.account_add_picture);
        m_AddButton.setPadding(20);
        m_AddButton.setPaintColor(R.color.colorBorderAdd);
        m_AddButton.setPaintWidth(4);
        m_AddButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(Constants.TAG, "--onItemClick--ACCOUNT_MORE_INFO_IMAGE--");

                Intent intent = new Intent(mContext, MultiImageSelectorActivity.class);
                // whether show camera
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                // max select image amount
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 4);
                // select mode (MultiImageSelectorActivity.MODE_SINGLE OR MultiImageSelectorActivity.MODE_MULTI)
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
                // default select images (support array list)

                if (m_LatestImageList.size() > 0) {
                    intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, m_LatestImageList);
                }
                startActivityForResult(intent, Constants.ACCOUNT_MORE_INFO_IMAGE);
            }
        });

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.i(Constants.TAG, "-----AccountStartActivity- onOptionsItemSelected-------");
        switch (item.getItemId()) {
            case android.R.id.home:
                m_ShowQuitMessageBox();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: {
                m_ShowQuitMessageBox();
            }
            return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean m_ShowQuitMessageBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        View messageContent = mLayoutInflater.inflate(
                R.layout.dialog_content_info, null);
        builder.setView(messageContent);

        TextView content = (TextView) messageContent.findViewById(R.id.dialog_message_content);
        content.setText(getString(R.string.give_up_edit));

        builder.setPositiveButton(R.string.give_up_sure,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        //getWindow().setSoftInputMode(
                        //        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN  | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                        finish();
                        overridePendingTransition(R.anim.out_push_up, R.anim.out_push_down);
                    }
                }).setNegativeButton(R.string.give_up_cancel, null)
                .create().show();
        return true;
    }

    private boolean m_InitEditText() {
        m_InputEditText = (EditText) findViewById(R.id.start_input_value);

        if (false == m_bCreateNewAccount) {
            DecimalFormat df = new DecimalFormat("###,###");

            String afterFormat = df.format(m_LatestCost);

            Log.i(Constants.TAG, "--before format" + m_LatestCost + "---afterFormat--------" + afterFormat);

            m_InputEditText.setText(afterFormat);

            m_InputEditText.setSelection(m_InputEditText.length());
        }else{
            m_InputEditText.requestFocus();
        }

        m_InputEditText.addTextChangedListener(new TextWatcher() {
            private boolean m_bChanged = false;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (m_bChanged) {
                    return;
                }

                if (s.toString().length() == 0) {
                    Log.i(Constants.TAG, "--afterTextChanged---length == 0");
                    return;
                }

                String cuttedStr = s.toString();

                Log.i(Constants.TAG, "--before replace---" + cuttedStr);

                m_bChanged = true;

                cuttedStr = cuttedStr.replace(",", "");

                Log.i(Constants.TAG, "--after replace---" + cuttedStr);

                Double currentValue = Double.valueOf(cuttedStr.toString());

                DecimalFormat df = new DecimalFormat("###,###");

                String afterFormat = df.format(currentValue);

                Log.i(Constants.TAG, "--before format" + cuttedStr + "---afterFormat--------" + afterFormat);

                m_InputEditText.setText(afterFormat);

                m_InputEditText.setSelection(m_InputEditText.length());

                Log.i(Constants.TAG,
                        "--m_LatestCost.Cost--" + m_LatestCost + "---currentValue--------" + currentValue);

                m_LatestCost = currentValue;
                m_bChanged = false;
            }

        });
        return true;
    }

    private boolean m_LoadMoreInfoDataSrc() {

        final Integer ACCOUNT_MORE_INFO_ITEM_COUNT = 3;

        mMoreInfoListDataSource.clear();
        for (int index = 0; index < ACCOUNT_MORE_INFO_ITEM_COUNT; index++) {
            MoreInfoItem item = null;
            switch (index) {
                case Constants.ACCOUNT_MORE_INFO_CATEGORY: {

                    item = new MoreInfoItem(getString(R.string.add_category_app_name), m_LatestCategory,
                            Constants.ACCOUNT_MORE_INFO_TYPE_TEXT);
                    mMoreInfoListDataSource.add(item);
                }
                break;
                case Constants.ACCOUNT_MORE_INFO_BRAND: {

                    item = new MoreInfoItem(getString(R.string.add_brand_app_name), m_LatestBrand,
                            Constants.ACCOUNT_MORE_INFO_TYPE_TEXT);
                    mMoreInfoListDataSource.add(item);
                }
                break;
                case Constants.ACCOUNT_MORE_INFO_POSITION: {

                    item = new MoreInfoItem(getString(R.string.add_position_app_name), m_LatestPosition,
                            Constants.ACCOUNT_MORE_INFO_TYPE_TEXT);
                    mMoreInfoListDataSource.add(item);
                }
                break;
                default:
                    break;
            }
        }

        return true;
    }

    private boolean m_InitMoreInfoList() {
        m_LoadMoreInfoDataSrc();
        m_MoreInfoList = (LinearLayout) findViewById(R.id.account_more_info_list);

        m_MoreInfoList.removeAllViews();
        for (int index = 0; index < mMoreInfoListDataSource.size(); index++) {
            MoreInfoItem item = mMoreInfoListDataSource.get(index);

            Log.i(Constants.TAG, "--ACCOUNT_MORE_INFO_IMAGE --item.itemLable--" + item.itemLable);

            final int nCurrentIndex = index;
            final View InfoItem = mLayoutInflater.inflate(
                    R.layout.activity_account_more_info_item_text, null);
            TextView lable = (TextView) InfoItem
                    .findViewById(R.id.more_info_item_label);
            lable.setText(item.itemLable);

            TextView itemValue = (TextView) InfoItem
                    .findViewById(R.id.more_info_item_value);
            itemValue.setText(item.itemValue);

            InfoItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processOnItemClick(nCurrentIndex);
                }
            });

            if(item.itemValue.equals("") == false) {
                ImageView check = (ImageView) InfoItem
                        .findViewById(R.id.more_info_check);
                check.setImageResource(R.mipmap.checkbox_checked);
            }
            m_MoreInfoList.addView(InfoItem);
        }

        return true;
    }

    private boolean m_UpdateMoreInfoList(int index, String value) {
        View InfoItem = m_MoreInfoList.getChildAt(index);
        TextView itemValue = (TextView) InfoItem
                .findViewById(R.id.more_info_item_value);
        itemValue.setText(value);

        ImageView check = (ImageView) InfoItem
                .findViewById(R.id.more_info_check);
        check.setImageResource(R.mipmap.checkbox_checked);

        return true;
    }

    //@Override
    //public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    public void processOnItemClick(int position) {
        // TODO Auto-generated method stub
        switch (position) {
            case Constants.ACCOUNT_MORE_INFO_CATEGORY: {
                Log.i(Constants.TAG, "--onItemClick--ACCOUNT_MORE_INFO_CATEGORY--");
                MobclickAgent.onEvent(mContext, "add_category");
                Intent intent = new Intent();
                intent.setClass(this, AccountAddCategoryActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("category", m_LatestCategory);
                intent.putExtras(mBundle);
                startActivityForResult(intent, Constants.ACCOUNT_MORE_INFO_CATEGORY);
            }
            break;
            case Constants.ACCOUNT_MORE_INFO_BRAND: {
                Log.i(Constants.TAG, "--onItemClick--ACCOUNT_MORE_INFO_BRABD--");
                MobclickAgent.onEvent(mContext, "add_brand");
                Intent intent = new Intent();
                intent.setClass(this, AccountAddBrandActivity.class);

                Bundle mBundle = new Bundle();
                mBundle.putString("brand", m_LatestBrand);
                intent.putExtras(mBundle);

                startActivityForResult(intent, Constants.ACCOUNT_MORE_INFO_BRAND);
            }
            break;
            case Constants.ACCOUNT_MORE_INFO_POSITION: {
                Log.i(Constants.TAG, "--onItemClick--ACCOUNT_MORE_INFO_POSITION--");
                MobclickAgent.onEvent(mContext, "add_position");
                Intent intent = new Intent();
                intent.setClass(this, AccountAddPositionActivity.class);

                startActivityForResult(intent, Constants.ACCOUNT_MORE_INFO_POSITION);
            }
            break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(Constants.TAG, "onActivityResult" + "requestCode" + requestCode + "---resultCode=" + resultCode);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.ACCOUNT_MORE_INFO_CATEGORY: {
                    if (data != null) {
                        String Category = data.getStringExtra("category");
                        Log.i(Constants.TAG, "----Category--" + Category);
                        m_LatestCategory = Category;

                        m_UpdateMoreInfoList(requestCode, m_LatestCategory);
                    }

                }
                break;
                case Constants.ACCOUNT_MORE_INFO_BRAND: {
                    if (data != null) {
                        String brand = data.getStringExtra("brand");
                        Log.i(Constants.TAG, "----brand--" + brand);
                        m_LatestBrand = brand;
                        m_UpdateMoreInfoList(requestCode, m_LatestBrand);
                    }
                }
                break;

                case Constants.ACCOUNT_MORE_INFO_POSITION: {
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        PoiInfo poi = bundle.getParcelable("poi");
                        Log.i(Constants.TAG, "--ACCOUNT_MORE_INFO_POSITION--position-result-" + poi);
                        m_LatestPoi = poi;
                        m_LatestPosition = poi.name;
                        m_bPoiInfoChange = true;
                        m_UpdateMoreInfoList(requestCode, m_LatestPosition);
                    }
                }
                break;

                case Constants.ACCOUNT_MORE_INFO_IMAGE: {
                    if (data != null) {

                        m_bImageListChange = true;

                        m_LatestImageList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);

                        Log.i(Constants.TAG, "----imageSelectedlist-data size--" + m_LatestImageList.size());

                        m_UpdateImageList(m_LatestImageList);
                    } else {
                        Log.i(Constants.TAG, "----ACCOUNT_MORE_INFO_IMAGE-data == null-");
                    }
                }
                break;

                default:
                    break;
            }
            //m_LoadMoreInfoDataSrc();
            //m_MoreInfoAdapter.replaceDataSrc(mMoreInfoListDataSource);
            //m_UpdateMoreInfoList();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private Boolean m_InitImageList() {
        if (!m_bCreateNewAccount) {
            ArrayList<ImageItem> dataList = (ArrayList<ImageItem>) m_CurrentAccount.Imageitems();

            if (dataList.size() <= 0) {
                return false;
            }

            for (int index = 0; index < dataList.size(); index++) {
                m_LatestImageList.add(dataList.get(index).Path);
            }

            m_UpdateImageList(m_LatestImageList);
        }
        return true;
    }

    private Boolean m_UpdateImageList(ArrayList<String> result) {
        m_ImageContents = (LinearLayout) findViewById(R.id.image_contents);
        m_ImageContents.removeAllViews();

        for (int index = 0; index < result.size(); index++) {
            String addPath = result.get(index);

            Log.i(Constants.TAG, "--ACCOUNT_MORE_INFO_IMAGE --add new Image--" + addPath);

            final int nCurrentIndex = index;
            final View commentItem = mLayoutInflater.inflate(
                    R.layout.activity_account_more_info_item_image, null);
            ImageView ImageItem = (ImageView) commentItem
                    .findViewById(R.id.more_info_item_image);

            String DecoderImagePath = "file://" + addPath;

            Log.i(Constants.TAG, "--ACCOUNT_MORE_INFO_IMAGE --DecoderImagePath--" + DecoderImagePath);

            Picasso.with(mContext)
            		.load(DecoderImagePath)
                    .fit()
            		.into(ImageItem);

            commentItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                    View messageContent = mLayoutInflater.inflate(
                            R.layout.dialog_content_info, null);
                    builder.setView(messageContent);

                    TextView content = (TextView) messageContent.findViewById(R.id.dialog_message_content);
                    content.setText(getString(R.string.confirm_to_delete_image));

                    builder.setPositiveButton(R.string.give_up_sure,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    m_ImageContents.removeView(commentItem);
                                    m_LatestImageList.remove(nCurrentIndex);
                                }
                            }).setNegativeButton(R.string.give_up_cancel, null)
                            .create().show();
                    return false;
                }
            });
            m_ImageContents.addView(commentItem);
        }
        return true;
    }

    private Boolean m_SaveAccount() {
        if (m_CurrentAccount != null) {

            MobclickAgent.onEvent(mContext, "save_account");

            //save comments
            m_CurrentAccount.Comments = m_CommentsText.getText().toString();

            m_CurrentAccount.CreateTime = m_LatestCreateTime;

            m_CurrentAccount.Brand = m_LatestBrand;

            m_CurrentAccount.Position = m_LatestPosition;

            m_CurrentAccount.Category = m_LatestCategory;

            m_CurrentAccount.Cost = m_LatestCost;

            if (m_CurrentAccount.isSyncOnServer()) {
                m_CurrentAccount.SyncStatus = Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_UP;
            }

            m_CurrentAccount.save();
            Log.i(Constants.TAG, "--m_CurrentAccount save id ---" + m_CurrentAccount.getId());

            if(m_LatestPoi != null) {
                //delete old poi info
                if (PoiItem.GetPoiItem(m_CurrentAccount) != null) {
                    PoiItem.delete(m_CurrentAccount);
                }
                //add new poi info
                PoiItem poi = PoiItem.build(m_LatestPoi, m_CurrentAccount);
                poi.save();
            }
            if (m_bPoiInfoChange) {
                PoiItem.delete(m_CurrentAccount);

                PoiItem item = PoiItem.build(m_LatestPoi, m_CurrentAccount);
                item.save();
            }

            if (m_bImageListChange) {

                if (!m_bCreateNewAccount) {
                    ImageItem.deleteAll(m_CurrentAccount);
                }

                MobclickAgent.onEvent(mContext, "add_images");

                Log.i(Constants.TAG, "----imageSelectedlist-data size--" + m_LatestImageList.size());
                ActiveAndroid.beginTransaction();
                try {
                    for (int index = 0; index < m_LatestImageList.size(); index++) {
                        String addPath = m_LatestImageList.get(index);

                        Log.i(Constants.TAG, "--ACCOUNT_MORE_INFO_IMAGE --add new Image--" + addPath);

                        ImageItem item = new ImageItem();
                        item.Path = addPath;
                        item.account = m_CurrentAccount;
                        item.save();

                    }
                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                }
            }
        }
        return true;
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        Log.i(Constants.TAG, "-onPause  -------");
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
