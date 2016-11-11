package com.vp.loveu.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vp.loveu.R;


/**
 * TabView
 * @author Administrator
 */
public class TabView extends LinearLayout {

	private TextView tabName,tabCount;
	private int tabTextColor;
	private int countTextColor;
	private int tabTextSize;
	private int countTextSize;
	private String tabText;
	private String countText;
	
	public TabView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater.from(context).inflate(R.layout.view_tab, this);
		tabName=(TextView)findViewById(R.id.tabName);
		tabCount=(TextView)findViewById(R.id.tvCount);
		
		TypedArray type=context.obtainStyledAttributes(R.styleable.TabView);
		initType(type);
	}
	
	public TabView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		LayoutInflater.from(context).inflate(R.layout.view_tab, this);
		tabName=(TextView)findViewById(R.id.tabName);
		tabCount=(TextView)findViewById(R.id.tvCount);
	}
	
	
	public TabView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		LayoutInflater.from(context).inflate(R.layout.view_tab, this);
		tabName=(TextView)findViewById(R.id.tabName);
		tabCount=(TextView)findViewById(R.id.tvCount);
		
		TypedArray type=context.obtainStyledAttributes(R.styleable.TabView);
		initType(type);
	}

	private void initType(TypedArray type) {
		// TODO Auto-generated method stub
		tabTextColor=type.getColor(R.styleable.TabView_tabTextColor, 0x00000000);
		countTextColor=type.getColor(R.styleable.TabView_countTextColor, 0x00FF0000);
		tabTextSize=type.getDimensionPixelSize(R.styleable.TabView_tabTextSize,12);
		countTextSize=type.getDimensionPixelSize(R.styleable.TabView_countTextSize,12);
		tabText=type.getString(R.styleable.TabView_tabText);
		countText=type.getString(R.styleable.TabView_countText);
		if (null!=tabText) {
			tabName.setText(tabText);
		}
		
		if (null!=countText) {
			tabCount.setText(countText);
		}
		
		tabName.setTextSize(tabTextSize);
		tabCount.setTextSize(countTextSize);
		tabName.setTextColor(tabTextColor);
		tabCount.setTextColor(countTextColor);
		type.recycle();
	}
	
	public void setCountTextVisible(int visible) {
		// TODO Auto-generated method stub
		tabCount.setVisibility(visible);
	}
	
	
	public void setCountViewBackground(int resId) {
		// TODO Auto-generated method stub
		tabCount.setBackgroundResource(resId);
	}
	
	
	public void setCount(String string) {
		// TODO Auto-generated method stub
		tabCount.setText(string);
	}
	
	public void setCountTextColor(int color) {
		// TODO Auto-generated method stub
		tabCount.setTextColor(color);
	}
	
	public void setCountTextSize(int unit,int textSize) {
		// TODO Auto-generated method stub
		tabCount.setTextSize(unit,textSize);
	}
	
	public void setTabTitle(String string) {
		// TODO Auto-generated method stub
		tabName.setText(string);
	}
	
	public void setTabTextColor(int color) {
		// TODO Auto-generated method stub
		tabName.setTextColor(color);
	}
	
	public void setTabTextTypeface(Typeface tabTypeface, int tabTypefaceStyle){
		tabName.setTypeface(tabTypeface, tabTypefaceStyle);
	}
	
	public void setTabTextSize(int unit,int textSize){
		tabName.setTextSize(unit,textSize);
	}
}
