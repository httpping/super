/**   
* @Title: MyFragment.java 
* @Package com.vp.loveu.my 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zeus   
* @date 2015-10-20 下午4:30:23 
* @version V1.0   
*/
package com.vp.loveu.discover.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.amap.api.mapcore2d.ew;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.base.VpFragment;
import com.vp.loveu.bean.NetBaseBean;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.discover.bean.OldTagBean;
import com.vp.loveu.discover.ui.CourseHomeActivity;
import com.vp.loveu.discover.ui.MatchmakerActivity;
import com.vp.loveu.discover.ui.PuaCourseActivity;
import com.vp.loveu.discover.ui.SearchPromoCodeActivity;
import com.vp.loveu.discover.ui.TutorHomeActivity;
import com.vp.loveu.discover.widget.DiscoverItemView;
import com.vp.loveu.discover.widget.TagViews;
import com.vp.loveu.my.listener.OnFollowUserListener;
import com.vp.loveu.util.VPLog;
import com.vp.loveu.widget.PublicTitleView;

import cz.msebera.android.httpclient.Header;

/**

 *
 * @ClassName:
 * @Description:发现fragment
 * @author pzj
 * @date 
 */
public class DiscoverFragment extends VpFragment implements OnClickListener {
	private View view;
	private PublicTitleView mPubTitleView;
	
	private DiscoverItemView mItemPua;
	private DiscoverItemView mItemLover;
	private DiscoverItemView mItemCourse;
	private DiscoverItemView mItemTutor;
	private DiscoverItemView mItemPromoCode;
	
	public LinearLayout mOnlineCourseView;//在线课程
	public LinearLayout mPuaView;//pua
	
	public OldTagBean mOldTagBean;//标签
	
	private Context mContext;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.discover_fragment, null);
		mContext=getActivity();
		mItemPua=(DiscoverItemView) view.findViewById(R.id.discover_item_pua);
		mItemLover=(DiscoverItemView) view.findViewById(R.id.discover_item_lover);
		mItemCourse=(DiscoverItemView) view.findViewById(R.id.discover_item_course);
		mItemTutor=(DiscoverItemView) view.findViewById(R.id.discover_item_tutor);
		mItemPromoCode=(DiscoverItemView) view.findViewById(R.id.discover_item_promo_code);
		mItemPua.setOnClickListener(this);
		mItemLover.setOnClickListener(this);
		mItemCourse.setOnClickListener(this);
		mItemTutor.setOnClickListener(this);
		mItemPromoCode.setOnClickListener(this);
		
		mItemLover.hideRigthMore();
		mItemTutor.hideRigthMore();
		mItemPromoCode.hideRigthMore();
		
		mOnlineCourseView = (LinearLayout) view.findViewById(R.id.course_items_layout);
		mPuaView = (LinearLayout) view.findViewById(R.id.pua_items_layout);
		
		initData();
		
		return view;
	}

	
	
	
	/**
	 * 根据接口跟新 ui
	 */
	private void initData() {
		
		VpActivity activity = (VpActivity) getActivity();
		if (activity == null) {
			return;
		}
		
		if (activity.mClient == null) {
			activity.mClient = new VpHttpClient(getActivity());
			activity.mClient.setShowProgressDialog(false);
		}
		activity.mClient.get(VpConstants.DISCOVER_INDEX, new RequestParams(), new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				 NetBaseBean baseBean =NetBaseBean.parseJson(responseBody);
				 if (baseBean.isSuccess()) {
					mOldTagBean = OldTagBean.praseOldTagBean(baseBean.data);
					if (getActivity() ==null) {
						mOldTagBean = null;
						return;
					}
					
					if (mOldTagBean.classroom!=null && mOldTagBean.classroom.size()>0) {
						mOnlineCourseView.setVisibility(View.VISIBLE);
						//online course tag
						TagViews views = new TagViews(getActivity());
						views.updateView(mOldTagBean.classroom);
						mOnlineCourseView.removeAllViews();
						mOnlineCourseView.addView(views);
					}
					
					
					if (mOldTagBean.pua!=null && mOldTagBean.pua.size()>0) {
						mPuaView.removeAllViews();
						mPuaView.setVisibility(View.VISIBLE);
						//pua 课堂 tag
						TagViews pua = new TagViews(getActivity());
						View view =pua.createDivier();
						view.setBackgroundResource(R.color.divide_line_bg);
						pua.addView(view);
						pua.updateView(mOldTagBean.pua);
						mPuaView.addView(pua);
					}
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				//失败了
				//VPLog.d("main", "" +error.);
				error.printStackTrace();
			}
		});
	}
	
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		
		if (isVisibleToUser) {
			VPLog.d("discover", "vis:"+isVisibleToUser + "tag :"+mOldTagBean);
			if (mOldTagBean == null) {
				initData();
			}
		}
		
	}
	
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.discover_item_pua:
			//PUA课堂
			Intent puaIntent=new Intent(mContext,PuaCourseActivity.class);
			mContext.startActivity(puaIntent);
			break;
		case R.id.discover_item_lover:
			//红娘一对一
			Intent matchMakerIntent=new Intent(mContext,MatchmakerActivity.class);
			mContext.startActivity(matchMakerIntent);
			break;
		case R.id.discover_item_course:
			//在线课堂
			Intent intent=new Intent(mContext,CourseHomeActivity.class);
			mContext.startActivity(intent);
			break;
		case R.id.discover_item_tutor:
			//在线导师
			Intent tutorIntent=new Intent(mContext,TutorHomeActivity.class);
			mContext.startActivity(tutorIntent);
			break;
		case R.id.discover_item_promo_code:
			//搜索优惠码
			Intent promocode= new Intent(mContext,SearchPromoCodeActivity.class);
			mContext.startActivity(promocode);
			break;

		default:
			break;
		}
		
	}
}
