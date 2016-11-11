package com.vp.loveu.index.holder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import java.util.ArrayList;

import com.vp.loveu.R;
import com.vp.loveu.channel.widget.TopicPicContainer;
import com.vp.loveu.comm.ShowImagesViewPagerActivity;
import com.vp.loveu.index.activity.FreeHelpActivity;
import com.vp.loveu.index.bean.FreeHelpBean;
import com.vp.loveu.message.utils.WxUtil;
import com.vp.loveu.util.UIUtils;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月20日下午3:56:17
 * @功能TODO
 * @作者 mi
 */

public class FreeHelpTopHolder extends BaseHolder<FreeHelpBean> {

	private String tag = "FreeHelpTopHolder";
	private ImageView mFreeIvProgress;
	private TextView mFreeTvNumber;
	private RotateAnimation anima;
	private ScaleAnimation scale;
	private TextView mTvNickName;
	private TextView mTvContent;
	private Button mBtPacketFlag;
	private TextView mTvCount;
	private LinearLayout mLyIvContainer;
	private LinearLayout mIconContainer;
	private FreeHelpActivity mActivity;
	private int width = 103;
	private int height = 63;
	private int margin = 10;
	
	public FreeHelpTopHolder(Context context){
		super(context);
		mActivity = (FreeHelpActivity)context;
	}

	@Override
	protected View initView() {
		return View.inflate(mContext, R.layout.free_help_top_layout, null);
	}
	
	@Override
	protected void findView() {
		mFreeIvProgress = (ImageView) mRootView.findViewById(R.id.free_help_top_iv_progress);
		mFreeTvNumber = (TextView) mRootView.findViewById(R.id.free_help_top_tv_number);
		mTvNickName = (TextView) mRootView.findViewById(R.id.public_fell_help_tv_nickname);
		mTvContent = (TextView) mRootView.findViewById(R.id.public_fell_help_tv_content);
		mBtPacketFlag = (Button) mRootView.findViewById(R.id.public_fell_help_bt_packet_flag);
		mLyIvContainer = (LinearLayout) mRootView.findViewById(R.id.public_fell_help_ly_iv_container);
		mIconContainer = (LinearLayout) mRootView.findViewById(R.id.public_fell_help_logoname_container);
		mTvCount = (TextView) mRootView.findViewById(R.id.free_help_top_tv_count);
		mIconContainer.setVisibility(View.GONE);
		mRootView.findViewById(R.id.public_fell_help_bt_packet_flag).setVisibility(View.GONE);
		startAnimation();// 做动画
	}

	@Override
	protected void initData(final FreeHelpBean data) {
		mTvContent.setText(data.content);
		if (data.photoList == null || data.photoList.size()<=0) {
			mLyIvContainer.setVisibility(View.GONE);//如果没有图片则不显示
		}else{
			mLyIvContainer.post(new Runnable() {
				@Override
				public void run() {
					TopicPicContainer ivContainer = new TopicPicContainer(mContext);
					mLyIvContainer.addView(ivContainer);
					ivContainer.setDatas((ArrayList<String>) data.photoList,mLyIvContainer.getWidth());
				}
			});
			
		}
		new MyAsyncTask().execute();
	}

	private void startAnimation() {
		anima = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		anima.setDuration(1000);
		anima.setRepeatCount(Animation.INFINITE);
		mFreeIvProgress.startAnimation(anima);
		/*scale = new ScaleAnimation(0.5f, 1.5f, 0.5f, 1.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
		scale.setInterpolator(new AccelerateDecelerateInterpolator());
		scale.setDuration(1000);
		scale.setRepeatCount(Animation.INFINITE);
		mFreeTvNumber.startAnimation(scale);*/
	}

	public void stopAnimation() {
		if (anima != null) {
			anima.cancel();
		}
		if (scale != null) {
			scale.cancel();
		}
	}

	private void browsePhoto(int position){
		Intent intent = new Intent(mContext, ShowImagesViewPagerActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(ShowImagesViewPagerActivity.IMAGES, mData.photoList);
		intent.putExtra(ShowImagesViewPagerActivity.POSITION, position);
		UIUtils.getContext().startActivity(intent);
	}
	
	private class MyAsyncTask extends AsyncTask<Void, Integer, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			
			for (int i = 0; i <= mData.maxNumb; i++) { 
				 publishProgress(i);
				SystemClock.sleep(400);
			}
			return null;
		}
		@Override
		protected void onProgressUpdate(Integer... values) {
			int result = values[0];
			mTvCount.setText("已邀请"+result+"位用户");
			mFreeTvNumber.setText(result+"");
		}
	}
}
