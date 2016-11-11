package com.vp.loveu.my.fragment;

import java.util.ArrayList;
import java.util.List;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.vp.loveu.base.VpFragment;
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
import com.vp.loveu.index.widget.RecoderFrameLayout;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.message.ui.PrivateChatActivity;
import com.vp.loveu.my.activity.CollectActivity;
import com.vp.loveu.my.activity.MyCenterActivity;
import com.vp.loveu.my.activity.UserIndexActivity;
import com.vp.loveu.my.bean.DllBean;
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
 * @时间2015年12月4日下午2:49:37
 * @功能 收藏动态的fragment
 * @作者 pzj
 */

public class DLLFragment extends VpFragment implements OnRefreshListener2<ListView> {
	private Context mContext;
	private VpHttpClient mClient;
	private PullToRefreshListView mPullListView;
	private ListView mListView;
	private TextView mTvEmptyView;
	private TopicAdapter mAdapter;
	private DisplayImageOptions options;
	private static final int mLimit=10;
	private int mPage=1;//分页页码
	private ArrayList<DllBean> mDatas;
	private SharedPreferencesHelper sharedPreferencesHelper;
	private static int OPERATER_ATTENTION=0;//关注
	private static int OPERATER_LIKE=1;//点赞
	private static int OPERATER_COLLECT=2;//收藏
	private static int OPERATER_CANCELCOLLECT=3;//取消收藏
	private ArrayList<DllBean> mRemoveBeanList=new ArrayList<DllBean>();//记录取消收藏的数据
	private int currentClickPosition;//记录当前点击的主题
	private android.view.animation.Animation animation;
	private boolean isOperatorFinish;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext=getActivity();
		this.mClient=new VpHttpClient(mContext);
		ScreenUtils.initScreen(getActivity());
		mClient.setShowProgressDialog(false);
		isOperatorFinish=true;
		sharedPreferencesHelper = SharedPreferencesHelper.getInstance(mContext);
		View view=inflater.inflate(R.layout.my_dll_activity, null);
		mPullListView=(PullToRefreshListView) view.findViewById(R.id.my_topic_list);
		mPullListView.setMode(Mode.BOTH);
		mListView = mPullListView.getRefreshableView();
		mTvEmptyView= (TextView) view.findViewById(R.id.public_empty_view);
		mDatas=new ArrayList<DllBean>();
		mAdapter=new TopicAdapter();
		mListView.setAdapter(mAdapter);
		mPullListView.setOnRefreshListener(this);
		animation=AnimationUtils.loadAnimation(mContext,R.anim.topic_like_nn);
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
		 
