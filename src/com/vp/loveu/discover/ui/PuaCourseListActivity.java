package com.vp.loveu.discover.ui;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
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
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.channel.utils.ToastUtils;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.discover.bean.PuaCourseDetailBean;
import com.vp.loveu.index.activity.ArticleActivity;

import cz.msebera.android.httpclient.Header;

/**
 * @author：pzj
 * @date: 2015年11月23日 上午11:22:44
 * @Description:PUA课堂列表
 */
public class PuaCourseListActivity extends VpActivity implements OnItemClickListener, OnRefreshListener<ListView> {
	public static final String ID="id";
	public static final String NAME="name";
	private int id;
	private String title;
	private PullToRefreshListView mPullListView;
	private ListView mListView;
	private TextView mTvEmptyView;
	private PuaListAdapter mAdapter;
	private int mLimit=10;//获取记录条数
	private int mPage=1;//分页页码
	
	private ArrayList<PuaCourseDetailBean> mDatas;
	private DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.discover_pua_list_activity);
		this.id=getIntent().getIntExtra(ID, 0);
		this.title=getIntent().getStringExtra(NAME);
		this.mClient=new VpHttpClient(this);
		this.mDatas=new ArrayList<PuaCourseDetailBean>();
		initView();
		initDatas(false);
	}


	private void initView() {
		initPublicTitle();
		mPubTitleView.mBtnLeft.setText("");
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		this.mPubTitleView.mTvTitle.setText(this.title);
		mPullListView=(PullToRefreshListView) findViewById(R.id.discover_pua_list);
		mTvEmptyView=(TextView) findViewById(R.id.public_empty_view);
		
		mPullListView.setMode(Mode.PULL_FROM_END);//向上拉刷新
		mListView = mPullListView.getRefreshableView();
		mAdapter=new PuaListAdapter();
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mPullListView.setOnRefreshListener(this);
		
		options = new DisplayImageOptions.Builder()
		         .showImageOnLoading(R.color.frenchgrey) // resource or
		        .showImageForEmptyUri(R.drawable.ic_launcher) // resource or
		        .showImageOnFail(R.drawable.ic_launcher) // resource or
		        .resetViewBeforeLoading(false) // default
		        .cacheInMemory(true) // default
		        .cacheOnDisk(true) // default
		        .bitmapConfig(Bitmap.Config.RGB_565)  
		        .considerExifParams(false) // default
		        .displayer(new SimpleBitmapDisplayer())
		        .build();
	}
	

	private void initDatas(final boolean isLoadMore) {

		String url = VpConstants.DISCOVER_NEWS_LIST;
		RequestParams params = new RequestParams();
		params.put("limit", this.mLimit);
		params.put("page", this.mPage);
		params.put("id", this.id);
		mClient.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				mPullListView.onRefreshComplete();
				String result = ResultParseUtil.deAesResult(responseBody);
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString(VpConstants.HttpKey.CODE);
					if ("0".equals(code)) {// 返回成功
						JSONArray jsonData=json.getJSONArray(VpConstants.HttpKey.DATA);
						ArrayList<PuaCourseDetailBean> currentList  = PuaCourseDetailBean.parseArrayJson(jsonData);
						if(currentList!=null && currentList.size()>0){
							mDatas.addAll(currentList);
							mAdapter.notifyDataSetChanged();
							mPage++;
							
						}else{
							if(isLoadMore)
								ToastUtils.showTextToast(PuaCourseListActivity.this, "没有更多数据了");
						}
						
						if(mDatas!=null && mDatas.size()>0){
							mPullListView.setVisibility(View.VISIBLE);
							mTvEmptyView.setVisibility(View.GONE);
						}else{
							mPullListView.setVisibility(View.GONE);
							mTvEmptyView.setVisibility(View.VISIBLE);
						}
						
					} else {
						String message = json.getString(VpConstants.HttpKey.MSG);
						Toast.makeText(PuaCourseListActivity.this, message, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				mPullListView.onRefreshComplete();
				Toast.makeText(PuaCourseListActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();

			}
		});
		
	
	}
	
	private class PuaListAdapter extends BaseAdapter{

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
			ViewHolder holder=null;
			if(convertView==null){
				convertView=View.inflate(PuaCourseListActivity.this, R.layout.discover_pua_list_item, null);
				holder=new ViewHolder();
				holder.ivIcon=(ImageView) convertView.findViewById(R.id.discover_pua_list_iv_icon);
				holder.tvName=(TextView) convertView.findViewById(R.id.discover_pua_list_tv_title);
				holder.tvTutor=(TextView) convertView.findViewById(R.id.discover_pua_list_tv_tutor);
				holder.emptyTop= convertView.findViewById(R.id.discover_pua_empty_top);
				holder.emptyBottom= convertView.findViewById(R.id.discover_pua_empty_bottom);
				holder.ivPlayer = (ImageView) convertView.findViewById(R.id.index_video_play_start);
				holder.container = (FrameLayout) convertView.findViewById(R.id.discover_pua_list_flag);
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder) convertView.getTag();
			}
			PuaCourseDetailBean bean = mDatas.get(position);
			
			holder.ivPlayer.setVisibility(bean.getHas_video() == 1 ? View.VISIBLE : View.GONE);
			if(bean.getPic()!=null && !"".equals(bean.getPic())){
				holder.container.setVisibility(View.VISIBLE);
				holder.ivIcon.setVisibility(View.VISIBLE);
				ImageLoader.getInstance().displayImage(bean.getPic(), holder.ivIcon, options);
			}else{
				holder.container.setVisibility(View.GONE);
				holder.ivIcon.setVisibility(View.GONE);
//				holder.tvName.setPadding(UIUtils.dp2px(5), 0, 0, 0);
//				holder.tvTutor.setPadding(UIUtils.dp2px(5), 0, 0, 0);
			}
			
			holder.emptyTop.setVisibility(position==0?View.VISIBLE:View.GONE);
			try {
				mDatas.get(position+1);
				holder.emptyBottom.setVisibility(View.GONE);
			} catch (Exception e) {
				//表示已经是最后一条数据
				holder.emptyBottom.setVisibility(View.VISIBLE);
			}
			
			holder.tvName.setText(bean.getTitle());
			holder.tvTutor.setText(bean.getNickname());
			return convertView;
		}
		
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		position=position-mListView.getHeaderViewsCount();
		Intent intent = new Intent(PuaCourseListActivity.this, ArticleActivity.class);
		intent.putExtra(ArticleActivity.KEY_FLAG, mDatas.get(position).getId());
		intent.putExtra(ArticleActivity.KEY_FLAG_TYPE, ArticleActivity.TYPE_PUA);
		intent.putExtra(ArticleActivity.KEY_FLAG_SHARE_TITLE,mDatas.get(position).getTitle() );
		intent.putExtra(ArticleActivity.KEY_FLAG_SHARE_ICONPATH, mDatas.get(position).getSmall_pic());
		startActivity(intent);
		
	}
	
	class ViewHolder{
		public ImageView ivIcon;
		public ImageView ivPlayer;
		public TextView tvName;
		public TextView tvTutor;
		public View emptyTop;
		public View emptyBottom;
		public FrameLayout container;
	}


	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		initDatas(true);
	}
}
