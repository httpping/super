package com.vp.loveu.bean;

import java.io.Serializable;

/**
 * 地图定位bean
 * @author tanping
 * 2015-11-24
 */
public class MapLoactionBean implements Serializable{
	
	public boolean result = false ;//定位结果
	
	public double lat;
	public double lon;
	
	public String cityCode;
	public String adCode; //区域编码 
	public String province;//省
	public String city ;//市
	public String district; //区（县）
	
	public String desc ;// 位置描述

	@Override
	public String toString() {
		return "MapLoactionBean [result=" + result + ", lat=" + lat + ", lon=" + lon + ", cityCode=" + cityCode + ", adCode=" + adCode
				+ ", province=" + province + ", city=" + city + ", district=" + district + ", desc=" + desc + "]";
	}
	
	
}
