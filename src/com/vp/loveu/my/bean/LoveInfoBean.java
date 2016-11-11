package com.vp.loveu.my.bean;

import com.vp.loveu.bean.VPBaseBean;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年12月7日下午1:07:43
 * @功能 婚恋资料的数据bean
 * @作者 mi
 */

public class LoveInfoBean extends VPBaseBean{
	public int code;
	public String data;
	public int is_encrypt;
	public String msg;

	public class LoveDataBean extends VPBaseBean{
		//顺序不要改动    
		public int uid;
		public String birthday;
		public int constellations;
		public int zodiak;
		public int edu;
		public int income;
		public int height;
		public int weight;
		public int child_status;
		public int marital_status;
		public int car_status;
		public int house_status;
		public int blood_type;
		public int nation;
		public int job;
		public String home_area_code;
		public String signature;
		@Override
		public String toString() {
			return "LoveDataBean [uid=" + uid + ", birthday=" + birthday + ", constellations=" + constellations + ", zodiak=" + zodiak
					+ ", edu=" + edu + ", income=" + income + ", height=" + height + ", weight=" + weight + ", child_status=" + child_status
					+ ", marital_status=" + marital_status + ", car_status=" + car_status + ", house_status=" + house_status
					+ ", blood_type=" + blood_type + ", nation=" + nation + ", job=" + job + ", home_area_code=" + home_area_code
					+ ", signature=" + signature + "]";
		}
	}		
}
