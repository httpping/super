package com.vp.loveu.message.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.vp.loveu.message.bean.ChatSessionBean;
import com.vp.loveu.message.db.ChatSessionDao;
import com.vp.loveu.message.view.ItemMessageSessionView;

 

public class MessageSessionAdapter extends CursorAdapter {
	
	private Context mContext;
	private Cursor c;

	public MessageSessionAdapter(Context context, Cursor c) {
		super(context, c);
		this.mContext = context;
		this.c = c ;
		
	}

	@Override
	public void bindView(View arg0, Context arg1, Cursor arg2) {
	   ChatSessionBean messageSession =  ChatSessionDao.getInstance(mContext).getObject(arg2);
	   ItemMessageSessionView itemMessageSessionView =  (ItemMessageSessionView) arg0;
	   if (itemMessageSessionView !=null) {
		   Log.d("newview", "view： bindView old");
		   itemMessageSessionView.setMsgSession(messageSession);
	   }else {
		   Log.d("newview", "view： bindView new");
	}
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		Log.d("newview", "view： new");
		ItemMessageSessionView itemMessageSessionView = new ItemMessageSessionView(mContext);
		return itemMessageSessionView ;
	}
	  

}
