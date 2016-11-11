package com.vp.loveu.discover.ui;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.bean.InwardAction;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.discover.bean.SearchPromoCodeBean;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.util.LoginStatus;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * @时间2016年3月3日下午2:32:02
 * @功能 搜索优惠码
 * @作者 mi
 */

public class SearchPromoCodeActivity extends VpActivity implements OnClickListener {
	
	EditText mEditContent;
	Button mBtnCommit;
	TextView mTvRemind;
	String content;
	Gson gson = new Gson();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_promocode);
		initPublicTitle();
		initView();
	}

	private void initView() {
		mPubTitleView.mBtnLeft.setText("");
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mTvTitle.setText("优惠码频道");
		mEditContent = (EditText) findViewById(R.id.search_et);
		mBtnCommit = (Button) findViewById(R.id.search_bt_commit);
		mTvRemind = (TextView) findViewById(R.id.search_tv);
		mBtnCommit.setOnClickListener(this);
		mEditContent.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mTvRemind.setText("");
				mTvRemind.setVisibility(View.INVISIBLE);
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		content = mEditContent.getText().toString().trim();
		if (TextUtils.isEmpty(content)) {
			Toast.makeText(this, R.string.public_str_search_promocode,Toast.LENGTH_SHORT).show();
			return;
		}
		if (v.equals(mBtnCommit)) {
			usePromoCode();
		}
	}
	
	/**
	 * 使用优惠码
	 */
	private void usePromoCode(){
		if (mClient == null) {
			mClient = new VpHttpClient(this);
		}
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo == null) {
			return;
		}
		JsonObject obj = new JsonObject();
		obj.addProperty("coupon", content);
		obj.addProperty("uid", loginInfo.getUid());
		
		mClient.post(VpConstants.USE_PROMO_CODE, new RequestParams(), obj.toString(), new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				try {
					String deAesResult = ResultParseUtil.deAesResult(responseBody);
					SearchPromoCodeBean bean = gson.fromJson(deAesResult, SearchPromoCodeBean.class);
					if (bean != null && bean.code == 0) {
						InwardAction parseAction = InwardAction.parseAction(bean.data.url);
						if (parseAction!= null) {
							parseAction.setPromoCode(content);//设置优惠码
							parseAction.toStartActivity(SearchPromoCodeActivity.this);
						}
					}else{
						mTvRemind.setVisibility(View.VISIBLE);
						mTvRemind.setText(bean.msg+"");
					}
				} catch (Exception e) {
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(SearchPromoCodeActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
