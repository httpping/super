package com.vp.loveu.coupon.widget;

import java.util.Date;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.ResultParseUtil;
import com.vp.loveu.R;
import com.vp.loveu.message.utils.VlinkTimeUtil;
import com.vp.loveu.my.bean.PromoCodeBean;
 

/**
 * 优惠券card view
 * 
 * @author tanping
 * 
 */
public class CouponCardView extends RelativeLayout {

	private TextView mCouponCode;
	private TextView mCouponContent;
	private TextView mCouponPrice;
	private TextView mCouponOldPrice;
	private TextView mCouponStatus;
	private RelativeLayout mCouponStatusLayout;
			TextView mCouponPriceFlag;
	private TextView mCouponTimeSche;

	private PromoCodeBean couponCodeBean;// 消息体

	public CouponCardView(Context context) {
		super(context);
		initView();
	}

	public CouponCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {
		inflate(getContext(), R.layout.coupon_item_view, this);
		mCouponCode = (TextView) findViewById(R.id.coupon_code);
		mCouponContent = (TextView) findViewById(R.id.coupon_content);
		mCouponPrice = (TextView) findViewById(R.id.coupon_price);
		mCouponTimeSche = (TextView) findViewById(R.id.coupon_schedule);
		mCouponStatus = (TextView) findViewById(R.id.coupon_able_status);
		mCouponStatusLayout = (RelativeLayout) findViewById(R.id.coupon_able_status_layout);
		mCouponOldPrice = (TextView) findViewById(R.id.coupon_old_price);
		mCouponPriceFlag = (TextView) findViewById(R.id.coupon_price_flag);
	}

	public void setCouponCodeBean(PromoCodeBean couponCodeBean) {
		this.couponCodeBean = couponCodeBean;
		if (couponCodeBean == null) {
			return ;
		}
		mCouponCode.setText("优惠码："+couponCodeBean.coupon);
		mCouponContent.setText(couponCodeBean.name);
		mCouponPrice.setText(couponCodeBean.price+"");
		mCouponOldPrice.setText(couponCodeBean.original_price+"");
		mCouponOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); 

		
		Date beginDate= VlinkTimeUtil.parseYYYYMMDDHHMM(couponCodeBean.begin_time);
		Date endDate = VlinkTimeUtil.parseYYYYMMDDHHMM(couponCodeBean.end_time);
		
		
		//
		if (beginDate !=null && endDate!=null && System.currentTimeMillis() + ResultParseUtil.timeinterval < beginDate.getTime() && System.currentTimeMillis() + ResultParseUtil.timeinterval < endDate.getTime()) {//开始时间大了
			mCouponStatusLayout.setBackgroundResource(R.drawable.coupon_item_right_enable_bg);
			mCouponStatus.setText("未\n生\n效");
			mCouponPrice.setTextColor(getResources().getColor(R.color.share_text_color));
			mCouponPriceFlag.setTextColor(getResources().getColor(R.color.share_text_color));
			mCouponCode.setTextColor(getResources().getColor(R.color.normal_editText_color));
			mCouponContent.setTextColor(getResources().getColor(R.color.dark_gray));
			mCouponOldPrice.setTextColor(getResources().getColor(R.color.gray));
			mCouponTimeSche.setTextColor(getResources().getColor(R.color.grey));
		}else if (endDate ==null || System.currentTimeMillis() + ResultParseUtil.timeinterval < endDate.getTime()) {//不限制
			mCouponStatusLayout.setBackgroundResource(R.drawable.coupon_item_right_enable_bg);
			mCouponStatus.setText("生\n效\n中");
			mCouponPrice.setTextColor(getResources().getColor(R.color.share_text_color));
			mCouponPriceFlag.setTextColor(getResources().getColor(R.color.share_text_color));
			mCouponCode.setTextColor(getResources().getColor(R.color.normal_editText_color));
			mCouponContent.setTextColor(getResources().getColor(R.color.dark_gray));
			mCouponOldPrice.setTextColor(getResources().getColor(R.color.gray));
			mCouponTimeSche.setTextColor(getResources().getColor(R.color.grey));
		}else {
			mCouponStatusLayout.setBackgroundResource(R.drawable.coupon_item_right_disable_bg);
			mCouponStatus.setText("已\n过\n期");
		 
			mCouponPrice.setTextColor(getResources().getColor(R.color.normal_my_gray));
			mCouponPriceFlag.setTextColor(getResources().getColor(R.color.normal_my_gray));
			mCouponCode.setTextColor(getResources().getColor(R.color.normal_my_gray));
			mCouponContent.setTextColor(getResources().getColor(R.color.normal_my_gray));
			mCouponOldPrice.setTextColor(getResources().getColor(R.color.normal_my_gray));
			mCouponTimeSche.setTextColor(getResources().getColor(R.color.normal_my_gray));

		}
		
		String beginTimeStr = VlinkTimeUtil.fromatMMDDHHMM(beginDate);
		String endString = VlinkTimeUtil.fromatMMDDHHMM(endDate);
		beginTimeStr = TextUtils.isEmpty(beginTimeStr)?"-":beginTimeStr;
		endString = TextUtils.isEmpty(endString)?"-":endString;
		String timeSche ="消费时效 ：" +beginTimeStr +" 至 " +endString;
		mCouponTimeSche.setText(timeSche);
		
		
		
	}

	
	 
	 
}
