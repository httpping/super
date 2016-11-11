package com.vp.loveu.index.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.vp.loveu.R;

public class RatioImageView extends ImageView {
	public final static int RELATIVE_WIDTH = 0;
	public final static int RELATIVE_HEIGHT = 1;

	private float mRatio;
	private int mRelative = RELATIVE_WIDTH;

	public RatioImageView(Context context) {
		this(context, null);
	}

	public RatioImageView(Context context, AttributeSet set) {
		super(context, set);
		TypedArray ta = context.obtainStyledAttributes(set, R.styleable.RatioImageView);
		mRatio = ta.getFloat(R.styleable.RatioImageView_ratio, 0);
		mRelative = ta.getInt(R.styleable.RatioImageView_relative, RELATIVE_WIDTH);
		ta.recycle();
	}

	public void setRatio(float ratio) {
		this.mRatio = ratio;
	}

	public void setRelative(int relative) {

		if (relative == RELATIVE_HEIGHT) {
			this.mRelative = relative;
		} else {
			this.mRelative = RELATIVE_WIDTH;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 获得宽度值
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int heightsMode = MeasureSpec.getMode(heightMeasureSpec);
		if (widthMode == MeasureSpec.EXACTLY && mRatio != 0 && mRelative == RELATIVE_WIDTH) {
			int width = widthSize - getPaddingLeft() - getPaddingRight();
			int height = (int) (width / mRatio + 0.5f);
			setMeasuredDimension(widthSize, height + getPaddingTop() + getPaddingBottom());
		} else if (heightsMode == MeasureSpec.EXACTLY && mRatio != 0 && mRelative == RELATIVE_HEIGHT) {
			int height = heightSize - getPaddingTop() - getPaddingBottom();
			int width = (int) (height * mRatio + 0.5f);
			setMeasuredDimension(width + getPaddingLeft() + getPaddingRight(), heightSize);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
}
