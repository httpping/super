package com.vp.loveu.discover.ui;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.vp.loveu.discover.bean.PuaCoruseBean;
import com.vp.loveu.discover.bean.PuaCoruseBeanVo;
import com.vp.loveu.discover.bean.PuaCoruseBeanVo.PuaCoruseSubBean;
import com.vp.loveu.util.UIUtils;

import cz.msebera.android.httpclient.Header;

/**
 * @author：pzj
 * @date: 2015年11月23日 上午9:32:12
 * @Description:PUA课堂
 */
public class PuaCourseActivity extends VpActivity implements OnRefreshListener<ListView> {
	private PullToRefreshListView mPullListView;
	private ListView mListView;
	private PuaCourseAdapter mAdapter;
	private DisplayImageOptions options;
	private ArrayList<PuaCoruseBeanVo>  mPuaList;
	private ArrayList<PuaCoruseBean>  mDatas;
	private static final int TYPE_TITLE=0;
	private static final int TYPE_CONTENT=1;
	private TextView mTvEmptyView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mClient = new VpHttpClient(this);
		setContentView(R.layout.discover_course_pua_activity);
		mDatas=new ArrayList<PuaCoruseBean>();
		initView();
		initDatas();
	}

	private void initView() {
		initPublicTitle();
		mPubTitleView.mBtnLeft.setText("");
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mTvTitle.setText("PUA课堂");
		this.mTvEmptyView=(TextView) findViewById(R.id.public_empty_view);
		mPullListView=(PullToRefreshListView) findViewById(R.id.discover_pua_course_listview);
		mPullListView.setMode(Mode.PULL_FROM_END);//向上拉刷新
		mListView=mPullListView.getRefreshableView();
		mPullListView.setOnRefreshListener(this);
		mAdapter=new PuaCourseAdapter();
		mListView.setAdapter(mAdapter);
		
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

	private void initDatas() {
		String url = VpConstants.DISCOVER_NEWS_PUA;

		mClient.get(url, new RequestParams(), new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString(VpConstants.HttpKey.CODE);

					if ("0".equals(code)) {// 返回成功
						JSONArray jsonData=json.getJSONArray(VpConstants.HttpKey.DATA);
						mPuaList = PuaCoruseBeanVo.parseArrayJson(jsonData);
						setListViewData();
						setViewVisiable();
					} else {
						String message = json.getString(VpConstants.HttpKey.MSG);
						Toast.makeText(PuaCourseActivity.this, message, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(PuaCourseActivity.this, "网络访问异常", Toast.LENGTH_SHORT).show();

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
	
	protected void setListViewData() {
		if(mPuaList!=null){
			for(PuaCoruseBeanVo puaBean:mPuaList){
				//title
				PuaCoruseBean titleBean=new PuaCoruseBean();
				titleBean.setType(TYPE_TITLE);
				titleBean.setTitle(puaBean.getName());
				mDatas.add(titleBean);
				//子分类
				for(int i=0;i<puaBean.getChildren().size();i++){
					PuaCoruseSubBean childBean=puaBean.getChildren().get(i);
					PuaCoruseBean contentBean=new PuaCoruseBean();
					contentBean.setType(TYPE_CONTENT);
					contentBean.setId1(childBean.getId());
					contentBean.setName1(childBean.getName());
					contentBean.setPic1(childBean.getPic());
					if(i<puaBean.getChildren().size()-1){
						//获取下一条数据,填充到listview item右边部分
						childBean=puaBean.getChildren().get(i+1);
						contentBean.setId2(childBean.getId());
						contentBean.setName2(childBean.getName());
						contentBean.setPic2(childBean.getPic());
						i++;
					}
					mDatas.add(contentBean);
					
				}
				
			}
			
			this.mAdapter.notifyDataSetChanged();
		}
		
	}

	private class PuaCourseAdapter extends BaseAdapter{

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
		public int getItemViewType(int position) {
			
			return mDatas.get(position).getType();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int currentType=getItemViewType(position);
			PuaCoruseBean bean = mDatas.get(position);
			if(currentType==TYPE_TITLE){
				convertView=View.inflate(PuaCourseActivity.this, R.layout.discover_pua_item_title, null);
				TextView tv=(TextView) convertView.findViewById(R.id.discover_pua_item_title);
				tv.setText(mDatas.get(position).getTitle());
			}else if(currentType==TYPE_CONTENT){
				ContentHolder holder=null;
				if(convertView==null || !(convertView.getTag() instanceof ContentHolder)){
					holder=new ContentHolder();
					convertView=View.inflate(PuaCourseActivity.this, R.layout.discover_pua_item_content, null);
					holder.icon1=(ImageView) convertView.findViewById(R.id.discover_item_iv_icon1);
					holder.icon2=(ImageView) convertView.findViewById(R.id.discover_item_iv_icon2);
					holder.tvdesc1=(TextView) convertView.findViewById(R.id.discover_item_iv_desc1);
					holder.tvdesc2=(TextView) convertView.findViewById(R.id.discover_item_iv_desc2);
					convertView.setTag(holder);
				}else{
					holder=(ContentHolder) convertView.getTag();
				}
				ImageLoader.getInstance().displayImage(bean.getPic1(), holder.icon1, options);
				holder.tvdesc1.setText(bean.getName1());
				createImageClickListener(holder.icon1,bean.getId1(),bean.getName1());
				if(bean.getId2()!=0){
					ImageLoader.getInstance().displayImage(bean.getPic2(), holder.icon2, options);
					holder.tvdesc2.setText(bean.getName2());
					createImageClickListener(holder.icon2,bean.getId2(),bean.getName2());
				}
				
				//如果下一条目为titleItem，设置padding
				try {
					PuaCoruseBean nextBean = mDatas.get(position+1);
					if(nextBean.getType()==TYPE_TITLE){
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
				
				
			}
			return convertView;
		}
		
	}
	
	class ContentHolder{
		public ImageView icon1;
		public ImageView icon2;
		public TextView tvdesc1;
		public TextView tvdesc2;
	}

	/**
	 * 点击图片进入到相应的分类页面
	 * @param icon
	 * @param id
	 */
	public void createImageClickListener(ImageView icon, final int id,final String name) {
		icon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(PuaCourseActivity.this,PuaCourseListActivity.class);
				intent.putExtra(PuaCourseListActivity.ID, id);
				intent.putExtra(PuaCourseListActivity.NAME, name);
				
				startActivity(intent);
			}
		});
		
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		new Thread(){
			public void run() {
				SystemClock.sleep(1500);
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						mPullListView.onRefreshComplete();
						ToastUtils.showTextToast(PuaCourseActivity.this, "没有更多数据了");
					}
				});
			};
		}.start();

		
	}

}
