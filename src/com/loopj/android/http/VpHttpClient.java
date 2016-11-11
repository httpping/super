/**   
* @Title: VpHttpClient.java 
* @Package com.loopj.android.http 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zeus   
* @date 2015-10-21 下午3:09:26 
* @version V1.0   
*/
package com.loopj.android.http;
 
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.vp.loveu.base.VpActivity;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.util.AESUtil;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.MD5Util;
import com.vp.loveu.util.VPLog;
import com.vp.loveu.widget.CustomProgressDialog;

import cz.msebera.android.httpclient.entity.StringEntity;

/**

 *
 * @ClassName: 网络请求客户端
 * @Description: 网络请求
 * @author ping
 * @date 
 */
public class VpHttpClient  extends AsyncHttpClient{
	
	//public static final String KEY="1234567891234567"; //very happy
	public static final String KEY=VpConstants.KEY_DD; //very happy
	private CustomProgressDialog progressDialog = null;
	private boolean showProgressDialog = true;	
	public boolean isMode = false;

	private Context mContext;
	/**
	 * 服务器制定端口
	 */
	public VpHttpClient(Context context){
		super(VpConstants.PORT);
		this.mContext = context;
	}
	
	
	/**
	 * url : 传人的URL 
	 * Context :上下文 activity
	 * params ：参数
	 * responseHandler:回调
	 */
	public RequestHandle get(Context context ,String url, RequestParams params, ResponseHandlerInterface responseHandler) {
		url = addPublicParams(url, null,"",false ,responseHandler);
		if (responseHandler!=null && responseHandler instanceof AsyncHttpResponseHandler) {
			((AsyncHttpResponseHandler)responseHandler).vpHttpClient = this;
		}
		return super.get(context, url, params, responseHandler);
	}	
	
