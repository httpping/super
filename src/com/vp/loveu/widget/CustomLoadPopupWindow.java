package com.vp.loveu.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.vp.loveu.R;
import com.vp.loveu.util.VPLog;

public class CustomLoadPopupWindow  extends PopupWindow{
	public static final String TAG ="CustomLoadPopupWindow";
	private Context mContext;

	public CustomLoadPopupWindow(Context context) {
		super(context);
		mContext = context;
		View rootview = LayoutInflater.from(context).inflate(R.layout.custom_progress_dialog, null);
		setContentView(rootview);
	 /*  com.vp.loveu.widget.MetaballView metaballView = (MetaballView) rootview.findViewById(R.id.metaball);
	    metaballView.setPaintMode(1);*/
		//设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.MATCH_PARENT);
		this.setWidth(LayoutParams.MATCH_PARENT);
		//设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(false);
		setOutsideTouchable(false);
		setIgnoreCheekPress();
		getContentView().setBackgroundResource(R.color.write);
		//实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xffffff);
		//设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		//setAnimationStyle(R.style.dialogWindowAnim);
	}
	
	
	/**
	 * show
	 */
	public void show(){
		VPLog.d(TAG, "SHOW");
		FragmentActivity activity = (FragmentActivity) mContext;
		//showAsDropDown();
		ViewGroup viewGroup = (ViewGroup) ((ViewGroup)activity.findViewById(android.R.id.content)).getChildAt(0);
		/*View view = new View(mContext);
		viewGroup.addView(view,0);*/
		showAsDropDown(viewGroup.getChildAt(0), 0, 0);
	}
}
