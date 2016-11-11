/**   
 * @Title: VpFragment.java 
 * @Package com.vp.loveu.base 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author zeus   
 * @date 2015-10-20 下午4:29:35 
 * @version V1.0   
 */
package com.vp.loveu.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vp.loveu.util.VPLog;

/**
 * 
 * 
 * @ClassName: 基础fragment
 * @Description:
 * @author ping
 * @date
 */
public class VpFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}


	@Override
	public void onPause() {
		VPLog.d(this.getClass().getName(), "---------onPause ");
		super.onPause();
	}

	@Override
	public void onStop() {
		VPLog.d(this.getClass().getName(), "---------onStop ");
		super.onStop();
	}

	@Override
	public void onDetach() {
		VPLog.d(this.getClass().getName(), "---------onDetach ");
		super.onDetach();
	}

	@Override
	public void onDestroyView() {
		VPLog.d(this.getClass().getName(), "---------onDestroyView ");
		super.onDestroyView();
	}

}
