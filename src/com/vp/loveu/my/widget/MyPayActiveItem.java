package com.vp.loveu.my.widget;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vp.loveu.R;
import com.vp.loveu.index.activity.ActiveWebActivity;
import com.vp.loveu.index.myutils.DisplayOptionsUtils;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.my.bean.MyCenterPayBean.MyCenterPayDataBean;
import com.vp.loveu.my.listener.OnPayClickListener;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年12月14日上午9:53:23
 * @功能TODO
 * @作者 mi
 */

public class MyPayActiveItem extends LinearLayout {

	private TextView mTvOrderNumber;
	private TextView mTvPayTime;
	private Button mBtPacket;
	private TextView mTvTitle;
	private TextView mTvActiveTime;
	private LinearLayout mProgressAll;
	private ImageView mBackGoundOne;
	private Context mContext;

	public MyPayActiveItem(Context context) {
		this(context, null);
	}

	public MyPayActiveItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		LayoutInflater.from(context).inflate(R.layout.my_center_player_list_item, this);
		initView();
	}

	private void initView() {
		mTvOrderNumber = (TextView) findViewById(R.id.my_center_play_tv_ordernumber);
		mTvPayTime = (TextView) findViewById(R.id.my_center_play_tv_ordertime);
		mBtPacket = (Button) findViewById(R.id.my_center_play_bt_flag);
		mTvTitle = (TextView) findViewById(R.id.index_active_tv_title);
		mTvActiveTime = (TextView) findViewById(R.id.index_active_tv_time);
		mProgressAll = (LinearLayout) findViewById(R.id.index_active_progress_all);
		mBackGoundOne = (ImageView) findViewById(R.id.index_active_iv_background_one);
		mProgressAll.setVisibility(View.GONE);
	}

	public void setData(final MyCenterPayDataBean data) {
		if (data == null) {
			return;
		}
		mTvOrderNumber.setText(data.order_no);
		mTvPayTime.setText(MyUtils.dateFromLong(data.create_time));
		try {
			String json_cont = data.json_cont;
			JSONObject obj = new JSONObject(json_cont);
			String optString = obj.optString("end_time");
			long endTime = Long.valueOf(optString);//结束时间
			long currentTime = System.currentTimeMillis() / 1000;
			if (currentTime > endTime || data.status == 90) {//活动时间超时 或者 二维码被扫描 都算是失效
				mBtPacket.setText("已失效");
				mBtPacket.setTextColor(Color.parseColor("#ffffff"));
				mBtPacket.setBackgroundResource(R.drawable.commit_fell_shape);
				mBtPacket.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (listener!=null) {
							listener.failed(v, data);
						}
					}
				});
			} else{
				mBtPacket.setText("现场签到");
				mBtPacket.setTextColor(Color.parseColor("#ffffff"));
				mBtPacket.setBackgroundResource(R.drawable.commit_fell_shape_become);
				mBtPacket.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (listener!=null) {
							listener.success(v, data);
						}
					}
				});
			}
			
			
		} catch (Exception e) {
		}
		
		
		
		
		mTvTitle.setText(data.product_name);
		if (data.pics != null && data.pics.size() > 0) {
			ImageLoader.getInstance().displayImage(data.pics.get(0), mBackGoundOne, DisplayOptionsUtils.getOptionsConfig());
		}

		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), ActiveWebActivity.class);
				intent.putExtra(ActiveWebActivity.KEY_WEB, data.product_id);
				getContext().startActivity(intent);
			}
		});

		String json = data.json_cont;
		if (!TextUtils.isEmpty(json)) {
			try {
				JSONObject obj = new JSONObject(json);
				String beginTime = obj.getString("begin_time");
				String endTime = obj.getString("end_time");
				mTvActiveTime.setText("活动时间 :" + MyUtils.dateFromLong(beginTime)); // 活动开始时间
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private OnPayClickListener listener;
	public void setOnPayClickListener(OnPayClickListener listener){
		if (listener!=null) {
			this.listener = listener;
		}
	}
}
