package com.vp.loveu.message.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jivesoftware.smackx.chatstates.ChatState;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.VpHttpClient;
import com.me.nereo.multi_image_selector.MultiImageSelectorActivity;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.bean.UserTriggerExp;
import com.vp.loveu.comm.ShowImagesViewPagerActivity;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.message.adapter.ChatListAdapter;
import com.vp.loveu.message.bean.ChatMessage;
import com.vp.loveu.message.bean.ChatMessage.MsgReadStatus;
import com.vp.loveu.message.bean.ChatMessage.MsgSendStatus;
import com.vp.loveu.message.bean.ChatMessage.MsgShowType;
import com.vp.loveu.message.bean.ChatMessage.MsgType;
import com.vp.loveu.message.bean.ChatSessionBean;
import com.vp.loveu.message.bean.SendImagePackage;
import com.vp.loveu.message.bean.UserInfo;
import com.vp.loveu.message.db.ChatMessageDao;
import com.vp.loveu.message.db.ChatSessionDao;
import com.vp.loveu.message.db.UserInfoDao;
import com.vp.loveu.message.receiver.ChatBroadcast;
import com.vp.loveu.message.utils.BroadcastType;
import com.vp.loveu.message.view.ChatInputView;
import com.vp.loveu.message.view.ChatInputView.OnSendListener;
import com.vp.loveu.service.XmppService;
import com.vp.loveu.service.XmppService.XmppServiceBinder;
import com.vp.loveu.service.util.SendPacketRun.SendPacketListener;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.ScreenUtils;
import com.vp.loveu.util.ToastUtil;
import com.vp.loveu.util.VPLog;

/**
 * 私聊
 * @author tanping
 *
 */
public class PrivateChatActivity extends VpActivity implements OnRefreshListener,OnItemLongClickListener,OnSendListener{
	
	public static final int SELECT_IMGAGE_REQUEST = 12345;//选择图片通过文件
	public static final int SELECT_AREA_REQUEST = 912;//位置信息

	public static final int SEND_IMGE_REQUEST = 12346;// xmpp 发送图片类消息

	
	public static final String TAG = "chat";
	public static final String CHAT_USER_ID ="chat_user_id";//聊天用户
	public static final String CHAT_USER_HEAD_IMAGE ="chat_user_head_image";//聊天用户
	public static final String CHAT_USER_NAME="chat_user_name";//聊天用户
	public static final String CHAT_XMPP_USER="chat_xmpp_user";//聊天xmpp user name

	
	public static final String CHAT_TYPE = "chat_type";//聊天类型，传人聊天类型需要做特殊操作，其他不用


	
	public static final long TIEMSTREAP_TIME =10*60*1000;//10分钟
	public long lastTimestamp;//最后一次添加时间戳的时间

	public static final int CHAT_LIST_MAX_NUMBER=300;//聊天用户列表最多放300条进入messages

	public String chatXmppUser ;
	public String chatHeadImg;
	public String chatUserName ;
	public String chatUserId; 
	
	public long lastMsgId;// 该聊天界面中最后一个消息id
	
	public int chatType ;
	
	private ChatBroadcast mChatBroadcast;//接收消息广播
	
	
	ChatListAdapter adapter;
	ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;

	List<ChatMessage> messages ;
	
