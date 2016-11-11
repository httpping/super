package com.vp.loveu.message.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.vp.loveu.R;
import com.vp.loveu.message.bean.ChatMessage;
import com.vp.loveu.message.utils.DensityUtil;
import com.vp.loveu.message.utils.FaceConversionUtil;
import com.vp.loveu.util.ClipboardUtil;
import com.vp.loveu.util.VPLog;

/**
 * 用户接收到消息的view
 * 
 * @author tanping
 * 
 */
public class ChatOutView extends RelativeLayout implements IMsgUpdater,OnLongClickListener {

	private ImageView mHeadImageView;
	private TextView msgBodyView;
	private ImageLoader imageLoader;
	private ImageView msgStatus ;
	 private AnimationDrawable animationDrawable;
	
	public ChatMessage message;// 消息体
	DisplayImageOptions options;

	public ChatOutView(Context context) {
		super(context);
		initView();
	}

	public ChatOutView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {
		inflate(getContext(), R.layout.message_item_chat_out, this);
		
		if (isInEditMode()) {
			return ;
		}
		
		mHeadImageView = (ImageView) findViewById(R.id.head_image);
		msgBodyView = (TextView) findViewById(R.id.msg_text_body);
		msgStatus = (ImageView) findViewById(R.id.left_status);
		msgBodyView.setOnLongClickListener(this);
	
		
		options = new DisplayImageOptions.Builder()
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
		//VPLog.d("out", message.toString());
		if (message.txt == null) {
			message.txt ="";
		}
		if (message.mSpannableString==null && message.txt.length()>15) {
			if (mLoadingFace!=null) {
				mLoadingFace.cancel(true);
			}
			msgBodyView.setText("...");
			mLoadingFace = new LoadingFace();
			mLoadingFace.execute();
		}else {
			message.mSpannableString = FaceConversionUtil.getInstace().getExpressionString(getContext(), message.txt);
			msgBodyView.setText(message.mSpannableString); 
		}
		
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
			ImageLoader.getInstance().displayImage(message.fromUserInfo.headImage, mHeadImageView, options);;
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

    /**
     * 复制功能
     */
    CopyPopupWIndow copyPopupWIndow;
	@Override
	public boolean onLongClick(View v) {
		VPLog.d("c", "long ");
		 copyPopupWIndow = new CopyPopupWIndow(getContext(),new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (message!=null) {
					ClipboardUtil.copy(message.txt, getContext());
				}
				copyPopupWIndow.dismiss();
			}
		});
		int[] location = new int[2];
		mHeadImageView.getLocationOnScreen(location);
		int[] bodyLoc = new int[2];
		msgBodyView.getLocationOnScreen(bodyLoc);
		
		int py = location[0]-bodyLoc[0];//差值
		
		//VPLog.d("tag", msg)
		
		copyPopupWIndow.showAsDropDown(mHeadImageView, -(py+DensityUtil.dip2px(getContext(), 52))/2 ,-DensityUtil.dip2px(getContext(), 37+41));
		return false;
	}
}
