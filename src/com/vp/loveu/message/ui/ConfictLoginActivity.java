package com.vp.loveu.message.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.vp.loveu.MainActivity;
import com.vp.loveu.R;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.widget.IOSAlertDialog;
/**
 * 冲突登录
 * @author tanping
 * 2015-12-29
 */
public class ConfictLoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confict_login);
		
		IOSAlertDialog iosAlertDialog = new IOSAlertDialog(this);
		
		iosAlertDialog.builder().setTitle("通知").setMsg("你的账号在另一台设备上已登录").setNegativeButton(" 退出", new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LoginStatus.loginOut();
				Intent intent = new Intent(ConfictLoginActivity.this,MainActivity.class);
				intent.putExtra("command", "quit");
				startActivity(intent);
				finish();
			}
		}).setPositiveButton("重新登录", new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LoginStatus.loginOut();
				//重新登录
				Intent intent = new Intent(ConfictLoginActivity.this,MainActivity.class);
				intent.putExtra("command", "logout");
				startActivity(intent);
				finish();
			}
		}).setCancelable(false).show();
		
	}

	 
}
