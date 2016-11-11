package com.vp.loveu.channel.fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.channel.bean.ReportBean;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.util.LoginStatus;

import cz.msebera.android.httpclient.Header;

/**
 * @author：pzj
 * @date: 2015年11月30日 上午9:24:53
 * @Description:举报
 */
public class MoreReportDialogFragment extends DialogFragment implements OnClickListener {
	public static final int CAT_ID_0=0;//其他不合法内容
	public static final int CAT_ID_1=1;//色情暴力
	public static final int CAT_ID_2=2;//虚假广告
	public static final int CAT_ID_3=3;//敏感信息
	public static final int CAT_ID_4=4;//不实谣言
	
	private  TextView mTvEroticism;
	private  TextView mTvFlaseAd;
	private  TextView mTvSensitiveInfo;
	private  TextView mTvRumor;
	private  TextView mTvOther;
	private  TextView mTvCancel;
	private ReportBean mReportBean;
	private Context mContext;
	private VpHttpClient mClient;
	public MoreReportDialogFragment(Context context,ReportBean reportBean) {
		this.mContext=context;
		this.mReportBean=reportBean;
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);  
        View view = inflater.inflate(R.layout.channel_topic_more_pop_report, container);  
        this.mTvEroticism=(TextView) view.findViewById(R.id.channel_topic_report_eroticism);
        this.mTvFlaseAd=(TextView) view.findViewById(R.id.channel_topic_report_falsead);
        this.mTvSensitiveInfo=(TextView) view.findViewById(R.id.channel_topic_report_sensitiveinfo);
        this.mTvRumor=(TextView) view.findViewById(R.id.channel_topic_report_rumor);
        this.mTvOther=(TextView) view.findViewById(R.id.channel_topic_report_other);
        this.mTvCancel=(TextView) view.findViewById(R.id.channel_topic_report_cancel);
        this.mTvEroticism.setOnClickListener(this);
        this.mTvFlaseAd.setOnClickListener(this);
        this.mTvSensitiveInfo.setOnClickListener(this);
        this.mTvRumor.setOnClickListener(this);
        this.mTvOther.setOnClickListener(this);
        this.mTvCancel.setOnClickListener(this);
        return view;
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics( dm );
		getDialog().getWindow().setLayout( dm.widthPixels, getDialog().getWindow().getAttributes().height );
		
		WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
		layoutParams.width = dm.widthPixels;
		layoutParams.gravity = Gravity.BOTTOM;
		getDialog().getWindow().setAttributes(layoutParams);
	}

	@Override
	public void onClick(View v) {
		dismiss();
		switch (v.getId()) {
		case R.id.channel_topic_report_eroticism:  
			//举报-色情暴力
			uploadReportMsg(1);
			break;
		case R.id.channel_topic_report_falsead:  
			//举报-虚假广告
			uploadReportMsg(2);
			break;
		case R.id.channel_topic_report_sensitiveinfo:  
			//举报-敏感信息
			uploadReportMsg(3);
			break;
		case R.id.channel_topic_report_rumor:  
			//举报-不实谣言
			uploadReportMsg(4);
			break;
		case R.id.channel_topic_report_other:  
			//举报-其他不合法内容
			uploadReportMsg(0);
			break;
		case R.id.channel_topic_more_cancel:  
			break;

		default:
			break;
		}

	}
	/**
	 * 
	 * @param catId 举报分类
	 */
	private void uploadReportMsg(int catId){
		if(mReportBean==null){
			Toast.makeText(mContext, "举报参数不能为空!", Toast.LENGTH_SHORT).show();
			return;
		}
		this.mClient=new VpHttpClient(mContext);
    	String url=VpConstants.CHANNEL_USER_COMPLAIN;
		JSONObject body=new JSONObject();
		try {
			body.put("uid", LoginStatus.getLoginInfo().getUid());
			body.put("nickname", LoginStatus.getLoginInfo().getNickname());
			body.put("t_uid", this.mReportBean.getT_uid());
			body.put("t_nickname", this.mReportBean.getT_nickname());
			body.put("remark", this.mReportBean.getRemark());
			if(this.mReportBean.getPics()!=null && this.mReportBean.getPics().size()>0){				
				JSONArray array=new JSONArray(this.mReportBean.getPics());
				body.put("pics",array);
			}
			body.put("cate_id", catId);
			body.put("type", this.mReportBean.getType());
			body.put("info_id", this.mReportBean.getInfo_id());
		} catch (Exception e) {
			Toast.makeText(mContext, "请求参数有误", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			return;
		}
    	this.mClient.post(url, new RequestParams(), body.toString(), false, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result=ResultParseUtil.deAesResult(responseBody);
				JSONObject json = null;
				try {
					json = new JSONObject(result);
					String code = json.getString(VpConstants.HttpKey.CODE);
					if("0".equals(code)){//返回成功
						Toast.makeText(mContext, "举报成功", Toast.LENGTH_SHORT).show();
					}else{
						String message = json.getString(VpConstants.HttpKey.MSG);
						Toast.makeText(mContext,message, Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
				};
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
			}
		});
    
	}
	
}
