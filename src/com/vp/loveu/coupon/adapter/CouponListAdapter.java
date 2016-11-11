package com.vp.loveu.coupon.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.vp.loveu.coupon.widget.CouponCardView;
import com.vp.loveu.my.bean.PromoCodeBean;

/**
 * 优惠券列表
 * @author tanping
 * 2016-3-7
 */
public class CouponListAdapter extends BaseAdapter {
	private Context cxt;
	private List<PromoCodeBean> listPromoCodeBean;

	private ListView listView;
	public static final int NEED_WIDTH = 128 * 2;
	public static final int NEED_HEIGTH = 128 * 2;


	public List<PromoCodeBean> getListPromoCodeBean() {
		return listPromoCodeBean;
	}

	public CouponListAdapter(Context formClient, List<PromoCodeBean> list,
			ListView listView) {
		this.listView = listView;
		this.listPromoCodeBean = list;
		this.cxt = formClient;
	}

	@Override
	public int getCount() {
		if (listPromoCodeBean == null) {
			return 0;
		}
		return listPromoCodeBean.size();
	}

	@Override
	public Object getItem(int position) {
		return listPromoCodeBean.get(position);
	}


	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		PromoCodeBean codeBean = listPromoCodeBean.get(position);
		CouponCardView cardView =null;
		if (convertView !=null) {
			cardView = (CouponCardView) convertView;
		}else{
		  cardView = new CouponCardView(cxt);
		}
			
		cardView.setCouponCodeBean(codeBean);
		
		return cardView;
	}

}