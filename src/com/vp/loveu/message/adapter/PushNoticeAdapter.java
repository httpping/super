package com.vp.loveu.message.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.vp.loveu.message.bean.PushNoticeBean;
import com.vp.loveu.message.db.PushNoticeBeanDao;
import com.vp.loveu.message.view.ItemMessageNoticeView;

 

public class PushNoticeAdapter extends CursorAdapter {
	
	private Context mContext;
	private Cursor c;

	public PushNoticeAdapter(Context context, Cursor c) {
		super(context, c);
		this.mContext = context;
		this.c = c ;
		
	}

	@Override
	public void bindView(View arg0, Context arg1, Cursor arg2) {
		PushNoticeBean msgData =  PushNoticeBeanDao.getInstance(mContext).getObject(arg2);
		ItemMessageNoticeView noticeView =  (ItemMessageNoticeView) arg0;
	   if (noticeView !=null) {
		   Log.d("newview", "view： bindView old");
		   noticeView.setMsgSession(msgData);
		   //itemMessageSessionView.setMsgSession(messageSession);
	   }else {
		   Log.d("newview", "view： bindView new");
	}
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		Log.d("newview", "view： new");
		ItemMessageNoticeView session = new ItemMessageNoticeView(mContext);
		return session ;
	}
	  

}
