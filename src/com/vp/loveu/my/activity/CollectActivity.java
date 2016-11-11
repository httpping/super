package com.vp.loveu.my.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.my.fragment.ArticleFragment;
import com.vp.loveu.my.fragment.DLLFragment;
import com.vp.loveu.widget.PagerSlidingTabStrip;

/**
 * @项目名称nameloveu1.0
 * @时间2015年12月4日下午2:31:38
 * @功能 收藏的页面
 * @作者 mi
 */

public class CollectActivity extends VpActivity {

	private ImageView mIvBack;
	private PagerSlidingTabStrip mSliding;
	private ViewPager mViewPager;
	private String[] titles = new String[] { "文章", "动态" };
	private MyAdapter adapter;
	private List<Fragment> mDatas = new ArrayList<Fragment>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.collect_activity);
		initView();
		initData();
	}

	private void initView() {
		mIvBack = (ImageView) findViewById(R.id.collect_iv_back);
		mSliding = (PagerSlidingTabStrip) findViewById(R.id.collect_pagerslidiingtabstrip);
		mViewPager = (ViewPager) findViewById(R.id.collect_viewpager);
		mIvBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void initData() {
		ArticleFragment articleFragment = new ArticleFragment();
		DLLFragment dllFragment = new DLLFragment();
		mDatas.add(articleFragment);
		mDatas.add(dllFragment);
		adapter = new MyAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(adapter);
		mSliding.setViewPager(mViewPager);
	}

	private class MyAdapter extends FragmentPagerAdapter {

		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return mDatas.get(arg0);
		}

		@Override
		public int getCount() {
			return mDatas.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}
	}
}
