package com.vp.loveu.service.util;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.PacketExtension;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;

import com.vp.loveu.MainActivity;
import com.vp.loveu.base.VpApplication;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.message.bean.ChatMessage;
import com.vp.loveu.message.bean.ChatMessage.MsgReadStatus;
import com.vp.loveu.message.bean.ChatMessage.MsgShowType;
import com.vp.loveu.message.bean.ChatMessage.MsgType;
import com.vp.loveu.message.bean.PushNoticeBean;
import com.vp.loveu.message.bean.PushNoticeBean.BubbleType;
import com.vp.loveu.message.bean.PushNoticeBean.PushType;
import com.vp.loveu.message.bean.UserInfo;
import com.vp.loveu.message.db.ChatMessageDao;
import com.vp.loveu.message.db.PushNoticeBeanDao;
import com.vp.loveu.message.db.UserInfoDao;
import com.vp.loveu.message.provider.MessageSessionProvider;
import com.vp.loveu.message.ui.ConfictLoginActivity;
import com.vp.loveu.message.utils.BroadcastType;
import com.vp.loveu.message.utils.XmppUtils;
import com.vp.loveu.service.XmppService;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.MsgSharePreferenceUtil;
import com.vp.loveu.util.VPLog;


/**
 * 消息接收中心，处理 message中心。 不阻碍读线程的性能
 * @author tanping
 * 2015-11-2
 */
public class AcceptChatRun  implements Runnable {
	
