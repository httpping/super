/**   
* @Title: MyFragment.java 
* @Package com.vp.loveu.my 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zeus   
* @date 2015-10-20 下午4:30:23 
* @version V1.0   
*/
package com.vp.loveu.channel.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.color;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
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
import com.vp.loveu.base.VpFragment;
import com.vp.loveu.channel.bean.ChannelHomeBean;
import com.vp.loveu.channel.bean.ChannelHomeBean.Radio;
import com.vp.loveu.channel.bean.ChannelHomeBean.Topic;
import com.vp.loveu.channel.bean.ChannelHomeBean.Video;
import com.vp.loveu.channel.bean.ChannelHomeBeanVo;
import com.vp.loveu.channel.db.RadioDbDao;
import com.vp.loveu.channel.db.RadioHistoryBean;
import com.vp.loveu.channel.ui.ChannelDetailActivity;
import com.vp.loveu.channel.ui.ChannelListActivity;
import com.vp.loveu.channel.ui.ChannelTopicListActivity;
import com.vp.loveu.channel.ui.VideoDetailActivity;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.util.Base64Utils;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.SharedPreferencesHelper;
import com.vp.loveu.util.UIUtils;

import cz.msebera.android.httpclient.Header;

/**
 *
 * 
 * @ClassName:
 * @Description:频道fragment
 * @author pzj
 * @date
 */
public class ChannelFragment extends VpFragment implements OnItemClickListener, OnRefreshListener2<ListView> {
	private View rootView;
	private PullToRefreshListView mPullListView;
	private ListView mChannelListView;
	private ChangeHomeAdapter mAdapter;
	private VpHttpClient mClient;
	private Context mContext;
	private static final String CACHE="ChannelFragmentCache";

	private static final int TYPE_TITLE_VIDEO = 0;// title
	private static final int TYPE_TITLE_RADIO = 1;// title
	private static final int TYPE_TITLE_TOPIC = 2;// title
	private static final int TYPE_VIDEO = 3;// video
	private static final int TYPE_RADIO = 4;// radio
	private static final int TYPE_TOPIC = 5;// chat
	private static final String[] titles={"新手视频","恋爱电台","大家都在聊"};

	private ArrayList<ChannelHomeBean> mDatas;
	private ArrayList<Video> videos;// 视频列表
	private ArrayList<Radio> radios;// 电台列表
	private ArrayList<Topic> topics;// 话题列表

	private SharedPreferencesHelper mSharedPreference;
	private static final String LAST_TIME = "last_time";
	private DisplayImageOptions options;
	
	private int mVedioItemCount=0;
	private int mVedioCurrentItemIndex=0;
	private int mLimit=5;
	private RadioDbDao mDao;
	
	private boolean isFirstLoadMore=true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.mContext = getActivity();
		rootView = inflater.inflate(R.layout.channel_fragment, null);
		mClient = new VpHttpClient(mContext);
		mClient.setShowProgressDialog(false);
		this.mDao=new RadioDbDao(mContext);
		mSharedPreference = SharedPreferencesHelper.getInstance(mContext);
		this.mPullListView = (PullToRefreshListView) rootView.findViewById(R.id.channel_listview);
		mPullListView.setMode(Mode.BOTH);
		mChannelListView=mPullListView.getRefreshableView();
		mPullListView.setOnRefreshListener(this);
		
		options = new DisplayImageOptions.Builder()
		         .showImageOnLoading(R.color.frenchgrey) // resource or
		        .showImageForEmptyUri(R.drawable.default_portrait) // resource or
		        .showImageOnFail(R.drawable.default_portrait) // resource or
		        .resetViewBeforeLoading(false) // default
		        .cacheInMemory(true) // default
		        .cacheOnDisk(true) // default
		        .bitmapConfig(Bitmap.Config.RGB_565)  
		        .considerExifParams(false) // default
		        .displayer(new SimpleBitmapDisplayer())
		        .build();
		
		this.mChannelListView.setOnItemClickListener(this);
		
		//
		//TextView headView=new TextView(mContext);
		///headView.setHeight((int)getResources().getDimension(R.dimen.public_title_height));
		//headView.setBackgroundColor(color.transparent);
		//mChannelListView.addHeaderView(headView);
		mAdapter = new ChangeHomeAdapter();
		this.mChannelListView.setAdapter(mAdapter);
		initDatas(false);
		this.isFirstLoadMore=true;
		return rootView;
	}
	
	
