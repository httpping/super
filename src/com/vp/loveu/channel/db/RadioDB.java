package com.vp.loveu.channel.db;
/**
 * @author：pzj
 * @date: 2015年12月2日 下午2:55:05
 * @Description:
 */
public class RadioDB {
	public static final String DB_NAME="radio_db";//数据库名
	public static final int DB_VERSION=1;//数据库版本
	
	//数据表
	public interface RadioTable{
		public static final String TABLE_NAME="radio_history";
		
		public static final String COLUMN_ID="_id";//id
		public static final String COLUMN_UID="uid";//登录用户ID
		public static final String COLUMN_RID="rid";//电台ID
		public static final String COLUMN_NAME="name";//电台名称
		public static final String COLUMN_USER_NUM="user_num";
		public static final String COLUMN_RUID="ruid";//电台作者ID
		public static final String COLUMN_NICKNAME="nickname";//电台作者昵称
		public static final String COLUMN_URL="url";//播放url
		public static final String COLUMN_TOTALPOSITION="totalPosition";//总时长
		public static final String COLUMN_CURRENTPOSITION="currentPosition";//以播放时长
		public static final String COLUMN_UPLOADTOSERVER="uploadtoserver";//是否已上报到服务器 1是，0否

		public static final String SQL_CREATE_TABLE = "create table " + TABLE_NAME + "("
				+ COLUMN_ID + " integer primary key autoincrement,"
				+ COLUMN_UID + " integer," 
				+ COLUMN_RID + " integer,"
				+ COLUMN_NAME + " text,"
				+ COLUMN_USER_NUM + " integer,"
				+ COLUMN_RUID + " integer,"
				+ COLUMN_NICKNAME + " text,"
				+ COLUMN_URL + " text,"
				+ COLUMN_TOTALPOSITION + " integer,"
				+ COLUMN_CURRENTPOSITION + " integer,"
				+ COLUMN_UPLOADTOSERVER + " integer"
				+ ")";
	
	}

}
