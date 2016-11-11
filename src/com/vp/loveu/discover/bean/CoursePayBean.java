package com.vp.loveu.discover.bean;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vp.loveu.R;
import com.vp.loveu.pay.bean.PayBindViewBean;
import com.vp.loveu.util.UIUtils;

/**
 * @author：pzj
 * @date: 2015年12月2日 下午12:02:22
 * @Description:
 */
public class CoursePayBean extends PayBindViewBean {
	private int id;
	private String pic;
	private String name;
	private double price;
	private int courseListener;
	private String remark;
	private int rebate_day;

	private CourseDetailBean mData;
	private String mPromoCode;// 是否是优惠码进来的

	public CoursePayBean(int id, String pic, String name, double price, int courseListener, String remark, int rebate_day) {
		super();
		this.id = id;
		this.pic = pic;
		this.name = name;
		this.price = price;
		this.courseListener = courseListener;
		this.remark = remark;
		this.rebate_day = rebate_day;
	}

	public CoursePayBean(CourseDetailBean data) {
		mData = data;
		this.id = data.getId();
		this.price = data.getPrice();
	}

	@Override
	public View createShowView(Context context) {
		View view = View.inflate(context, R.layout.discover_home_item_view_lesson, null);
		ImageLoader.getInstance().displayImage(mData.getPic(), (ImageView) view.findViewById(R.id.discover_lesson_icon_left));
		((TextView) view.findViewById(R.id.discover_lesson_title)).setText(mData.getName());
		
		TextView tvPrice = (TextView) view.findViewById(R.id.discover_lesson_price);//实际价格
		TextView tvPriceLine = (TextView) view.findViewById(R.id.discover_lesson_price_line);//原来价格
		
		tvPrice.setText("￥" + mData.getPrice()); 
		if (TextUtils.isEmpty(mPromoCode)) {//优惠码为空 只显示实际价格即可
			tvPriceLine.setVisibility(View.GONE);
		}else{
			//展示原有的价格
			tvPriceLine.setVisibility(View.VISIBLE);//显示原价
			String str = "￥"+mData.getOriginal_price()+"";
			tvPriceLine.setText(str);
			TextPaint paint = tvPriceLine.getPaint();
			paint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			paint.setTextSize(tvPrice.getTextSize() + 5);
		}
		
		((TextView) view.findViewById(R.id.discover_lesson_listener)).setText(mData.getUser_num() + "人在学");
		if (mData.getRebate_day() > 0) {
			view.findViewById(R.id.discover_lesson_container).setVisibility(View.VISIBLE);
			((TextView) view.findViewById(R.id.discover_lesson_seven)).setVisibility(View.VISIBLE);
			((TextView) view.findViewById(R.id.discover_lesson_seven)).setText(mData.getRebate_day() + "");
			((TextView) view.findViewById(R.id.discover_lesson_remark)).setText(mData.getRemark());
		}else{
			view.findViewById(R.id.discover_lesson_container).setVisibility(View.INVISIBLE);
		}
		view.setPadding(0, UIUtils.dp2px(10), 0, UIUtils.dp2px(10));
		return view;
	}

	@Override
	public JSONObject getParams() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("id", id);
			obj.put("price", price);
			obj.put("coupon", mPromoCode);
		} catch (JSONException e) {
			Toast.makeText(UIUtils.getContext(), "参数错误", Toast.LENGTH_SHORT).show();
		}
		return obj;
	}

	public void setIsPromoCode(String str) {
		mPromoCode = str;
	}
	public String getPromoCode(){
		return mPromoCode;
	}
}
