package com.vp.loveu.coupon.ui;

import java.text.DecimalFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.bean.NetBaseBean;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.coupon.widget.DateTimePickDialogUtil;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.message.utils.VlinkTimeUtil;
import com.vp.loveu.my.bean.PromoCodeBean;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.ToastUtil;

import cz.msebera.android.httpclient.Header;

/**
 * 优惠码
 * @author tanping
 * 2016-3-7
 */
public class ModifyCouponCodeActivity extends VpActivity  implements OnClickListener{

	/**
	 * json string 参数
	 */
	public static final String PARAMS ="params"; 
	
	PromoCodeBean promoCodeBean;
	
	//view
	TextView courseNameTextView;
	TextView coursePriceTextView;
	
	TimePickerDialog timePickerDialog;
	private String initStartDateTime = "2013年9月3日 14:44"; // 初始化开始时间  
    private String initEndDateTime = "2014年8月23日 17:44"; // 初始化结束时间  
	
	
    public EditText mPricEditText;
    public TextView mBeginTimeEditText;
    public TextView mEndTimeEditText;
    public EditText mCouponCodeEditText;
    
    TextView mPriceErrorRemindTextView;
    TextView mCouponCodeErrorRemind;
    
    Button mSubmitButton;
    
    RelativeLayout mBeginLayout;
    RelativeLayout mEndLayout;
    DateTimePickDialogUtil mBeginDateTimePickDialogUtil;
    DateTimePickDialogUtil mEndDateTimePickDialogUtil;
    
