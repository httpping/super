package com.vp.loveu.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class CustomLinearLayout extends LinearLayout {

	private int[] datas = new int[] {};
	private Context mContext;

	public CustomLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
	}

	public CustomLinearLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomLinearLayout(Context context) {
		this(context, null);
	}
	
	private void initView() {
		switch (datas.length) {
		case 0:
			throw new RuntimeException("please set datas");
		case 1:
			ImageView iv1 = new ImageView(mContext);
			iv1.setScaleType(ScaleType.FIT_XY);
			LinearLayout.LayoutParams layout1 = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT);
			iv1.setBackgroundResource(datas[0]);
			addView(iv1,layout1);
			break;
		case 2:
			LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 0, 1);
			ImageView iv2 = new ImageView(mContext);
			iv2.setScaleType(ScaleType.CENTER_INSIDE);
			iv2.setBackgroundResource(datas[0]);
			ImageView iv22 = new ImageView(mContext);
			iv22.setScaleType(ScaleType.CENTER_INSIDE);
			iv22.setBackgroundResource(datas[1]);
			setOrientation(LinearLayout.VERTICAL);
			addView(iv2,params2);
			addView(iv22,params2);
			break;
		case 3:
			LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,0, 1);
			LinearLayout.LayoutParams params33 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.FILL_PARENT, 1);
			ImageView iv3 = new ImageView(mContext);
			iv3.setScaleType(ScaleType.FIT_CENTER);
			iv3.setBackgroundResource(datas[0]);
			iv3.setAdjustViewBounds(false);
			iv3.setLayoutParams(params3);
			LinearLayout layout3 = new LinearLayout(mContext);
			layout3.setOrientation(LinearLayout.HORIZONTAL);
			ImageView iv33 = new ImageView(mContext);
			iv33.setScaleType(ScaleType.CENTER_CROP);
			iv33.setBackgroundResource(datas[1]);
			iv33.setLayoutParams(params33);
			ImageView iv333 = new ImageView(mContext);
			iv333.setScaleType(ScaleType.CENTER);
			iv333.setBackgroundResource(datas[2]);
			iv333.setLayoutParams(params33);
			layout3.addView(iv33);
			layout3.addView(iv333);
			setOrientation(LinearLayout.VERTICAL);
			addView(iv3);
			addView(layout3,params3);
			break;
		case 5:
			LinearLayout.LayoutParams params5 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.FILL_PARENT, 1);
			LinearLayout layout5 = new LinearLayout(mContext);
			layout5.setOrientation(LinearLayout.VERTICAL);
			LinearLayout.LayoutParams layout5params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,0,1);
			ImageView iv5 = new ImageView(mContext);
			//iv5.setScaleType(ScaleType.CENTER);
			iv5.setBackgroundResource(datas[0]);
			iv5.setLayoutParams(layout5params);
			ImageView iv55 = new ImageView(mContext);
			iv55.setScaleType(ScaleType.CENTER_CROP);
			iv55.setBackgroundResource(datas[1]);
			iv55.setLayoutParams(layout5params);
			layout5.addView(iv5);
			layout5.addView(iv55);
			LinearLayout layout55 = new LinearLayout(mContext);
			layout55.setOrientation(LinearLayout.VERTICAL);
			ImageView iv555 = new ImageView(mContext);
			iv555.setScaleType(ScaleType.FIT_XY);
			iv555.setAdjustViewBounds(true);
			iv555.setBackgroundResource(datas[2]);
			iv555.setLayoutParams(layout5params);
			ImageView iv5555 = new ImageView(mContext);
			iv5555.setScaleType(ScaleType.CENTER);
			iv5555.setAdjustViewBounds(false);
			iv5555.setBackgroundResource(datas[3]);
			iv5555.setLayoutParams(layout5params);
			ImageView iv55555 = new ImageView(mContext);
			iv55555.setScaleType(ScaleType.CENTER_CROP);
			iv55555.setBackgroundResource(datas[4]);
			iv55555.setLayoutParams(layout5params);
			layout55.addView(iv555);
			layout55.addView(iv5555);
			layout55.addView(iv55555);
			setOrientation(LinearLayout.HORIZONTAL);
			addView(layout5,params5);
			addView(layout55,params5);
			break;
		default:
			break;
		}
	}

	public void setData(int[] arr) {
		datas = arr;
		initView();
	}
}
