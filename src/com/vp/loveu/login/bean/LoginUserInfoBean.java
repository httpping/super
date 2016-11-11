package com.vp.loveu.login.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.MainActivity;
import com.vp.loveu.base.VpApplication;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.login.ui.WelcomeActivity.SaveUserInfoCallBack;
import com.vp.loveu.util.AESUtil;
import com.vp.loveu.util.MD5Util;
import com.vp.loveu.util.SharedPreferencesHelper;
import com.vp.loveu.util.UIUtils;

import cz.msebera.android.httpclient.Header;

/**
 * @author：pzj
 * @date: 2015-10-29 上午9:37:33
 * @Description:登录成功用户bean
 */
public class LoginUserInfoBean {
	public static final String UID = "uid";
	public static final String LOGIN_UID = "login_uid";
	public static final String NICKNAME = "nickname";
	public static final String SEX = "sex";
	public static final String BIRTHDAY = "birthday";
	public static final String MT = "mt";
	public static final String PHOTOS = "photos";
	public static final String PORTRAIT = "portrait";
	public static final String DATING_STATUS = "dating_status";
	public static final String AREA_CODE = "area_code";
	public static final String XMPPUSER = "xmpp_user";
	public static final String XMPPPWD = "xmpp_pwd";
	public static final String ISFILLEINFO = "is_filled_info";
	public static final String LAT = "lat";
	public static final String LNG = "lng";
	public static final String LOGINTYPE="login_type";
	public static final String THIRDLOGIN="is_third_login";
	public static final String THIRDPORTRAIT="third_portrait";
	public static final String THIRDNICKNAME="third_nickname";
	public static final String THIRDOPENID="third_open_id";
	public static final String ADCODE="adcode";
	
	private int uid;
	private String nickname;
	private int sex;
	private String birthday;
	public String mt;
	private ArrayList<String> photos;
	private String portrait;
	private int dating_status;
	private String area_code;
	private String xmpp_user;
	private String adCode;
	private String xmpp_pwd;
	private String is_filled_info;
	private Context mContext;
	public float lat;
	public float lng;
	private String openId;
	//1=手机号登陆,2=新浪微博登陆，3=QQ登陆,4=微信登录
	private int loginType;
	public static final int LOGINTYPE_PHONE=1;
	public static final int LOGINTYPE_SINA=2;
	public static final int LOGINTYPE_QQ=3;
	public static final int LOGINTYPE_WECHAT=4;
	
	
	public static final String PORTRAIT_FILE_PATH = "portrait_file_path";
	private String mPortraitFilePath;//本地头像地址
	
	private String mSplit="=====";
	
	private SharedPreferencesHelper sharedPreferencesHelper;
	
	private SaveUserInfoCallBack mCallBack;
	
	public LoginUserInfoBean(Context context) {
		this.mContext=context;
		sharedPreferencesHelper = SharedPreferencesHelper.getInstance(context);
	}
	
	public static LoginUserInfoBean parseJson(String json) {
		Gson gson=new Gson();
		LoginUserInfoBean bean=gson.fromJson(json, LoginUserInfoBean.class);
		return bean;
	}
	
