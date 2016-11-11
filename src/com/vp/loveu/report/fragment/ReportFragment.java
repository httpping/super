/**   
* @Title: MessageFragment.java 
* @Package com.vp.loveu.message 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zeus   
* @date 2015-10-20 下午4:30:52 
* @version V1.0   
*/
package com.vp.loveu.report.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vp.loveu.base.VpFragment;

/**

 *
 * @ClassName: 
 * @Description:发表动态fragment
 * @author pzj
 * @date 
 */
public class ReportFragment extends VpFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		TextView tv=new TextView(getActivity());
		tv.setText("发表动态页面");
		return tv;
	}
}
