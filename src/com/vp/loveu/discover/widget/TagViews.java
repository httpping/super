package com.vp.loveu.discover.widget;

import java.util.List;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vp.loveu.R;
import com.vp.loveu.bean.InwardAction;
import com.vp.loveu.discover.bean.OldTagBean;
import com.vp.loveu.message.utils.DensityUtil;
import com.vp.loveu.util.ScreenBean;

/**
 * 标签view
 * @author tanping
 * 2016-1-25
 */
public class TagViews  extends LinearLayout implements OnClickListener{
	
	public TagViews(Context context) {
		super(context);
	}
	
	public TagViews(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	

	
	public List<OldTagBean> mOldTagBeans;
	public void updateView(List<OldTagBean> beans){
		mOldTagBeans = beans;
		
		setOrientation(LinearLayout.VERTICAL);
		if (mOldTagBeans ==null || mOldTagBeans.size() ==0) {
			setVisibility(View.GONE);
			return ;
		}
		 
		LinearLayout hLayout = null;
		for (int i = 0; i < mOldTagBeans.size(); i++) {
			 if (i % 2 ==0) {
				hLayout = new LinearLayout(getContext());
				hLayout.setOrientation(LinearLayout.HORIZONTAL);
				hLayout.setGravity(Gravity.CENTER_VERTICAL);
				addView(hLayout);
				addView(createDivier());
			}
			hLayout.addView(createTagTextView(mOldTagBeans.get(i)));
			if (i % 2 ==0) {
				hLayout.addView(createHDivier());
			} 
		}
		 

	}
	/**
	 * 创建textview
	 * @param bean
	 * @return
	 */
	public TextView createTagTextView(OldTagBean bean){
		
		TextView textView = new TextView(getContext());
		if (bean == null) {
			return textView;
		}
		
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
		textView.setGravity(Gravity.CENTER_VERTICAL);
		//textView.setHeight(DensityUtil.dip2px(getContext(), 65));
		textView.setWidth(ScreenBean.screenWidth/2-2);
		int padding = DensityUtil.dip2px(getContext(), 15);
		textView.setPadding(padding,padding,padding,padding);
		textView.setSingleLine();
		textView.setEllipsize(TruncateAt.END);
		textView.setText(bean.mName);
		textView.setTag(bean.mUrl);
		textView.setTextColor(getResources().getColor(R.color.text_two));
		textView.setOnClickListener(this);
		
		return textView;
	}
	
	/**
	 * 
	 * @return
	 */
	public View createHDivier(){
		View view = new View(getContext());
		view.setBackgroundResource(R.color.tag_divide_line_bg);
		LayoutParams params = new LayoutParams(1, DensityUtil.dip2px(getContext(), 30));
		view.setLayoutParams(params);
		return view;
	}
	
	public View createDivier(){
		View view = new View(getContext());
		view.setBackgroundResource(R.color.tag_divide_line_bg);
		LayoutParams params = new LayoutParams(-1, 1);
		view.setLayoutParams(params);
		return view;
	}

	@Override
	public void onClick(View v) {
		String tag =  (String) v.getTag();
		
		InwardAction action= InwardAction.parseAction(tag);
		if (action !=null) {
			action.toStartActivity(getContext());
		}
	}
	
}
