package com.vp.loveu.discover.ui;

import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.discover.bean.MatchMakerBean;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.message.bean.ChatMessage;
import com.vp.loveu.message.bean.ChatMessage.MsgShowType;
import com.vp.loveu.message.bean.ChatMessage.MsgType;
import com.vp.loveu.message.bean.UserInfo;
import com.vp.loveu.message.db.ChatMessageDao;
import com.vp.loveu.message.db.UserInfoDao;
import com.vp.loveu.service.XmppService;
import com.vp.loveu.service.XmppService.XmppServiceBinder;
import com.vp.loveu.service.util.SendPacketRun.SendPacketListener;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.VPLog;
import com.vp.loveu.widget.CircleImageView;

import cz.msebera.android.httpclient.Header;

/**
 * @author：pzj
 * @date: 2015年11月23日 下午2:09:50
 * @Description:红娘一对一页面
 */
public class MatchmakerActivity extends VpActivity implements OnClickListener {
	private CircleImageView mIvPortrait;
	private View mRootView;
	private TextView mTvMakerName;
	private TextView mTvMakerChange;
	private TextView mTvHelpCount;
	private TextView mTvSexMale;
	private TextView mTvSexFemale;
	private EditText mEtRemarks;
	private Button mBtnCommit;
	private View mViewLine;
	private boolean isMale=true;
//    private float mDistance;
    private DisplayImageOptions options;
	private int mLimit=10;//获取记录条数
	private int mPage=1;//分页页码
	private ArrayList<MatchMakerBean> mMatchMakerList;
	private MatchMakerBean mSelectedMaker;
	private boolean isInit=true;//首次进入页面
	
