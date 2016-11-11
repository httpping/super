package com.vp.loveu.coupon.bean;

import java.util.List;

import com.vp.loveu.bean.VPBaseBean;
import com.vp.loveu.my.bean.PromoCodeBean;

/**
 * 选择课程
 * @author tanping
 * 2016-3-7
 */
public class SelectCourseBean extends VPBaseBean {


	public int id;
	public String name;
	public String pic;
	public double price;
	
	public int code;
	public int is_encrypt;
	public String msg;
	
	public List<SelectCourseBean> data;
	
	public boolean isCheck;
	
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
