package com.vp.loveu.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import cn.sharesdk.onekeyshare.SignupNopage;

import com.vp.loveu.base.VpApplication;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.login.ui.LoginActivity;

public class HttpUtils {
	private static HashMap<String, Object> cookicsHashMap = new HashMap<String, Object>();

	/**
	 * 
	 * @Title: isWIFIAvailable
	 * @Description: 检测wifi是否可用
	 * @param @param inContext
	 * @param @return 设定文件
	 * @return boolean 返回类型
	 * @throws
	 */
	public static boolean isWIFIAvailable(Context inContext) {
		try {
			ConnectivityManager cm = (ConnectivityManager) inContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			NetworkInfo info = cm.getActiveNetworkInfo();
			return wifi.isAvailable();
		} catch (Exception e) {
			return false;
		}

	}

	/**
	 * 
	 * @Title: isWiFiActive
	 * @Description: 检测网络是否连接
	 * @param inContext
	 *            上下文对象
	 * @return boolean
	 * @throws
	 * @author jie
	 * @date 2013-5-14 上午9:00:57
	 * @History: author time version desc
	 */
	public static boolean isWiFiActive(Context inContext) {
		try {
			ConnectivityManager cm = (ConnectivityManager) inContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			return (info != null && info.isConnected());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 
	 * @Title: userLogin
	 * @Description:
	 * @param @param context
	 * @param @param username
	 * @param @param password
	 * @param @return
	 * @param @throws Exception 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public static String post(Context context, Map<String, String> map, int type)
			throws Exception {
		if (isWiFiActive(context)) {
//			map.put("CustID", sharedPreferencesHelper.getStringValue(VpConstants.HttpKey.CusID));
//			map.put("CookCode", sharedPreferencesHelper.getStringValue(VpConstants.HttpKey.CookCode));
			HttpEntity httpEntity = doPost(VpConstants.Action_type + type, map);
			if (null != httpEntity) {
				String s = EntityUtils.toString(httpEntity);
				Prints.i("lisy", "获得" + type + "的数据:" + s);
				JSONObject jsonObject = new JSONObject(s);
				if ("0".equals(jsonObject.getString(VpConstants.HttpKey.status))) {
					if (Boolean.valueOf(jsonObject
							.getString(VpConstants.HttpKey.login))) {
						return jsonObject
								.getJSONObject(VpConstants.HttpKey.result)
								.toString();
					} else {
						Intent intent = new Intent(
								"com.zngoo.pczylove.service.OpenfireService");
						intent.putExtra(VpConstants.IntentKey.type, 8);
						context.startService(intent);
						return "";
					}

				} else {
					throw new GSException(
							jsonObject.getString(VpConstants.HttpKey.MSG));
				}
			} else {
				throw new GSException(VpConstants.ERROR_SERVER);
			}
		} else {
			throw new GSException(VpConstants.ERROR_INT);
		}
	}

	public static String post(Context context, Map<String, String> map,
			String type) throws Exception {
		if (isWiFiActive(context)) {
			HttpEntity httpEntity = doPost(VpConstants.Action_type + type, map);
			if (null != httpEntity) {
				String s = EntityUtils.toString(httpEntity);
				Prints.i("lisy", "获得" + type + "的数据:" + s);
				JSONObject jsonObject = new JSONObject(s);
				if(jsonObject.getInt(VpConstants.HttpKey.ResultCode)==1009){
					SignupNopage.clearPlatfor();
					Intent intent = new Intent(context,LoginActivity.class);
					intent.putExtra("reStart", true);
					context.startActivity(intent);
					VpApplication.getInstance().exit();
				}
				return s;
			} else {
				throw new GSException(VpConstants.ERROR_SERVER);
			}
		} else {
			throw new GSException(VpConstants.ERROR_INT);
		}
	}

	/** 上传图片 **/
	public static String updateImage(Context context, String path, int type,
			String cuid, String vtype) throws Exception {
		if (isWiFiActive(context)) {

			String s = uploadImage(VpConstants.Action_type + type, path, cuid,
					vtype);
			if (null != s) {
				Prints.i("lisy", "获得" + type + "的数据:" + s);
				JSONObject jsonObject = new JSONObject(s);
				if ("0".equals(jsonObject.getString(VpConstants.HttpKey.status))) {
					return jsonObject.getJSONObject(VpConstants.HttpKey.returns)
							.toString();
				} else {
					throw new GSException(
							jsonObject.getString(VpConstants.HttpKey.MSG));
				}
			} else {
				throw new GSException(VpConstants.ERROR_SERVER);
			}
		} else {
			throw new GSException(VpConstants.ERROR_INT);
		}
	}

	private static String uploadImage(String actionUrl, String path,
			String cuid, String vtype) {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "WebKitFormBoundaryiLJZB0O6hiAeO5pH";
		try {
			URL url = new URL(actionUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestMethod("POST");
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data;name=\"vtype\"" + end
					+ end);
			ds.writeBytes(vtype + "" + end);
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data;name=\"cuid\"" + end
					+ end);
			ds.writeBytes(cuid + "" + end);
			ds.writeBytes(twoHyphens + boundary + end);

			ds.writeBytes("Content-Disposition: form-data;name=\"CustID\""
					+ end + end);
//			ds.writeBytes(sharedPreferencesHelper.getStringValue(VpConstants.HttpKey.CusID) + "" + end);
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data;name=\"CookCode\""
					+ end + end);
//			ds.writeBytes(sharedPreferencesHelper.getStringValue(VpConstants.HttpKey.CookCode) + "" + end);
			ds.writeBytes(twoHyphens + boundary + end);
			File file = new File(path);
			ds.writeBytes("Content-Disposition: form-data; " + "name=\"file"
					+ 1 + "\";filename=\"" + file.getName() + "\"" + end);
			ds.writeBytes(end);
			FileInputStream fStream = new FileInputStream(file);
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			int length = -1;
			while ((length = fStream.read(buffer)) != -1) {
				ds.write(buffer, 0, length);
			}
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			fStream.close();
			ds.flush();
			ds.close();
			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			return b.toString().trim();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static HttpEntity doPost1(String action, Map<String, String> values,
			String cusid, String cookcode) throws Exception {

		action = action + "CookCode=" + cookcode + "&CusID=" + cusid;

		HttpPost httpPost = new HttpPost(action);
		HttpParams params = new BasicHttpParams();

		HttpConnectionParams.setConnectionTimeout(params, 6 * 1000);

		HttpConnectionParams.setSoTimeout(params, 6 * 1000);

		HttpConnectionParams.setSocketBufferSize(params, 8192);

		// 设置重定向，缺省为 true

		HttpClientParams.setRedirecting(params, true);

		DefaultHttpClient httpClient = new DefaultHttpClient(params);

		for (Map.Entry<String, String> tiem : values.entrySet()) {
			params.setParameter(tiem.getKey(), tiem.getValue());
		}

		httpPost.setParams(params);

		HttpResponse httpResponse = httpClient.execute(httpPost);
		if (httpResponse != null
				&& httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			return httpResponse.getEntity();
		}
		return null;
	}

	/**
	 * 发送Post请求
	 * 
	 * @param url
	 *            Action的URL
	 * @param values
	 *            传递参数键值对
	 * @return
	 */
	private static HttpEntity doPost(String action, Map<String, String> values)
			throws Exception {
		if (values != null) {
			String st = "";
			for (String key : values.keySet()) {
				if (key.equals("NickName")) {
					String s_nickname;
					s_nickname = URLEncoder.encode(values.get(key), "utf-8");
					st += key + "=" + s_nickname + "&";
				} else {
					st += key + "=" + values.get(key) + "&";
				}
			}
			st = st.substring(0, st.length() - 1);
			action += st;
		}
		HttpPost httpPost = new HttpPost(action);
		HttpParams params = new BasicHttpParams();

		HttpConnectionParams.setConnectionTimeout(params, 6 * 1000);

		HttpConnectionParams.setSoTimeout(params, 6 * 1000);

		HttpConnectionParams.setSocketBufferSize(params, 8192);

		// 设置重定向，缺省为 true

		HttpClientParams.setRedirecting(params, true);

		DefaultHttpClient httpClient = new DefaultHttpClient(params);
		UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(
				parameterPairs(values), "UTF-8");

		httpPost.setEntity(p_entity);

		HttpResponse httpResponse = httpClient.execute(httpPost);
		if (httpResponse != null
				&& httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			return httpResponse.getEntity();
		}
		return null;
	}

	/**
	 * 转换传递参数键值对
	 * 
	 * @param values
	 *            传递参数键值对
	 * @return
	 */
	public static ArrayList<BasicNameValuePair> parameterPairs(
			Map<String, String> values) {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		if (values != null) {
			Set<String> keys = values.keySet();
			for (Iterator<String> i = keys.iterator(); i.hasNext();) {
				String key = i.next();
				Prints.i("lisy", "key=" + key + "   value=" + values.get(key));
				pairs.add(new BasicNameValuePair(key, values.get(key)));
			}
		}
		return pairs;
	}

	/**
	 * 拼接URL
	 * 
	 * @param url
	 *            Action的URL
	 * @param values
	 *            传递参数键值对
	 * @return
	 */
	public static String spliceUrl(String action, Map<String, String> values) {
		String url = action;
		if (values != null) {
			Set<String> keys = values.keySet();
			for (Iterator<String> i = keys.iterator(); i.hasNext();) {
				String key = i.next();
				url += ("/" + key + "/" + values.get(key));
			}
		}
		if (!TextUtils.equals(url, action)) {
			url += "/";
		}
		return url;
	}

	public static String post(Context context, Map<String, String> map,
			String cusid, String cookcode, String type) throws Exception {
		if (isWiFiActive(context)) {
			HttpEntity httpEntity = doPost(VpConstants.Action_type + type, map,
					cusid, cookcode);
			if (null != httpEntity) {
				String s = EntityUtils.toString(httpEntity);
				Prints.i("lisy", "获得" + type + "的数据:" + s);
				JSONObject jsonObject = new JSONObject(s);
				// }
				return s;
			} else {
				throw new GSException(VpConstants.ERROR_SERVER);
			}
		} else {
			throw new GSException(VpConstants.ERROR_INT);
		}
	}

	private static HttpEntity doPost(String action, Map<String, String> values,
			String cusid, String cookcode) throws Exception {
		action = action + "CookCode=" + cookcode + "&CusID=" + cusid;
		HttpPost httpPost = new HttpPost(action);
		HttpParams params = new BasicHttpParams();

		HttpConnectionParams.setConnectionTimeout(params, 6 * 1000);

		HttpConnectionParams.setSoTimeout(params, 6 * 1000);

		HttpConnectionParams.setSocketBufferSize(params, 8192);

		// 设置重定向，缺省为 true

		HttpClientParams.setRedirecting(params, true);

		DefaultHttpClient httpClient = new DefaultHttpClient(params);
		UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(
				parameterPairs(values), "UTF-8");

		httpPost.setEntity(p_entity);

		HttpResponse httpResponse = httpClient.execute(httpPost);
		if (httpResponse != null
				&& httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			return httpResponse.getEntity();
		}
		return null;

	}

	public static String dopost(String string, String cookCode, String cusID,
			String personalBasic) throws Exception, IOException {
		string += "?CusID=" + cusID;
		HttpPost httpPost = new HttpPost(string);
		httpPost.setHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");

		DefaultHttpClient httpClient = new DefaultHttpClient();
		NameValuePair nameValuePair = new BasicNameValuePair("model",
				personalBasic);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(nameValuePair);
		UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(
				nameValuePairs, "UTF-8");
		httpPost.setEntity(p_entity);
		HttpEntity httpEntity = null;

		HttpResponse httpResponse = httpClient.execute(httpPost);
		if (httpResponse != null
				&& httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			httpEntity = httpResponse.getEntity();
		}
		if (null != httpEntity) {
			String s = EntityUtils.toString(httpEntity);
			Prints.i("lisy", "获得" + string + "的数据:" + s);
			JSONObject jsonObject = new JSONObject(s);
			return s;
		} else {
			throw new GSException(VpConstants.ERROR_SERVER);
		}

	}

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String sendGet(String url, String param) {
		String result = "";
		BufferedReader in = null;
		HttpEntity httpEntity = null;
		try {
			String urlNameString = url + param;
			HttpGet httpGet = new HttpGet(urlNameString);
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient.execute(httpGet);

			if (httpResponse != null
					&& httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				httpEntity = httpResponse.getEntity();
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		if (null != httpEntity) {
			String s = null;
			try {
				s = EntityUtils.toString(httpEntity);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Prints.i("lisy", "获得" + url + "的数据:" + s);
			try {
				JSONObject jsonObject = new JSONObject(s);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return s;
		} else {
			try {
				throw new GSException(VpConstants.ERROR_SERVER);
			} catch (GSException e) {
				e.printStackTrace();
			}
		}
		return null;

	}

	public static String get(Context context, Map<String, String> map,
			String getattentionme) {
		String action = VpConstants.Action_type + getattentionme;
		String st = "";
		for (String key : map.keySet()) {
			st += key + "=" + map.get(key) + "&";
		}
		st = st.substring(0, st.length() - 1);
		action += st;
		HttpEntity httpEntity = null;
		try {

			HttpGet httpGet = new HttpGet(action);
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient.execute(httpGet);
			if (httpResponse != null
					&& httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				httpEntity = httpResponse.getEntity();
			}
			if (httpEntity != null) {
				String s = EntityUtils.toString(httpEntity);
				Prints.i("lisy", "获得" + action + "的数据:" + s);
				return s;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/** 传参于body里 */
	public static String dopost(String string, Map<String, String> map,
			Map<String, String> map2) throws Exception {
		if (map != null) {
			String st = "";
			for (String key : map.keySet()) {
				st += key + "=" + map.get(key) + "&";
			}
			st = st.substring(0, st.length() - 1);
			string += st;
		}
		HttpPost httpPost = new HttpPost(string);
		HttpParams params = new BasicHttpParams();
		httpPost.setHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");

		HttpConnectionParams.setConnectionTimeout(params, 6 * 1000);

		HttpConnectionParams.setSoTimeout(params, 6 * 1000);

		HttpConnectionParams.setSocketBufferSize(params, 8192);

		// 设置重定向，缺省为 true

		HttpClientParams.setRedirecting(params, true);

		DefaultHttpClient httpClient = new DefaultHttpClient(params);
		UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(
				parameterPairs(map2), "UTF-8");

		httpPost.setEntity(p_entity);

		HttpResponse httpResponse = httpClient.execute(httpPost);

		HttpEntity httpEntity = null;

		if (httpResponse != null
				&& httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			httpEntity = httpResponse.getEntity();
		}
		String s;
		if (null != httpEntity) {
			s = EntityUtils.toString(httpEntity);
			Prints.i("lisy", "获得" + string + "的数据:" + s);
			return s;
		} else {
			return null;
			// throw new GSException(VpConstants.ERROR_SERVER);
		}

	}
	
	
	
}
