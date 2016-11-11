package com.vp.loveu.message.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.vp.loveu.base.VpFragment;
import com.vp.loveu.channel.bean.TopicBean;
import com.vp.loveu.channel.bean.TopicBean.Source;
import com.vp.loveu.channel.widget.TopicPicContainer;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.bean.FellHelpBean.FellHelpBeanAudiosBean;
import com.vp.loveu.index.bean.MySeekHelpBean.MySeekAudioBean;
import com.vp.loveu.index.widget.RecoderFrameLayout;
import com.vp.loveu.message.ui.CommenNoticListActivity;
import com.vp.loveu.my.bean.DllBean;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.ScreenUtils;
import com.vp.loveu.util.SharedPreferencesHelper;
import com.vp.loveu.util.VpDateUtils;
import com.vp.loveu.widget.CircleImageView;

import cz.msebera.android.httpclient.Header;

/**
 * @author：pzj
 * @date: 2015年12月4日 下午5:48:12
 * @Description:回复列表
 */
public class CommentFragment extends VpFragment implements OnRefreshListener<ListView> {
	private PullToRefreshListView mPullListView;
	private ListView mListView;
	private CommentAdapter mAdapter;
	private Context mContext;
	private DisplayImageOptions options;
	private int mLimit=10;
	private ArrayList<TopicBean> mDatas;
	private VpHttpClient mClient;
	private int mAlreadyReadCommentId;
	public SharedPreferencesHelper msp;
	public static final String ALREADYREAD_COMMENT_ID="alreadyread_comment_id";//已读评论的ID
	private int mUnReadCommentCount;
	private TextView mTvEmpty;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext=getActivity();
		mClient=new VpHttpClient(mContext);
		ScreenUtils.initScreen(getActivity());
		mDatas=new ArrayList<TopicBean>();
		msp=SharedPreferencesHelper.getInstance(mContext);
		View v=View.inflate(mContext, R.layout.message_comment_fragment, null);
		mTvEmpty=(TextView) v.findViewById(R.id.public_empty_view);
		mPullListView=(PullToRefreshListView) v.findViewById(R.id.message_comment_listview);
		mPullListView.setMode(Mode.PULL_FROM_END);//向上拉刷新
		mListView=mPullListView.getRefreshableView();
		mPullListView.setOnRefreshListener(this);
		mAdapter = new CommentAdapter();
		mListView.setAdapter(mAdapter);
		mAlreadyReadCommentId=msp.getIntegerValue(ALREADYREAD_COMMENT_ID);
		options = new DisplayImageOptions.Builder()
		         .showImageOnLoading(R.drawable.default_image_loading_fail) // resource or
		        .showImageForEmptyUri(R.drawable.default_image_loading_fail) // resource or
		        .showImageOnFail(R.drawable.default_image_loading_fail) // resource or
		        .resetViewBeforeLoading(false) // default
		        .cacheInMemory(true) // default
		        .cacheOnDisk(true) // default
		        .bitmapConfig(Bitmap.Config.RGB_565)  
		        .considerExifParams(false) // default
		        .displayer(new SimpleBitmapDisplayer())
		        .build();
		
