package com.vp.loveu.util;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.vp.loveu.bean.MapLoactionBean;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

 
/**
 * 地图定位 通过网络
 * @author tanping
 * 2015-11-24
 */
public class MapLocationNetwork  implements AMapLocationListener,Runnable{
	
	public MapLoactionBean mLoactionBean ;
	
	private LocationManagerProxy aMapLocManager = null;
	private AMapLocation aMapLocation;// 用于判断定位超时
	private Handler stopHandler = new Handler();
	
	private Handler mHandler ;
	public static final int MAP_RESULT_WHAT= 2012;//请求返回结果
	
	public MapLocationNetwork(Handler handler,Context context){
		mHandler = handler;
		aMapLocManager = LocationManagerProxy.getInstance(context);
		/*
		 * mAMapLocManager.setGpsEnable(false);//
		 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
		 * API定位采用GPS和网络混合定位方式
		 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
		 */
		aMapLocManager.requestLocationData(
				LocationProviderProxy.AMapNetwork, 2000, 8, this);
		stopHandler.postDelayed(this, 12000);// 设置超过12秒还没有定位到就停止定位
		mLoactionBean = new MapLoactionBean();
	}
	
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void onLocationChanged(AMapLocation location) {
		VPLog.d("map", "onLocationChanged:"+location.toString());
		if (location != null) {
			stopLocation();
			this.aMapLocation = location;// 判断超时机制
			mLoactionBean.adCode = location.getAdCode();
			mLoactionBean.cityCode = location.getCityCode();
			mLoactionBean.province = location.getProvince();
			mLoactionBean.city = location.getCity();
			mLoactionBean.district = location.getDistrict();
			Bundle locBundle = location.getExtras();
			if (locBundle != null) {
				mLoactionBean.cityCode = locBundle.getString("citycode");
				mLoactionBean.desc = locBundle.getString("desc");
			}
			
			Double geoLat = location.getLatitude();
			Double geoLng = location.getLongitude();
			double[] pos = MapCoordinateUtil.bd_encrypt(geoLng, geoLat);
			mLoactionBean.lon = pos[0];
			mLoactionBean.lat = pos[1];
			
			mLoactionBean.result = true;
			
			if (TextUtils.isEmpty(mLoactionBean.city)) {
				VPLog.d("map", "fail p ");
				mLoactionBean.result = false;
			}
			
			Message message = new Message() ;
			message.obj = mLoactionBean;
			message.what = MAP_RESULT_WHAT ; 
			if (mHandler!=null) {
				mHandler.sendMessage(message);
			}
			mLoactionBean =null;
			mHandler = null;
		}else {
			//run();
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		
	}

	
	/**
	 * 销毁定位
	 */
	private void stopLocation() {
		if (aMapLocManager != null) {
			aMapLocManager.removeUpdates(this);
			aMapLocManager.destroy();
		}
		aMapLocManager = null;
	}
	
	@Override
	public void run() {
		VPLog.d("map", "fail postion");
		//定位失败
		Message message = new Message() ;
		message.obj = mLoactionBean;
		message.what = MAP_RESULT_WHAT ; 
		if (mHandler!=null && mLoactionBean!=null) {
			mHandler.sendMessage(message);
		}
		
		if (aMapLocation == null) {
			stopLocation();// 销毁掉定位
		}
		
	}

}
