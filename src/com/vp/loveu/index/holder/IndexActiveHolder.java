package com.vp.loveu.index.holder;

import java.util.List;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vp.loveu.R;
import com.vp.loveu.index.activity.ActiveWebActivity;
import com.vp.loveu.index.activity.CityActiveListActivity;
import com.vp.loveu.index.bean.IndexBean.IndexActBean;
import com.vp.loveu.index.bean.IndexBean.IndexActUserBean;
import com.vp.loveu.index.myutils.DisplayOptionsUtils;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.index.widget.IndexActiveInfosRelativeLayout;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.util.SharedPreferencesHelper;
import com.vp.loveu.util.UIUtils;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月17日上午10:59:15
 * @功能 活动模块的holder
 * @作者mi
 */

public class IndexActiveHolder extends BaseHolder<List<IndexActBean>> implements OnClickListener {

	private static final String TAG = "IndexActiveHolder";
	private static final String OPEN_ANIMATION_FLAG = "open_animation_flag";// 动画保存标记
	private ImageView mActiveIvBackground;
	private TextView mActiveTvTitle;
	private TextView mActiveTvTime;
	private ProgressBar mActiveProgress;
	private TextView mActiveTvProgress;
	private TextView mTvIconMore;
	private Button mBtnApply;
	private ImageView mActiveIvIn;
	private LinearLayout mActiveContainer;
	private TextView mAnimationNumber;
	private TextView mAnimationFlag;
	private ImageView mAnimationIvAllow;
	private RelativeLayout mAnimationToggle;
	private LinearLayout mAnimationLayoutFlag;
	private boolean isOpened;
	private int height;
	private IndexActiveInfosRelativeLayout layout = null;
	private IndexActBean mDatas;
	private MyAsyncTask myAsyncTask;
	private List<IndexActUserBean> mUserData;

	public IndexActiveHolder(Context context) {
		super(context);
	}

	@Override
	protected View initView() {
		return View.inflate(mContext, R.layout.index_public_active_show, null);
	}

	@Override
	protected void findView() {
		mActiveIvBackground = (ImageView) mRootView.findViewById(R.id.index_active_iv_background_one);
		mActiveTvTitle = (TextView) mRootView.findViewById(R.id.index_active_tv_title);
		mActiveTvTime = (TextView) mRootView.findViewById(R.id.index_active_tv_time);
		mActiveProgress = (ProgressBar) mRootView.findViewById(R.id.index_active_progressBar);
		mActiveTvProgress = (TextView) mRootView.findViewById(R.id.index_active_tv_progress);
		mActiveContainer = (LinearLayout) mRootView.findViewById(R.id.index_active_infos_container);
		mAnimationNumber = (TextView) mRootView.findViewById(R.id.index_animation_tv_number);
		mAnimationFlag = (TextView) mRootView.findViewById(R.id.index_animation_tv_togle);
		mAnimationIvAllow = (ImageView) mRootView.findViewById(R.id.index_animation_iv_flag);
		mAnimationToggle = (RelativeLayout) mRootView.findViewById(R.id.index_animation_relativelayout);
		mAnimationLayoutFlag = (LinearLayout) mRootView.findViewById(R.id.index_active_animation);
		mActiveIvIn = (ImageView) mRootView.findViewById(R.id.index_active_iv_icon_in);
		mTvIconMore = (TextView) mRootView.findViewById(R.id.index_active_icon_more);
		mBtnApply = (Button) mRootView.findViewById(R.id.index_active_btn_apply);
		mBtnApply.setOnClickListener(this);
		mActiveIvIn.setVisibility(View.GONE);
		mAnimationToggle.setOnClickListener(this);
		mTvIconMore.setOnClickListener(this);
		
	}

	/**
	 * 可见时候的回掉
	 * @param loginInfo
	 *            void TODO 同步本地头像和昵称
	 */
	public void onStart(LoginUserInfoBean loginInfo) {
		if (mUserData != null) {
			for (int i = 0; i < mUserData.size(); i++) {
				IndexActUserBean bean = mUserData.get(i);
				if (bean.uid == loginInfo.getUid()) {
					if (mActiveContainer != null) {
						IndexActiveInfosRelativeLayout item = (IndexActiveInfosRelativeLayout) mActiveContainer.getChildAt(i);
						bean.nickname = loginInfo.getNickname();
						bean.portrait = loginInfo.getPortrait();
						item.setData(bean);
					}
				}
			}
		}
	}

