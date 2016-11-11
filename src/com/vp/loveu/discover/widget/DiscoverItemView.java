package com.vp.loveu.discover.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vp.loveu.R;

/**
 * @author：pzj
 * @date: 2015-11-16 上午10:04:28
 * @Description:
 */
public class DiscoverItemView extends LinearLayout {
	private ImageView mIvIcon;
	private TextView mTvName;


	public DiscoverItemView(Context context) {
		this(context,null);
		
	}
	public DiscoverItemView(Context context, AttributeSet set) {
		super(context, set);
		View.inflate(context, R.layout.discover_home_item_view, this);
		mIvIcon=(ImageView) this.findViewById(R.id.discover_item_icon);
		mTvName=(TextView) this.findViewById(R.id.discover_item_name);
		
		if (isInEditMode()) {
			return;
		}
		
		TypedArray ta = context.obtainStyledAttributes(set, R.styleable.DiscoverItemView);
		String name = ta.getString(R.styleable.DiscoverItemView_itemName);
		Drawable drawable = ta.getDrawable(R.styleable.DiscoverItemView_itemImage);
		mTvName.setText(name);
		mIvIcon.setImageDrawable(drawable);
		ta.recycle();
		if (isInEditMode()) {
			return;
		}
	}

	
	public void hideRigthMore(){
		mTvName.setCompoundDrawables(null, null,null, null);
	}
	
	
	

}
