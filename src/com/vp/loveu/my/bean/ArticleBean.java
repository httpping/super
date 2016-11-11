package com.vp.loveu.my.bean;

import java.util.List;

import com.vp.loveu.bean.VPBaseBean;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年12月11日下午5:34:59
 * @功能TODO
 * @作者 mi
 */

public class ArticleBean extends VPBaseBean{
	public int code;
	public String data;
	public int is_encrypt;
	public String msg;

	public class ArticleDataBean extends VPBaseBean{
		public int author_id;
		public String author_nickname;
		public String author_portrait;
		public String create_time;
		public int id;
		public List<String>	pics;
		public String publish_time;
		public String summary;
		public int target_id;
		public int target_type;
		public String title;
		public int has_video;
		public int fav_num;
		public int is_fav;
		public int is_like;
		public int like_num;
		public int pid;
		
		
		@Override
		public boolean equals(Object o) {
			if (o!= null && o instanceof ArticleDataBean) {
				ArticleDataBean b = (ArticleDataBean) o;
				return b.target_id == this.target_id;
			}
			return super.equals(o);
		}
	}
}
