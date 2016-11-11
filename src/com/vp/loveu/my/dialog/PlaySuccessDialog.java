package com.vp.loveu.my.dialog;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.vp.loveu.R;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.my.bean.MyCenterPayBean.MyCenterPayDataBean;
import com.vp.loveu.util.AESUtil;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.widget.CricleProgressView;
import com.zbar.lib.bitmap.CreateQrImage;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月25日下午2:24:00
 * @功能 我的支付列表 支付有效的dialog
 * @作者 mi
 */

public class PlaySuccessDialog extends Dialog implements android.view.View.OnClickListener {

	private ImageView mIvClose;
	private CricleProgressView mProgress;
	private ImageView mIvZxing;
	private TextView mTvTitle;
	private TextView mTvTime;
	private MyCenterPayDataBean data;
	private LoginUserInfoBean loginInfo;

	public PlaySuccessDialog(Context context) {
		super(context, R.style.dialog);
	}

	public PlaySuccessDialog(Context context, MyCenterPayDataBean data) {
		this(context);
		this.data = data;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo == null) {
			return;
		}
		setContentView(R.layout.play_success_layout);
		setCanceledOnTouchOutside(false);
		setCancelable(false);
		LayoutParams attributes = getWindow().getAttributes();
		attributes.width = getScreenWidth() - 36 * 2;
		getWindow().setAttributes(attributes);
		initView();
	}

	private void initView() {
		mIvClose = (ImageView) findViewById(R.id.play_success_iv_close);
		mProgress = (CricleProgressView) findViewById(R.id.play_success_dialog_progress);
		mIvZxing = (ImageView) findViewById(R.id.play_success_iv_dimension);
		mTvTitle = (TextView) findViewById(R.id.play_success_tv_title);
		mTvTime = (TextView) findViewById(R.id.play_success_tv_time);
		mTvTitle.setText(data.product_name);
		String json = data.json_cont;
		if (!TextUtils.isEmpty(json)) {
			try {
				JSONObject obj = new JSONObject(json);
				String beginTime = obj.getString("begin_time");
				String endTime = obj.getString("end_time");
				mTvTime.setText(MyUtils.dateFromLong(beginTime)); // 活动开始时间
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		// loveu://pua_course?id =3&title=fdsfdsf id 为3的pua
		String url = "loveu://activity_sign_in?order_id=" + data.id + 
				"&uid=" + loginInfo.getUid() + "&activity_id=" + data.product_id;
		 try {
			 
			String encrypt = AESUtil.Encrypt(url, VpConstants.KEY_QR_PASS);
			mIvZxing.setImageBitmap(CreateQrImage.create(encrypt, 0, 0));
			mIvClose.setOnClickListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int getScreenWidth() {
		return getWindow().getWindowManager().getDefaultDisplay().getWidth();
	}

	public int getOrderNumber() {
		return data.id;
	}

	public MyCenterPayDataBean getData() {
		return data;
	}

	@Override
	public void onClick(View v) {
		if (v.equals(mIvClose)) {
			if (isShowing()) {
				dismiss();
				// mProgress.stopRun();
			}
		}
	}

	public void stopProgress() {
		mProgress.stopRun();
	}
}
