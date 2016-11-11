package com.vp.loveu.message.db;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.vp.loveu.message.bean.ChatMessage;
import com.vp.loveu.message.bean.ChatSessionBean;
import com.vp.loveu.message.bean.PushNoticeBean;
import com.vp.loveu.message.bean.UserInfo;

/**
 * @author tp
 */
public class DatabaseHelper extends SQLiteOpenHelper{
	
	private static final int VERSION = 8;
	public static final String DB_NAME ="chat_database";
	private static DatabaseHelper instance;
	
	//构造函数
	public DatabaseHelper(Context context , CursorFactory factory,
			int version) {
		super(context, DB_NAME, factory, version);
		 
	}
	
	public DatabaseHelper(Context context , int version) {
		super(context, DB_NAME, null, version);
		 
	}
	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	
	//获得实例
	public static DatabaseHelper getInstance(Context context){
		if(instance==null){
			instance = new DatabaseHelper(context.getApplicationContext());
		}
		 
		return instance;
	}
	
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		//创建表 
		for (int i = 0; i < table.length; i++) {
			db.execSQL(table[i]);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		
		try {
			Log.d("onUpgrade", "onUpgrade");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//删除表 
		for (int i = 0; i < dropTables.length; i++) {
			db.execSQL(dropTables[i]);
		}
		
		//创建表 
		for (int i = 0; i < table.length; i++) {
			db.execSQL(table[i]);
		}		
	}
	
	
	private static String[] dropTables = new String[]{
		"DROP TABLE IF EXISTS "+ChatMessage.TABLE_NAME,
		"DROP TABLE IF EXISTS " +UserInfo.TABLE_NAME,
		"DROP TABLE IF EXISTS " +ChatSessionBean.TABLE_NAME,
		"DROP TABLE IF EXISTS " +PushNoticeBean.TABLE_NAME,

	};
	
    private static String[] table =  new String[]	{
    	 "CREATE TABLE  IF NOT EXISTS "+ChatMessage.TABLE_NAME+"( " +
    	 ChatMessage.ID  + " integer PRIMARY KEY autoincrement," +
    	 ChatMessage.FROM_NAME+" text," +
    	 ChatMessage.TO_NAME+" text , " +
    	 ChatMessage.MSG_TYPE+" int," +
    	 ChatMessage.BODY +" text," + 
    	 ChatMessage.TIMESTAMP +" long," +
    	 ChatMessage.SHOW_TYPE +" int," +
    	 ChatMessage.READ_STATUE +" int ," +
    	 ChatMessage.SEND_MSG_STATUS+" int," +
    	 ChatMessage.LOGIN_USER_NAME+" text ," +
    	 ChatMessage.CHAT_OTHER_USER_NAME+" text  "+
    	 ")",
 		"create table  if not exists "+UserInfo.TABLE_NAME+"(" +
 				 UserInfo.USER_ID + " int PRIMARY KEY," +
 				 UserInfo.USER_NAME +"  text, " +
 				 UserInfo.HEAD_IMAGE +" text," +
 				 UserInfo.XMPP_USER +" text, "	+	
 				 UserInfo.BLACK +" int ," +
 				 UserInfo.ONLINE_REMIND	+ " int "+ ")",
 		 "CREATE TABLE  IF NOT EXISTS "+ChatSessionBean.TABLE_NAME+"( " +
 				 ChatMessage.ID  + " int," +
 		    	 ChatMessage.FROM_NAME+" text," +
 		    	 ChatMessage.TO_NAME+" text , " +
 		    	 ChatMessage.MSG_TYPE+" int," +
 		    	 ChatMessage.BODY +" text," + 
 		    	 ChatMessage.TIMESTAMP +" long," +
 		    	 ChatMessage.SHOW_TYPE +" int," +
 		    	 ChatMessage.READ_STATUE +" int ," +
 		    	 ChatMessage.SEND_MSG_STATUS+" int," +
 		    	 ChatMessage.LOGIN_USER_NAME+" text ," +
 		    	 ChatMessage.CHAT_OTHER_USER_NAME+" text ,"+
 		    	 ChatSessionBean.MESSAGE_COUNT +" int, "+
 		    	 ChatSessionBean.IS_STICK + " int, " + 
 		    	 "PRIMARY KEY (" + ChatMessage.LOGIN_USER_NAME +" , " +
 		    	 ChatMessage.CHAT_OTHER_USER_NAME+")" +
 		    	 ")",
	    	"create table  if not exists "+PushNoticeBean.TABLE_NAME+"(" +
	    			PushNoticeBean.ID + " int PRIMARY KEY," +
	    			PushNoticeBean.TIMESTAMP +"  long, " +
	    			PushNoticeBean.BODY +" text,"+
	    			PushNoticeBean.READ_STATUE +" int ," +
	    			PushNoticeBean.LOGIN_USER_ID +" text ) ", 	 
    };
  
   /*
    * 关闭游标
    */
   public static void closeCursor(Cursor c){
	   if (c!=null && !c.isClosed()) {
			c.close();
		}
   }
}