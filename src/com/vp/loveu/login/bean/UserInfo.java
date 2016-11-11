/**   
* @Title: UserInfo.java 
* @Package com.vp.loveu.my.bean 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zeus   
* @date 2015-10-27 上午10:40:47 
* @version V1.0   
*/
package com.vp.loveu.login.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.comm.UserInfoConstants;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.util.SharedPreferencesHelper;
import com.vp.loveu.util.UIUtils;

import android.util.Log;

/**

 *
 * @ClassName: 用户信息
 * @Description:
 * @author ping
 * @date 
 */
public class UserInfo {

	public int regType;
	public int loginType;
	public String mt;
//	public String psw;
	public String openWebUid;
	public String nickname;
	public String portrait;
	public int sex;
	public String areaCode;
	public String birthday;
	public int height;
	public int edu;
	public int income;
	public int maritalStatus;
	public String authCode;
	public String apnsToken;
	public String deviceId;
	public String lastAreaCode;
	public float lat;
	public float lng;
	public static final String REG_TYPE = "reg_type";
	public static final String MT = "mt";
//	public static final String PSW = "psw";
	public static final String OPEN_WEB_UID = "open_web_uid";
	public static final String NICKNAME = "nickname";
	public static final String PORTRAIT = "portrait";
	public static final String SEX = "sex";
	public static final String AREA_CODE = "area_code";
	public static final String BIRTHDAY = "birthday";
	public static final String HEIGHT = "height";
	public static final String EDU = "edu";
	public static final String INCOME = "income";
	public static final String MARITAL_STATUS = "marital_status";
	public static final String AUTH_CODE = "auth_code";
	public static final String APNS_TOKEN = "apns_token";
	public static final String DEVICE_ID = "device_id";
	public static final String LAST_AREA_CODE = "last_area_code";
	public static final String LAT = "lat";
	public static final String LNG = "lng";
	private SharedPreferencesHelper sharedPreferencesHelper;
	
	public UserInfo() {
		sharedPreferencesHelper = SharedPreferencesHelper.getInstance(UIUtils.getContext());
	}

	public static UserInfo parseJson(String json) {
		UserInfo objectBean = new UserInfo();
		try {
			JSONObject jsonObject = new JSONObject(json);
			if (jsonObject.has(REG_TYPE)) {
				objectBean.regType = jsonObject.getInt(REG_TYPE);
			}
			if (jsonObject.has(MT)) {
				objectBean.mt = jsonObject.getString(MT);
			}
//			if (jsonObject.has(PSW)) {
//				objectBean.psw = jsonObject.getString(PSW);
//			}
			if (jsonObject.has(OPEN_WEB_UID)) {
				objectBean.openWebUid = jsonObject.getString(OPEN_WEB_UID);
			}
			if (jsonObject.has(NICKNAME)) {
				objectBean.nickname = jsonObject.getString(NICKNAME);
			}
			if (jsonObject.has(PORTRAIT)) {
				objectBean.portrait = jsonObject.getString(PORTRAIT);
			}
			if (jsonObject.has(SEX)) {
				objectBean.sex = jsonObject.getInt(SEX);
			}
			if (jsonObject.has(AREA_CODE)) {
				objectBean.areaCode = jsonObject.getString(AREA_CODE);
			}
			if (jsonObject.has(BIRTHDAY)) {
				objectBean.birthday = jsonObject.getString(BIRTHDAY);
			}
			if (jsonObject.has(HEIGHT)) {
				objectBean.height = jsonObject.getInt(HEIGHT);
			}
			if (jsonObject.has(EDU)) {
				objectBean.edu = jsonObject.getInt(EDU);
			}
			if (jsonObject.has(INCOME)) {
				objectBean.income = jsonObject.getInt(INCOME);
			}
			if (jsonObject.has(MARITAL_STATUS)) {
				objectBean.maritalStatus = jsonObject.getInt(MARITAL_STATUS);
			}
			if (jsonObject.has(AUTH_CODE)) {
				objectBean.authCode = jsonObject.getString(AUTH_CODE);
			}
			if (jsonObject.has(APNS_TOKEN)) {
				objectBean.apnsToken = jsonObject.getString(APNS_TOKEN);
			}
			if (jsonObject.has(DEVICE_ID)) {
				objectBean.deviceId = jsonObject.getString(DEVICE_ID);
			}
			if (jsonObject.has(LAST_AREA_CODE)) {
				objectBean.lastAreaCode = jsonObject.getString(LAST_AREA_CODE);
			}
			if (jsonObject.has(LAT)) {
				objectBean.lat = jsonObject.getInt(LAT);
			} 
			
			
			
			if (jsonObject.has(LNG)) {
				objectBean.lng = jsonObject.getInt(LNG);
			}
		} catch (Exception e) {
			return null;
		}
		return objectBean;
	}

