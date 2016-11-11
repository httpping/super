package com.vp.loveu.discover.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
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
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.discover.bean.TutorBean;
import com.vp.loveu.my.activity.MyCenterActivity;
import com.vp.loveu.my.activity.UserIndexActivity;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.UIUtils;
import com.vp.loveu.widget.CircleImageView;

import cz.msebera.android.httpclient.Header;

/**
 * @author：pzj
 * @date: 2015年11月23日 下午5:33:03
 * @Description:在线导师
 */
public class TutorHomeActivity extends VpActivity implements OnRefreshListener<ListView> {
	private PullToRefreshListView mPullListView;
	private ListView mListView;
	private int mLimit=10;//获取记录条数
	private int mPage=1;//分页页码
	private TutorAdapter mAdapter;
	private List<TutorBean> mDatas;
	private DisplayImageOptions options;
	private TextView mTvEmptyDesc;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.discover_tutor_online_activity);
		this.mClient=new VpHttpClient(this);
		mDatas=new ArrayList<TutorBean>();
		initView();
		initDatas();
	}

	private void initView() {
		initPublicTitle();
		this.mPubTitleView.mBtnLeft.setText("");
		this.mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		this.mPubTitleView.mTvTitle.setText("在线导师");
		mPullListView=(PullToRefreshListView) findViewById(R.id.discover_tutor_list);
		mTvEmptyDesc=(TextView) findViewById(R.id.discover_item_tv_empty);
		
		mPullListView.setMode(Mode.PULL_FROM_END);//向上拉刷新
		mListView = mPullListView.getRefreshableView();
		mAdapter=new TutorAdapter();
		mListView.setAdapter(mAdapter);
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
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				position=position-mListView.getHeaderViewsCount();
				int currentUid = mDatas.get(position).getUid();
				//点击头像
				if (currentUid == LoginStatus.getLoginInfo().getUid()) {
					Intent intent = new Intent(UIUtils.getContext(), MyCenterActivity.class);
					startActivity(intent);
				}else{
					Intent intent = new Intent(UIUtils.getContext(), UserIndexActivity.class);
					intent.putExtra(UserIndexActivity.KEY_UID,currentUid);
					startActivity(intent);
				}
				
			}
		});
		
	}

	private void initDatas() {
		String url = VpConstants.DISCOVER_SUPERVISOR;
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
						List<TutorBean> currentList = TutorBean.createFromJsonArray(jsonData.toString());
						if(currentList!=null && currentList.size()>0){
							mDatas.addAll(currentList);
							mAdapter.notifyDataSetChanged();
							mPage++;
							
						}
						setViewVisiable();
					} else {
						String message = json.getString(VpConstants.HttpKey.MSG);
						Toast.makeText(TutorHomeActivity.this, message, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				mPullListView.onRefreshComplete();
				Toast.makeText(TutorHomeActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();

			}
		});
		
	}
	
	private void setViewVisiable(){
		if(mDatas!=null && mDatas.size()>0){
			mPullListView.setVisibility(View.VISIBLE);
			mTvEmptyDesc.setVisibility(View.GONE);
		}else{
			mPullListView.setVisibility(View.GONE);
			mTvEmptyDesc.setVisibility(View.VISIBLE);
		}
	}
	
	private class TutorAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			if(mDatas!=null)
				return mDatas.size();
			return 0;
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
			final TutorBean bean = mDatas.get(position);
			TutorHolder holder=null;
			if(convertView==null){
				holder=new TutorHolder();
				convertView=View.inflate(TutorHomeActivity.this, R.layout.discover_tutor_online_item, null);
				holder.ivIcon=(CircleImageView) convertView.findViewById(R.id.discover_item_iv_icon);
				holder.tvName=(TextView) convertView.findViewById(R.id.discover_item_tv_name);
				holder.tvLevel=(TextView) convertView.findViewById(R.id.discover_item_tv_level);
				convertView.setTag(holder);
			}else{
				holder=(TutorHolder) convertView.getTag();
			}
			ImageLoader.getInstance().displayImage(bean.getPortrait(),holder.ivIcon,options);
			holder.tvName.setText(bean.getNickname());
			holder.tvLevel.setText("V"+bean.getGrade());
//			holder.ivIcon.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					int currentUid = bean.getUid();
//					//点击头像
//					if (currentUid == LoginStatus.getLoginInfo().getUid()) {
//						Intent intent = new Intent(UIUtils.getContext(), MyCenterActivity.class);
//						startActivity(intent);
//					}else{
//						Intent intent = new Intent(UIUtils.getContext(), UserIndexActivity.class);
//						intent.putExtra(UserIndexActivity.KEY_UID,currentUid);
//						startActivity(intent);
//					}
//					
//				}
//			});
			return convertView;
		}
		
	}
	
	class TutorHolder{
		public CircleImageView ivIcon;
		public TextView tvName;
		public TextView tvLevel;
	}


	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		initDatas();
		
	}

}
