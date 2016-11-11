package com.vp.loveu.index.bean;

import java.util.List;

import com.vp.loveu.bean.VPBaseBean;

/**
 * @项目名称nameloveu1.0
 * @时间2015年12月8日下午12:00:23
 * @功能 上传情感求助的bean
 * @作者 mi
 */

public class UpdateFellHelpBean extends VPBaseBean{

	public int uid;
	public int type;
	public double price;
	public int pay_type;
	public String cont;
	public List<String> pics;
	public String audio;
	public String audio_title;
	
	public class UpdateFellHelp extends VPBaseBean{
		public int code;
		public String data;
		public int is_encrypt;
		public String msg;
	}
	public class UpdateDataBean extends VPBaseBean{
		public int order_id;//支付类型，和请求字段一致
		public int pay_type;//订单ID
		public int src_id;//求助ID
		public int user_num;//发送求助人数[(v1.0.6)增加user_num响应字段]
	}
}
