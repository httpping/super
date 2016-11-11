package com.vp.loveu.comm;

import android.app.Activity;
import android.os.Bundle;

import com.vp.loveu.bean.InwardAction;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.VPLog;
/**
 * 服务器内部跳转
 * @author tanping
 * 2015-12-17
 */
public class InWardActionActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_in_ward_action);
		VPLog.d("tag", "inward action");
		//loveu 解析协议
		LoginUserInfoBean mine = LoginStatus.getLoginInfo();
		String uri =  getIntent().getDataString();
		if (mine !=null && uri!=null) { 
			InwardAction action = InwardAction.parseAction(uri);
			if (action!=null) {
				action.toStartActivity(this);
			}
		}
		finish();
	}

	 
}
