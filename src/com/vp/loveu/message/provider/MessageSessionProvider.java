package com.vp.loveu.message.provider;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.vp.loveu.message.bean.ChatSessionBean;
import com.vp.loveu.message.db.ChatSessionDao;
import com.vp.loveu.message.db.DatabaseHelper;

/**
 * 消息内容提供者
 * @author tanping
 * 2015-11-2
 */
public class MessageSessionProvider extends ContentProvider {

	public static final String AUTHORITIES ="com.vp.loveu.message.provider";
	
	private SQLiteDatabase db;
	ChatSessionDao mChatSessionDao;
	
	private static final UriMatcher URIMATCHER;
	public static final  String ALL = "all";
	public static final Uri CONTENT_MSG_SESSION_URI = Uri.parse("content://" + AUTHORITIES + "/" + ALL);

	public static final int ALL_ITEMS = 0x1;
	public static final int ALL_ITEMS_USER = 0x2;

	static {
		URIMATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URIMATCHER.addURI(AUTHORITIES, ALL, ALL_ITEMS);
		URIMATCHER.addURI(AUTHORITIES, ALL + "/#", ALL_ITEMS_USER);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (values ==null) {
			return null;
		}
		String userID = values.getAsString(ChatSessionBean.LOGIN_USER_NAME);
		if (userID == null) {
			return null;
		}
		 
		getContext().getContentResolver().notifyChange(uri, null);	
		return uri;
	}

	@Override
	public boolean onCreate() {
		db = DatabaseHelper.getInstance(getContext()).getWritableDatabase();
		mChatSessionDao = ChatSessionDao.getInstance(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		// 根据 置顶 和 未读消息数量排序
		/*MineInfo loginUser = LoginStatus.getLoginInfo(getContext()); 
		if (loginUser ==null) {
			loginUser = new MineInfo();
		}*/
		Cursor c = mChatSessionDao.findAllCursor("test1");
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		Log.i("tag", "update update update ContentValues" );
		getContext().getContentResolver().notifyChange(uri, null);	
		return 0;
	}

	 

}
