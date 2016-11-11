package com.vp.loveu.index.bean;

import java.util.List;

import com.vp.loveu.bean.VPBaseBean;

/**
 * @项目名称nameloveu1.0
 * @时间2015年11月24日上午10:04:40
 * @功能 情感求助的数据bean
 * @作者 mi
 */

public class FellHelpBean extends VPBaseBean {
	public int code;
	public FellHelpBeanData data;
	public int is_encrypt;
	public String msg;
	
	public static class FellHelpBeanData extends VPBaseBean{
		public List<FellHelpBeanAskedBean> asked;
		public int asked_num;
		public double expert_price;
		public int max_answer_num;
		public double talent_price;
	}
	public static class FellHelpBeanAskedBean extends VPBaseBean{
		public List<FellHelpBeanAudiosBean> audios;
		public String cont;
		public String create_time;
		public int id;
		public String nickname;
		public String portrait	;
		public double price;
		public List<FellHelpBeanAskedBean> replys;
		public int type;
		public List<String> pics;
		public int uid;
	}
	public static class FellHelpBeanAudiosBean extends VPBaseBean{
		public String title;
		public String url;
	}
}
