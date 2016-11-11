package com.vp.loveu.service;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.sasl.SASLErrorException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.iqlast.LastActivityManager;
import org.jivesoftware.smackx.iqlast.packet.LastActivity;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.xevent.MessageEventManager;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;

import com.vp.loveu.base.VpApplication;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.message.bean.ChatMessage;
import com.vp.loveu.message.bean.ChatMessage.MsgType;
import com.vp.loveu.message.bean.UserInfo;
import com.vp.loveu.message.ui.ConfictLoginActivity;
import com.vp.loveu.service.util.AcceptChatRun;
import com.vp.loveu.service.util.SendPacketRun;
import com.vp.loveu.service.util.SendPacketRun.SendPacketListener;
import com.vp.loveu.service.util.SendPacketRun.VpSendPacket;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.MsgSharePreferenceUtil;
import com.vp.loveu.util.VPLog;

/**
 * xmpp service
 * @author tanping 
 *
 */
public class XmppService extends Service {
	
	public static final String TAG = "xmpp";
	public static final String RESOURCE = "mobile";
	
	//生产服务器
	public static final String SERVICE_NAME ="xmpp.iuapp.cn";
	public static final String SERVICE_IP ="xmpp.iuapp.cn";
	public static final int SERVICE_PORT =5022;

	//测试服务器
//	public static final String SERVICE_NAME ="ay140514152219z";
//	public static final String SERVICE_IP ="115.29.244.85";
//	public static final int SERVICE_PORT =5222;

	public static final String ADMIN_SERVICE ="admin";

	
	XMPPConnection mConnection;
	ChatManager mChatManager; //消息管理
	OfflineMessageManager mOfflineMessageManager;//
	MessageEventManager mEventManager;
	
	ExecutorService mExecutorService;
	boolean isLogin = false ;
	boolean isQuit = false;
	
	LastActivityManager mLastActivityManager ;
	
	public long send_long_time =1;//单位s
	
	public static final int REPORT_STATUS_INTERVAL = 15*60*1000 ;//上报状态时间间隔
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		IBinder result = null;
		if (null == result)
			result = new XmppServiceBinder();
		