		initDatas(false);
		return view;
	}
	
	private void initDatas(final boolean isLoadMore){
		String url = VpConstants.COLLECT_ARTICLE_URL;
		RequestParams params = new RequestParams();
		params.put("limit", this.mLimit);
		params.put("page", this.mPage);
		params.put("type", 3);
		params.put("id", LoginStatus.getLoginInfo().getUid());
		mClient.setShowProgressDialog(false);
		mClient.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				mPullListView.onRefreshComplete();
				String result = ResultParseUtil.deAesResult(responseBody);
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString(VpConstants.HttpKey.CODE);
					if ("0".equals(code)) {// 返回成功
						String data=json.getString(VpConstants.HttpKey.DATA);
						JSONArray jsonData = new JSONArray(data);
						List<DllBean> currentList = DllBean.parseArrayJson(jsonData);
						if(currentList!=null && currentList.size()>0){
							mDatas.addAll(currentList);
							mAdapter.notifyDataSetChanged();
							mPage++;
							
						}else{
							if(isLoadMore)
								Toast.makeText(mContext, "没有更多数据了", Toast.LENGTH_SHORT).show();
						}
						
						setViewVisiable();
						
					} else {
						String message = json.getString(VpConstants.HttpKey.MSG);
						Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();

			}
		});
		

	
	}
	
	private void setViewVisiable(){
		if(mDatas!=null && mDatas.size()>0){
			mPullListView.setVisibility(View.VISIBLE);
			mTvEmptyView.setVisibility(View.GONE);
		}else{
			mPullListView.setVisibility(View.GONE);
			mTvEmptyView.setVisibility(View.VISIBLE);
		}
	}
	
	private class TopicAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			if(mDatas!=null && mDatas.size()>0){
				return mDatas.size();
			}else{
				return 0;
			}
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

			final DllBean dllBean = mDatas.get(position);
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
				holder.mRadio = (RecoderFrameLayout) convertView.findViewById(R.id.radio);
				holder.tvAddone=(TextView) convertView.findViewById(R.id.channel_tv_like_addone);
				holder.tvLikeCount=(TextView) convertView.findViewById(R.id.channel_topic_tv_like_count);
				holder.llContainer=(TopicPicContainer) convertView.findViewById(R.id.channel_topic_pic_ll_container);
				convertView.setTag(holder);
			}else{
				holder=(TopicHolder) convertView.getTag();
			}
			holder.tvFloor.setVisibility(View.GONE);
			holder.ivCollect.setImageResource(dllBean.getIs_fav()==1?R.drawable.channel_topic_collected:R.drawable.channel_topic_collect);
			
			//判断是否是语音
			if (!TextUtils.isEmpty(dllBean.getAudio())) {
				holder.mRadio.setVisibility(View.VISIBLE);
				holder.tvCont.setVisibility(View.GONE);
				holder.mRadio.setData(getAudioDataBean(dllBean));
			}else{
				holder.mRadio.setVisibility(View.GONE);
				holder.tvCont.setVisibility(View.VISIBLE);
			}
			
			
			createClickListener(holder.ivCollect,position,holder.tvAddone,holder.tvLikeCount);
			createClickListener(holder.ivComment,position,holder.tvAddone,holder.tvLikeCount);
			createClickListener(holder.ivLike,position,holder.tvAddone,holder.tvLikeCount);
			createClickListener(holder.ivMore,position,holder.tvAddone,holder.tvLikeCount);
			createClickListener(holder.ivPortrait,position,holder.tvAddone,holder.tvLikeCount);
			createClickListener(holder.ivDelete,position,holder.tvAddone,holder.tvLikeCount);
			
			//自己的帖子，回复按钮变成删除，没有“更多”功能
			if(dllBean.getAuthor_id()==LoginStatus.getLoginInfo().getUid()){
				holder.ivComment.setVisibility(View.GONE);
				holder.ivDelete.setVisibility(View.VISIBLE);
				holder.ivMore.setVisibility(View.GONE);
			}else{
				holder.ivComment.setVisibility(View.VISIBLE);
				holder.ivDelete.setVisibility(View.GONE);
				holder.ivMore.setVisibility(View.VISIBLE);
			}
			
			//检查该登录用户的本地头像是否与服务器返回一致(服务端做了缓存)
			String portrait=dllBean.getAuthor_portrait();
			if(dllBean.getAuthor_id()==LoginStatus.getLoginInfo().getUid()){
				String localPortrait=sharedPreferencesHelper.getStringValue(LoginUserInfoBean.PORTRAIT);
				if(localPortrait!=null && !localPortrait.equals(portrait))
					portrait=localPortrait;
			}
			//同上
			String nickName=dllBean.getAuthor_nickname();
			if(dllBean.getAuthor_id()==LoginStatus.getLoginInfo().getUid()){
				String localNickName=sharedPreferencesHelper.getStringValue(LoginUserInfoBean.NICKNAME);
				if(localNickName!=null && !localNickName.equals(nickName))
					nickName=localNickName;
					
			}
			
			
			ImageLoader.getInstance().displayImage(portrait, holder.ivPortrait, options);
			holder.tvNickName.setText(nickName);
			holder.tvTime.setText(VpDateUtils.getStandardDate(dllBean.getCreate_time()));
			
