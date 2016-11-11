package com.vp.loveu.my.bean;

import java.util.List;

import com.vp.loveu.bean.VPBaseBean;

/**
 * @项目名称nameloveu1.0
 * @时间2015年12月12日下午3:15:13
 * @功能TODO
 * @作者 mi
 */

public class MyCenterPayBean extends VPBaseBean{
	public int code;
	public List<MyCenterPayDataBean> data;
	public int is_encrypt;
	public String msg;
	public class MyCenterPayDataBean{
		public String create_time;
		public int id;
		public String json_cont;
		public String order_no;
		public String pay_time;
		public int pay_type;
		public List<String> pics;
		public double price;
		public int product_id;
		public String product_name;
		public int status;
		public int type;
		@Override
		public String toString() {
			return "MyCenterPayDataBean [create_time=" + create_time + ", id=" + id + ", json_cont=" + json_cont + ", order_no=" + order_no
					+ ", pay_time=" + pay_time + ", pay_type=" + pay_type + ", pics=" + pics + ", price=" + price + ", product_id="
					+ product_id + ", product_name=" + product_name + ", status=" + status + ", type=" + type + "]";
		}
	}
}
