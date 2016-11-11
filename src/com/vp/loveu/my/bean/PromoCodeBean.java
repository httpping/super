package com.vp.loveu.my.bean;

import java.util.List;

import com.vp.loveu.bean.VPBaseBean;
import com.vp.loveu.index.bean.HotUserBean.HotDataBean;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2016年2月26日上午11:47:50
 * @功能TODO
 * @作者 mi
 */

public class PromoCodeBean extends VPBaseBean {
	public String begin_time;
	public String coupon;
	public String end_time;
	public int id;
	public String name;
	public double original_price;
	public String pic;
	public double price;
	public int src_id;
	public int type;
	public int code =-1;
	public int is_encrypt;
	public String msg;
	public String data;
	
	public int uid;
	public List<PromoCodeBean> adatper_data;

	
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof PromoCodeBean)) {
			return false;
		}
		PromoCodeBean bean =  (PromoCodeBean) o ;
		if (bean.id == id ) {
			return true;
		}
		return super.equals(o);
	}
}
