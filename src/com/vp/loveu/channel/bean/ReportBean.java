package com.vp.loveu.channel.bean;

import java.util.ArrayList;

/**
 * @author：pzj
 * @date: 2015年11月30日 上午11:14:44
 * @Description:举报Bean
 */
public class ReportBean {

	/**
	 * 被举报的用户ID
	 */
	private int t_uid;
	
	/**
	 * 被举报人用户昵称
	 */
	private String t_nickname;
	
	 /**
	  * 举报的内容
	  */
	private String remark;
	
	/**截图
	 * 
	 */
	private ArrayList<String> pics;
	
	/**
	 * 举报分类
	 * 	0=其他不合法内容
	*   1=色情暴力
	*	2=虚假广告
	*	3=敏感信息
	*	4=不实谣言
	 */
	private int cate_id;
	
/**
 * 举报类型
 * 	0=举报人
 *	1=举报大家都在看信息
 */
	private int type;

	/**
	 * 信息ID，比如只有type=1时才有此值，当举报内容为此值时可将原文的内容填写到remark里面。
	 */
	private int info_id;
	
	
	public ReportBean() {
		// TODO Auto-generated constructor stub
	}
	

	public ReportBean(int t_uid, String t_nickname, String remark, ArrayList<String> pics, int type,
			int info_id) {
		super();
		this.t_uid = t_uid;
		this.t_nickname = t_nickname;
		this.remark = remark;
		this.pics = pics;
		this.type = type;
		this.info_id = info_id;
	}


	public int getT_uid() {
		return t_uid;
	}

	public void setT_uid(int t_uid) {
		this.t_uid = t_uid;
	}

	public String getT_nickname() {
		return t_nickname;
	}

	public void setT_nickname(String t_nickname) {
		this.t_nickname = t_nickname;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public ArrayList<String> getPics() {
		return pics;
	}

	public void setPics(ArrayList<String> pics) {
		this.pics = pics;
	}

	public int getCate_id() {
		return cate_id;
	}

	public void setCate_id(int cate_id) {
		this.cate_id = cate_id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getInfo_id() {
		return info_id;
	}

	public void setInfo_id(int info_id) {
		this.info_id = info_id;
	}	
	
	
}
