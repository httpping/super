package com.vp.loveu.message.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.vp.loveu.message.bean.UserInfo;

/**
 * 可序列化
 * 
 * @author ping
 * 
 */
public class UserInfoDao implements Serializable {

	private static UserInfoDao instance;
	private SQLiteDatabase db;
	public static int INIT_MSG_COUNT = 25;// 25条消息

	private UserInfoDao(Context context) {
		db = DatabaseHelper.getInstance(context).getWritableDatabase();
		System.out.println("db----->" + db);
	}

	public static UserInfoDao getInstance(Context context) {
		if (instance == null) {
			instance = new UserInfoDao(context);
		}
		return instance;
	}

	/**
	 * 插入或
	 * 
	 * @param UserInfo
	 * @return
	 */
	public long saveOrUpdate(UserInfo bean) {
		if (bean == null) {
			return 0;
		} else {
			ContentValues cvs = new ContentValues();
			cvs.put(UserInfo.USER_ID, bean.userId);
			cvs.put(UserInfo.USER_NAME, bean.userName);
			cvs.put(UserInfo.HEAD_IMAGE, bean.headImage);
			if (!TextUtils.isEmpty(bean.xmppUser)) {
				cvs.put(UserInfo.XMPP_USER, bean.xmppUser.toLowerCase());
			}
			
			
			if (bean.black >=0) {//黑名单
				cvs.put(UserInfo.BLACK, bean.black);
			}
			if (bean.onLineRemind >=0) {
				cvs.put(UserInfo.ONLINE_REMIND, bean.onLineRemind);
			}
			
			List<UserInfo> chatUserInfos = isExistence(bean.userId);
			if (chatUserInfos == null || chatUserInfos.size() == 0) { // 传送文件重复插入bug处理方法
				System.out.println("insert..." + UserInfo.USER_ID);
				long backId = db.insert(UserInfo.TABLE_NAME, null, cvs);
				return backId;
			} else {
				try {
					System.out.println("update..." + bean.userId);
					return db.update(UserInfo.TABLE_NAME, cvs, UserInfo.USER_ID
							+ " = ?",
							new String[] { String.valueOf(bean.userId + "") });
				} catch (Exception e) {

				}

			}
		}
		return 0;
	}

	/**
	 * 是否存在
	 * 
	 * @return
	 */
	public List<UserInfo> isExistence(String id) {

		String sql = "select * from " + UserInfo.TABLE_NAME + " where "
				+ UserInfo.USER_ID + " =? ";
		Cursor mCursor = db.rawQuery(sql, new String[] { id + "" });

		List<UserInfo> chatUserInfos = new ArrayList<UserInfo>();

		while (mCursor.moveToNext()) {
			UserInfo UserInfo = getObject(mCursor);
			chatUserInfos.add(UserInfo);
		}

		if (mCursor != null && !mCursor.isClosed()) {
			mCursor.close();
		}
		return chatUserInfos;
	}

	/**
	 * c所有
	 * 
	 * @return
	 */
	public List<UserInfo> findall() {

		Log.i("tag", "findall");
		String sql = "select * from  "+UserInfo.TABLE_NAME;
		Cursor mCursor = db.rawQuery(sql, new String[] {});
		// Cursor mCursor= db.query(UserInfo.TABLE_NAME, null, null, null,
		// null, null, null);

		List<UserInfo> chatUserInfos = new ArrayList<UserInfo>();

		while (mCursor.moveToNext()) {
			UserInfo UserInfo = getObject(mCursor);
			chatUserInfos.add(UserInfo);
		}

		if (mCursor != null && !mCursor.isClosed()) {
			mCursor.close();
		}
		return chatUserInfos;
	}

	public int delete(int id) {

		return 0;
	}

	/**
	 * 查询总条数
	 * 
	 * @return
	 */
	public long getCount() {
		Cursor cursor = db.rawQuery("select count(*)from "
				+ UserInfo.TABLE_NAME, null);
		cursor.moveToFirst();
		long count = cursor.getLong(0);
		cursor.close();
		Log.d(" UserInfo count", count + "");
		return count;
	}

	/**
	 * 根据id查找
	 * 
	 * @param id
	 * @return
	 */
	public UserInfo findById(String id) {

		Cursor c = db.query(UserInfo.TABLE_NAME, null, UserInfo.USER_ID
				+ " = ?", new String[] { "" + id }, null, null, null);
		UserInfo fluid = null;
		while (c.moveToNext()) {
			fluid = getObject(c);
		}
		DatabaseHelper.closeCursor(c);
		return fluid;
	}

	/**
	 * 根据id查找
	 * 
	 * @param id
	 * @return
	 */
	public UserInfo findByXmppUser(String xmppUser) {

		Cursor c = db.query(UserInfo.TABLE_NAME, null, UserInfo.XMPP_USER
				+ " = ?", new String[] { "" + xmppUser }, null, null, null);
		UserInfo fluid = null;
		while (c.moveToNext()) {
			fluid = getObject(c);
		}
		DatabaseHelper.closeCursor(c);
		return fluid;
	}
	
	// 赋值
	private UserInfo getObject(Cursor c) {
		UserInfo user = new UserInfo();
		user.userId = c.getString(c.getColumnIndex(UserInfo.USER_ID));
		user.userName = c.getString(c.getColumnIndex(UserInfo.USER_NAME));
		user.headImage = c.getString(c.getColumnIndex(UserInfo.HEAD_IMAGE));
		user.xmppUser = c.getString(c.getColumnIndex(UserInfo.XMPP_USER));
		user.black = c.getInt(c.getColumnIndex(UserInfo.BLACK));
		user.onLineRemind = c.getInt(c.getColumnIndex(UserInfo.ONLINE_REMIND));
		return user;

	}

}
