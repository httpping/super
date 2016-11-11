package com.vp.loveu.my.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vp.loveu.R;
import com.vp.loveu.discover.ui.CourseDetailActivity;
import com.vp.loveu.index.myutils.DisplayOptionsUtils;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.my.activity.HeartShowActivity;
import com.vp.loveu.my.bean.MyCenterPayBean.MyCenterPayDataBean;
import com.vp.loveu.my.listener.OnPayClickListener;

/**
 * @项目名称nameloveu1.0
 * @时间2015年12月14日上午10:24:17
 * @功能TODO
 * @作者 mi
 */

public class MyPayCourse extends LinearLayout {

	private TextView mTvOrderNumber;
	private TextView mTvOrderTime;
	private ImageView mIvIcon;
	private TextView mTvContent;
	private Button mBtCommit;
	private TextView mTvMoney;
	private MyCenterPayDataBean mDatas;
	
	public MyPayCourse(Context context) {
		this(context,null);
	}
	
	public MyPayCourse(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.my_pay_item_three, this);
		initView();
	}

	private void initView() {
		mTvOrderNumber = (TextView) findViewById(R.id.my_pay_item_three_tv_ordernumber);
		mTvOrderTime = (TextView) findViewById(R.id.my_pay_item_three_tv_ordertime);
		mIvIcon = (ImageView) findViewById(R.id.my_pay_item_three_iv_icon);
		mTvContent = (TextView) findViewById(R.id.my_pay_item_three_tv_content);
		mBtCommit = (Button) findViewById(R.id.my_pay_item_three_btn_commit);
		mTvMoney = (TextView) findViewById(R.id.my_pay_item_three_tv_money);
		mBtCommit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(),HeartShowActivity.class);
				intent.putExtra(HeartShowActivity.KEY, true);
				int id = mDatas.id;
				intent.putExtra("id", id);
				intent.putExtra("normal", true);
				intent.putExtra(HeartShowActivity.TYPE, 1);
				getContext().startActivity(intent);
			}
		});
	}

	public void setData(MyCenterPayDataBean data) {
		if (data == null) {
			return;
		}
		mDatas = data;
		if (data.pics != null && data.pics.size() > 0) {
			ImageLoader.getInstance().displayImage(data.pics.get(0), mIvIcon, DisplayOptionsUtils.getOptionsConfig());
		}
		double money = mDatas.price;
		if (money == 0) {
			mTvMoney.setText("免费");
		}else{
			mTvMoney.setText(money+"元");
		}
		mTvOrderNumber.setText("订单编号 :  "+data.order_no);
		mTvOrderTime.setText("支付时间 :  "+MyUtils.dateFromLong(data.pay_time));
		mTvContent.setText(data.product_name);
		
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), CourseDetailActivity.class);
				intent.putExtra(CourseDetailActivity.COURSE_ID, mDatas.product_id);
				getContext().startActivity(intent);
			}
		});
	}
	public OnPayClickListener listener;
	public void setClickListener(OnPayClickListener listener){
		this.listener = listener;
	}
}