		initDatas(this.mLimit , 0, 1);
		return v;
	}
	



	private void initDatas(int limit,int index,final int dir) {
		String url = VpConstants.MESSAGE_USER_REPLY_LIST;
		RequestParams params = new RequestParams();
		params.put("limit", this.mLimit);
		params.put("dir", dir);//获取数据的方向，1=是正向，即获取更新的信息，0=是反向，即获取更旧的信息
		params.put("index", index);//获取记录的偏移量，当dir为1时则为上次返回记录的第一信息ID，当dir=0时为上次返回记录最后一条的ID
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
						JSONArray jsonData =new JSONArray(json.getString(VpConstants.HttpKey.DATA));
						ArrayList<TopicBean> currList = TopicBean.parseArrayJson(jsonData,mAlreadyReadCommentId);
						if(currList!=null && currList.size()>0){
							if(dir==0){
								mDatas.addAll(currList);
							}else{
								currList.addAll(mDatas);
								mDatas=currList;
							}
							msp.putIntegerValue(ALREADYREAD_COMMENT_ID, mDatas.get(0).getId());
							calculateUnReadCount();
							mAdapter.notifyDataSetChanged();							
						}
						
						if(mDatas!=null && mDatas.size()>0){
							mPullListView.setVisibility(View.VISIBLE);
							mTvEmpty.setVisibility(View.GONE);
						}else{
							mPullListView.setVisibility(View.GONE);
							mTvEmpty.setVisibility(View.VISIBLE);
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
	
	/**
	 * 计算未读评论数量
	 */
	private void calculateUnReadCount(){
		mUnReadCommentCount=0;
		if(mDatas!=null && mDatas.size()>0){
			for (TopicBean bean:mDatas) {
				if(!bean.isRead())
					mUnReadCommentCount++;
			}
			
			
		}
		if(mUnReadCommentCount>0){
			((CommenNoticListActivity)getActivity()).updateTabPointStatus(0, true);
		}
	}


	private class CommentAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			if(mDatas!=null && mDatas.size()>0)
				return mDatas.size()+1;
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
			if(position==mUnReadCommentCount){//以往回复title
				convertView = View.inflate(mContext, R.layout.default_sub_title_item_view, null);
				((TextView)convertView.findViewById(R.id.channel_item_tv_name)).setText("以往回复");
			}else{
				if(position>mUnReadCommentCount)
					position=position-1;
				TopicBean topicBean = mDatas.get(position);
				CommentHolder commentHolder=null;
				if(convertView==null || !(convertView.getTag() instanceof CommentHolder)){
					commentHolder=new CommentHolder();
					convertView=View.inflate(mContext, R.layout.message_comment_list_item, null);
					commentHolder.ivPortrait=(CircleImageView) convertView.findViewById(R.id.message_comment_portrait);
					commentHolder.tvNickName=(TextView) convertView.findViewById(R.id.message_comment_nickname);
					commentHolder.tvTime=(TextView) convertView.findViewById(R.id.message_comment_time);
					commentHolder.tvCont=(TextView) convertView.findViewById(R.id.message_comment_cont);
					commentHolder.tvSrcCont=(TextView) convertView.findViewById(R.id.message_comment_src_cont);
					commentHolder.llContainer=(TopicPicContainer) convertView.findViewById(R.id.message_comment_pic_ll_container);
					commentHolder.ivDefaultIcon=(ImageView) convertView.findViewById(R.id.message_default_icon);
					commentHolder.mRadio = (RecoderFrameLayout) convertView.findViewById(R.id.radio);
					commentHolder.messageRadio = (RecoderFrameLayout) convertView.findViewById(R.id.message_radio);
					convertView.setTag(commentHolder);
				}else{
					commentHolder=(CommentHolder) convertView.getTag();
				}
				
				ImageLoader.getInstance().displayImage(topicBean.getPortrait(), commentHolder.ivPortrait, options);
				commentHolder.tvNickName.setText(topicBean.getNickname());
				commentHolder.tvTime.setText(VpDateUtils.getStandardDate(topicBean.getCreate_time()));
				commentHolder.tvCont.setText(topicBean.getCont());
				commentHolder.tvCont.setVisibility((topicBean.getCont()==null || "".equals(topicBean.getCont()))?View.GONE:View.VISIBLE);
				
				//图片
				commentHolder.llContainer.setDatas(topicBean.getPics());
				//语音
				commentHolder.messageRadio.setVisibility(TextUtils.isEmpty(topicBean.getAudio()) ? View.GONE : View.VISIBLE);
				commentHolder.messageRadio.setData(getAudioDataBean(topicBean));
				
				if(topicBean.getSource()!=null){
					//被回复的内容
					if(topicBean.getSource().getCont()==null || "".equals(topicBean.getSource().getCont())){
						commentHolder.tvSrcCont.setVisibility(View.GONE);
						
						if (!TextUtils.isEmpty(topicBean.getSource().getAudio())) {//音频不为空的话
							commentHolder.mRadio.setVisibility(View.VISIBLE);
							commentHolder.mRadio.setData(getAudioDataBeanForSource(topicBean.getSource()));
						}else{
							commentHolder.mRadio.setVisibility(View.GONE);
							commentHolder.ivDefaultIcon.setVisibility(View.VISIBLE);
						}
					}else{
						commentHolder.tvSrcCont.setText(topicBean.getSource().getCont());
						commentHolder.tvSrcCont.setVisibility(View.VISIBLE);
						commentHolder.ivDefaultIcon.setVisibility(View.GONE);
						commentHolder.mRadio.setVisibility(View.GONE);
					}
				}else{				
					commentHolder.tvSrcCont.setText("");
				}
			}
			return convertView;
		}
		
	}
	
	private FellHelpBeanAudiosBean getAudioDataBean(TopicBean dllBean) {
		FellHelpBeanAudiosBean bean = new FellHelpBeanAudiosBean();
		bean.title = dllBean.getAudio_title();
		bean.url = dllBean.getAudio();
		return bean;
	}
	
	public FellHelpBeanAudiosBean getAudioDataBeanForSource(Source source) {
		FellHelpBeanAudiosBean bean = new FellHelpBeanAudiosBean();
		bean.title = source.getAudio_title();
		bean.url = source.getAudio();
		return bean;
	}


	class CommentHolder{
		public CircleImageView ivPortrait;
		public TextView tvNickName;
		public TextView tvTime;
		public TextView tvCont;
		public TextView tvSrcCont;
		public TopicPicContainer  llContainer;
		public ImageView ivDefaultIcon;
		public RecoderFrameLayout mRadio;//被回复的语音
		public RecoderFrameLayout messageRadio;//回复的语音
	}


	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		// 上拉加载更多
		if(mDatas!=null && mDatas.size()>0){			
			initDatas(mLimit, mDatas.get(mDatas.size()-1).getId(),0);
		}
		
	}
}
