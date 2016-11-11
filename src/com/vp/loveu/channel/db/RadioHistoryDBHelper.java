package com.vp.loveu.channel.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author：pzj
 * @date: 2015年12月2日 下午2:51:40
 * @Description: 电台播放历史记录库
 */
public class RadioHistoryDBHelper extends SQLiteOpenHelper {

	private static final String TAG = "RadioHistoryDBHelper";

	public RadioHistoryDBHelper(Context context) {
		super(context, RadioDB.DB_NAME, null, RadioDB.DB_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// 初始化数据库
		// 创建表
		String sql = RadioDB.RadioTable.SQL_CREATE_TABLE;
		Log.d(TAG, sql);
		db.execSQL(sql);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
