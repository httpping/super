package com.vp.loveu.index.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月24日下午3:11:22
 * @功能 免费求助的数据bean
 * @作者 mi
 */

public class FreeHelpBean implements Serializable {
	public String content;
	public ArrayList<String> photoList;
	public int maxNumb;
	public int src_id;
	public int last_id ;
	public int userNum;
}
