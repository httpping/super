/**   
* @Title: EmojiViewAdapter.java 
* @Package com.zngoo.pczylove.adapter 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zeus   
* @date 2015-9-22 下午2:31:48 
* @version V1.0   
*/
package com.vp.loveu.message.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**

 *
 * @ClassName:
 * @Description:
 * @author zeus
 * @date 
 */
public class EmojiViewAdapter extends PagerAdapter {

	private List<View> mImageViewList;

	public EmojiViewAdapter(List<View> mImageViewList) {
		super();
		this.mImageViewList = mImageViewList;
	}

	/** 
	 * 该方法将返回所包含的 Item总个数。为了实现一种循环滚动的效果，返回了基本整型的最大值，这样就会创建很多的Item, 
	 * 其实这并非是真正的无限循环。 
	 */
	@Override
	public int getCount() {
		/*if (mImageViewList.size() == 1) {
			return 1;
		} else {
			return Integer.MAX_VALUE;
		}*/
		return mImageViewList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	/** 
	* 销毁预加载以外的view对象, 会把需要销毁的对象的索引位置传进来，就是position， 
	* 因为mImageViewList只有五条数据，而position将会取到很大的值， 
	* 所以使用取余数的方法来获取每一条数据项。 
	*/
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
		object = null;
	}

	/**
	 * 创建一个view
	 */
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ViewGroup parent = (ViewGroup) mImageViewList.get(
				position % mImageViewList.size()).getParent();
		if (parent != null) {
			parent.removeAllViews();
		}
		container.addView(mImageViewList.get(position % mImageViewList.size()));
		return mImageViewList.get(position % mImageViewList.size());
	}
}
