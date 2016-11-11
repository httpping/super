package com.vp.loveu.discover.bean;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.vp.loveu.bean.VPBaseBean;

/**

*
* @ClassName:
* @Description: 在线课程bean
* @author pzj
* @date 
*/
public class CourseOnlineBeanVo {

	private ArrayList<Lesson> lessons;
	private ArrayList<User> users;
	private int user_num;
	
	
	public static CourseOnlineBeanVo parseJson(String json){
		Gson gson=new Gson();
		CourseOnlineBeanVo bean=gson.fromJson(json, CourseOnlineBeanVo.class);
		return bean;
	}
	
	public ArrayList<Lesson> getLessons() {
		return lessons;
	}
	public void setLessons(ArrayList<Lesson> lessons) {
		this.lessons = lessons;
	}
	public ArrayList<User> getUsers() {
		return users;
	}
	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}
	public int getUser_num() {
		return user_num;
	}
	public void setUser_num(int user_num) {
		this.user_num = user_num;
	}

	public class Lesson{
		private int id	;
		private String name	;
		private String pic	;
		private int user_num	;
		private double price	;
		private int rebate_day;
		private String remark;
		private int is_official;
		private double original_price;
		
		
		
		public double getOriginal_price() {
			return original_price;
		}
		public void setOriginal_price(double original_price) {
			this.original_price = original_price;
		}
		public int getIs_official() {
			return is_official;
		}
		public void setIs_official(int is_official) {
			this.is_official = is_official;
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
		public void setName(String name) {
			this.name = name;
		}
		public String getPic() {
			return pic;
		}
		public void setPic(String pic) {
			this.pic = pic;
		}
		public int getUser_num() {
			return user_num;
		}
		public void setUser_num(int user_num) {
			this.user_num = user_num;
		}
		public double getPrice() {
			return price;
		}
		public void setPrice(double price) {
			this.price = price;
		}
		public String getRemark() {
			return remark;
		}
		public void setRemark(String remark) {
			this.remark = remark;
		}
		public int getRebate_day() {
			return rebate_day;
		}
		public void setRebate_day(int rebate_day) {
			this.rebate_day = rebate_day;
		}	
		
		
	}
	
	public class User extends VPBaseBean{
       private int uid	;
       private String nickname;	
       private String portrait;
       private String lesson_name;
       private double price;
       private String create_time;
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
	public String getPortrait() {
		return portrait;
	}
	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}
	public String getLesson_name() {
		return lesson_name;
	}
	public void setLesson_name(String lesson_name) {
		this.lesson_name = lesson_name;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
       
       
	}
}


