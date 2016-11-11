package com.vp.loveu.channel.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author：pzj
 * @date: 2015年12月2日 下午3:15:58
 * @Description:
 */
public class RadioDbDao {
	
	private RadioHistoryDBHelper mDbHelper;

	public RadioDbDao(Context context) {
		mDbHelper = new RadioHistoryDBHelper(context);
	}
	
	
	/**
	 * 添加
	 */
	public  boolean insert(RadioHistoryBean bean){
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
//		values.put(RadioDB.RadioTable.COLUMN_ID, bean.get_id());
		values.put(RadioDB.RadioTable.COLUMN_UID, bean.getUid());
		values.put(RadioDB.RadioTable.COLUMN_RID, bean.getRid());
		values.put(RadioDB.RadioTable.COLUMN_RUID, bean.getRuid());
		values.put(RadioDB.RadioTable.COLUMN_NAME, bean.getName());
		values.put(RadioDB.RadioTable.COLUMN_NICKNAME, bean.getNickname());
		values.put(RadioDB.RadioTable.COLUMN_URL, bean.getUrl());
		values.put(RadioDB.RadioTable.COLUMN_USER_NUM, bean.getUser_num());
		values.put(RadioDB.RadioTable.COLUMN_TOTALPOSITION, bean.getTotalPosition());
		values.put(RadioDB.RadioTable.COLUMN_CURRENTPOSITION, bean.getCurrentPosition());
		values.put(RadioDB.RadioTable.COLUMN_UPLOADTOSERVER, 0);
		long result=db.insert(RadioDB.RadioTable.TABLE_NAME, null, values);
		db.close();
		return result!=-1;
	}
	
	/**
	 * 判断当前记录是否存在
	 * @param uid 用户ID
	 * @param rid 电台ID
	 * @return
	 */
	public RadioHistoryBean findRadioHistory(int uid,int rid){
		RadioHistoryBean bean=null;
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		Cursor cursor = db.query(RadioDB.RadioTable.TABLE_NAME, new String[]{RadioDB.RadioTable.COLUMN_UID,RadioDB.RadioTable.COLUMN_RID,RadioDB.RadioTable.COLUMN_CURRENTPOSITION,RadioDB.RadioTable.COLUMN_TOTALPOSITION,RadioDB.RadioTable.COLUMN_UPLOADTOSERVER}, RadioDB.RadioTable.COLUMN_UID+"=?"+" and "+RadioDB.RadioTable.COLUMN_RID+"=?", new String[]{uid+"",rid+""}, null, null, null);
		if(cursor!=null){
			if (cursor.moveToNext()) {
				bean=new RadioHistoryBean();
				bean.setUid(cursor.getInt(0));
				bean.setRid(cursor.getInt(1));
				bean.setCurrentPosition(cursor.getInt(2));
				bean.setTotalPosition(cursor.getInt(3));
				bean.setUploadtoserver(cursor.getInt(4));
			}
			cursor.close();
		}
		db.close();
		
		return bean;
	}

	public boolean update(int uid,int rid,int currentPosition,int totalPosition){
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put(RadioDB.RadioTable.COLUMN_CURRENTPOSITION, currentPosition);
		values.put(RadioDB.RadioTable.COLUMN_TOTALPOSITION, totalPosition);
		int rows = db.update(RadioDB.RadioTable.TABLE_NAME, values, RadioDB.RadioTable.COLUMN_UID+"=?"+" and "+RadioDB.RadioTable.COLUMN_RID+"=?", new String[]{uid+"",rid+""});
		db.close();
		return rows>0;
	}
	
	public boolean updateUploadInfo(int uid,int rid){
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put(RadioDB.RadioTable.COLUMN_UPLOADTOSERVER, 1);
		int rows = db.update(RadioDB.RadioTable.TABLE_NAME, values, RadioDB.RadioTable.COLUMN_UID+"=?"+" and "+RadioDB.RadioTable.COLUMN_RID+"=?", new String[]{uid+"",rid+""});
		db.close();
		return rows>0;
	}
	
	public boolean delete(int uid,int rid){
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		int result = db.delete(RadioDB.RadioTable.TABLE_NAME, RadioDB.RadioTable.COLUMN_UID+"=?"+" and "+RadioDB.RadioTable.COLUMN_RID+"=?", new String[]{uid+"",rid+""});
		db.close();
		return result>0;
	}
}
