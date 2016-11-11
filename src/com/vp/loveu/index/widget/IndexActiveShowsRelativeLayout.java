package com.vp.loveu.index.widget;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vp.loveu.R;
import com.vp.loveu.index.activity.ActiveWebActivity;
import com.vp.loveu.index.bean.ActivityDetailBean.ActivityDetailData;
import com.vp.loveu.index.bean.CityActiveBean;
import com.vp.loveu.index.bean.CityActiveBean.ActBean;
import com.vp.loveu.index.myutils.DisplayOptionsUtils;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.util.UIUtils;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月17日下午5:48:38
 * @功能 活动展示的item
 * @作者 mi
 */

public class IndexActiveShowsRelativeLayout extends RelativeLayout {

	private TextView mTvTitle;
	private TextView mTvTime;
	private ProgressBar mProgressbar;
	private TextView mTvRatio;
	private ImageView mIvAllow;
	//private View mBackGroundTwo;
	private ImageView mIvBackgoundOne;
	private RelativeLayout mRootView;

	public IndexActiveShowsRelativeLayout(Context context) {
		this(context, null);
	}

	public IndexActiveShowsRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		View.inflate(context, R.layout.index_active_item_show, this);
		initView();
	}

	private void initView() {
		mTvTitle = (TextView) findViewById(R.id.index_active_tv_title);
		mTvTime = (TextView) findViewById(R.id.index_active_tv_time);
		mProgressbar = (ProgressBar) findViewById(R.id.index_active_progressBar);
		mTvRatio = (TextView) findViewById(R.id.index_active_tv_progress);
		mIvAllow = (ImageView) findViewById(R.id.index_active_iv_icon_in);
		//mBackGroundTwo = findViewById(R.id.index_active_iv_background_two);
		mIvBackgoundOne = (ImageView) findViewById(R.id.index_active_iv_background_one);
		mRootView = (RelativeLayout) findViewById(R.id.index_active_rootview);
	}

	public void setMarginBotton(boolean yes) {
		if (!yes) {
			return;
		}
		MarginLayoutParams params = new MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT);
		params.bottomMargin = UIUtils.dp2px(10);
		mRootView.setLayoutParams(new RelativeLayout.LayoutParams(params));
	}
	
	public void setData(final ActBean data) {
		if (data == null) {
			return;
		}
		mTvTitle.setText(data.name);
		mTvTime.setText("活动时间:  " + MyUtils.dateFromLong(data.begin_time));
		int progress = (int) (data.progress + 0.5f);
		mProgressbar.setProgress(progress > 100 ? 100 : progress);
		mTvRatio.setText((int) (data.progress + 0.5f) + "%");
		ImageLoader.getInstance().displayImage(data.pic, mIvBackgoundOne, DisplayOptionsUtils.getOptionsConfig());
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(data.id);
			}
		});

	}

	public void setData(final ActivityDetailData bean) {
		if (bean == null) {
			return;
		}
		mIvAllow.setVisibility(View.GONE);
		ActBean actBean = new ActBean();
		actBean.name = bean.name;
		actBean.begin_time = bean.begin_time;
		actBean.pic = bean.pic;
		actBean.progress = bean.progress;
		actBean.id = bean.id;
		setData(actBean);
	}

	private void startActivity(int id) {
		Intent intent = new Intent(getContext(), ActiveWebActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(ActiveWebActivity.KEY_WEB, id);
		getContext().startActivity(intent);
	}
}
