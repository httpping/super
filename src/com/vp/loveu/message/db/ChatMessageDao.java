package com.vp.loveu.message.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.vp.loveu.message.bean.ChatMessage;
import com.vp.loveu.message.bean.ChatMessage.MsgSendStatus;
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
public class ChatMessageDao implements Serializable {

	private static ChatMessageDao instance;
	private SQLiteDatabase db;
	public static int INIT_MSG_COUNT = 25;// 25条消息
	
	ChatSessionDao sessionDao;
	UserInfoDao mUserInfoDao;

	private ChatMessageDao(Context context) {
		db = DatabaseHelper.getInstance(context).getWritableDatabase();
		sessionDao = ChatSessionDao.getInstance(context);
		mUserInfoDao = UserInfoDao.getInstance(context);
		System.out.println("db----->" + db);
	}

	public static ChatMessageDao getInstance(Context context) {
		if (instance == null) {
			instance = new ChatMessageDao(context);
		}
		return instance;
	}

	/**
	 * 插入或
	 * 
	 * @param ChatMessage
	 * @return
	 */
	public synchronized long saveOrUpdate(ChatMessage chatMessage) {

		if (chatMessage == null) {
			return 0;
		} else {
			
			
/*			 ChatMessage.ID  + " int," +
			    	 ChatMessage.FROM_NAME+" text," +
			    	 ChatMessage.TO_NAME+" text , " +
			    	 ChatMessage.MSG_TYPE+" int," +
			    	 ChatMessage.BODY +" text," + 
			    	 ChatMessage.TIMESTAMP +" long," +
			    	 ChatMessage.SHOW_TYPE +" int," +
			    	 ChatMessage.READ_STATUE +" int ," +
			    	 ChatMessage.SEND_MSG_STATUS+" int," +
			    	 ChatMessage.LOGIN_USER_ID+" text ," +
			    	 ChatMessage.CHAT_OTHER_USER_ID+" int ,"+*/
			
			ContentValues cvs = new ContentValues();
			if (chatMessage.id!=0) {
				cvs.put(ChatMessage.ID, chatMessage.id);
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
			 
			
			//用来处理session 会话
			ChatSessionBean sessionBean = ChatSessionBean.chatToSession(chatMessage);
			//if (chatMessage.showType == ChatMessage.MsgShowType.in.ordinal() || chatMessage.showType==ChatMessage.MsgShowType.out.ordinal()) {//不是图片和系统消息
			sessionDao.saveOrUpdate(sessionBean);//保存session
			//}
			
			System.out.println(chatMessage);
			List<ChatMessage> chatChatMessages =null ;
			if (chatMessage.id!=0) {
				chatChatMessages = isExistence(chatMessage.id);
			}
			VPLog.d("dao", "chatChatMessages:"+chatChatMessages);
			if (chatChatMessages == null || chatChatMessages.size() == 0 ) { // 传送文件重复插入bug处理方法
				System.out.println("insert msg..." + chatMessage.id);
				long backId = db.insert(ChatMessage.TABLE_NAME, null, cvs);
				return backId;
			} else {
				try {
					System.out.println("update msg..." + chatMessage.id);
					return db
							.update(ChatMessage.TABLE_NAME, cvs, ChatMessage.ID
									+ " = ?", new String[] { String
									.valueOf(chatMessage.id + "") });
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
	public List<ChatMessage> isExistence(long id) {
		VPLog.d("dao", "isExistence:"+id);
		String sql = "select * from "+ChatMessage.TABLE_NAME+" where " + ChatMessage.ID + " =? ";
		Cursor mCursor = db.rawQuery(sql, new String[] { id + "" });

		List<ChatMessage> chatChatMessages = new ArrayList<ChatMessage>();

		while (mCursor.moveToNext()) {
			ChatMessage ChatMessage = getObject(mCursor);
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
	public List<ChatMessage> findall() {

		Log.i("tag", "findall");
		String sql = "select * from " + ChatMessage.TABLE_NAME;
		Cursor mCursor = db.rawQuery(sql, new String[] {});
		// Cursor mCursor= db.query(ChatMessage.TABLE_NAME, null, null, null,
		// null, null, null);

		List<ChatMessage> chatChatMessages = new ArrayList<ChatMessage>();

		while (mCursor.moveToNext()) {
			ChatMessage ChatMessage = getObject(mCursor);
			chatChatMessages.add(ChatMessage);
		}

		if (mCursor != null && !mCursor.isClosed()) {
			mCursor.close();
		}
		return chatChatMessages;
	}

	public int delete(int id) {
		
		Log.d("dele", "chat:del");
		String sql ="delete from " +ChatMessage.TABLE_NAME +" where "+ChatMessage.ID + "="+id;
		ChatMessage message = findById(id);
		Log.d("delete", "chat:"+message);
		db.execSQL(sql);
		
		return 0;
	}
	
	public int deleteForTimestamp(long time) {
		
		Log.d("dele", "chat:del");
		String sql ="delete from " +ChatMessage.TABLE_NAME +" where "+ChatMessage.TIMESTAMP + "="+time;
		//ChatMessage message = findById(id);
		//Log.d("delete", "chat:"+message);
		db.execSQL(sql);
		
		return 0;
	}

	/**
	 * 统计未读消息数量
	 */
	public int getUnreadMsgTotalByName(String chatTo, String loginUserId) {

		String sql = "select count(*) num from +" + ChatMessage.TABLE_NAME
				+ "  where " + ChatMessage.CHAT_OTHER_USER_NAME + "=? and "
				+ ChatMessage.LOGIN_USER_NAME + "=? and "
				+ ChatMessage.READ_STATUE + "=? group by "
				+ ChatMessage.CHAT_OTHER_USER_NAME + ","
				+ ChatMessage.LOGIN_USER_NAME;

		Cursor mCursor = db.rawQuery(sql, new String[] { chatTo, loginUserId,
				ChatMessage.MsgReadStatus.unread.ordinal() + "" });
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
	public ChatMessage getChatUserLastMessage(String loginId, String chatUserId) {
		String sql = "select * from " + ChatMessage.TABLE_NAME + "  where "
				+ ChatMessage.LOGIN_USER_NAME + "=? and "
				+ ChatMessage.CHAT_OTHER_USER_NAME + "=? " + " order by "
				+ ChatMessage.ID + " desc ";// 不要求状态
		// String sql =
		// "select * from chat_msg  where  from_id=? and to_id=? and status =?  order by id desc ";
		// //要求状态
		Cursor mCursor = db.rawQuery(sql, new String[] { loginId, chatUserId });
		ChatMessage chatMsg = null;
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
		String sql = "update " + ChatMessage.READ_STATUE + "=? from "
				+ ChatMessage.TABLE_NAME + " where "
				+ ChatMessage.LOGIN_USER_NAME + " =? and "
				+ ChatMessage.CHAT_OTHER_USER_NAME + " =?";
		ContentValues cvs = new ContentValues();
		cvs.put(ChatMessage.READ_STATUE,
				ChatMessage.MsgReadStatus.read.ordinal());
		try {
			db.update(ChatMessage.TABLE_NAME, cvs, ChatMessage.LOGIN_USER_NAME
					+ " = ? and " + ChatMessage.CHAT_OTHER_USER_NAME + "=? ",
					new String[] { loginUserId, chatUserId });
			sessionDao.setReadMsg(loginUserId, chatUserId);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
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
				+ ChatMessage.TABLE_NAME, null);
		cursor.moveToFirst();
		long count = cursor.getLong(0);
		cursor.close();
		Log.d(" ChatMessage count", count + "");
		return count;
	}

	/**
	 * 查询两 用户聊天记录数
	 */
	public long getUserChatCount(String myUser, String otherUser) {
		/* where (from_id=? and to_id=?) or (to_id=? and from_id=?) */
		Cursor cursor = db.rawQuery("select count(*) from "
				+ ChatMessage.TABLE_NAME + "  where "
				+ ChatMessage.LOGIN_USER_NAME + "=? and "
				+ ChatMessage.CHAT_OTHER_USER_NAME + "= ? ", new String[] {
				myUser, otherUser });
		cursor.moveToFirst();
		long count = cursor.getLong(0);
		cursor.close();
		Log.d(" ChatMessage count", count + " " + myUser + " -" + otherUser);
		return count;
	}

	/**
	 * 查询两 用户聊天记录数
	 */
	public boolean deleteUserChat(String myUser, String otherUser) {
		db.execSQL(" delete from " + ChatMessage.TABLE_NAME + "  where "
				+ ChatMessage.LOGIN_USER_NAME + "=? and "
				+ ChatMessage.CHAT_OTHER_USER_NAME + "= ? ", new String[] {
				myUser, otherUser });
		
		sessionDao.updateUserChat(myUser, otherUser,"");
		
		Log.d(" ChatMessage deleteUserChat", " " + myUser + " -" + otherUser);
		return true;
	}
	
	/**
	 * 查询两 用户聊天记录数
	 */
	public boolean deleteUserChat(String myUser) {
		db.execSQL(" delete from " + ChatMessage.TABLE_NAME + "  where "
				+ ChatMessage.LOGIN_USER_NAME + "=? " , new String[] {
				myUser });
		sessionDao.deleteUserChat(myUser);
		Log.d(" ChatMessage deleteUserChat", " " + myUser + " -" );
		return true;
	}
	

	
	/**
	 * 拉取离该消息id 最近的一条消息
	 */
	public List<ChatMessage> findPage(int loginUserId, int otherUser, int maxMsgId) {
		
		int pageSize =INIT_MSG_COUNT;
		int page = 0;
		if (pageSize < 0) {
			pageSize = INIT_MSG_COUNT;
		}

		String sql = "select * from +" + ChatMessage.TABLE_NAME + "  where "
				+ ChatMessage.CHAT_OTHER_USER_NAME + "=? and "
				+ ChatMessage.LOGIN_USER_NAME + "=? and "
				+ ChatMessage.ID +" < ? order by " + ChatMessage.ID
				+ " limit " + pageSize + " offset " + page * pageSize;

		Cursor mCursor = db.rawQuery(sql,
				new String[] { loginUserId+"", otherUser+"",maxMsgId+"" });
		List<ChatMessage> chaTotals = new ArrayList<ChatMessage>();

		while (mCursor.moveToNext()) {
			ChatMessage chatMsg = getObject(mCursor);
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
	public ChatMessage findLastTimeStamp(String loginUserId, String otherUser) {

		String sql = "select * from " + ChatMessage.TABLE_NAME + "  where "
				+ ChatMessage.CHAT_OTHER_USER_NAME + "=? and "
				+ ChatMessage.LOGIN_USER_NAME + "=? and "+
			    ChatMessage.SHOW_TYPE+" = "+ ChatMessage.MsgShowType.timestamp.ordinal()+" order by " + ChatMessage.TIMESTAMP + " desc ";
		VPLog.d("chatdao", "sql:"+sql);		 

		Cursor mCursor = db.rawQuery(sql,
				new String[] { otherUser+"",loginUserId+"" });
		List<ChatMessage> chaTotals = new ArrayList<ChatMessage>();

		while (mCursor.moveToNext()) {
			ChatMessage chatMsg = getObject(mCursor);
			try {
				//Long.parseLong(chatMsg.body); //能解析就是时间
				if (mCursor != null && !mCursor.isClosed()) {
					mCursor.close();
				}
				return chatMsg;
			} catch (Exception e) {
			}finally{
				if (mCursor!=null) {
					mCursor.close();
				}
			}
		}
		if (mCursor != null && !mCursor.isClosed()) {
			mCursor.close();
		}

		return null;
	}
	
	

	

	/**
	 * 升序 分页查找,用于聊天页面下拉刷新
	 */
	public List<ChatMessage> findPage(String loginUser, String otherUser,
			int page, int pageSize) {

		if (pageSize < 0) {
			pageSize = INIT_MSG_COUNT;
		}

		String sql = "select * from " + ChatMessage.TABLE_NAME + "  where "
				+ ChatMessage.CHAT_OTHER_USER_NAME + "=? and "
				+ ChatMessage.LOGIN_USER_NAME + "=? order by " + ChatMessage.TIMESTAMP
				+ " limit " + pageSize + " offset " + page * pageSize;

		Cursor mCursor = db.rawQuery(sql,
				new String[] { otherUser,loginUser });
		List<ChatMessage> chaTotals = new ArrayList<ChatMessage>();

		while (mCursor.moveToNext()) {
			ChatMessage chatMsg = getObject(mCursor);
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
	public int getCountPage(String loginXmppUser,String otherXmppUser) {
		long count = getUserChatCount(loginXmppUser+"",otherXmppUser+"");
		int allPage = (int) (count / INIT_MSG_COUNT);
		if (allPage>0 && count % INIT_MSG_COUNT ==0) {//整页，最后一页为空
			allPage--;
		}
		/*Log.d("count page", " count page "+count +" page" + allPage + " " + getCount() + " "
				+ INIT_MSG_COUNT);*/
		return allPage;
	}
	

	/**
	 * 根据id查找
	 * 
	 * @param id
	 * @return
	 */
	public ChatMessage findById(int id) {

		Cursor c = db.query(ChatMessage.TABLE_NAME, null, ChatMessage.ID
				+ " = ?", new String[] { "" + id }, null, null, null);
		ChatMessage fluid = null;
		while (c.moveToNext()) {
			fluid = getObject(c);
		}
		DatabaseHelper.closeCursor(c);
		return fluid;
	}

	// 赋值
	private ChatMessage getObject(Cursor c) {
		ChatMessage chatMessage = new ChatMessage();
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
		
		if (chatMessage.sendStatus == MsgSendStatus.send.ordinal()) {//防止发送中退出无法接受状态更新
			chatMessage.sendStatus = MsgSendStatus.success.ordinal();
		}

		
		UserInfo info;
		if (chatMessage.showType <= MsgShowType.in_map.ordinal()) {//接收到的消息
			 info = mUserInfoDao.findByXmppUser(chatMessage.otherUser);
		}else {
			info = mUserInfoDao.findByXmppUser(chatMessage.loginUser);
		}
		chatMessage.fromUserInfo = info;
		
		chatMessage.parseBody();
	// Log.d(" ChatMessage","getObject"+chatMessage);

		return chatMessage;

	}

	/***
	 * 查找以前聊过天的内容 查找消息根据login user id 和 消息内容
	 * 
	 * @param id
	 * @param string
	 */
	public List<ChatMessage> findMsgByFromAndBody(String id, String string) {

		if (string != null) {
			string = string.replace("%", "[%]");
			string = string.replace("_", "[_]");
		}
		/* where (from_id=? and to_id=?) or (to_id=? and from_id=?) */
		Cursor cursor = db.rawQuery("select * from " + ChatMessage.TABLE_NAME
				+ "  where " + ChatMessage.LOGIN_USER_NAME + "=? and "
				+ ChatMessage.BODY + " like '%" + string
				+ "%' order by id desc ", new String[] { id });
		List<ChatMessage> chatChatMessages = new ArrayList<ChatMessage>();

		while (cursor.moveToNext()) {
			ChatMessage chatm = getObject(cursor);
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
