package com.vp.loveu.channel.ui;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.me.nereo.multi_image_selector.MultiImageSelectorActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.channel.bean.ReportBean;
import com.vp.loveu.channel.bean.TopicBean;
import com.vp.loveu.channel.fragment.MoreDialogFragment;
import com.vp.loveu.channel.fragment.MoreDialogFragment.MorePopItemClickListener;
import com.vp.loveu.channel.fragment.MoreReportDialogFragment;
import com.vp.loveu.channel.utils.SendTopicUtils;
import com.vp.loveu.channel.utils.SendTopicUtils.SendTopCallback;
import com.vp.loveu.channel.utils.ToastUtils;
import com.vp.loveu.channel.widget.AudioButton;
import com.vp.loveu.channel.widget.AudioButton.OnSendAudioListener;
import com.vp.loveu.channel.widget.TopicPicContainer;
import com.vp.loveu.comm.ShowImagesViewPagerActivity;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.bean.FellHelpBean;
import com.vp.loveu.index.bean.FellHelpBean.FellHelpBeanAudiosBean;
import com.vp.loveu.index.widget.RecoderFrameLayout;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.message.ui.PrivateChatActivity;
import com.vp.loveu.my.activity.MyCenterActivity;
import com.vp.loveu.my.activity.UserIndexActivity;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.ScreenUtils;
import com.vp.loveu.util.SharedPreferencesHelper;
import com.vp.loveu.util.UIUtils;
import com.vp.loveu.util.VPLog;
import com.vp.loveu.util.VpDateUtils;
import com.vp.loveu.widget.CircleImageView;
import com.vp.loveu.widget.IOSActionSheetDialog;
import com.vp.loveu.widget.IOSActionSheetDialog.OnSheetItemClickListener;
import com.vp.loveu.widget.IOSActionSheetDialog.SheetItemColor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;

/**
 * @author：pzj
 * @date: 2015年11月25日 上午11:47:11
 * @Description:大家都在聊主题列表
 */
