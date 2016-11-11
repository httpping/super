package com.vp.loveu.my.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AbsListView;

import com.vp.loveu.index.adapter.VpBaseAdapter;
import com.vp.loveu.index.holder.BaseHolder;
import com.vp.loveu.my.bean.MyCenterPayBean.MyCenterPayDataBean;
import com.vp.loveu.my.listener.OnPayClickListener;
import com.vp.loveu.my.widget.MyPayActiveItem;
import com.vp.loveu.my.widget.MyPayCourse;
import com.vp.loveu.my.widget.MyPayFellHelp;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2016年1月5日下午12:02:16
 * @功能TODO
 * @作者 mi
 */

public class MyCenterPlayListAdapter extends VpBaseAdapter<MyCenterPayDataBean> {

	private OnPayClickListener listener;

	public MyCenterPlayListAdapter(Context context, AbsListView listView, List<MyCenterPayDataBean> data) {
		super(context, listView, data);
	}

	public MyCenterPlayListAdapter(Context context, AbsListView listView, List<MyCenterPayDataBean> mData, OnPayClickListener listener) {
		super(context, listView, mData);
		this.listener = listener;
	}

	@Override
	public BaseHolder<MyCenterPayDataBean> getHolder() {
		return null;
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public int getItemType(int position) {
		int type = mDatas.get(position).type;
		if (type == 1) {
			return 0;
		} else if (type == 2) {
			return 1;
		} else if (type == 3) {
			return 2;
		}
		return super.getItemType(position);
	}

	@Override
	public BaseHolder<MyCenterPayDataBean> getItemTypeHoler(int type) {
		BaseHolder<MyCenterPayDataBean> holder = null;
		switch (type) {
		case 0:
			holder = new MyPayActiveItemHolder(mContext);
			break;
		case 1:
			holder = new MyPayFellHelpHolder(mContext);
			break;
		case 2:
			holder = new MyPayCourseHolder(mContext);
			break;
		default:
			break;
		}
		return holder;
	}

	class MyPayActiveItemHolder extends BaseHolder<MyCenterPayDataBean> {

		private MyPayActiveItem itemView;

		public MyPayActiveItemHolder(Context context) {
			super(context);
		}

		@Override
		protected View initView() {
			return new MyPayActiveItem(mContext);
		}

		@Override
		protected void findView() {
			itemView = (MyPayActiveItem) mRootView;
			itemView.setOnPayClickListener(listener);
		}

		@Override
		protected void initData(MyCenterPayDataBean bean) {
			itemView.setData(bean);
		}
	}

	class MyPayFellHelpHolder extends BaseHolder<MyCenterPayDataBean> {

		private MyPayFellHelp itemView;

		public MyPayFellHelpHolder(Context context) {
			super(context);
		}

		@Override
		protected View initView() {
			return new MyPayFellHelp(mContext);
		}

		@Override
		protected void findView() {
			itemView = (MyPayFellHelp) mRootView;
		}
		
		@Override
		protected void initData(MyCenterPayDataBean data) {
			itemView.setData(data);
		}

	}

	class MyPayCourseHolder extends BaseHolder<MyCenterPayDataBean> {

		private MyPayCourse itemView;

		public MyPayCourseHolder(Context context) {
			super(context);
		}

		@Override
		protected View initView() {
			return new MyPayCourse(mContext);
		}

		@Override
		protected void findView() {
			itemView = (MyPayCourse) mRootView;
		}

		@Override
		protected void initData(MyCenterPayDataBean data) {
			itemView.setData(data);
		}
	}
}
