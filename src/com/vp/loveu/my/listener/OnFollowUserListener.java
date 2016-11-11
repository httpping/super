package com.vp.loveu.my.listener;

import com.vp.loveu.my.bean.FollowBean.FollowData;

/**
 * @项目名称nameloveu1.0
 * @时间2015年12月18日上午10:11:25
 * @功能TODO
 * @作者 mi
 */

public interface OnFollowUserListener {
	/**添加关注*/
	void addFollow(FollowData bean);
	/**取消关注*/
	void cancleFollow(FollowData bean);
}