public class ChannelTopicListActivity extends VpActivity implements OnRefreshListener2<ListView>, OnClickListener, OnSendAudioListener {
	public static final String TOPICID = "topic_id";
	public static final String TOPICNAME = "topic_name";
	private static final int REQUEST_IMAGE = 0;
	private static final int REQUEST_REPLY = 1;
	private int topicId;
	private String topicName;
	private PullToRefreshListView mPullListView;
	private ListView mListView;
	private TopicAdapter mAdapter;
	private int mLimit = 10;
	private ArrayList<TopicBean> mDatas;
	private DisplayImageOptions options;
	private android.view.animation.Animation animation;
	private EditText mEtMessage;
	private TextView mSend;
	private ArrayList<String> mSelectedPicList;
	private ImageView mIvDefaultPic;
	private CircleImageView mIvSelectedPic;
	private TextView mTvSelectedCount;
	private int currentClickPosition;// 记录当前点击的主题
	private RelativeLayout mRlOperatorContainer;
	private static int OPERATER_ATTENTION = 0;// 关注
	private static int OPERATER_LIKE = 1;// 点赞
	private static int OPERATER_COLLECT = 2;// 收藏
	private static int OPERATER_CANCELCOLLECT = 3;// 取消收藏
	private SharedPreferencesHelper sharedPreferencesHelper;
	private boolean isOperatorFinish;
	private TextView mTvEmptyView;
	private ImageView mTvRecorder;// 录音按钮
	private LinearLayout recoderDialog;
	private ImageView dialogIv;
	private TextView dialogTv;
	private long recorderId = System.currentTimeMillis();
	AudioButton mAudioBtn;
	Gson gson = new Gson();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channel_topic_list_activity);
		ScreenUtils.initScreen(this);
		isOperatorFinish = true;
		this.topicId = getIntent().getIntExtra(TOPICID, -1);
		this.topicName = getIntent().getStringExtra(TOPICNAME);
		this.mClient = new VpHttpClient(this);
		sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
		mDatas = new ArrayList<TopicBean>();
		animation = AnimationUtils.loadAnimation(ChannelTopicListActivity.this, R.anim.topic_like_nn);
		initView();
		initDatas(this.topicId, this.mLimit, 0, 1, false);
	}

	private void initView() {
		initPublicTitle();
		mPubTitleView.mBtnLeft.setText("");
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		this.mPubTitleView.mTvTitle.setText(this.topicName);
		mPullListView = (PullToRefreshListView) findViewById(R.id.channel_topic_list);
		this.mSend = (TextView) findViewById(R.id.channel_topic_btn_send);
		this.mEtMessage = (EditText) findViewById(R.id.channel_topic_et_desc);
		this.mIvDefaultPic = (ImageView) findViewById(R.id.channel_topic_default_pic);
		this.mIvSelectedPic = (CircleImageView) findViewById(R.id.channel_topic_iv_selected);
		this.mTvSelectedCount = (TextView) findViewById(R.id.channel_topic_tv_selected_count);
		this.mRlOperatorContainer = (RelativeLayout) findViewById(R.id.channel_topic_operator_contrainer);
		this.mTvEmptyView = (TextView) findViewById(R.id.public_empty_view);
		mTvRecorder = (ImageView) findViewById(R.id.audio_iv);
		mAudioBtn = (AudioButton) findViewById(R.id.audio_btn);
		recoderDialog = (LinearLayout) findViewById(R.id.dialog);
		dialogIv = (ImageView) findViewById(R.id.dialog_iv);
		dialogTv = (TextView) findViewById(R.id.dialog_tv);

		mAudioBtn.setListener(this);
		this.mIvDefaultPic.setOnClickListener(this);
		this.mIvSelectedPic.setOnClickListener(this);
		mTvRecorder.setOnClickListener(this);
		this.mSend.setOnClickListener(this);
		this.mEtMessage.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if ((mSelectedPicList != null && mSelectedPicList.size() > 0) || !TextUtils.isEmpty(mEtMessage.getText().toString())) {
					mSend.setEnabled(true);
				} else {
					mSend.setEnabled(false);
				}
			}
		});

		mPullListView.setMode(Mode.BOTH);
		mListView = mPullListView.getRefreshableView();
		mAdapter = new TopicAdapter();
		mListView.setAdapter(mAdapter);
		mPullListView.setOnRefreshListener(this);
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_image_loading) // resource
																											// or
				.showImageForEmptyUri(R.drawable.default_image_loading_fail) // resource
																				// or
				.showImageOnFail(R.drawable.default_image_loading_fail) // resource
																		// or
				.resetViewBeforeLoading(false) // default
				.cacheInMemory(true) // default
				.cacheOnDisk(true) // default
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(false) // default
				.displayer(new SimpleBitmapDisplayer()).build();
	}

	private void initDatas(int id, int limit, final int index, final int dir, final boolean isLoadMoreData) {

		String url = VpConstants.CHANNEL_FORUM_DETAIL;
		RequestParams params = new RequestParams();
		params.put("id", this.topicId);
		params.put("limit", this.mLimit);
		params.put("dir", dir);// 获取数据的方向，1=是正向，即获取更新的信息，0=是反向，即获取更旧的信息
		params.put("index", index);// 获取记录的偏移量，当dir为1时则为上次返回记录的第一信息ID，当dir=0时为上次返回记录最后一条的ID
		params.put("uid", LoginStatus.getLoginInfo().getUid());

		mClient.setShowProgressDialog(true);
		mClient.get(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				mPullListView.onRefreshComplete();
				String result = ResultParseUtil.deAesResult(responseBody);
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString(VpConstants.HttpKey.CODE);
					if ("0".equals(code)) {// 返回成功
						JSONArray jsonData = new JSONArray(json.getString(VpConstants.HttpKey.DATA));
						ArrayList<TopicBean> currList = TopicBean.parseArrayJson(jsonData);
						if (currList != null && currList.size() > 0) {
							mDatas.removeAll(currList);
							if (dir == 0) {
								mDatas.addAll(currList);
							} else {
								// currList.addAll(mDatas);
								mDatas.removeAll(currList);
								mDatas.addAll(0, currList);
								// mDatas = currList;
							}
							mAdapter.notifyDataSetChanged();

						} else {
							if (isLoadMoreData)
								ToastUtils.showTextToast(ChannelTopicListActivity.this, "没有更多数据了");
						}

						setViewVisiable();

					} else {
						String message = json.getString(VpConstants.HttpKey.MSG);
						Toast.makeText(ChannelTopicListActivity.this, message, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				mPullListView.onRefreshComplete();
				Toast.makeText(ChannelTopicListActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
			}
		});

	}

	private void setViewVisiable() {
		if (mDatas != null && mDatas.size() > 0) {
			mPullListView.setVisibility(View.VISIBLE);
			mTvEmptyView.setVisibility(View.GONE);
		} else {
			mPullListView.setVisibility(View.GONE);
			mTvEmptyView.setVisibility(View.VISIBLE);
		}
	}

	private class TopicAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mDatas != null && mDatas.size() > 0) {
				return mDatas.size();
			} else {
				return 0;
			}

		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TopicBean topicBean = mDatas.get(position);
			TopicHolder holder = null;
			if (convertView == null) {
				holder = new TopicHolder();
				convertView = View.inflate(ChannelTopicListActivity.this, R.layout.channel_topic_list_item, null);
				holder.ivPortrait = (CircleImageView) convertView.findViewById(R.id.channel_topic_iv_portrait);
				holder.tvNickName = (TextView) convertView.findViewById(R.id.channel_topic_tv_nickname);
				holder.tvTime = (TextView) convertView.findViewById(R.id.channel_topic_tv_time);
				holder.tvFloor = (TextView) convertView.findViewById(R.id.channel_topic_tv_floor);
				holder.tvViewNum = (TextView) convertView.findViewById(R.id.channel_topic_tv_viewnum);
				holder.tvCont = (TextView) convertView.findViewById(R.id.channel_topic_tv_cont);
				holder.ivCollect = (ImageView) convertView.findViewById(R.id.channel_topic_iv_collect);
				holder.ivComment = (ImageView) convertView.findViewById(R.id.channel_topic_iv_comment);
				holder.ivLike = (ImageView) convertView.findViewById(R.id.channel_topic_iv_like);
				holder.ivMore = (ImageView) convertView.findViewById(R.id.channel_topic_iv_more);
				holder.ivDelete = (ImageView) convertView.findViewById(R.id.channel_topic_iv_del);
				holder.tvAddone = (TextView) convertView.findViewById(R.id.channel_tv_like_addone);
				holder.audioContainer = (LinearLayout) convertView.findViewById(R.id.audio_container);
				holder.tvLikeCount = (TextView) convertView.findViewById(R.id.channel_topic_tv_like_count);
				holder.llContainer = (TopicPicContainer) convertView.findViewById(R.id.channel_topic_pic_ll_container);
				convertView.setTag(holder);
			} else {
				holder = (TopicHolder) convertView.getTag();
			}

			createClickListener(holder.ivCollect, position, holder.tvAddone, holder.tvLikeCount);// 收藏
			createClickListener(holder.ivComment, position, holder.tvAddone, holder.tvLikeCount);// 评论
			createClickListener(holder.ivLike, position, holder.tvAddone, holder.tvLikeCount);// 喜欢
			createClickListener(holder.ivMore, position, holder.tvAddone, holder.tvLikeCount);// 更多
			createClickListener(holder.ivPortrait, position, holder.tvAddone, holder.tvLikeCount);// 头像---用户主页
			createClickListener(holder.ivDelete, position, holder.tvAddone, holder.tvLikeCount);// 删除

			// 自己的帖子，回复按钮变成删除，没有“更多”功能
			if (topicBean.getUid() == LoginStatus.getLoginInfo().getUid()) {
				holder.ivComment.setVisibility(View.GONE);
				holder.ivDelete.setVisibility(View.VISIBLE);
				holder.ivMore.setVisibility(View.GONE);
			} else {
				holder.ivComment.setVisibility(View.VISIBLE);
				holder.ivDelete.setVisibility(View.GONE);
				holder.ivMore.setVisibility(View.VISIBLE);
			}

			// 检查该登录用户的本地头像是否与服务器返回一致(服务端做了缓存)
			String portrait = topicBean.getPortrait();
			if (topicBean.getUid() == LoginStatus.getLoginInfo().getUid()) {
				String localPortrait = sharedPreferencesHelper.getStringValue(LoginUserInfoBean.PORTRAIT);
				if (localPortrait != null && !localPortrait.equals(portrait))
					portrait = localPortrait;
			}
			// 同上
			String nickName = topicBean.getNickname();
			if (topicBean.getUid() == LoginStatus.getLoginInfo().getUid()) {
				String localNickName = sharedPreferencesHelper.getStringValue(LoginUserInfoBean.NICKNAME);
				if (localNickName != null && !localNickName.equals(nickName))
					nickName = localNickName;

			}

			ImageLoader.getInstance().displayImage(portrait, holder.ivPortrait, options);
			holder.tvNickName.setText(nickName);
			holder.tvTime.setText(VpDateUtils.getStandardDate(topicBean.getCreate_time()));
			holder.tvFloor.setText(topicBean.getFloor_num() + "楼");
			holder.tvViewNum.setText(topicBean.getView_num() + "人看过");

			// 收藏,点赞
			holder.ivCollect
					.setImageResource(topicBean.getIs_fav() == 1 ? R.drawable.channel_topic_collected : R.drawable.channel_topic_collect);
			holder.ivLike.setImageResource(topicBean.getIs_like() == 1 ? R.drawable.channel_topic_likeed : R.drawable.channel_topic_like);
			if (topicBean.getLike_num() > 0) {
				holder.tvLikeCount.setVisibility(View.VISIBLE);
				holder.tvLikeCount.setText("(" + topicBean.getLike_num() + ")");
			} else {
				holder.tvLikeCount.setVisibility(View.GONE);
			}

			// 设置内容
			if (topicBean.getSource() != null) {
				String floorMsg = "回复" + topicBean.getSource().getFloor_num() + "楼:  ";
				String conent = topicBean.getCont();
				SpannableStringBuilder style = new SpannableStringBuilder(floorMsg + conent);
				style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.sub_textView_color99)), 0, floorMsg.length(),
						Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				style.setSpan(new AbsoluteSizeSpan(UIUtils.dp2px(12)), 0, floorMsg.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				holder.tvCont.setText(style);
			} else {
				holder.tvCont.setText(topicBean.getCont());
			}
			holder.tvCont.setVisibility((topicBean.getCont() == null || "".equals(topicBean.getCont())) ? View.GONE : View.VISIBLE);

			if (topicBean.getPics() == null || topicBean.getPics().size() <= 0) {
				// 没有图片
				holder.llContainer.removeAllViews();
			} else {
				holder.llContainer.setDatas(topicBean.getPics());
			}

			if (TextUtils.isEmpty(topicBean.getAudio()) || topicBean.getAudio().length() < 9) {// 防止
																								// http://
																								// 出现
				holder.audioContainer.removeAllViews();
			} else {
				// 说明是音频文件
				holder.audioContainer.removeAllViews();
				RecoderFrameLayout item = audioItem = new RecoderFrameLayout(ChannelTopicListActivity.this);
				item.setData(getAudioDataBean(topicBean));
				holder.audioContainer.addView(item);
			}
			return convertView;
		}
	}

	private FellHelpBeanAudiosBean getAudioDataBean(TopicBean topicBean) {
		FellHelpBeanAudiosBean bean = new FellHelpBean.FellHelpBeanAudiosBean();
		bean.url = topicBean.getAudio();
		bean.title = topicBean.getAudio_title();
		return bean;
	}

	private RecoderFrameLayout audioItem;// 记录当前播放item的引用对象 当ondestory的时候要释放

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			if (audioItem != null) {
				audioItem.playerRecoderFrameLayout.mediaPlayer.stopPlayer();
				audioItem.playerRecoderFrameLayout = null;
			}
		} catch (Exception e) {
		}
}

