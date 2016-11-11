package com.vp.loveu.message.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.vp.loveu.R;
import com.vp.loveu.message.adapter.InputPlusAdapter;
import com.vp.loveu.message.bean.OperateDataBaseEntity;
import com.vp.loveu.message.bean.SelectImgageBean;

public class PlusPopupWindow  extends PopupWindow{
	
	Context mContext;
	public GridView mPlusGridView;
	public InputPlusAdapter mInputPlusAdapter;
	public List<OperateDataBaseEntity> mPlusEntities;
	
	public PlusPopupWindow(Context context){
		super(context);
		mContext = context;
		View rootview = LayoutInflater.from(context).inflate(R.layout.message_chat_input_view_plus_popup, null);
		mPlusGridView = (GridView) rootview.findViewById(R.id.plus_grid_view);
		setContentView(rootview);
		
		initPlusView();
		
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

	
	/**
	 * init plus 模块
	 */
	private void initPlusView() {
		mPlusEntities = new ArrayList<OperateDataBaseEntity>();
		/*for (int i = 0; i < 5; i++) {
			OperateDataBaseEntity entity = new OperateDataBaseEntity();
			mPlusEntities.add(entity);
		}*/
		
		OperateDataBaseEntity entity = new OperateDataBaseEntity();
		entity.title ="位置";
		entity.imgRid = R.drawable.message_plus_select_position;
		mPlusEntities.add(entity);
		SelectImgageBean selectImgageBean = new SelectImgageBean();
		selectImgageBean.title = "图片";
		selectImgageBean.imgRid = R.drawable.message_plus_select_image;
		mPlusEntities.add(selectImgageBean);
		
		
		mInputPlusAdapter = new InputPlusAdapter(getContext(), mPlusEntities);
		mPlusGridView.setAdapter(mInputPlusAdapter);
		 
	}


	private Context getContext() {
		return mContext;
	}

	
}