	UserInfo loginUserInfo;
	UserInfo chatUserInfo;
	int curPage;
	ChatMessageDao mChatMessageDao;
	UserInfoDao mUserInfoDao;
	
	
	ChatInputView mChatInputView;
	XmppServiceBinder mBinder;
	ServiceConnection mServiceConnection;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_chat_activity_main);
		
		 /* if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
	             getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	             getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		  }*/
		
		
		initView();
		initData();
		Intent intent = new Intent(this,XmppService.class);
		
	    
        mServiceConnection = new ServiceConnection() {
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				VPLog.d("main", "onServiceConnected");
				mBinder = (XmppServiceBinder) service;
				
			}
		};
        
        bindService(intent,mServiceConnection , BIND_AUTO_CREATE);
	    
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		finish();
		startActivity(intent);
		
	}
	
	private LinearLayout parentLayout;
	private void initView() {
		
		
		initPublicTitle();
		mPubTitleView.mBtnLeft.setText("");
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		//mPubTitleView.mBtnRight.setBackgroundResource(R.drawable.message_title_more);
		ImageView more = new ImageView(this);
		more.setImageResource(R.drawable.message_title_more);
		more.setLayoutParams(mPubTitleView.mBtnRight.getLayoutParams());
		mPubTitleView.mRightLayout.addView(more);
		//mPubTitleView.mBtnRight.setBackgroundResource(R.drawable.icon_scan);
		mPubTitleView.mTvTitle.setText("与test2聊天");
		mPubTitleView.mRightLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PrivateChatActivity.this,PersonSettingActivity.class);
				intent.putExtra(PersonSettingActivity.PARAMS_KEY, chatUserInfo);
				startActivity(intent);
				
			}
		});
		
		parentLayout = (LinearLayout) findViewById(R.id.list_parent);
		checkKeyboardHeight(parentLayout);
		
		messages = new LinkedList<ChatMessage>();
		mChatInputView = (ChatInputView) findViewById(R.id.chat_message_input);
		mChatInputView.setSendClickListener(this);//发送监听器
		mChatInputView.parentLayout = parentLayout;
		
		listView = (ListView) findViewById(R.id.chat_msgs_listview);
		adapter = new ChatListAdapter(this, messages, listView);
		listView.setAdapter(adapter);
		/*listView.setMode(Mode.PULL_FROM_START);
		listView.setOnRefreshListener(this);*/
		listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		listView.setOnItemLongClickListener(this);
		listView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				try {
					mChatInputView.hideEditMode();
				} catch (Exception e) {
					e.printStackTrace();
				}

				return false;
			}
		});
		//listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
		
		
		//findview
		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
		//设置卷内的颜色
		swipeRefreshLayout.setColorSchemeResources(R.color.green_public,
				android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
		//设置下拉刷新监听
		swipeRefreshLayout.setOnRefreshListener(this);

		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.i("chat", "p:"+arg2 );
				if (messages!=null && arg2>0) {
					ChatMessage msg = messages.get(arg2);
					if (msg.sendStatus == ChatMessage.MsgSendStatus.fail.ordinal()) {//属于失败的范畴
						 if (msg.msgType == MsgType.img.getValue() || msg.msgType == MsgType.maps.getValue() ) {//图片 和 地图
							if (msg.imgUrl.startsWith("http")) {//已经上传成功
								msg.sendStatus = MsgSendStatus.send.ordinal();
								msg.sendImgUrl = msg.imgUrl;
								onSendImage(msg);
							}else {
			        			SendImagePackage send = new SendImagePackage(mClient, msg.imgUrl,mHandler);
			        			send.chatMessage = msg;
								msg.sendStatus = MsgSendStatus.send.ordinal();
			        			send.send();
								//adapter.notifyDataSetChanged();
			        			updateView(msg, false);
							}
						}else if (msg.msgType == MsgType.txt.getValue()) {//重发文本消息
							msg.sendStatus = MsgSendStatus.send.ordinal();
							VPLog.d(TAG, "send:"+msg);
							sendMessage(msg);
							updateView(msg, false);
						}  
						 
						 return;
					}
					
					if (msg.msgType == MsgType.img.getValue()) {
						Intent intent = new Intent(PrivateChatActivity.this,ShowImagesViewPagerActivity.class);
						ArrayList<String> results = ChatMessage.findImgages(messages);
						int position = results.indexOf(msg.imgUrl);
						VPLog.d(TAG, "p:" +position);
						intent.putStringArrayListExtra("images",results);
						intent.putExtra("position", position);
						startActivity(intent);
					}else if (msg.msgType == MsgType.maps.getValue()) {//地图
						Intent intent = new Intent(PrivateChatActivity.this,LocationPostionActivity.class);
						intent.putExtra(LocationPostionActivity.BD_MAPS_LAT, msg.lat);
						intent.putExtra(LocationPostionActivity.BD_MAPS_LONG, msg.lon);
						startActivity(intent);
					}
				}
			}
		});
	}



	private void initData() {
		
		//获取用户信息
		 LoginUserInfoBean loginuser = LoginStatus.getLoginInfo();
		if (loginuser== null) {
			finish();
			return;
		}
		mChatMessageDao =ChatMessageDao.getInstance(this);
		loginUserInfo = new UserInfo();
		loginUserInfo.xmppUser =loginuser.getXmpp_user().toLowerCase();
		loginUserInfo.headImage = loginuser.getPortrait();
		loginUserInfo.userName = loginuser.getNickname();
		loginUserInfo.userId = loginuser.getUid()+"";
		UserInfoDao.getInstance(this).saveOrUpdate(loginUserInfo);//保存数据库
		
		
		chatXmppUser = getIntent().getStringExtra(CHAT_XMPP_USER); // "yh_B8AxNhqZ40-l3wA8yrqbuA".toLowerCase();//getIntent().getIntExtra(CHAT_USER_ID, -1);
		chatUserName = getIntent().getStringExtra(CHAT_USER_NAME);
		chatHeadImg = getIntent().getStringExtra(CHAT_USER_HEAD_IMAGE);
		chatUserId = getIntent().getStringExtra(CHAT_USER_ID);
		if (chatUserId ==null) {
			int uid =getIntent().getIntExtra(CHAT_USER_ID, -1);
			if (uid >0) {
				chatUserId = uid +"";
			}
		}
		if (!TextUtils.isEmpty(chatXmppUser)) {
			chatXmppUser = chatXmppUser.toLowerCase();
		}
		chatUserInfo =new UserInfo();
		//chatXmppUser = "yh_efe8pvhv6ued2icr_hwryq".toLowerCase();
		chatUserInfo.xmppUser = (chatXmppUser+"").toLowerCase();
		chatUserInfo.userName = chatUserName +"";
		chatUserInfo.headImage = chatHeadImg +"";
		chatUserInfo.userId = chatUserId +"";
		
		if (TextUtils.isEmpty(chatUserId) || TextUtils.isEmpty(chatXmppUser)) {
			ToastUtil.showToast(this, "用户信息不全", 0);
			finish();
			return;
		}
		
		if (chatUserInfo.xmppUser.equals(loginuser.getXmpp_user())) {
			ToastUtil.showToast(this, "不能和自己对话", 0);
			finish();
			return ;
		}
		UserInfoDao.getInstance(this).saveOrUpdate(chatUserInfo);//保存数据库
		
		mPubTitleView.mTvTitle.setText(chatUserName);
		
		/*if (chatUserId==null) {
			ToastUtil.showToast(this, "请传人正确的用户信息", 0);
		}*/
		
		registerBroad();//注册接收广播
		
		VPLog.d("dao",  mChatMessageDao.findById(1) +"");
		
		new AsyncTask<String, String, String>() {

			@Override
			protected String doInBackground(String... params) {
				mClient = new VpHttpClient(PrivateChatActivity.this);
				curPage =  mChatMessageDao.getCountPage(loginUserInfo.xmppUser,chatXmppUser);
				List<ChatMessage> cs = findPage();
				if (cs !=null && cs.size() <10 && curPage>=0) {//数量太少，多拉取一页
					findPage();
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				updateView();
				setRead();
				addTimeSteamp(0);
			}
		}.execute();
		
		
		 //获取用户聊天会话，决定是否需要上传积分
		 List<ChatSessionBean> beans = ChatSessionDao.getInstance(this).isExistence(loginUserInfo.xmppUser, chatUserInfo.xmppUser);
		 VPLog.d(TAG, "beans:" +beans);
		 if (beans == null || beans.size() == 0) {
			 VPLog.d(TAG, "积分");
			 new UserTriggerExp().triggerExp(5001,loginuser.getUid()+"",chatUserInfo.userId,null);//触发积分	
		 }
		 
 
	}



	
	/**
	 * 分页查找
	 * @return
	 */
	public boolean isrefresh = false;
	public List<ChatMessage> findPage(){
		if (curPage < 0) {
			swipeRefreshLayout.setRefreshing(false);
			swipeRefreshLayout.setRefreshing(false);
			if (isrefresh) {
				Toast.makeText(this, "没有更多消息", 0).show();
			}
			return new LinkedList<ChatMessage>();
		}
		Log.d("chat", "cur:"+curPage);
		final List<ChatMessage> chats = mChatMessageDao.findPage(loginUserInfo.xmppUser+"", chatXmppUser+"", curPage, -1);//
		curPage--;
		
		for (int i = 0; i < chats.size(); i++) {
			VPLog.d(TAG, chats.get(i).toString());
		}
		/*ChatMessage s = mChatMessageDao.findById(57);
		VPLog.d(TAG, "test:" +s) ;*/
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
//				Log.d("chat", "chats :" +chats);
				if (messages.size() ==0) {
					messages.addAll(chats);
				}else {
					messages.addAll(0,chats);
				}
				//int itemP = listView.getSelectedItemPosition();
				swipeRefreshLayout.setRefreshing(false);
				//int itemP = listView.getSelectedItemPosition();
				//adapter.notifyDataSetChanged();
				listView.setSelection(chats.size());
				Log.d("tag", "size :" + chats.size());
			}
		});
		
	
		return chats;
	}


	
	/**
	 * 注册消息监听广播
	 */
	private void registerBroad(){
		mChatBroadcast = new ChatBroadcast(mHandler, chatXmppUser);
	    IntentFilter filter = new IntentFilter();
	    filter.addAction(BroadcastType.PRIVATE_CHAT_RECEVIE +chatXmppUser);
	    VPLog.d(TAG, "registerBroad:" +BroadcastType.PRIVATE_CHAT_RECEVIE +chatXmppUser);
	    registerReceiver(mChatBroadcast, filter);
	}
	/**
	 * 注销广播
	 */
	private void unRegisterBroad(){
		if (mChatBroadcast!=null ) {
			try {
				unregisterReceiver(mChatBroadcast);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static final int RECEIVE_MSG =200;//接收到消息
	public static final int SEND_MSG =201;//发送消息
	public static final int REQUEST_USER_INFO =202;//获取用户信息
	public static final int REMIND_INFO =203;//提醒类的消息
//	public static final int TIMESTAMP_INFO =204;//时间戳

	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case RECEIVE_MSG:
			 	ChatMessage message =  (ChatMessage) msg.obj;
			 	//Log.d(TAG, message+"");
			 	if (message.msgType == MsgType.clear_all_msg.getValue()) {//清理
					messages.clear();
					updateView();
					return ;
				}
			 	messages.add(message);
			 	clearMessages();//优化内存
				addTimeSteamp(messages.size());
			 	updateView();
			 	//mInputView.setUserdEable(true);//可以聊天了
				break;
			case SEND_MSG: //发送消息返回了
				break;
			case REQUEST_USER_INFO: //获取用户状态信息
				 if (msg.obj!=null) {
				 } 
				 break;
			case SEND_IMGE_REQUEST://发送图片
				onSendImage((ChatMessage) msg.obj);
				break;
			default:
				break;
			}
		};
	};
	
	
	
	public void updateView(){
		Log.d("chats", "update view:"+ messages.size());
		adapter.notifyDataSetChanged();
		//listView.smoothScrollToPosition(messages.size());
	}
	/**
	 * 
	 * @param message  修改了相应的message
	 * @param isChange 位置不变
	 */
	public void updateView(ChatMessage message,boolean isChange){
		
		
		if (message.viewUpdate!=null) {
			message.viewUpdate.drawView();
			return;
		}
		
		
		adapter.notifyDataSetChanged();
		
		
	}
	
	
	/**
	 * 清理内存中的数据
	 */
	public void clearMessages(){
		
		while (messages.size()> CHAT_LIST_MAX_NUMBER) {
			messages.remove(0);
		}
	}
	/**
	 * 清理本地缓存 cache中的数据
	 */
	public void clearCacheMessages(){
		if (loginUserInfo==null) {
			finish();
			return;
		}
		messages.clear();
		lastTimestamp = 0 ; //重头计算
		mChatMessageDao.deleteUserChat(loginUserInfo.userId+"", chatXmppUser+"");
		updateView();
		  
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		unRegisterBroad();
		if (mServiceConnection !=null && mBinder!=null) {
		    unbindService(mServiceConnection);
		}
	}
	@Override
	protected void onStop() {
		super.onStop();
		
	}
	
	

	/**
	 * 关闭软件盘
	 */
	public void closeSoftKeyword() {
	}


 
	
	@Override
	public void finish() {
		super.finish();
		
		setRead();
		if (mHandler!=null) {
			//mHandler.removeMessages(TIMESTAMP_INFO);
		}
		
	}
 

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		
		if (messages== null || messages.size()  <arg2-1) {
			return true;
		}
		ChatMessage message = messages.get(arg2-1);
		//不属于 in 和 out 的消息不能删
