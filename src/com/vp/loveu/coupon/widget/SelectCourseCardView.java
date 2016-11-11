package com.vp.loveu.coupon.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.vp.loveu.R;
import com.vp.loveu.coupon.bean.SelectCourseBean;
 

/**
 * 优惠券card view
 * 
 * @author tanping
 * 
 */
public class SelectCourseCardView extends RelativeLayout  {

	
	ImageView selectButton;
	TextView courseName ;
	TextView coursePrice ;
	private SelectCourseBean couponCodeBean;// 消息体
	

	public SelectCourseCardView(Context context) {
		super(context);
		initView();
	}

	public SelectCourseCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {
		inflate(getContext(), R.layout.coupon_select_course_item_view, this);
		selectButton = (ImageView) findViewById(R.id.selected_toggle);
		courseName =(TextView) findViewById(R.id.course_name);
		coursePrice = (TextView) findViewById(R.id.course_price);
		
	}

	public void setCouponCodeBean(SelectCourseBean couponCodeBean) {
		this.couponCodeBean = couponCodeBean;
		if (couponCodeBean == null) {
			return ;
		}
		
		if (couponCodeBean.isCheck) {
			selectButton.setSelected(true);
		}
		courseName.setText(couponCodeBean.name);
		coursePrice.setText(couponCodeBean.price+"");
		
	}

	 
	 
	 
}
