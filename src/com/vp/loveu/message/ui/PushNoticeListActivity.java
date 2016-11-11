package com.vp.loveu.message.ui;


import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.message.adapter.PushNoticeAdapter;
import com.vp.loveu.message.db.PushNoticeBeanDao;
import com.vp.loveu.util.LoginStatus;

/**
 * 官方通知
 * @author tanping
 * 2015-11-26
 */
public class PushNoticeListActivity extends VpActivity {

	ListView mListView;
	Cursor mCursor; //游标
	
	PushNoticeAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.push_activity_push_notice_list);
		
		initView();
	}

	private void initView() {
		initPublicTitle();
		mPubTitleView.mBtnLeft.setText("");
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mTvTitle.setText(R.string.title_activity_push_notice_list);
		
		mListView = (ListView) findViewById(R.id.push_notice_list);
		LoginUserInfoBean mine = LoginStatus.getLoginInfo();
		if (mine == null) {
			return ; 
		}
		mCursor = PushNoticeBeanDao.getInstance(this).findAllCursor(mine.getUid()+"");
		
		mAdapter = new PushNoticeAdapter(this, mCursor);
		mListView.setAdapter(mAdapter);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		try {
			mCursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
