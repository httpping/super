package com.vp.loveu.message.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.vp.loveu.R;

public class CopyPopupWIndow extends PopupWindow {

	Context mContext ;
	public CopyPopupWIndow(Context context,OnClickListener clickListener){
		super(context);
		mContext = context;
		View rootview = LayoutInflater.from(context).inflate(R.layout.message_chat_item_copy_popup, null);
		setContentView(rootview);
		
		rootview.setOnClickListener(clickListener);
		
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
