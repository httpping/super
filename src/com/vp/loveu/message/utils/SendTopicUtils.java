package com.vp.loveu.message.utils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.message.bean.ReplyFellHelpBean.Audio;
import com.vp.loveu.util.LoginStatus;

import cz.msebera.android.httpclient.Header;

/**
 * @author：pzj
 * @date: 2015年11月27日 下午2:57:10
 * @Description:
 */
public class SendTopicUtils {
	private Context mContext;
	private VpHttpClient mClient;
	private int mTopId;
	private String mCont;
	private ArrayList<Audio> mAudios;
	private SendTopCallback mCallBack;
	
	public SendTopicUtils(Context context,VpHttpClient client) {
		this.mContext=context;
		this.mClient=client;
	}
	
	/**
	 * 发表
	 */
    public void sendTopic(int topId,String cont,ArrayList<Audio> audios, SendTopCallback callBack) {
    	this.mTopId=topId;
    	this.mCont=cont;
    	this.mAudios=audios;
    	if(callBack==null){
    		Toast.makeText(mContext, "参数SendTopCallback不能为空", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	this.mCallBack=callBack;
    	
    	this.mClient.startProgressDialog();
    	
    	publishTopic();
		
	}

    public void publishTopic(){
    	String url=VpConstants.MESSAGE_USER_RESOLOVE_HELP;
		JSONObject body=new JSONObject();
		try {
			body.put("uid", LoginStatus.getLoginInfo().getUid());
			body.put("id", this.mTopId);
			body.put("status", 2);//=忽略2=同意回答3=拒绝回答
			if(this.mCont!=null)
				body.put("cont", this.mCont);			
			if(this.mAudios!=null && this.mAudios.size()>0){
				String json = new Gson().toJson(this.mAudios);
				JSONArray array=new JSONArray(json);
				body.put("audios",array);
			}
		} catch (Exception e) {
			Toast.makeText(mContext, "请求参数有误", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			return;
		}
    	this.mClient.post(url, new RequestParams(), body.toString(), false, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				mClient.stopProgressDialog();
				mCallBack.onSuccess();
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				mCallBack.onFailed("网络访问错误");
				mClient.stopProgressDialog();
			}
		});
    }
    public interface SendTopCallback{
    	public void onSuccess();
    	public void onFailed(String msg);
    }
    
    
	
}
