package com.vp.loveu.message.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.bean.NetBaseBean;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.message.bean.ChatMessage;
import com.vp.loveu.message.bean.ChatMessage.MsgType;
import com.vp.loveu.message.bean.UserInfo;
import com.vp.loveu.message.db.ChatMessageDao;
import com.vp.loveu.message.db.UserInfoDao;
import com.vp.loveu.message.utils.BroadcastType;
import com.vp.loveu.message.utils.DensityUtil;
import com.vp.loveu.message.utils.ToastUtil;
import com.vp.loveu.message.utils.XmppUtils;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.VPLog;
import com.vp.loveu.widget.IOSActionSheetDialog;
import com.vp.loveu.widget.IOSActionSheetDialog.OnSheetItemClickListener;
import com.vp.loveu.widget.IOSActionSheetDialog.SheetItemColor;

import cz.msebera.android.httpclient.Header;

public class PersonSettingActivity extends VpActivity  implements OnClickListener{

	public static final String PARAMS_KEY ="user";
	public static final String PARAMS_KEY_FROM ="from";
	
	private RelativeLayout goPersonCenter ;
	private ImageView userHeadImage ;
	private TextView userNiceName ;
	private ImageView onlineRemindToggle ;
	private RelativeLayout onlineRemind ;
	private RelativeLayout clearChatMsg ;
	private View clearChatDivier;
	private RelativeLayout goBlack ;
	private ImageView goBlackToggle ;
	private RelativeLayout reportPerson ;
	DisplayImageOptions roundedOptions;

	
	
