package com.vp.loveu.my.bean;

import java.util.List;

import com.vp.loveu.bean.VPBaseBean;

/**
 * @项目名称nameloveu1.0
 * @时间2015年11月30日下午3:47:26
 * @功能 我关注的数据bean
 * @作者 mi
 */

public class FollowBean extends VPBaseBean{
	public int code;
	public List<FollowData> data;
	public int is_encrypt;
	public String msg;
	
	public class FollowData extends VPBaseBean{
		public String nickname;
		public String portrait;
		public int sex;
		public int status;
		public int uid;
		public boolean isCancle;
		
		@Override
		public boolean equals(Object o) {
			if (o !=null && o instanceof FollowData) {
				FollowData d =(FollowData) o;
				return d.uid == uid;
			}
			return super.equals(o);
		}
	}
	
}
