package com.vp.loveu.message.view;


import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.vp.loveu.util.ScreenBean;
import com.vp.loveu.util.ScreenUtils;

/**
 * 多个button 的情况
 * 
 * @author tp
 * 
 */
public class UIButtonGroupView extends LinearLayout implements IUpdateUiView {
	public static final int BUTTON_NUM = 4;// 包含的button数量
	protected int backDrawble ;//= R.color.white;
	/*
	 * protected int leftBtnBackDrawble = drawable.left_btn_bg; protected int
	 * rightBtnBackDrawble = drawable.right_btn_bg; protected int
	 * midBtnBackDrawble = drawable.mid_btn_bg;
	 */
	protected int dividerDrawable = android.R.color.black;
	private boolean isDiver = true;// 是否有diver
	private int itemWidth = 0;
	private int itemHight;
	private float wcompareh = 13F / 15; // 高比宽 高/宽

	private Point theScreenResolution;// 屏幕展现的宽高

	private int selectItemID = -1;// 选中的id
	// private float

	private List<View> views = new ArrayList<View>();// view的集合

	public UIButtonGroupView(Context context) {
		super(context);
		init(context);
	}

	public UIButtonGroupView(Context context, AttributeSet attr) {
		super(context, attr);
		init(context);
	}

	@SuppressLint("NewApi")
	protected void init(Context context) {
		setOrientation(HORIZONTAL);
		setGravity(Gravity.CENTER);
		// setBackgroundResource(backDrawble);
		setWidth(LayoutParams.FILL_PARENT);
		// setBackgroundResource(color.black);
		// setPadding(0, 0, 0,
		// getResources().getDimensionPixelSize(R.dimen.button_group_padding));
		ScreenUtils.initScreen((Activity) getContext());

		if (theScreenResolution == null) { // 初始化
			WindowManager manager = (WindowManager) getContext()
					.getSystemService(Context.WINDOW_SERVICE);
			Display display = manager.getDefaultDisplay();
			theScreenResolution = new Point();
			display.getSize(theScreenResolution);
		}
		if (ScreenBean.displayCoefficient == 0) {
			ScreenUtils.initScreen((Activity) getContext());
		}
		
		
		//不允许多个点击
		setMotionEventSplittingEnabled(false);

	}

	boolean isDraw = false;

	@SuppressLint("NewApi")
	/**
	 * 更新view
	 */
	public void updateViewShow() {
		if (views == null || views.size() == 0 || theScreenResolution == null) { //
			// setVisibility(View.GONE);
			return;
		}
		setBackgroundResource(backDrawble);
		// if (!isDiver) {
		if (itemWidth <= 0) {// 不设置宽度 根据全屏幕来计算
			itemWidth = theScreenResolution.x / BUTTON_NUM;
		}
		itemHight = (int) (itemWidth * wcompareh);// 15比15


		if (!isDraw) { // 减少重画
			removeAllViews();
			for (int i = 0; i < views.size(); i++) {
				// Log.i("tag", "===" + i + " width" + itemHight);
				final View view = views.get(i);
				LayoutParams layoutParams = new LayoutParams(itemWidth,
						itemHight);
				view.setLayoutParams(layoutParams);
				layoutParams.weight = 1;
				((IUpdateUiView) view).updateViewShow();
				addView(view);
				// view

				if (isDiver) {// 有divier
					if (i + 1 != views.size()) {
						addDivider();// 增加divier
					}
				}
			}
			isDraw = true;
		} else {
			Log.i("tag", "重画来了， 避免了 对不对");
			try {
				for (int i = 0; i < views.size(); i++) {

					IUpdateUiView view = (IUpdateUiView) views.get(i);
					if (view != null) {
						view.updateViewShow();
					}
				}
			} catch (Exception e) {
			}

		}

	}

