package com.vp.loveu.my.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
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
import com.vp.loveu.channel.bean.ReportBean;
import com.vp.loveu.channel.fragment.MoreDialogFragment;
import com.vp.loveu.channel.fragment.MoreDialogFragment.MorePopItemClickListener;
import com.vp.loveu.channel.fragment.MoreReportDialogFragment;
import com.vp.loveu.channel.ui.ChannelTopicReplyActivity;
import com.vp.loveu.channel.widget.TopicPicContainer;
import com.vp.loveu.comm.ShowImagesViewPagerActivity;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.bean.FellHelpBean.FellHelpBeanAudiosBean;
import com.vp.loveu.index.bean.MySeekHelpBean.MySeekAudioBean;
import com.vp.loveu.index.widget.EmptyTextView;
import com.vp.loveu.index.widget.RecoderFrameLayout;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.message.ui.PrivateChatActivity;
import com.vp.loveu.my.bean.MyDllBean;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.ScreenUtils;
import com.vp.loveu.util.SharedPreferencesHelper;
import com.vp.loveu.util.UIUtils;
import com.vp.loveu.util.VpDateUtils;
import com.vp.loveu.widget.CircleImageView;
import com.vp.loveu.widget.IOSActionSheetDialog;
import com.vp.loveu.widget.IOSActionSheetDialog.OnSheetItemClickListener;
import com.vp.loveu.widget.IOSActionSheetDialog.SheetItemColor;

import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * @时间2015年12月9日下午2:55:16
 * @功能 我的动态的界面  TODO:
 * @作者 pzj
 */

public class MyDLLActivity extends VpActivity implements OnRefreshListener2<ListView> {

	public static final String UID="uid";
	public static final String NICKNAME = "nickname";
	private Context mContext;
	private VpHttpClient mClient;
	private PullToRefreshListView mPullListView;
	private ListView mListView;
	private TextView mTvEmpty;
	private TopicAdapter mAdapter;
	private DisplayImageOptions options;
	private static final int mLimit=10;
	private int mPage=1;//分页页码
	private ArrayList<MyDllBean> mDatas;
	private int mUid=-1;
	private String nickName;
	private SharedPreferencesHelper sharedPreferencesHelper;
	private int currentClickPosition;//记录当前点击的主题
	private static int OPERATER_ATTENTION=0;//关注
	private static int OPERATER_LIKE=1;//点赞
	private static int OPERATER_COLLECT=2;//收藏
	private static int OPERATER_CANCELCOLLECT=3;//取消收藏
	private android.view.animation.Animation animation;
	
