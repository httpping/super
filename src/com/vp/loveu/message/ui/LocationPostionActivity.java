package com.vp.loveu.message.ui;

import android.location.Location;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.message.utils.AMapUtil;
import com.vp.loveu.util.MapCoordinateUtil;
import com.vp.loveu.util.VPLog;

/**
 * 位置信息点击查看详情
 * @author tanping
 * 2015-11-10
 */
public class LocationPostionActivity extends VpActivity implements LocationSource,
		AMapLocationListener,OnCameraChangeListener {
	
	public static final String TAG ="LocationSourceActivity";
	
	
	public static final String  MAP_BITMAP_SHOT ="map_shot";
	public static final String  MAP_BITMAP_LON ="map_lon";
	public static final String  MAP_BITMAP_LAT ="map_lat";
	public static final String  MAP_AREA_DESC ="map_area_desc";
	
	
	public static final String  BD_MAPS_LONG = "baidu_maps_long";
	public static final String  BD_MAPS_LAT = "baidu_maps_lat";
	
	
	private UiSettings mUiSettings;

	private AMap aMap;
	private MapView mapView;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	
	private Marker geoMarker;
	LatLonPoint latLonPoint ;
	String areaDesc;
	
	private int area_level = 0;//地图的zoom
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.locations_position_activity);
		
		initPublicTitle();
		mPubTitleView.mTvTitle.setText("位置");
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);

		double lat = getIntent().getDoubleExtra(BD_MAPS_LAT, -1);
		double lon = getIntent().getDoubleExtra(BD_MAPS_LONG, -1);
		
		if (lat > 0 && lon > 0) {
			double[] gaode = MapCoordinateUtil.bd_decrypt(lon, lat);
			latLonPoint = new LatLonPoint(gaode[1], gaode[0]);
		}
		
		VPLog.d(TAG, "lat:"+latLonPoint);
		
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		
		if (aMap == null) {
			aMap = mapView.getMap();
			aMap.setOnCameraChangeListener(this);// 对amap添加移动地图事件监听器
			geoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
			
			mUiSettings = aMap.getUiSettings();
			mUiSettings.setCompassEnabled(true);
			mUiSettings.setMyLocationButtonEnabled(true);
			mUiSettings.setScaleControlsEnabled(true);
			setUpMap();
		}
	 
	}

	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		/*// 自定义系统定位小蓝点
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
		myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
		myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
		// myLocationStyle.anchor(int,int)//设置小蓝点的锚点
		myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
		aMap.setMyLocationStyle(myLocationStyle);*/
		
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
	   // aMap.setMyLocationType()
		if (latLonPoint !=null ) {//标注传人的位置
			aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
					AMapUtil.convertToLatLng(latLonPoint), 15));
			
			geoMarker.setPosition(AMapUtil.convertToLatLng(latLonPoint));
			aMap.invalidate();

		}
		
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	/**
	 * 此方法已经废弃
	 */
	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		if (mListener != null && aLocation != null) {
			mListener.onLocationChanged(aLocation);// 显示系统小蓝点
			VPLog.d(TAG, "onLocationChanged");
		}
	}

	/**
	 * 激活定位
	 */
	boolean isFirstIn = true;
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		VPLog.d(TAG, "activate");
		if (mAMapLocationManager == null && !isFirstIn) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			/*
			 * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
			mAMapLocationManager.requestLocationData(
					LocationProviderProxy.AMapNetwork, 2000, 10, this);
		}
		isFirstIn = false;
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destroy();
		}
		mAMapLocationManager = null;
	}
 
	@Override
	public void onCameraChange(CameraPosition arg0) {
	/*	LatLonPoint latLonPoint = new LatLonPoint(arg0.target.latitude, arg0.target.longitude);
		VPLog.d(TAG, "onLocationChanged");
		geoMarker.setPosition(AMapUtil.convertToLatLng(latLonPoint));
		//aMap.invalidate();
		if (area_level <=0) {//移动到这里，拉开地图的zoom 
			area_level = 15;
			aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
					AMapUtil.convertToLatLng(latLonPoint), area_level));
		}*/
	}

	/**
	 * 拖动地图修改位置
	 */
	@Override
	public void onCameraChangeFinish(CameraPosition arg0) {

		/*if (latLonPoint!=null && arg0.target.latitude == latLonPoint.getLatitude() && arg0.target.longitude == latLonPoint.getLongitude()) {
			VPLog.d(TAG, "onCameraChangeFinish");
			return;
		}
		
		latLonPoint = new LatLonPoint(arg0.target.latitude, arg0.target.longitude);
		geoMarker.setPosition(AMapUtil.convertToLatLng(latLonPoint));
		aMap.invalidate();
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 50,
				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
		
		mPubTitleView.mBtnRight.setEnabled(true);
		*/
		
	}

 
}
