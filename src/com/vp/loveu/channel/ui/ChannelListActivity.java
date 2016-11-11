package com.vp.loveu.channel.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.base.VpApplication;
import com.vp.loveu.channel.db.RadioDbDao;
import com.vp.loveu.channel.db.RadioHistoryBean;
import com.vp.loveu.channel.musicplayer.MusicService;
import com.vp.loveu.channel.utils.ToastUtils;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.ServiceStateUtils;
import com.vp.loveu.util.SharedPreferencesHelper;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;

/**
 * @author：pzj
 * @date: 2015年11月19日 下午2:49:42
 * @Description:电台列表页面
 */
public class ChannelListActivity extends VpActivity implements OnRefreshListener<ListView>, OnItemClickListener, OnClickListener {
	private PullToRefreshListView mPullListView;
	private ChannelAdapter mAdapter;
	private List<RadioBean> mDatas;
	private RelativeLayout mBtnUploadAudio;
	private int mLimit=10;//获取记录条数
	private int mPage=1;//分页页码
	private ListView mListView;
	private RadioDbDao mDao;
	private TextView mTvEmptyView;
	private TextView mTvInteGral;//分数
	SharedPreferencesHelper sp ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channel_list_activity);
		this.mClient=new VpHttpClient(this);
		this.mDatas=new ArrayList<ChannelListActivity.RadioBean>();
		this.mDao=new RadioDbDao(this);
		initView();
		initDatas(false);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(mAdapter!=null)
			mAdapter.notifyDataSetChanged();
	}

	private void initView() {
		initPublicTitle();
		this.mPubTitleView.mBtnLeft.setText("");
		this.mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		this.mPubTitleView.mTvTitle.setText("电台列表");
		mPullListView=(PullToRefreshListView) findViewById(R.id.channel_list);
		this.mTvEmptyView=(TextView) findViewById(R.id.public_empty_view);
		mBtnUploadAudio =  (RelativeLayout) findViewById(R.id.btn_upload_audios);
		mTvInteGral = (TextView) findViewById(R.id.upload_integral);
		
		sp = SharedPreferencesHelper.getInstance(this);
		mTvInteGral.setText(" +"+sp.getStringValue("upload_integral"));
        // Set a listener to be invoked when the list should be refreshed.  
		mPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {  
            @Override  
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {  
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),  
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);  
                // Update the LastUpdatedLabel  
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);  
  
            }  
        });
		mPullListView.setMode(Mode.PULL_FROM_END);//向上拉刷新
		mListView = mPullListView.getRefreshableView();
		mAdapter=new ChannelAdapter();
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mPullListView.setOnRefreshListener(this);
		mBtnUploadAudio.setOnClickListener(this);
		
	}
	

	private void initDatas(final boolean isLoadMore) {
		String url = VpConstants.CHANNEL_LIST;
		RequestParams params = new RequestParams();
		params.put("limit", this.mLimit);
		params.put("page", this.mPage);
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
						RadioBean radioBean =new RadioBean();
						List<RadioBean> currentList = radioBean.createFromJsonArray(jsonData.toString());
						if(currentList!=null && currentList.size()>0){
							mDatas.addAll(currentList);
							mAdapter.notifyDataSetChanged();
							mPage++;
							
						}else{
							if(isLoadMore)
								ToastUtils.showTextToast(ChannelListActivity.this, "没有更多数据了");
//							Toast.makeText(ChannelListActivity.this, "没有更多数据了", Toast.LENGTH_LONG).show();
						}
						
						setViewVisiable();
					} else {
						String message = json.getString(VpConstants.HttpKey.MSG);
						Toast.makeText(ChannelListActivity.this, message, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				mPullListView.onRefreshComplete();
				Toast.makeText(ChannelListActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();

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

	
	private class ChannelAdapter extends BaseAdapter{

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
			RadioBean bean = mDatas.get(position);
			//恋爱电台item
			RadioHolder radioHolder=null;
			if(convertView==null){
				radioHolder=new RadioHolder();
				convertView = View.inflate(ChannelListActivity.this, R.layout.channel_index_item_radio_view, null);
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
			radioHolder.tvListener.setText(bean.getUser_num()+"人听过");
			radioHolder.tvTutor.setText("导师 :"+bean.getNickname());
			if(ServiceStateUtils.isRunging(ChannelListActivity.this, MusicService.class) && bean.getId()==VpApplication.getInstance().getmRadioId()){
				radioHolder.mIconRight.setVisibility(View.VISIBLE); 
				//暂时不需要做动画效果
//			    radioHolder.mIconRight.setBackgroundResource(R.drawable.channel_playing_animation);
//			    ((AnimationDrawable) radioHolder.mIconRight.getBackground()).start();
			}else{
//				radioHolder.mIconRight.clearAnimation();
				radioHolder.mIconRight.setVisibility(View.GONE);
			}
			RadioHistoryBean historyBean = mDao.findRadioHistory(LoginStatus.getLoginInfo().getUid(), bean.getId());
			if(historyBean!=null){
				radioHolder.mIconLeft.setImageResource(R.drawable.icon_love_radio_listenered);
				mDatas.get(position).setListened(true);
			}else{				
				radioHolder.mIconLeft.setImageResource(R.drawable.icon_love_radio);
			}
			return convertView;
		}
	}
	
	class RadioHolder{
		public TextView tvTitle;
		public TextView tvListener;
		public TextView tvTutor;
		public ImageView mIconLeft;
		public ImageView mIconRight;
	}
	
	
	public class RadioBean{
		private int id	;
		private String name	;
		private int user_num;	
		private int uid;	
		private String nickname;
		private String url;
		private String cover;
		
		private boolean isListened;
		private int totalPosition;
		private int currentPosition;
		
		public  RadioBean parseJson(String json) {
			Gson gson=new Gson();
			RadioBean bean=gson.fromJson(json, RadioBean.class);
			return bean;
		}
		
		public  List<RadioBean> createFromJsonArray(String json) {
			List<RadioBean> modeBens = new ArrayList<RadioBean>();
			try {
				JSONArray jsonArray = new JSONArray(json);
				for (int i = 0; i < jsonArray.length(); i++) {
					RadioBean bean =parseJson(jsonArray.getString(i));
					if (bean != null) {
						modeBens.add(bean);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return modeBens;
		}
		
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getUser_num() {
			return user_num;
		}
		public void setUser_num(int user_num) {
			this.user_num = user_num;
		}
		public int getUid() {
			return uid;
		}
		public void setUid(int uid) {
			this.uid = uid;
		}
		public String getNickname() {
			return nickname;
		}
		public void setNickname(String nickname) {
			this.nickname = nickname;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}

		public boolean isListened() {
			return isListened;
		}

		public void setListened(boolean isListened) {
			this.isListened = isListened;
		}

		public int getTotalPosition() {
			return totalPosition;
		}

		public void setTotalPosition(int totalPosition) {
			this.totalPosition = totalPosition;
		}

		public int getCurrentPosition() {
			return currentPosition;
		}

		public void setCurrentPosition(int currentPosition) {
			this.currentPosition = currentPosition;
		}

		public String getCover() {
			return cover;
		}

		public void setCover(String cover) {
			this.cover = cover;
		}
		
		
		
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
			initDatas(true);
		}

	/**
	 * listview item click event
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		position=position-mListView.getHeaderViewsCount();
		Intent intent=new Intent(ChannelListActivity.this,ChannelDetailActivity.class);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_upload_audios:
			Intent intent = new Intent(this,UploadAudioManagerActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

}