	@Override
	protected void initData(List<IndexActBean> data) {

		mDatas = data.get(0);// 暂时展示一个活动
		mActiveTvTitle.setText(mDatas.name);// 活动名称
		mAnimationNumber.setText(mDatas.remaining_num + "");// 剩余名额
		mActiveTvTime.setText("活动时间:  " + MyUtils.dateFromLong(mDatas.begin_time));// 活动举办时间
		ImageLoader.getInstance().displayImage(mDatas.pic, mActiveIvBackground, DisplayOptionsUtils.getOptionsConfig());
		mActiveIvBackground.setOnClickListener(this);

		// 报名用户 userInfo
		List<IndexActUserBean> userData = mUserData = mDatas.users;
		if (userData != null && userData.size() > 0) {
			for (int i = 0; i < userData.size(); i++) {
				IndexActUserBean bean = userData.get(i);
				layout = new IndexActiveInfosRelativeLayout(mContext);
				layout.setData(bean);
				mActiveContainer.addView(layout);
			}
		}

		mAnimationLayoutFlag.measure(0, 0);
		height = mAnimationLayoutFlag.getMeasuredHeight();

		isOpened = SharedPreferencesHelper.getInstance(UIUtils.getContext()).getBooleanValue(OPEN_ANIMATION_FLAG);
		if (!isOpened) {
			// 如果打开就直接关闭
			opendNoAnimation();
		} else {
			colseNoAnimation();
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.index_animation_relativelayout:
			// 关闭就打开，打开就关闭
			if (!isOpened) {
				closeAnimation();
				ObjectAnimator.ofFloat(mAnimationIvAllow, "rotation", 0, 180).setDuration(500).start();
			} else {
				openAnimation();
				ObjectAnimator.ofFloat(mAnimationIvAllow, "rotation", 180, 360).setDuration(500).start();
			}
			isOpened = !isOpened;
			// 保存状态
			SharedPreferencesHelper.getInstance(UIUtils.getContext()).putBooleanValue(OPEN_ANIMATION_FLAG, isOpened);
			break;
		case R.id.index_active_iv_background_one:
		case R.id.index_active_btn_apply:
			Intent activeInfo = new Intent(mContext, ActiveWebActivity.class);
			activeInfo.putExtra(ActiveWebActivity.KEY_WEB, mDatas.id);
			mContext.startActivity(activeInfo);
			break;

		case R.id.index_active_icon_more:
			// 查看更多
			// Intent more = new Intent(mContext, CityActiveActivity.class);
			// more.putExtra(CityActiveActivity.KEY_ISSHOWMORE, false);
			// mContext.startActivity(more);
			Intent more = new Intent(mContext, CityActiveListActivity.class);
			more.putExtra(CityActiveListActivity.KEY, false);
			mContext.startActivity(more);
			break;
		default:
			break;
		}
	}

	/** 不带动画的关闭 */
	private void colseNoAnimation() {
		LayoutParams params = mAnimationLayoutFlag.getLayoutParams();
		params.height = 0;
		mAnimationLayoutFlag.requestLayout();
		mAnimationFlag.setText("展开");
		ObjectAnimator.ofFloat(mAnimationIvAllow, "rotation", 0, 180).setDuration(10).start();
	}

	/** 不带动画的打开 */
	private void opendNoAnimation() {
		LayoutParams params = mAnimationLayoutFlag.getLayoutParams();
		params.height = height;
		mAnimationLayoutFlag.requestLayout();
		mAnimationFlag.setText("收起");
		ObjectAnimator.ofFloat(mAnimationIvAllow, "rotation", 180, 360).setDuration(10).start();
	}

	private void openAnimation() {
		ValueAnimator animator = ValueAnimator.ofInt(0, height);
		animator.setDuration(800);
		animator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int height = (Integer) animation.getAnimatedValue();
				LayoutParams params = mAnimationLayoutFlag.getLayoutParams();
				params.height = height;
				mAnimationLayoutFlag.setLayoutParams(params);
			}
		});
		animator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				mAnimationToggle.setEnabled(false);
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				mAnimationToggle.setEnabled(false);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				mAnimationToggle.setEnabled(true);
				mAnimationFlag.setText("收起");
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}
		});
		animator.start();
	}

	private void closeAnimation() {
		ValueAnimator animator = ValueAnimator.ofInt(height, 0);
		animator.setDuration(800);
		animator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int height = (Integer) animation.getAnimatedValue();
				LayoutParams params = mAnimationLayoutFlag.getLayoutParams();
				params.height = height;
				mAnimationLayoutFlag.setLayoutParams(params);
			}
		});
		animator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				mAnimationToggle.setEnabled(false);
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				mAnimationToggle.setEnabled(false);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				mAnimationToggle.setEnabled(true);
				mAnimationFlag.setText("展开");
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}
		});
		animator.start();
	}

	public void startProgress() {
		//stopProgress();
		//myAsyncTask = new MyAsyncTask();
		//myAsyncTask.executeOnExecutor(Executors.newFixedThreadPool(2));
		mHandler.removeMessages(PROGRESS_WHAT);
		Message sendMessage = new Message();
		sendMessage.what = PROGRESS_WHAT;
		sendMessage.obj = 0;
		mHandler.sendMessageDelayed(sendMessage,PROGRESS_WHAT);
	}
	
	//handler
	public static final int PROGRESS_WHAT =156;
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case PROGRESS_WHAT:
				int max = (int) (mDatas.progress + 0.5);
				int progress = (int) msg.obj;
				try {
					mActiveProgress.setProgress(progress);// 进度
					mActiveTvProgress.setText(progress + "%");// 文本进度
					if (progress >=max) {
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				Message sendMessage = new Message();
				sendMessage.what = PROGRESS_WHAT;
				sendMessage.obj = ++progress;
				mHandler.sendMessageDelayed(sendMessage,100);
				break;
			default:
				break;
			}
			
		};
	};

	public void stopProgress() {
		/*if (myAsyncTask != null && !myAsyncTask.isCancelled() && myAsyncTask.getStatus() != Status.FINISHED) {
			myAsyncTask.cancel(true);
			myAsyncTask = null;
		}*/
		mHandler.removeMessages(PROGRESS_WHAT);
	}

	private class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
		@Override
		protected void onPreExecute() {
			//SystemClock.sleep(500);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (isCancelled()) {
				return null;
			}
			int max = (int) (mDatas.progress + 0.5);
			for (int i = 0; i <= max; i++) {
				publishProgress(i);
				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			Integer progress = values[0];
			mActiveProgress.setProgress(progress);// 进度
			mActiveTvProgress.setText(progress + "%");// 文本进度
		}
	}
	
}
