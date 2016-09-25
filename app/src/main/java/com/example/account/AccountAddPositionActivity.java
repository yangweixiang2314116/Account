package com.example.account;


//public class AccountAddPositionActivity extends ActionBarActivity {

	//private FlowLayout mHotFlowLayout;
	
	//private Intent  mIntent = null;
    //private Context mContext = null;

	/*
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setTitle(getString(R.string.add_position_app_name));
		
		setContentView(R.layout.activity_account_add_category);
		
		//mHotFlowLayout = (FlowLayout) findViewById(R.id.hot_category_content);
		
		mIntent = getIntent();
				
		mContext = this;
		//init();

		MobclickAgent.onEvent(mContext, "enter_position");
	}
	*/


	/*
	public void init() {
		AccountApiConnector.instance(this).getHotPositionList( new JsonHttpResponseHandler() {
            
			 @Override
	            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
	                // If the response is JSONObject instead of expected JSONArray
				 	Log.i(Constants.TAG, "---getHotTagList--onSuccess--response---"+response);
				 	
				 	new ProcessPositionTask(response).execute();
	            }
			 
		        @Override
		        
		        public void  onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response)
		        {
		            super.onFailure(statusCode, headers, throwable, response);
				 	Log.i(Constants.TAG, "---getHotTagList--onFailure--statusCode---"+statusCode);
				 	Log.i(Constants.TAG, "---getHotTagList--onFailure--responseString---"+response);
				 	
		            Toast.makeText(mContext, R.string.get_data_error, Toast.LENGTH_SHORT).show();
		        }

               @Override
               public void onFinish() {
                   super.onFinish();
               }
	            
       });	
	}

	*/

	/*
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
	*/

	/*
	private class PositionInfo
	{
		public long id;
		public String shop;
		
		public boolean build(JSONObject response)
		{
	        if (Looper.myLooper() == Looper.getMainLooper()) {
	            Throwable warn = new Throwable("Please do not execute Animation.build(JSONObject object) " +
	                    "in Main thread, it's bad performance and may block the ui thread");
	            throw new RuntimeException(warn);
	        }
	        
	        try {
	        	id = response.isNull("id") ? 0 : response.getLong("id");
	        	shop = response.isNull("shop") ? "" : response.getString("shop");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        return true;
		}
	}
	
	   private class ProcessPositionTask extends AsyncTask<Void, Void, Boolean> {
	        private JSONArray m_responseObject = null;
	        private ArrayList<PositionInfo> m_PositionList = null;
	        
	        
	        public ProcessPositionTask(JSONArray responseObject) {
	        	m_responseObject = responseObject;
	        	m_PositionList = new ArrayList<PositionInfo>();
	        }

	        @Override
	        protected Boolean doInBackground(Void... voids) {
	        	Boolean bResult = false;
	            Log.i(Constants.TAG, "----ProcessPositionTask--doInBackground----");
	            try {
	            	//TODO
	                for (int index = 0; index < m_responseObject.length(); index++) {
	                	
	                	Log.i(Constants.TAG, "----------"+m_responseObject.getJSONObject(index));
	                	
	                	PositionInfo item = new PositionInfo();
	                	item.build(m_responseObject.getJSONObject(index));
	                	m_PositionList.add(item);
	                }
	                
	            	bResult = true;
	            } catch (Exception e) {
	                e.printStackTrace();
	                bResult = false;
	            } finally {
	                return bResult;
	            }
	        }

	        @Override
	        protected void onPostExecute(Boolean result) {
	            super.onPostExecute(result);
	            Log.i(Constants.TAG, "----ProcessBrandTask--onPostExecute----");
	    		for (int index = 0; index < m_PositionList.size(); index++ ) {
	    			addTextView(m_PositionList.get(index).shop);
	    		}
	    		
	        }
	    }
	*/

	/*
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	*/
//}

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
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
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
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
import com.example.module.NetworkUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

public class AccountAddPositionActivity extends AppCompatActivity implements BDLocationListener, OnGetGeoCoderResultListener, BaiduMap.OnMapStatusChangeListener, TextWatcher, OnScrollListener  {

	private String mCurSearchContent = "";
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private ListView poisLL;

