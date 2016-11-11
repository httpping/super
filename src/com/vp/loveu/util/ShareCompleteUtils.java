package com.vp.loveu.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.comm.VpConstants;

import cz.msebera.android.httpclient.Header;

/**
 * @author：pzj
 * @date: 2015年12月18日 上午9:56:12
 * @Description: 分享完成后上报功能
 */
public class ShareCompleteUtils {

	private Context mContext;
	
	public ShareCompleteUtils(Context context) {
		this.mContext=context;
	}
	/**
	 * 
	 * @param uid 用户ID
	 * @param id  被分享信息ID
	 * @param type 类型 999=分享APP本身 1=长文推荐 2=PUA课堂 3=大家都在聊
	 */
	public void reportData(int uid,int id,int type){
		VpHttpClient client=new VpHttpClient(mContext);
		client.setShowProgressDialog(false);
		String url=VpConstants.PUBLIC_ADD_SHARE;//分享上传接口
		JSONObject data= new JSONObject();
		try {
			data.put("uid", uid);
			data.put("id", id);
			data.put("type", type);//点赞类型1=长文推荐 2=PUA课堂 3=大家都在聊

			client.post(url, new RequestParams(), data.toString(), false, new AsyncHttpResponseHandler() {
				
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String result = ResultParseUtil.deAesResult(responseBody);
					try {
						JSONObject json = new JSONObject(result);
						String code = json.getString(VpConstants.HttpKey.CODE);

						if ("0".equals(code)) {
							
						} else {
							String message = json.getString(VpConstants.HttpKey.MSG);
							Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
					
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();

		}

	
	}
}
