/**   
* @Title: AreaBean.java 
* @Package com.vp.loveu.bean 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zeus   
* @date 2015-10-27 下午4:26:47 
* @version V1.0   
*/
package com.vp.loveu.bean;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

/**

 *
 * @ClassName: 地址bean
 * @Description:
 * @author ping 
 * @date 
 */
public class AreaBean {
	
	public String id ;
	public String name ;
	public List<AreaBean> children;
	public AreaBean father;
	
	
	
/*	public static List<AreaBean> praseArea(JSONArray array){
		List<AreaBean> areas = new LinkedList<AreaBean>();
		//Log.d("json", "json:" +array);
		try {
			for (int i = 0; i < array.length(); i++) {
				JSONObject json = array.getJSONObject(i);
				
				AreaBean bean = new AreaBean();
				bean.id = json.getString("Id");
				bean.name = json.getString("Name");
				if (json.has("Children")) {
					bean.children = praseChildrenArea(json.getJSONArray("Children"),bean);
				}
				areas.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		return areas;
		
	}*/
	
	private static List<AreaBean> praseChildrenArea(JSONArray array,AreaBean father){
		List<AreaBean> areas = new LinkedList<AreaBean>();
		//Log.d("json", "json:" +array);
		try {
			for (int i = 0; i < array.length(); i++) {
				JSONObject json = array.getJSONObject(i);
				
				AreaBean bean = new AreaBean();
				bean.id = json.getString("Id");
				bean.name = json.getString("Name");
				bean.father = father;
				if (json.has("Children")) {
					bean.children = praseChildrenArea(json.getJSONArray("Children"),father);
				}
				areas.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		return areas;
		
	}
	
	/**
	 * 
	 * @param array
	 * @param father null
	 * @param level  0 
	 * @return
	 * List<AreaBean>
	 * TODO
	 */
	public static List<AreaBean> praseAreaCity(JSONArray array,AreaBean father,int level){
		List<AreaBean> areas = new LinkedList<AreaBean>();
		//Log.d("json", "json:" +array);
		try {
			level = level+1;//自增
			for (int i = 0; i < array.length(); i++) {
				JSONObject json = array.getJSONObject(i);
				
				AreaBean bean = new AreaBean();
				bean.id = json.getString("Id");
				bean.name = json.getString("Name");
				bean.father = father;
				if (json.has("Children") && level <=1) {
					bean.children = praseAreaCity(json.getJSONArray("Children"),bean,level);
				}
				areas.add(bean);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		return areas;
		
	}
	
	
	public static  AreaBean findArea(List<AreaBean> aras ,String code ) {
		if(TextUtils.isEmpty(code) || aras==null || aras.isEmpty()){
			return null ;
		}
		
		for(int i=0 ;i < aras.size() ;i++){
			AreaBean bean = aras.get(i);
			if (code.startsWith(bean.id)) {//省
				if(code.equals(bean.id)){
					return bean;//省
				}else{
					if(bean.id.length() ==4){
						return bean; //解析到市
					}
					return findArea(bean.children, code); //递归
				}
			}
		}
		
		return null ;
	}

	@Override
	public String toString() {
		return "AreaBean [id=" + id + ", name=" + name + ", children=" + children + ", father=" + father + "]";
	}

}
