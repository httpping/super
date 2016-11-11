package com.vp.loveu.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.base.VpApplication;
import com.vp.loveu.util.VPLog;

public class WXPayEntryActivity extends VpActivity implements IWXAPIEventHandler {

    private static String TAG = "微信支付结果";
    private IWXAPI api;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.pay_result);

	init();
	api = WXAPIFactory.createWXAPI(this, WxConsants.APP_ID);
	api.handleIntent(getIntent(), this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
	super.onNewIntent(intent);
	setIntent(intent);
	api.handleIntent(intent, this);
    }

    private void init() {
    }

    public void onReq(BaseReq req) {
	VPLog.i(TAG, "req.openId = " + req.openId);
    }

    public void onResp(BaseResp resp) {
    	VPLog.i(TAG, "errCode = " + resp.errCode);
    	VPLog.i(TAG, "errStr = " + resp.errStr);
	switch (resp.errCode) {
	case 0:
		
		//第三方平台充值
		if (VpApplication.getInstance().mPayBindViewBean!=null) {
			VpApplication.getInstance().mPayBindViewBean.isWxPay = true;
			VpApplication.getInstance().mPayBindViewBean.payResult = true;
			Toast.makeText(this, "支付成功", 1).show();
			finish();
			return;
		}
		//第三方平台充值
		if (VpApplication.getInstance().mEnjoyPayBindBean!=null) {
			VpApplication.getInstance().mEnjoyPayBindBean.isWxPay = true;
			VpApplication.getInstance().mEnjoyPayBindBean.payResult = true;
			Toast.makeText(this, "支付成功", 1).show();
			finish();
			return;
		}
		
	    Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
	    finish();
	    break;

	default:
		//第三方平台充值
		if (VpApplication.getInstance().mPayBindViewBean!=null) {
			VpApplication.getInstance().mPayBindViewBean.isWxPay = true;
			VpApplication.getInstance().mPayBindViewBean.payResult = false;//支付失败
			Toast.makeText(this, "支付失败", 1).show();
			finish();
			return;
		}
		if (VpApplication.getInstance().mEnjoyPayBindBean!=null) {
			VpApplication.getInstance().mEnjoyPayBindBean.isWxPay = true;
			VpApplication.getInstance().mEnjoyPayBindBean.payResult = false;//支付失败
			Toast.makeText(this, "支付失败", 1).show();
			finish();
			return;
		}
	    Toast.makeText(this, "支付失败", Toast.LENGTH_SHORT).show();
	    finish();
	    break;
	}
    }
    
  
   

}