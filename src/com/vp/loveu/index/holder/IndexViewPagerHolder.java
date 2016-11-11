package com.vp.loveu.index.holder;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vp.loveu.R;
import com.vp.loveu.bean.InwardAction;
import com.vp.loveu.index.bean.IndexBean.IndexFosBean;
import com.vp.loveu.index.myutils.DisplayOptionsUtils;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月17日上午9:05:50
 * @功能 viewpager 的holder
 * @作者 mi
 */

public class IndexViewPagerHolder extends BaseHolder<List<IndexFosBean>> {

	private ViewPager mViewPager;
	private LinearLayout mViewPagerContainer;
	private MyPagerAdapter mAdapter;
	private List<ImageView> mDatas = new ArrayList<ImageView>();

	public IndexViewPagerHolder(Context context) {
		super(context);
	}
	
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
				sendEmptyMessageDelayed(1, 8000);
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected View initView() {
		return View.inflate(mContext, R.layout.index_public_banner, null);
	}
	
	@Override
	protected void findView() {
		mViewPager = (ViewPager) mRootView.findViewById(R.id.index_viewpager);
		mViewPagerContainer = (LinearLayout) mRootView.findViewById(R.id.index_viewpager_container);
	}

	private void initTouch() {
		mViewPager.setOnTouchListener(new OnTouchListener() {
			private float downX, downY;
			private int position;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					position = mViewPager.getCurrentItem() % mData.size();
					handler.removeCallbacksAndMessages(null);
					downX = event.getX();
					downY = event.getY();
					break;
				case MotionEvent.ACTION_UP:
					handler.removeCallbacksAndMessages(null);
					handler.sendEmptyMessageDelayed(1,8000);
					if (Math.abs(event.getX() - downX) < 5 && Math.abs(event.getY() - downY) < 5) {
						IndexFosBean indexFosBean = mData.get(position);
						Log.d("aaa", indexFosBean.toString());
						if (indexFosBean != null) {
							InwardAction parseAction = InwardAction.parseAction(indexFosBean.url);
							if (parseAction != null) {
								parseAction.toStartActivity(mContext);
							}
						}
					}
					break;
				case MotionEvent.ACTION_MOVE:
					handler.removeCallbacksAndMessages(null);
					break;
				case MotionEvent.ACTION_CANCEL:
					handler.removeCallbacksAndMessages(null);
					handler.sendEmptyMessageDelayed(1,8000);
				default:
					break;
				}
				return false;
			}
		});
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				for (int i = 0; i < mViewPagerContainer.getChildCount(); i++) {
					ImageView childAt = (ImageView) mViewPagerContainer.getChildAt(i);
					if (i == arg0 % mViewPagerContainer.getChildCount()) {
						childAt.setImageResource(R.drawable.index_viewpager_container_bottom_selected);
					} else {
						childAt.setImageResource(R.drawable.index_viewpager_container_bottom);
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		handler.sendEmptyMessageDelayed(1, 8000);
	}

	private class MyPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			if (mDatas.size() <= 0) {
				return;
			}
			container.removeView(mDatas.get(position % mDatas.size()));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			if (mDatas.size() <= 0) {
				return super.instantiateItem(container, position);
			}
			ImageView iv = mDatas.get(position % mDatas.size());
			container.addView(iv);
			return iv;
		}
	}

	public void handlerCancleAll() {
		handler.removeCallbacksAndMessages(null);
	}

	@Override
	protected void initData(List<IndexFosBean> data) {
		mViewPagerContainer.removeAllViews();
		if (data.size() <= 3) {
			for (int j = 0; j < 3; j++) {
				addData(data);
			}
			
		} else {
			addData(data);
		}
		
		addDian(data);
		mAdapter = new MyPagerAdapter();
		mViewPager.setAdapter(mAdapter);
		initTouch();
		int temp = Integer.MAX_VALUE / 2;
		int position = temp - temp % mDatas.size();
		mViewPager.setCurrentItem(position);
	}
	

	private void addDian(List<IndexFosBean> data) {
		for (int i = 0; i < data.size(); i++) {
			ImageView iiv = new ImageView(mContext);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			params.leftMargin = 4;
			if (i == 0) {
				iiv.setImageResource(R.drawable.index_viewpager_container_bottom_selected);
			} else {
				iiv.setImageResource(R.drawable.index_viewpager_container_bottom);
			}
			mViewPagerContainer.addView(iiv, params);
		}
	}

	private void addData(List<IndexFosBean> data) {
		for (int i = 0; i < data.size(); i++) {
			IndexFosBean bean = data.get(i);
			ImageView iv = new ImageView(mContext);
			iv.setScaleType(ScaleType.CENTER_CROP);
			ImageLoader.getInstance().displayImage(bean.pic, iv, DisplayOptionsUtils.getOptionsConfig());
			mDatas.add(iv);
		}
	}
}