		return result;
	}

	public class XmppServiceBinder extends Binder {
		public XmppService getService() {
			return XmppService.this;
		}
	}
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		//createConnect();
	}
	
	/**
	 * 创建连接
	 */
	public void createConnect(){
		if (mConnection == null) { //建立连接
			ConnectionConfiguration connConfig = new ConnectionConfiguration(
					SERVICE_IP, SERVICE_PORT,SERVICE_NAME);
			connConfig.setReconnectionAllowed(true);
			connConfig.setSecurityMode(SecurityMode.disabled);

			/*try {
				connConfig.setCustomSSLContext(SSLContext.getDefault());
				connConfig.setKeystoreType("ssl");
			} catch (Exception e) {
				e.printStackTrace();
			}*/
			mConnection = new XMPPTCPConnection(connConfig);
			mConnection.addConnectionListener(connectionListener);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mhHandler.removeMessages(SEND_LOGIN_REQUEST);
		VPLog.d(TAG, "onDestroy()");
		isLogin = false;
		isQuit = true;
		logout();
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		if (mExecutorService==null) {
			mExecutorService = Executors.newSingleThreadExecutor();//单例的线程池
		}
		createConnect();
		login();
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	
	/**
	 * 登出
	 */
	private synchronized void logout(){
		VPLog.d(TAG, "==logout===");
		if (mConnection!=null) {
			new Thread(){
				public void run() {
					try {
						isLogin = false;
						//注销
						mConnection.disconnect();
						mExecutorService.shutdownNow();//立即停止
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						mConnection = null;
					}
				};
			}.start();
		}
	}
	
	
	
	
	/**
	 * 登录
	 */
	private synchronized void login(){
		if (mExecutorService==null) {
			mExecutorService = Executors.newSingleThreadExecutor();//单例的线程池
		}
		if (isQuit) { //登出
			try {
				mhHandler.removeMessages(SEND_LOGIN_REQUEST);//清理掉
				mhHandler.removeMessages(GET_ONLINE_REMIND);
			} catch (Exception e) {
				// TODO: handle exception
			}
			return ;
		}
		//去登陆
		mExecutorService.submit(new Runnable() {
			
			@Override
			public void run() {
				getConnect();
			}
		});
	 
		
	}
	
	
	private synchronized void getConnect() {
		try {
			VPLog.d(TAG, "login");
			if (isLogin && mConnection!=null && mConnection.isAuthenticated()) {
				VPLog.d(TAG, "已经登录成功过");
				return;
			}
			if (isQuit) {//登出
				return ;
			}
			if (mConnection==null) {//已经建立
				createConnect();
			}
			
			
			LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
			if (loginInfo == null) {
				stopSelf();
				return;
			}
			VPLog.d(TAG, "source:"+RESOURCE);
			mConnection.connect();
			String service = mConnection.getServiceName();
			VPLog.d(TAG, "xmpp:" +loginInfo.getXmpp_user() +"　pwd:" +loginInfo.getXmpp_pwd() + " - " + service);
			mConnection.login(loginInfo.getXmpp_user().trim(), loginInfo.getXmpp_pwd().trim(), RESOURCE);
			//mConnection.login("test", "123456", RESOURCE);
			//mConnection.login("test2", "123456");
			mChatManager =  ChatManager.getInstanceFor(mConnection);
			mChatManager.addChatListener(mPushListener);
			PingManager pingManager = PingManager.getInstanceFor(mConnection);
			pingManager.registerPingFailedListener(new PingFailedListener() {
				
				@Override
				public void pingFailed() {
					VPLog.d(TAG, "ping fail");
					isLogin = false;
					login(); //重新去登陆
				}
			});
			//pingManager.setDefaultPingInterval(60);//60s一次ping 
			//pingManager.setPingInterval(60);
		//	ChatStateManager chatStateManager = ChatStateManager.getInstance(mConnection);
			
			Roster roster =  mConnection.getRoster();
			roster.addRosterListener(new RosterListener() {
				
				@Override
				public void presenceChanged(Presence arg0) {
					//用户在线提醒
					VPLog.d(TAG+"roster", "presenceChanged" +arg0);
					VPLog.d(TAG+"roster", "presenceChanged" +arg0.toXML());

				}
				
				@Override
				public void entriesUpdated(Collection<String> arg0) {
					for (String c :arg0){
						VPLog.d(TAG+"roster","entriesUpdated : "+c);
					}
				}
				
				@Override
				public void entriesDeleted(Collection<String> arg0) {
					for (String c :arg0){
						VPLog.d(TAG+"roster","entriesDeleted : "+c);
					}
				}
				
				@Override
				public void entriesAdded(Collection<String> arg0) {
					for (String c :arg0){
						VPLog.d(TAG+"roster","entriesAdded : "+c);
					}
				}
			});
			offlineManager();
			//登录
			Presence presence = new Presence(Presence.Type.available);  
			mConnection.sendPacket(presence);
			isLogin = true;
			send_long_time = 1;
			mhHandler.removeMessages(SEND_LOGIN_REQUEST);
			
			//上报状态
			mhHandler.sendEmptyMessage(REPORT_USER_STATUS);
			
		}catch(SASLErrorException e){//用户名或密码错误
			//需要重新登录。
			VPLog.e(TAG, "用户名 或者密码错误");
			e.printStackTrace();
			stopSelf();
		}catch (Exception e) {
			e.printStackTrace();
			mhHandler.sendEmptyMessage(SEND_LOGIN_REQUEST);
		}
	}
	
	/**
	 * 欢迎回来
	 */
	public void welcome(){
		VPLog.d(TAG, "welcome ...");
		try {
			MsgSharePreferenceUtil sharePreferenceUtil = new MsgSharePreferenceUtil(this, "xmpp_service");
			String wel =  sharePreferenceUtil.getValueForKey("welcome");
			if (TextUtils.isEmpty(wel)) {
				sharePreferenceUtil.addKey("welcome", "welcome");
			}else {
				//return
				return ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ChatMessage message = new ChatMessage();
		message.msgType = MsgType.welcome_service.getValue();
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo == null) {
			VPLog.d(TAG, "welcome ... no login ");
			return;
		}
		UserInfo userInfo = new UserInfo();
		userInfo.userId = loginInfo.getUid()+"";
		message.fromUserInfo = userInfo;
		VPLog.d(TAG, "welcome:"+message.toJsonObject().toString());
		try {
			sendPacket(ADMIN_SERVICE, message.toJsonObject().toString(),null, 201+"", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 上线提醒,只有成功后不再发送， 除非进入mainactivity ，
	 */
	public void getOnlineRemind(){
		VPLog.d(TAG, "getOnlineRemind ...");
		try {
			MsgSharePreferenceUtil sharePreferenceUtil = new MsgSharePreferenceUtil(this, "xmpp_service");
			String wel =  sharePreferenceUtil.getValueForKey("get_online_remind");
			if (TextUtils.isEmpty(wel)) {//第一次进来
				//sharePreferenceUtil.addKey("get_online_remind", System.currentTimeMillis()+"");
			}else {
				//判断是否满足一小时
				try {
					long time =  Long.parseLong(wel);
					if (System.currentTimeMillis() - time >= 60*60*1000) {//大于一小时
						//sharePreferenceUtil.addKey("get_online_remind", System.currentTimeMillis()+"");
					}else {
						VPLog.d(TAG, "online 小于一小时 remove");
						mhHandler.removeMessages(GET_ONLINE_REMIND);//删除消息
						VPLog.d(TAG, System.currentTimeMillis()+"");
						return ;
					}
				} catch (Exception e) {
					sharePreferenceUtil.removeKey("get_online_remind");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
		
		ChatMessage message = new ChatMessage();
		message.msgType = MsgType.get_online_remind.getValue();
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo == null) {
			VPLog.d(TAG, "reportStatus ... no login ");
			return;
		}
		UserInfo userInfo = new UserInfo();
		userInfo.userId = loginInfo.getUid()+"";
		userInfo.userName = loginInfo.getNickname();
		message.fromUserInfo = userInfo;
		VPLog.d(TAG, "getOnlineRemind :"+message.toJsonObject().toString());
		try {
			sendPacket(ADMIN_SERVICE, message.toJsonObject().toString(),null, 202+"", new SendPacketListener() {
				
				@Override
				public void onFinish(String command, int result, String packet) {
					if (result>0) {//发送成功
						mhHandler.removeMessages(GET_ONLINE_REMIND);//删除消息
						try {
							MsgSharePreferenceUtil sharePreferenceUtil = new MsgSharePreferenceUtil(XmppService.this, "xmpp_service");
							sharePreferenceUtil.addKey("get_online_remind", System.currentTimeMillis()+"");//更新时间
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 上报状态
	 */
	public void reportStatus(){
		ChatMessage message = new ChatMessage();
		message.msgType = MsgType.report_status.getValue();
		message.txt = Presence.Mode.available.name();
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo == null) {
			VPLog.d(TAG, "reportStatus ... no login ");
			return;
		}
		UserInfo userInfo = new UserInfo();
		userInfo.userId = loginInfo.getUid()+"";
		message.fromUserInfo = userInfo;
		VPLog.d(TAG, "reportStatus:"+message.toJsonObject().toString());
		try {
			sendPacket(ADMIN_SERVICE, message.toJsonObject().toString(),null, 200+"", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public void lastActivityManger(){
		
		LastActivityManager lastActivityManager = LastActivityManager.getInstanceFor(mConnection);
		try {
			LastActivity result = lastActivityManager.getLastActivity("test1@tp/"+RESOURCE);
			VPLog.d(TAG, "last:"+result);
		} catch (NoResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMPPErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 离线管理
	 */
	public void offlineManager(){
		mOfflineMessageManager = new OfflineMessageManager(mConnection);
		try {
			VPLog.d(TAG+"off", "offline"+ mOfflineMessageManager.getMessageCount() ) ;
			List<Message> offMsg = mOfflineMessageManager.getMessages();
			for (int i = 0; i < offMsg.size(); i++) {
				VPLog.d(TAG, offMsg.get(i).toXML()+"");
			}
			Iterator<org.jivesoftware.smack.packet.Message> it = mOfflineMessageManager.getMessages().iterator();

			// System.out.println(offlineManager.supportsFlexibleRetrieval());
			System.out.println("离线消息数量: " + mOfflineMessageManager.getMessageCount());

			Map<String, ArrayList<Message>> offlineMsgs = new HashMap<String, ArrayList<Message>>();

			while (it.hasNext()) {
				org.jivesoftware.smack.packet.Message message = it.next();
				System.out
						.println("收到离线消息, Received from 【" + message.getFrom()
								+ "】 message: " + message.getBody());
				String fromUser = message.getFrom().split("/")[0];

				if (offlineMsgs.containsKey(fromUser)) {
					offlineMsgs.get(fromUser).add(message);
				} else {
					ArrayList<Message> temp = new ArrayList<Message>();
					temp.add(message);
					offlineMsgs.put(fromUser, temp);
				}
			}

			// 在这里进行处理离线消息集合......
			Set<String> keys = offlineMsgs.keySet();
			Iterator<String> offIt = keys.iterator();
			while (offIt.hasNext()) {
				String key = offIt.next();
				ArrayList<Message> ms = offlineMsgs.get(key);
			}

			mOfflineMessageManager.deleteMessages();
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 连接监听
	 */
	ConnectionListener connectionListener = new ConnectionListener() {

		@Override
		public void authenticated(XMPPConnection arg0) {
			VPLog.i(TAG, "authenticated");
			isLogin = true;
			welcome();

		}

		@Override
		public void connected(XMPPConnection arg0) {
			VPLog.i(TAG, "connected");
		}

		@Override
		public void connectionClosed() {
			VPLog.i(TAG, "connectionClosed");
			isLogin = false;
		}

		@Override
		public void connectionClosedOnError(Exception arg0) {
			VPLog.i(TAG, "connectionClosedOnError");
		    //这里就是网络不正常或者被挤掉断线激发的事件
            if(arg0.getMessage()!=null && arg0.getMessage().contains("conflict")){ //被挤掉线
                VPLog.e(TAG,"非正常关闭异常:"+arg0.getMessage());
                //VPLog.e(TAG,"非正常关闭异常:"+arg0);
                arg0.printStackTrace();
                isQuit = true;
                mhHandler.removeMessages(SEND_LOGIN_REQUEST);
                //logout();
                if (mConnection!=null) {
					try {
						mConnection.disconnect();
						mExecutorService.shutdownNow();//立即停止
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
                Intent intent = new  Intent(VpApplication.getInstance(),ConfictLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                stopSelf();
            }
			//isLogin = false;
		}

		@Override
		public void reconnectingIn(int arg0) {
			VPLog.i(TAG, "reconnectingIn");
			if (!isLogin &&!isQuit) {//没有登录
				login();//去登陆
			}
		}

		@Override
		public void reconnectionFailed(Exception arg0) {
			VPLog.i(TAG, "reconnectionFailed :" +arg0);
			
		}

		@Override
		public void reconnectionSuccessful() {
			VPLog.i(TAG, "reconnectionSuccessful");
			isLogin = true;
			mExecutorService.execute(new Runnable() {
				
				@Override
				public void run() {
					offlineManager();
				}
			});
		}
		
		 
	};
	
	
	/**
	 * 发送 包体
	 * @param to
	 * @param packet
	 * @param state 可以为null 不需要状态 如果为编辑  composing 状态  packet==null 
	 * @param command
	 * @param listener
	 */
	public synchronized void sendPacket(String to,String packet,ChatState state,String command,SendPacketListener listener){
		VPLog.d(TAG, "to:"+to +" packet:"+packet);
		to = to+"";
		VpSendPacket vpPacket = new VpSendPacket(to,packet,state,command,listener);
		SendPacketRun sendRun = new SendPacketRun(vpPacket) {
			
			@Override
			public void run() {
				if (mConnection!=null && mConnection.isConnected() && mConnection.isAuthenticated()) {
					VPLog.d(TAG, ""+mVpPacket);
					if (mVpPacket.to.indexOf("@")<0) {
						mVpPacket.to += "@"+SERVICE_NAME;
					}
					Chat chat = mChatManager.createChat(mVpPacket.to, null);
					android.os.Message osmsg = new android.os.Message();
					osmsg.obj = mVpPacket;
					osmsg.what = SEND_PACKET;
					try {
						Message chatMessage = new Message();
						chatMessage.setBody(mVpPacket.packet);
						if (mVpPacket.mChatState!=null) {
							chatMessage.addExtension(new ChatStateExtension(mVpPacket.mChatState));
						}
						chat.sendMessage(chatMessage);//发送消息
						mVpPacket.result = 1;
					} catch (Exception e) {
						e.printStackTrace();
						mVpPacket.result = -1;
					}finally{
						if (chat!=null) {
							chat.close();
						}
					}
					mhHandler.sendMessageDelayed(osmsg, 10);//成功
					VPLog.d(TAG, ""+mVpPacket);

				}else { //没有登录，去登陆
					login();
					android.os.Message osmsg = new android.os.Message();
					osmsg.obj = mVpPacket;
					osmsg.what = SEND_PACKET;
					mVpPacket.result = -1;
					mhHandler.sendMessage(osmsg);
				}
			}
		};
		
		if (mExecutorService==null) {
			mExecutorService = Executors.newSingleThreadExecutor();//单例的线程池
		}
		mExecutorService.submit(sendRun);
	}
	
	
	/**
	 * handler处理
	 */
	public static final int SEND_PACKET =12345;//发送包裹
	public static final int SEND_LOGIN_REQUEST=2345;//发送
	public static final int REPORT_USER_STATUS=2346;//上报状态
	public static final int GET_ONLINE_REMIND=2347;//获取在线提醒


	public Handler mhHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case SEND_PACKET: //发送包裹
				if (msg.obj!=null && msg.obj instanceof VpSendPacket) {
					VpSendPacket vpSendPacket = (VpSendPacket) msg.obj;
					if (vpSendPacket.mSendPacketListener!=null) {
						vpSendPacket.mSendPacketListener.onFinish(vpSendPacket.command, vpSendPacket.result, vpSendPacket.packet);
					}
				};
				break;
			case SEND_LOGIN_REQUEST: //去登陆
				VPLog.d(TAG, "try login:"+ send_long_time);
				mhHandler.removeMessages(SEND_LOGIN_REQUEST);
				if (send_long_time <5*60) {//单位s 5分钟
					send_long_time++;
				}
				 mhHandler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							login();
						}
					}, send_long_time*1000);
				break;
			case REPORT_USER_STATUS:
				mhHandler.removeMessages(REPORT_USER_STATUS);
				mhHandler.sendEmptyMessageDelayed(REPORT_USER_STATUS, REPORT_STATUS_INTERVAL);
				reportStatus();
				break ;
			case GET_ONLINE_REMIND:
				mhHandler.removeMessages(GET_ONLINE_REMIND);
				mhHandler.sendEmptyMessageDelayed(GET_ONLINE_REMIND, REPORT_STATUS_INTERVAL);
				getOnlineRemind();//上线提醒
				break ;
			default:
				break;
			}
			
		};
	};
	
	
	
	
	/**
	 * push 监听器
	 */
	ChatManagerListener mPushListener = new ChatManagerListener() {
		
		@Override
		public void chatCreated(Chat arg0, boolean arg1) {
		 arg0.addMessageListener(new ChatStateListener() {
			@Override
			public void processMessage(Chat arg0, Message arg1) {
				
				//接收消息
				mExecutorService.submit(new AcceptChatRun(arg1,XmppService.this));
				//回收 chat 
				arg0.close();
				
			}
			
			@Override
			public void stateChanged(Chat chat, ChatState state) {
				VPLog.d(TAG, "stateChanged:"+state.name());
			}
		});
			
			
		}
	};
	
}