	/**
	 * url : 传人的URL 
	 * 
	 * 
 	 * params ：参数
	 * responseHandler:回调
	 */
	public RequestHandle get(String url, RequestParams params, ResponseHandlerInterface responseHandler) {
		return get(mContext, url, params, responseHandler);
	}	
	
	
	/**
	 * 添加公共参数
	 * @param url 
	 * @param params url后面的参数
	 * @param body post的内容
	 * @return
	 */
	private String addPublicParams(String url,RequestParams params,String body,boolean isEncrypt ,ResponseHandlerInterface responseHandler){
		
		if(url == null){
			return "";
		}
		
		Log.d(LOG_TAG, body+"");
 		RequestParams publicParams = null;
		LoginStatus statue = new LoginStatus();
		String sign = body;
		if(!TextUtils.isEmpty(body) && isEncrypt){// post 请求专用
			try {
				sign =AESUtil.Encrypt(body, KEY);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Log.d(LOG_TAG, "sign:"+sign);
		//System.out.println("sign:"+sign);
		long timestamp =System.currentTimeMillis()/1000 + ResultParseUtil.timeinterval/1000;
		String pp ="UserID="+statue.getAPIUserID() +"&PSW=" +statue.getAPIPassword()+"&Content=" + sign + "&Timestamp="+timestamp;
		//Log.e(LOG_TAG, "pp="+pp);
		String signmd5 = MD5Util.MD532(pp);
	//	Log.d(LOG_TAG, "md5="+signmd5);
		if(params !=null){
			publicParams = params ;
		}else{
			publicParams = new RequestParams();
		}
		
		publicParams.put("user_id", statue.getAPIUserID());
		publicParams.put("timestamp",timestamp);
		publicParams.put("sign", signmd5);
		publicParams.put("v", VpConstants.VERSION);
		
		//获取用户信息
		 LoginUserInfoBean loginuser = LoginStatus.getLoginInfo();
		if (loginuser== null) {
			publicParams.put("psex", 0);
		}else {
			publicParams.put("psex",loginuser.getSex());
		}
 
		url = getUrlWithQueryString(true, url, publicParams);
		
		Log.d(LOG_TAG, "url=" +url);
		return url ;
	}
	
	
	 /**
     * 不加密的 post 请求
     * params : 相当于get的参数
     * body ： 不放在url后的json数据
     * isEncrypt ： 加密
     * URL ： url 
     */
    public RequestHandle post(Context context,String url, RequestParams params,String body ,boolean isEncrypt, ResponseHandlerInterface responseHandler) {
    	
    	if (responseHandler!=null && responseHandler instanceof AsyncHttpResponseHandler) {
			((AsyncHttpResponseHandler)responseHandler).vpHttpClient = this;
		}
    	
    	VPLog.d("as", "b:"+body);
    	
    	String data =null;
    	if(body!=null){
    		if (isEncrypt) {//需要加密
        		try {
					data = AESUtil.Encrypt(body, KEY);
				} catch (Exception e) {
					e.printStackTrace();
				} 
    		}else{
    			data = body.toString();
    		}
    	}
    	
    	url = addPublicParams(url, params,body,isEncrypt, responseHandler);
		super.addHeader(HEADER_CONTENT_TYPE,  "application/x-www-form-urlencoded; charset=UTF-8");
    	//发送请求
    	if(data !=null){
    		try {
				return super.post(context, url,new StringEntity(data,Charset.forName("utf-8")), null, responseHandler);
			} catch (Exception e) {
				if(responseHandler!=null){
	    			responseHandler.sendFailureMessage(-1, null, "数据错误".getBytes(), e);
	    		}
				e.printStackTrace();
			}
    		
    		return null;
    	}else{
    		Log.d(LOG_TAG,"else request");
    		try {
				return super.post(null, url, new StringEntity(""), "application/x-www-form-urlencoded", responseHandler);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
    	}
    	
    	return null;
    }
    
    /**
     * post 请求，采用new client默认的context，可以cancel掉所有的
     * @param url
     * @param params
     * @param body
     * @param isEncrypt
     * @param responseHandler
     * @return
     */
    public RequestHandle post(String url, RequestParams params,String body ,boolean isEncrypt, ResponseHandlerInterface responseHandler) {
        return post(mContext, url, params,body,isEncrypt, responseHandler);
    }
	
    /**
     * 不加密的 post 请求
     * params : 相当于get的参数
     * body ： 不放在url后的json数据
     */
    public RequestHandle post(String url, RequestParams params ,String body, ResponseHandlerInterface responseHandler) {
        return post(mContext, url, params,body,false, responseHandler);
    }
    
    
    /**
     * 文件上传协议
     * url : url 
     * fid : 服务器指定
     * filepath：文件路径
     * isimg  :是否是图片
     * is_square : true为正方形填充图片，false为原图长宽比,仅对图片有效
     * is_save_original :　true:保留原图，false:不保留原图,仅对图片有效
     * responseHandler : 接口回调
     */
    public RequestHandle postFile(final String url,final String fid,final String filePath,final boolean isImg,final boolean is_square,final boolean is_save_original,final ResponseHandlerInterface responseHandler) {
    	//多线程处理
    	Runnable run =  new Runnable() {
			public void run() {
				String furl = getUrlWithQueryString(true, url+"/"+fid, null);
				Log.d(LOG_TAG, "furl :" +furl);
				Log.d(LOG_TAG, "执行 run ");
				if(isImg){//是图片
					//获取图片的宽高
				}
				long timestamp =System.currentTimeMillis()/1000  + ResultParseUtil.timeinterval/1000;
				LoginStatus statue = new LoginStatus();
				String pp ="UserID="+statue.getAPIUserID() +"&PSW=" +statue.getAPIPassword() + "&Timestamp="+timestamp +"&ID="+fid;
				String sign = MD5Util.MD532(pp);
				RequestParams params =new RequestParams();
				params.put("u", statue.getAPIUserID());
				params.put("t",timestamp);
				params.put("s", sign);
				params.put("id", fid);
				try {
					params.put("file", new File(filePath));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					if(responseHandler!=null){
		    			responseHandler.sendFailureMessage(-1, null, "数据错误".getBytes(), e);
		    		}
				}
				VPLog.d(LOG_TAG, params.toString());
				VPLog.d(LOG_TAG, filePath+"");
				//多线程访问 header冲突，无法上传文件
				//VpHttpClient.super.removeHeader(HEADER_CONTENT_TYPE);
				VpHttpClient client = new VpHttpClient(mContext);
				client.setConnectTimeout(1000*60*5);//文件链接超时5分钟
				client.setShowProgressDialog(isShowProgressDialog());
				RequestHandle requestHandle = client.post(furl, params, responseHandler);//上传
				
				try {//加入到 this 的client中， cancel 的时候能够杀死
					if (mContext != null) {
			            List<RequestHandle> requestList;
			            // Add request to request map
			            synchronized (requestMap) {
			                requestList = requestMap.get(mContext);
			                if (requestList == null) {
			                    requestList = Collections.synchronizedList(new LinkedList<RequestHandle>());
			                    requestMap.put(mContext, requestList);
			                }
			            }
			            requestList.add(requestHandle);
			            Iterator<RequestHandle> iterator = requestList.iterator();
			            while (iterator.hasNext()) {
			                if (iterator.next().shouldBeGarbageCollected()) {
			                    iterator.remove();
			                }
			            }
			        }
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
			}
		};
    	
		getThreadPool().execute(run);//运行
		
    	return null;
    }
    

	public void startProgressDialog() {
		
		Log.d(LOG_TAG, "startProgressDialog");
		
		if (mContext==null) {
			return ;
		}
		
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(mContext);
	
		//	if (isMode) {
				progressDialog.setCanceledOnTouchOutside(true);
				isMode = !isMode;
		//	}
	
		}
	
		if (mContext instanceof VpActivity) {
			VpActivity activity = (VpActivity) mContext;
			if (!activity.isFinishing()) {
				try {
					Log.d(LOG_TAG, "startProgressDialog show()");
					progressDialog.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	
	}
	
	public void stopProgressDialog() {
		Log.d(LOG_TAG, "stopProgressDialog");
		try {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean isShowProgressDialog() {
		return showProgressDialog;
	}
	
	public void setShowProgressDialog(boolean showProgressDialog) {
		this.showProgressDialog = showProgressDialog;
	}
	    	
}
