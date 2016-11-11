package com.vp.loveu.discover.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.vp.loveu.bean.VPBaseBean;

/**
 * 历史 tag 
 * @author tanping
 * 2016-1-25
 */
public class OldTagBean  extends VPBaseBean{

	public List<OldTagBean> classroom;
	public List<OldTagBean> pua;
	
	public static String NAME ="name";
	public static String URL ="url";
	
	public  String mUrl;
	public  String mName;
	
	/**
	 * 解析接口
	 * @param json
	 * @return
	 */
	public static OldTagBean  praseOldTagBean(String json){
		OldTagBean bean = new OldTagBean();
		try {
			bean.classroom = new ArrayList<OldTagBean>();
			bean.pua = new ArrayList<OldTagBean>();
			
			JSONObject object = new JSONObject(json);
			
			JSONArray cr = object.optJSONArray("classroom");
			if (cr != null) {
				for (int i = 0; i < cr.length(); i++) {
					 bean.classroom.add(praseChildBean(cr.getJSONObject(i)));
				}
			}
			JSONArray pua = object.optJSONArray("pua");
			if (cr != null) {
				for (int i = 0; i < pua.length(); i++) {
					 bean.pua.add(praseChildBean(pua.getJSONObject(i)));
				}
			}
		} catch (Exception e) {
		}
		
		
		return bean;
	}
	
	public static OldTagBean praseChildBean(JSONObject json){
		OldTagBean bean = new OldTagBean();
		
		if (json!=null) {
			bean.mName = json.optString(NAME);
			bean.mUrl = json.optString(URL);
		}
		return bean;
	}
	
	
}