	public static List<UserInfo> createFromJsonArray(String json) {
		List<UserInfo> modeBens = new ArrayList<UserInfo>();
		try {
			JSONArray jsonArray = new JSONArray(json);
			for (int i = 0; i < jsonArray.length(); i++) {
				UserInfo bean = UserInfo.parseJson(jsonArray.getString(i));
				if (bean != null) {
					modeBens.add(bean);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return modeBens;
	}
	
	
	
	/**
	 * 用户注册接口
	 * @param resonese
	 */
//	public void register(AsyncHttpResponseHandler responseHandler,VpHttpClient client){
//		setUserInfo();		
//		String url = VpConstants.USER_REGISTER;		
//		JSONObject data= new JSONObject();
//		try {
//			data.put("reg_type", this.regType);
//			data.put("mt", this.mt);
////			data.put("psw", this.psw);
////			data.put("open_web_uid", this.openWebUid);
//			data.put("nickname", this.nickname);			
//			data.put("portrait", this.portrait);
//			data.put("sex", this.sex);
//			data.put("area_code", this.areaCode);
//			data.put("birthday", this.birthday);		
//			data.put("height", this.height);
////			
//			data.put("edu", this.edu);
//			data.put("income", this.income);
//			data.put("marital_status", this.maritalStatus);
//			data.put("height", this.height);
//			
//			data.put("edu", this.edu);
//			data.put("income",this.income);
//			data.put("marital_status", this.maritalStatus);		
//			data.put("auth_code", this.authCode);			
//			data.put("apns_token", "");			
//			data.put("device_id", "");
//			data.put("last_area_code","");
//			data.put("lat", this.lat);
//			data.put("lng", this.lng);
//			client.post(url, new RequestParams(), data.toString(), true, responseHandler);
//		} catch (JSONException e) {
//			e.printStackTrace();
//			if (responseHandler!=null) {
//    			responseHandler.sendFailureMessage(-1, null, "数据错误".getBytes(), e);
//			}
//		}
//	}
	
	/**
	 * 用户登录接口
	 * @param resonese
	 */
	public void login(AsyncHttpResponseHandler responseHandler,VpHttpClient client){
		String url = VpConstants.USER_LOGIN;
		
		JSONObject data= new JSONObject();
		try {
			data.put("login_type", this.loginType);
			data.put("mt", this.mt);
			data.put("auth_code", this.authCode);
			data.put("open_web_uid", this.openWebUid);
			data.put("apns_token", "");
			data.put("device_id", "");
			data.put("last_area_code",this.areaCode);
			data.put("lat", this.lat);
			data.put("lng", this.lng);
			
			Log.d("tag:result","params "+data.toString());
			
			client.post(url, new RequestParams(), data.toString(), true, responseHandler);
		} catch (JSONException e) {
			e.printStackTrace();
			if (responseHandler!=null) {
    			responseHandler.sendFailureMessage(-1, null, "数据错误".getBytes(), e);
			}
		}
	}
	
	
	//上传头像
	public void uploadPortrait(AsyncHttpResponseHandler responseHandler,VpHttpClient client){
		String locPortrait=sharedPreferencesHelper.getStringValue(UserInfoConstants.PORTRAIT);	
		if(locPortrait==null || "".equals(locPortrait)){
			responseHandler.onCancel();
		}else{
			client.postFile(VpConstants.FILE_UPLOAD, VpConstants.FILE_UPLOAD_PATH_PORTRAIT, locPortrait, true, true, true, responseHandler);
		}
	}

//	private void setUserInfo() {
//		this.regType=sharedPreferencesHelper.getIntegerValue(UserInfoConstants.REG_TYPE);
//		this.mt=sharedPreferencesHelper.getStringValue(UserInfoConstants.MT);
//		this.authCode=sharedPreferencesHelper.getStringValue(UserInfoConstants.AUTH_CODE);
////		this.psw=sharedPreferencesHelper.getStringValue(UserInfoConstants.PWD);
////		this.portrait=sharedPreferencesHelper.getStringValue(UserInfoConstants.PORTRAIT);		
//		this.nickname=sharedPreferencesHelper.getStringValue(UserInfoConstants.NICKNAME);
//		this.birthday=sharedPreferencesHelper.getStringValue(UserInfoConstants.BIRTHDAY);
//		this.sex=sharedPreferencesHelper.getIntegerValue(UserInfoConstants.SEX);
//		this.areaCode=sharedPreferencesHelper.getStringValue(UserInfoConstants.AREA_CODE);
//		//身高上传时去掉单位
//		this.height=Integer.parseInt(sharedPreferencesHelper.getStringValue(UserInfoConstants.HEIGHT).replace("cm", ""));
//		
//		this.edu=getEduCode(sharedPreferencesHelper.getStringValue(UserInfoConstants.EDU));
//		this.income=getIncomeCode(sharedPreferencesHelper.getStringValue(UserInfoConstants.INCOME));
//		this.maritalStatus=getMaryCode(sharedPreferencesHelper.getStringValue(UserInfoConstants.MARITAL_STATUS));
//		
//	}
	
//	private int getEduCode(String eduDesc){
////		0=其他，1=小学，2=初中，3=高中，4=中专，5=大专，6=本科，7=硕士，8=博士
//		String[] edus = UIUtils.getContext().getResources().getStringArray(R.array.Edu);
//		for(int i=0;i<edus.length;i++){
//			if(edus!=null && edus[i].equals(eduDesc)){
//				String code= UIUtils.getContext().getResources().getStringArray(R.array.Edu_index)[i];
//				return Integer.parseInt(code);
//			}
//		}
//		return 0;
//	}
//	private int getIncomeCode(String eduDesc){
////		收入，10=3000以下，11=3000-5000，12=5000-8000，13=8000-15000，14=15000-25000，15=25000以上
//		String[] salarys = UIUtils.getContext().getResources().getStringArray(R.array.Salary);
//		for(int i=0;i<salarys.length;i++){
//			if(salarys!=null && salarys[i].equals(eduDesc)){
//				String code= UIUtils.getContext().getResources().getStringArray(R.array.Salary_index)[i];
//				return Integer.parseInt(code);
//			}
//		}
//		return 0;
//	}
//	
//	private int getMaryCode(String eduDesc){
////		婚姻状况，0=未婚，1=离异，2=丧偶
//		String[] marrys = UIUtils.getContext().getResources().getStringArray(R.array.Married);
//		for(int i=0;i<marrys.length;i++){
//			if(marrys!=null && marrys[i].equals(eduDesc)){
//				String code= UIUtils.getContext().getResources().getStringArray(R.array.Married_index)[i];
//				return Integer.parseInt(code);
//			}
//		}
//		return 0;
//	}

}
