package com.vp.loveu.message.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.vp.loveu.message.bean.OperateDataBaseEntity;
import com.vp.loveu.message.view.UIImgAndTitleView;
import com.vp.loveu.util.ScreenUtils;
import com.vp.loveu.util.VPLog;

/**
 * 

 *
 * @ClassName:
 * @Description: plus 面板填充
 * @author ping
 * @date
 */
public class InputPlusAdapter extends BaseAdapter {

	
	
	private List<OperateDataBaseEntity> data;
	private LayoutInflater inflater;
	private Context mContext;
	
	private int size = 0;

	public InputPlusAdapter(Context context, List<OperateDataBaseEntity> list) {
		this.inflater = LayoutInflater.from(context);
		this.data = list;
		this.size = list.size();
		mContext = context;
		ScreenUtils.initScreen((Activity) context);
	}

	@Override
	public int getCount() {
		return this.size;
	}


	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		OperateDataBaseEntity plusBaseEntity = data.get(position);
		UIImgAndTitleView view = new UIImgAndTitleView(mContext);
		view.setEntity(plusBaseEntity);
		VPLog.d("tag", "face adapter");

		return view;
	}

 
	@Override
	public Object getItem(int position) {
		return data==null?null : data.get(position);
	}
}