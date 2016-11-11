package com.vp.loveu.message.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.channel.utils.ToastUtils;
import com.vp.loveu.channel.widget.TopicPicContainer;
import com.vp.loveu.comm.ShowImagesViewPagerActivity;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.myutils.MediaPlayerRecordUtils;
import com.vp.loveu.index.myutils.MediaPlayerRecordUtils.OnCompletionListenerCallBack;
import com.vp.loveu.index.myutils.MediaRecorderUtils;
import com.vp.loveu.message.bean.ReplyFellHelpBean;
import com.vp.loveu.message.bean.ReplyFellHelpBean.Audio;
import com.vp.loveu.message.bean.ReplyFellHelpBean.Reply;
import com.vp.loveu.message.utils.SendTopicUtils;
import com.vp.loveu.message.utils.SendTopicUtils.SendTopCallback;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.ScreenUtils;
import com.vp.loveu.util.VpDateUtils;
import com.vp.loveu.widget.CircleImageView;
import com.vp.loveu.widget.IOSActionSheetDialog;
import com.vp.loveu.widget.IOSActionSheetDialog.OnSheetItemClickListener;
import com.vp.loveu.widget.IOSActionSheetDialog.SheetItemColor;

import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月26日上午9:36:17
 * @功能 邀请情感解答 -- 红包界面
 * @作者 mi
 */

public class ReplyFellHelpActivity extends VpActivity implements OnRefreshListener<ListView> {

	private String TAG = "ReplyFellHelpActivity";
	public static final int RESULT_CODE_REPLAY = 0;
	public static final int VIEW_TYPE_NORMAL = 0;
	public static final int VIEW_TYPE_TITLE = 1;

	private int progress = -1;
	private boolean isRecording;
	private PullToRefreshListView mPullListView;
	private ListView mListview;
	private Vibrator mVibrator;
	private int leastTime = 10;// 最小录制的时间
	private int maxTime = 60;// 最大录制的时间
	private MediaRecorderUtils mediaRecorder = new MediaRecorderUtils();// 录音
	private MediaPlayerRecordUtils mediaPlay = new MediaPlayerRecordUtils();// 播放
	private MyAdapter mAdapter;
	public static final int STATYUS_TYPE0 = 0;// 待回复
	public static final int STATYUS_TYPE2 = 2;// 已回复
	private static final int mLimit = 10;
	private int mPage = 1;// 分页页码
	private ArrayList<ReplyFellHelpBean> mDatas = new ArrayList<ReplyFellHelpBean>();
	private int mUnAnswererCount;
	private DisplayImageOptions options;
	private boolean isAddTitleView;
	private int mCurrentClickPosition;
	private ViewHolder mCurrentClickHolder;
	private ScaleAnimation scaleAnimation;
	private TextView mTvEmptyView;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (progress >= maxTime) {
					Toast.makeText(getApplicationContext(), "录音时间过长", Toast.LENGTH_SHORT).show();
					stopRecord();
					isRecording = !isRecording;
					break;
				}
				mCurrentClickHolder.btnRecored.setText(++progress + "''");
				handler.sendEmptyMessageDelayed(0, 1000);
				break;

