package com.vp.loveu.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.vp.loveu.R;
import com.vp.loveu.message.utils.DensityUtil;

public class CricleProgressView extends View {
	
	
	Bitmap bigquan ;
	int redRadius;//红点半径
	int mRadian;//弧度
	int textSize ; //字体大小
	int speed = 2; // 弧度运行的速度
	int needle =25;//1秒钟的针数
	String text ="已支付";
	
	public CricleProgressView(Context context) {
		super(context);
		initView();
	}
	
	public CricleProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	
	
	
	private void initView() {
		if (isInEditMode()) {
			return ;
		}
		bigquan = BitmapFactory.decodeResource(getResources(), R.drawable.icon_circle_bg);
		redRadius = DensityUtil.dip2px(getContext(), 6);//6dp
		textSize = DensityUtil.dip2px(getContext(), 24);
		timeHandler.sendEmptyMessage(TIME_RUN);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isInEditMode()) {
			return ;
		}
		
		int w = getWidth();
		int h = getHeight();
		
		int quanW = bigquan.getWidth();
		int quanH = bigquan.getHeight();
	 		 
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		
		//Log.d("quan", "w = "+w + "  h="+h +" qw=" +quanW +" qh ="+quanH );
		int x = (w - quanW)/2;
		int y = (h - quanH)/2;
		//Log.d("quan", "w = "+w + "  h="+h +" qw=" +quanW +" qh ="+quanH   +" x="+ x +" y="+y);
		//大背景
		canvas.drawBitmap(bigquan, x, y,paint);
		
		//进度 flag 为缩放比例。一般情况下flag 不会出现缩放
		int radius = quanW/2; //宽度的三分之一
		int centerPointx = w/2;
		int centerPointy =h/2;
		//mRadian = 60;
		double cos = Math.cos(mRadian*Math.PI/180);
		//Log.d("xy",  mRadian +"zx:" +zx);
		
		//则圆上每个点的X坐标=a + Math.sin(2*Math.PI / 360) * r ；Y坐标=b + Math.cos(2*Math.PI / 360) * r ；
		float xx = (float) (centerPointx  + Math.sin(-mRadian*Math.PI /180)*radius);
		float yy = (float) (centerPointy  + Math.cos(-mRadian*Math.PI /180)*radius);
		
		//中间的文字
		Paint cPaint = new Paint();
		cPaint.setAntiAlias(true);
		cPaint.setColor(Color.parseColor("#FF8000"));
		//Log.d("quan", "cx" + centerPointx + " cy:"+ centerPointy+ "xx:" + xx +" yy:" +yy + " rad:" +mRadian  +" radus:" +radius);

		canvas.drawCircle(xx, yy, redRadius, cPaint);
		
		cPaint.setTextSize(textSize);
		cPaint.setTextAlign(Align.CENTER);
		cPaint.setColor(Color.parseColor("#46A680"));
		canvas.drawText(text, centerPointx, centerPointy+textSize/3, cPaint);
				
	}
	
	public static final int TIME_RUN = 178;
	Handler timeHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case TIME_RUN:
				mRadian+=speed;
				mRadian%=360; //小宇360度
				invalidate();
				if (isRun) {
					timeHandler.sendEmptyMessageDelayed(TIME_RUN, 1000/needle);//1s 30针
				}
				break;

			default:
				break;
			}
		};
	};
	
	boolean isRun = true;
	public void stopRun(){
		isRun = false;
		timeHandler.removeMessages(TIME_RUN);
	}
	
	public void setText(String text) {
		this.text = text;
	}
	public void setNeedle(int needle) {
		this.needle = needle;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
}
