package com.vp.loveu.widget;

import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vp.loveu.R;


/**
 * Top View
 * @author pzj
 *
 */
public class TopView {

	private Button btRight;
	private ImageView btLeft;
	private TextView tvTitile;
	private PagerSlidingTabStrip paper;

	public TopView(View parentView) {
		// TODO Auto-generated constructor stub
		initTopView(parentView);
	}

	public void initTopView(View view) {
		// TODO Auto-generated method stub
		btLeft = (ImageView) view.findViewById(R.id.btLeft);
		btRight = (Button) view.findViewById(R.id.btRight);
		tvTitile = (TextView) view.findViewById(R.id.tvTitle);
		paper = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
	}

	public void setLeftButtonVisible(int visible) {
		// TODO Auto-generated method stub
		btLeft.setVisibility(visible);
	}

	public void setLeftButtonText(String text) {
		// TODO Auto-generated method stub
	}

	public void setTitleVisible(int visible) {
		// TODO Auto-generated method stub
		tvTitile.setVisibility(visible);
	}

	public void setPagerSlidingTabStripVisible(int visible) {
		// TODO Auto-generated method stub
		paper.setVisibility(visible);
	}

	public void setRightButtonTextColor(int color) {
		// TODO Auto-generated method stub
		btRight.setTextColor(color);
	}

	public void setRightButtonText(String text) {
		// TODO Auto-generated method stub
		btRight.setText(text);
	}

	public void setViewPager(ViewPager viewPager) {
		// TODO Auto-generated method stub
		paper.setViewPager(viewPager);
	}

	public void setLeftBackground(Drawable background) {
		// TODO Auto-generated method stub
//		btLeft.setBackground(null);
		btLeft.setBackgroundDrawable(background);
	}

	public void setLeftButtonBackgroundResource(int resid) {
		// TODO Auto-generated method stub
//		btLeft.setBackground(null);
		btLeft.setBackgroundResource(resid);
	}

	public void setRightButtonBackground(Drawable background) {
		// TODO Auto-generated method stub
//		btRight.setBackground(null);
		btRight.setBackgroundDrawable(background);
	}

	public void setRightButtonBackgroundResource(int resid) {
		// TODO Auto-generated method stub
//		btRight.setBackground(null);
		btRight.setBackgroundResource(resid);
	}

	public void setRightButtonVisible(int visible) {
		// TODO Auto-generated method stub
		btRight.setVisibility(visible);
	}

	public void setTitileText(String string) {
		// TODO Auto-generated method stub
		tvTitile.setText(string);
	}

	public void setLeftButtonOnClickListener(OnClickListener onClickListener) {
		// TODO Auto-generated method stub
		btLeft.setOnClickListener(onClickListener);
	}

	public void setRightButtonOnClickListener(OnClickListener onClickListener) {
		// TODO Auto-generated method stub
		btRight.setOnClickListener(onClickListener);
	}

	public PagerSlidingTabStrip getPaper() {
		return paper;
	}

	/**
	 * @param textMyGray
	 */
	public void setLeftButtonTextColor(int color) {
		// TODO Auto-generated method stub
	}

}