class TopicHolder {
	public CircleImageView ivPortrait;
	public TextView tvNickName;
	public TextView tvTime;
	public TextView tvFloor;
	public TextView tvViewNum;
	public TextView tvCont;
	public LinearLayout picContainer;
	public ImageView ivCollect;
	public ImageView ivComment;
	public ImageView ivLike;
	public ImageView ivMore;
	public ImageView ivDelete;
	public TextView tvAddone;
	public TextView tvLikeCount;
	public TopicPicContainer llContainer;
	public LinearLayout audioContainer;

	}

	public void createPicClickListener(ImageView imageView, final ArrayList<String> pics, final int position) {
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ChannelTopicListActivity.this, ShowImagesViewPagerActivity.class);
				intent.putStringArrayListExtra(ShowImagesViewPagerActivity.IMAGES, pics);
				intent.putExtra(ShowImagesViewPagerActivity.POSITION, position);
				startActivity(intent);

			}
		});
	}

	/**
	 * 评论,点赞,收藏，更多
	 * 
	 * @param ivCollect
	 * @param position
	 */
	public void createClickListener(final ImageView imageView, final int position, final TextView tvAddOne, final TextView tvLikeCount) {
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				currentClickPosition = position;

				TopicBean topicBean = mDatas.get(position);
				switch (imageView.getId()) {
				case R.id.channel_topic_iv_collect:
					// 收藏
					// 判断当前主题是否收藏
					int isFav = mDatas.get(currentClickPosition).getIs_fav();
					if (0 == isFav) {// 未收藏
						attentionOperate(OPERATER_COLLECT, imageView, null, null);
					} else {
						attentionOperate(OPERATER_CANCELCOLLECT, imageView, null, null);
					}
					break;
				case R.id.channel_topic_iv_comment:
					// 回复
					Intent replyIntent = new Intent(ChannelTopicListActivity.this, ChannelTopicReplyActivity.class);
					replyIntent.putExtra(ChannelTopicReplyActivity.TOPIC_ID, topicId);
					replyIntent.putExtra(ChannelTopicReplyActivity.TOPIC_RID, topicBean.getId());
					replyIntent.putExtra(ChannelTopicReplyActivity.TOPIC_UID, topicBean.getUid());
					replyIntent.putExtra(ChannelTopicReplyActivity.TOPIC_PORTRAIT, topicBean.getPortrait());
					replyIntent.putExtra(ChannelTopicReplyActivity.TOPIC_NAME, topicBean.getNickname());
					replyIntent.putExtra(ChannelTopicReplyActivity.TOPIC_CONT, topicBean.getCont());

					if (!TextUtils.isEmpty(topicBean.getAudio())) {
						// 音频
						replyIntent.putExtra("audio", getAudioDataBean(topicBean)); // 对象带过去
					}
					replyIntent.putExtra(ChannelTopicReplyActivity.TOPIC_TIME, VpDateUtils.getStandardDate(topicBean.getCreate_time()));
					replyIntent.putExtra(ChannelTopicReplyActivity.TOPIC_PIC,
							(topicBean.getPics() == null || topicBean.getPics().size() == 0) ? false : true);
					startActivityForResult(replyIntent, REQUEST_REPLY);
					break;
				case R.id.channel_topic_iv_like:
					// 判断当前主题是否已赞
					int like = mDatas.get(currentClickPosition).getIs_like();
					if (0 == like) {// 未赞
						attentionOperate(OPERATER_LIKE, imageView, tvAddOne, tvLikeCount);
					}

					break;
				case R.id.channel_topic_iv_more:
					// 更多
					MoreDialogFragment editNameDialog = new MoreDialogFragment(new MorePopItemClickListener() {

						@Override
						public void onMoreItemClick(View v) {
							switch (v.getId()) {
							case R.id.channel_topic_more_attention:
								attentionOperate(OPERATER_ATTENTION, null, null, null);
								break;
							case R.id.channel_topic_more_chat:
								chatOperater();
								break;
							case R.id.channel_topic_more_report:
								TopicBean topicBean = mDatas.get(currentClickPosition);
								ReportBean bean = new ReportBean(topicBean.getUid(), topicBean.getNickname(), null, null, 1,
										topicBean.getId());
								MoreReportDialogFragment reportDialog = new MoreReportDialogFragment(ChannelTopicListActivity.this, bean);
								reportDialog.show(getSupportFragmentManager(), "report");
								break;

							default:
								break;
							}

						}
					});
					editNameDialog.show(getSupportFragmentManager(), "more");
					break;
				case R.id.channel_topic_iv_portrait:
					int currentUid = mDatas.get(currentClickPosition).getUid();
					// 点击头像
					if (currentUid == LoginStatus.getLoginInfo().getUid()) {
						Intent intent = new Intent(UIUtils.getContext(), MyCenterActivity.class);
						startActivity(intent);
					} else {
						Intent intent = new Intent(UIUtils.getContext(), UserIndexActivity.class);
						intent.putExtra(UserIndexActivity.KEY_UID, currentUid);
						startActivity(intent);
					}
					break;

				case R.id.channel_topic_iv_del:
					// 删除
					deleteConfirmOperation();
					break;
				default:
					break;
				}

			}
		});

	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// 下拉刷新
		// initDatas(topicId, mLimit, index, 1, false);
		int index = 0;
		if (mDatas.size() != 0) {
			index = mDatas.get(0).getId();
		}
		initDatas(topicId, mLimit, index, 1, false);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// 上拉加载更多
		if (mDatas != null && mDatas.size() > 0) {
			initDatas(topicId, mLimit, mDatas.get(mDatas.size() - 1).getId(), 0, true);
		} else {
			new Thread() {
				public void run() {
					SystemClock.sleep(1500);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mPullListView.onRefreshComplete();
						}
					});

				};
			}.start();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.channel_topic_default_pic:
		case R.id.channel_topic_iv_selected:
			Intent intent = new Intent(this, MultiImageSelectorActivity.class);
			// 是否显示拍摄图片
			intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
			// 最大可选择图片数量
			intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 3);
			// 选择模式
			intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
			// 默认选择
			if (mSelectedPicList != null && mSelectedPicList.size() > 0) {
				intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectedPicList);
			}
			startActivityForResult(intent, REQUEST_IMAGE);

			break;
		case R.id.channel_topic_btn_send:
			// 发送
			// mClient=new VpHttpClient(this);
			
			//开始发送 设置不可点击
			mSend.setEnabled(false);
			SendTopicUtils topUtils = new SendTopicUtils(this, mClient);
			topUtils.sendTopic(this.topicId, 0, this.mEtMessage.getText().toString(), this.mSelectedPicList, new SendTopCallback() {
				@Override
				public void onSuccess() {
					// 发表成功
					Toast.makeText(ChannelTopicListActivity.this, "发表成功", Toast.LENGTH_LONG).show();
					int index = 0;
					if (mDatas != null && mDatas.size() > 0) {
						index = mDatas.get(0).getId();
					}
					initDatas(topicId, mLimit, index, 1, false);
					mEtMessage.setText("");
					if (mSelectedPicList != null) {
						mSelectedPicList.clear();
						resetPicView();
					}

				}

				@Override
				public void onFailed(String msg) {
					mSend.setEnabled(true);
					Toast.makeText(ChannelTopicListActivity.this, msg, Toast.LENGTH_SHORT).show();

				}
			});
			break;
		case R.id.audio_iv:
			// 显示录音按钮
			mAudioBtn.setVisibility(!isShowAudioBtn ? View.VISIBLE : View.GONE);
			mEtMessage.setVisibility(!isShowAudioBtn ? View.GONE : View.VISIBLE);
			mTvRecorder.setImageResource(!isShowAudioBtn ? R.drawable.allchat_text : R.drawable.allchat_voice);
			mSend.setEnabled(isShowAudioBtn && !TextUtils.isEmpty(mEtMessage.getText().toString().trim()));
			isShowAudioBtn = !isShowAudioBtn;
			break;
		default:
			break;
		}
	}

	boolean isShowAudioBtn;

	/**
	 * 发送录音文件到服务器
	 */
	private void sendOperation(final String path, final int s) {
		// 发送之前应该先判断当前是否已经停止录音
		if (mClient == null) {
			mClient = new VpHttpClient(this);
		}
		mClient.setShowProgressDialog(false);
		mClient.postFile(VpConstants.FILE_UPLOAD, VpConstants.FILE_UPLOAD_PATH_AMR_FILE, path, false, false, false,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						String result = ResultParseUtil.deAesResult(responseBody);
						if (!TextUtils.isEmpty(result)) {
							JSONObject json = null;
							try {
								json = new JSONObject(result);
								String state = json.getString(VpConstants.HttpKey.STATE);
								if ("1".equals(state)) {// 上传成功
									String url = json.getString(VpConstants.HttpKey.URL);
									// 发送成功 删除本地音频文件
									File file = new File(path);
									if (file.exists() && file.isFile()) {
										file.delete();
									}
									// 发表音频
									sendAudioFile(url, s);
								} else {
									mClient.stopProgressDialog();
									String msg = json.getString(VpConstants.HttpKey.MSG);
									Toast.makeText(ChannelTopicListActivity.this, msg, Toast.LENGTH_SHORT).show();
								}
							} catch (JSONException e1) {
								mClient.stopProgressDialog();
								e1.printStackTrace();
							}
							;
						} else {
							mClient.stopProgressDialog();
							Toast.makeText(ChannelTopicListActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
						}

					}

					@Override
					public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
						mClient.stopProgressDialog();
						Toast.makeText(ChannelTopicListActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
					}
				});
	}

	// 开始发送
	protected void sendAudioFile(String url, int s) {
		if (mClient == null) {
			mClient = new VpHttpClient(this);
		}
		mClient.setShowProgressDialog(false);
		JsonObject obj = new JsonObject();
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		int uid = 5;
		if (loginInfo != null) {
			uid = loginInfo.getUid();
		}
		obj.addProperty("uid", uid);
		obj.addProperty("id", topicId);// 话题id
		obj.addProperty("rid", 0);// 被回复信息ID,没有则为0
		obj.addProperty("audio", url);
		obj.addProperty("audio_title", s + "");
		mClient.post(VpConstants.CHANNEL_FORUM_REPLY, new RequestParams(), obj.toString(), new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String deAesResult = ResultParseUtil.deAesResult(responseBody);
				TopicBean fromJson = gson.fromJson(deAesResult, TopicBean.class);
				if (fromJson.code == 0) {
					// 发表成功 --- addAdapter --- notifydatesetchanged
					TopicBean data = gson.fromJson(fromJson.data, TopicBean.class);
					mDatas.add(0, data);
					mAdapter.notifyDataSetChanged();
				} else {
					Toast.makeText(getApplicationContext(), fromJson.msg, Toast.LENGTH_SHORT).show();
				}
				mClient.stopProgressDialog();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				mClient.stopProgressDialog();
				Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_IMAGE) {
			if (resultCode == RESULT_OK) {
				mSelectedPicList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
				resetPicView();
			}
		} else if (requestCode == REQUEST_REPLY) {
			if (resultCode == RESULT_OK) {
				if (mDatas != null && mDatas.size() > 0) {
					initDatas(topicId, mLimit, mDatas.get(0).getId(), 1, false);
				}
			}
		}
	}

	private void resetPicView() {
		if (mSelectedPicList != null && mSelectedPicList.size() > 0) {
			this.mIvDefaultPic.setVisibility(View.GONE);
			this.mIvSelectedPic.setVisibility(View.VISIBLE);
			this.mTvSelectedCount.setVisibility(View.VISIBLE);
			this.mTvSelectedCount.setText(mSelectedPicList.size() + "");
			Options options = new Options();
			options.inSampleSize = 8;
			Bitmap bitmap = BitmapFactory.decodeFile(mSelectedPicList.get(0), options);
			this.mIvSelectedPic.setImageBitmap(bitmap);
		} else {
			this.mIvDefaultPic.setVisibility(View.VISIBLE);
			this.mIvSelectedPic.setVisibility(View.GONE);
			this.mTvSelectedCount.setVisibility(View.GONE);
		}

		if ((mSelectedPicList != null && mSelectedPicList.size() > 0) || !TextUtils.isEmpty(mEtMessage.getText().toString())) {
			mSend.setEnabled(true);
		} else {
			mSend.setEnabled(false);
		}
	}

	/**
	 * 私聊
	 */
	private void chatOperater() {
		TopicBean topicBean = mDatas.get(this.currentClickPosition);
		Intent chatIntent = new Intent(this, PrivateChatActivity.class);
		chatIntent.putExtra(PrivateChatActivity.CHAT_USER_ID, topicBean.getUid());
		chatIntent.putExtra(PrivateChatActivity.CHAT_USER_NAME, topicBean.getNickname());
		chatIntent.putExtra(PrivateChatActivity.CHAT_USER_HEAD_IMAGE, topicBean.getPortrait());
		chatIntent.putExtra(PrivateChatActivity.CHAT_XMPP_USER, topicBean.getXmpp_user());
		startActivity(chatIntent);
	}

	/**
	 * 关注,赞,收藏
	 */
	private void attentionOperate(final int operaterType, final ImageView imageView, final TextView tvAddOne, final TextView likeCount) {
		if (!isOperatorFinish)
			return;
		isOperatorFinish = false;
		String url = null;
		JSONObject data = new JSONObject();
		try {
			if (operaterType == OPERATER_ATTENTION) {// 关注
				url = VpConstants.CHANNEL_USER_CREATE_FOLLOW;
				data.put("from_uid", LoginStatus.getLoginInfo().getUid());
				data.put("to_uid", mDatas.get(this.currentClickPosition).getUid());

			} else if (operaterType == OPERATER_LIKE) {// 点赞
				url = VpConstants.CHANNEL_GENERAL_ADD_PRAISE;
				data.put("uid", LoginStatus.getLoginInfo().getUid());
				data.put("id", mDatas.get(this.currentClickPosition).getId());
				data.put("type", 3);// 点赞类型1=长文推荐 2=PUA课堂 3=大家都在聊
			} else if (operaterType == OPERATER_COLLECT) {// 收藏
				url = VpConstants.CHANNEL_GENERAL_ADD_FAVORITE;// 收藏
				data.put("uid", LoginStatus.getLoginInfo().getUid());
				data.put("id", mDatas.get(this.currentClickPosition).getId());
				data.put("type", 3);// 点赞类型1=长文推荐 2=PUA课堂 3=大家都在聊
			} else if (operaterType == OPERATER_CANCELCOLLECT) {
				url = VpConstants.My_DELETE_FAVORITE;// 取消收藏
				data.put("uid", LoginStatus.getLoginInfo().getUid());
				data.put("id", mDatas.get(this.currentClickPosition).getId());
				data.put("type", 3);// 点赞类型1=长文推荐 2=PUA课堂 3=大家都在聊
			}

			if (operaterType == OPERATER_LIKE || operaterType == OPERATER_ATTENTION) {
				// 关闭加载对话框
				mClient.setShowProgressDialog(false);
			}

			mClient.post(url, new RequestParams(), data.toString(), false, new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					isOperatorFinish = true;
					mClient.setShowProgressDialog(true);
					String result = ResultParseUtil.deAesResult(responseBody);
					try {
						JSONObject json = new JSONObject(result);
						String code = json.getString(VpConstants.HttpKey.CODE);

						if ("0".equals(code)) {// 返回成功
							if (operaterType == OPERATER_ATTENTION) {// 关注

								Toast.makeText(ChannelTopicListActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
							} else if (operaterType == OPERATER_LIKE) {// 赞
								tvAddOne.setVisibility(View.VISIBLE);
								tvAddOne.startAnimation(animation);
								new Handler().postDelayed(new Runnable() {
									public void run() {
										tvAddOne.setVisibility(View.GONE);
									}
								}, 1000);
								imageView.setImageResource(R.drawable.channel_topic_likeed);
								int count = mDatas.get(currentClickPosition).getLike_num() + 1;
								mDatas.get(currentClickPosition).setIs_like(1);
								mDatas.get(currentClickPosition).setLike_num(count);
								likeCount.setVisibility(View.VISIBLE);
								likeCount.setText("(" + count + ")");

							} else if (operaterType == OPERATER_COLLECT) {// 收藏
								Toast.makeText(ChannelTopicListActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
								imageView.setImageResource(R.drawable.channel_topic_collected);
								mDatas.get(currentClickPosition).setIs_fav(1);
								mDatas.get(currentClickPosition).setFav_num(mDatas.get(currentClickPosition).getFav_num() + 1);
								// mAdapter.notifyDataSetChanged();
							} else if (operaterType == OPERATER_CANCELCOLLECT) {// 取消收藏
								imageView.setImageResource(R.drawable.channel_topic_collect);
								mDatas.get(currentClickPosition).setIs_fav(0);
								mDatas.get(currentClickPosition).setFav_num(mDatas.get(currentClickPosition).getFav_num() - 1);
							}

						} else {
							String message = json.getString(VpConstants.HttpKey.MSG);
							Toast.makeText(ChannelTopicListActivity.this, message, Toast.LENGTH_LONG).show();
							mClient.setShowProgressDialog(true);
						}
					} catch (JSONException e) {
						mClient.setShowProgressDialog(true);
						e.printStackTrace();
					}

				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					isOperatorFinish = true;
					mClient.setShowProgressDialog(true);
					Toast.makeText(ChannelTopicListActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();

				}
			});
		} catch (JSONException e) {
			e.printStackTrace();

		}

	}

	/**
	 * 删除帖子
	 */
	private void deleteConfirmOperation() {
		new IOSActionSheetDialog(this).builder().setTitle("确定删除吗？").setCancelable(false).setCanceledOnTouchOutside(false)
				.addSheetItem("确定", SheetItemColor.Green, new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						deleteOperation();
					}
				}).show();

	}

	private void deleteOperation() {
		String url = VpConstants.CHANNEL_FORUM_DELETE;
		;
		JSONObject data = new JSONObject();
		try {
			data.put("uid", LoginStatus.getLoginInfo().getUid());
			data.put("id", mDatas.get(this.currentClickPosition).getId());

			mClient.post(url, new RequestParams(), data.toString(), false, new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String result = ResultParseUtil.deAesResult(responseBody);
					try {
						JSONObject json = new JSONObject(result);
						String code = json.getString(VpConstants.HttpKey.CODE);

						if ("0".equals(code)) {
							Toast.makeText(ChannelTopicListActivity.this, "删除成功", Toast.LENGTH_LONG).show();
							mDatas.remove(mDatas.get(currentClickPosition));
							mAdapter.notifyDataSetChanged();
							setViewVisiable();
						} else {
							String message = json.getString(VpConstants.HttpKey.MSG);
							Toast.makeText(ChannelTopicListActivity.this, message, Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					Toast.makeText(ChannelTopicListActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();

				}
			});
		} catch (JSONException e) {
			e.printStackTrace();

		}
	}

	/**
	 * 点击输入框，发送按钮，图片选择按钮以外区域，隐藏键盘
	 */

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (isShouldHideInput(mRlOperatorContainer, ev) && isShouldHideInput(mPubTitleView, ev)) {

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			}
			return super.dispatchTouchEvent(ev);
		}
		// 必不可少，否则所有的组件都不会有TouchEvent了
		if (getWindow().superDispatchTouchEvent(ev)) {
			return true;
		}
		return onTouchEvent(ev);
	}

	public boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null) {
			int[] leftTop = { 0, 0 };
			// 获取输入框当前的location位置
			v.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top + v.getHeight();
			int right = left + v.getWidth();
			if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
				// 点击的是输入框区域，保留点击EditText的事件
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	@Override
	public void updateVolume(int volume) {
		if (mAudioBtn.isOutside) {
			return;
		}
		VPLog.d("aaa", "volume" + volume);
		int identifier = getResources().getIdentifier("volume" + volume, "drawable", getPackageName());
		dialogIv.setImageResource(identifier);
	}

	@Override
	public void updateView(int status) {
		if (status == AudioButton.STATE_RECORDERING) {
			// 正在录音
			dialogTv.setText("手指上滑,取消发送");
			dialogIv.setImageResource(R.drawable.volume1);
			dialogTv.setBackgroundColor(Color.parseColor("#00000000"));
		} else if (status == AudioButton.STATE_WANT_TO_CANCEL) {
			// 想要取消
			dialogTv.setText("松开手指,取消发送");
			dialogTv.setBackgroundColor(Color.parseColor("#F77600"));
			dialogIv.setImageResource(R.drawable.cancle_recoder);
		}
	}

	@Override
	public void sendAudio(String path, int s) {
		if (!TextUtils.isEmpty(path)) {// path 表示录音文件路径 s表示时长
			mClient.startProgressDialog();
			sendOperation(path, s);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mAudioBtn.onPasue();
	}

	@Override
	public void onStopRecoder() {
		recoderDialog.setVisibility(View.GONE);
		//停止发送后 需要恢复默认图片 volume1
		dialogIv.setImageResource(R.drawable.volume1);
		dialogTv.setText("手指上滑,取消发送");
		dialogTv.setBackgroundColor(Color.parseColor("#00000000"));
	}

	@Override
	public void onStartRecoder() {
		recoderDialog.setVisibility(View.VISIBLE);
	}
}