	public static List<LoginUserInfoBean> createFromJsonArray(String json) {
		List<LoginUserInfoBean> modeBens = new ArrayList<LoginUserInfoBean>();
		try {
			JSONArray jsonArray = new JSONArray(json);
			for (int i = 0; i < jsonArray.length(); i++) {
				LoginUserInfoBean bean = LoginUserInfoBean
						.parseJson(jsonArray.getString(i));
				if (bean != null) {
					modeBens.add(bean);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return modeBens;
	}
	
	public  void saveLoginUserInfo(final int loginType,final String openId,final String uid,final String xmppUser,final String xmppPwd,final String isFilledInfo,float lat,float lng,VpHttpClient mClient,SaveUserInfoCallBack callBack) {
		mCallBack=callBack;
		this.saveLoginUserInfo(loginType,openId,uid, xmppUser, xmppPwd, isFilledInfo,lat,lng, mClient);
	}
	
	//获取并保存登录用户信息
	public  void saveLoginUserInfo(final int loginType,final String openId,final String uid,final String xmppUser,final String xmppPwd,final String isFilledInfo,final float lat,final float lng,final VpHttpClient mClient) {
		if(uid!=null){
			String url=VpConstants.My_INFO;
			RequestParams params=new RequestParams();
			params.put("id", uid);
			mClient.setShowProgressDialog(false);
			mClient.get(url, params, new AsyncHttpResponseHandler() {
				
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					//返回成功
					String result=ResultParseUtil.deAesResult(responseBody);
					JSONObject json = null;
					try {
						json = new JSONObject(result);
						String code = json.getString(VpConstants.HttpKey.CODE);
						String message = json.getString(VpConstants.HttpKey.MSG);
						String data=json.getString(VpConstants.HttpKey.DATA);
						if("0".equals(code)){//获取基本信息成功
							LoginUserInfoBean infoBean = parseJson(data);
							infoBean.setUid(Integer.parseInt(uid));
							infoBean.setXmpp_user(xmppUser);
							infoBean.setXmpp_pwd(xmppPwd);
							infoBean.setIs_filled_info(isFilledInfo);
							infoBean.setLat(lat);
							infoBean.setLng(lng);
							infoBean.setLoginType(loginType);
							infoBean.setOpenId(openId);
							
							turnToHome(infoBean);
							if(mCallBack!=null){
								mCallBack.onSuccess();
							}
							
						}else{
							Toast.makeText(mContext, message,
									Toast.LENGTH_LONG).show();
							turnToHome(null);
							if(mCallBack!=null){
								mCallBack.onFailed();
							}
						}

					} catch (JSONException e1) {
						e1.printStackTrace();
						turnToHome(null);
						if(mCallBack!=null){
							mCallBack.onFailed();
						}
					};
					
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					turnToHome(null);
					if(mCallBack!=null){
						mCallBack.onFailed();
					}
				}
				
				
			});
		}
		
	}
	
	public void turnToHome(LoginUserInfoBean infoBean){
		LoginUserInfoBean bean=infoBean;
		if(bean==null){
			//从本地获取用户信息
			bean = getUserInfoFromLocal();
		}
		
		if(bean==null){
			//获取用户信息失败
			Toast.makeText(mContext, "获取用户信息失败", Toast.LENGTH_SHORT).show();
		}else{
			//保存到内存
			VpApplication.getInstance().setUser(bean);
			//保存到本地
			saveUserInfoTolocal(bean);
//			//登录跳转到首页
			Intent intent=new Intent(mContext, MainActivity.class);
			mContext.startActivity(intent);
		}
	}
	
	
	/**
	 * 保存用户信息到本地
	 */
	private void saveUserInfoTolocal(LoginUserInfoBean bean){
		if(bean==null)
			return;
		String uid=null;
		try {
			uid=AESUtil.Encrypt(bean.getUid()+"", VpHttpClient.KEY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		sharedPreferencesHelper.putStringValue(ADCODE, bean.adCode);
		sharedPreferencesHelper.putStringValue(LOGIN_UID, uid);
		sharedPreferencesHelper.putIntegerValue(SEX,bean.getSex());
		sharedPreferencesHelper.putIntegerValue(LOGINTYPE,bean.getLoginType());
		sharedPreferencesHelper.putIntegerValue(DATING_STATUS,bean.getDating_status());
		sharedPreferencesHelper.putStringValue(NICKNAME, bean.getNickname());
		sharedPreferencesHelper.putStringValue(BIRTHDAY,bean.getBirthday());
		sharedPreferencesHelper.putStringValue(MT,bean.getMt());
		if(bean.getPhotos()!=null && bean.getPhotos().size()>0){
			StringBuilder sb=new StringBuilder();
			for (int i=0;i<bean.getPhotos().size();i++) {
				sb.append(bean.getPhotos().get(i));
				if(i!=bean.getPhotos().size()-1)
					sb.append(this.mSplit);
			}
			sharedPreferencesHelper.putStringValue(PHOTOS,sb.toString());
		}
		sharedPreferencesHelper.putStringValue(PORTRAIT,bean.getPortrait());
		sharedPreferencesHelper.putStringValue(AREA_CODE,bean.getArea_code());
		sharedPreferencesHelper.putStringValue(XMPPUSER,bean.getXmpp_user());
		sharedPreferencesHelper.putStringValue(XMPPPWD,bean.getXmpp_pwd());
		sharedPreferencesHelper.putStringValue(ISFILLEINFO,bean.is_filled_info);
		sharedPreferencesHelper.putStringValue(LAT,bean.getLat()+"");
		sharedPreferencesHelper.putStringValue(LNG,bean.getLng()+"");
		sharedPreferencesHelper.putIntegerValue(LOGINTYPE,bean.getLoginType());
		sharedPreferencesHelper.putStringValue(THIRDOPENID, bean.getOpenId());
		//保存用户头像文件到本地
		mPortraitFilePath=sharedPreferencesHelper.getStringValue(PORTRAIT_FILE_PATH);
		String destFile=getDestFile(bean.getPortrait());
		if(!destFile.equals(mPortraitFilePath)){			
			downLoadPortrait(bean.getPortrait(),destFile);
		}
		
	}
	
	private String getDestFile(String srcpath){
		String path = new File(UIUtils.getContext().getFilesDir(),MD5Util.MD516(srcpath)+".png").getAbsolutePath();
		return path;
	}
	
	private void downLoadPortrait(String srcUrl,final String destUrl) {
		VpHttpClient client=new VpHttpClient(mContext);
		client.setShowProgressDialog(false);
		client.get(srcUrl, new RequestParams(), new BinaryHttpResponseHandler() {
			
			
			@Override
			public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] binaryData) {
		        Bitmap bmp = BitmapFactory.decodeByteArray(binaryData, 0,  
		                binaryData.length);  
		  
		        File file = new File(destUrl);  
		        // 压缩格式  
		        CompressFormat format = Bitmap.CompressFormat.JPEG;  
		        // 压缩比例  
		        int quality = 100;  
		        try {  
		            // 若存在则删除  
		            if (file.exists())  
		                file.delete();  
		            // 创建文件  
		            file.createNewFile();  
		            //  
		            OutputStream stream = new FileOutputStream(file);  
		            // 压缩输出  
		            bmp.compress(format, quality, stream);  
		            // 关闭  
		            stream.close();  
		  
		            sharedPreferencesHelper.putStringValue(PORTRAIT_FILE_PATH, destUrl);
		        } catch (IOException e) {  
		            e.printStackTrace();  
		        }  
				
			}
			
			@Override
			public void onProgress(long bytesWritten, long totalSize) {
				super.onProgress(bytesWritten, totalSize);
			}
			
			@Override
			public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] binaryData,
					Throwable error) {
				
			}
		});
		
	}

