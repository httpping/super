package com.vp.loveu.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;

/**
 * @author：pzj
 * @date: 2015-11-10 下午5:25:16
 * @Description:
 */
public class PublicTitleView  extends RelativeLayout implements OnClickListener{
	public TextView mBtnLeft;
	public TextView mBtnRight;
	public TextView mTvTitle;
	
	public FrameLayout mRightLayout;
	public FrameLayout mLeftLayout;
	
	public View bgView ;
	
	public RelativeLayout mRadioContainer;
	public CircleImageView mRadioIv;
	public ImageView mRadioIvPlay;
	
	public PublicTitleView(Context context) {
		super(context);
		initView();
	}
	
	
	public PublicTitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
		
	}
	
	private void initView() {
		inflate(getContext(), R.layout.public_top, this);
		
		if (isInEditMode()) {
			return ;
		}
		mBtnLeft=(TextView) findViewById(R.id.public_top_back);
		mBtnRight=(TextView) findViewById(R.id.public_top_save);
		mTvTitle=(TextView) findViewById(R.id.public_top_title);
		
		mRightLayout =(FrameLayout) findViewById(R.id.rigth_btn_view);
		mLeftLayout  =(FrameLayout) findViewById(R.id.left_back_view);
		mRadioContainer=(RelativeLayout) findViewById(R.id.public_top_radio_play);
		mRadioIv=(CircleImageView) findViewById(R.id.public_top_radio_iv);
		mRadioIvPlay=(ImageView) findViewById(R.id.public_top_radio_iv_playing);
		mLeftLayout.setOnClickListener(this);
		
		bgView = findViewById(R.id.bg_view);
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_back_view:
			if(getContext() instanceof VpActivity){
				((VpActivity)getContext()).finish();
			}
			break;

		default:
			break;
		}
		
	}
	
	public void setBgColor(String color){
		bgView.setBackgroundColor(Color.parseColor(color));
	}

	

}
