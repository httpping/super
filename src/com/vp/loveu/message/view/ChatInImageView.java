package com.vp.loveu.message.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.message.bean.ChatMessage;
import com.vp.loveu.message.utils.DensityUtil;
import com.vp.loveu.message.utils.FaceConversionUtil;
import com.vp.loveu.util.VPLog;

/**
 * 用户接收到消息的view
 * 
 * @author tanping
 * 
 */
public class ChatInImageView extends RelativeLayout implements IMsgUpdater,OnClickListener{

	private ImageView mHeadImageView;
	private ImageView msgBodyView;
	private ImageLoader imageLoader;

	public ChatMessage message;// 消息体

	DisplayImageOptions options;
	DisplayImageOptions roundedOptions;

	public ChatInImageView(Context context) {
		super(context);
		initView();
	}

	public ChatInImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {
		inflate(getContext(), R.layout.message_item_chat_in_image, this);
		mHeadImageView = (ImageView) findViewById(R.id.head_image);
		msgBodyView = (ImageView) findViewById(R.id.msg_image_body);

		
		 options = new DisplayImageOptions.Builder()
		 .showImageOnLoading(R.drawable.default_image_loading) // resource or
	        .showImageForEmptyUri(R.drawable.default_image_loading_fail) // resource or
	        .showImageOnFail(R.drawable.default_image_loading_fail) // resource or
	     .resetViewBeforeLoading(false) // default
	     .delayBeforeLoading(50).cacheInMemory(true) // default
	     .cacheOnDisk(true) // default
	     .considerExifParams(false) // default
	     .displayer(new SimpleBitmapDisplayer())
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
			return ;
		}
		float flag = 1;
		if (message.height >= message.width) {
			if (message.height == 0) {
				msgBodyView.getLayoutParams().height = 50;//
				msgBodyView.getLayoutParams().height = 50;//
			} else {
				if (message.height > DensityUtil.dip2px(getContext(), 180)) {
					msgBodyView.getLayoutParams().height = DensityUtil.dip2px(
							getContext(), 180);
					flag = DensityUtil.dip2px(getContext(), 180) * 1.0f
							/ message.height;
				} else {
					msgBodyView.getLayoutParams().height = message.height;
				}
				msgBodyView.getLayoutParams().width = (int) (message.width * flag);
			}
		} else {
			if (message.width ==0) {
				msgBodyView.getLayoutParams().width = 50;//
				msgBodyView.getLayoutParams().height = 50;//
			}else {
				if (message.width > DensityUtil.dip2px(getContext(), 180)) {
					msgBodyView.getLayoutParams().width = DensityUtil.dip2px(getContext(), 180);
					flag = DensityUtil.dip2px(getContext(), 180)*1.0f/message.width;
				}else {
					msgBodyView.getLayoutParams().width = message.width;
				}
				msgBodyView.getLayoutParams().height = (int) (message.height*flag);
			}
		}
		
		
		String uri = message.imgUrl+"" ;
		ImageLoader.getInstance().cancelDisplayTask(msgBodyView);
	 	ImageLoader.getInstance().displayImage(uri, msgBodyView,options);
	 	
	 	try {
	 		mHeadImageView.setOnClickListener(this);
			ImageLoader.getInstance().displayImage(message.fromUserInfo.headImage, mHeadImageView, roundedOptions);;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	LoadingFace mLoadingFace;
	class LoadingFace extends AsyncTask<String, String, String>{

		@Override
		protected String doInBackground(String... params) {
			VPLog.d("inview", "back loading");
			message.mSpannableString = FaceConversionUtil.getInstace().getExpressionString(getContext(), message.txt);
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			drawView();
		}
	};
	

	/**
	 * 设置图片
	 * 
	 * @param type
	 * @param imageUrl
	 */
	public void setImage( String imageUrl) {
		 
	}
	@Override
	public void onClick(View v) {
		if (v.equals(mHeadImageView)) {
			if (getContext() instanceof VpActivity) {
				 VpActivity activity = (VpActivity) getContext();
				 if (message!=null) {
					activity.goOtherUserInfo(message.fromUserInfo.userId);
				}
			}
		}
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
