package com.vp.loveu.discover.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.Gson;

/**
 * @author：pzj
 * @date: 2015年11月23日 上午9:53:51
 * @Description:
 */
public class PuaCoruseBeanVo {
	private int id;
	private String name;
	private String pic;
	private ArrayList<PuaCoruseSubBean> children;	
	
	public static PuaCoruseBeanVo parseJson(String json){
		Gson gson=new Gson();
		return gson.fromJson(json, PuaCoruseBeanVo.class);
	}
	
	public static ArrayList<PuaCoruseBeanVo> parseArrayJson(JSONArray jsonData){
		ArrayList<PuaCoruseBeanVo> list=new ArrayList<PuaCoruseBeanVo>();
		if(jsonData!=null){
			for(int i=0;i<jsonData.length();i++){
				try {
					String json = jsonData.getString(i);
					PuaCoruseBeanVo bean =PuaCoruseBeanVo.parseJson(json);
					if(bean!=null)
						list.add(bean);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	
	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<PuaCoruseSubBean> getChildren() {
		return children;
	}
	public void setChildren(ArrayList<PuaCoruseSubBean> children) {
		this.children = children;
	}
	
	
	public class PuaCoruseSubBean{
		private int id;
		private String name;
		private String pic;
		
		
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getPic() {
			return pic;
		}
		public void setPic(String pic) {
			this.pic = pic;
		}
		
		
	}

}