//	@Override
//	public void setUserVisibleHint(boolean isVisibleToUser) {
//		super.setUserVisibleHint(isVisibleToUser);
//		if (getUserVisibleHint() &&  getActivity()!=null) {//init页面
//			initDatas(false);
//		}
//		
//	}
	
	private void initDatas(final boolean isRefresh) {
		//获取缓存进行显示
		String historyResult = mSharedPreference.getStringValue(CACHE);
		if(historyResult!=null && !historyResult.isEmpty()){			
			handleResult(Base64Utils.decode(historyResult));
		}
		String url = VpConstants.CHANNEL_HOME;
		RequestParams params = new RequestParams();
		long lastTime = mSharedPreference.getLongValue(LAST_TIME);
		if (lastTime != 0) {
			params.add("time", lastTime + "");
		} else {
			params.add("time", System.currentTimeMillis() + "");
		}

		mClient.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				mPullListView.onRefreshComplete();
				String result = ResultParseUtil.deAesResult(responseBody);
				//进行缓存
				mSharedPreference.putStringValue(CACHE, Base64Utils.encode(result));
				handleResult(result);
				if(isRefresh)
					Toast.makeText(mContext, "刷新成功", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				mPullListView.onRefreshComplete();

			}
		});

	}
	
	/**
	 * 处理返回结果
	 * @param result
	 */
	private void handleResult(String result){
		try {
			JSONObject json = new JSONObject(result);
			String code = json.getString(VpConstants.HttpKey.CODE);

			if ("0".equals(code)) {// 返回成功
				JSONObject jsonData = json.getJSONObject(VpConstants.HttpKey.DATA);
				ChannelHomeBeanVo homeBean = ChannelHomeBeanVo.parseJson(jsonData.toString());
				videos = homeBean.getVideos();
				radios = homeBean.getRadios();
				topics = homeBean.getTopics();
				setViewDatas();
				mSharedPreference.putLongValue(LAST_TIME, System.currentTimeMillis());
			} else {
				String message = json.getString(VpConstants.HttpKey.MSG);
				Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected void setViewDatas() {
		mDatas = new ArrayList<ChannelHomeBean>();
		//视频列表
		ChannelHomeBean videoTitleBean=new ChannelHomeBean();
		videoTitleBean.setType(TYPE_TITLE_VIDEO);
		videoTitleBean.setName(titles[0]);
		mDatas.add(videoTitleBean);
		if(videos!=null){
			this.mVedioItemCount=videos.size()%2==0?videos.size()/2:videos.size()/2+1;
			ChannelHomeBean bean=null;
			for (int i=0;i<videos.size();i++) {
				Video video=videos.get(i);
				bean=new ChannelHomeBean();
				bean.setType(TYPE_VIDEO);
				bean.setCreate_time(video.getCreate_time());
				bean.setId(video.getId());
				bean.setLearn_num(video.getLearn_num());
				bean.setName(video.getName());
				bean.setPic(video.getPic());
				bean.setPrice(video.getPrice());
				bean.setVideo_num(video.getVideo_num());
				
				if(i<videos.size()-1){
					//获取下一条数据,填充到listview item右边部分
					video=videos.get(i+1);
//					bean=new ChannelHomeBean();
//					bean.setType(TYPE_VIDEO);
					bean.setCreate_time_right(video.getCreate_time());
					bean.setId_right(video.getId());
					bean.setLearn_num_right(video.getLearn_num());
					bean.setName_right(video.getName());
					bean.setPic_right(video.getPic());
					bean.setPrice_right(video.getPrice());
					bean.setVideo_num_right(video.getVideo_num());
					i++;
				}
				mDatas.add(bean);
			}
		}
		//电台列表
		ChannelHomeBean radioTitleBean=new ChannelHomeBean();
		radioTitleBean.setType(TYPE_TITLE_RADIO);
		radioTitleBean.setName(titles[1]);
		mDatas.add(radioTitleBean);
		if(radios!=null){
			ChannelHomeBean bean=null;
			for (Radio radio : radios) {
				bean=new ChannelHomeBean();
				bean.setType(TYPE_RADIO);
				bean.setId(radio.getId());
				bean.setName(radio.getName());
				bean.setNickname(radio.getNickname());
				bean.setUid(radio.getUid());
				bean.setOnline_num(radio.getOnline_num());
				bean.setUrl(radio.getUrl());
				bean.setCover(radio.getCover());
				mDatas.add(bean);
			}
		}
		//大家都在聊列表
		ChannelHomeBean topicTitleBean=new ChannelHomeBean();
		topicTitleBean.setType(TYPE_TITLE_TOPIC);
		topicTitleBean.setName(titles[2]);
		mDatas.add(topicTitleBean);
		if(topics!=null){
			ChannelHomeBean bean=null;
			for (Topic topic : topics) {
				bean=new ChannelHomeBean();
				bean.setType(TYPE_TOPIC);
				bean.setCont(topic.getCont());
				bean.setCreate_time(topic.getCreate_time());
				bean.setId(topic.getId());
				bean.setJoin_num(topic.getJoin_num());
				bean.setName(topic.getName());
				bean.setUnread_num(topic.getUnread_num());
				mDatas.add(bean);
			}
		}
		
		//刷新UI
		this.mAdapter.notifyDataSetChanged();
		
	}

	private class ChangeHomeAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			if (mDatas != null && mDatas.size() > 0)
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
		public int getItemViewType(int position) {
			return mDatas.get(position).getType();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ChannelHomeBean bean = mDatas.get(position);
			int itemViewType = getItemViewType(position);
			if (itemViewType ==TYPE_TITLE_RADIO || itemViewType ==TYPE_TITLE_VIDEO || itemViewType ==TYPE_TITLE_TOPIC) {
				// 分类标题
				convertView = View.inflate(mContext, R.layout.channel_index_item_title_view, null);
				TextView tv = (TextView) convertView.findViewById(R.id.channel_item_tv_name);
				if(itemViewType ==TYPE_TITLE_RADIO){
					RelativeLayout ivMore = (RelativeLayout) convertView.findViewById(R.id.channel_item_iv_more);
					ivMore.setVisibility(View.VISIBLE);
					convertView.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							//查看更多电台
							Intent intent=new Intent(mContext, ChannelListActivity.class);
							startActivity(intent);
							
						}
					});
				}
				tv.setText(bean.getName());
			} else if (itemViewType == TYPE_VIDEO) {
				mVedioCurrentItemIndex++;
				//新手视频item
				VideoHolder videoHolder=null;
				if(convertView==null || !(convertView.getTag() instanceof VideoHolder)){
					videoHolder=new VideoHolder();
					convertView = View.inflate(mContext, R.layout.channel_index_item_vedio_view, null);
					videoHolder.ivPic1=(ImageView) convertView.findViewById(R.id.channel_item_iv_icon1);
					videoHolder.ivPic2=(ImageView) convertView.findViewById(R.id.channel_item_iv_icon2);
					videoHolder.ivFree1=(ImageView) convertView.findViewById(R.id.channel_item_iv_free1);
					videoHolder.ivFree2=(ImageView) convertView.findViewById(R.id.channel_item_iv_free2);
					videoHolder.tvDesc1=(TextView) convertView.findViewById(R.id.channel_item_iv_desc1);
					videoHolder.tvDesc2=(TextView) convertView.findViewById(R.id.channel_item_iv_desc2);
					videoHolder.mVideoRight = (LinearLayout) convertView.findViewById(R.id.video_right);
					convertView.setTag(videoHolder);
				}else{
					videoHolder=(VideoHolder) convertView.getTag();
				}
				
				ImageLoader.getInstance().displayImage(bean.getPic(), videoHolder.ivPic1, options);
				videoHolder.ivFree1.setVisibility(bean.getPrice()==0?View.VISIBLE:View.GONE);
				//videoHolder.ivFree1.setVisibility(View.GONE);
				videoHolder.tvDesc1.setText(bean.getName());
				createImageClickListener(videoHolder.ivPic1,bean.getId());
				if(bean.getId_right()!=0){//右边有视频
					videoHolder.mVideoRight.setVisibility(View.VISIBLE);
					videoHolder.mVideoRight.setEnabled(true);
					ImageLoader.getInstance().displayImage(bean.getPic_right(), videoHolder.ivPic2, options);
					videoHolder.ivFree2.setVisibility(bean.getPrice_right()==0?View.VISIBLE:View.GONE);
					//videoHolder.ivFree2.setVisibility(View.GONE);
					videoHolder.tvDesc2.setText(bean.getName_right());
					createImageClickListener(videoHolder.ivPic2,bean.getId_right());
				}else{
					videoHolder.mVideoRight.setVisibility(View.INVISIBLE);
					videoHolder.mVideoRight.setEnabled(false);
				}
				
//				// 最后一个视频，需要手动设置marginBottom
//				if (mVedioCurrentItemIndex == mVedioItemCount) {
//					LinearLayout layout = (LinearLayout) convertView;
//					layout.setPadding(0, 0, 0, UIUtils.dp2px(15));
//					convertView = layout;
//				}
				
				//如果下一条目为titleItem，设置padding
				try {
					ChannelHomeBean nextBean = mDatas.get(position+1);
					if((nextBean.getType()==TYPE_TITLE_RADIO) || (nextBean.getType()==TYPE_TITLE_VIDEO) ||(nextBean.getType()==TYPE_TITLE_RADIO)){
						LinearLayout layout = (LinearLayout) convertView;
						layout.setPadding(0, 0, 0, UIUtils.dp2px(15));
						convertView = layout;
					}
				} catch (Exception e) {
					
				}finally{
					if(position==mDatas.size()-1){
						LinearLayout layout = (LinearLayout) convertView;
						layout.setPadding(0, 0, 0, UIUtils.dp2px(15));
						convertView = layout;
					}
				}
				
			} else if (itemViewType == TYPE_RADIO) {
				//恋爱电台item
				RadioHolder radioHolder=null;
				if(convertView==null || !(convertView.getTag() instanceof RadioHolder)){
					radioHolder=new RadioHolder();
					convertView = View.inflate(mContext, R.layout.channel_index_item_radio_view, null);
					radioHolder.tvTitle=(TextView) convertView.findViewById(R.id.channel_radio_title);
					radioHolder.tvListener=(TextView) convertView.findViewById(R.id.channel_radio_listener);
					radioHolder.tvTutor=(TextView) convertView.findViewById(R.id.channel_radio_tutor);
					radioHolder.mIconLeft=(ImageView) convertView.findViewById(R.id.channel_radio_icon_left);
					radioHolder.mIconRight=(ImageView) convertView.findViewById(R.id.channel_radio_icon_right);
					convertView.setTag(radioHolder);
				}else{
					radioHolder=(RadioHolder) convertView.getTag();
				}
				radioHolder.tvTitle.setText(bean.getName());
				radioHolder.tvListener.setText(bean.getOnline_num()+"人在收听");
				radioHolder.tvTutor.setText("导师 :"+bean.getNickname());
				RadioHistoryBean historyBean = mDao.findRadioHistory(LoginStatus.getLoginInfo().getUid(), bean.getId());
				if(historyBean!=null){
					radioHolder.mIconLeft.setImageResource(R.drawable.icon_love_radio_listenered);
					mDatas.get(position).setListened(true);
				}else{				
					radioHolder.mIconLeft.setImageResource(R.drawable.icon_love_radio);
				}
				
				
			} else if (itemViewType == TYPE_TOPIC) {
				//话题item
				TopicHolder topicHolder=null;
				if(convertView==null || !(convertView.getTag() instanceof TopicHolder)){
					topicHolder=new TopicHolder();
					convertView = View.inflate(mContext, R.layout.channel_index_item_chat_view, null);
					topicHolder.tvTitle=(TextView) convertView.findViewById(R.id.channel_topic_title);
					topicHolder.tvListener=(TextView) convertView.findViewById(R.id.channel_topic_listener);
					convertView.setTag(topicHolder);
				}else{
					topicHolder=(TopicHolder) convertView.getTag();
				}
				topicHolder.tvTitle.setText(bean.getName());
				topicHolder.tvListener.setText(bean.getJoin_num()+"人参与");
			}
			return convertView;
			
			
		}

	}
	
	class VideoHolder{
		public ImageView ivPic1;
		public ImageView ivFree1;
		public ImageView ivPic2;
		public ImageView ivFree2;
		public TextView tvDesc1;
		public LinearLayout mVideoRight;
		public TextView tvDesc2;
	}
	
	class RadioHolder{
		public TextView tvTitle;
		public TextView tvListener;
		public TextView tvTutor;
		public ImageView mIconLeft;
		public ImageView mIconRight;
	}
	class TopicHolder{
		public TextView tvTitle;
		public TextView tvListener;
	}
	/**
	 * listview item click event
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		position=position-mChannelListView.getHeaderViewsCount();
		ChannelHomeBean channelHomeBean = null;
		try {
			channelHomeBean = mDatas.get(position);
		} catch (Exception e) {
			return;
		}
		if(TYPE_RADIO==channelHomeBean.getType()){
			//电台
			Intent intent=new Intent(mContext,ChannelDetailActivity.class);
			intent.putExtra(ChannelDetailActivity.RADIOID, mDatas.get(position).getId());
			intent.putExtra(ChannelDetailActivity.CHANNEL_NAME, mDatas.get(position).getName());
			intent.putExtra(ChannelDetailActivity.TUTOR_NAME, mDatas.get(position).getNickname());			
			RadioHistoryBean historyBean = mDao.findRadioHistory(LoginStatus.getLoginInfo().getUid(),mDatas.get(position).getId());
			if(historyBean!=null){
				intent.putExtra(ChannelDetailActivity.IS_LISTENED, true);
				intent.putExtra(ChannelDetailActivity.CURRENTPOSTION,historyBean.getCurrentPosition());
				intent.putExtra(ChannelDetailActivity.TOTALPOSTION,historyBean.getTotalPosition());
			}
			
			boolean result = insertHistoryData(LoginStatus.getLoginInfo().getUid(),mDatas.get(position).getId(),mDatas.get(position).getUrl());
			if(result){
				mDatas.get(position).setListened(true);
				ImageView iv=(ImageView) view.findViewById(R.id.channel_radio_icon_left);
				iv.setImageResource(R.drawable.icon_love_radio_listenered);
			}
			startActivity(intent);
		}else if(TYPE_TOPIC==mDatas.get(position).getType()){
			//大家都在聊
			Intent topIntent=new Intent(mContext,ChannelTopicListActivity.class);
			topIntent.putExtra(ChannelTopicListActivity.TOPICID, mDatas.get(position).getId());
			topIntent.putExtra(ChannelTopicListActivity.TOPICNAME, mDatas.get(position).getName());
			startActivity(topIntent);
		}
		
	}
	
	private boolean insertHistoryData(int uid,int rid,String url) {
		boolean flag=true;
		if(mDao.findRadioHistory(uid,rid)==null){
			RadioHistoryBean bean=new RadioHistoryBean();
			bean.setUid(uid);
			bean.setRid(rid);
			bean.setUrl(url);
			flag = mDao.insert(bean);
		}
		return flag;
	}

	public void createImageClickListener(ImageView ivPic1, final int id) {
		ivPic1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent =new Intent(mContext, VideoDetailActivity.class);
				intent.putExtra(VideoDetailActivity.VIDEO_ID, id);
				startActivity(intent);
			}
		});
		
	}


	/**
	 * 加载更多话题
	 */
	private void loadMoreDatas() {
		String url = VpConstants.CHANNEL_FORUM_LIST;
		RequestParams params = new RequestParams();
		params.put("limit", this.mLimit);
		params.put("dir", 0);//获取数据的方向，1=是正向，即获取更新的信息，0=是反向，即获取更旧的信息
		if(isFirstLoadMore){
			params.put("index", 0);//获取记录的偏移量，当dir为1时则为上次返回记录的第一信息ID，当dir=0时为上次返回记录最后一条的ID
			isFirstLoadMore=false;
		}else {
			params.put("index", (topics==null || topics.size()==0)?0:topics.get(topics.size()-1).getId());
		}
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
						ArrayList<Topic> currentList = new ChannelHomeBean().new Topic().parseArrayJson(jsonData);
						if(currentList!=null && currentList.size()>0){
							topics.removeAll(currentList);
							topics.addAll(currentList);
							ChannelHomeBean bean=null;
							for (Topic topic : currentList) {
								bean=new ChannelHomeBean();
								bean.setType(TYPE_TOPIC);
								bean.setCont(topic.getCont());
								bean.setCreate_time(topic.getCreate_time());
								bean.setId(topic.getId());
								bean.setJoin_num(topic.getJoin_num());
								bean.setName(topic.getName());
								bean.setUnread_num(topic.getUnread_num());
								mDatas.add(bean);
							}
							mAdapter.notifyDataSetChanged();
							
						}else{
							Toast.makeText(mContext, "没有更多数据了", Toast.LENGTH_SHORT).show();
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
				mPullListView.onRefreshComplete();

			}
		});

	
		
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		if(mDatas!=null && mDatas.size()>0){
			initDatas(true);
			isFirstLoadMore=true;
		}
		
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if(topics==null || topics.size()==0){
			new Thread(){
				public void run() {
					SystemClock.sleep(1500);	
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mPullListView.onRefreshComplete();
						}
					});

				};
			}.start();
		}else{
			loadMoreDatas();
		}
		
	}
	
}