	XmppServiceBinder mBinder;
	ServiceConnection mServiceConnection;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.discover_matchmaker_activity);
		this.mClient=new VpHttpClient(this);
		initView();
		initDatas();
		
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
	protected void onDestroy() {
		super.onDestroy();
		if (mServiceConnection !=null && mBinder!=null) {
		    unbindService(mServiceConnection);
		}
	}

	private void initView() {
		initPublicTitle();
		this.mPubTitleView.mBtnLeft.setText("");
		this.mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		this.mPubTitleView.mTvTitle.setText("红娘一对一");
		this.mRootView=findViewById(R.id.matchmaker_root_view);
		this.mIvPortrait=(CircleImageView) findViewById(R.id.discover_maker_portrait);
		this.mTvMakerName=(TextView) findViewById(R.id.discover_maker_name);
		this.mTvMakerChange=(TextView) findViewById(R.id.discover_maker_change);
		this.mTvHelpCount=(TextView) findViewById(R.id.discover_help_count);
		this.mTvSexMale=(TextView) findViewById(R.id.discover_sex_male);
		this.mTvSexFemale=(TextView) findViewById(R.id.discover_sex_female);
		this.mViewLine=findViewById(R.id.discover_sex_underline);
		this.mBtnCommit=(Button) findViewById(R.id.discover_btn_commit);
		this.mEtRemarks=(EditText) findViewById(R.id.discover_remarks);
		this.mTvSexMale.setOnClickListener(this);
		this.mTvSexFemale.setOnClickListener(this);
		this.mBtnCommit.setOnClickListener(this);
		mIvPortrait.setOnClickListener(this);
		
		this.mTvSexMale.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {


			@Override @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onGlobalLayout() {
            	
        		android.view.ViewGroup.LayoutParams layoutParams = mViewLine.getLayoutParams();
        		layoutParams.width=mTvSexMale.getWidth();
        		
        		//男性用户登录默认选找女朋友
        		if(isInit && 1==LoginStatus.getLoginInfo().getSex()){
        			ObjectAnimator.ofFloat(mViewLine, "translationX",mTvSexMale.getX() , mTvSexFemale.getX()).setDuration(0).start();
        			mTvSexMale.setTextColor(Color.parseColor("#666666"));
        			mTvSexFemale.setTextColor(Color.parseColor("#FF8000"));
        			isMale=false;
        			isInit=false;
        		}
        		
        		
                Rect rect = new Rect();
                //获取root在窗体的可视区域
                mRootView.getWindowVisibleDisplayFrame(rect);
                //获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)
                int rootInvisibleHeight = mRootView.getRootView().getHeight() - rect.bottom;
                //若不可视区域高度大于100，则键盘显示
                if (rootInvisibleHeight > 100) {
                    int[] location = new int[2];
                    //获取scrollToView在窗体的坐标
                    mEtRemarks.getLocationInWindow(location);
                    //计算root滚动高度，使scrollToView在可见区域的底部
                    int srollHeight = (location[1] + mEtRemarks.getHeight()) - rect.bottom;
                    mRootView.scrollTo(0, srollHeight);
                } else {
                    //键盘隐藏
                	mRootView.scrollTo(0, 0);
                }
        		

            }
        });
		
		options = new DisplayImageOptions.Builder()
		         .showImageOnLoading(R.color.frenchgrey) // resource or
		        .showImageForEmptyUri(R.drawable.default_portrait) // resource or
		        .showImageOnFail(R.drawable.default_portrait) // resource or
		        .resetViewBeforeLoading(false) // default
		        .cacheInMemory(true) // default
		        .cacheOnDisk(true) // default
		        .bitmapConfig(Bitmap.Config.RGB_565)  
		        .considerExifParams(false) // default
		        .displayer(new SimpleBitmapDisplayer())
		        .build();
		
		
	}

	private void initDatas() {
		String url = VpConstants.DISCOVER_MATCHMARKER_LIST;
		RequestParams params = new RequestParams();
		params.put("limit", this.mLimit);
		params.put("page", this.mPage);
		mClient.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString(VpConstants.HttpKey.CODE);
					if ("0".equals(code)) {// 返回成功
						JSONArray jsonData=new JSONArray(json.getString(VpConstants.HttpKey.DATA));
						mMatchMakerList  = MatchMakerBean.parseArrayJson(jsonData);
						setViewDatas();
					} else {
						String message = json.getString(VpConstants.HttpKey.MSG);
						Toast.makeText(MatchmakerActivity.this, message, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(MatchmakerActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();

			}
		});
		
	
		
	}
	protected void setViewDatas() {
		if(mMatchMakerList!=null && mMatchMakerList.size()>0){
			//随机取出一个红娘信息
			int index = new Random().nextInt(mMatchMakerList.size());
			mSelectedMaker = mMatchMakerList.get(index);
			ImageLoader.getInstance().displayImage(mSelectedMaker.getPortrait(), mIvPortrait, options);
			this.mTvMakerName.setText("红娘："+mSelectedMaker.getNickname());
			
			String msg="已成功帮助"+mSelectedMaker.getUser_num()+"位用户";
			SpannableStringBuilder style=new SpannableStringBuilder(msg);
			style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.normal_bg_orange)), msg.indexOf("助")+1, msg.indexOf("位"), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			this.mTvHelpCount.setText(style);
		}
		
	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.discover_sex_male:
			if(!isMale){
				ObjectAnimator.ofFloat(mViewLine, "translationX", mTvSexFemale.getX(),mTvSexMale.getX()).setDuration(500).start();
				isMale=true;
			}
			mTvSexMale.setTextColor(Color.parseColor("#FF8000"));
			mTvSexFemale.setTextColor(Color.parseColor("#666666"));
			break;
		case R.id.discover_sex_female:
			if(isMale){
				ObjectAnimator.ofFloat(mViewLine, "translationX",mTvSexMale.getX() , mTvSexFemale.getX()).setDuration(500).start();
				isMale=false;
			}
			mTvSexMale.setTextColor(Color.parseColor("#666666"));
			mTvSexFemale.setTextColor(Color.parseColor("#FF8000"));
			break;
		case R.id.discover_btn_commit:
			//提交
			doSubmit();
			break;

		case R.id.discover_maker_portrait:
			//用户主页
			goOtherUserInfo(mSelectedMaker.getUid());
			break;
		default:
			break;
		}
		
	}

	/**
	 * 提交信息
	 */
	private void doSubmit() {
		int uid=LoginStatus.getLoginInfo().getUid();
		if(mSelectedMaker==null){
			Toast.makeText(this, "请选择红娘", Toast.LENGTH_SHORT).show();
			return;
		}
		final String remarks=this.mEtRemarks.getText().toString();
		if(TextUtils.isEmpty(remarks)){
			Toast.makeText(this, "请输入描述信息", Toast.LENGTH_SHORT).show();
			return;
		}
		int mid=mSelectedMaker.getUid();
		int sex=isMale?1:2;
		String url=VpConstants.DISCOVER_MATCHMARKER_APPLY;
		JsonObject body=null;
		try {
			body=new JsonObject();
			body.addProperty("uid", uid);
			body.addProperty("mid", mid);
			body.addProperty("sex", sex);
			body.addProperty("remark", remarks);
		} catch (Exception e) {
			Toast.makeText(this, "参数错误", Toast.LENGTH_SHORT).show();
		}
		this.mClient.post(url, new RequestParams(), body.toString(), new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString(VpConstants.HttpKey.CODE);
					if ("0".equals(code)) {// 返回成功
						//TODO 发送成功 以IM短信形式发送推送信息给红娘 某某（13855624235）想找男朋友，希望：xxxxxx
						LoginUserInfoBean mine = LoginStatus.getLoginInfo();
						String sendMsg="";
						if(mine.getLoginType()==LoginUserInfoBean.LOGINTYPE_PHONE){
							sendMsg=mine.getNickname()+"("+mine.getMt()+") 想找"+(isMale?"男":"女")+"朋友,希望:"+remarks;
						}else{
							sendMsg=mine.getNickname()+"(ID: "+mine.getUid()+") 想找"+(isMale?"男":"女")+"朋友,希望:"+remarks;
						}
						
						ChatMessage message = new ChatMessage();
						message.txt=sendMsg;
						 UserInfo userInfo = new UserInfo();
						 userInfo.xmppUser = mine.getXmpp_user().toLowerCase();
						 userInfo.headImage = mine.getPortrait();
						 userInfo.userName = mine.getNickname();
						 userInfo.userId = mine.getUid()+"";
						 UserInfoDao.getInstance(MatchmakerActivity.this).saveOrUpdate(userInfo);
						 
						 
						 //红娘 的user info 
						 UserInfo hl = new UserInfo();
						 hl.xmppUser = mSelectedMaker.getXmpp_user().toLowerCase();
						 hl.headImage = mSelectedMaker.getPortrait();
						 hl.userName = mSelectedMaker.getNickname();
						 hl.userId = mSelectedMaker.getUid()+"";
						 UserInfoDao.getInstance(MatchmakerActivity.this).saveOrUpdate(hl);
						 
						 message.fromUserInfo = userInfo;
						 message.to =mSelectedMaker.getXmpp_user();
						 message.msgType = MsgType.txt.getValue();
						 message.showType = MsgShowType.out.ordinal();
						 message.loginUser = mine.getXmpp_user().toLowerCase();
						 message.otherUser = hl.xmppUser;
						 message.createBody();
						 long id = ChatMessageDao.getInstance(MatchmakerActivity.this).saveOrUpdate(message);
						 message.id = id ;
						
						 //save
						 mBinder.getService().sendPacket(mSelectedMaker.getXmpp_user().toLowerCase(), message.toJsonObject().toString(), null, id+"", new SendPacketListener(){

							@Override
							public void onFinish(String command, int result, String packet) {
									if(result >0 ){
										startActivity(new Intent(MatchmakerActivity.this,MatchmakerResultActivity.class));
										finish();
									}
							}
							 
						 });
					} else {
						String message = json.getString(VpConstants.HttpKey.MSG);
						Toast.makeText(MatchmakerActivity.this, message, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(MatchmakerActivity.this, "网络访问错误", Toast.LENGTH_SHORT).show();
				
			}
		});
		
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
	    if (ev.getAction() == MotionEvent.ACTION_DOWN) {  
	        View v = getCurrentFocus();  
	        if (isShouldHideInput(v, ev)) {  
	  
	            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
	            if (imm != null) {  
	                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
	            }  
	        }  
	        return super.dispatchTouchEvent(ev);  
	    }  
	    // 必不可少，否则所有的组件都不会有TouchEvent了  
	    if (getWindow().superDispatchTouchEvent(ev)) {  
	        return true;  
	    }  
	    return onTouchEvent(ev);  
	}
	
	public  boolean isShouldHideInput(View v, MotionEvent event) {  
	    if (v != null && (v instanceof EditText)) {  
	        int[] leftTop = { 0, 0 };  
	        //获取输入框当前的location位置  
	        v.getLocationInWindow(leftTop);  
	        int left = leftTop[0];  
	        int top = leftTop[1];  
	        int bottom = top + v.getHeight();  
	        int right = left + v.getWidth();  
	        if (event.getX() > left && event.getX() < right  
	                && event.getY() > top && event.getY() < bottom) {  
	            // 点击的是输入框区域，保留点击EditText的事件  
	            return false;  
	        } else {  
	            return true;  
	        }  
	    }  
	    return false;  
	} 
	
}
