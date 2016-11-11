package com.vp.loveu.index.holder;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AbsListView;

import com.vp.loveu.index.adapter.VpBaseAdapter;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月17日上午9:00:59
 * @功能 holder的基类
 * @作者 mi
 */

public abstract class BaseHolder<T> {

	protected View mRootView;
	protected Context mContext;
	protected List<T> mDatas;
	protected T mData;
	protected int mPosition;
	protected VpBaseAdapter<T> mAdapter;
	protected AbsListView mListview;

	public BaseHolder(Context context) {
		this.mContext = context;
		mRootView = initView();
		findView();
	}

	protected void findView() {
	}

	protected abstract View initView();

	public View getView() {
		return mRootView;
	}

	protected void initData(T data) {
	};

	public void setData(T t) {
		this.mData = t;
		initData(t);
	}

	public void setData(AbsListView listView,VpBaseAdapter<T> adapter, List<T> mDatas, int position, T bean) {
		this.mListview = listView;
		this.mAdapter = adapter;
		this.mDatas = mDatas;
		this.mPosition = position;
		this.mData = bean;
		initData(bean);
	};
}
