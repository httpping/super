package com.vp.loveu.coupon.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.vp.loveu.coupon.bean.SelectCourseBean;
import com.vp.loveu.coupon.widget.SelectCourseCardView;

/**
 * 优惠券列表
 * @author tanping
 * 2016-3-7
 */
public class SelectCourseAdapter extends BaseAdapter {
	private Context cxt;
	private List<SelectCourseBean> listSelectCourseBean;

	private ListView listView;
	public static final int NEED_WIDTH = 128 * 2;
	public static final int NEED_HEIGTH = 128 * 2;


	public List<SelectCourseBean> getListSelectCourseBean() {
		return listSelectCourseBean;
	}

	public SelectCourseAdapter(Context formClient, List<SelectCourseBean> list,
			ListView listView) {
		this.listView = listView;
		this.listSelectCourseBean = list;
		this.cxt = formClient;
	}

	@Override
	public int getCount() {
		if (listSelectCourseBean == null) {
			return 0;
		}
		return listSelectCourseBean.size();
	}

	@Override
	public Object getItem(int position) {
		return listSelectCourseBean.get(position);
	}


	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		SelectCourseBean codeBean = listSelectCourseBean.get(position);
		
		SelectCourseCardView cardView = new SelectCourseCardView(cxt);
		cardView.setCouponCodeBean(codeBean);
		
		return cardView;
	}

}