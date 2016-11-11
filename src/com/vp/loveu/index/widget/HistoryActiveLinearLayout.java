package com.vp.loveu.index.widget;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vp.loveu.R;
import com.vp.loveu.index.activity.ActiveWebActivity;
import com.vp.loveu.index.bean.CityActiveBean.ActBean;
import com.vp.loveu.index.myutils.DisplayOptionsUtils;
import com.vp.loveu.index.myutils.MyUtils;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月18日上午10:14:30
 * @功能 历史活动列表的item
 * @作者 mi
 */

public class HistoryActiveLinearLayout extends LinearLayout implements OnClickListener {

	private LinearLayout mRight;
	private LinearLayout mLeft;
	private ImageView mIvLeft;
	private ImageView mIvRight;
	private TextView mTvLeftTitle;
	private TextView mTvRightTitle;
	private TextView mTvLeftTime;
	private TextView mTvRightTime;
	private TextView mTvTitleFlag;
	private ActBean left;
	private ActBean right;
	private String tag = "HistoryActiveLinearLayout";

	public HistoryActiveLinearLayout(Context context) {
		this(context, null);
	}

	public HistoryActiveLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		View.inflate(context, R.layout.cityactive_history_list_item, this);
		initView();
	}

	private void initView() {
		mRight = (LinearLayout) findViewById(R.id.histor_item_ly_right);
		mLeft = (LinearLayout) findViewById(R.id.histor_item_ly_left);
		mIvLeft = (ImageView) findViewById(R.id.histor_item_iv_left);
		mIvRight = (ImageView) findViewById(R.id.histor_item_iv_right);
		mTvLeftTitle = (TextView) findViewById(R.id.histor_item_tv_left_title);
		mTvRightTitle = (TextView) findViewById(R.id.histor_item_tv_right_title);
		mTvLeftTime = (TextView) findViewById(R.id.histor_item_tv_left_time);
		mTvRightTime = (TextView) findViewById(R.id.histor_item_tv_right_time);
		mTvTitleFlag = (TextView) findViewById(R.id.history_tv_flag);
	}

	public void setData(List<ActBean> data) {
		if (data == null || data.size() < 0) {
			return;
		}

		try {
			left = data.get(0);
			if (left != null) {
				ImageLoader.getInstance().displayImage(left.small_pic, mIvLeft, DisplayOptionsUtils.getOptionsConfig());
				mTvLeftTime.setText(MyUtils.dateFromLong(left.begin_time));
				mTvLeftTitle.setText(left.name);
				mLeft.setOnClickListener(this);
			}
			right = data.get(1);
			if (right != null) {
				mRight.setOnClickListener(this);
				ImageLoader.getInstance().displayImage(right.small_pic, mIvRight, DisplayOptionsUtils.getOptionsConfig());
				mTvRightTime.setText(MyUtils.dateFromLong(right.begin_time));
				mTvRightTitle.setText(right.name);
				mRight.setVisibility(View.VISIBLE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		if (v == mLeft) {
			// 跳转到活动详情界面
			if (left != null) {
				startIntent(left.id);
			}
		} else if (v == mRight) {
			if (right != null) {
				startIntent(right.id);
			}
		}
	}

	private void startIntent(int id) {
		Intent intent = new Intent(getContext(), ActiveWebActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(ActiveWebActivity.KEY_WEB, id);
		getContext().startActivity(intent);
	}
	
	public void setTvTitleFlagIsShow(boolean isShow){
		mTvTitleFlag.setVisibility(isShow ? View.VISIBLE : View.GONE);
	}
}
