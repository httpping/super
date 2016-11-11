package com.vp.loveu.message.view;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.vp.loveu.R;
import com.vp.loveu.bean.InwardAction;
import com.vp.loveu.message.bean.PushNoticeBean;
import com.vp.loveu.util.VpDateUtils;

/**
 *  
 * 通知itemview 通知内容
 * @author tp
 * 
 */
public class ItemMessageNoticeView extends LinearLayout implements OnClickListener{
	
	public static final String TAG ="ItemMessageNoticeView";
	 
	private PushNoticeBean tabData;
 
	TextView content;
	TextView time ;
	DisplayImageOptions options;


	public ItemMessageNoticeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ItemMessageNoticeView(Context context) {
		super(context);
		init();
	}

	private void init() {
		Log.i("tag", "===============init ========== ");
		inflate(getContext(),R.layout.message_push_notice_item_view, this);
		setGravity(Gravity.CENTER);
		setOrientation(VERTICAL);
		time = (TextView) findViewById(R.id.timestamp);
		content = (TextView) findViewById(R.id.push_notice_txt);
		setOnClickListener(this);
		
	}

	/**
	 * 更新view 显示
	 */
	public void updateViewShow() {
			
		if (tabData == null ) {
			return ;
		}
		
		String timestamp = VpDateUtils.getStandardDate(tabData.timestamp+"");// VlinkTimeUtil.getexpiredDate(new Date(tabData.timestamp*1000));
		time.setText(timestamp);
		
		content.setText(tabData.txt);
		
	}

	public void setMsgSession(PushNoticeBean data) {
		tabData = data;
		
		updateViewShow();
	}

	@Override
	public void onClick(View v) {
		
		if (tabData!=null) {
			InwardAction action = InwardAction.parseAction(tabData.url);
			if (action!=null) {//去跳转
				action.toStartActivity(getContext());
			}
		}
		
	}
	
	
	 
}
