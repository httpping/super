package com.vp.loveu.bean;

/**
 * @ClassName: NotifyBean 通知栏实体类
 * @Description:
 * @author zeus
 * @date 2015-8-12 上午10:38:37
 * 
 */
public class NotifyBean {
	private int id;
	private String cusId;
	private int activityCount;
	private int friendCount;
	private int praisedCount;
	private int commentCount;
	private int visitorCount;

	public int getAllCount() {
		return allCount;
	}

	private int allCount;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCusId() {
		return cusId;
	}

	public void setCusId(String cusId) {
		this.cusId = cusId;
	}

	public int getActivityCount() {
		return activityCount;
	}

	public void setActivityCount(int activityCount) {
		this.activityCount = activityCount;
		this.allCount += activityCount;
	}

	public int getFriendCount() {
		return friendCount;
	}

	public void setFriendCount(int friendCount) {
		this.friendCount = friendCount;
		this.allCount += friendCount;
	}

	public int getPraisedCount() {
		return praisedCount;
	}

	public void setPraisedCount(int praisedCount) {
		this.praisedCount = praisedCount;
		this.allCount += praisedCount;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
		this.allCount += commentCount;
	}

	public int getVisitorCount() {
		return visitorCount;
	}

	public void setVisitorCount(int visitorCount) {
		this.visitorCount = visitorCount;
		this.allCount += visitorCount;
	}

}
