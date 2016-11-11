package com.vp.loveu.message.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;
import com.vp.loveu.R;
import com.vp.loveu.message.adapter.EmojiViewAdapter;
import com.vp.loveu.message.adapter.FaceAdapter;
import com.vp.loveu.message.bean.ChatEmoji;
import com.vp.loveu.message.utils.DensityUtil;
import com.vp.loveu.message.utils.FaceConversionUtil;

public class EmojiPopupWindow  extends PopupWindow{

	Context mContext ;
	
	public ArrayList<View> pageViews;
	/** 表情数据填充器 */
	public List<FaceAdapter> faceAdapters;
	/** 表情集合 */
	public List<List<ChatEmoji>> emojis;
	/** 当前表情页 */
	public int current = 0;
	public CirclePageIndicator mPageIndicator;
	public ViewPager viewPagerEmoji;
	
	OnEmojiItemClick mItemClickListener;
	
	public EmojiPopupWindow(Context context,OnEmojiItemClick clickListener) {
		super(context);
		mItemClickListener = clickListener;
		mContext = context;
		View rootview = LayoutInflater.from(context).inflate(R.layout.message_chat_input_view_emoji_popup, null);
		viewPagerEmoji = (ViewPager) rootview.findViewById(R.id.vp_contains);
		mPageIndicator = (CirclePageIndicator) rootview.findViewById(R.id.indicator);
		initEmojiAll();
		
		TextView textView = new TextView(getContext());
		textView.setText("hello world");
		setWidth(-1);
		setContentView(rootview);
		
		
		this.setWidth(-1);
		//设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		//设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(false);
		setOutsideTouchable(false);
		setIgnoreCheekPress();
		getContentView().setBackgroundResource(R.color.write);
		//实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xffffff);
		//设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		//setAnimationStyle(R.style.dialogWindowAnim);
	}
	
	private void initEmojiAll() {
		initEmojiView();
		initEmojiData();
	}
	

	/**
	 * 
	 */
	private void initEmojiData() {
		
		viewPagerEmoji.setAdapter(new EmojiViewAdapter(pageViews));
		viewPagerEmoji.setCurrentItem(0);
		mPageIndicator.setViewPager(viewPagerEmoji);
		mPageIndicator.setRadius(DensityUtil.dip2px(getContext(), 3.5f));
		mPageIndicator.setPadding(DensityUtil.dip2px(getContext(), 20), 0, 0, 0);
		
		current = 0;
	}
 
	/**
	 * 
	 */
	private void initEmojiView() {
		
		emojis = FaceConversionUtil.getInstace().emojiLists;
		if (emojis == null || emojis.size() == 0) {
			FaceConversionUtil.getInstace().getFileText(getContext());
			emojis = FaceConversionUtil.getInstace().emojiLists;
		}
		pageViews = new ArrayList<View>();

		// 中间添加表情页

		faceAdapters = new ArrayList<FaceAdapter>();
		for (int i = 0; i < emojis.size(); i++) {
			GridView view = new GridView(getContext());
			FaceAdapter adapter = new FaceAdapter(getContext(), emojis.get(i),mItemClickListener);
			view.setAdapter(adapter);
			faceAdapters.add(adapter);
			 
			view.setNumColumns(7);
			view.setBackgroundColor(Color.TRANSPARENT);
			view.setHorizontalSpacing(1);
			view.setVerticalSpacing(1);
			view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
			view.setCacheColorHint(0);
			view.setPadding(1, 0, 1, 0);
			view.setSelector(new ColorDrawable(Color.TRANSPARENT));
			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
			view.setGravity(Gravity.CENTER);
			pageViews.add(view);
		}

		
	}
	
	public interface OnEmojiItemClick{
		public void onEmojiClick(ChatEmoji emoji);
	}
	
	public Context getContext(){
		return mContext;
	}

}
