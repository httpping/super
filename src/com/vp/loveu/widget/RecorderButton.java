package com.vp.loveu.widget;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2016年3月2日下午5:29:22
 * @功能TODO
 * @作者 mi
 */

public class RecorderButton extends Button {

	public RecorderButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public RecorderButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RecorderButton(Context context) {
		this(context, null);
	}

	private void init() {
		setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				
				PopupWindow popupWindow = new PopupWindow(getContext());
				TextView textView = new TextView(getContext());
				textView.setText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
				popupWindow.setContentView(textView);
				popupWindow.showAtLocation(textView, Gravity.CENTER, 0, 0);
//				Builder build = new AlertDialog.Builder(getContext()).setTitle("title");
//				build.setNegativeButton("确定", new DialogInterface.OnClickListener() {
//					
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						
//					}
//				});
//				build.setPositiveButton("quxiao", null);
//				build.create().show();
//				setVisibility(View.GONE);
				return true;
			}
		});
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			Log.d("aaa", "MOVE");
			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}

}
