package com.vp.loveu.channel.widget;

import com.vp.loveu.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2016年3月4日上午9:18:55
 * @功能 分享的view
 * @作者 mi
 */

public class ShareView extends View {

	BitmapDrawable dr;
	int width, height;
	Paint mPaint;
	String str = "+99";
	int tvWidth, tvHeight;
	boolean isShowInteger = true;

	public ShareView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(context.getResources().getColor(R.color.share_text_color));
		mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 9, context.getResources().getDisplayMetrics()));
		tvWidth = (int) (mPaint.measureText(str) + 0.5f);
		Rect bounds = new Rect();
		mPaint.getTextBounds(str, 0, str.length(), bounds);
		tvHeight = bounds.bottom - bounds.top;
	}

	public ShareView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ShareView(Context context) {
		this(context, null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(width + tvWidth, height);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		dr.draw(canvas);
		if (isShowInteger && !TextUtils.isEmpty(text)) {
			canvas.drawText(text, width, tvHeight, mPaint);
		}
	}

	String text;

	public void setIsShowInteger(boolean isShow) {
		isShowInteger = isShow;
		invalidate();
	}

	public void setDrawable(int resId) {
		dr = (BitmapDrawable) getContext().getResources().getDrawable(resId);
		width = dr.getMinimumWidth();
		height = dr.getMinimumHeight();
		dr.setBounds(0, 0, width,height);
	}

	public void setText(int intergral) {
		text = "+" + intergral;
		invalidate();
	}
}