//		if (message.showType == ChatMessage.MsgShowType.in.ordinal() || message.showType == ChatMessage.MsgShowType.out.ordinal()) {
//			//@null
//		}else {
//			return true;
//		}
		String format = "确定删除此条消息吗？";
	   // delOrNot(format,arg2);
	    return true;
	}
	
	  /**
     * 删除对话框
     * @param position 要删除的数据
     * @param title 对话框title
     */
    public void delOrNot(String title,final int pos) {}
    
    

    /**
     * 重发
     * @param title
     */
  public void errorReSend(final ChatMessage msg) {}
  
  
  
  
  /**
   * 添加时间戳
   * @author tanping
   *
   */
  class MyAddTimeTask extends AsyncTask<Integer, String, ChatMessage>{

	@Override
	protected ChatMessage doInBackground(Integer... arg0) {
		Log.d("chat", "addTimeSteamp");
		int postion =  arg0[0];
		  if (messages!=null && messages.size()>0) {//让所有的内容置为已读
				 //ChatMessage readMe = messages.get(messages.size()-1);
				 //readMe.readStatus = ChatMessage.MsgReadStatus.read.ordinal();
				 //mChatMessageDao.saveOrUpdate(readMe);
			  
				 ChatMessage temp = mChatMessageDao.findLastTimeStamp(loginUserInfo.xmppUser,chatXmppUser);
				 long tempTime = 0;

				 if (temp == null) {//没有时间戳的时候出现一条
					 tempTime = 0;//当前时间
				 }else {
					 tempTime  =temp.timestamp*1000; //当前时间 数据库存储为 秒
				 }
				 long jian = System.currentTimeMillis() - tempTime;
				 Log.d("chat", "temp:"+temp +" postion:" + postion +" in jian : "+ jian + " ge:"+(jian > TIEMSTREAP_TIME));
				 
				 if (jian > TIEMSTREAP_TIME) {//没有时间戳 第一条添加时间戳
					//10分钟一段
					 JSONObject jsonObject = new JSONObject();
					 try {
						jsonObject.put("txt", System.currentTimeMillis()+"");
					 } catch (JSONException e) {
						e.printStackTrace();
					 }
					 ChatMessage remindChatMessage= ChatMessage.createRemindMessage(loginUserInfo.xmppUser, chatXmppUser,jsonObject.toString());
					 if (temp == null) {//添加到第一条
						 remindChatMessage.timestamp = messages.get(0).timestamp-1;
						 remindChatMessage.postion = 0;
						 return remindChatMessage;
					 }else {//不在第一条
						 if (postion !=0) {
							 remindChatMessage.postion = postion;
							 remindChatMessage.body = jsonObject.toString()+"";
							 if (messages.get(messages.size()-1).showType == MsgShowType.timestamp.ordinal()) {//最后一条不能为时间戳，不然出现多条时间戳就不好了
								return null;
							 }
							 remindChatMessage.timestamp = messages.get(messages.size()-1).timestamp-1;
							 return remindChatMessage;
						 }
					 }
							
				 } 
			}
		
		return null;
	}
	 
	@Override
	protected void onPostExecute(ChatMessage result) {
		Log.d("chat", "result:" +result);
		if (result != null) {
			if (messages!=null &&  messages.size()> 0 &&  messages.size() > result.postion-1) {
				result.showType = ChatMessage.MsgShowType.timestamp.ordinal();//系统
				if (result.postion == 0) {
					result.showType = ChatMessage.MsgShowType.timestamp.ordinal();
					messages.add(result.postion,result);
				}else {
					messages.add(result.postion-1,result);
				}
				result.fromUserInfo = loginUserInfo;
				lastTimestamp = System.currentTimeMillis();
				mChatMessageDao.saveOrUpdate(result);//所有的提醒信息都保存起来
				adapter.notifyDataSetChanged();
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						updateView();						
					}
				});
				
			}
		}
	
		isTimeSteampRun = false;//可以继续添加了

	}
  }
  
  /**
   * 添加时间戳
   */
  boolean isTimeSteampRun = false ;
  public void addTimeSteamp(int postion){

	 if (System.currentTimeMillis() - lastTimestamp > TIEMSTREAP_TIME && !isTimeSteampRun) {//到时间了
		 Log.d("chat", "add time :" +isTimeSteampRun);
		 isTimeSteampRun =true;
		 new MyAddTimeTask().execute(postion);
	 }
	
  }
  
  //设置已读
  public void setRead(){
	  try {
		  ChatSessionDao.getInstance(this).setReadMsg(loginUserInfo.xmppUser+"", chatXmppUser+"");
	  } catch (Exception e) {
		e.printStackTrace();
	  }
  }

  /**
   * chat input send 按钮按下
   * 
   */
	@Override
	public void onSend(String txt) {
		
		if (TextUtils.isEmpty(txt)) {
			return ;
		}
		
		ChatMessage message = new ChatMessage();
		message.fromUserInfo = loginUserInfo;
		message.txt = txt;
		message.msgType = MsgType.txt.getValue();
		message.showType = MsgShowType.out.ordinal();
		message.sendStatus = MsgSendStatus.send.ordinal();
		message.loginUser = loginUserInfo.xmppUser;
		message.otherUser = chatUserInfo.xmppUser;
		message.createBody();
		messages.add(message);
		message.id = mChatMessageDao.saveOrUpdate(message);
		updateView();
		sendMessage(message); 
		
		addTimeSteamp(messages.size());
	}
  
	 /**
	   * chat  发送图片消息
	   * 
	   */
	public void onSendImage(ChatMessage message) {
		
		if (TextUtils.isEmpty(message.sendImgUrl)) {//失败了
			message.sendStatus = MsgSendStatus.fail.ordinal();
			updateView();
			return;
		}
		message.fromUserInfo = loginUserInfo;
		message.sendStatus = MsgSendStatus.send.ordinal();
		message.loginUser = loginUserInfo.xmppUser;
		message.otherUser = chatUserInfo.xmppUser;
		message.readStatus = MsgReadStatus.read.ordinal();
		if (message.msgType == MsgType.img.getValue()) {
			message.txt="[图片]";
		}
		message.imgUrl = message.sendImgUrl;
		message.createBody();
		VPLog.d(TAG, "send imag:" +message);
		//updateView();
		//adapter.notifyDataSetChanged();
		updateView(message, false);
		sendMessage(message);
	}
	
	/**
	 * 发送message
	 * @param message
	 */
	public void sendMessage(ChatMessage message){
		
		if (mBinder!=null) {
			mBinder.getService().sendPacket(chatUserInfo.xmppUser, message.toJsonObject().toString(),ChatState.active, message.id+"", new SendPacketListener() {
				
				@Override
				public void onFinish(String command, int result, String packet) {
					try {
						long id = Long.parseLong(command);
						ChatMessage chatMessage = ChatMessage.findById(messages, id);
						if (chatMessage !=null) {
							if (result >0) {
								chatMessage.sendStatus = MsgSendStatus.success.ordinal();
							}else {
								chatMessage.sendStatus = MsgSendStatus.fail.ordinal();
							}
							mChatMessageDao.saveOrUpdate(chatMessage);
							//adapter.notifyDataSetChanged();
							updateView(chatMessage, false);
						}
					} catch (Exception e) {
					}
					VPLog.d(TAG, "result:"+result +" pac:"+packet);
				}
			});
		}
	}
	
	
	/**
	 * Checking keyboard height and keyboard visibility
	 */
	private int keyboardHeight;	
	private boolean isKeyBoardVisible;
	int previousHeightDiffrence = 0;
	private void checkKeyboardHeight(final View parentLayout) {

		parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						
						Rect r = new Rect();
						parentLayout.getWindowVisibleDisplayFrame(r);
						
						int screenHeight = parentLayout.getRootView()
								.getHeight();
						int heightDifference = screenHeight - (r.bottom);
						
						if (previousHeightDiffrence - heightDifference > 50) {							
							//popupWindow.dismiss();
						}
						
						//Log.d("tag", "heightDifference:" +heightDifference);

						
						previousHeightDiffrence = heightDifference;
						if (heightDifference > 100) {
							mChatInputView.isKeyBoardVisible = true;
							isKeyBoardVisible = true;
							changeKeyboardHeight(heightDifference);

						} else {
							isKeyBoardVisible = false;
							mChatInputView.isKeyBoardVisible = false;
							if (mChatInputView!=null && mChatInputView.isShowPopup()) {
								mChatInputView.mContainerView.setVisibility(View.VISIBLE);
							}
						}

					}
				});

	}

	/**
	 * change height of emoticons keyboard according to height of actual
	 * keyboard
	 * 
	 * @param height
	 *            minimum height by which we can make sure actual keyboard is
	 *            open or not
	 */
	private void changeKeyboardHeight(int height) {
		
		if (height > 100) {
			keyboardHeight = height;
			if (mChatInputView.mContainerView.getLayoutParams().height != keyboardHeight){
				mChatInputView.mContainerView.setVisibility(View.GONE);
			}
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, keyboardHeight);
			mChatInputView.mContainerView.setLayoutParams(params);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		
		ScreenUtils.initScreen(this);
	}
	
	/**
	 * Overriding onKeyDown for dismissing keyboard on key down
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//VPLog.d(TAG, keyCode +"-----" +event.getAction());
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mChatInputView!=null && mChatInputView.isShowPopup() || isKeyBoardVisible) {
				mChatInputView.hideEditMode();
				return false;
				
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {
        	//选择照片后
        	if(requestCode == SELECT_IMGAGE_REQUEST && resultCode == RESULT_OK){
        		ArrayList<String> mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
        		if (mSelectPath !=null && mSelectPath.size()>0 && !TextUtils.isEmpty(mSelectPath.get(0))) {
            		VPLog.d(TAG, mSelectPath.get(0)+""); 
        			SendImagePackage send = new SendImagePackage(mClient, mSelectPath.get(0),mHandler);
            		ChatMessage message = send.getChatMessage();
            		message.txt="[图片]";
            		message.createBody();
            		VPLog.d(TAG, ""+message);
            		if (message!=null) {
            			message.fromUserInfo = loginUserInfo;
            			message.loginUser = loginUserInfo.xmppUser;
            			message.otherUser = chatXmppUser;
            			messages.add(message);
            			message.id = mChatMessageDao.saveOrUpdate(message);
            			send.send();
                		updateView();
                		addTimeSteamp(messages.size());
					}
            		
				}
        	}else if (resultCode == RESULT_OK && requestCode == SELECT_AREA_REQUEST) {//位置
				String shotPath = data.getStringExtra(LocationSourceActivity.MAP_BITMAP_SHOT);
				double lat = data.getDoubleExtra(LocationSourceActivity.MAP_BITMAP_LAT, 0);
				double lon = data.getDoubleExtra(LocationSourceActivity.MAP_BITMAP_LON, 0);
				String areaDesc =  data.getStringExtra(LocationSourceActivity.MAP_AREA_DESC);
				
				
				//有情况
				if (!TextUtils.isEmpty(shotPath)) {
        			SendImagePackage send = new SendImagePackage(mClient, shotPath,mHandler);
            		ChatMessage message = send.getChatMessage();
            		if (message!=null) {
            			message.fromUserInfo = loginUserInfo;
            			message.loginUser = loginUserInfo.xmppUser;
            			message.otherUser = chatXmppUser;
            			message.msgType = MsgType.maps.getValue();
            			message.lon = lon;
            			message.lat = lat;
            			message.txt = areaDesc;
            			messages.add(message);
            			message.id = mChatMessageDao.saveOrUpdate(message);
            			send.send();
                		updateView();
                		addTimeSteamp(messages.size());
					}

				}
				
			}
        } 
	}

	@Override
	public void onRefresh() {
		Log.d("tag", "me:"+messages);
		isrefresh = true;
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				findPage();
			}
		}, 10);
	}
	
	
//	public void upImgaeFile(String path){
//		
//	}
}
