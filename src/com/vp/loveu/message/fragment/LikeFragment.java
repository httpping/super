package com.vp.loveu.message.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.vp.loveu.channel.bean.LikeMsgBean;
import com.vp.loveu.channel.bean.TopicBean;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.bean.FellHelpBean.FellHelpBeanAudiosBean;
import com.vp.loveu.index.widget.RecoderFrameLayout;
import com.vp.loveu.message.ui.CommenNoticListActivity;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.SharedPreferencesHelper;
import com.vp.loveu.util.VpDateUtils;
import com.vp.loveu.widget.CircleImageView;

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
import cz.msebera.android.httpclient.Header;

/**
 * @author：pzj
 * @date: 2015年12月4日 下午5:48:25
 * @Description:赞列表
 */
public class LikeFragment extends VpFragment implements OnRefreshListener<ListView> {
	private PullToRefreshListView mPullListView;
	private ListView mListView;
	private CommentAdapter mAdapter;
	private Context mContext;
	private DisplayImageOptions options;
	private int mLimit=10;
	private ArrayList<LikeMsgBean> mDatas;
	private VpHttpClient mClient;
	private int mAlreadyReadLikeId;
	public SharedPreferencesHelper msp;
	public static final String ALREADYREAD_LIKE_ID="alreadyread_like_id";//已读赞的ID
	private int mUnReadLikeCount;
	private TextView mTvEmpty;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext=getActivity();
		mClient=new VpHttpClient(mContext);
		mDatas=new ArrayList<LikeMsgBean>();
		msp=SharedPreferencesHelper.getInstance(mContext);
		View v=View.inflate(mContext, R.layout.message_like_fragment, null);
		mTvEmpty=(TextView) v.findViewById(R.id.public_empty_view);
		mPullListView=(PullToRefreshListView) v.findViewById(R.id.message_like_listview);
		mPullListView.setMode(Mode.PULL_FROM_END);//向上拉刷新
		mListView=mPullListView.getRefreshableView();
		mPullListView.setOnRefreshListener(this);
		mAdapter = new CommentAdapter();
		mListView.setAdapter(mAdapter);
		mAlreadyReadLikeId=msp.getIntegerValue(ALREADYREAD_LIKE_ID);
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
		String url = VpConstants.MESSAGE_USER_LIKED_LIST;
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
						ArrayList<LikeMsgBean> currList = LikeMsgBean.parseArrayJson(jsonData,mAlreadyReadLikeId);
						if(currList!=null && currList.size()>0){
							if(dir==0){
								mDatas.addAll(currList);
							}else{
								currList.addAll(mDatas);
								mDatas=currList;
							}
							msp.putIntegerValue(ALREADYREAD_LIKE_ID, mDatas.get(0).getId());
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
	 * 计算未读赞数量
	 */
	private void calculateUnReadCount(){
		mUnReadLikeCount=0;
		if(mDatas!=null && mDatas.size()>0){
			for (LikeMsgBean bean:mDatas) {
				if(!bean.isRead())
					mUnReadLikeCount++;
			}
		}
		if(mUnReadLikeCount>0){
			((CommenNoticListActivity)getActivity()).updateTabPointStatus(1, true);
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
			if(position==mUnReadLikeCount){//以往回复title
				convertView = View.inflate(mContext, R.layout.default_sub_title_item_view, null);
				((TextView)convertView.findViewById(R.id.channel_item_tv_name)).setText("以往赞");
			}else{
				if(position>mUnReadLikeCount)
					position=position-1;
				LikeMsgBean LikeMsgBean = mDatas.get(position);
				CommentHolder commentHolder=null;
				if(convertView==null || !(convertView.getTag() instanceof CommentHolder)){
					commentHolder=new CommentHolder();
					convertView=View.inflate(mContext, R.layout.message_liked_list_item, null);
					commentHolder.ivPortrait=(CircleImageView) convertView.findViewById(R.id.message_liked_portrait);
					commentHolder.tvNickName=(TextView) convertView.findViewById(R.id.message_liked_nickname);
					commentHolder.tvTime=(TextView) convertView.findViewById(R.id.message_liked_time);
					commentHolder.tvSrcCont=(TextView) convertView.findViewById(R.id.message_liked_src_cont);
					commentHolder.mRadio = (RecoderFrameLayout) convertView.findViewById(R.id.radio);
					commentHolder.ivDefaultIcon=(ImageView) convertView.findViewById(R.id.message_default_icon);
					convertView.setTag(commentHolder);
				}else{
					commentHolder=(CommentHolder) convertView.getTag();
				}
				
				ImageLoader.getInstance().displayImage(LikeMsgBean.getPortrait(), commentHolder.ivPortrait, options);
				commentHolder.tvNickName.setText(LikeMsgBean.getNickname());
				commentHolder.tvTime.setText(VpDateUtils.getStandardDate(LikeMsgBean.getCreate_time()));
				
				if(LikeMsgBean.getSummary()==null || "".equals(LikeMsgBean.getSummary())){
					commentHolder.tvSrcCont.setVisibility(View.GONE);
					commentHolder.ivDefaultIcon.setVisibility(View.GONE);
					if (!TextUtils.isEmpty(LikeMsgBean.getAudio())) {
						commentHolder.mRadio.setData(getAudioDataBean(LikeMsgBean));
						commentHolder.mRadio.setVisibility(View.VISIBLE);
					}else{
						commentHolder.mRadio.setVisibility(View.GONE);
					}
				}else{
					commentHolder.tvSrcCont.setText(LikeMsgBean.getSummary());
					commentHolder.tvSrcCont.setVisibility(View.VISIBLE);
					commentHolder.ivDefaultIcon.setVisibility(View.GONE);
					commentHolder.mRadio.setVisibility(View.GONE);
				}

			}
			return convertView;
		}
		
	}
	
	private FellHelpBeanAudiosBean getAudioDataBean(LikeMsgBean dllBean) {
		FellHelpBeanAudiosBean bean = new FellHelpBeanAudiosBean();
		bean.title = dllBean.getAudio_title();
		bean.url = dllBean.getAudio();
		return bean;
	}
	
	class CommentHolder{
		public CircleImageView ivPortrait;
		public TextView tvNickName;
		public TextView tvTime;
		public TextView tvSrcCont;
		public ImageView ivDefaultIcon;
		public RecoderFrameLayout mRadio;
	}


	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		// 上拉加载更多
		if(mDatas!=null && mDatas.size()>0){			
			initDatas(mLimit, mDatas.get(mDatas.size()-1).getId(),0);
		}
		
	}
}
