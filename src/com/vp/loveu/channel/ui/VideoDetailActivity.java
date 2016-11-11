package com.vp.loveu.channel.ui;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.channel.bean.RewardsBean;
import com.vp.loveu.channel.bean.VideoDetailBean;
import com.vp.loveu.channel.bean.VideoDetailBean.Video;
import com.vp.loveu.channel.dialog.RewardsDialog;
import com.vp.loveu.channel.widget.VideoDirItemView;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.my.bean.NewIntergralBean.NewIntergralDataBean;
import com.vp.loveu.pay.bean.EnjoyPayBean.EnjoyPayType;
import com.vp.loveu.pay.ui.EnjoyPayActivity;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.widget.ZanAllHeadView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;

/**
 * @author：pzj
 * @date: 2015年11月24日 下午3:22:36
 * @Description: 新手视频详情页面
 */
public class VideoDetailActivity extends VpActivity implements OnClickListener {
	public static final String VIDEO_ID = "id";
	public static final String VIDEO_V_ID = "vid";
	private int id;
	private int vid;
	private VideoDetailBean mVideoBean;
	private NewIntergralDataBean bean;
	private ZanAllHeadView mUserView;
	private ImageView mIvPic;
	private TextView mUserLearnNum;
	private TextView mVideoTotalVideoCount;
	private TextView mVideoDegree;
	private TextView mTotalUserLearnNum;
	private DisplayImageOptions options;
	private int mCurrentVideoIndex = 0;
	private LinearLayout mOtherVideoContainer;
	private LinearLayout mUsersContainer;
	private ScrollView mScrollView;
	private TextView mBtnRewards;// 打赏
	RewardsBean enjoyPayBean = new RewardsBean();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channel_video_detail_activity);
		this.id = getIntent().getIntExtra(VIDEO_ID, -1);
		this.vid = getIntent().getIntExtra(VIDEO_V_ID, 0);
		this.bean = (NewIntergralDataBean) getIntent().getSerializableExtra("obj");
		this.mClient = new VpHttpClient(this);
		initView();
		initDatas(this.vid);
	}

	private void initView() {
		initPublicTitle();
		this.mPubTitleView.mBtnLeft.setText("");
		this.mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		this.mPubTitleView.mTvTitle.setText("");
		this.mUserView = (ZanAllHeadView) findViewById(R.id.channel_users);
		this.mIvPic = (ImageView) findViewById(R.id.channel_video_iv_pic);
		this.mUserLearnNum = (TextView) findViewById(R.id.channel_video_learn_num);
		this.mVideoTotalVideoCount = (TextView) findViewById(R.id.channel_tv_video_count);
		this.mVideoDegree = (TextView) findViewById(R.id.channel_video_degree);
		this.mOtherVideoContainer = (LinearLayout) findViewById(R.id.channel_video_others_container);
		this.mUsersContainer = (LinearLayout) findViewById(R.id.channel_video_user_container);
		this.mTotalUserLearnNum = (TextView) findViewById(R.id.channel_video_total_users);
		mScrollView = (ScrollView) findViewById(R.id.channel_video_sl_root);
		mBtnRewards = (TextView) findViewById(R.id.reward_pay);
		mScrollView.setVerticalScrollBarEnabled(false);
		this.mIvPic.setOnClickListener(this);
		mBtnRewards.setOnClickListener(this);
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.color.frenchgrey) // resource
																							// or
				.showImageForEmptyUri(R.drawable.ic_launcher) // resource or
				.showImageOnFail(R.drawable.ic_launcher) // resource or
				.resetViewBeforeLoading(false) // default
				.cacheInMemory(true) // default
				.cacheOnDisk(true) // default
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(false) // default
				.displayer(new SimpleBitmapDisplayer()).build();

	}

	private void initDatas(int vid) {
		String url = VpConstants.CHANNEL_VIDEO_DETAIL;
		RequestParams params = new RequestParams();
		params.put("id", this.id);
		params.put("vid", vid);
		params.put("uid", LoginStatus.getLoginInfo().getUid());
		mClient.get(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString(VpConstants.HttpKey.CODE);
					if ("0".equals(code)) {// 返回成功
						JSONObject jsonData = json.getJSONObject(VpConstants.HttpKey.DATA);
						mVideoBean = VideoDetailBean.parseJson(jsonData.toString());
						setViewData();
					} else {
						String message = json.getString(VpConstants.HttpKey.MSG);
						Toast.makeText(VideoDetailActivity.this, message, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(VideoDetailActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();

			}
		});

	}

	protected void setViewData() {
		if (mVideoBean != null) {

			JSONObject obj = new JSONObject();
			try {
				obj.put("username", mVideoBean.getNickname());
				obj.put("user_avatar", mVideoBean.getPortrait());
				obj.put("to_uid", mVideoBean.getUid());
				obj.put("type", EnjoyPayType.new_vedio.getValue());
				obj.put("remark", mVideoBean.getName());
				obj.put("src_id", mVideoBean.getId());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			enjoyPayBean.setObj(obj.toString());

			int reward_able = mVideoBean.getReward_able();
			// 是否可打赏，1=可以，0=不可以
			mBtnRewards.setVisibility(reward_able == 1 ? View.VISIBLE : View.GONE);

			this.mPubTitleView.mTvTitle.setText(mVideoBean.getName());
			ImageLoader.getInstance().displayImage(mVideoBean.getPic(), this.mIvPic, options);
			this.mVideoTotalVideoCount.setText("目录(共" + mVideoBean.getVideo_num() + "节)");
			if (mVideoBean.getVideos() != null && mVideoBean.getVideos().size() > 0) {
				for (int i = 0; i < mVideoBean.getVideos().size(); i++) {
					if (mVideoBean.getVideos().get(i).getId() == this.vid) {
						this.mCurrentVideoIndex = i;
					}
				}
				// 当前视频小节
				setTopView();
			}
			// 其他章节信息
			if (mVideoBean.getVideos() != null && mVideoBean.getVideos().size() > 0) {
				for (int i = 0; i < mVideoBean.getVideos().size(); i++) {
					Video video = mVideoBean.getVideos().get(i);
					VideoDirItemView dv = new VideoDirItemView(this);
					dv.setViewStatus(mVideoBean.getVideos().size() - i, video.getName(), video.getIs_learned() == 1);
					video.videoId = this.id;
					video.vid = this.vid;
					video.share_exp = mVideoBean.getShare_exp();
					video.bean = bean;
					dv.setVideoData(video);
					LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					this.mOtherVideoContainer.addView(dv, params);
					createItemViewListener(dv, i);
				}
			}

			// 显示用户信息
			if (mVideoBean.getUsers() == null || mVideoBean.getUsers().size() == 0) {
				this.mUsersContainer.setVisibility(View.GONE);
			} else {
				mUserView.setDatas(mVideoBean.getUsers());
				String learnNumStr = " 人在学";
				int learnNum = mVideoBean.getLearn_num();
				SpannableStringBuilder style = new SpannableStringBuilder(learnNum + learnNumStr);
				style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.sub_textView_color66)), (learnNum + "").length(),
						(learnNum + learnNumStr).length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				this.mTotalUserLearnNum.setText(style);
				this.mUsersContainer.setVisibility(View.VISIBLE);
			}

		}
	}

	/**
	 * 设置当前目录的下选择了的章节
	 */
	private void setTopView() {
		Video video = mVideoBean.getVideos().get(mCurrentVideoIndex);
		if (video != null) {
			// ImageLoader.getInstance().displayImage(video.getPic(),
			// this.mIvPic, options);

			// this.mPubTitleView.mTvTitle.setText(video.getName());
			String learnNumStr = "学习人数:";
			int learnNum = video.getLearn_num();
			SpannableStringBuilder style = new SpannableStringBuilder(learnNumStr + learnNum);
			style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.sub_textView_color99)), 0, learnNumStr.length(),
					Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			this.mUserLearnNum.setText(style);

			String degressStr = "难度:";
			int degress = video.getDegree();
			style = new SpannableStringBuilder(degressStr + getDegree(degress));
			style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.sub_textView_color99)), 0, degressStr.length(),
					Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			this.mVideoDegree.setText(style);

		}

	}

	private String getDegree(int degree) {
		String val = "";
		switch (degree) {
		case 1:
			val = "初级";
			break;
		case 2:
			val = "中级";
			break;
		case 3:
			val = "高级";
			break;

		default:
			break;
		}
		return val;
	}

	private void createItemViewListener(VideoDirItemView dv, final int position) {
		dv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mCurrentVideoIndex = position;
				setTopView();
				// Intent intent = new Intent(VideoDetailActivity.this,
				// VideoViewActivity.class);
				// Video video = new VideoDetailBean().new Video();
				// video.setUrl("http://7xr1ib.media1.z0.glb.clouddn.com/TVIDEO0001.MP4");
				// video.setUrl("http://7xr1ib.media1.z0.glb.clouddn.com/TV1.mp4");
				// //
				// video.setUrl("http://7xr1ib.media1.z0.glb.clouddn.com/TV2.mp4");
				// video.setTimestamp(ResultParseUtil.timeinterval);
				// intent.putExtra(VideoPlayActivity.URL, video);
				// startActivity(intent);

				
				
				// 跳转视频播放界面
				Video video = mVideoBean.getVideos().get(mCurrentVideoIndex);
				if (video != null) {
					String url = video.getUrl();
					if (!TextUtils.isEmpty(url) && (url.endsWith(".MP4") || url.endsWith(".mp4"))) {
						// 根据视频链接判断跳转具体的界面
						Intent intent = new Intent(VideoDetailActivity.this, VideoViewActivity.class);
						video.setTimestamp(ResultParseUtil.timeinterval);
						intent.putExtra(VideoPlayActivity.URL, video);
						startActivity(intent);
					} else {
						Intent intents = new Intent(VideoDetailActivity.this, VideoPlayActivity.class);
						intents.putExtra(VideoPlayActivity.URL, video.getUrl());
						startActivity(intents);
					}
					if (video.getIs_learned() == 0) {
						sendMsg2Server(video.getId(), video.getName());
					}
				}
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.channel_video_iv_pic:
			// Video video = mVideoBean.getVideos().get(mCurrentVideoIndex);
			// if(video!=null){
			// if(video.getIs_learned()==0)
			// sendMsg2Server(video.getId(),video.getName());
			// Intent intent=new
			// Intent(VideoDetailActivity.this,VideoPlayActivity.class);
			// intent.putExtra(VideoPlayActivity.URL, video.getUrl());
			// startActivity(intent);
			// }
			break;
		case R.id.reward_pay:
			rewards();
			break;

		default:
			break;
		}
	}

	private RewardsDialog rewardsDialog;

	// 去打赏
	private void rewards() {
		// if (rewardsDialog == null) {
		// rewardsDialog = new RewardsDialog(this);
		// }
		// rewardsDialog.show();

		Intent intent = new Intent(this, EnjoyPayActivity.class);
		intent.putExtra(EnjoyPayActivity.PAY_PARAMS, enjoyPayBean);
		startActivity(intent);
	}

	/**
	 * 上报视频学习
	 */
	private void sendMsg2Server(int vid, final String name) {
		String url = VpConstants.CHANNEL_VIDEO_LEARN;
		mClient.setShowProgressDialog(false);
		JSONObject body = new JSONObject();
		try {
			body.put("uid", LoginStatus.getLoginInfo().getUid());
			body.put("id", this.id);
			body.put("vid", vid);

		} catch (Exception e) {
			Toast.makeText(VideoDetailActivity.this, "请求参数有误", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			return;
		}
		this.mClient.post(url, new RequestParams(), body.toString(), false, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);
				JSONObject json = null;
				try {
					json = new JSONObject(result);
					String code = json.getString(VpConstants.HttpKey.CODE);
					if ("0".equals(code)) {// 返回成功
						mVideoBean.getVideos().get(mCurrentVideoIndex).setIs_learned(1);
						VideoDirItemView itemView = (VideoDirItemView) mOtherVideoContainer.getChildAt(mCurrentVideoIndex);
						itemView.setViewStatus(mCurrentVideoIndex + 1, name, true);
					} else {
						String message = json.getString(VpConstants.HttpKey.MSG);
						Toast.makeText(VideoDetailActivity.this, message, Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				;
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(VideoDetailActivity.this, "网络访问错误", Toast.LENGTH_SHORT).show();
			}
		});

	}
}
