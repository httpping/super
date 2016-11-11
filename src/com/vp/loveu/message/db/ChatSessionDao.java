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

import com.vp.loveu.message.bean.ChatMessage;
import com.vp.loveu.message.bean.ChatMessage.MsgShowType;
import com.vp.loveu.message.bean.ChatSessionBean;
import com.vp.loveu.message.bean.UserInfo;
import com.vp.loveu.util.VPLog;

/**
 * 可序列化
 * 
 * @author ping
 * 
 */
public class ChatSessionDao implements Serializable {

	public static final String TAG = "ChatSessionDao";
	
	private static ChatSessionDao instance;
	private SQLiteDatabase db;
	public static int INIT_MSG_COUNT = 25;// 25条消息
	
	UserInfoDao mUserInfoDao;

	private ChatSessionDao(Context context) {
		db = DatabaseHelper.getInstance(context).getWritableDatabase();
		mUserInfoDao = UserInfoDao.getInstance(context);
		System.out.println("db----->" + db);
	}

	public static ChatSessionDao getInstance(Context context) {
		if (instance == null) {
			instance = new ChatSessionDao(context);
		}
		return instance;
	}

	/**
	 * 插入或
	 * 
	 * @param ChatMessage
	 * @return
	 */
	public synchronized long saveOrUpdate(ChatSessionBean chatMessage) {

		if (chatMessage == null) {
			return 0;
		} else {
			if ( TextUtils.isEmpty(chatMessage.loginUser) || TextUtils.isEmpty(chatMessage.otherUser) ) {//数据异常不处理
				return 0;
			}
			List<ChatSessionBean> chatChatMessages = isExistence(chatMessage.loginUser,chatMessage.otherUser);
			//不是in 和 out 的不需要存储到 消息会话
			
			//时间戳 和 系统提醒类消息
			if (chatMessage.showType == MsgShowType.system_txt.ordinal() || chatMessage.showType == MsgShowType.timestamp.ordinal()) {
				/*if (chatChatMessages!=null && chatChatMessages.size()>0) {
					chatMessage = chatChatMessages.get(0);//替换
					chatMessage.count = 0 ;//
				}else {
					//不合法的数据
					VPLog.d(TAG, "不合法数据");
					return 0;
				}*/
				return 0;
			}
			
			ContentValues cvs = new ContentValues();
			if (chatMessage.id!=0) {
				cvs.put(ChatMessage.ID, chatMessage.id);
			}
			
			if (chatMessage.is_stick >=0) {//对置顶有操作的
				cvs.put(ChatSessionBean.IS_STICK, chatMessage.is_stick);
			}
			
			cvs.put(ChatMessage.FROM_NAME, chatMessage.from);
			cvs.put(ChatMessage.TO_NAME, chatMessage.to);
			cvs.put(ChatMessage.MSG_TYPE, chatMessage.msgType);
			cvs.put(ChatMessage.BODY, chatMessage.body);
			cvs.put(ChatMessage.TIMESTAMP, chatMessage.timestamp);
			cvs.put(ChatMessage.SHOW_TYPE, chatMessage.showType);
			cvs.put(ChatMessage.READ_STATUE, chatMessage.readStatus);
			cvs.put(ChatMessage.SEND_MSG_STATUS, chatMessage.sendStatus);
			cvs.put(ChatMessage.LOGIN_USER_NAME, chatMessage.loginUser);
			cvs.put(ChatMessage.CHAT_OTHER_USER_NAME, chatMessage.otherUser);
			
			Log.d(TAG, "session :" +chatMessage);
			
			if (chatChatMessages == null || chatChatMessages.size() == 0) { // 传送文件重复插入bug处理方法
				VPLog.d(TAG,"session insert..." + chatMessage.id);
				if (chatMessage.readStatus != ChatMessage.MsgReadStatus.read.ordinal()) {//没有读
					cvs.put(ChatSessionBean.MESSAGE_COUNT, 1);
					Log.d(TAG, "n:"+1);
				} 
				long backId = db.insert(ChatSessionBean.TABLE_NAME, null, cvs);
				return backId;
			} else {
				try {
					if (chatMessage.readStatus != ChatMessage.MsgReadStatus.read.ordinal()) {//没有读
						cvs.put(ChatSessionBean.MESSAGE_COUNT, chatChatMessages.get(0).count+1);
						Log.d(TAG, "nl:"+ (chatChatMessages.get(0).count+1));
					}else {
						cvs.put(ChatSessionBean.MESSAGE_COUNT, 0);
						Log.d(TAG, "svae nl:"+ 0);
					} 
					//Log.d("s", "chatChatMessages:"+chatChatMessages);
					
					VPLog.d(TAG,"session update..." + chatMessage.id);
					return db
							.update(ChatSessionBean.TABLE_NAME, cvs, ChatSessionBean.LOGIN_USER_NAME
									+ " = ? and "+ChatMessage.CHAT_OTHER_USER_NAME +" = ?", new String[] { 
									chatMessage.loginUser + "",chatMessage.otherUser+"" });
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
	public List<ChatSessionBean> isExistence(String loginUser ,String chatOtherUser ) {

		String sql = "select * from "+ChatSessionBean.TABLE_NAME+" where " + ChatSessionBean.LOGIN_USER_NAME + " =? and "
				+ ChatSessionBean.CHAT_OTHER_USER_NAME + " =? ";
		Cursor mCursor = db.rawQuery(sql, new String[] { loginUser + "",chatOtherUser+"" });

		List<ChatSessionBean> chatChatMessages = new ArrayList<ChatSessionBean>();

		while (mCursor.moveToNext()) {
			ChatSessionBean ChatMessage = getObject(mCursor);
			chatChatMessages.add(ChatMessage);
		}

		if (mCursor != null && !mCursor.isClosed()) {
			mCursor.close();
		}
		return chatChatMessages;
	}
	
	
	
	/**
	 * 全部设置为已读
	 */
	public boolean readAllUserChat(String myUser) {
		db.execSQL(" update " + ChatSessionBean.TABLE_NAME  
				+ " set "+ ChatMessage.READ_STATUE+" = "+ ChatSessionBean.MsgReadStatus.read.ordinal() + " , "
				+ ChatSessionBean.MESSAGE_COUNT +" = 0 " 
				+ "  where " 
				+ ChatMessage.LOGIN_USER_NAME + "=? " , new String[] {
				myUser });
		Log.d(" ChatMessage readAllUserChat", " " + myUser + " -" );
		return true;
	}
	

	/**
	 * c所有
	 * 
	 * @return
	 */
	public List<ChatSessionBean> findall() {

		Log.i("tag", "findall");
		String sql = "select * from " + ChatSessionBean.TABLE_NAME;
		Cursor mCursor = db.rawQuery(sql, new String[] {});
		// Cursor mCursor= db.query(ChatMessage.TABLE_NAME, null, null, null,
		// null, null, null);

		List<ChatSessionBean> chatChatMessages = new ArrayList<ChatSessionBean>();

		while (mCursor.moveToNext()) {
			ChatSessionBean ChatMessage = getObject(mCursor);
			chatChatMessages.add(ChatMessage);
		}

		if (mCursor != null && !mCursor.isClosed()) {
			mCursor.close();
		}
		return chatChatMessages;
	}
	
	/**
	 * c所有
	 * 
	 * @return
	 */
	public Cursor findAllCursor(String  longUser) {

		Log.i("tag", "findall");
		String sql = "select * from " + ChatSessionBean.TABLE_NAME +" where "+ ChatSessionBean.LOGIN_USER_NAME +" = ?" +" order by " + ChatSessionBean.IS_STICK +" desc , " +ChatSessionBean.TIMESTAMP +" desc ";
		Cursor mCursor = db.rawQuery(sql, new String[] {longUser});
		 
		return mCursor;
	}

	/**
	 * 清理会话
	 * @param loginId
	 * @return
	 */
	public int delete(int loginId) {
		
		Log.d("dele", "chat:del");
		String sql ="delete from " +ChatSessionBean.TABLE_NAME +" where "+ ChatSessionBean.LOGIN_USER_NAME + "="+loginId;
		//ChatSessionBean message = findById(loginId);
		//Log.d("delete", "chat:"+message);
		db.execSQL(sql);
		
		return 0;
	}
	
	public int deleteForTimestamp(long time) {
		
		Log.d("dele", "chat:del");
		String sql ="delete from " +ChatSessionBean.TABLE_NAME +" where "+ChatSessionBean.TIMESTAMP + "="+time;
		//ChatMessage message = findById(id);
		//Log.d("delete", "chat:"+message);
		db.execSQL(sql);
		
		return 0;
	}

	/**
	 * 统计未读消息数量
	 */
	public int getUnreadMsgTotalByName(String chatTo, String loginUserId) {

		String sql = "select count(*) num from +" + ChatSessionBean.TABLE_NAME
				+ "  where " + ChatMessage.CHAT_OTHER_USER_NAME + "=? and "
				+ ChatSessionBean.LOGIN_USER_NAME + "=? and "
				+ ChatSessionBean.READ_STATUE + "=? group by "
				+ ChatSessionBean.CHAT_OTHER_USER_NAME + ","
				+ ChatSessionBean.LOGIN_USER_NAME;

		Cursor mCursor = db.rawQuery(sql, new String[] { chatTo, loginUserId,
				ChatSessionBean.MsgReadStatus.unread.ordinal() + "" });
		int result = 0;
		while (mCursor.moveToNext()) {
			result = mCursor.getInt(0);
		}
		if (mCursor != null && !mCursor.isClosed()) {
			mCursor.close();
		}

		return result;
	}

	/**
	 * 获取 聊天用户最后
	 */
	public ChatSessionBean getChatUserLastMessage(String loginId, String chatUserId) {
		String sql = "select * from " + ChatSessionBean.TABLE_NAME + "  where "
				+ ChatSessionBean.LOGIN_USER_NAME + "=? and "
				+ ChatSessionBean.CHAT_OTHER_USER_NAME + "=? " + " order by "
				+ ChatSessionBean.ID + " desc ";// 不要求状态
		// String sql =
		// "select * from chat_msg  where  from_id=? and to_id=? and status =?  order by id desc ";
		// //要求状态
		Cursor mCursor = db.rawQuery(sql, new String[] { loginId, chatUserId });
		ChatSessionBean chatMsg = null;
		while (mCursor.moveToNext()) {
			chatMsg = getObject(mCursor);
			break;

		}
		if (mCursor != null && !mCursor.isClosed()) {
			mCursor.close();
		}

		return chatMsg;
	}

	/**
	 * 设置已读
	 */
	public void setReadMsg(String loginUserId, String chatUserId) {
		Log.i("test", "setReadMsg from= " + loginUserId + " to=" + chatUserId);
		String sql = "update " + ChatSessionBean.READ_STATUE + "=? from "
				+ ChatSessionBean.TABLE_NAME + " where "
				+ ChatSessionBean.LOGIN_USER_NAME + " =? and "
				+ ChatSessionBean.CHAT_OTHER_USER_NAME + " =?";
		ContentValues cvs = new ContentValues();
		cvs.put(ChatSessionBean.READ_STATUE,
				ChatSessionBean.MsgReadStatus.read.ordinal());
		cvs.put(ChatSessionBean.MESSAGE_COUNT, 0);
		try {
			db.update(ChatSessionBean.TABLE_NAME, cvs, ChatSessionBean.LOGIN_USER_NAME
					+ " = ? and " + ChatSessionBean.CHAT_OTHER_USER_NAME + "=? ",
					new String[] { loginUserId, chatUserId });
		} catch (Exception e) {
			e.printStackTrace();
		}

		// db.execSQL( sql, new String[] { ChatMessage.STATUS[1],from,to });
	}

	/**
	 * 查询总条数
	 * 
	 * @return
	 */
	public long getCount() {
		Cursor cursor = db.rawQuery("select count(*)from "
				+ ChatSessionBean.TABLE_NAME, null);
		cursor.moveToFirst();
		long count = cursor.getLong(0);
		cursor.close();
		Log.d(" ChatMessage count", count + "");
		return count;
	}

	/**
	 * 查询两 用户聊天记录数
	 */
	public long getUserChatCount(String myUser) {
		/* where (from_id=? and to_id=?) or (to_id=? and from_id=?) */
		Cursor cursor = db.rawQuery("select count(*) from "
				+ ChatSessionBean.TABLE_NAME + "  where "
				+ ChatSessionBean.LOGIN_USER_NAME + "=? "
				 , new String[] {
				myUser });
		cursor.moveToFirst();
		long count = cursor.getLong(0);
		cursor.close();
		Log.d(" ChatMessage count", count + " " + myUser + " -" );
		return count;
	}

	/**
	 * 获取未读数据总数量
	 */
	public long getUserUnReadChatCount(String myUser) {
		/* where (from_id=? and to_id=?) or (to_id=? and from_id=?) */
		Cursor cursor = db.rawQuery("select sum(msg_count) from "
				+ ChatSessionBean.TABLE_NAME + "  where "
				+ ChatSessionBean.LOGIN_USER_NAME + "=? and "
				+ ChatSessionBean.MESSAGE_COUNT + "!=0"
				 , new String[] {
				myUser });
		cursor.moveToFirst();
		long count = cursor.getLong(0);
		cursor.close();
		//Log.d(" ChatMessage count", count + " " + myUser + " -" );
		return count ;
	}
	
	/**
	 * 删除 用户聊天记录数，会话
	 */
	public boolean deleteUserChat(String myUser, String otherUser) {
		db.execSQL(" delete from " + ChatSessionBean.TABLE_NAME + "  where "
				+ ChatSessionBean.LOGIN_USER_NAME + "=? and "
				+ ChatSessionBean.CHAT_OTHER_USER_NAME + "= ? ", new String[] {
				myUser, otherUser });
		Log.d(" session deleteUserChat", " " + myUser + " -" + otherUser);
		return true;
	}
	
	/**
	 * 更新用户 msg
	 */
	public boolean updateUserChat(String myUser, String otherUser,String msg) {
//		db.execSQL(" update "  + ChatSessionBean.BODY +" =? " +
//				" from " + ChatSessionBean.TABLE_NAME + 
//				"  where " 
//				+ ChatSessionBean.LOGIN_USER_ID + "=? and "
//				+ ChatSessionBean.CHAT_OTHER_USER_NAME + "= ? ", new String[] {msg ,
//				myUser, otherUser });
		ContentValues cvs = new ContentValues();
		cvs.put(ChatMessage.BODY,
				msg);
		db.update(ChatSessionBean.TABLE_NAME, cvs, ChatSessionBean.LOGIN_USER_NAME
				+ " = ? and " + ChatSessionBean.CHAT_OTHER_USER_NAME + "=? ",
				new String[] { myUser, otherUser });
		Log.d(" session deleteUserChat", " " + myUser + " -" + otherUser);
		return true;
	}
	
	/**
	 * 查询两 用户聊天记录数
	 */
	public boolean deleteUserChat(String myUser ) {
		db.execSQL(" delete from " + ChatSessionBean.TABLE_NAME + "  where "
				+ ChatSessionBean.LOGIN_USER_NAME + "=? ", new String[] {
				myUser });
		Log.d(" session deleteUserChat", " " + myUser + " -" );
		return true;
	}
	
	
	/**
	 * 拉取离该消息id 最近的一条消息
	 */
	public List<ChatSessionBean> findPage(int loginUserId, int otherUser, int maxMsgId) {
		
		int pageSize =INIT_MSG_COUNT;
		int page = 0;
		if (pageSize < 0) {
			pageSize = INIT_MSG_COUNT;
		}

		String sql = "select * from +" + ChatSessionBean.TABLE_NAME + "  where "
				+ ChatSessionBean.CHAT_OTHER_USER_NAME + "=? and "
				+ ChatSessionBean.LOGIN_USER_NAME + "=? and "
				+ ChatSessionBean.ID +" < ? order by " + ChatSessionBean.ID
				+ " limit " + pageSize + " offset " + page * pageSize;

		Cursor mCursor = db.rawQuery(sql,
				new String[] { loginUserId+"", otherUser+"",maxMsgId+"" });
		List<ChatSessionBean> chaTotals = new ArrayList<ChatSessionBean>();

		while (mCursor.moveToNext()) {
			ChatSessionBean chatMsg = getObject(mCursor);
			chaTotals.add(chatMsg);// 添加
		}
		if (mCursor != null && !mCursor.isClosed()) {
			mCursor.close();
		}

		return chaTotals;
	}
	

	/**
	 * 升序 分页查找,用于聊天页面下拉刷新
	 */
	public List<ChatSessionBean> findPage(String loginUserId,int page, int pageSize) {

		if (pageSize < 0) {
			pageSize = INIT_MSG_COUNT;
		}

		String sql = "select * from " + ChatSessionBean.TABLE_NAME + "  where "
				+ ChatSessionBean.LOGIN_USER_NAME + "=? order by " + ChatSessionBean.ID
				+ " limit " + pageSize + " offset " + page * pageSize;

		Cursor mCursor = db.rawQuery(sql,
				new String[] {loginUserId});
		List<ChatSessionBean> chaTotals = new ArrayList<ChatSessionBean>();

		while (mCursor.moveToNext()) {
			ChatSessionBean chatMsg = getObject(mCursor);
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
	 * 根据id查找
	 * 
	 * @param id
	 * @return
	 */
	public ChatSessionBean findById(float id) {

		Cursor c = db.query(ChatSessionBean.TABLE_NAME, null, ChatSessionBean.ID
				+ " = ?", new String[] { "" + id }, null, null, null);
		ChatSessionBean fluid = null;
		while (c.moveToNext()) {
			fluid = getObject(c);
		}
		DatabaseHelper.closeCursor(c);
		return fluid;
	}

	// 赋值
	public ChatSessionBean getObject(Cursor c) {
		ChatSessionBean chatMessage = new ChatSessionBean();
		
		chatMessage.id = c.getInt(c.getColumnIndex(ChatMessage.ID));
		chatMessage.from = c.getString(c.getColumnIndex(ChatMessage.FROM_NAME));
		chatMessage.to = c.getString(c.getColumnIndex(ChatMessage.TO_NAME));
		chatMessage.body = c.getString(c.getColumnIndex(ChatMessage.BODY));
		chatMessage.msgType = c.getInt(c.getColumnIndex(ChatMessage.MSG_TYPE));
		chatMessage.sendStatus = c.getInt(c
				.getColumnIndex(ChatMessage.SEND_MSG_STATUS));
		chatMessage.readStatus = c.getInt(c
				.getColumnIndex(ChatMessage.READ_STATUE));
		chatMessage.showType = c
				.getInt(c.getColumnIndex(ChatMessage.SHOW_TYPE));
		
		chatMessage.loginUser = c.getString(c
				.getColumnIndex(ChatMessage.LOGIN_USER_NAME));
		chatMessage.otherUser = c.getString(c
				.getColumnIndex(ChatMessage.CHAT_OTHER_USER_NAME));
		chatMessage.timestamp = c.getLong(c
				.getColumnIndex(ChatMessage.TIMESTAMP));
		chatMessage.is_stick = c.getInt(c.getColumnIndex(ChatSessionBean.IS_STICK));
		chatMessage.count =  c.getInt(c.getColumnIndex(ChatSessionBean.MESSAGE_COUNT));
		chatMessage.parseBody();

		UserInfo info;
		info = mUserInfoDao.findByXmppUser(chatMessage.otherUser);
		 
		chatMessage.fromUserInfo = info;

		return chatMessage;

	}

	/***
	 * 查找以前聊过天的内容 查找消息根据login user id 和 消息内容
	 * 
	 * @param id
	 * @param string
	 */
	public List<ChatSessionBean> findMsgByFromAndBody(String id, String string) {

		if (string != null) {
			string = string.replace("%", "[%]");
			string = string.replace("_", "[_]");
		}
		/* where (from_id=? and to_id=?) or (to_id=? and from_id=?) */
		Cursor cursor = db.rawQuery("select * from " + ChatSessionBean.TABLE_NAME
				+ "  where " + ChatSessionBean.LOGIN_USER_NAME + "=? and "
				+ ChatSessionBean.BODY + " like '%" + string
				+ "%' order by id desc ", new String[] { id });
		List<ChatSessionBean> chatChatMessages = new ArrayList<ChatSessionBean>();

		while (cursor.moveToNext()) {
			ChatSessionBean chatm = getObject(cursor);
			if (chatm != null) {
				chatChatMessages.add(chatm);
			}

		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		Log.d(" ChatMessage count", id + " " + string);
		return chatChatMessages;
	}

}