	private boolean isOperatorFinish;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mydll_activity);
		mContext=this;
		mClient=new VpHttpClient(this);
		isOperatorFinish=true;
		animation=AnimationUtils.loadAnimation(this,R.anim.topic_like_nn);
		ScreenUtils.initScreen(this);
		sharedPreferencesHelper = SharedPreferencesHelper.getInstance(mContext);
		mUid=getIntent().getIntExtra(UID, -1);
		nickName = getIntent().getStringExtra(NICKNAME);
		if(mUid==-1 || mUid == LoginStatus.getLoginInfo().getUid()){
			mUid=LoginStatus.getLoginInfo().getUid();
		}
		initPublicTitle();
		initView();
		initDatas(this.mLimit , 0, 1,false);
	}

	private void initView() {
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		if (mUid == LoginStatus.getLoginInfo().getUid()) {
			mPubTitleView.mTvTitle.setText("我的动态");
		}else{
			mPubTitleView.mTvTitle.setText(nickName+"的动态");
		}
		mTvEmpty=(TextView) findViewById(R.id.public_empty_view);
		mPullListView=(PullToRefreshListView) findViewById(R.id.mydll_listview);
		mPullListView.setMode(Mode.BOTH);
		mListView = mPullListView.getRefreshableView();
		mDatas=new ArrayList<MyDllBean>();
		mAdapter=new TopicAdapter();
		mListView.setAdapter(mAdapter);
		mPullListView.setOnRefreshListener(this);
		 options = new DisplayImageOptions.Builder()
		         .showImageOnLoading(R.drawable.default_image_loading) // resource or
		        .showImageForEmptyUri(R.drawable.default_image_loading_fail) // resource or
		        .showImageOnFail(R.drawable.default_image_loading_fail) // resource or
		        .resetViewBeforeLoading(false) // default
		        .cacheInMemory(true) // default
		        .cacheOnDisk(true) // default
		        .bitmapConfig(Bitmap.Config.RGB_565)  
		        .considerExifParams(false) // default
		        .displayer(new SimpleBitmapDisplayer())
		        .build();

	}
	
	private  void initDatas(int limit,int index,final int dir,final boolean isLoadMore){
		String url = VpConstants.MY_TIME_LINE;
		RequestParams params = new RequestParams();
		params.put("limit", this.mLimit);
		params.put("dir", dir);//获取数据的方向，1=是正向，即获取更新的信息，0=是反向，即获取更旧的信息
		params.put("index", index);//获取记录的偏移量，当dir为1时则为上次返回记录的第一信息ID，当dir=0时为上次返回记录最后一条的ID
		params.put("id", this.mUid);
		
		mClient.get(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				mPullListView.onRefreshComplete();
				String result = ResultParseUtil.deAesResult(responseBody);
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString(VpConstants.HttpKey.CODE);

					if ("0".equals(code)) {// 返回成功
						JSONArray jsonData =new JSONArray(json.getString(VpConstants.HttpKey.DATA));
						ArrayList<MyDllBean> currList = MyDllBean.parseArrayJson(jsonData);
						if(currList!=null && currList.size()>0){
							if(dir==0){
								mDatas.addAll(currList);
							}else{
								currList.addAll(mDatas);
								mDatas=currList;
							}
							mAdapter.notifyDataSetChanged();							
						}else{
							if(isLoadMore)
								Toast.makeText(MyDLLActivity.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
						}
						
						setViewVisiable();

					} else {
						String message = json.getString(VpConstants.HttpKey.MSG);
						Toast.makeText(MyDLLActivity.this, message, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				mPullListView.onRefreshComplete();
				Toast.makeText(mContext, R.string.network_error, 0).show();
			}
		});
		
	
	}
	
	private void setViewVisiable(){
		if(mDatas!=null && mDatas.size()>0){
			mPullListView.setVisibility(View.VISIBLE);
			mTvEmpty.setVisibility(View.GONE);
		}else{
			mPullListView.setVisibility(View.GONE);
			mTvEmpty.setVisibility(View.VISIBLE);
			if(mUid == LoginStatus.getLoginInfo().getUid())
				mTvEmpty.setText("你还没有任何动态");
			else
				mTvEmpty.setText("TA还没有任何动态");
		}
	}
	
	private class TopicAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			if(mDatas!=null)
				return mDatas.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			MyDllBean bean = mDatas.get(position);
			TopicHolder holder=null;
			if(convertView==null){
				holder=new TopicHolder();
				convertView=View.inflate(mContext, R.layout.channel_topic_list_item, null);
				holder.ivPortrait=(CircleImageView) convertView.findViewById(R.id.channel_topic_iv_portrait);
				holder.tvNickName=(TextView) convertView.findViewById(R.id.channel_topic_tv_nickname);
				holder.tvTime=(TextView) convertView.findViewById(R.id.channel_topic_tv_time);
				holder.tvFloor=(TextView) convertView.findViewById(R.id.channel_topic_tv_floor);
				holder.tvViewNum=(TextView) convertView.findViewById(R.id.channel_topic_tv_viewnum);
				holder.tvCont=(TextView) convertView.findViewById(R.id.channel_topic_tv_cont);
				holder.ivCollect=(ImageView) convertView.findViewById(R.id.channel_topic_iv_collect);
				holder.ivComment=(ImageView) convertView.findViewById(R.id.channel_topic_iv_comment);
				holder.ivLike=(ImageView) convertView.findViewById(R.id.channel_topic_iv_like);
				holder.ivMore=(ImageView) convertView.findViewById(R.id.channel_topic_iv_more);
				holder.ivDelete=(ImageView) convertView.findViewById(R.id.channel_topic_iv_del);
				holder.tvAddone=(TextView) convertView.findViewById(R.id.channel_tv_like_addone);
				holder.tvLikeCount=(TextView) convertView.findViewById(R.id.channel_topic_tv_like_count);
				holder.llContainer=(TopicPicContainer) convertView.findViewById(R.id.channel_topic_pic_ll_container);
				holder.mOperationContainer=(RelativeLayout) convertView.findViewById(R.id.channel_topic_operation_container);
				holder.mRadio = (RecoderFrameLayout) convertView.findViewById(R.id.radio);
				convertView.setTag(holder);
			}else{
				holder=(TopicHolder) convertView.getTag();
			}
//			holder.mOperationContainer.setVisibility(View.GONE);
			
			
			//判断是否是语音
			if (!TextUtils.isEmpty(bean.getAudio())) {
				holder.mRadio.setVisibility(View.VISIBLE);
				holder.tvCont.setVisibility(View.GONE);
				holder.mRadio.setData(getAudioDataBean(bean));
			}else{
				holder.mRadio.setVisibility(View.GONE);
				holder.tvCont.setVisibility(View.VISIBLE);
			}
			
			
			createClickListener(holder.ivCollect,position,holder.tvAddone,holder.tvLikeCount);
			createClickListener(holder.ivComment,position,holder.tvAddone,holder.tvLikeCount);
			createClickListener(holder.ivLike,position,holder.tvAddone,holder.tvLikeCount);
			createClickListener(holder.ivMore,position,holder.tvAddone,holder.tvLikeCount);
			createClickListener(holder.ivDelete,position,holder.tvAddone,holder.tvLikeCount);
			
			//自己的帖子，回复按钮变成删除，没有“更多”功能
			if(bean.getUid()==LoginStatus.getLoginInfo().getUid()){
				holder.ivComment.setVisibility(View.GONE);
				holder.ivDelete.setVisibility(View.VISIBLE);
				holder.ivMore.setVisibility(View.GONE);
			}else{
				holder.ivComment.setVisibility(View.VISIBLE);
				holder.ivDelete.setVisibility(View.GONE);
				holder.ivMore.setVisibility(View.VISIBLE);
			}
			
			
			//检查该登录用户的本地头像是否与服务器返回一致(服务端做了缓存)
			String portrait=bean.getPortrait();
			if(bean.getUid()==LoginStatus.getLoginInfo().getUid()){
				String localPortrait=sharedPreferencesHelper.getStringValue(LoginUserInfoBean.PORTRAIT);
				if(localPortrait!=null && !localPortrait.equals(portrait))
					portrait=localPortrait;
			}
			//同上
			String nickName=bean.getNickname();
			if(bean.getUid()==LoginStatus.getLoginInfo().getUid()){
				String localNickName=sharedPreferencesHelper.getStringValue(LoginUserInfoBean.NICKNAME);
				if(localNickName!=null && !localNickName.equals(nickName))
					nickName=localNickName;
					
			}
			
			ImageLoader.getInstance().displayImage(portrait, holder.ivPortrait, options);
			holder.tvNickName.setText(nickName);
			holder.tvTime.setText(VpDateUtils.getStandardDate(bean.getCreate_time()));
			
			holder.tvViewNum.setText(bean.getView_num()+"人看过");
			holder.tvFloor.setVisibility(View.GONE);
			holder.tvCont.setVisibility((bean.getCont()==null || "".equals(bean.getCont())?View.GONE:View.VISIBLE));		
			holder.tvCont.setText(bean.getCont());
			
			holder.llContainer.setDatas(bean.getPics());
			//收藏,点赞
			holder.ivCollect.setImageResource(bean.getIs_fav()==1?R.drawable.channel_topic_collected:R.drawable.channel_topic_collect);
			holder.ivLike.setImageResource(bean.getIs_like()==1?R.drawable.channel_topic_likeed:R.drawable.channel_topic_like);
			if(bean.getLike_num()>0){
				holder.tvLikeCount.setVisibility(View.VISIBLE);
				holder.tvLikeCount.setText("("+bean.getLike_num()+")");
			}else{
				holder.tvLikeCount.setVisibility(View.GONE);
			}
			
			return convertView;
		
		}

		
	}
	
	private FellHelpBeanAudiosBean getAudioDataBean(MyDllBean dllBean) {
		FellHelpBeanAudiosBean bean = new FellHelpBeanAudiosBean();
		bean.title = dllBean.getAudio_title();
		bean.url = dllBean.getAudio();
		return bean;
	}
	
	class TopicHolder{
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
		public RelativeLayout mOperationContainer;
		public RecoderFrameLayout mRadio;
	}

	public void createPicClickListener(ImageView imageView, final ArrayList<String> pics,final int position) {
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(mContext,ShowImagesViewPagerActivity.class);
				intent.putStringArrayListExtra(ShowImagesViewPagerActivity.IMAGES, pics);
				intent.putExtra(ShowImagesViewPagerActivity.POSITION, position);
				startActivity(intent);
				
			}
		});
	}
	
	/**
	 * 评论,点赞,收藏，更多
	 * @param ivCollect
	 * @param position
	 */
	public void createClickListener(final ImageView imageView, final int position,final TextView tvAddOne,final TextView tvLikeCount) {
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				currentClickPosition=position;
				
				MyDllBean myDllBean = mDatas.get(position);
				switch (imageView.getId()) {
				case R.id.channel_topic_iv_collect:
					//收藏
					//判断当前主题是否收藏
					int isFav = mDatas.get(currentClickPosition).getIs_fav();
					if(0==isFav){//未收藏
						attentionOperate(OPERATER_COLLECT,imageView,null,null);
					}else{
						attentionOperate(OPERATER_CANCELCOLLECT,imageView,null,null);
					}
					break;
				case R.id.channel_topic_iv_comment:
					//回复
					Intent replyIntent=new Intent(MyDLLActivity.this, ChannelTopicReplyActivity.class);
					replyIntent.putExtra(ChannelTopicReplyActivity.TOPIC_ID, myDllBean.getTopic_id());
					replyIntent.putExtra(ChannelTopicReplyActivity.TOPIC_RID, myDllBean.getId());
					replyIntent.putExtra(ChannelTopicReplyActivity.TOPIC_UID,myDllBean.getUid());
					replyIntent.putExtra(ChannelTopicReplyActivity.TOPIC_PORTRAIT, myDllBean.getPortrait());
					replyIntent.putExtra(ChannelTopicReplyActivity.TOPIC_NAME, myDllBean.getNickname());
					replyIntent.putExtra(ChannelTopicReplyActivity.TOPIC_CONT, myDllBean.getCont());
					replyIntent.putExtra(ChannelTopicReplyActivity.TOPIC_TIME, VpDateUtils.getStandardDate(myDllBean.getCreate_time()));
					replyIntent.putExtra(ChannelTopicReplyActivity.TOPIC_PIC, (myDllBean.getPics()==null || myDllBean.getPics().size()==0)?false:true);
					
					//javabean带过去
					if (!TextUtils.isEmpty(myDllBean.getAudio())) {
						replyIntent.putExtra("audio", getAudioDataBean(myDllBean));
					}
					startActivity(replyIntent);
					break;
				case R.id.channel_topic_iv_like:
					//判断当前主题是否已赞
					int like = mDatas.get(currentClickPosition).getIs_like();
					if(0==like){//未赞
						attentionOperate(OPERATER_LIKE,imageView,tvAddOne,tvLikeCount);
					}
					
					break;
				case R.id.channel_topic_iv_more:
					//更多
					MoreDialogFragment editNameDialog = new MoreDialogFragment(new MorePopItemClickListener() {
						
						@Override
						public void onMoreItemClick(View v) {
							switch (v.getId()) {
							case R.id.channel_topic_more_attention:        
								attentionOperate(OPERATER_ATTENTION,null,null,null);		
								break;
							case R.id.channel_topic_more_chat:  
								chatOperater();
								break;
							case R.id.channel_topic_more_report:  
								MyDllBean myDllBean = mDatas.get(currentClickPosition);
								ReportBean bean=new ReportBean(myDllBean.getUid(), myDllBean.getNickname(), null, null, 1, myDllBean.getId());
								MoreReportDialogFragment reportDialog = new MoreReportDialogFragment(MyDLLActivity.this,bean);  
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
					//点击头像
					if (currentUid == LoginStatus.getLoginInfo().getUid()) {
						Intent intent = new Intent(UIUtils.getContext(), MyCenterActivity.class);
						startActivity(intent);
					}else{
						Intent intent = new Intent(UIUtils.getContext(), UserIndexActivity.class);
						intent.putExtra(UserIndexActivity.KEY_UID,currentUid);
						startActivity(intent);
					}
					break;
					
				case R.id.channel_topic_iv_del:
					//删除
					deleteConfirmOperation();
					break;
				default:
					break;
				}
				
			}
		});
		
	}
	
	 
	private void chatOperater() {
		MyDllBean myDllBean = mDatas.get(this.currentClickPosition);
		Intent chatIntent = new Intent(this,PrivateChatActivity.class);
		 chatIntent.putExtra(PrivateChatActivity.CHAT_USER_ID, myDllBean.getUid());
		 chatIntent.putExtra(PrivateChatActivity.CHAT_USER_NAME,myDllBean.getNickname());
		 chatIntent.putExtra(PrivateChatActivity.CHAT_USER_HEAD_IMAGE, myDllBean.getPortrait());
		 chatIntent.putExtra(PrivateChatActivity.CHAT_XMPP_USER, myDllBean.getXmpp_user());
		 startActivity(chatIntent);
	}
	
	/**
	 * 删除帖子
	 */
	private void deleteConfirmOperation(){
		new IOSActionSheetDialog(this)
		.builder()
		.setTitle("确定删除吗？")
		.setCancelable(false)
		.setCanceledOnTouchOutside(false)
		.addSheetItem("确定", SheetItemColor.Green,
				new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						deleteOperation();
					}
	 })
	 .show();

	}
	
	private void deleteOperation(){
		String url =VpConstants.CHANNEL_FORUM_DELETE;;
		JSONObject data= new JSONObject();
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
							Toast.makeText(MyDLLActivity.this, "删除成功", Toast.LENGTH_LONG).show();
							mDatas.remove( mDatas.get(currentClickPosition));
							mAdapter.notifyDataSetChanged();
							setViewVisiable();
						} else {
							String message = json.getString(VpConstants.HttpKey.MSG);
							Toast.makeText(MyDLLActivity.this, message, Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					Toast.makeText(MyDLLActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
					
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();

		}
	}
	
	/**
	 * 关注,赞,收藏
	 */
	private void attentionOperate(final int operaterType,final ImageView imageView,final TextView tvAddOne,final TextView likeCount) {
		if(!isOperatorFinish)
			return;
		isOperatorFinish=false;
		String url =null;
		JSONObject data= new JSONObject();
		try {
			if(operaterType==OPERATER_ATTENTION){//关注
				url = VpConstants.CHANNEL_USER_CREATE_FOLLOW;
				data.put("from_uid", LoginStatus.getLoginInfo().getUid());
				data.put("to_uid", mDatas.get(this.currentClickPosition).getUid());
				
			}else if(operaterType==OPERATER_LIKE){//点赞
				url = VpConstants.CHANNEL_GENERAL_ADD_PRAISE;
				data.put("uid", LoginStatus.getLoginInfo().getUid());
				data.put("id", mDatas.get(this.currentClickPosition).getId());
				data.put("type", 3);//点赞类型1=长文推荐 2=PUA课堂 3=大家都在聊
			}else if(operaterType==OPERATER_COLLECT){//收藏
				url = VpConstants.CHANNEL_GENERAL_ADD_FAVORITE;//收藏
				data.put("uid", LoginStatus.getLoginInfo().getUid());
				data.put("id", mDatas.get(this.currentClickPosition).getId());
				data.put("type", 3);//点赞类型1=长文推荐 2=PUA课堂 3=大家都在聊
			}else if(operaterType==OPERATER_CANCELCOLLECT){
				url = VpConstants.My_DELETE_FAVORITE;//取消收藏
				data.put("uid", LoginStatus.getLoginInfo().getUid());
				data.put("id", mDatas.get(this.currentClickPosition).getId());
				data.put("type", 3);//点赞类型1=长文推荐 2=PUA课堂 3=大家都在聊
			}
			
			if(operaterType==OPERATER_LIKE || operaterType==OPERATER_ATTENTION){
				//关闭加载对话框
				mClient.setShowProgressDialog(false);
			}

			mClient.post(url, new RequestParams(), data.toString(), false, new AsyncHttpResponseHandler() {
				
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					isOperatorFinish=true;
					mClient.setShowProgressDialog(true);
					String result = ResultParseUtil.deAesResult(responseBody);
					try {
						JSONObject json = new JSONObject(result);
						String code = json.getString(VpConstants.HttpKey.CODE);

						if ("0".equals(code)) {// 返回成功
							if(operaterType==OPERATER_ATTENTION){//关注	

								Toast.makeText(MyDLLActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
							}else if(operaterType==OPERATER_LIKE){//赞
								tvAddOne.setVisibility(View.VISIBLE);
								tvAddOne.startAnimation(animation);
								new Handler().postDelayed(new Runnable(){
									public void run() {
										tvAddOne.setVisibility(View.GONE);
									} 
								}, 1000);
								imageView.setImageResource(R.drawable.channel_topic_likeed);
								int count=mDatas.get(currentClickPosition).getLike_num()+1;
								mDatas.get(currentClickPosition).setIs_like(1);
								mDatas.get(currentClickPosition).setLike_num(count);
								likeCount.setVisibility(View.VISIBLE);
								likeCount.setText("("+count+")");
								
							}else if(operaterType==OPERATER_COLLECT){//收藏
								Toast.makeText(MyDLLActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
								imageView.setImageResource(R.drawable.channel_topic_collected);
								mDatas.get(currentClickPosition).setIs_fav(1);
								mDatas.get(currentClickPosition).setFav_num(mDatas.get(currentClickPosition).getFav_num()+1);
//								mAdapter.notifyDataSetChanged();
							}else if(operaterType==OPERATER_CANCELCOLLECT){//取消收藏
								imageView.setImageResource(R.drawable.channel_topic_collect);
								mDatas.get(currentClickPosition).setIs_fav(0);
								mDatas.get(currentClickPosition).setFav_num(mDatas.get(currentClickPosition).getFav_num()-1);
							}

						} else {
							String message = json.getString(VpConstants.HttpKey.MSG);
							Toast.makeText(MyDLLActivity.this, message, Toast.LENGTH_LONG).show();
							mClient.setShowProgressDialog(true);
						}
					} catch (JSONException e) {
						mClient.setShowProgressDialog(true);
						e.printStackTrace();
					}
					
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					isOperatorFinish=true;
					mClient.setShowProgressDialog(true);
					Toast.makeText(MyDLLActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
					
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();

		}
		
	}
	
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// 下拉刷新
		if(mDatas!=null && mDatas.size()>0){			
			initDatas(mLimit, mDatas.get(0).getId(),1,false);
		}else{
			new Thread(){
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
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// 上拉加载更多
		if(mDatas!=null && mDatas.size()>0){			
			initDatas(mLimit, mDatas.get(mDatas.size()-1).getId(),0,true);
		}else{
			new Thread(){
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
	

}
