package com.vp.loveu.util;


/**
 * 地图坐标转换 ，百度 和 高德地图的坐标转换
 * @author tanping
 * 2015-11-20
 */
public class MapCoordinateUtil {
	
	private static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
	
	/**
	 * 将 GCJ-02 坐标转换成 BD-09 坐标
	 * GoogleMap和高德map用的是同一个坐标系GCJ-02
	 * */
	public static double[] bd_encrypt(double gg_lon,double gg_lat) {
		double bd_lat = 0.0;
		double bd_lon = 0.0;
		double location[] = new double[2];
		double x = gg_lon, y = gg_lat;
		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
		bd_lon = z * Math.cos(theta) + 0.0065;
		bd_lat = z * Math.sin(theta) + 0.006;
		location[0] = bd_lon;
		location[1] = bd_lat; 
		return location;
	}

	/**
	 * 将 BD-09 坐标转换成 GCJ-02 坐标
	 * GoogleMap和高德map用的是同一个坐标系GCJ-02
	 * */
	public static double[] bd_decrypt(double bd_lon,double bd_lat) {
		double gg_lat = 0.0;
		double gg_lon = 0.0;
		double location[] = new double[2];
		double x = bd_lon - 0.0065, y = bd_lat - 0.006;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
		gg_lon = z * Math.cos(theta);
		gg_lat = z * Math.sin(theta);
		location[0] = gg_lon;
		location[1] = gg_lat;
		return location;
	}

}
