package com.vp.loveu.coupon.ui;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
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
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.coupon.adapter.SelectCourseAdapter;
import com.vp.loveu.coupon.bean.SelectCourseBean;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.index.widget.EmptyTextView;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.my.bean.PromoCodeBean;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.ToastUtil;
import com.vp.loveu.util.VPLog;

import cz.msebera.android.httpclient.Header;

/**
 * 选着课程哦
 * @author tanping
 * 2016-3-7
 */
public class SelectCourseActivity extends VpActivity implements OnRefreshListener2<ListView>,OnItemClickListener,OnClickListener{

	private PullToRefreshListView mListView;
	SelectCourseAdapter mAdapter;
	private int mPage =1;
	private int mLimit =20;
	private List<SelectCourseBean> mData;
	Gson gson = new Gson();
	
	View addCouponView;
	public PromoCodeBean promoCodeBean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_course);
		mData = new LinkedList<SelectCourseBean>();
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
		mPubTitleView.mTvTitle.setText(R.string.title_activity_select_course);
		mListView = (PullToRefreshListView) findViewById(R.id.coupon_list);
		mListView.setMode(Mode.BOTH);
		mListView.setOnItemClickListener(this);
		ILoadingLayout up = mListView.getLoadingLayoutProxy(false, true);
		ILoadingLayout down = mListView.getLoadingLayoutProxy(true, false);
		 
		EmptyTextView tv = new EmptyTextView(this);
		tv.setWidth(MyUtils.getScreenWidthAndHeight(this)[0]);
		tv.setHeight(MyUtils.getScreenWidthAndHeight(this)[1]);
		tv.setGravity(Gravity.CENTER);
		tv.setText("没有课程数据");
		tv.setTextSize(14);
		mListView.setEmptyView(tv);
		mListView.setOnRefreshListener(this);
		
		mAdapter = new SelectCourseAdapter(this, mData, mListView.getRefreshableView());
		mListView.setAdapter(mAdapter);
		
		addCouponView = findViewById(R.id.btn_add_coupon);
		addCouponView.setOnClickListener(this);
		mListView.getRefreshableView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);//开启单选模式

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
		
		mClient.get(VpConstants.MY_COURSE, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				mListView.onRefreshComplete();
				String result = ResultParseUtil.deAesResult(responseBody);
				
				SelectCourseBean fromJson = gson.fromJson(result, SelectCourseBean.class);
				if (fromJson.code == 0) {
					//Type listType = new TypeToken<LinkedList<SelectCourseBean>>(){}.getType(); 
					 //fromJson.adatper_data = gson.fromJson(fromJson.data, listType); 
					
					if (mPage == 1) {
						mData.clear();
						mData.addAll(fromJson.data);
						mAdapter.notifyDataSetChanged();
						if (mData.size() ==1) {//只有一个
							oldPosition =0;
							onClick(addCouponView);//去提交
						}
						
					} else {
						if (fromJson.data == null || fromJson.data.size() <= 0) {
							Toast.makeText(getApplicationContext(), R.string.not_more_data, Toast.LENGTH_SHORT).show();
						} else {
							mData.addAll(fromJson.data);
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
		oldPosition =-1;
		startHttp(mPage);
	}


	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		startHttp(++mPage);
	}


	int oldPosition = -1;
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		VPLog.d("coupon", "" +position);

		if (oldPosition >=0) {
			mData.get(oldPosition).isCheck = false;
		}	
		oldPosition = position-1;
		mData.get(oldPosition).isCheck =true;
		mAdapter.notifyDataSetChanged();
	}
	
	
	 


	@Override
	public void onClick(View v) {
		
		if (v.equals(addCouponView)) {
			
			if (oldPosition <0) {
				ToastUtil.showToast(this, "请选择课程", 0);
				return ;
			}
			SelectCourseBean selectCourseBean = mData.get(oldPosition);
			
			PromoCodeBean bean = new PromoCodeBean();
			bean.name = selectCourseBean.name;
			bean.src_id = selectCourseBean.id;
			bean.original_price = selectCourseBean.price;
			bean.type = 1;//在线课程
			
			String course  = gson.toJson(bean);
			Intent intent = new Intent(this,ModifyCouponCodeActivity.class);
			intent.putExtra(ModifyCouponCodeActivity.PARAMS, course);
			startActivityForResult(intent, MyCouponActivity.EIDT_COUPON_REQUEST_CODE);
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		
		if (data ==null) {
			return ;
		}
		if (requestCode == MyCouponActivity.EIDT_COUPON_REQUEST_CODE) {
			String result =  data.getStringExtra(ModifyCouponCodeActivity.PARAMS);
			try {
				promoCodeBean = gson.fromJson(result, PromoCodeBean.class);
				if (promoCodeBean.id !=0) {
					finish();
				}
			} catch (Exception e) {
			}
		}
	}
	@Override
	public void finish() {
		
		Intent intent = new Intent();
		intent.putExtra(ModifyCouponCodeActivity.PARAMS, gson.toJson(promoCodeBean).toString());
		setResult(MyCouponActivity.EIDT_COUPON_REQUEST_CODE, intent);
		
		super.finish();

	}
	
}
