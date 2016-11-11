package com.vp.loveu.index.activity;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.ILoadingLayout;
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
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.bean.MapLoactionBean;
import com.vp.loveu.bean.NetBaseBean;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.bean.NearPersonBean;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.MapLocationNetwork;
import com.vp.loveu.util.ToastUtil;
import com.vp.loveu.widget.CircleImageView;
import com.vp.loveu.widget.CustomProgressDialog;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月17日下午3:32:12
 * @功能 附近的人的activity
 * @作者 mi
 */

public class NearByActivity extends VpActivity implements OnRefreshListener2<ListView>{

	protected static final String TAG = "NearByActivity";
	public PullToRefreshListView mListView;
	
	public int page =1 ;
	public int limit =15;
	public double lat =22.33 ;
	public double lon =114.07 ;
	public String area_code ="4403";
	private CustomProgressDialog progressDialog = null;
	public List<NearPersonBean> nearPersonBeans;
	MyAdapter adapter;
	DisplayImageOptions options;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.index_nearby_activity);
		
		options = new DisplayImageOptions.Builder()
		 .showImageOnLoading(R.drawable.default_image_loading) // resource or
	     .showImageForEmptyUri(R.drawable.default_image_loading_fail) // resource or
	     .showImageOnFail(R.drawable.default_image_loading_fail) // resource or
        .resetViewBeforeLoading(false) // default
        .delayBeforeLoading(50).cacheInMemory(true) // default
        .cacheOnDisk(true) // default
        .bitmapConfig(Bitmap.Config.RGB_565)  
        .considerExifParams(false) // default
        .build();
		
		initPublicTitle();
		initView();
		
		progressDialog = CustomProgressDialog.createDialog(this);
		if (progressDialog!=null) {
			progressDialog.show();
		}
		MapLocationNetwork mapLocationNetwork = new MapLocationNetwork(mHandler, this);
	}
	
	public void loadData() {
	
		// nearby?page={page}&limit={limit}&area_code={area_code}&lat={lat}&lng={lng}
		mClient = new VpHttpClient(this);
		mClient.setShowProgressDialog(false);
		RequestParams params = new RequestParams();
		LoginUserInfoBean mine = LoginStatus.getLoginInfo();
		if (mine == null) {
			mine = new LoginUserInfoBean(this);
		}
		params.put("page", page);
		params.put("uid", mine.getUid());
		params.put("limit", limit);
		params.put("area_code", area_code);
		params.put("lat", lat);
		params.put("lng", lon);
		mClient.setShowProgressDialog(false);
		mClient.get(VpConstants.NEARBY_URL, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				mListView.onRefreshComplete();
				String result = ResultParseUtil.deAesResult(responseBody);
				
				try {
					JSONObject jsonObject = new JSONObject(result);
					if (jsonObject.getInt("code") ==0) {//success
						List<NearPersonBean> lists = NearPersonBean.createFromJsonArray(jsonObject.getString("data"));
						if (lists.size() ==0) {
							ToastUtil.showToast(NearByActivity.this, "没有更多内容", 0);
						}
						if (page == 1) {
							nearPersonBeans.clear();
						}
						Message message = new Message();
						message.what = REFRESH_VIEW;
						message.obj = nearPersonBeans.size() ;
						nearPersonBeans.addAll(lists);//添加
						
						if (lists.size() < limit && page!=1) {//禁止上拉加载
							mListView.setMode(Mode.PULL_FROM_START);
						}else {
							mListView.setMode(Mode.BOTH);
						}
						page++;
						mHandler.sendMessage(message);
					}else{
						
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				mListView.onRefreshComplete();
				//ToastUtil.showToast(NearByActivity.this, "加载失败", 0);
				Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
				//finish();
			}
		});
		
		mListView.setAdapter(new MyAdapter());
	}

	
	public static final int REFRESH_VIEW = 1909;
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case REFRESH_VIEW:
				int postion = (int) msg.obj;
				mListView.onRefreshComplete();
				mListView.getRefreshableView().setSelection(postion);
				if (progressDialog!=null) {
					progressDialog.hide();
					progressDialog = null;
				}
				break;
			case MapLocationNetwork.MAP_RESULT_WHAT:
				MapLoactionBean bean = (MapLoactionBean) msg.obj;
				if (bean == null || !bean.result) {
					ToastUtil.showToast(NearByActivity.this, "定位失败，请检查网络", 1);
					finish();
					return;
				}
				lon = bean.lon;
				lat = bean.lat;
				area_code = bean.adCode;
				loadData();
				break ;
			default:
				break;
			}
			
		};
	};
	
	public void initView() {
		mPubTitleView.mBtnLeft.setText("");
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mTvTitle.setText("附近的人");
		mPubTitleView.mBtnRight.setVisibility(View.GONE);
		mListView = (PullToRefreshListView) findViewById(R.id.nearby_listview);
		mListView.setMode(Mode.BOTH);
		ILoadingLayout up = mListView.getLoadingLayoutProxy(false, true);
		ILoadingLayout down = mListView.getLoadingLayoutProxy(true, false);
		mListView.setOnRefreshListener(this);
		ListView refreshableView = mListView.getRefreshableView();
		//refreshableView.setDivider(new ColorDrawable(Color.parseColor("#80cccccc")));
		nearPersonBeans = new LinkedList<NearPersonBean>();
		adapter = new MyAdapter();
		mListView.setAdapter(adapter);
		mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				NearPersonBean nearPersonBean = nearPersonBeans.get(position-1);
				goOtherUserInfo(nearPersonBean.uid);
			}
		});
	}

	public class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return nearPersonBeans==null?0:nearPersonBeans.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			NearPersonBean bean = nearPersonBeans.get(position);
			if (convertView == null) {
				convertView = View.inflate(NearByActivity.this, R.layout.nearby_listview_item, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			if (holder!=null) {
				holder.updateView(bean);
			}
			return convertView;
		}
	}
	
	public  class ViewHolder{
		public CircleImageView nearbyItemIvIcon ;
		public TextView nearbyItemTvName ;
		public ImageView nearbyItemTvSex ;
		public TextView nearbyItemTvDescribe ;
		public ImageView nearbyItemIvFlag ;
		public TextView nearbyItemTvDistance ;
		public Button nearbyItemIbFollow ;
		
		public ViewHolder(View rootView){
			nearbyItemIvIcon = (CircleImageView) rootView.findViewById(R.id.nearby_item_iv_icon);
			nearbyItemTvName = (TextView)rootView.findViewById(R.id.nearby_item_tv_name);
			nearbyItemTvSex = (ImageView)rootView.findViewById(R.id.nearby_item_tv_sex);
			nearbyItemTvDescribe = (TextView)rootView.findViewById(R.id.nearby_item_tv_describe);
			nearbyItemIvFlag = (ImageView)rootView.findViewById(R.id.nearby_item_iv_flag);
			nearbyItemTvDistance = (TextView)rootView.findViewById(R.id.nearby_item_tv_distance);
			nearbyItemIbFollow = (Button)rootView.findViewById(R.id.nearby_item_ib_follow);
		}
		
		public void updateView(NearPersonBean bean){
			String userInfoHead = getUserInfoHead(bean.uid);
			bean.portrait =  userInfoHead == null ? bean.portrait : userInfoHead;
			ImageLoader.getInstance().displayImage(bean.portrait, nearbyItemIvIcon, options);
			nearbyItemTvName.setText(bean.nickname);
			//nearbyItemTvDescribe.setText(bean.)
			if (bean.distance <500) {//m
				nearbyItemTvDistance.setText((int)(bean.distance)+"m");
			}else{//km
				int distance = (int) (bean.distance*100);
				DecimalFormat df = new DecimalFormat();
				df.applyPattern("0");
				String dstr = null;
				nearbyItemIvFlag.setVisibility(View.VISIBLE);

				if (distance/100/1000 > 1000) {
					dstr=">1000公里";
					//nearbyItemIvFlag.setVisibility(View.INVISIBLE);
				}else {
					dstr= df.format((int)(distance/100.0/1000.0));
					dstr +="公里";
				}
				nearbyItemTvDistance.setText(dstr);
				
			}
			if (bean.sex ==1) {
				nearbyItemTvDescribe.setText("想找一个女朋友");
				nearbyItemTvSex.setImageResource(R.drawable.man);
			}else {
				nearbyItemTvDescribe.setText("想找一个男朋友");
				nearbyItemTvSex.setImageResource(R.drawable.woman);
			}
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		page =1 ;
		loadData();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadData();
	}
}
