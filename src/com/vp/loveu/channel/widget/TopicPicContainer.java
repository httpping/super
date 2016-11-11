package com.vp.loveu.channel.widget;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.vp.loveu.R;
import com.vp.loveu.comm.ShowImagesViewPagerActivity;
import com.vp.loveu.util.ScreenUtils;
import com.vp.loveu.util.ShareCompleteUtils;
import com.vp.loveu.util.UIUtils;

/**
 * @author：pzj
 * @date: 2016年1月5日 下午2:27:54
 * @Description:
 */
public class TopicPicContainer extends LinearLayout {
	private Context mContext;
	private int mPadding=5;//default
	private DisplayImageOptions options;

	public TopicPicContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public TopicPicContainer(Context context) {
		super(context);
		initView(context);
	}

	private void initView(Context context) {
		this.mContext=context;
		View.inflate(context, R.layout.channel_topic_pic_view, this);
		 options = new DisplayImageOptions.Builder()
		         .showImageOnLoading(R.drawable.default_image_loading) // resource or
		        .showImageForEmptyUri(R.drawable.default_image_loading_fail) // resource or
		        .showImageOnFail(R.drawable.default_image_loading_fail) // resource or
		        .resetViewBeforeLoading(false) // default
		        .cacheInMemory(true) // default
		        .cacheOnDisk(true) // default
		        .bitmapConfig(Bitmap.Config.RGB_565)  
		        .considerExifParams(false) // default
		        .displayer(new SimpleBitmapDisplayer())
		        .build();
	}
	
	private int width = ScreenUtils.getScaleScreenWidth(1);
	public void setDatas(ArrayList<String> picList,int relativeWidth){
		width = relativeWidth;
		setDatas(picList);
	}
	
	public void setDatas(ArrayList<String> picList){
		if(picList==null || picList.size()==0){
			this.setVisibility(View.GONE);
			return;
		}
		this.setVisibility(View.VISIBLE);
		this.removeAllViews();
		int padding=UIUtils.dp2px(mPadding);
		
		int restWidth=width-((LinearLayout)this.getParent()).getPaddingLeft()*2-((picList.size()-1)*padding)-(this.getPaddingLeft())*2;
		int imageWidth=0;
		if(picList.size()==1){
			imageWidth=restWidth;
		}else{
			imageWidth=(int)restWidth/3;
		}
		
		int imageHeight=(int)(imageWidth*0.68);
		for (int i = 0; i < picList.size(); i++) {
			ImageView imageView=new ImageView(mContext);
			this.addView(imageView);	
			if (picList.get(i).startsWith("http://") || picList.get(i).startsWith("https://")) {
				ImageLoader.getInstance().displayImage(picList.get(i), imageView,options);
			}else{
				ImageLoader.getInstance().displayImage("file://"+picList.get(i), imageView,options);
			}
			LayoutParams params = (LayoutParams) imageView.getLayoutParams();
			params.width = imageWidth;
			if (picList.size() == 1) {
				//params.height =LayoutParams.WRAP_CONTENT;
			}else{
				params.height =imageHeight;
			}
			if(i==0)
				params.leftMargin = 0;
			else
				params.leftMargin =UIUtils.dp2px(mPadding);
			if( picList.size()==1){
				imageView.setScaleType(ScaleType.FIT_START);
			}else{
				imageView.setScaleType(ScaleType.CENTER_INSIDE);
				imageView.setBackgroundColor(Color.parseColor("#F2F2F2"));
			}
				
			createPicClickListener(imageView,picList,i);
		}
	
	}
	
	public void createPicClickListener(ImageView imageView, final ArrayList<String> pics,final int position) {
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(mContext,ShowImagesViewPagerActivity.class);
				intent.putStringArrayListExtra(ShowImagesViewPagerActivity.IMAGES, pics);
				intent.putExtra(ShowImagesViewPagerActivity.POSITION, position);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
				
			}
		});
	}

	
}
