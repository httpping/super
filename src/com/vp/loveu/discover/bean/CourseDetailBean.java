package com.vp.loveu.discover.bean;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.vp.loveu.bean.VPBaseBean;
import com.vp.loveu.discover.bean.CourseOnlineBeanVo.User;

/**
 * @author：pzj
 * @date: 2015年11月18日 下午3:51:56
 * @Description: 在线课程详情bean
 */
public class CourseDetailBean extends VPBaseBean{
	
	private int id	;
	private String name;
	private String pic;
	private int user_num;
	private double price	;
	private String remark;
	private ArrayList<User>	users;
	private Mentor mentor;
	private String description;
	private int rebate_day;
	private Consult consult;
	private double original_price;
	private int is_official;
	
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

	
	public static CourseDetailBean parseJson(String json){
		Gson gson=new Gson();
		return gson.fromJson(json, CourseDetailBean.class);
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
		public ArrayList<User> getUsers() {
			return users;
		}
		public void setUsers(ArrayList<User> users) {
			this.users = users;
		}
		public Mentor getMentor() {
			return mentor;
		}
		public void setMentor(Mentor mentor) {
			this.mentor = mentor;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		
		
    
		
		public Consult getConsult() {
			return consult;
		}

		public void setConsult(Consult consult) {
			this.consult = consult;
		}

		public int getRebate_day() {
			return rebate_day;
		}

		public void setRebate_day(int rebate_day) {
			this.rebate_day = rebate_day;
		}




	public class Mentor extends VPBaseBean{
        	private int uid;
    		private int is_identifiy;
    		private String grade;
    		private String nickname;
    		private String portrait;
    		private String xmpp_user;
    		private String description;
			public int getUid() {
				return uid;
			}
			public void setUid(int uid) {
				this.uid = uid;
			}
			public int getIs_identifiy() {
				return is_identifiy;
			}
			public void setIs_identifiy(int is_identifiy) {
				this.is_identifiy = is_identifiy;
			}
			public String getGrade() {
				return grade;
			}
			public void setGrade(String grade) {
				this.grade = grade;
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
			public String getXmpp_user() {
				return xmpp_user;
			}
			public void setXmpp_user(String xmpp_user) {
				this.xmpp_user = xmpp_user;
			}
			public String getDescription() {
				return description;
			}
			public void setDescription(String description) {
				this.description = description;
			}
    		
    		
    }
	
	public class Consult extends VPBaseBean{
		private int uid;
		private String nickname;
		private String portrait;
		private String xmpp_user;
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
		public String getXmpp_user() {
			return xmpp_user;
		}
		public void setXmpp_user(String xmpp_user) {
			this.xmpp_user = xmpp_user;
		}
		
	}
    		
}
