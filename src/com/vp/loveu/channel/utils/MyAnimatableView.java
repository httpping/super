package com.vp.loveu.channel.utils;

import android.view.View;
import android.widget.LinearLayout.LayoutParams;

/**
 * layoutparams
 * @author tanping
 * 2015-12-25
 */
public class MyAnimatableView {
	int leftMargin=0;
	View m_v = null;

	public MyAnimatableView(View v) {
		m_v = v;
	}

	public int getLeftMargin() {
		return leftMargin;
	}

	public void setLeftMargin(int leftMargin) {
		this.leftMargin = leftMargin;
		LayoutParams params = (LayoutParams) m_v.getLayoutParams();
		params.leftMargin = leftMargin;
	}

	
	 
}
