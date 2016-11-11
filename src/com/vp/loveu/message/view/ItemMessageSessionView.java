package com.vp.loveu.message.view;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.vp.loveu.R;
import com.vp.loveu.message.bean.ChatSessionBean;
import com.vp.loveu.message.ui.PrivateChatActivity;
import com.vp.loveu.message.utils.DensityUtil;
import com.vp.loveu.util.ScreenBean;
import com.vp.loveu.util.ScreenUtils;

/**
 *  
 * 聊天会话
 * @author tp
 * 
 */
public class ItemMessageSessionView extends RelativeLayout implements OnClickListener,OnLongClickListener{
	
	public static final String TAG ="ItemMessageNoticeView";
	 
	private ChatSessionBean tabData;
 
	TextView content;
	TextView unReadNum ;
	ImageView headView;
	TextView niceNameView ;
	DisplayImageOptions roundedOptions;


	public ItemMessageSessionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ItemMessageSessionView(Context context) {
		super(context);
		init();
	}

	private void init() {
		Log.i("tag", "===============init ========== ");
		inflate(getContext(),R.layout.message_chat_session_item_view, this);
		content = (TextView) findViewById(R.id.chat_info);
		unReadNum = (TextView) findViewById(R.id.unread_num);
		headView = (ImageView) findViewById(R.id.head_image);
		niceNameView = (TextView) findViewById(R.id.nice_name);
		
		 roundedOptions = new DisplayImageOptions.Builder()
	        .showImageOnLoading(R.drawable.default_image_loading) // resource or
	        .showImageForEmptyUri(R.drawable.default_image_loading_fail) // resource or
	        .showImageOnFail(R.drawable.default_image_loading_fail) // resource or
	        .resetViewBeforeLoading(false) // default
	        .delayBeforeLoading(50).cacheInMemory(true) // default
	        .cacheOnDisk(true) // default
	        .bitmapConfig(Bitmap.Config.RGB_565)  
	        .considerExifParams(false) // default
	        .displayer(new RoundedBitmapDisplayer(DensityUtil.dip2px(getContext(), 50)))
	        .build();
		 setOnClickListener(this);
		 setOnLongClickListener(this);
	}

	/**
	 * 更新view 显示
	 */
	public void updateViewShow() {
			
		if (tabData == null ) {
			return ;
		}
		content.setText(tabData.txt);
		try {
			niceNameView.setText(tabData.fromUserInfo.userName);
			if (tabData.count >0) {
				unReadNum.setVisibility(VISIBLE);
				String redStr ="";
				if (tabData.count>=100) {
					redStr ="99+";
				}else {
					redStr = tabData.count+"";
				}
				unReadNum.setText(redStr);
			}else {
				unReadNum.setVisibility(GONE);
			}
			ImageLoader.getInstance().displayImage(tabData.fromUserInfo.headImage, headView, roundedOptions);
		} catch (Exception e) {
		}
		
		
	}

	public void setMsgSession(ChatSessionBean data) {
		tabData = data;
		if (mAndStickPopupWIndow!=null) {
			mAndStickPopupWIndow.dismiss();
			mAndStickPopupWIndow = null;
		}
		updateViewShow();
	}

	@Override
	public void onClick(View v) {
		if (tabData!=null) {
			 Intent chatIntent = new Intent(getContext(),PrivateChatActivity.class);
			 if (tabData.fromUserInfo==null) {
				Toast.makeText(getContext(), "没有相关用户信息", 0).show();
				return ;
			 }
			 chatIntent.putExtra(PrivateChatActivity.CHAT_USER_ID, tabData.fromUserInfo.userId);
			 chatIntent.putExtra(PrivateChatActivity.CHAT_USER_NAME,tabData.fromUserInfo.userName);
			 chatIntent.putExtra(PrivateChatActivity.CHAT_USER_HEAD_IMAGE, tabData.fromUserInfo.headImage);
			 chatIntent.putExtra(PrivateChatActivity.CHAT_XMPP_USER, tabData.fromUserInfo.xmppUser);

			 getContext().startActivity(chatIntent);
		}
	}
	
	DeleteAndStickPopupWIndow mAndStickPopupWIndow;
	@Override
	public boolean onLongClick(View v) {
		
		mAndStickPopupWIndow =new DeleteAndStickPopupWIndow(getContext(), tabData);
		if (ScreenBean.screenWidth ==0) {
			ScreenUtils.initScreen((Activity) getContext());
		}
		//VPLog.d("tag", msg)
		mAndStickPopupWIndow.showAsDropDown(headView, (ScreenBean.screenWidth - DensityUtil.dip2px(getContext(), 109))/2 -DensityUtil.dip2px(getContext(), 15),-DensityUtil.dip2px(getContext(), 37+50+13));
		return false;
	}
	
	
	 
}