	/**
	 * 更新view的选择状态
	 * 
	 * @param view
	 */
	public void updateClickItem(View view) {
		if (view == null || views == null) {
			return;
		}
		updateTitleViewSelect();
		for (int i = 0; i < views.size(); i++) {
			if (view.equals(views.get(i))) {// 没有选中 不允许重复选中
				if (views.get(i).isSelected()) {
					// 已经处理过了，无需处理
				} else {
					updateTitleViewSelect();
					if (selectItemID != -1) {
						((IUpdateUiView) views.get(selectItemID))
								.updateViewShow();// 重画
					}
					views.get(i).setSelected(true);
					selectItemID = i;
					((IUpdateUiView) views.get(selectItemID)).updateViewShow();// 重画
				}

				break;
			}
		}
	}
	
	public void pagerselect(int postion){
		
		if ( postion <0 || views == null || postion>=views.size()) {
			return ;
		}
		
		
		if (selectItemID !=-1) {
			views.get(selectItemID).setSelected(false);
		}
		views.get(postion).setSelected(true);

		selectItemID = postion;
		
		updateViewShow();
		 
	}

	/**
	 * 更新选中状态 全部不选中
	 */
	public void updateTitleViewSelect() {
		for (int i = 0; i < views.size(); i++) {
			// titleViews.get(i).setSelect(false);
			views.get(i).setSelected(false);
			try {
				((IUpdateUiView) views.get(selectItemID)).updateViewShow();// 重画
			} catch (Exception e) {
				// e.printStackTrace();
			}

		}
	}

	public void addDivider() {
		Log.i("tag", "diviver");
		View divider = new View(getContext());
		LayoutParams dividerParam = new LayoutParams(
				(int) (3/*1 * ScreenBean.displayCoefficient*/), itemHight * 3 / 5);
		dividerParam.topMargin = itemHight / 5;
		dividerParam.bottomMargin = itemHight / 5;
		dividerParam.gravity = Gravity.CENTER;
		divider.setBackgroundDrawable(getResources().getDrawable(
				dividerDrawable));
		divider.setLayoutParams(dividerParam);
		addView(divider, dividerParam);
	}

	/*
	 * public void changeToBlackTheame(){ backDrawble =
	 * drawable.btn_bg_cafe_shop; setBackgroundResource(backDrawble);
	 * leftBtnBackDrawble = drawable.black_left_btn; midBtnBackDrawble =
	 * drawable.black_mid_btn; rightBtnBackDrawble = drawable.black_right_btn; }
	 */

	public boolean isDiver() {
		return isDiver;
	}

	public void setDiver(boolean isDiver) {
		this.isDiver = isDiver;
	}

	public void setWidth(int width) {
		this.itemWidth = width;
	}

	public int getHight() {
		return itemHight;
	}

	public void setHight(int hight) {
		this.itemHight = hight;
	}

	public List<View> getViews() {
		return views;
	}

	public void setViews(List<View> views) {
		this.views = views;
		isDraw = false;
		updateViewShow();
	}

	public int getItemWidth() {
		return itemWidth;
	}

	public void setItemWidth(int itemWidth) {
		this.itemWidth = itemWidth;
	}

	public int getItemHight() {
		return itemHight;
	}

	public void setItemHight(int itemHight) {
		this.itemHight = itemHight;
	}

	public int getBackDrawble() {
		return backDrawble;
	}

	public void setBackDrawble(int backDrawble) {
		this.backDrawble = backDrawble;
	}

	public float getWcompareh() {
		return wcompareh;
	}

	/**
	 * 宽高比
	 * 
	 * @param wcompareh
	 */
	public void setWcompareh(float wcompareh) {
		this.wcompareh = wcompareh;
	}

	public int getSelectItemID() {
		return selectItemID;
	}

	public void setSelectItemID(int selectItemID) {
		this.selectItemID = selectItemID;

	}

	/**
	 * item =-1 全部
	 * 
	 * @param b
	 * @param item
	 */
	public void setItemClickable(boolean b, int item) {
		try {
			if (item == -1) {
				for (int i = 0; i < views.size(); i++) {
					views.get(i).setClickable(b);
				}
			} else {
				views.get(item).setClickable(b);
			}
		} catch (Exception e) {
			Log.i("button","保证没有问题 没有必要盘");
			e.printStackTrace();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// TODO Auto-generated method stub

	}
}
