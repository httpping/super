package com.vp.loveu.message.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
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
public class ChatInView extends RelativeLayout implements IMsgUpdater,OnLongClickListener,OnClickListener {

	private ImageView mHeadImageView;
	private TextView msgBodyView;

	public ChatMessage message;// 消息体

	DisplayImageOptions options;
	DisplayImageOptions roundedOptions;

	public ChatInView(Context context) {
		super(context);
		initView();
	}

	public ChatInView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {
		inflate(getContext(), R.layout.message_item_chat_in, this);
		mHeadImageView = (ImageView) findViewById(R.id.head_image);
		msgBodyView = (TextView) findViewById(R.id.msg_text_body);
		msgBodyView.setOnLongClickListener(this);
		
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
		if (message.mSpannableString==null && message.txt.length()>50) {
			if (mLoadingFace!=null) {
				mLoadingFace.cancel(true);
			}
			StringBuffer rep = new StringBuffer();//替换表情
			for (int i = 0; i < message.txt.length(); i++) {
				rep.append('　');
			}
			msgBodyView.setText(rep);
			mLoadingFace = new LoadingFace();
			mLoadingFace.execute();
		}else {
			message.mSpannableString = FaceConversionUtil.getInstace().getExpressionString(getContext(), message.txt);
			msgBodyView.setText(message.mSpannableString); 
		}
		
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
		
		int py = bodyLoc[0]-location[0];//差值
		
		//VPLog.d("tag", msg)
		
		copyPopupWIndow.showAsDropDown(mHeadImageView, (py+DensityUtil.dip2px(getContext(), 52))/2 ,-DensityUtil.dip2px(getContext(), 37+41));
		return false;
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
}
