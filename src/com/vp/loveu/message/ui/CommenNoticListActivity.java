package com.vp.loveu.message.ui;

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
import com.vp.loveu.message.fragment.CommentFragment;
import com.vp.loveu.message.fragment.LikeFragment;
import com.vp.loveu.widget.PagerSlidingTabStrip;

/**
 * @author：pzj
 * @date: 2015年12月4日 下午5:12:39
 * @Description:评论回复列表
 */
public class CommenNoticListActivity extends VpActivity  {

	private ViewPager mViewPager;
	private PagerSlidingTabStrip mIndicator;
	private ImageView mIvBack;
	private String[] titles = new String[]{"评论回复","被赞"};
	private List<Fragment> mData = new ArrayList<Fragment>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_commentnotic_list_activity);
		initView();
		initData();
	}

	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.fans_viewpager);
		mIndicator = (PagerSlidingTabStrip) findViewById(R.id.fans_indicator);
		mIvBack = (ImageView) findViewById(R.id.fans_iv_back);
		mIvBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	private void initData() {
		CommentFragment followFragment = new CommentFragment();
		LikeFragment fansFragment = new LikeFragment();
		mData.add(followFragment);
		mData.add(fansFragment);
		mViewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
		mIndicator.setViewPager(mViewPager);
		
		
	}
	
	public void updateTabPointStatus(int position,boolean isShow){
		mIndicator.updateTabPointStatus(position, isShow);
	}
	
	private class MyAdapter extends FragmentPagerAdapter{

		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return mData.get(arg0);
		}

		@Override
		public int getCount() {
			return mData.size();
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}
	}

}
