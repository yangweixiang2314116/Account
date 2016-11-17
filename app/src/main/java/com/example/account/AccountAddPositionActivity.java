package com.example.account;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.example.module.BaseActivity;
import com.example.module.DialogHelp;
import com.example.module.NetworkUtils;
import com.example.module.OfflineHistory;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class AccountAddPositionActivity extends BaseActivity implements BDLocationListener, OnGetGeoCoderResultListener, OnScrollListener {

    private MaterialSearchView mSearchView;
    private FlowLayout mOfflineHistoryLayout;
    protected ArrayList<OfflineHistory> mOfflineHistoryDataSource = new ArrayList<OfflineHistory>();
    private RelativeLayout mOfflineRecentlyRL;

    private ListView poisLL;

    /**
     * 定位端
     */
    private LocationClient mLocClient;
    /**
     * 定位坐标
     */
    private LatLng locationLatLng;
    /**
     * 定位城市
     */
    private String city;
    /**
     * 反地理编码
     */
    private GeoCoder geoCoder;

    /**
     * 搜索输入框对应的ListView
     */
    private ListView searchPois;
    private LayoutInflater mLayoutInflater;
    private View mFooterView;
    private TextView mLoadText;
    private ProgressBar mLoadProgress;
    private String mKeyWords = "";
    private Integer mCurrentPageNumber = 0;
    private Boolean mUpdating = false;
    private boolean mIsEnd = false;
    private PoiSearchAdapter mSearchAdapter = null;

    private Intent mIntent = null;
    private Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.in_push_right_to_left, R.anim.in_stable);
        setTheme(R.style.MIS_NO_ACTIONBAR);
        setContentView(R.layout.activity_account_add_position);

        Toolbar toolbar = (Toolbar) findViewById(R.id.position_toolbar);
        if(toolbar != null){
            setSupportActionBar(toolbar);
        }
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setTitle(getString(R.string.add_position_app_name));
        }

        mLayoutInflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mIntent = getIntent();

        mContext = this;

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mInitSearchView();

        mInitOfflineHistory();

        if (NetworkUtils.isNetworkAvailable(mContext)) {
            initView();
        } else {
            m_ShowNetWorkMessageBox();
        }
    }

    private boolean m_ShowNetWorkMessageBox() {

        android.support.v7.app.AlertDialog.Builder dialog = DialogHelp.getMessageDialog(mContext, getString(R.string.network_disconnect), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                finish();
                overridePendingTransition(R.anim.in_stable, R.anim.out_push_left_to_right);
            }
        });
        dialog.setCancelable(false);
        dialog.show();

        return true;
    }

    private void initView() {
        poisLL = (ListView) findViewById(R.id.main_pois);

        poisLL.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MobclickAgent.onEvent(mContext, "click_offline_position_locationlist");
                PoiAdapter adapter = (PoiAdapter) poisLL.getAdapter();
                if (adapter != null) {

                    PoiInfo poi = (PoiInfo) adapter.getItem(position);

                    Log.i(Constants.TAG, "--PoiInfo place_area--" + poi.city);
                    Log.i(Constants.TAG, "--PoiInfo longitude--" + poi.name);
                    Log.i(Constants.TAG, "--post account item address--" + poi.address);
                    Log.i(Constants.TAG, "--post account item uid--" + poi.uid);

                    mSavePoiItem(poi);

                    Bundle data = new Bundle();
                    data.putParcelable("poi", poi);
                    mIntent.putExtras(data);

                    setResult(Activity.RESULT_OK, mIntent);

                    getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                    finish();
                    overridePendingTransition(R.anim.in_stable, R.anim.out_push_left_to_right);
                }
            }
        });

        searchPois = (ListView) findViewById(R.id.main_search_pois);

        searchPois.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MobclickAgent.onEvent(mContext, "click_offline_position_searchlist");
                if (mSearchAdapter != null) {
                    PoiInfo poi = (PoiInfo) mSearchAdapter.getItem(position);

                    mSavePoiItem(poi);
                    Bundle data = new Bundle();
                    data.putParcelable("poi", poi);
                    mIntent.putExtras(data);

                    setResult(Activity.RESULT_OK, mIntent);

                    getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                    finish();
                    overridePendingTransition(R.anim.in_stable, R.anim.out_push_left_to_right);
                }
            }
        });

        mFooterView = mLayoutInflater.inflate(R.layout.load_item, null);
        mLoadProgress = (ProgressBar) mFooterView.findViewById(R.id.poi_loading);
        mLoadText = (TextView) mFooterView.findViewById(R.id.load_text);
        searchPois.addFooterView(mFooterView);

        //初始化定位
        mLocClient = new LocationClient(this);
        //注册定位监听
        mLocClient.registerLocationListener(this);

        //定位选项
        LocationClientOption option = new LocationClientOption();
        /**
         * coorType - 取值有3个：
         * 返回国测局经纬度坐标系：gcj02
         * 返回百度墨卡托坐标系 ：bd09
         * 返回百度经纬度坐标系 ：bd09ll
         */
        option.setCoorType("bd09ll");
        //设置是否需要地址信息，默认为无地址
        option.setIsNeedAddress(true);
        //设置是否需要返回位置语义化信息，可以在BDLocation.getLocationDescribe()中得到数据，ex:"在天安门附近"， 可以用作地址信息的补充
        option.setIsNeedLocationDescribe(true);
        //设置是否需要返回位置POI信息，可以在BDLocation.getPoiList()中得到数据
        option.setIsNeedLocationPoiList(true);
        /**
         * 设置定位模式
         * Battery_Saving
         * 低功耗模式
         * Device_Sensors
         * 仅设备(Gps)模式
         * Hight_Accuracy
         * 高精度模式
         */
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //设置是否打开gps进行定位
        option.setOpenGps(true);
        //设置扫描间隔，单位是毫秒 当<1000(1s)时，定时定位无效
        option.setScanSpan(1000);

        //设置 LocationClientOption
        mLocClient.setLocOption(option);

        //开始定位
        mLocClient.start();

        MobclickAgent.onEvent(mContext, "enter_offline_position");
    }

    /**
     * 定位监听
     *
     * @param bdLocation
     */
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {

        //如果bdLocation为空或mapView销毁后不再处理新数据接收的位置
        if (bdLocation == null) {
            return;
        }

        //定位数据
        MyLocationData data = new MyLocationData.Builder()
                //定位精度bdLocation.getRadius()
                .accuracy(bdLocation.getRadius())
                //此处设置开发者获取到的方向信息，顺时针0-360
                .direction(bdLocation.getDirection())
                //经度
                .latitude(bdLocation.getLatitude())
                //纬度
                .longitude(bdLocation.getLongitude())
                //构建
                .build();

        //获取坐标，待会用于POI信息点与定位的距离
        locationLatLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
        //获取城市，待会用于POISearch
        city = bdLocation.getCity();


        //创建GeoCoder实例对象
        geoCoder = GeoCoder.newInstance();
        //发起反地理编码请求(经纬度->地址信息)
        ReverseGeoCodeOption reverseGeoCodeOption = new ReverseGeoCodeOption();
        //设置反地理编码位置坐标
        reverseGeoCodeOption.location(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
        geoCoder.reverseGeoCode(reverseGeoCodeOption);

        //设置查询结果监听者
        geoCoder.setOnGetGeoCodeResultListener(this);
    }

    //地理编码查询结果回调函数
    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
    }

    //反地理编码查询结果回调函数
    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

        Log.i(Constants.TAG, "------AccountAddPositionActivity---- onGetReverseGeoCodeResult-----");

        List<PoiInfo> poiInfos = reverseGeoCodeResult.getPoiList();
        PoiAdapter poiAdapter = new PoiAdapter(AccountAddPositionActivity.this, poiInfos);
        Log.i(Constants.TAG, "------ onGetReverseGeoCodeResult---size--"+poiInfos.size());
        poisLL.setAdapter(poiAdapter);
        poisLL.requestFocus();

        if (mLocClient.isStarted()) {
            Log.i(Constants.TAG, "------AccountAddPositionActivity---- stop-----");
            mLocClient.stop();
        }
    }

    private void m_TriggerSearchData() {
        Log.i(Constants.TAG, "------m_TriggerSearchData mKeyWords-----" + mKeyWords + "--mCurrentPageNumber--" + mCurrentPageNumber);

        //创建PoiSearch实例
        PoiSearch poiSearch = PoiSearch.newInstance();
        //城市内检索
        PoiCitySearchOption poiCitySearchOption = new PoiCitySearchOption();
        //关键字
        poiCitySearchOption.keyword(mKeyWords);
        //城市
        poiCitySearchOption.city(city);
        //设置每页容量，默认为每页14条
        poiCitySearchOption.pageCapacity(14);
        //分页编号
        poiCitySearchOption.pageNum(mCurrentPageNumber++);
        poiSearch.searchInCity(poiCitySearchOption);

        mUpdating = true;
        //设置poi检索监听者
        poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            //poi 查询结果回调
            @Override
            public void onGetPoiResult(PoiResult poiResult) {

                mUpdating = false;

                if (poiResult == null) {
                    Log.i(Constants.TAG, "------onGetPoiResult no data !!!!-----");
                    Toast.makeText(mContext, R.string.account_search_failed, Toast.LENGTH_SHORT).show();
                    return;
                }

                List<PoiInfo> poiInfos = poiResult.getAllPoi();
                if (poiInfos != null && poiInfos.size() > 0) {
                    Log.i(Constants.TAG, "------mSearchAdapter----I poiInfos.size()-----"+ poiInfos.size());
                    if (mSearchAdapter != null) {
                        Log.i(Constants.TAG, "------AccountAddPositionActivity----AddNewPOI !!!!-----");
                        mSearchAdapter.AddNewPOI(poiInfos);
                    } else {
                        Log.i(Constants.TAG, "------AccountAddPositionActivity----Initial  adapter-----");
                        mSearchAdapter = new PoiSearchAdapter(AccountAddPositionActivity.this, poiInfos, locationLatLng);
                        searchPois.setAdapter(mSearchAdapter);
                    }

                    searchPois.setVisibility(View.VISIBLE);
                    mLoadProgress.setVisibility(View.VISIBLE);
                    mLoadText.setVisibility(View.INVISIBLE);
                    searchPois.setOnScrollListener(AccountAddPositionActivity.this);
                    mFooterView.setOnClickListener(null);
                } else {
                    if (mCurrentPageNumber != 1) {
                        mIsEnd = true;
                        mLoadProgress.setVisibility(View.INVISIBLE);
                        mLoadText.setText(R.string.end);
                        mLoadText.setVisibility(View.VISIBLE);
                        Log.i(Constants.TAG, "------AccountAddPositionActivity---- last page !!!!-----");
                    } else {
                        Log.i(Constants.TAG, "------AccountAddPositionActivity---- getAllPoi no data !!!!-----");
                        Toast.makeText(mContext, R.string.account_search_failed, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            //poi 详情查询结果回调
            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });
    }

    private void m_RequestSearchData(String value) {
        if (value.length() == 0 || "".equals(value)) {
            searchPois.setVisibility(View.GONE);
        } else {
            mKeyWords = value;
            mCurrentPageNumber = 0;
            mSearchAdapter = null;
            mIsEnd = false;
            m_TriggerSearchData();
        }
    }

    //回退键
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocClient != null) {
            //退出时停止定位
            mLocClient.stop();

            //释放资源
            if (geoCoder != null) {
                geoCoder.destroy();
            }

        }
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (mUpdating) {
            return;
        }
        if (mUpdating == false && totalItemCount != 0
                && firstVisibleItem + visibleItemCount >= totalItemCount && !mIsEnd) {
            mUpdating = true;
            m_TriggerSearchData();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(item);

        return true;
    }

    private  void  mInitOfflineHistory()
    {
        TextView clearOnlineButton  = (TextView) findViewById(R.id.clear_position_history);

        mOfflineHistoryLayout = (FlowLayout) findViewById(R.id.position_history_content);

        mOfflineRecentlyRL = (RelativeLayout) findViewById(R.id.account_recently_offline_position_part);

        Log.i(Constants.TAG, "------setOnClickListener---clear_brand_history--");
        clearOnlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(Constants.TAG, "------enter into onClick---clearCategoryButton--");
                OfflineHistory.deleteAll();
                Toast.makeText(mContext, getString(R.string.accout_search_clear_history_success), Toast.LENGTH_SHORT).show();
                mOfflineHistoryLayout.setVisibility(View.GONE);
                mOfflineRecentlyRL.setVisibility(View.GONE);
            }
        });

        mOfflineHistoryDataSource = (ArrayList<OfflineHistory>) OfflineHistory.GetHistoryItems();

        Log.i(Constants.TAG, "------mOfflineHistoryDataSource.size()--------" + mOfflineHistoryDataSource.size());

        if (mOfflineHistoryDataSource.size() > 0) {
            mOfflineHistoryLayout.setVisibility(View.VISIBLE);
            for (int index = 0; index < mOfflineHistoryDataSource.size(); index++) {
                addTextView(mOfflineHistoryDataSource.get(index));
            }
        } else {
            mOfflineHistoryLayout.setVisibility(View.GONE);
            mOfflineRecentlyRL.setVisibility(View.GONE);
        }
    }

    public void addTextView(OfflineHistory item) {

        TextView offlineTag = (TextView) LayoutInflater.from(this).inflate(R.layout.flow_layout_item, mOfflineHistoryLayout, false);
        offlineTag.setText(item.name);
        offlineTag.setTag(item);
        offlineTag.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                TextView offItem = (TextView) v;
                OfflineHistory chose = (OfflineHistory)offItem.getTag();
                if(null == chose)
                {
                    Log.i(Constants.TAG, "------null == chose--------");
                    return ;
                }
                Log.i(Constants.TAG, "------addTextView--------" + chose.name);
                Bundle data = new Bundle();
                data.putParcelable("offline", chose);
                mIntent.putExtras(data);

                setResult(Activity.RESULT_OK, mIntent);

                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                finish();
                overridePendingTransition(R.anim.in_stable, R.anim.out_push_left_to_right);

            }
        });

        mOfflineHistoryLayout.addView(offlineTag);
    }

    private void mSavePoiItem(PoiInfo poi)
    {
        if (false == OfflineHistory.IsExistOfflineContent(poi)) {

            OfflineHistory item = OfflineHistory.Build(poi);
            item.save();
            Log.i(Constants.TAG, "------mCurOffLineContent save on DB success--------" + poi.name);
        } else {
            Log.i(Constants.TAG, "------mCurOffLineContent already on DB--------" + poi.name);
            OfflineHistory item = OfflineHistory.GetOfflineitemByContent(poi);
            item.lastUseTime = System.currentTimeMillis();
            item.save();
        }
    }

    private void mInitSearchView()
    {
        mSearchView = (MaterialSearchView) findViewById(R.id.search_view);
        mSearchView.setVoiceSearch(false);
        mSearchView.setCursorDrawable(R.drawable.color_cursor);
        mSearchView.setEllipsize(true);
        //mSearchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(Constants.TAG, "------onQueryTextSubmit-----");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                Log.i(Constants.TAG, "------onQueryTextChange-----"+newText);
                m_RequestSearchData(newText);
                return false;
            }
        });

        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
                Log.i(Constants.TAG, "------onSearchViewShown-----");
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
                Log.i(Constants.TAG, "------onSearchViewClosed-----");
            }
        });
    }

}