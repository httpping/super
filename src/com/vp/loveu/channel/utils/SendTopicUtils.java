package com.vp.loveu.channel.utils;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.openshare.download.ExecutorDownLoadReactor;
import com.openshare.download.util.Util;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.message.utils.WxUtil;
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
	public static int WIDTH =1440;
	public static int HEIGHT =1920;
	public int mUploadSuccessCount=0;//记录图片上传结果,用于判断是否所有图片上传完毕
	public int mUploadFailedCount=0;
	private ArrayList<String> mSelectedPicList;
	private ArrayList<String> mUploadSuccessPathList=new ArrayList<String>();
	private int mTopId;
	private int mTopRid;
	private String mCont;
	private SendTopCallback mCallBack;
	
	public SendTopicUtils(Context context,VpHttpClient client) {
		this.mContext=context;
		this.mClient=client;
	}
	
	/**
	 * 发表
	 */
    public void sendTopic(int topId,int topRid,String cont,ArrayList<String> selectedPicPaths,SendTopCallback callBack) {
    	this.mTopId=topId;
    	this.mTopRid=topRid;
    	this.mCont=cont;
    	this.mSelectedPicList=selectedPicPaths;
    	if(callBack==null){
    		Toast.makeText(mContext, "参数SendTopCallback不能为空", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	this.mCallBack=callBack;
    	
    	this.mClient.startProgressDialog();
    	
    	if(mSelectedPicList!=null && mSelectedPicList.size()>0){
    		//上传图片
    		for(String path:mSelectedPicList){
    			String newPath = compressBitmap(path);
    			uploadPic(newPath);
    		}
    	}else{
    		//发表文字
    		publishTopic(false);
    	}
		
	}
    
    /**
     * 上传图片到服务器
     * @param newPath 文件路径
     */
    public void uploadPic(final String newPath) {
		mClient.postFile(VpConstants.FILE_UPLOAD, VpConstants.FILE_UPLOAD_PATH_PIC_FILE, newPath, true, true, true, new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result=ResultParseUtil.deAesResult(responseBody);
				if(result!=null){
					JSONObject json = null;
					try {
						json = new JSONObject(result);
						String state = json.getString(VpConstants.HttpKey.STATE);
						if("1".equals(state)){//上传成功
							mUploadSuccessCount++;
							mUploadSuccessPathList.add(json.getString(VpConstants.HttpKey.URL));
							//删除压缩后图片
							File f=new File(newPath);
							if(f.exists() && f.isFile())
								f.delete();
						}else{
							mUploadFailedCount++;
						}
					} catch (JSONException e1) {
						e1.printStackTrace();
						mUploadFailedCount++;
					};
				}else{
					mUploadFailedCount++;
				}				
				publishTopic(true);
				
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				mUploadFailedCount++;
				publishTopic(true);
			}
		});	
	}
    
    public void publishTopic(boolean hasPic){
    	if(hasPic){
			if((mUploadSuccessCount+mUploadFailedCount)==mSelectedPicList.size()){
				//图片上传完毕(成功或失败)
				if(mUploadSuccessCount>0){
					//只要有一张图片上传成功,去发表
					publishTopic();
				}else{
					//所有图片上传失败
					Toast.makeText(mContext, "图片上传失败,请重试!", Toast.LENGTH_SHORT).show();
					mClient.stopProgressDialog();
				}
			}
    	}else{
    		//发表文字
    		publishTopic();
    	}
    }
    
    public void publishTopic(){
    	String url=VpConstants.CHANNEL_FORUM_REPLY;
		JSONObject body=new JSONObject();
		try {
			body.put("uid", LoginStatus.getLoginInfo().getUid());
			body.put("id", this.mTopId);
			body.put("rid", this.mTopRid);
			body.put("cont", this.mCont);
			if(mUploadSuccessPathList!=null && mUploadSuccessPathList.size()>0){				
				JSONArray array=new JSONArray(mUploadSuccessPathList);
				body.put("pics",array);
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
				String result=ResultParseUtil.deAesResult(responseBody);
				JSONObject json = null;
				try {
					json = new JSONObject(result);
					String code = json.getString(VpConstants.HttpKey.CODE);
					if("0".equals(code)){//返回成功
						mCallBack.onSuccess();
					}else{
						String message = json.getString(VpConstants.HttpKey.MSG);
						mCallBack.onFailed(message);
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
				};
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				mCallBack.onFailed("网络访问错误");
				mClient.stopProgressDialog();
			}
		});
    }
    
	/**
     * 压缩图片
     */
    private String compressBitmap(String srcPath){
		//压缩图片
		String imgName = Util.getMd5(srcPath);
		String imgNewPath;
		try {
			imgNewPath = ExecutorDownLoadReactor.getRootFilePath() +"/" + imgName+".jpg";
			boolean b = WxUtil.transImage(srcPath, imgNewPath, WIDTH, HEIGHT, 80);	
			if (b) {
				return imgNewPath;
			}else{
				return srcPath;
			}
		} catch (Exception e) {
			Toast.makeText(mContext, "图片压缩失败", Toast.LENGTH_SHORT).show();
			return srcPath;
		}		
    }
    
    public interface SendTopCallback{
    	public void onSuccess();
    	public void onFailed(String msg);
    }
    
    
	
}
