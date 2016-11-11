package com.vp.loveu.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.comm.VpConstants;

import cz.msebera.android.httpclient.Header;

/**
 * 上传图片工具
 * @author tanping
 * 2015-12-10
 */
public class UpLoadUtil {
	
	static String TAG ="UpLoadUtil";
	
	public Map<String, String> results ;
	public VpHttpClient mClient;
	public List<String> mFiles;
	private OnUpLoadResult mOnUpLoadResult;
	String url;
	String fid ;
	
	private int count =0;
	/**
	 * 
	 * @param client // 客户端
	 * @param files //文件列表
	 * @param url 上传url
	 * @param fid 上传的位置
	 */
	public UpLoadUtil(VpHttpClient client,List<String> files,String url ,String fid){
		this.mFiles = files;
		this.mClient = client;
		this.url =url;
		this.fid = fid;
		
		results = new HashMap<String, String>();
	}
	
	/**
	 * 运行上传文件 ，可以重复调用，如果想全部上传成功
	 * @param ahandler
	 * @return
	 */
	public boolean start(OnUpLoadResult ahandler){
		count=0;
		this.mOnUpLoadResult = ahandler;
		if (mClient == null || mFiles==null || mFiles.size() == 0 || mOnUpLoadResult == null) {
			return false;
		}
		
		for (int i = 0; i < mFiles.size(); i++) {//
			if (TextUtils.isEmpty(mFiles.get(i))) { //null
				count++;
				continue;
			}

			if (results.get(mFiles.get(i))!=null) {//支持重复上传
				count++;
				continue;
			}
			
			if (mFiles.get(i).startsWith("http://")) {//http 开头的不需要上传
				count++;
				results.put(mFiles.get(i), mFiles.get(i));
				continue;
			}
			
			File file = new File(mFiles.get(i));
			if (!file.exists()) {//not exists
				count++;
				continue ;
			}
			
			
			
			mClient.postFile(url, fid, mFiles.get(i), false, false, false, new AsyncHttpResponseHandler() {
				
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					count++;
					String result=ResultParseUtil.deAesResult(responseBody);
					 VPLog.d(TAG, "res:" + result);
						if(result!=null){
							JSONObject json = null;
							try {
								json = new JSONObject(result);
								String state = json.getString(VpConstants.HttpKey.STATE);
								if("1".equals(state)){//上传成功
									String imgUrl = json.getString(VpConstants.HttpKey.URL);
									String fileName = json.getString("name");
									for (int j = 0; j < mFiles.size(); j++) {
										if(mFiles.get(j).endsWith(fileName)){
											results.put(mFiles.get(j), imgUrl);
										}
									}

									VPLog.d(TAG, "成功:"+imgUrl);
								}else{
									VPLog.d(TAG, "失败:");
								}
							} catch (JSONException e1) {
								e1.printStackTrace();
							};
						}
					
					if (count >= mFiles.size()) {//完成了
						mOnUpLoadResult.finish(UpLoadUtil.this);
					}
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					count++;
					
					if (count >= mFiles.size()) {//完成了
						mOnUpLoadResult.finish(UpLoadUtil.this);
					}
				}
			});
		}
		
		if (count == mFiles.size()) {//
			mOnUpLoadResult.finish(UpLoadUtil.this);
		}
		
		
		return true;
	}

	
	public static interface OnUpLoadResult{
		public void finish(UpLoadUtil data);
	}
	
	
	/**
	 * 是否上传成功
	 * @return
	 */
	public boolean isUpLoadSuccess(){
		for (int i = 0; i < mFiles.size(); i++) {
			if (results.get(mFiles.get(i))==null) {//支持重复上传
				return false;
			}
		}
		return true;
	}
 
}