	UserInfo mUserInfo;
	UserInfo loginUserInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_setting);
		initView();
		
	}

	private void initView() {
		
		mUserInfo = (UserInfo) getIntent().getSerializableExtra(PARAMS_KEY);
		if (mUserInfo == null) {
			Toast.makeText(this, "没有发现用户", 0).show();
			finish();
			return ;
		}
		
		mUserInfo =  UserInfoDao.getInstance(this).findById(mUserInfo.userId);
		VPLog.d(PARAMS_KEY, "" +mUserInfo);
	
		//获取用户信息
		 LoginUserInfoBean loginuser = LoginStatus.getLoginInfo();
		if (loginuser== null) {
			finish();
			return;
		}
		loginUserInfo = new UserInfo();
		loginUserInfo.xmppUser =loginuser.getXmpp_user().toLowerCase();
		loginUserInfo.headImage = loginuser.getPortrait();
		loginUserInfo.userName = loginuser.getNickname();
		loginUserInfo.userId = loginuser.getUid()+"";
		
		
		initPublicTitle();
		mPubTitleView.setVisibility(View.VISIBLE);
		mPubTitleView.mBtnLeft.setText("");
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mTvTitle.setText(R.string.title_activity_person_setting);
		
		goPersonCenter = (RelativeLayout)findViewById(R.id.go_person_center);
		userHeadImage = (ImageView)findViewById(R.id.user_head_image);
		userNiceName = (TextView)findViewById(R.id.user_nice_name);
		onlineRemindToggle = (ImageView)findViewById(R.id.online_remind_toggle);
		onlineRemind = (RelativeLayout)findViewById(R.id.online_remind);
		clearChatMsg = (RelativeLayout)findViewById(R.id.clear_chat_msg);
		goBlack = (RelativeLayout)findViewById(R.id.go_black);
		goBlackToggle = (ImageView)findViewById(R.id.go_black_toggle);
		reportPerson = (RelativeLayout)findViewById(R.id.report_person);
		
		clearChatDivier = findViewById(R.id.clear_chat_msg_divier);
		
		//
		String from = getIntent().getStringExtra(PARAMS_KEY_FROM);
		if (!TextUtils.isEmpty(from)) {
			clearChatDivier.setVisibility(View.GONE);
			clearChatMsg.setVisibility(View.GONE);
			goPersonCenter.setVisibility(View.GONE);
		}
		goPersonCenter.setOnClickListener(this);
		onlineRemind.setOnClickListener(this);
		clearChatMsg.setOnClickListener(this);
		goBlack.setOnClickListener(this);
		reportPerson.setOnClickListener(this);
		
		
		 roundedOptions = new DisplayImageOptions.Builder()
	        .showImageOnLoading(R.drawable.default_image_loading) // resource or
	        .showImageForEmptyUri(R.drawable.default_image_loading_fail) // resource or
	        .showImageOnFail(R.drawable.default_image_loading_fail) // resource or
	        .resetViewBeforeLoading(false) // default
	        .delayBeforeLoading(50).cacheInMemory(true) // default
	        .cacheOnDisk(true) // default
	        .bitmapConfig(Bitmap.Config.RGB_565)  
	        .considerExifParams(false) // default
	        .displayer(new RoundedBitmapDisplayer(DensityUtil.dip2px(this, 50)))
	        .build();
		
		ImageLoader.getInstance().displayImage(mUserInfo.headImage, userHeadImage, roundedOptions);
		userNiceName.setText(mUserInfo.userName);
		updateView();
		
		isOnline();
	}

	public void isOnline(){

		
		updateView();
		String url = VpConstants.USER_IS_ONLINE_REMIND;
		
		JSONObject data = new JSONObject();
		try {
			data.put("uid", loginUserInfo.userId);
			data.put("t_uid", mUserInfo.userId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (mClient == null) {
			mClient = new VpHttpClient(this);
		}
		mClient.post(url, new RequestParams(), data.toString(), new AsyncHttpResponseHandler() {
			
			private String deAesResult;

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				deAesResult = ResultParseUtil.deAesResult(responseBody);
				NetBaseBean baseBean = NetBaseBean.parseJson(deAesResult);
				if (baseBean.isSuccess()) {
					JSONObject data;
					try {
						data = new JSONObject(baseBean.data);
						int statue  = data.getInt("status");
						mUserInfo.onLineRemind = statue;
					} catch (JSONException e) {
						e.printStackTrace();
					}
					//成功
					UserInfoDao.getInstance(PersonSettingActivity.this).saveOrUpdate(mUserInfo);
				} 
				updateView();

			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				new NetBaseBean().showMsgToastInfo();
				if (mUserInfo.onLineRemind>0) {
					mUserInfo.onLineRemind = 0;
				}else {
					mUserInfo.onLineRemind = 1 ;
				}
				UserInfoDao.getInstance(PersonSettingActivity.this).saveOrUpdate(mUserInfo);

				updateView();
			}
		});
	
		
	}
	
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.go_person_center:
			goOtherUserInfo(mUserInfo.userId);
			break;
		case R.id.online_remind:
			//预处理
			if (mUserInfo.onLineRemind>0) {
				mUserInfo.onLineRemind = 0;
			}else {
				mUserInfo.onLineRemind = 1 ;
			}
			updateOnLineRemind();
			break ;
		case R.id.clear_chat_msg:
			clearChatMsg();
			break ;
		case R.id.go_black:
			if (mUserInfo.black>0) {
				mUserInfo.black = 0;
			}else {
				mUserInfo.black = 1 ;
			}
			break ;
		case R.id.report_person:
			reportPerson();
			break ;
		default:
			break;
		}
		
		updateView();
		UserInfoDao.getInstance(this).saveOrUpdate(mUserInfo);
	}
	
	private void updateOnLineRemind() {
		
		updateView();
		String url = VpConstants.USER_ONLINE_REMIND;
		
		JSONObject data = new JSONObject();
		try {
			data.put("uid", loginUserInfo.userId);
			data.put("t_uid", mUserInfo.userId);
			data.put("type", mUserInfo.onLineRemind);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (mClient == null) {
			mClient = new VpHttpClient(this);
		}
		mClient.post(url, new RequestParams(), data.toString(), new AsyncHttpResponseHandler() {
			
			private String deAesResult;

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				deAesResult = ResultParseUtil.deAesResult(responseBody);
				NetBaseBean baseBean = NetBaseBean.parseJson(deAesResult);
				if (baseBean.isSuccess()) {
					//成功
					UserInfoDao.getInstance(PersonSettingActivity.this).saveOrUpdate(mUserInfo);
				}else {
					//状态还原
					if (mUserInfo.onLineRemind>0) {
						mUserInfo.onLineRemind = 0;
					}else {
						mUserInfo.onLineRemind = 1 ;
					}
					updateView();
					baseBean.showMsgToastInfo();
					UserInfoDao.getInstance(PersonSettingActivity.this).saveOrUpdate(mUserInfo);

				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				new NetBaseBean().showMsgToastInfo();
				if (mUserInfo.onLineRemind>0) {
					mUserInfo.onLineRemind = 0;
				}else {
					mUserInfo.onLineRemind = 1 ;
				}
				UserInfoDao.getInstance(PersonSettingActivity.this).saveOrUpdate(mUserInfo);

				updateView();
			}
		});
	}

	/**
	 * 举报
	 */
	OnSheetItemClickListener reportItemClickListener;
	public void reportPerson(){
		String[] reports =  getResources().getStringArray(R.array.Report_array);
		
		reportItemClickListener = new OnSheetItemClickListener() {
			
			@Override
			public void onClick(int which) {
				
				 if (mClient == null) {
					mClient = new VpHttpClient(PersonSettingActivity.this);
				 }
				 JSONObject data = new JSONObject();
				//获取用户信息
				 LoginUserInfoBean loginuser = LoginStatus.getLoginInfo();
				if (loginuser== null) {
					finish();
					return;
				}
				try {
					data.put("uid", loginuser.getUid());
					data.put("nickname", loginuser.getNickname());
					data.put("t_uid", mUserInfo.userId);
					data.put("t_nickname", mUserInfo.userName);
					data.put("cate_id", which);
					data.put("type", 0);//举报人
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				mClient.post(VpConstants.CHANNEL_USER_COMPLAIN, new RequestParams(), data.toString(), new AsyncHttpResponseHandler() {
					
					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						 	
						  String result = ResultParseUtil.deAesResult(responseBody);
					      NetBaseBean baseBean =  NetBaseBean.parseJson(result);
						  baseBean.showMsgToastInfo();
					}
					
					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						new NetBaseBean().showMsgToastInfo();
					}
				});
			}
		};
		
		  IOSActionSheetDialog reportSheet = new IOSActionSheetDialog(this)
			.builder()
			.setTitle("举报")
			.setCancelable(false)
			.setCanceledOnTouchOutside(false);
		  
		  if (reports!=null) {
			for (int i = 0; i < reports.length; i++) {
				reportSheet.addSheetItem(reports[i], SheetItemColor.Black, reportItemClickListener);
			}
			reportSheet.show();//展示
		  }
	  
	}
	
	
	/**
	 * 清理消息
	 */
	void clearChatMsg(){
		
		new IOSActionSheetDialog(this)
		.builder()
		.setTitle("确定删除和Ta的聊天记录吗？")
		.setCancelable(false)
		.setCanceledOnTouchOutside(false)
		.addSheetItem("确定", SheetItemColor.Green,
				new OnSheetItemClickListener() {
					@Override
					public void onClick(int which) {
						new AsyncTask<String,String, String>() {

							@Override
							protected String doInBackground(String... params) {
								ChatMessageDao.getInstance(PersonSettingActivity.this).deleteUserChat(loginUserInfo.xmppUser, mUserInfo.xmppUser);
								ChatMessage message = new ChatMessage();
								message.msgType = MsgType.clear_all_msg.getValue();
								
								//发送清理的广播 清理聊天界面的会话
								String action = BroadcastType.PRIVATE_CHAT_RECEVIE +"" + XmppUtils.getJidToUsername(mUserInfo.xmppUser);
								Intent intent = new Intent(action);
								intent.putExtra("chat_message", message);
								sendBroadcast(intent);
								return null;
							}
							
							@Override
							protected void onPostExecute(String result) {
								ToastUtil.show(PersonSettingActivity.this, "清理完成");
							}
						}.execute();
						
					}
	 })
	 .show();
		
	
	}
	
	
	public void updateView(){
		if (mUserInfo.onLineRemind >0) {
			onlineRemindToggle.setSelected(true);
		}else {
			onlineRemindToggle.setSelected(false);
		}
		
		if (mUserInfo.black >0) {
			goBlackToggle.setSelected(true);
		}else {
			goBlackToggle.setSelected(false);
		}
	}

}
