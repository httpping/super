package com.vp.loveu.index.bean;

import java.util.List;

import com.vp.loveu.bean.VPBaseBean;

import android.app.Service;
import android.os.Handler;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月17日下午2:56:15
 * @功能 app应用首页的数据bean
 * @作者 mi
 */

public class IndexBean extends VPBaseBean{
	
	public int code;
	public IndexDataBean data;
	public int is_encrypt;
	public String msg;

	public class IndexDataBean extends VPBaseBean{
		public List<IndexActBean> activities;
		public List<IndexArtBean> articles;
		public List<IndexFosBean> focus;
		public List<IndexServiceBean> services;
		public List<IndexUserBean> users;
		public List<String> order;//模块顺序
	}

	public class IndexFosBean extends VPBaseBean{
		public String json_content;
		public String memo;
		public String pic;
		public String publish_time;
		public int rec_type;
		public String sub_title;
		public int target_id;
		public String title;
		public String url;
		
		@Override
		public String toString() {
			return "IndexFosBean [json_content=" + json_content + ", memo=" + memo + ", pic=" + pic + ", publish_time=" + publish_time
					+ ", rec_type=" + rec_type + ", sub_title=" + sub_title + ", target_id=" + target_id + ", title=" + title + ", url="
					+ url + "]";
		}
		
	}

	public class IndexArtBean extends VPBaseBean{
		public int has_video;
		public int id;
		public String nickname;
		public String pic;
		public String portrait;
		public int praise_num;
		public String small_pic;
		public String summary;
		public String title;
		public int uid;
		public String update_time;
		public int view_num;
	}

	public class IndexActBean extends VPBaseBean{
		public String apply_end_time;
		public String begin_time;
		public String end_time;
		public int id;
		public String name;
		public String pic;
		public double progress;
		public int remaining_num;
		public String small_pic;
		public List<IndexActUserBean> users;
	}

	public class IndexActUserBean extends VPBaseBean{
		public String create_time;
		public String nickname;
		public String portrait;
		public int uid;
	}

	public class IndexServiceBean extends VPBaseBean{
		public String icon;
		public String name;
		public String url;
	}

	public class IndexUserBean extends VPBaseBean{
		public int exp;
		public String nickname;
		public String portrait;
		public int rank;
		public int uid;
	}
}