	/**
	 * 定位模式
	 */
	private MyLocationConfiguration.LocationMode mCurrentMode;
	/**
	 * 定位端
	 */
	private LocationClient mLocClient;
	/**
	 * 是否是第一次定位
	 */
	private boolean isFirstLoc = true;
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
	 * 界面上方布局
	 */
	private RelativeLayout topRL;
	/**
	 * 搜索地址输入框
	 */
	private EditText searchAddress;
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
		mLayoutInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mIntent = getIntent();

		mContext = this;

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		if(NetworkUtils.isNetworkAvailable(mContext)) {
			initView();
		}
		else{
			m_ShowNetWorkMessageBox();
		}
	}

	private boolean m_ShowNetWorkMessageBox()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

		View messageContent = mLayoutInflater.inflate(
				R.layout.dialog_content_info, null);
		builder.setView(messageContent);

		TextView  content = (TextView)messageContent.findViewById(R.id.dialog_message_content);
		content.setText(getString(R.string.network_disconnect));

		builder.setPositiveButton(R.string.give_up_sure,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
										int which) {
						getWindow().setSoftInputMode(
								WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

						finish();
						overridePendingTransition(R.anim.in_stable, R.anim.out_push_left_to_right);
					}
				})	.create().show();
		return true;
	}
	private void initView() {
		mMapView = (MapView) findViewById(R.id.main_bdmap);
		mBaiduMap = mMapView.getMap();

		poisLL = (ListView) findViewById(R.id.main_pois);

		poisLL.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				PoiAdapter adapter = (PoiAdapter) poisLL.getAdapter();
					if(adapter != null)
					{
						//hide soft input
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(searchAddress.getWindowToken(), 0);

						PoiInfo poi = (PoiInfo)adapter.getItem(position);

						Log.i(Constants.TAG, "--PoiInfo place_area--"+poi.city);
						Log.i(Constants.TAG, "--PoiInfo longitude--"+poi.name);
						Log.i(Constants.TAG, "--post account item address--"+poi.address);
						Log.i(Constants.TAG, "--post account item uid--" + poi.uid);

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

		topRL = (RelativeLayout) findViewById(R.id.main_top_RL);

		searchAddress = (EditText) findViewById(R.id.main_search_address);

		searchPois = (ListView) findViewById(R.id.main_search_pois);

		searchPois.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if(mSearchAdapter != null)
				{
					//hide soft input
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(searchAddress.getWindowToken(), 0);

					PoiInfo poi = (PoiInfo)mSearchAdapter.getItem(position);
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

		//定义地图状态
		MapStatus mMapStatus = new MapStatus.Builder().zoom(18).build();
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
		//改变地图状态
		mBaiduMap.setMapStatus(mMapStatusUpdate);

		//地图状态改变相关监听
		mBaiduMap.setOnMapStatusChangeListener(this);

		//开启定位图层
		mBaiduMap.setMyLocationEnabled(true);

		//定位图层显示方式
		mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;

		/**
		 * 设置定位图层配置信息，只有先允许定位图层后设置定位图层配置信息才会生效
		 * customMarker用户自定义定位图标
		 * enableDirection是否允许显示方向信息
		 * locationMode定位图层显示方式
		 */
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, null));

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

		MobclickAgent.onEvent(mContext, "enter_position");
	}

	/**
	 * 定位监听
	 *
	 * @param bdLocation
	 */
	@Override
	public void onReceiveLocation(BDLocation bdLocation) {

		//如果bdLocation为空或mapView销毁后不再处理新数据接收的位置
		if (bdLocation == null || mBaiduMap == null) {
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

		//设置定位数据
		mBaiduMap.setMyLocationData(data);

		//是否是第一次定位
		if (isFirstLoc) {
			isFirstLoc = false;
			LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
			MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(ll, 18);
			mBaiduMap.animateMapStatus(msu);
		}

		//获取坐标，待会用于POI信息点与定位的距离
		locationLatLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
		//获取城市，待会用于POISearch
		city = bdLocation.getCity();

		//文本输入框改变监听，必须在定位完成之后
		searchAddress.addTextChangedListener(this);
		searchAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					searchAddress.setCursorVisible(true);
					getWindow().setSoftInputMode(
							WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST);
				} else {
					searchAddress.setCursorVisible(false);
				}
			}
		});

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
		poisLL.setAdapter(poiAdapter);
		poisLL.requestFocus();

		if(mLocClient.isStarted()) {
			Log.i(Constants.TAG, "------AccountAddPositionActivity---- stop-----");
			mLocClient.stop();
		}
	}


	/**
	 * 手势操作地图，设置地图状态等操作导致地图状态开始改变
	 *
	 * @param mapStatus 地图状态改变开始时的地图状态
	 */
	@Override
	public void onMapStatusChangeStart(MapStatus mapStatus) {
	}

	/**
	 * 地图状态变化中
	 *
	 * @param mapStatus 当前地图状态
	 */
	@Override
	public void onMapStatusChange(MapStatus mapStatus) {
	}

	/**
	 * 地图状态改变结束
	 *
	 * @param mapStatus 地图状态改变结束后的地图状态
	 */
	@Override
	public void onMapStatusChangeFinish(MapStatus mapStatus) {
		//地图操作的中心点
		Log.i(Constants.TAG, "------AccountAddPositionActivity---- onMapStatusChangeFinish-----");
		LatLng cenpt = mapStatus.target;
		geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(cenpt));
	}

	/**
	 * 输入框监听---输入之前
	 *
	 * @param s     字符序列
	 * @param start 开始
	 * @param count 总计
	 * @param after 之后
	 */
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	/**
	 * 输入框监听---正在输入
	 *
	 * @param s      字符序列
	 * @param start  开始
	 * @param before 之前
	 * @param count  总计
	 */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	/**
	 * 输入框监听---输入完毕
	 *
	 * @param s
	 */
	@Override
	public void afterTextChanged(Editable s) {
		m_RequestSearchData(s.toString());
	}


	private void  m_TriggerSearchData()
	{
		Log.i(Constants.TAG, "------m_TriggerSearchData mKeyWords-----"+mKeyWords+ "--mCurrentPageNumber--"+mCurrentPageNumber);

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

				if(poiResult == null)
				{
					Log.i(Constants.TAG, "------onGetPoiResult no data !!!!-----");
					Toast.makeText(mContext, R.string.account_search_failed, Toast.LENGTH_SHORT).show();
					return ;
				}

				List<PoiInfo> poiInfos = poiResult.getAllPoi();
				if(poiInfos != null &&poiInfos.size() > 0) {

					if(mSearchAdapter != null)
					{
						Log.i(Constants.TAG, "------AccountAddPositionActivity----AddNewPOI !!!!-----");
						mSearchAdapter.AddNewPOI(poiInfos);
					}
					else
					{
						Log.i(Constants.TAG, "------AccountAddPositionActivity----Initial  adapter-----");
						mSearchAdapter = new PoiSearchAdapter(AccountAddPositionActivity.this, poiInfos, locationLatLng);
						searchPois.setAdapter(mSearchAdapter);
					}

					searchPois.setVisibility(View.VISIBLE);
					mLoadProgress.setVisibility(View.VISIBLE);
					mLoadText.setVisibility(View.INVISIBLE);
					searchPois.setOnScrollListener(AccountAddPositionActivity.this);
					mFooterView.setOnClickListener(null);
				}
				else
				{
					if(mCurrentPageNumber != 1) {
						mIsEnd = true;
						mLoadProgress.setVisibility(View.INVISIBLE);
						mLoadText.setText(R.string.end);
						mLoadText.setVisibility(View.VISIBLE);
						Log.i(Constants.TAG, "------AccountAddPositionActivity---- last page !!!!-----");
					}
					else {
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

	private  void m_RequestSearchData(String value)
	{
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
		if(mMapView != null) {
			// activity 恢复时同时恢复地图控件
			mMapView.onResume();
		}
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(mMapView != null) {
			// activity 暂停时同时暂停地图控件
			mMapView.onPause();
		}
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mMapView != null) {
			//退出时停止定位
			mLocClient.stop();
			//退出时关闭定位图层
			mBaiduMap.setMyLocationEnabled(false);

			// activity 销毁时同时销毁地图控件
			mMapView.onDestroy();

			//释放资源
			if (geoCoder != null) {
				geoCoder.destroy();
			}

			mMapView = null;
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

}