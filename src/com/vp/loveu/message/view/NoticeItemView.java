package com.vp.loveu.message.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vp.loveu.R;
import com.vp.loveu.message.ui.PrivateChatActivity;

/***
 * 消息通知中心 
 * @author tanping
 * 2015-11-16
 */
public class NoticeItemView  extends RelativeLayout{
	
	public ImageView leftImageView;
	public TextView centerTextView;
	public TextView redPoint;
	
	
	public NoticeItemView(Context context) {
		super(context);
		initView();
	}

	
	public NoticeItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {
		inflate(getContext(), R.layout.message_home_item_view, this);
		
		if (isInEditMode()) {
			return ;
		}
		
	 
		
		leftImageView = (ImageView) findViewById(R.id.left_image_logo);
		centerTextView = (TextView) findViewById(R.id.center_txt_content);
		redPoint = (TextView) findViewById(R.id.right_red_point);
		
		setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(),PrivateChatActivity.class);
				getContext().startActivity(intent);
			}
		});
		updateRedPoint(0);
	}
	
	public void updateRedPoint(int num){
		if (num >0) {
			redPoint.setVisibility(VISIBLE);
			String redStr ="";
			if (num>=100) {
				redStr ="99+";
			}else {
				redStr = num+"";
			}
			redPoint.setText(redStr);
		}else {
			redPoint.setVisibility(GONE);
		}
	}
}