	/**
	 * 从本地获取用户信息
	 * @return
	 */
	public LoginUserInfoBean getUserInfoFromLocal(){
		LoginUserInfoBean bean=null;
		String uid = sharedPreferencesHelper.getStringValue(LOGIN_UID);
		String xmppUser=sharedPreferencesHelper.getStringValue(XMPPUSER);
		String xmppPwd=sharedPreferencesHelper.getStringValue(XMPPPWD);
		if(uid!=null && !"".equals(uid)){
			try {
				uid=AESUtil.Decrypt(uid+"", VpHttpClient.KEY);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(uid!=null && !TextUtils.isEmpty(xmppUser) && !TextUtils.isEmpty(xmppPwd)){
			bean=new LoginUserInfoBean(mContext);
			bean.setUid(Integer.parseInt(uid));
			bean.setSex(sharedPreferencesHelper.getIntegerValue(SEX));
			bean.setDating_status(sharedPreferencesHelper.getIntegerValue(DATING_STATUS));
			bean.setNickname(sharedPreferencesHelper.getStringValue(NICKNAME));
			bean.setBirthday(sharedPreferencesHelper.getStringValue(BIRTHDAY));
			bean.setMt(sharedPreferencesHelper.getStringValue(MT));
			bean.setPortrait(sharedPreferencesHelper.getStringValue(PORTRAIT));
			bean.setArea_code(sharedPreferencesHelper.getStringValue(AREA_CODE));
			bean.setXmpp_user(xmppUser);
			bean.setXmpp_pwd(xmppPwd);
			bean.setAdCode(sharedPreferencesHelper.getStringValue(ADCODE));
			bean.setIs_filled_info(sharedPreferencesHelper.getStringValue(ISFILLEINFO));
			bean.setLat(Float.parseFloat(sharedPreferencesHelper.getStringValue(LAT)));
			bean.setLng(Float.parseFloat(sharedPreferencesHelper.getStringValue(LNG)));
			bean.setLoginType(sharedPreferencesHelper.getIntegerValue(LOGINTYPE));
			bean.setOpenId(sharedPreferencesHelper.getStringValue(THIRDOPENID));
			bean.setOpenId(sharedPreferencesHelper.getStringValue(THIRDOPENID));
			String photos = sharedPreferencesHelper.getStringValue(PHOTOS);
			if(photos!=null){
				ArrayList<String> photoList=new ArrayList<String>();
				String[] photosArr = photos.split(mSplit);
				for(int i=0;i<photosArr.length;i++){
					photoList.add(photosArr[i]);
				}
				bean.setPhotos(photoList);
			}
		}
		return bean;
	}
	
	/**
	 * 清除登录用户信息
	 */
	public void cleanAllUserInfo(){
//		sharedPreferencesHelper.removeValue(LOGIN_UID);
//		sharedPreferencesHelper.removeValue(SEX);
//		sharedPreferencesHelper.removeValue(DATING_STATUS);
//		sharedPreferencesHelper.removeValue(NICKNAME);
//		sharedPreferencesHelper.removeValue(BIRTHDAY);
//		sharedPreferencesHelper.removeValue(MT);
//		sharedPreferencesHelper.removeValue(PORTRAIT);
//		sharedPreferencesHelper.removeValue(AREA_CODE);
//		sharedPreferencesHelper.removeValue(XMPPUSER);
//		sharedPreferencesHelper.removeValue(XMPPPWD);
//		sharedPreferencesHelper.removeValue(ISFILLEINFO);
//		sharedPreferencesHelper.removeValue(LAT);
//		sharedPreferencesHelper.removeValue(LNG);
//		sharedPreferencesHelper.removeValue(PHOTOS);
//		sharedPreferencesHelper.removeValue(THIRDOPENID);
		sharedPreferencesHelper.removeAllValue();
		VpApplication.getInstance().setUser(null);
	}
	
	/**
	 * 保存用户头像
	 * @return
	 */
	
	public void saveUserPortrait(String path,String localFilePath){
		if(!TextUtils.isEmpty(path) && !TextUtils.isEmpty(localFilePath)){
			VpApplication.getInstance().getUser().setPortrait(path);
			VpApplication.getInstance().getUser().setmPortraitFilePath(localFilePath);
			sharedPreferencesHelper.putStringValue(PORTRAIT,path);
			String destFile=getDestFile(localFilePath);
			sharedPreferencesHelper.putStringValue(PORTRAIT_FILE_PATH,destFile);
		}
	}
	/**
	 * 保存用户昵称
	 * @return
	 */
	
	public void saveUserNickName(String nickName){
		if(nickName!=null){
			VpApplication.getInstance().getUser().setNickname(nickName);
			sharedPreferencesHelper.putStringValue(NICKNAME,nickName);
		}
	}
	
	
	public  boolean isLogin(){
		LoginUserInfoBean bean = getLoginInfo();
		return bean!=null;
	}
	
	public  LoginUserInfoBean getLoginInfo(){
		LoginUserInfoBean bean;
		bean= VpApplication.getInstance().getUser();
		if(bean==null)
			bean=getUserInfoFromLocal();
		return bean;
			
	}
	
	public void setAdCode(String adCode){
		this.adCode = adCode;
	}
	public String getAdCode(){
		return adCode;
	}
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public int getDating_status() {
		return dating_status;
	}
	public void setDating_status(int dating_status) {
		this.dating_status = dating_status;
	}

	public String getArea_code() {
		return area_code;
	}
	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}


	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getXmpp_user() {
		return xmpp_user;
	}

	public void setXmpp_user(String xmpp_user) {
		this.xmpp_user = xmpp_user;
	}

	public String getXmpp_pwd() {
		return xmpp_pwd;
	}

	public void setXmpp_pwd(String xmpp_pwd) {
		this.xmpp_pwd = xmpp_pwd;
	}
	
	public String getIs_filled_info() {
		return is_filled_info;
	}

	public void setIs_filled_info(String is_filled_info) {
		this.is_filled_info = is_filled_info;
	}

	public String getPortrait() {
		return portrait;
	}

	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}

	public ArrayList<String> getPhotos() {
		return photos;
	}

	public void setPhotos(ArrayList<String> photos) {
		this.photos = photos;
	}

	public float getLat() {
		return lat;
	}

	public void setLat(float lat) {
		this.lat = lat;
	}

	public float getLng() {
		return lng;
	}

	public void setLng(float lng) {
		this.lng = lng;
	}

	public int getLoginType() {
		return loginType;
	}

	public void setLoginType(int loginType) {
		this.loginType = loginType;
	}

	public String getMt() {
		return mt;
	}

	public void setMt(String mt) {
		this.mt = mt;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getmPortraitFilePath() {
		return mPortraitFilePath;
	}

	public void setmPortraitFilePath(String mPortraitFilePath) {
		this.mPortraitFilePath = mPortraitFilePath;
	}
	
	
}
