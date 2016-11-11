package com.vp.loveu.message.view;

import java.beans.PropertyChangeEvent;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.vp.loveu.R;
import com.vp.loveu.message.bean.OperateDataBaseEntity;
import com.vp.loveu.util.ScreenBean;
import com.vp.loveu.util.ScreenUtils;

/** 
 * @author tanping

 * @Description: 小图标的 view 的 绑定

 * @date 2015-11-7 下午8:10:22 
 *  
 */
public class UIImgAndTitleView extends RelativeLayout implements IUpdateUiView {

	public OperateDataBaseEntity entity; //数据
	DisplayImageOptions options ;
	/**
	 * @param context
	 * @param attrs
	 */
	public UIImgAndTitleView(Context context ) {
		super(context);
		
		initView();
	}
	
	/**
	 * @param context
	 * @param attrs
	 */
	public UIImgAndTitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initView();
	}

	/**
	 * 
	 */
	private void initView() {
		
		inflate(getContext(), R.layout.message_item_title_img_view, this);
		titleText = (TextView) findViewById(R.id.text_show);
		imageView = (ImageView) findViewById(R.id.image_show);
		
		setGravity(Gravity.CENTER);
		
		
		/**
		 * 根据 item 的高度做出处理
		 */
		android.widget.AbsListView.LayoutParams layoutParams = new android.widget.AbsListView.LayoutParams(-2, -2);
		ScreenUtils.initScreen((Activity) getContext());
		itemWidth = ScreenBean.screenWidth/4;
		itemHight = (int) (itemWidth * wcompareh);// 15比15
		layoutParams.width = itemHight;
		layoutParams.height = itemHight;
		setLayoutParams(layoutParams);
		
		//imageView.setBackgroundResource(R.drawable.message_plus_record_corner_round_all_bg_5);

		/**
		 * 根据 item 的高度做出处理
		 */
	    layoutParams = (android.widget.AbsListView.LayoutParams) getLayoutParams();
		if (layoutParams != null) { // 宽 和高的 比例 1/3
			android.view.ViewGroup.LayoutParams imgLayoutParams = imageView.getLayoutParams();
			imgLayoutParams.width =  layoutParams.width * 2 / 3;
			imgLayoutParams.height = layoutParams.height * 2 / 3;
			
			int paddingImg =imgLayoutParams.width/5;
		/*	imageView.setPadding(paddingImg,paddingImg,paddingImg,paddingImg);
			imageView.setLayoutParams(imgLayoutParams);*/
			titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX,layoutParams.width / 7);
			 
		}
		
	}

 
	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	TextView titleText;
	ImageView imageView;
	
	private int itemWidth = 0;
	private int itemHight;
	private float wcompareh = 18*4F / 72; // 高比宽 高/宽
	@Override
	public void updateViewShow() {
		
		Log.d("img_title", "image_title");
		if (entity !=null) {
			titleText.setText(entity.title);
			imageView.setImageResource(entity.imgRid);
			//ImageLoader.getInstance().displayImage(entity.smallUrl, imageView,options);
			setOnClickListener(entity);//设置点击事件
		}
	}

	public OperateDataBaseEntity getEntity() {
		return entity;
	}

	public void setEntity(OperateDataBaseEntity entity) {
		this.entity = entity;
		updateViewShow();
	}
	
}