	private Gson gson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_coupon_code);
		initPublicTitle();
		
		String param = getIntent().getStringExtra(PARAMS);
		gson = new Gson();
		try {
			promoCodeBean = gson.fromJson(param, PromoCodeBean.class);
			if (promoCodeBean.src_id <=0) {
				ToastUtil.showToast(this, "请选择课程", 0);
				finish();
				return ;
			}
		} catch (Exception e) {
			ToastUtil.showToast(this, "请选择课程", 0);
			finish();
			return ;
		}
		
		initView();
	}
	

	private void initView() {
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mBtnLeft.setText(null);
		mPubTitleView.mBtnLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		if (promoCodeBean.id ==0) {
			mPubTitleView.mTvTitle.setText(R.string.title_activity_add_coupon_code);	
		}else {
			mPubTitleView.mTvTitle.setText(R.string.title_activity_modify_coupon_code);
		}
		
		
		courseNameTextView = (TextView) findViewById(R.id.course_name);
		coursePriceTextView = (TextView) findViewById(R.id.course_price);
		mBeginLayout = (RelativeLayout) findViewById(R.id.begin_layout);
		mEndLayout = (RelativeLayout) findViewById(R.id.endtime_layout);
		mBeginTimeEditText = (TextView) findViewById(R.id.coupon_begin_time);
		mEndTimeEditText = (TextView) findViewById(R.id.coupon_end_time);
		
		mPricEditText = (EditText) findViewById(R.id.modify_price);
		mPriceErrorRemindTextView = (TextView) findViewById(R.id.modify_price_error_remind);
		
		mCouponCodeEditText = (EditText) findViewById(R.id.coupon_code);
		mCouponCodeErrorRemind = (TextView) findViewById(R.id.coupon_error_remind);
		
		courseNameTextView.setText(promoCodeBean.name);
		coursePriceTextView.setText("￥"+promoCodeBean.original_price+"");
		mCouponCodeEditText.setText(promoCodeBean.coupon);
		
		if (promoCodeBean.price !=0) {
			mPricEditText.setText(promoCodeBean.price+"");
		}
		
		if (!TextUtils.isEmpty(promoCodeBean.begin_time)) {
			try {
				mBeginTimeEditText.setText( VlinkTimeUtil.fromatDateChina(VlinkTimeUtil.parseYYYYMMDDHHMM(promoCodeBean.begin_time)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			try {//当前时间
				mBeginTimeEditText.setText( VlinkTimeUtil.fromatDateChina(new Date(System.currentTimeMillis())));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (!TextUtils.isEmpty(promoCodeBean.end_time)) {
			try {
				mEndTimeEditText.setText( VlinkTimeUtil.fromatDateChina(VlinkTimeUtil.parseYYYYMMDDHHMM(promoCodeBean.end_time)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	
		
		
		mBeginLayout.setOnClickListener(this);
		mEndLayout.setOnClickListener(this);
		mBeginTimeEditText.setOnClickListener(this);
		mEndTimeEditText.setOnClickListener(this);
		
		mSubmitButton = (Button) findViewById(R.id.btn_add_coupon);
		mSubmitButton.setOnClickListener(this);
		
		//隐藏错误提醒
		mPricEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mPriceErrorRemindTextView.setText(null);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}


	@Override
	public void onClick(View v) {
		
		if (v.equals(mBeginLayout) || v.equals(mBeginTimeEditText)) {
			if (mBeginDateTimePickDialogUtil ==null) {
				mBeginDateTimePickDialogUtil = new DateTimePickDialogUtil(this, null);
			}
			mBeginDateTimePickDialogUtil.dateTimePicKDialog(mBeginTimeEditText);
		}else if (v.equals(mEndLayout) || v.equals(mEndTimeEditText)) {
			if (mEndDateTimePickDialogUtil ==null) {
				mEndDateTimePickDialogUtil = new DateTimePickDialogUtil(this, null);
			}
			mEndDateTimePickDialogUtil.dateTimePicKDialog(mEndTimeEditText);
		}else if (v.equals(mSubmitButton)) {
			submit();
		}
		
	}
	
	
	
	/**
	 * 添加优惠码
	 * @param promoCodeId  优惠码id
	 * void
	 * @throws JSONException 
	 */
	private void addPromoCode() throws JSONException{
		if (mClient == null) {
			mClient = new VpHttpClient(this);
		}

		JSONObject params =  new JSONObject();
		
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo == null) {
			return;
		}
		//promoCodeBean.uid = loginInfo.getUid();
		params.put("uid", loginInfo.getUid());
		params.put("coupon", promoCodeBean.coupon);
		params.put("type", promoCodeBean.type);
		params.put("price",promoCodeBean.price);
		params.put("src_id", promoCodeBean.src_id);
		params.put("begin_time",promoCodeBean.begin_time);
		params.put("end_time", promoCodeBean.end_time);
		//params.put("end_time", VlinkTimeUtil.fromatYYYYMMDDHHMM(new Date(System.currentTimeMillis()+ 1000*10)) );
		mClient.post(VpConstants.ADD_PROMO_CODE, new RequestParams(), params.toString(),true, new AsyncHttpResponseHandler() {
			

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String deAesResult = ResultParseUtil.deAesResult(responseBody);
				PromoCodeBean fromJson = gson.fromJson(deAesResult, PromoCodeBean.class);
				try {
					if (fromJson.code ==0) {
						PromoCodeBean bean = gson.fromJson(fromJson.data, PromoCodeBean.class);
						promoCodeBean.id = bean.id;
						promoCodeBean.code =0;
						finish();
					}
				} catch (Exception e) {
				}
				NetBaseBean.parseJson(deAesResult).showMsgToastInfo();
				
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(ModifyCouponCodeActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	
	/**
	 * 编辑优惠码
	 * @param promoCodeId  优惠码id
	 * void
	 * @throws JSONException 
	 */
	private void editPromoCode() throws JSONException{
		if (mClient == null) {
			mClient = new VpHttpClient(this);
		}

		JSONObject params =  new JSONObject();
		
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo == null) {
			return;
		}
		//promoCodeBean.uid = loginInfo.getUid();
		params.put("uid", loginInfo.getUid());
		params.put("id", promoCodeBean.id);
		params.put("coupon", promoCodeBean.coupon);
		params.put("type", promoCodeBean.type);
		params.put("price",promoCodeBean.price);
		params.put("src_id", promoCodeBean.src_id);
		params.put("begin_time",promoCodeBean.begin_time);
		params.put("end_time", promoCodeBean.end_time);
		//params.put("end_time", VlinkTimeUtil.fromatYYYYMMDDHHMM(new Date(System.currentTimeMillis()+ 1000*10)) );
		mClient.post(VpConstants.EDIT_PROMO_CODE, new RequestParams(), params.toString(),true, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String deAesResult = ResultParseUtil.deAesResult(responseBody);
				PromoCodeBean fromJson = gson.fromJson(deAesResult, PromoCodeBean.class);
				NetBaseBean.parseJson(deAesResult).showMsgToastInfo();
				if (fromJson.code == 0) {
					promoCodeBean.code =0;
					finish();
				}
				
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(ModifyCouponCodeActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	
	
	
	/*
	 * 提交
	 */
	public void submit(){
		
		if (TextUtils.isEmpty(mPricEditText.getText())) {
			mPriceErrorRemindTextView.setText("价格必须填写");
			return ;
		}else {
			double price = Double.parseDouble(mPricEditText.getText().toString());
			if (price <=0) {
				ToastUtil.showToast(this, "价格必须大于0", 1);
				return;
			}
			if (price > promoCodeBean.original_price) {
				mPriceErrorRemindTextView.setText("您输入的价格已高于原价！");
				return ;
			}
			DecimalFormat df=new DecimalFormat("#.00");
			
			promoCodeBean.price = Double.parseDouble(df.format(price));
		}
		
		if (TextUtils.isEmpty(mCouponCodeEditText.getText())) {
			ToastUtil.showToast(this, "优惠码不能为空", 1);
			return;
		}
		if (mCouponCodeEditText.getText().toString().indexOf(" ")>=0 ||mCouponCodeEditText.getText().toString().indexOf("　")>=0) {
			ToastUtil.showToast(this, "优惠码不能包含空格", 1);
			return ;
		}
		promoCodeBean.coupon = mCouponCodeEditText.getText().toString();
		
		if (!TextUtils.isEmpty(mBeginTimeEditText.getText())) {
			
			if (!TextUtils.isEmpty(mEndTimeEditText.getText())) {
				if (mBeginTimeEditText.getText().toString().compareTo(mEndTimeEditText.getText().toString())>0) {
					ToastUtil.showToast(this, "开始时间大于结束时间", 1);
					return ;
				}else {
					promoCodeBean.end_time = VlinkTimeUtil.fromatYYYYMMDDHHMM(VlinkTimeUtil.parseDateChina(mEndTimeEditText.getText().toString()));
				}
			}
			promoCodeBean.begin_time = VlinkTimeUtil.fromatYYYYMMDDHHMM(VlinkTimeUtil.parseDateChina(mBeginTimeEditText.getText().toString()));
		}
		
		if (TextUtils.isEmpty(promoCodeBean.end_time)) {
			ToastUtil.showToast(this, "结束时间不能为空", 0);
			return ;
		}
		
		if (promoCodeBean.id ==0) {
			try {
				addPromoCode();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return;
		}else {
			try {
				editPromoCode();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	@Override
	public void finish() {
		
		Intent intent = new Intent();
		intent.putExtra(PARAMS, gson.toJson(promoCodeBean).toString());
		setResult(MyCouponActivity.EIDT_COUPON_REQUEST_CODE, intent);
		
		super.finish();

	}
}
