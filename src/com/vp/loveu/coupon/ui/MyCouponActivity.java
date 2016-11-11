package com.vp.loveu.coupon.ui;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import cn.sharesdk.framework.authorize.b;

import com.amap.api.mapcore2d.da;
import com.aps.be;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.bean.NetBaseBean;
import com.vp.loveu.bean.VPBaseBean;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.coupon.adapter.CouponListAdapter;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.index.widget.EmptyTextView;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.message.utils.VlinkTimeUtil;
import com.vp.loveu.my.bean.PromoCodeBean;
import com.vp.loveu.util.ClipboardUtil;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.VPLog;
import com.vp.loveu.widget.IOSActionSheetDialog;
import com.vp.loveu.widget.IOSActionSheetDialog.OnSheetItemClickListener;
import com.vp.loveu.widget.IOSActionSheetDialog.SheetItemColor;

import cz.msebera.android.httpclient.Header;

/**
 * vp activity
 * @author tanping
 * 2016-3-7
 */
public class MyCouponActivity extends VpActivity implements OnRefreshListener2<ListView>,OnItemClickListener,OnClickListener{

	
	public static final int EIDT_COUPON_REQUEST_CODE = 200;//请求码
	
	private PullToRefreshListView mListView;
	CouponListAdapter mAdapter;
	private int mPage =1;
	private int mLimit =20;
	private List<PromoCodeBean> mData;
	Gson gson = new Gson();
	
