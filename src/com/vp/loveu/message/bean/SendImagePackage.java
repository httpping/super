package com.vp.loveu.message.bean;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.openshare.download.util.Util;
import com.vp.loveu.base.VpApplication;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.message.bean.ChatMessage.MsgSendStatus;
import com.vp.loveu.message.bean.ChatMessage.MsgShowType;
import com.vp.loveu.message.bean.ChatMessage.MsgType;
import com.vp.loveu.message.ui.PrivateChatActivity;
import com.vp.loveu.message.utils.WxUtil;
import com.vp.loveu.util.VPLog;

import cz.msebera.android.httpclient.Header;


/**
 * 发送图片
 * @author tanping
 * 2015-11-18
 */
public class SendImagePackage  implements Runnable {
	
	public VpHttpClient mClient;
	public String imgPath ;//文件路径
	public Handler mHandler;
	public  ChatMessage chatMessage;
	
	public static final String TAG = SendImagePackage.class.getName();
	
	public static int WIDTH =1440;
	public static int HEIGHT =1920;
	
	
	/**
	 * 文件上传
	 * @param client
	 * @param imgPath
	 */
	public SendImagePackage(VpHttpClient client ,String imgPath,Handler handler) {
		
		if (TextUtils.isEmpty(imgPath) || client== null) {
			return ;
		}
		mClient = client;
		mHandler = handler;
		ChatMessage message = new ChatMessage();
		message.showType = MsgShowType.out_img.ordinal();
		message.sendStatus = MsgSendStatus.send.ordinal();
		message.msgType = MsgType.img.getValue();
		message.imgUrl = imgPath;
		
		try {//提取图片的宽高
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
			
			int ih = options.outHeight; 
			int iw = options.outWidth;
			
			int flag = 1;
			
			while ( HEIGHT*flag < ih || WIDTH*flag <iw ){
				flag++;
			}
			
			message.width = iw/flag;
			message.height = ih/flag;
		} catch (Exception e) {
		}
		
		message.createBody();
		chatMessage = message;
		
	}
	
	/**
	 * 发送
	 */
	public void send(){
		VPLog.d(TAG, "send");
		 new Thread(this).start(); //kais
	}
	
	
	
	
	
	public ChatMessage getChatMessage() {
		return chatMessage;
	}

	@Override
	public void run() {
	
		try {
			String imgName = Util.getMd5(chatMessage.imgUrl);
			String imgNewPath = VpApplication.getContext().getCacheDir().getPath() +"/" + imgName+".jpg" ;
			
			File file = new File(imgNewPath);
			if (file.exists()) {//存在 已经发送过
				
			}
			boolean b = WxUtil.transImage(chatMessage.imgUrl, imgNewPath, WIDTH, HEIGHT, 80);
			
			if (b) {
				chatMessage.locImgUrl = imgNewPath;
				upLoadPortrait(imgNewPath);
			}
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 上传文件
	 */
	public String newPath;
	private void upLoadPortrait(String path) {
		newPath = path;
		mClient.postFile(VpConstants.FILE_UPLOAD, VpConstants.FILE_UPLOAD_PATH_PORTRAIT, path, true, true, true,responseHandler);
	}

	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result=ResultParseUtil.deAesResult(responseBody);
				VPLog.d(TAG, "res:" + result);
				if(result!=null){
					JSONObject json = null;
					try {
						json = new JSONObject(result);
						String state = json.getString(VpConstants.HttpKey.STATE);
						if("1".equals(state)){//上传成功
							String imgUrl = json.getString(VpConstants.HttpKey.URL);
							VPLog.d(TAG, "成功:"+imgUrl);
							chatMessage.sendImgUrl = imgUrl;
							try {
								//new File(newPath).delete();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}else{
							VPLog.d(TAG, "失败:");
						}
					} catch (JSONException e1) {
						e1.printStackTrace();
					};
				}
				
				//返回
				if (mHandler!=null) {
					Message msg = new Message();
					msg.what = PrivateChatActivity.SEND_IMGE_REQUEST;
					msg.obj = chatMessage;
					mHandler.sendMessage(msg);
				}
				
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				//返回	
				if (mHandler!=null) {
					Message msg = new Message();
					msg.what = PrivateChatActivity.SEND_IMGE_REQUEST;
					msg.obj = chatMessage;
					mHandler.sendMessage(msg);
				}	
			}
		};	
}
