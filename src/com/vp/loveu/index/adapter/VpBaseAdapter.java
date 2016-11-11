package com.vp.loveu.index.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.vp.loveu.index.holder.BaseHolder;

/**
 * @项目名称nameloveu1.0
 * 
 * @param <T>
 * @时间2016年1月4日下午5:55:41
 * @功能TODO
 * @作者 mi
 */

public abstract class VpBaseAdapter<T> extends BaseAdapter {

	public List<T> mDatas;
	public int normal_count = 1;
	public int normal_type = 0;
	public Context mContext;
	public AbsListView mListView;

	public VpBaseAdapter(Context context, AbsListView listView, List<T> data) {
		this.mContext = context;
		this.mListView = listView;
		this.mDatas = data;
	}

	@Override
	public int getCount() {
		if (mDatas != null) {
			return mDatas.size();
		}
		return 0;
	}

	@Override
	public int getItemViewType(int position) {
		return getItemType(position);
	}

	@Override
	public int getViewTypeCount() {
		return normal_count;
	}

	@Override
	public Object getItem(int position) {
		if (mDatas != null) {
			return mDatas.get(position);
		}
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		BaseHolder<T> holder = null;
		if (convertView == null) {
			if (getViewTypeCount() == normal_count) {// 1中类型
				holder = getHolder();
				convertView = holder.getView();
			} else {// 多种类型
				holder = getItemTypeHoler(type);
				if (holder == null) {
					throw new RuntimeException("please init getItemTypeHoler");
				}
				convertView = holder.getView();
			}
			convertView.setTag(holder);
		} else {
			holder = (BaseHolder<T>) convertView.getTag();
		}

		T t = mDatas.get(position);
		holder.setData(mListView, this, mDatas, position, t);

		return convertView;
	}

	public abstract BaseHolder<T> getHolder();

	public int getItemType(int position) {
		return normal_type;
	};

	public BaseHolder<T> getItemTypeHoler(int type) {
		return null;
	};
}