//			holder.tvViewNum.setText(dllBean.getView_num()+"人看过");
			holder.tvViewNum.setVisibility(View.GONE);
			holder.tvCont.setVisibility((dllBean.getSummary()==null || "".equals(dllBean.getSummary())?View.GONE:View.VISIBLE));		
			holder.tvCont.setText(dllBean.getSummary());
			
			holder.llContainer.setDatas(dllBean.getPics());
			
			//收藏,点赞
			holder.ivCollect.setImageResource(dllBean.getIs_fav()==1?R.drawable.channel_topic_collected:R.drawable.channel_topic_collect);
			holder.ivLike.setImageResource(dllBean.getIs_like()==1?R.drawable.channel_topic_likeed:R.drawable.channel_topic_like);
			if(dllBean.getLike_num()>0){
				holder.tvLikeCount.setVisibility(View.VISIBLE);
				holder.tvLikeCount.setText("("+dllBean.getLike_num()+")");
			}else{
				holder.tvLikeCount.setVisibility(View.GONE);
			}
			
			return convertView;
		
		}

		
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
		public RecoderFrameLayout mRadio;
	}
	
	private FellHelpBeanAudiosBean getAudioDataBean(DllBean dllBean) {
		FellHelpBeanAudiosBean bean = new FellHelpBeanAudiosBean();
		bean.title = dllBean.getAudio_title();
		bean.url = dllBean.getAudio();
		return bean;
	}
	
	/**
	 * 收藏or取消收藏
	 * @param postion
	 */
	protected void cancelAttention(final int operaterType,final int postion,final View view) {

		String url;
		if(operaterType==OPERATER_COLLECT)
			 url =VpConstants.CHANNEL_GENERAL_ADD_FAVORITE;
		else
			 url =VpConstants.My_DELETE_FAVORITE;
		JSONObject data= new JSONObject();
		try {
			data.put("uid", LoginStatus.getLoginInfo().getUid());
			data.put("id", mDatas.get(postion).getTarget_id());
			data.put("type", 3);//点赞类型1=长文推荐 2=PUA课堂 3=大家都在聊

			mClient.post(url, new RequestParams(), data.toString(), false, new AsyncHttpResponseHandler() {
				
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String result = ResultParseUtil.deAesResult(responseBody);
					try {
						JSONObject json = new JSONObject(result);
						String code = json.getString(VpConstants.HttpKey.CODE);

						if ("0".equals(code)) {
							if(operaterType==OPERATER_COLLECT){
								Toast.makeText(mContext, "收藏成功", Toast.LENGTH_SHORT).show();
								mDatas.get(postion).setIs_fav(1);
								mRemoveBeanList.remove(mDatas.get(postion));
								if(view instanceof ImageView){
									((ImageView)view).setImageResource(R.drawable.channel_topic_collected);
								}
							}else{
								//取消收藏
//								Toast.makeText(mContext, "取消收藏成功", Toast.LENGTH_SHORT).show();
								mDatas.get(postion).setIs_fav(0);
								mRemoveBeanList.add(mDatas.get(postion));
								if(view instanceof ImageView){
									((ImageView)view).setImageResource(R.drawable.channel_topic_collect);
								}
							}
						} else {
							String message = json.getString(VpConstants.HttpKey.MSG);
							Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
					
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();

		}
		
	
		
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
				
				DllBean dllBean = mDatas.get(position);
				switch (imageView.getId()) {
				case R.id.channel_topic_iv_collect:
					boolean isFav = dllBean.getIs_fav()==1;
					if(isFav){//收藏
						cancelAttention(OPERATER_CANCELCOLLECT,position,v);
					}else{
						cancelAttention(OPERATER_COLLECT,position,v);
					}
					break;
				case R.id.channel_topic_iv_comment:
					//回复
					Intent replyIntent=new Intent(mContext, ChannelTopicReplyActivity.class);
					replyIntent.putExtra(ChannelTopicReplyActivity.TOPIC_ID, dllBean.getPid());
					replyIntent.putExtra(ChannelTopicReplyActivity.TOPIC_RID, dllBean.getTarget_id());
					replyIntent.putExtra(ChannelTopicReplyActivity.TOPIC_UID, dllBean.getAuthor_id());
					replyIntent.putExtra(ChannelTopicReplyActivity.TOPIC_PORTRAIT, dllBean.getAuthor_portrait());
					replyIntent.putExtra(ChannelTopicReplyActivity.TOPIC_NAME, dllBean.getAuthor_nickname());
					replyIntent.putExtra(ChannelTopicReplyActivity.TOPIC_CONT, dllBean.getTitle());
					replyIntent.putExtra(ChannelTopicReplyActivity.TOPIC_TIME, VpDateUtils.getStandardDate(dllBean.getCreate_time()));
					replyIntent.putExtra(ChannelTopicReplyActivity.TOPIC_PIC, (dllBean.getPics()==null || dllBean.getPics().size()==0)?false:true);
					//javabean带过去
					if (!TextUtils.isEmpty(dllBean.getAudio())) {
						replyIntent.putExtra("audio", getAudioDataBean(dllBean));
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
								DllBean dllBean = mDatas.get(currentClickPosition);
								ReportBean bean=new ReportBean(dllBean.getAuthor_id(), dllBean.getAuthor_nickname(), null, null, 1, dllBean.getTarget_id());
								MoreReportDialogFragment reportDialog = new MoreReportDialogFragment(mContext,bean);  
								reportDialog.show(((CollectActivity)getActivity()).getSupportFragmentManager(), "report");
								break;
						        

							default:
								break;
							}
							
						}
					});  
			        editNameDialog.show(((CollectActivity)getActivity()).getSupportFragmentManager(), "more");
					break;
				case R.id.channel_topic_iv_portrait:
					int currentUid = dllBean.getAuthor_id();
					//点击头像
					if (currentUid == LoginStatus.getLoginInfo().getUid()) {
						Intent intent = new Intent(mContext, MyCenterActivity.class);
						mContext.startActivity(intent);
					}else{
						Intent intent = new Intent(UIUtils.getContext(), UserIndexActivity.class);
						intent.putExtra(UserIndexActivity.KEY_UID,currentUid);
						mContext.startActivity(intent);
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
	
	/**
	 * 关注,赞
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
				data.put("to_uid", mDatas.get(this.currentClickPosition).getAuthor_id());
				
			}else if(operaterType==OPERATER_LIKE){//点赞
				url = VpConstants.CHANNEL_GENERAL_ADD_PRAISE;
				data.put("uid", LoginStatus.getLoginInfo().getUid());
				data.put("id", mDatas.get(this.currentClickPosition).getId());
				data.put("type", 3);//点赞类型1=长文推荐 2=PUA课堂 3=大家都在聊
			}			
			//关闭加载对话框
			mClient.setShowProgressDialog(false);


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

								Toast.makeText(mContext, "关注成功", Toast.LENGTH_SHORT).show();
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
								
							}

						} else {
							String message = json.getString(VpConstants.HttpKey.MSG);
							Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
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
					Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
					
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();

		}
		
	}
	
	/**
	 * 删除帖子
	 */
	private void deleteConfirmOperation(){
		new IOSActionSheetDialog(mContext)
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
							Toast.makeText(mContext, "删除成功", Toast.LENGTH_LONG).show();
							mDatas.remove( mDatas.get(currentClickPosition));
							mAdapter.notifyDataSetChanged();
							setViewVisiable();
						} else {
							String message = json.getString(VpConstants.HttpKey.MSG);
							Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
					
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();

		}
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

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		new Thread(){
			public void run() {
				
				if(mAdapter!=null && mRemoveBeanList!=null && mRemoveBeanList.size()>0 && mDatas!=null){
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mPullListView.onRefreshComplete();
							mDatas.removeAll(mRemoveBeanList);
							mAdapter.notifyDataSetChanged();
							Toast.makeText(mContext, "刷新成功", Toast.LENGTH_SHORT).show();
							setViewVisiable();
						}
					});
				}else{
					SystemClock.sleep(1500);
					if (getActivity()!=null) {
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								mPullListView.onRefreshComplete();
							}
						});
					}
					
				}

			};
		}.start();

		
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		initDatas(true);
		
	}


	
	/**
	 * 私聊
	 */
	private void chatOperater() {
		DllBean dllBean = mDatas.get(this.currentClickPosition);
		Intent chatIntent = new Intent(mContext,PrivateChatActivity.class);
		 chatIntent.putExtra(PrivateChatActivity.CHAT_USER_ID, dllBean.getAuthor_id());
		 chatIntent.putExtra(PrivateChatActivity.CHAT_USER_NAME,dllBean.getAuthor_nickname());
		 chatIntent.putExtra(PrivateChatActivity.CHAT_USER_HEAD_IMAGE, dllBean.getAuthor_portrait());
		 chatIntent.putExtra(PrivateChatActivity.CHAT_XMPP_USER, dllBean.getXmpp_user());
		 startActivity(chatIntent);
	}
}