	View addCouponView;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_coupon);
		mData = new LinkedList<PromoCodeBean>();
		initPublicTitle();
		if (mClient == null) { 
			mClient = new VpHttpClient(this);
		}
		
		initView();
		initData();
	}
	

	private void initView() {
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mBtnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mPubTitleView.mTvTitle.setText(R.string.title_activity_my_coupon);
		mListView = (PullToRefreshListView) findViewById(R.id.coupon_list);
		mListView.setMode(Mode.BOTH);
		mListView.setOnItemClickListener(this);
		ILoadingLayout up = mListView.getLoadingLayoutProxy(false, true);
		ILoadingLayout down = mListView.getLoadingLayoutProxy(true, false);
		 
		EmptyTextView tv = new EmptyTextView(this);
		tv.setWidth(MyUtils.getScreenWidthAndHeight(this)[0]);
		tv.setHeight(MyUtils.getScreenWidthAndHeight(this)[1]);
		tv.setGravity(Gravity.CENTER);
		tv.setText("没有优惠码数据");
		tv.setTextSize(14);
		mListView.setEmptyView(tv);
		mListView.setOnRefreshListener(this);
		
		mAdapter = new CouponListAdapter(this, mData, mListView.getRefreshableView());
		mListView.setAdapter(mAdapter);
		
		addCouponView = findViewById(R.id.btn_add_coupon);
		addCouponView.setOnClickListener(this);
	}
	

	private void initData() {
		startHttp(mPage);
	}
	

	private void startHttp(int page) {
		
		RequestParams params = new RequestParams();
		params.put("limit", mLimit);
		params.put("page", mPage);
		
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo == null) {
			return;
		}
		params.put("id", loginInfo.getUid());
		params.put("page", mPage);
		
		mClient.get(VpConstants.MY_ADD_PROMO_CODE, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				mListView.onRefreshComplete();
				String result = ResultParseUtil.deAesResult(responseBody);
				
				PromoCodeBean fromJson = gson.fromJson(result, PromoCodeBean.class);
				if (fromJson.code == 0) {
					Type listType = new TypeToken<LinkedList<PromoCodeBean>>(){}.getType(); 
					 fromJson.adatper_data = gson.fromJson(fromJson.data, listType); 
					
					if (mPage == 1) {
						mData.clear();
						mData.addAll(fromJson.adatper_data);
						mAdapter.notifyDataSetChanged();
						
					} else {
						if (fromJson.adatper_data == null || fromJson.adatper_data.size() <= 0) {
							Toast.makeText(getApplicationContext(), R.string.not_more_data, Toast.LENGTH_SHORT).show();
						} else {
							mData.addAll(fromJson.adatper_data);
							mAdapter.notifyDataSetChanged();
						}
					}
				} else {
					Toast.makeText(getApplicationContext(), fromJson.msg, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				mListView.onRefreshComplete();
				Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		mPage = 1;
		startHttp(mPage);
	}


	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		startHttp(++mPage);
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		VPLog.d("coupon", "" +position);
		moreOption(position-1);
	}
	
	public void moreOption(final int position){
		new IOSActionSheetDialog(this)
		.builder()
		//.setTitle("确定删除和Ta的聊天记录吗？")
		.setCancelable(false)
		.setCanceledOnTouchOutside(false)
		.addSheetItem("复制优惠码", SheetItemColor.Black,
				new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						try {
							PromoCodeBean bean =  mData.get(position);
							ClipboardUtil.copy(bean.coupon, MyCouponActivity.this);
							com.vp.loveu.util.ToastUtil.showToast(MyCouponActivity.this, "复制成功", 0);
						} catch (Exception e) {
							com.vp.loveu.util.ToastUtil.showToast(MyCouponActivity.this, "复制失败", 0);
						}
						
					}
	  }).addSheetItem("编辑",SheetItemColor.Black,
				new OnSheetItemClickListener() {
			@Override
			public void onClick(int which) {
				try {
					PromoCodeBean bean =  mData.get(position);
					Intent intent = new Intent(MyCouponActivity.this,ModifyCouponCodeActivity.class);
					intent.putExtra(ModifyCouponCodeActivity.PARAMS, gson.toJson(bean).toString());
					startActivityForResult(intent, EIDT_COUPON_REQUEST_CODE);
				} catch (Exception e) {
					e.printStackTrace();
					com.vp.loveu.util.ToastUtil.showToast(MyCouponActivity.this, "编辑失败", 0);
				}
			}
	 }).addSheetItem("删除",SheetItemColor.Black,
				new OnSheetItemClickListener() {
			@Override
			public void onClick(int which) {
				try {
					PromoCodeBean bean =  mData.get(position);
					deletePromoCode(bean.id); 
				} catch (Exception e) {
				}
			}
	 })
	 .show();
		
	}
	
	/**
	 * 删除优惠码
	 * @param promoCodeId  优惠码id
	 * void
	 */
	private void deletePromoCode(final int promoCodeId){
		if (mClient == null) {
			mClient = new VpHttpClient(this);
		}
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo == null) {
			return;
		}
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("uid",loginInfo.getUid());
		jsonObj.addProperty("id",promoCodeId);
		mClient.post(VpConstants.DELETE_PROMO_CODE, new RequestParams(), jsonObj.toString(),true, new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String deAesResult = ResultParseUtil.deAesResult(responseBody);
				 NetBaseBean baseBean =  NetBaseBean.parseJson(deAesResult);
				 if (baseBean.isSuccess()) {
					PromoCodeBean bean = new PromoCodeBean();
					bean.id = promoCodeId;
					mData.remove(bean);//删除
					mAdapter.notifyDataSetChanged();
				 }
				 baseBean.showMsgToastInfo();
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				new NetBaseBean().showMsgToastInfo();
			}
		});
	}


	@Override
	public void onClick(View v) {
		
		if (v.equals(addCouponView)) {
			Intent intent = new Intent(this,SelectCourseActivity.class);
			startActivityForResult(intent, EIDT_COUPON_REQUEST_CODE);

		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		
		if (data ==null) {
			return ;
		}
		if (requestCode == EIDT_COUPON_REQUEST_CODE) {
			String result =  data.getStringExtra(ModifyCouponCodeActivity.PARAMS);
			VPLog.d("coupon", "r:"+result);
			try {
				PromoCodeBean bean = gson.fromJson(result, PromoCodeBean.class);
				if (bean.id == 0 || bean.code <0) {
					return;
				}
				if (mData.contains(bean)) {
					int index =mData.indexOf(bean);
					mData.remove(bean);
					mData.add(index, bean);
				}else {
					mData.add(0,bean);
				}
				mAdapter.notifyDataSetChanged();
			} catch (Exception e) {
			}
		}
	}
	
}
