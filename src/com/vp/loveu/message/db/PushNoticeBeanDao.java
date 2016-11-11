package com.vp.loveu.message.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.vp.loveu.message.bean.PushNoticeBean;
import com.vp.loveu.util.VPLog;

/**
 * 可序列化
 * 
 * @author ping
 * 
 */
public class PushNoticeBeanDao implements Serializable {

	public static final String TAG = "PushNoticeBeanDao";
	
	private static PushNoticeBeanDao instance;
	private SQLiteDatabase db;
	public static int INIT_MSG_COUNT = 25;// 25条消息
	

	private PushNoticeBeanDao(Context context) {
		db = DatabaseHelper.getInstance(context).getWritableDatabase();
		System.out.println("db----->" + db);
	}

	public static PushNoticeBeanDao getInstance(Context context) {
		if (instance == null) {
			instance = new PushNoticeBeanDao(context);
		}
		return instance;
	}

	/**
	 * 插入或
	 * 
	 * @param pushBean
	 * @return
	 */
	public synchronized long saveOrUpdate(PushNoticeBean pushBean) {

		if (pushBean == null) {
			return 0;
		} else {
			 
			ContentValues cvs = new ContentValues();
			if (pushBean.id!=0) {
				cvs.put(pushBean.ID, pushBean.id);
			}
			
			cvs.put(pushBean.TIMESTAMP, pushBean.timestamp);
			cvs.put(pushBean.BODY, pushBean.body);
			cvs.put(pushBean.LOGIN_USER_ID, pushBean.loginId);
			cvs.put(pushBean.READ_STATUE, pushBean.readStatus);
			
			
			Log.d("session", "session :" +pushBean);
			
			
			List<PushNoticeBean> pushNoticeBeans =  isExistence(pushBean.id);
			if (pushNoticeBeans == null || pushNoticeBeans.size() == 0) { // 传送文件重复插入bug处理方法
				System.out.println("PushNoticeBean insert..." + pushBean.id);
				long backId = db.insert(PushNoticeBean.TABLE_NAME, null, cvs);
				VPLog.d(TAG ,"PushNoticeBean insert..." + backId);
				return backId;
			} else {
				try {
					VPLog.d(TAG ,"PushNoticeBean update..." + pushBean.id);
					return db
							.update(PushNoticeBean.TABLE_NAME, cvs, PushNoticeBean.ID  , new String[] { 
									pushBean.id+"" });
				} catch (Exception e) {
					e.printStackTrace();
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
	public List<PushNoticeBean> isExistence(int id ) {
		
		String sql = "select * from "+PushNoticeBean.TABLE_NAME+" where " + PushNoticeBean.ID + " =?  ";
		Cursor mCursor = db.rawQuery(sql, new String[] {id+"" });

		List<PushNoticeBean> chatpushBeans = new ArrayList<PushNoticeBean>();

		while (mCursor.moveToNext()) {
			PushNoticeBean pushBean = getObject(mCursor);
			chatpushBeans.add(pushBean);
		}

		if (mCursor != null && !mCursor.isClosed()) {
			mCursor.close();
		}
		return chatpushBeans;
	}
	
	
	
	 
	

	/**
	 * c所有
	 * 
	 * @return
	 */
	public List<PushNoticeBean> findall() {

		Log.i("tag", "findall");
		String sql = "select * from " + PushNoticeBean.TABLE_NAME;
		Cursor mCursor = db.rawQuery(sql, new String[] {});
		// Cursor mCursor= db.query(pushBean.TABLE_NAME, null, null, null,
		// null, null, null);

		List<PushNoticeBean> chatpushBeans = new ArrayList<PushNoticeBean>();

		while (mCursor.moveToNext()) {
			PushNoticeBean pushBean = getObject(mCursor);
			chatpushBeans.add(pushBean);
		}

		if (mCursor != null && !mCursor.isClosed()) {
			mCursor.close();
		}
		return chatpushBeans;
	}
	
	/**
	 * c所有
	 * 
	 * @return
	 */
	public Cursor findAllCursor(String  longUser) {

		Log.i("tag", "findall:"+longUser );
		String sql = "select * from " + PushNoticeBean.TABLE_NAME +" where "+ PushNoticeBean.LOGIN_USER_ID +" = "+longUser +" order by "+PushNoticeBean.TIMESTAMP +" desc ";
		Cursor mCursor = db.rawQuery(sql, new String[] {});
		 
		return mCursor;
	}

	/**
	 * 清理会话
	 * @param loginId
	 * @return
	 */
	public int delete(String loginId) {
		
		Log.d("dele", "chat:del");
		String sql ="delete from " +PushNoticeBean.TABLE_NAME +" where "+ PushNoticeBean.LOGIN_USER_ID + "="+loginId;
		//PushNoticeBean message = findById(loginId);
		//Log.d("delete", "chat:"+message);
		db.execSQL(sql);
		
		return 0;
	}
	
	public int deleteForTimestamp(long time) {
		
		Log.d("dele", "chat:del");
		String sql ="delete from " +PushNoticeBean.TABLE_NAME +" where "+PushNoticeBean.TIMESTAMP + "="+time;
		//pushBean message = findById(id);
		//Log.d("delete", "chat:"+message);
		db.execSQL(sql);
		
		return 0;
	}

 
 
	/**
	 * 查询总条数
	 * 
	 * @return
	 */
	public long getCount() {
		Cursor cursor = db.rawQuery("select count(*)from "
				+ PushNoticeBean.TABLE_NAME, null);
		cursor.moveToFirst();
		long count = cursor.getLong(0);
		cursor.close();
		Log.d(" pushBean count", count + "");
		return count;
	}
	
	/**
	 * 查询未读数量条数
	 * 
	 * @return
	 */
	public long getUnreadCount(String loginId) {
		
		String sql = "select count(*) from " + PushNoticeBean.TABLE_NAME +" where "+ PushNoticeBean.LOGIN_USER_ID +" = "+loginId +" and "+PushNoticeBean.READ_STATUE +"=0";
		Cursor cursor = db.rawQuery(sql, new String[] {});
		cursor.moveToFirst();
		long count = cursor.getLong(0);
		cursor.close();
		VPLog.d("pushBean unread count", count + "");
		return count;
	}
	
	/**
	 * 设置已读
	 */
	public void setReadMsg(String loginUserId) {
		Log.i("test", "setReadMsg from= " + loginUserId );
	
		ContentValues cvs = new ContentValues();
		cvs.put(PushNoticeBean.READ_STATUE,1);
		try {
			db.update(PushNoticeBean.TABLE_NAME, cvs, PushNoticeBean.LOGIN_USER_ID
					+ " = ? ",
					new String[] { loginUserId });
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	 
	 
	/**
	 * 升序 分页查找,用于聊天页面下拉刷新
	 */
	public List<PushNoticeBean> findPage(String loginUserId,int page, int pageSize) {

		if (pageSize < 0) {
			pageSize = INIT_MSG_COUNT;
		}

		String sql = "select * from " + PushNoticeBean.TABLE_NAME + "  where "
				+ PushNoticeBean.LOGIN_USER_ID + "=? order by " + PushNoticeBean.ID
				+ " limit " + pageSize + " offset " + page * pageSize;

		Cursor mCursor = db.rawQuery(sql,
				new String[] {loginUserId});
		List<PushNoticeBean> chaTotals = new ArrayList<PushNoticeBean>();

		while (mCursor.moveToNext()) {
			PushNoticeBean chatMsg = getObject(mCursor);
			chaTotals.add(chatMsg);// 添加
		}
		if (mCursor != null && !mCursor.isClosed()) {
			mCursor.close();
		}

		return chaTotals;
	}

	/**
	 * 获得总页数
	 * 
	 * @param size
	 * @return
	 */
	public int getCountPage(int loginId) {
		long count = getUserChatCount(loginId+"");
		int allPage = (int) (count / INIT_MSG_COUNT);
		if (allPage>0 && count % INIT_MSG_COUNT ==8) {
			allPage--;
		}
		Log.d("count page", " count page "+count +" page" + allPage + " " + getCount() + " "
				+ INIT_MSG_COUNT);
		return allPage;
	}

	/**
	 * 查询 记录数
	 */
	public long getUserChatCount(String myUser) {
		/* where (from_id=? and to_id=?) or (to_id=? and from_id=?) */
		Cursor cursor = db.rawQuery("select count(*) from "
				+ PushNoticeBean.TABLE_NAME + "  where "
				+ PushNoticeBean.LOGIN_USER_ID + "=? "
				 , new String[] {
				myUser });
		cursor.moveToFirst();
		long count = cursor.getLong(0);
		cursor.close();
		Log.d(" PushNoticeBean count", count + " " + myUser + " -" );
		return count;
	}
	/**
	 * 根据id查找
	 * 
	 * @param id
	 * @return
	 */
	public PushNoticeBean findById(float id) {

		Cursor c = db.query(PushNoticeBean.TABLE_NAME, null, PushNoticeBean.ID
				+ " = ?", new String[] { "" + id }, null, null, null);
		PushNoticeBean fluid = null;
		while (c.moveToNext()) {
			fluid = getObject(c);
		}
		DatabaseHelper.closeCursor(c);
		return fluid;
	}

	// 赋值
	public PushNoticeBean getObject(Cursor c) {
		PushNoticeBean pushBean = new PushNoticeBean();
		
		pushBean.id = c.getInt(c.getColumnIndex(pushBean.ID));
		pushBean.body = c.getString(c.getColumnIndex(pushBean.BODY));
		pushBean.readStatus = c.getInt(c
				.getColumnIndex(pushBean.READ_STATUE));
		
		pushBean.loginId = c.getString(c
				.getColumnIndex(pushBean.LOGIN_USER_ID));
		pushBean.timestamp = c.getLong(c
				.getColumnIndex(pushBean.TIMESTAMP));
		
		pushBean.paseNotcie(pushBean.body);//解析body
		

		return pushBean;

	}

	 

}
