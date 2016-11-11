package com.vp.loveu.message.bean;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.vp.loveu.message.ui.LocationSourceActivity;
import com.vp.loveu.message.ui.PrivateChatActivity;
import com.vp.loveu.util.VPLog;

/** 
 * @author tanping
 * plus 模块基础bean
 */
public class OperateDataBaseEntity  extends  ViewBindBean{

	public static final String TAG = "OperateDataBaseEntity";
	
	 @Override
	public void onClick(View v) {
		 VPLog.d(TAG, "moren dianji ");
		 Intent intent = new Intent(v.getContext(),LocationSourceActivity.class);
	     ((FragmentActivity) v.getContext()).startActivityForResult(intent, PrivateChatActivity.SELECT_AREA_REQUEST);
	}
}
