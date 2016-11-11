package com.vp.loveu.my.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.message.utils.ToastUtil;
import com.vp.loveu.my.bean.LoveInfoBean;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.SharedPreferencesHelper;

import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年12月8日上午10:11:32
 * @功能 内心独白的界面
 * @作者 mi
 */

public class HeartShowActivity extends VpActivity {

	public static final String KEY = "key_edit";
	public static final String TYPE = "type";
	public static final String KEY_JSON = "json";
	public static final int FEEDBACK = 1;// 意见反馈 other 内心独白
	public static final String CONTENT = "content";
	private EditText mContent;
	private TextView mTvShow;
	private Gson gson = new Gson();
	private boolean isEdit;// 是否是可编辑的样式？
	private int type;
	private String content;
	private VpHttpClient mClient;
	private SharedPreferencesHelper sp;
	private String json;
	private int id;
	private boolean isNormal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isEdit = getIntent().getBooleanExtra(KEY, false);
		type = getIntent().getIntExtra(TYPE, 2);
		json = getIntent().getStringExtra(KEY_JSON);
		content = getIntent().getStringExtra(CONTENT);
		isNormal = getIntent().getBooleanExtra("normal", false);
		id = getIntent().getIntExtra("id", 0);
		sp = SharedPreferencesHelper.getInstance(HeartShowActivity.this);
		setContentView(R.layout.heart_activity);
		initPublicTitle();
		initView();
	}

	private void initView() {
		mContent = (EditText) findViewById(R.id.heart_edit_content);
		mTvShow = (TextView) findViewById(R.id.heart_show_Text);
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		if (type == 1) {
			// 意见反馈
			mPubTitleView.mTvTitle.setText("意见反馈");
			mPubTitleView.mBtnRight.setText("提交");
		} else {
			mPubTitleView.mTvTitle.setText("内心独白");
			mPubTitleView.mBtnRight.setText("保存");
		}
		mPubTitleView.mBtnRight.setTextColor(getResources().getColor(R.color.normal_my_gray));
		mPubTitleView.mBtnRight.setEnabled(false);
		if (isEdit) {
			// 可编辑
			mContent.setVisibility(View.VISIBLE);
			mTvShow.setVisibility(View.GONE);
			if (!TextUtils.isEmpty(content) && !"未设置".equals(content)) {
				mContent.setText(content);
				mContent.setSelection(content.length());
			} else {
				mContent.setHint("说点什么...");
			}
			
			addTextChangedListener(mContent);
			
		} else {
			// 只能展示
			mContent.setVisibility(View.GONE);
			mTvShow.setVisibility(View.VISIBLE);
			mTvShow.setText(TextUtils.isEmpty(content) ? "当前用户没有内心独白哦" : content);
			mPubTitleView.mBtnRight.setVisibility(View.GONE);
		}
		mContent.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				mPubTitleView.mBtnRight.setEnabled(true);
				mPubTitleView.mBtnRight.setTextColor(getResources().getColor(R.color.normal_my_green));
			}
		});

		mPubTitleView.mBtnRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (type == 1) {
					// 意见反馈
					sendFeekback();
				} else {
					// 内心独白
					saveHeart();
				}
			}
		});
	}

	private boolean isShow = true;
	private void addTextChangedListener(final EditText edt) {
		edt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String trim = edt.getText().toString().trim();
				if (!TextUtils.isEmpty(trim)) {
					if (trim.length() > 250 && isShow) {
						ToastUtil.show(getApplicationContext(), "字数不能超过250个字");
						isShow = false;
					}else if(trim.length() < 250){
						isShow = true;
					}
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}

	protected void saveHeart() {
		String trim = mContent.getText().toString().trim();
		if (TextUtils.isEmpty(trim)) {
			Toast.makeText(getApplicationContext(), "内容不能为空", 0).show();
			return;
		}
		if (trim.equals(content)) {
			Toast.makeText(getApplicationContext(), "没有修改,无需保存", 0).show();
			return;
		}
		if (trim.length() >= 250) {
			Toast.makeText(getApplicationContext(), "最长不要超过250个字哦", 0).show();
			return;
		}
		if (mClient == null) {
			mClient = new VpHttpClient(this);
		}
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(json);
			LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
			if (loginInfo != null) {
				jsonObject.put("uid", loginInfo.getUid());
				jsonObject.put("signature", trim);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mClient.post(VpConstants.LOVE_INFO_SAVE_URL, new RequestParams(), jsonObject != null ? jsonObject.toString() : "",
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						LoveInfoBean fromJson = gson.fromJson(ResultParseUtil.deAesResult(responseBody), LoveInfoBean.class);
						if (fromJson.code == 0) {
							Toast.makeText(getApplicationContext(), "资料保存成功", Toast.LENGTH_SHORT).show();
							sp.putStringValue(KEY, mContent.getText().toString().trim());
							// mContent.getText().clear();
						} else {
							Toast.makeText(getApplicationContext(), fromJson.msg, Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
						Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
					}
				});
	}

	protected void sendFeekback() {
		String trim = mContent.getText().toString().trim();
		if (TextUtils.isEmpty(trim)) {
			Toast.makeText(getApplicationContext(), "内容不能为空", 0).show();
			return;
		}
		if (trim.length() >= 200) {
			Toast.makeText(getApplicationContext(), "最长不要超过200个字哦", 0).show();
			return;
		}
		if (mClient == null) {
			mClient = new VpHttpClient(this);
		}

		JsonObject obj = new JsonObject();
		obj.addProperty("soft_id", 1);// 分配的id
		obj.addProperty("soft_name", getResources().getString(R.string.app_name));// 软件名称
		obj.addProperty("type", 2);// 客户端类型 1web 2 app
		obj.addProperty("brand", android.os.Build.BRAND);// 手机品牌
		obj.addProperty("model", android.os.Build.MODEL);// 手机型号
		obj.addProperty("ver", android.os.Build.VERSION.RELEASE + "");// 系统版本
		obj.addProperty("content", trim);
		obj.addProperty("contact", "");
		if (isNormal) {
			obj.addProperty("target_type", 1);//目标类型 普通建议或订单建议
			obj.addProperty("target_id", id);//对应的uid
		}else{
			obj.addProperty("target_type", 0);//目标类型 普通建议或订单建议
		}
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo != null) {
			obj.addProperty("uid", loginInfo.getUid());// 用户的id
		}
		mClient.post(VpConstants.SEND_FEEKBACK, new RequestParams(), obj.toString(), true, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				Log.d("aaa", "success"+ResultParseUtil.deAesResult(responseBody));
				LoveInfoBean fromJson = gson.fromJson(ResultParseUtil.deAesResult(responseBody), LoveInfoBean.class);
				if (fromJson.code == 0) {
					Toast.makeText(getApplicationContext(), "意见发送成功", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), fromJson.msg, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
