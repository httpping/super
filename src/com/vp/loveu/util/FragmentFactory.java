package com.vp.loveu.util;

import com.vp.loveu.channel.fragment.ChannelFragment;
import com.vp.loveu.discover.fragment.DiscoverFragment;
import com.vp.loveu.index.fragment.IndexFragment;
import com.vp.loveu.message.fragment.MessageFragment;

import android.support.v4.app.Fragment;
import android.util.SparseArray;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月16日上午10:15:16
 * @功能TODO
 * @作者 mi
 */

public class FragmentFactory {

	private  SparseArray<Fragment> map = new SparseArray<Fragment>();
	
	public FragmentFactory(){
		
	}
	
	public Fragment getFragment(int position) {

		Fragment fragment = map.get(position);
		if (fragment != null) {
			return fragment;
		}

		switch (position) {
		case 0:
			// 首页
			fragment = new IndexFragment();
			break;
		case 1:
			// 频道
			fragment = new ChannelFragment();
			break;
		case 2:
			// 发现
			fragment = new DiscoverFragment();

			break;
		case 3:
			// 消息
			fragment = new MessageFragment();

			break;

		default:
			break;
		}

		map.put(position, fragment);
		return fragment;
	}
}