			default:
				break;
			}
		};
	};
	private boolean addTestData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_replyfellhelp_activity);
		mTvEmptyView = (TextView) findViewById(R.id.public_empty_view);
		mClient = new VpHttpClient(this);
		ScreenUtils.initScreen(this);
		mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_portrait) // resource
																									// or
				.showImageForEmptyUri(R.drawable.default_portrait) // resource
																	// or
				.showImageOnFail(R.drawable.default_portrait) // resource or
				.resetViewBeforeLoading(false) // default
				.cacheInMemory(true) // default
				.cacheOnDisk(true) // default
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(false) // default
				.displayer(new SimpleBitmapDisplayer()).build();
		initView();
		initData(false);

	}

	private void initData(final boolean isLoadMore) {

		String url = VpConstants.MESSAGE_USER_HELP_HISTORY;
		RequestParams params = new RequestParams();
		params.put("limit", this.mLimit);
		params.put("page", this.mPage);
		params.put("id", LoginStatus.getLoginInfo().getUid());

		mClient.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				mPullListView.onRefreshComplete();
				String result = ResultParseUtil.deAesResult(responseBody);
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString(VpConstants.HttpKey.CODE);
					if ("0".equals(code)) {// 返回成功
						JSONArray jsonData = json.getJSONArray(VpConstants.HttpKey.DATA);
						List<ReplyFellHelpBean> currentList = ReplyFellHelpBean.createFromJsonArray(jsonData.toString());
						if (currentList != null && currentList.size() > 0) {
							mDatas.addAll(currentList);
							if (!isAddTitleView)
								calculateUnAnsweererCount();
							mAdapter.notifyDataSetChanged();
							mPage++;

						} else {
							if (isLoadMore)
								ToastUtils.showTextToast(ReplyFellHelpActivity.this, "没有更多数据了");
						}

						if (mDatas != null && mDatas.size() > 0) {
							mPullListView.setVisibility(View.VISIBLE);
							mTvEmptyView.setVisibility(View.GONE);
						} else {
							mPullListView.setVisibility(View.GONE);
							mTvEmptyView.setVisibility(View.VISIBLE);
						}
					} else {
						String message = json.getString(VpConstants.HttpKey.MSG);
						Toast.makeText(ReplyFellHelpActivity.this, message, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				mPullListView.onRefreshComplete();
				Toast.makeText(ReplyFellHelpActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();

			}
		});

	}

	private void initView() {
		initPublicTitle();
		mPubTitleView.mTvTitle.setText("邀请情感解答");
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPullListView = (PullToRefreshListView) findViewById(R.id.reply_listview);
		mListview = mPullListView.getRefreshableView();
		mPullListView.setMode(Mode.PULL_FROM_END);// 向上拉刷新
		mAdapter = new MyAdapter();
		mListview.setAdapter(mAdapter);
		mPullListView.setOnRefreshListener(this);
	}

	/**
	 * 计算未解答数量
	 */
	private void calculateUnAnsweererCount() {
		// TODO 构造数据，便于测试
		// if(!addTestData){
		// addTestData=true;
		// ReplyFellHelpBean bean1=new ReplyFellHelpBean();
		// Audio dio=new ReplyFellHelpBean().new Audio();
		// dio.setTitle("18s");
		// dio.setUrl("http://");
		// ArrayList<Audio> arrayList = new ArrayList<Audio>();
		// arrayList.add(dio);
		// bean1.setAudios(arrayList);
		// bean1.setCont("静静顾盼，深深眷恋，清欢光阴里的点滴，聆听燕蝶 驻足时的清韵，羽翼眉底下的温存，静静顾盼，深深
		// 眷恋，清欢光阴里的点滴，聆听燕蝶...");
		// bean1.setCreate_time("1447665416");
		// bean1.setId(300);
		// bean1.setNickname("mary");
		// ArrayList<String> pics=new ArrayList<>();
		// pics.add("http://img2.3lian.com/2014/f5/158/d/86.jpg");
		// pics.add("http://img1.imgtn.bdimg.com/it/u=2260624360,613454631&fm=21&gp=0.jpg");
		// pics.add("http://images.99pet.com/InfoImages/wm600_450/1d770941f8d44c6e85ba4c0eb736ef69.jpg");
		// bean1.setPics(pics);
		// bean1.setPortrait("http://img.popoho.com/allimg/121204/2153155302-10.jpg");
		// bean1.setPrice(10);
		// bean1.setStatus(0);
		// bean1.setType(1);
		// bean1.setUid(18);
		//
		// ArrayList<Reply> replys1=new ArrayList<Reply>();
		// Reply r=new ReplyFellHelpBean().new Reply();
		// r.setStatus(0);
		// replys1.add(r);
		// bean1.setReplys(replys1);
		//
		// ReplyFellHelpBean bean2=new ReplyFellHelpBean();
		// dio.setTitle("18s");
		// dio.setUrl("http://");
		// arrayList.add(dio);
		// bean2.setAudios(arrayList);
		// bean2.setCont("静静顾盼，深深眷恋，清欢光阴里的点滴，聆听燕蝶 驻足时的清韵，羽翼眉底下的温存，静静顾盼，深深
		// 眷恋，清欢光阴里的点滴，聆听燕蝶...");
		// bean2.setCreate_time("1447665416");
		// bean2.setId(301);
		// bean2.setNickname("tom");
		// bean2.setPics(pics);
		// bean2.setPortrait("http://img.popoho.com/allimg/121204/2153155302-10.jpg");
		// bean2.setPrice(20);
		// bean2.setStatus(0);
		// bean2.setType(1);
		// bean2.setUid(18);
		//
		// ArrayList<Reply> replys2=new ArrayList<Reply>();
		// Reply r2=new ReplyFellHelpBean().new Reply();
		// r2.setStatus(0);
		// replys2.add(r2);
		// bean2.setReplys(replys2);
		//
		// mDatas.add(0,bean1);
		// mDatas.add(0,bean2);
		// }

		int answereredCount = 0;
		mUnAnswererCount = 0;
		if (mDatas != null && mDatas.size() > 0) {
			for (ReplyFellHelpBean bean : mDatas) {
				ArrayList<Reply> replys = bean.getReplys();
				if (replys != null && replys.size() > 0) {
					if (replys.get(0).getStatus() == STATYUS_TYPE0)// 0=待回复，1=已回复2=已拒绝
						mUnAnswererCount++;
					if (replys.get(0).getStatus() == STATYUS_TYPE2) {
						answereredCount++;
					}
				}
			}
			if (answereredCount > 0) {
				ReplyFellHelpBean titleBean = new ReplyFellHelpBean();
				titleBean.setViewType(VIEW_TYPE_TITLE);
				mDatas.add(mUnAnswererCount, titleBean);
				this.isAddTitleView = true;
			}
		}
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mDatas != null && mDatas.size() > 0)
				return mDatas.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			return mDatas.get(position).getViewType();
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (getItemViewType(position) == VIEW_TYPE_TITLE) {// 以往回复title
				convertView = View.inflate(ReplyFellHelpActivity.this, R.layout.default_sub_title_item_view, null);
				((TextView) convertView.findViewById(R.id.channel_item_tv_name)).setText("以往解答");
			} else {
				ReplyFellHelpBean helpBean = mDatas.get(position);

				ViewHolder holder = null;
				if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
					holder = new ViewHolder();
					convertView = View.inflate(getApplicationContext(), R.layout.reply_listview_head, null);
					holder.tvTime = (TextView) convertView.findViewById(R.id.message_help_time);
					holder.tvReject = (TextView) convertView.findViewById(R.id.message_help_reject_tv_answerer);
					holder.tvNickName = (TextView) convertView.findViewById(R.id.message_help_nickname);
					holder.tvCont = (TextView) convertView.findViewById(R.id.message_help_cont);
					holder.tvRecordTime = (TextView) convertView.findViewById(R.id.reply_tv_record_time);
					holder.btnPackage = (Button) convertView.findViewById(R.id.message_help_package_price);
					holder.btnSend = (Button) convertView.findViewById(R.id.reply_bt_send);
					holder.btnReSend = (Button) convertView.findViewById(R.id.reply_bt_resend);
					holder.btnAnswerer = (Button) convertView.findViewById(R.id.message_help_bt_answerer);
					holder.btnReject = (Button) convertView.findViewById(R.id.message_help_bt_reject);
					holder.btnRecored = (TextView) convertView.findViewById(R.id.reply_bt_record);
					holder.btnRepeatRecored = (Button) convertView.findViewById(R.id.reply_bt_reset);
					holder.ivPortrait = (CircleImageView) convertView.findViewById(R.id.message_help_portrait);
					holder.ivPlay = (ImageView) convertView.findViewById(R.id.reply_iv_playing);
					holder.ivPlayPoint = (ImageView) convertView.findViewById(R.id.reply_tv_record_cont_playering);
					holder.flStartAnswererContainer = (FrameLayout) convertView.findViewById(R.id.reply_fm);
					holder.llEndAnswererContainer = (LinearLayout) convertView.findViewById(R.id.reply_ly_bottom);
					holder.llPicContainer = (TopicPicContainer) convertView.findViewById(R.id.message_help_pic_ll_container);
					holder.llBeforeContainer = (RelativeLayout) convertView.findViewById(R.id.message_before_help_container);
					holder.rlOperatorContainer = (RelativeLayout) convertView.findViewById(R.id.message_help_operator_container);

					holder.tvBeforeCont = (TextView) convertView.findViewById(R.id.message_before_help_cont);
					holder.tvBeforeContRadio = (TextView) convertView.findViewById(R.id.message_before_help_cont_radio);
					holder.tvBeforeIvPlayPoint = (ImageView) convertView.findViewById(R.id.message_before_help_cont_playering);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}

				holder.tvTime.setText(VpDateUtils.getStandardDate(helpBean.getCreate_time()));
				holder.tvNickName.setText(helpBean.getNickname());
				holder.tvCont.setText(helpBean.getCont());
				ImageLoader.getInstance().displayImage(helpBean.getPortrait(), holder.ivPortrait, options);


				// 图片
				holder.llPicContainer.setDatas(helpBean.getPics());

				// 待回复
				if (helpBean.getReplys() != null && helpBean.getReplys().size() > 0) {
					Reply reply = helpBean.getReplys().get(0);
					if (reply.getStatus() == STATYUS_TYPE0) {
						holder.llBeforeContainer.setVisibility(View.GONE);
						if (helpBean.getPrice() <= 0) {// 价格为0为免费
							holder.btnPackage.setVisibility(View.GONE);
							holder.flStartAnswererContainer.setVisibility(View.GONE);
							holder.llEndAnswererContainer.setVisibility(View.GONE);
							holder.rlOperatorContainer.setVisibility(View.VISIBLE);
							holder.tvReject.setVisibility(View.GONE);
							if (helpBean.isReject()) {
								holder.tvReject.setVisibility(View.VISIBLE);
								holder.rlOperatorContainer.setVisibility(View.GONE);
							}

						} else {
							holder.btnPackage.setVisibility(View.VISIBLE);
							holder.flStartAnswererContainer.setVisibility(View.VISIBLE);
							holder.btnRecored.setText("建议60秒");
							holder.llEndAnswererContainer.setVisibility(View.GONE);
							holder.rlOperatorContainer.setVisibility(View.GONE);
							holder.btnPackage.setText(helpBean.getPrice()+"元红包");
						}
					} else {
						// 往期回复
						holder.llBeforeContainer.setVisibility(View.VISIBLE);
						holder.flStartAnswererContainer.setVisibility(View.GONE);
						holder.llEndAnswererContainer.setVisibility(View.GONE);
						holder.rlOperatorContainer.setVisibility(View.GONE);
						holder.btnPackage.setVisibility(helpBean.getPrice()>0?View.VISIBLE:View.GONE);
						holder.btnPackage.setText(helpBean.getPrice()>0 ? helpBean.getPrice()+"元" : "0");
						
						
						if (!TextUtils.isEmpty(reply.getCont())) {
							holder.tvBeforeCont.setVisibility(View.VISIBLE);
							holder.tvBeforeContRadio.setVisibility(View.GONE);
							holder.tvBeforeCont.setText(reply.getCont());
						} else {
							holder.tvBeforeCont.setVisibility(View.GONE);
							if (reply.getAudios() != null && reply.getAudios().size() > 0) {
								holder.tvBeforeContRadio.setVisibility(View.VISIBLE);
								holder.tvBeforeContRadio.setText(reply.getAudios().get(0).getTitle());
								boolean sendNow = reply.getAudios().get(0).isSendNow();
								boolean isSendSuccess = reply.getAudios().get(0).isSendSuccess();
								holder.btnReSend.setVisibility((sendNow && !isSendSuccess) ? View.VISIBLE : View.GONE);
							}
						}
					}
				}

				createViewClickListener(holder.btnAnswerer, holder, position);
				createViewClickListener(holder.btnReject, holder, position);
				createViewClickListener(holder.flStartAnswererContainer, holder, position);
				createViewClickListener(holder.btnSend, holder, position);
				createViewClickListener(holder.ivPlay, holder, position);
				createViewClickListener(holder.btnRepeatRecored, holder, position);
				createViewClickListener(holder.btnReSend, holder, position);
				createViewClickListener(holder.tvBeforeContRadio, holder, position);

			}
			return convertView;
		}

	}

	private static class ViewHolder {
		public TextView tvTime;
		public Button btnPackage;
		public TextView tvReject;
		public CircleImageView ivPortrait;
		public TextView tvNickName;
		public TextView tvCont;
		public FrameLayout flStartAnswererContainer;
		public LinearLayout llEndAnswererContainer;
		public TopicPicContainer llPicContainer;
		public RelativeLayout llBeforeContainer;
		public ImageView ivPlay;
		public ImageView ivPlayPoint;
		public TextView tvRecordTime;
		public Button btnSend;
		public Button btnReSend;
		public TextView btnRecored;
		public Button btnRepeatRecored;
		public Button btnAnswerer;
		public Button btnReject;
		public RelativeLayout rlOperatorContainer;

		public TextView tvBeforeCont;
		public TextView tvBeforeContRadio;
		public ImageView tvBeforeIvPlayPoint;
	}

	private void createViewClickListener(View v, final ViewHolder holder, final int position) {

		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCurrentClickHolder != null && position != mCurrentClickPosition) {
					// 点击其他item，需要先停止播放上一个录音
					stopAnimation();
					stopplayRecord();
				}
				if (position != mCurrentClickPosition && isRecording && (v.getId() == R.id.reply_fm || v.getId() == R.id.reply_bt_reset)) {
					Toast.makeText(ReplyFellHelpActivity.this, "请先停止其他解答录音", Toast.LENGTH_SHORT).show();
					return;
				}
				mCurrentClickPosition = position;
				mCurrentClickHolder = holder;

				switch (v.getId()) {
				case R.id.message_help_bt_answerer:
					// 普通回答
					replyOperation();
					break;
				case R.id.message_help_bt_reject:
					// 拒绝
					rejectOperation();
					break;
				case R.id.reply_fm:
					// 录音回答
					recordOperation();
					break;
				case R.id.reply_bt_reset:
					// 重录
					refreshUI(false);
					recordOperation();
					break;

				case R.id.reply_bt_send:
				case R.id.reply_bt_resend:
					// 发送
					sendOperation();
					break;
				case R.id.reply_iv_playing:
					// 播放本地录音
					playOperation(mediaRecorder.getPath(mDatas.get(mCurrentClickPosition).getId()));
					break;
				case R.id.message_before_help_cont_radio:
					// 以往解答 播放远程录音
					ArrayList<Reply> replys = mDatas.get(mCurrentClickPosition).getReplys();
					if (replys != null && replys.size() > 0 && replys.get(0).getAudios() != null && replys.get(0).getAudios().size() > 0)
						playOperation(mDatas.get(mCurrentClickPosition).getReplys().get(0).getAudios().get(0).getUrl());
					break;

				default:
					break;
				}

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent data) {
		if (responseCode == RESULT_OK && RESULT_CODE_REPLAY == requestCode) {
			String result = data.getStringExtra(FellHelpReplyActivity.CONT);
			if (mDatas.get(mCurrentClickPosition).getReplys() != null && mDatas.get(mCurrentClickPosition).getReplys().size() > 0) {
				mDatas.get(mCurrentClickPosition).getReplys().get(0).setStatus(STATYUS_TYPE2);
				mDatas.get(mCurrentClickPosition).getReplys().get(0).setCont(result);
				mAdapter.notifyDataSetChanged();
			}

		}
	}

	/**
	 * 播放录音
	 */
	private void playOperation(String path) {
		if (mediaPlay.isPlaying()) {
			stopplayRecord();
			stopAnimation();
		} else {
			mediaPlay.setmCallback(new OnCompletionListenerCallBack() {

				@Override
				public void onCompletionListenerCallBack() {
					stopAnimation();

				}

				@Override
				public void onError() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onPrepared() {
					// TODO Auto-generated method stub

				}
			});
			playRecord(path);
			startAnimation();
		}
	}

	protected void startAnimation() {
		ImageView mIvPointPlayering = getPlayingImageView();
		mIvPointPlayering.setVisibility(View.VISIBLE);
		scaleAnimation = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(600);
		scaleAnimation.setRepeatCount(Animation.INFINITE);
		mIvPointPlayering.setAnimation(scaleAnimation);
		scaleAnimation.start();
	}

	protected void stopAnimation() {
		ImageView mIvPointPlayering = getPlayingImageView();
		if (mIvPointPlayering != null && scaleAnimation != null && scaleAnimation.isInitialized()) {
			mIvPointPlayering.clearAnimation();
			mIvPointPlayering.setVisibility(View.GONE);
			// scaleAnimation.cancel();
		}
	}

	private ImageView getPlayingImageView() {
		ImageView mIvPointPlayering;
		ReplyFellHelpBean helpBean = mDatas.get(mCurrentClickPosition);
		if (helpBean.getReplys() != null && helpBean.getReplys().size() > 0 && helpBean.getReplys().get(0).getStatus() == STATYUS_TYPE0) {
			// 待回复view
			mIvPointPlayering = mCurrentClickHolder.ivPlayPoint;
		} else {
			// 已回复view
			mIvPointPlayering = mCurrentClickHolder.tvBeforeIvPlayPoint;
		}
		return mIvPointPlayering;
	}

	/**
	 * 发送录音文件到服务器
	 */
	private void sendOperation() {
		stopplayRecord();
		final String filePath = mediaRecorder.getPath(mDatas.get(mCurrentClickPosition).getId());
		final ArrayList<Audio> audios = new ArrayList<Audio>();
		mClient.postFile(VpConstants.FILE_UPLOAD, VpConstants.FILE_UPLOAD_PATH_AMR_FILE, filePath, false, false, false,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						String result = ResultParseUtil.deAesResult(responseBody);
						if (result != null) {
							JSONObject json = null;
							try {
								json = new JSONObject(result);
								String state = json.getString(VpConstants.HttpKey.STATE);
								if ("1".equals(state)) {// 上传成功
									String url = json.getString(VpConstants.HttpKey.URL);
									SendTopicUtils topUtils = new SendTopicUtils(ReplyFellHelpActivity.this, mClient);
									int id = mDatas.get(mCurrentClickPosition).getId();
									audios.add(new ReplyFellHelpBean().new Audio(url,
											mCurrentClickHolder.tvRecordTime.getText().toString().replace("\"", ""), true, true));
									topUtils.sendTopic(id, null, audios, new SendTopCallback() {

										@Override
										public void onSuccess() {
											// 发表成功
											Toast.makeText(ReplyFellHelpActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
											mDatas.get(mCurrentClickPosition).getReplys().get(0).setStatus(STATYUS_TYPE2);
											mDatas.get(mCurrentClickPosition).getReplys().get(0).setAudios(audios);
											mAdapter.notifyDataSetChanged();
										}

										@Override
										public void onFailed(String msg) {
											Toast.makeText(ReplyFellHelpActivity.this, msg, Toast.LENGTH_SHORT).show();

										}
									});

								} else {
									String msg = json.getString(VpConstants.HttpKey.MSG);
									Toast.makeText(ReplyFellHelpActivity.this, msg, Toast.LENGTH_SHORT).show();
								}
							} catch (JSONException e1) {
								e1.printStackTrace();
							}
							;
						} else {
							Toast.makeText(ReplyFellHelpActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
						}

					}

					@Override
					public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
						Toast.makeText(ReplyFellHelpActivity.this, "网络异常,发送失败", Toast.LENGTH_SHORT).show();
						audios.add(new ReplyFellHelpBean().new Audio(filePath,
								mCurrentClickHolder.tvRecordTime.getText().toString().replace("\"", ""), true, false));
						mDatas.get(mCurrentClickPosition).getReplys().get(0).setStatus(STATYUS_TYPE2);
						mDatas.get(mCurrentClickPosition).getReplys().get(0).setAudios(audios);
						mAdapter.notifyDataSetChanged();
					}
				});

	}

	/**
	 * 录音回答
	 */
	private void recordOperation() {
		if (mediaPlay.isPlaying())
			stopplayRecord();
		if (isRecording) {
			if (progress < leastTime) {
				Toast.makeText(this, "录音时间过短，请继续录制", Toast.LENGTH_SHORT).show();
				return;
			}
			mCurrentClickHolder.flStartAnswererContainer.setBackgroundResource(R.drawable.reply_bt_record_green_shape);
			Drawable drawable = getResources().getDrawable(R.drawable.message_fellhelp_voice);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); // 设置边界
			mCurrentClickHolder.btnRecored.setCompoundDrawables(null, null, drawable, null);// 画在右边
			stopRecord();// 结束录音
			mListview.setEnabled(true);
		} else {
			mCurrentClickHolder.flStartAnswererContainer.setBackgroundResource(R.drawable.reply_bt_record_yellow_shape);
			Drawable drawable = getResources().getDrawable(R.drawable.message_fellhelp_voiceing);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); // 设置边界
			mCurrentClickHolder.btnRecored.setCompoundDrawables(null, null, drawable, null);// 画在右边
			startRecord(mDatas.get(mCurrentClickPosition).getId());// 开始录音
			mListview.setEnabled(false);
		}
		isRecording = !isRecording;
	}

	/**
	 * 普通回复
	 */
	private void replyOperation() {
		// 回复
		ReplyFellHelpBean bean = mDatas.get(mCurrentClickPosition);
		Intent replyIntent = new Intent(this, FellHelpReplyActivity.class);
		replyIntent.putExtra(FellHelpReplyActivity.TOPIC_ID, bean.getId());
		replyIntent.putExtra(FellHelpReplyActivity.TOPIC_PORTRAIT, bean.getPortrait());
		replyIntent.putExtra(FellHelpReplyActivity.TOPIC_NAME, bean.getNickname());
		replyIntent.putExtra(FellHelpReplyActivity.TOPIC_CONT, bean.getCont());
		replyIntent.putExtra(FellHelpReplyActivity.TOPIC_TIME, VpDateUtils.getStandardDate(bean.getCreate_time()));
		replyIntent.putExtra(FellHelpReplyActivity.TOPIC_PIC, (bean.getPics() == null || bean.getPics().size() == 0) ? false : true);
		startActivityForResult(replyIntent, RESULT_CODE_REPLAY);
	}

	/**
	 * 残忍拒绝
	 */
	private void rejectOperation() {
		new IOSActionSheetDialog(this).builder().setTitle("确定残忍拒绝吗？").setCancelable(false).setCanceledOnTouchOutside(false)
				.addSheetItem("确定", SheetItemColor.Green, new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						String url = VpConstants.MESSAGE_USER_RESOLOVE_HELP;
						JSONObject body = new JSONObject();
						try {
							body.put("id", mDatas.get(mCurrentClickPosition).getId());
							body.put("uid", LoginStatus.getLoginInfo().getUid());
							body.put("status", 3);// 0=忽略2=同意回答3=拒绝回答
						} catch (Exception e) {
							Toast.makeText(ReplyFellHelpActivity.this, "请求参数错误", Toast.LENGTH_SHORT).show();
							return;
						}

						mClient.post(url, new RequestParams(), body.toString(), new AsyncHttpResponseHandler() {

							@Override
							public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
								String result = ResultParseUtil.deAesResult(responseBody);
								try {
									JSONObject json = new JSONObject(result);
									String code = json.getString(VpConstants.HttpKey.CODE);
									if ("0".equals(code)) {// 返回成功
										mDatas.get(mCurrentClickPosition).setReject(true);
										mAdapter.notifyDataSetChanged();
										Toast.makeText(ReplyFellHelpActivity.this, "已残忍拒绝", Toast.LENGTH_SHORT).show();
									} else {
										String message = json.getString(VpConstants.HttpKey.MSG);
										Toast.makeText(ReplyFellHelpActivity.this, message, Toast.LENGTH_LONG).show();
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}

							}

							@Override
							public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
								Toast.makeText(ReplyFellHelpActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();

							}
						});

					}
				}).show();
	}

	public void createPicClickListener(ImageView imageView, final ArrayList<String> pics, final int position) {
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ReplyFellHelpActivity.this, ShowImagesViewPagerActivity.class);
				intent.putStringArrayListExtra(ShowImagesViewPagerActivity.IMAGES, pics);
				intent.putExtra(ShowImagesViewPagerActivity.POSITION, position);
				startActivity(intent);

			}
		});
	}

	/**
	 * 停止录音 void TODO
	 */
	private void stopRecord() {
		handler.removeCallbacksAndMessages(null);
		refreshUI(true);
		mediaRecorder.stopRecord();
	}

	/**
	 * 开始录音 void TODO
	 */
	private void startRecord(int id) {
		if (mVibrator!=null) {
			mVibrator.vibrate(500);
		}
		mediaRecorder.startRecord(id);
		progress = -1;
		handler.sendEmptyMessageDelayed(0, 0);
	}

	/**
	 * 播放音频 void TODO
	 */
	private void playRecord(String path) {
		mediaPlay.startPlay(path);
	}

	/**
	 * 停止播放音频 void TODO
	 */
	private void stopplayRecord() {
		mediaPlay.stopPlayer();
	}

	protected void refreshUI(boolean isStop) {
		if (isStop) {
			mCurrentClickHolder.flStartAnswererContainer.setVisibility(View.GONE);
			mCurrentClickHolder.llEndAnswererContainer.setVisibility(View.VISIBLE);
			mCurrentClickHolder.tvRecordTime.setText(progress + "\"");
		} else {
			mCurrentClickHolder.flStartAnswererContainer.setVisibility(View.VISIBLE);
			mCurrentClickHolder.llEndAnswererContainer.setVisibility(View.GONE);
		}
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		initData(true);

	}
}
