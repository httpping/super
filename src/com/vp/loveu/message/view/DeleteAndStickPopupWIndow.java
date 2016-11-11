package com.vp.loveu.message.view;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.vp.loveu.R;
import com.vp.loveu.message.bean.ChatSessionBean;
import com.vp.loveu.message.db.ChatSessionDao;
import com.vp.loveu.message.provider.MessageSessionProvider;

/**
 * 删除或者置顶的功能
 * @author tanping
 * 2015-11-30
 */
public class DeleteAndStickPopupWIndow extends PopupWindow {

	Context mContext ;
	ChatSessionBean mSessionBean;
	public DeleteAndStickPopupWIndow(Context context,ChatSessionBean chatSessionBean){
		super(context);
		mContext = context;
		View rootview = LayoutInflater.from(context).inflate(R.layout.message_chat_item_delete_and_stick_popup, null);
		setContentView(rootview);
		
		this.mSessionBean= chatSessionBean;
		//rootview.setOnClickListener(clickListener);
		
		rootview.findViewById(R.id.stick_view).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mSessionBean!=null && mContext!=null) {
					mSessionBean.is_stick = 1;
					ChatSessionDao.getInstance(mContext).saveOrUpdate(mSessionBean);//保存
					ContentResolver contentProvider = mContext.getContentResolver();
					contentProvider.update(MessageSessionProvider.CONTENT_MSG_SESSION_URI,
							null, null, null);
					dismiss();
				}
			}
		});
		rootview.findViewById(R.id.delete_view).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mSessionBean!=null && mContext!=null) {
					ChatSessionDao.getInstance(mContext).deleteUserChat(mSessionBean.loginUser,mSessionBean.otherUser);//保存
					ContentResolver contentProvider = mContext.getContentResolver();
					contentProvider.update(MessageSessionProvider.CONTENT_MSG_SESSION_URI,
							null, null, null);
					dismiss();
				}
			}
		});
		
//       lp.alpha = 0.5f; //0.0-1.0
		
		this.setWidth(LayoutParams.WRAP_CONTENT);
		//设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		//设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		setOutsideTouchable(true);
		//setIgnoreCheekPress();
		ColorDrawable dw = new ColorDrawable(0xffffffff);
		//设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		//getContentView().setBackgroundColor(Color.parseColor("#10ff0000"));
		//实例化一个ColorDrawable颜色为半透明
		//ColorDrawable dw = new ColorDrawable(0xffffffff);
		//设置SelectPicPopupWindow弹出窗体的背景
        setBackgroundDrawable(new BitmapDrawable());
		//setAnimationStyle(R.style.dialogWindowAnim);
	
	}
	
}
