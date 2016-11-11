/**   
* @Title: MessageFragment.java 
* @Package com.vp.loveu.message 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zeus   
* @date 2015-10-20 下午4:30:52 
* @version V1.0   
*/
package com.vp.loveu.message.fragment;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.vp.loveu.R;
import com.vp.loveu.base.VpFragment;
import com.vp.loveu.message.adapter.MessageSessionAdapter;
import com.vp.loveu.message.bean.PushNoticeBean;
import com.vp.loveu.message.provider.MessageSessionProvider;
import com.vp.loveu.message.view.MessageIndexHeadView;
import com.vp.loveu.util.VPLog;
import com.vp.loveu.widget.TopView;

/**

 *
 * @ClassName: 消息主fragment
 * @Description:
 * @author ping
 * @date 
 */
public class MessageFragment extends VpFragment {
	private View view;
	private TopView topView;
	
	ListView listView;
	MessageSessionAdapter adapter;
	
	
	MessageIndexHeadView mHeadView;
	
	
	private Cursor mCursor;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.message_fragment, null);
		initView(view);
		initData();
		VPLog.d("tag", "ddd");
		return view;
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		VPLog.d("main", "message:"+isVisibleToUser +" act:"+getActivity() +" ada:"+adapter);
		if (isVisibleToUser && adapter==null && getActivity()!=null) {//init页面
			//initData();
		}
		/*if (adapter!=null) {
			listView.setAdapter(adapter);
		}*/
		if (isVisibleToUser && getActivity()!=null) {
			adapter.notifyDataSetChanged();
			ContentResolver contentProvider = getActivity().getContentResolver();
			contentProvider.update(MessageSessionProvider.CONTENT_MSG_SESSION_URI,
					null, null, null);
			PushNoticeBean.sendUpdateBroadcast(getActivity());
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		VPLog.d("main", "onDestroy");
		adapter = null;
		if (mCursor!=null) {
			mCursor.close();
		}
	}
	
	private void initData() {  
		VPLog.d("main", "initData");
		ContentResolver contentProvider = getActivity().getContentResolver();
		if (mCursor!=null) { //回收关闭
			mCursor.close();
		}
		mCursor = contentProvider.query(MessageSessionProvider.CONTENT_MSG_SESSION_URI,
				null, null, null,null);
		adapter =new MessageSessionAdapter(getActivity(),mCursor);
		listView.setAdapter(adapter);
	}

	private void initView(View view) {
		listView = (ListView) view
				.findViewById(R.id.message_session_list);
		mHeadView = new MessageIndexHeadView(getActivity());
		listView.addHeaderView(mHeadView);
	}
	
	@Override
	public void onResume() {
		VPLog.d("main", "onResume");
		VPLog.d(this.getClass().getName(), "---------onResume ");
		super.onResume();

		if (getActivity() == null) {
			return;
		}
		
		if (getUserVisibleHint() && getActivity()!=null) {
			ContentResolver contentProvider = getActivity().getContentResolver();
			contentProvider.update(MessageSessionProvider.CONTENT_MSG_SESSION_URI,
					null, null, null);
			PushNoticeBean.sendUpdateBroadcast(getActivity());
		}
	}
	
}