	public static final String TAG ="AcceptChatRun";
	public Message mMessage;// 消息
	public Context mContext;
	public AcceptChatRun(Message message,Context context){
		this.mMessage = message;
		this.mContext = context;
	}
	@Override
	public void run() {
		//处理message
		String from = mMessage.getFrom();
		String body = mMessage.getBody();
		//Log.d("chat", "msg:" +arg1);
		//VPLog.d(TAG,"from=" + from + "  body=" + body  +" type:" +arg1.getType());
		VPLog.i(TAG, mMessage.toXML()+"");
		
		if (body!=null) {
			try {
				JSONObject data = new JSONObject(body);
				int type = data.optInt("msg_type");
				long timestamp = data.getLong("timestamp");

				if (type>0) {//聊天消息
					ChatMessage chatMessage =  ChatMessage.parseJson(body);
					chatMessage.to = mMessage.getTo();
					chatMessage.loginUser = XmppUtils.getJidToUsername(chatMessage.to).toLowerCase();
					chatMessage.showType = MsgShowType.in.ordinal();
					chatMessage.timestamp = timestamp;
					chatMessage.readStatus = MsgReadStatus.unread.ordinal();//未读
					if (chatMessage.msgType == MsgType.img.getValue() || chatMessage.msgType == MsgType.maps.getValue()) {//图片
						chatMessage.showType = MsgShowType.in_img.ordinal();
					}
					
					chatMessage.otherUser = chatMessage.fromUserInfo.xmppUser;
					VPLog.d(TAG, ""+chatMessage);
					
					UserInfo uInfo =  UserInfoDao.getInstance(mContext).findByXmppUser(chatMessage.otherUser);
					//黑名单验证，黑名单丢弃
					if (uInfo !=null && uInfo.black >0) {//拉黑了
						VPLog.d(TAG, "拉黑了。。。");
						return;
					}
					
					//存数据库
					UserInfoDao.getInstance(mContext).saveOrUpdate(chatMessage.fromUserInfo);
					long chatId = ChatMessageDao.getInstance(mContext).saveOrUpdate(chatMessage);
					chatMessage.id = chatId;
					VPLog.i(TAG, "chat_id:"+chatId +" re:" +BroadcastType.PRIVATE_CHAT_RECEVIE +"" +XmppUtils.getJidToUsername(chatMessage.otherUser));
					
					//在线聊天窗口
					Intent intent = new Intent(BroadcastType.PRIVATE_CHAT_RECEVIE +"" + XmppUtils.getJidToUsername(chatMessage.otherUser));
					intent.putExtra("chat_message", chatMessage);
					mContext.sendBroadcast(intent);
					
					//更新聊天会话
					mContext.getContentResolver().update(MessageSessionProvider.CONTENT_MSG_SESSION_URI,
							null, null, null);
				}else {//推送消息
					VPLog.d(TAG, data.toString());
					long expire_time = data.getLong("expire_time");
					if (expire_time >0 && System.currentTimeMillis()/1000 > expire_time ) {//过期了，扔掉
						VPLog.d(TAG, "expire");
						return ;
					}
					if (from.toLowerCase().startsWith("yh")) {//非管理员
						return;
					}
					LoginUserInfoBean mine = LoginStatus.getLoginInfo();
					if (mine == null) {
						mine = new LoginUserInfoBean(mContext);
					}
					PushNoticeBean pushNoticeBean =  PushNoticeBean.parsePushJson(data.getString("body"));
					pushNoticeBean.timestamp = timestamp;
					pushNoticeBean.loginId = mine.getUid()+"";
					pushNoticeBean.readStatus = 0;//未读

					//推送
					if (pushNoticeBean.pushType == PushType.notice.getValue()) {//把通知存起来
						PushNoticeBeanDao.getInstance(mContext).saveOrUpdate(pushNoticeBean);//保存 ，，发送红点广播
					}else {
							if (pushNoticeBean.bubbleType == BubbleType.close_app.getValue()) {//强制杀死自己
								try {
									LoginStatus.loginOut();
									Intent intent = new Intent(mContext.getApplicationContext(),MainActivity.class);
									intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									intent.putExtra("command", "logout");
									mContext.startActivity(intent);
									
									//关掉服务
									Intent intents = new Intent(VpApplication.getContext(), XmppService.class);
									VpApplication.getInstance().stopService(intents);
								} catch (Exception e) {
									e.printStackTrace();
								}
								return;
							}
						 
						
						
						//气泡存储
						MsgSharePreferenceUtil msgSharePreferenceUtil = new MsgSharePreferenceUtil(mContext, "push_bubble");//气泡
						if (pushNoticeBean.bubbleType == BubbleType.praise.getValue()) {
							pushNoticeBean.bubbleType = BubbleType.comment.getValue();//赞和评论 都属于评论
						}
						
						String key = PushNoticeBean.BUBBLE_TYPE_KEY  +pushNoticeBean.bubbleType;
						int value =-1;
						
						try {//取int值，可能不为int，比如活动的签到推送。
							value = msgSharePreferenceUtil.getIntValueForKey(key);
						} catch (Exception e) {
						}
						
						try {
							if (value <=0) {
								msgSharePreferenceUtil.addKey(key, 1);
							}else {
								msgSharePreferenceUtil.addKey(key, value+1);//加1
							}
							
							//{"id":1100,"status":90}
							if (pushNoticeBean.bubbleType == BubbleType.join_activity.getValue()) {//参加活动
								msgSharePreferenceUtil.addKey(key, pushNoticeBean.txt);//参加活动
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				PushNoticeBean.sendUpdateBroadcast(mContext);//发送红点通知
			}
		}
		
		/*VPLog.d(TAG, ""+ arg1.getExtensions());
		VPLog.d(TAG, arg1.getBodies()+"");*/
		for ( PacketExtension ext :mMessage.getExtensions()){
			//VPLog.i(TAG+"-extens:", ext.getElementName() +" -" +ext.getNamespace() +" ==");
			/*try {
				Chat chat = mChatManager.createChat("test2@"+SERVICE_NAME, null);
				Message msg = new Message("test21");
				ChatStateExtension chatStateExtension = new ChatStateExtension(ChatState.gone);
				msg.addExtension(chatStateExtension);
				msg.setBody("收到"+ext.getElementName());
				msg.setThread(UUID.randomUUID().toString());
				chat.getThreadID();
				//chat.sendMessage(msg);
				chat.close();
			} catch (Exception e) {
				e.printStackTrace();
			}  
*/
		}
	}

}
