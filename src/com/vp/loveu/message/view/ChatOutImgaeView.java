package com.vp.loveu.message.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.vp.loveu.R;
import com.vp.loveu.message.bean.ChatMessage;
import com.vp.loveu.message.utils.DensityUtil;
import com.vp.loveu.util.ScreenBean;

/**
 * 用户接收到消息的view
 * 发送图片的view
 * @author tanping
 * 
 */
public class ChatOutImgaeView extends RelativeLayout implements IMsgUpdater {

	private ImageView mHeadImageView;
	private ImageView msgStatus ;
	private AnimationDrawable animationDrawable;
	private ImageView mBodyImageView;
	
	public static  int MAX_WIDTH =300;;
	public static  int MAX_HEGITH =300;

	 
	public ChatMessage message;// 消息体
	DisplayImageOptions options;
	DisplayImageOptions roundedOptions;

	public ChatOutImgaeView(Context context) {
		super(context);
		initView();
	}

	public ChatOutImgaeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {
		inflate(getContext(), R.layout.message_item_chat_out_image, this);
		mHeadImageView = (ImageView) findViewById(R.id.head_image);
		mBodyImageView =  (ImageView) findViewById(R.id.msg_image_body);
		msgStatus = (ImageView) findViewById(R.id.left_status);
		
		 MAX_WIDTH = ScreenBean.screenWidth/2;
		 MAX_HEGITH = MAX_WIDTH;
		
		 options = new DisplayImageOptions.Builder()
		 .showImageOnLoading(R.drawable.default_image_loading) // resource or
	     .showImageForEmptyUri(R.drawable.default_image_loading_fail) // resource or
	     .showImageOnFail(R.drawable.default_image_loading_fail) // resource or
        .resetViewBeforeLoading(false) // default
        .delayBeforeLoading(50).cacheInMemory(true) // default
        .cacheOnDisk(true) // default
        .bitmapConfig(Bitmap.Config.RGB_565)  
        .considerExifParams(false) // default
        .build();
		 
		 roundedOptions = new DisplayImageOptions.Builder()
	        .showImageOnLoading(R.drawable.default_image_loading) // resource or
	        .showImageForEmptyUri(R.drawable.default_image_loading_fail) // resource or
	        .showImageOnFail(R.drawable.default_image_loading_fail) // resource or
	        .resetViewBeforeLoading(false) // default
	        .delayBeforeLoading(50).cacheInMemory(true) // default
	        .cacheOnDisk(true) // default
	        .bitmapConfig(Bitmap.Config.RGB_565)  
	        .considerExifParams(false) // default
	        .displayer(new RoundedBitmapDisplayer(DensityUtil.dip2px(getContext(), 40)))
	        .build();
	}

	@Override
	public void setChatData(ChatMessage message) {
		if (this.message!=null) {
			this.message.viewUpdate = null;
		}
		this.message = message;
		drawView();
		
		
	}

	@Override
	public void drawView() {
		if (message==null) {
			return;
		}
		float flag = 1;

		if (message.height>= message.width) {
			if (message.height ==0) {
				mBodyImageView.getLayoutParams().height = 50;//
				mBodyImageView.getLayoutParams().width = 50;//

			}else {
				if (message.height > DensityUtil.dip2px(getContext(), 180)) {
					mBodyImageView.getLayoutParams().height = DensityUtil.dip2px(getContext(), 180);
					flag = DensityUtil.dip2px(getContext(), 180)*1.0f/message.height;
				}else {
					mBodyImageView.getLayoutParams().height = message.height;
				}
				mBodyImageView.getLayoutParams().width = (int) (message.width*flag);

			}
		
		}else {
			if (message.width ==0) {
				mBodyImageView.getLayoutParams().width = 50;//
				mBodyImageView.getLayoutParams().height = 50;//
			}else {
				if (message.width > DensityUtil.dip2px(getContext(), 180)) {
					mBodyImageView.getLayoutParams().width = DensityUtil.dip2px(getContext(), 180);
					flag = DensityUtil.dip2px(getContext(), 180)*1.0f/message.width;
				}else {
					mBodyImageView.getLayoutParams().width = message.width;
				}
				mBodyImageView.getLayoutParams().height = (int) (message.height*flag);
			}
		}
		
		String uri = message.locImgUrl ;
		
		if (!TextUtils.isEmpty(message.imgUrl) && uri == null) {
			if (message.imgUrl.startsWith("http")) {
				//non
				uri = message.imgUrl;
			}else {
				uri ="file://" +message.imgUrl;
			}
		}else {//本地的
			uri ="file://" + message.locImgUrl;
		}
	 
		ImageLoader.getInstance().cancelDisplayTask(mBodyImageView);
	 	ImageLoader.getInstance().displayImage(uri, mBodyImageView,options);
	 	 
	 	// ImageSize imageSize
	 	 
		 /*ImageLoader.getInstance().loadImage(uri, options, new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String arg0, View arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				
			}
			
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				 int ww = arg2.getWidth();
				 int hh = arg2.getHeight();
				 int flag =1;
				 while ( ww/flag >MAX_WIDTH || hh/flag>MAX_HEGITH){
					 flag++;
				 }
				 Bitmap bitmap = Bitmap.createBitmap(arg2, 0, 0, ww/flag, hh/flag);
				 mBodyImageView.setImageBitmap(bitmap);
				 if (!arg2.isRecycled()) {
					 arg2.recycle();
				} 
			}
			
			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				// TODO Auto-generated method stub
				
			}
		});*/
		
		
		if (message.sendStatus == ChatMessage.MsgSendStatus.send.ordinal()) {
			msgStatus.setImageResource(R.anim.progress_round);
			animationDrawable =  (AnimationDrawable) msgStatus.getDrawable();
			animationDrawable.start();
		}else if (message.sendStatus == ChatMessage.MsgSendStatus.fail.ordinal()) {
			msgStatus.setImageResource(R.drawable.message_icon_failed);
		}else {
			msgStatus.setImageResource(0);
		}
		
		try {
			ImageLoader.getInstance().displayImage(message.fromUserInfo.headImage, mHeadImageView, roundedOptions);;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

	/**
	 * 设置图片
	 * 
	 * @param type
	 * @param imageUrl
	 */
	public void setImage( String imageUrl) {
		 
	}

	 
    /**
     * 去掉回车带来的空白字符
     * 
     * @param content
     * @return
     */
    public String replaceBlank(String content) {
	String dest = "";
	if (content != null) {
	    Pattern p = Pattern.compile("\\s*|\t|\r|\n");
	    Matcher m = p.matcher(content);
	    dest = m.replaceAll("");
	}
	return dest;

    }
}
