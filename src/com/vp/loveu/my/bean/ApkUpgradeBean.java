package com.vp.loveu.my.bean;

import com.vp.loveu.bean.VPBaseBean;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年12月17日上午10:17:08
 * @功能TODO
 * @作者 mi
 */

public class ApkUpgradeBean extends VPBaseBean{
	public int code;
	public ApkUpgradeData data;
	public int is_encrypt;
	public String msg;

	public class ApkUpgradeData extends VPBaseBean{
		public int build_ver;
		public String name;
		public String package_name;
		public long size;
		public String summary;
		public String update_date;
		public int upgrade_type;
		public String url;
		public String ver;
	}
}
