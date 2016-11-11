/**   
* @Title: DEAESUtil.java 
* @Package com.loopj.android.http 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zeus   
* @date 2015-10-22 下午2:53:23 
* @version V1.0   
*/
package com.loopj.android.http;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import android.text.TextUtils;

import com.vp.loveu.message.utils.UTCDateUtils;
import com.vp.loveu.util.AESUtil;
import com.vp.loveu.util.SharedPreferencesHelper;
import com.vp.loveu.util.VPLog;

import cz.msebera.android.httpclient.Header;

/**

 *
 * @ClassName:
 * @Description: 接口结果解析
 * @author ping
 * @date 
 */
public class ResultParseUtil {
	
	public static String deAesResult(byte[] responseBody){
		
		if (responseBody == null){
			return null;
		}
		
		JSONObject data = null;
		
		try {
			data = new JSONObject(new String(responseBody,"utf-8"));
			
			int encry = data.optInt("is_encrypt");
			if(encry == 1){//decode
				String result = AESUtil.Decrypt(data.getString("data"), VpHttpClient.KEY);
				data.put("data", result);
			}
			
			int code = data.optInt("code");
			if (code!=0) {
			}
			VPLog.d("result", data.toString());
			return data.toString();
		}catch (Exception e) {
			e.printStackTrace();
			return null ;
		}
	}
	
	
	public  static Date requestDate;
	public static long timeinterval =0;//客户端与服务器的时间间隔
	/**
	 * 解析header
	 * @param header
	 */
	public static void parseHeader(Header[] header){
		if (header == null) {
			return;
		}
		String dateStr = null;
		for (int i = 0; i < header.length; i++) {
			if ("date".equals(header[i].getName().toLowerCase())) {
				dateStr = header[i].getValue();
			}
		}
		VPLog.d("head","utc: " + dateStr+"");

		if (!TextUtils.isEmpty(dateStr)) {
	         DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;  
	         Date date = UTCDateUtils.parseDate(dateStr);
			 VPLog.d("head",format.format(date) +"");
			 requestDate = date;
			 timeinterval =  date.getTime() -System.currentTimeMillis();
			 VPLog.d("head", "cha :" +timeinterval);
		}
	
	}
	
	public static Date getServerDate(){
		if(requestDate == null){
			return new Date();
		}
		return requestDate;
	}
	 
}
