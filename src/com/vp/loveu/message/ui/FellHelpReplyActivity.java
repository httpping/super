package com.vp.loveu.message.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.VpHttpClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.message.utils.SendTopicUtils;
import com.vp.loveu.message.utils.SendTopicUtils.SendTopCallback;
import com.vp.loveu.widget.CircleImageView;

/**
 * @author：pzj
 * @date: 2015年11月26日 下午4:11:05
 * @Description:邀请情感回复界面
 */
public class FellHelpReplyActivity extends VpActivity implements OnClickListener, TextWatcher {
	public static final String TOPIC_ID="topic_id";
	public static final String TOPIC_PORTRAIT="portrait";
	public static final String TOPIC_NAME="nikeName";
	public static final String TOPIC_CONT="cont";
	public static final String TOPIC_PIC="pic";
	public static final String TOPIC_TIME="time";	
	private int mTopId;
	private String mPortrait;
	private String mNickName;
	private String mCont;
	private String mTime;
	private boolean mHasPic;
	
	private EditText mEtReplyCont;
	private ImageView mUploadImage;
	private CircleImageView mIvPortrait;
	private TextView mTvNickName;
	private TextView mTvTime;
	private TextView mTvCont;
	private ImageView mDefaultImage;
	private LinearLayout mLlpicContainer;
	private DisplayImageOptions options;
	private static final int REQUEST_IMAGE = 0;
	public static final String CONT="cont";

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channel_topic_reply_activity);
		this.mClient=new VpHttpClient(this);
		this.mTopId=getIntent().getIntExtra(TOPIC_ID, -1);
		this.mPortrait=getIntent().getStringExtra(TOPIC_PORTRAIT);
		this.mNickName=getIntent().getStringExtra(TOPIC_NAME);
		this.mCont=getIntent().getStringExtra(TOPIC_CONT);
		this.mTime=getIntent().getStringExtra(TOPIC_TIME);
		this.mHasPic=getIntent().getBooleanExtra(TOPIC_PIC, false);
//		this.mUploadSuccessPathList=new ArrayList<String>();
		this.mClient=new VpHttpClient(this);
		this.mClient.setShowProgressDialog(false);
		initView();
		initDatas();
	}


	private void initView() {
		initPublicTitle();
		mPubTitleView.mBtnLeft.setText("");
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		this.mPubTitleView.mTvTitle.setText("帮他解答");		
		mPubTitleView.mBtnRight.setText("发送");
		this.mEtReplyCont=(EditText) findViewById(R.id.channel_topic_reply_cont);
		setSendBtnStatus();
		this.mUploadImage=(ImageView) findViewById(R.id.channel_topic_reply_iv_upload);
		this.mDefaultImage=(ImageView) findViewById(R.id.channel_topic_reply_default_icon);
		this.mIvPortrait=(CircleImageView) findViewById(R.id.channel_topic_reply_iv_portrait);
		this.mTvNickName=(TextView) findViewById(R.id.channel_topic_reply_tv_nickname);
		this.mTvTime=(TextView) findViewById(R.id.channel_topic_reply_tv_time);
		this.mTvCont=(TextView) findViewById(R.id.channel_topic_reply_tv_cont);
		this.mLlpicContainer=(LinearLayout) findViewById(R.id.channel_topic_reply_pic_container);
		this.mLlpicContainer.setVisibility(View.GONE);
		this.mUploadImage.setOnClickListener(this);
		this.mEtReplyCont.addTextChangedListener(this);
		mPubTitleView.mBtnRight.setOnClickListener(this);
		 options = new DisplayImageOptions.Builder()
		         .showImageOnLoading(R.drawable.default_image_loading) // resource or
		        .showImageForEmptyUri(R.drawable.default_image_loading_fail) // resource or
		        .showImageOnFail(R.drawable.default_image_loading_fail) // resource or
		        .resetViewBeforeLoading(false) // default
		        .cacheInMemory(true) // default
		        .cacheOnDisk(true) // default
		        .bitmapConfig(Bitmap.Config.RGB_565)  
		        .considerExifParams(false) // default
		        .displayer(new SimpleBitmapDisplayer())
		        .build();
	}


	private void initDatas() {
		ImageLoader.getInstance().displayImage(this.mPortrait, this.mIvPortrait, options);
		this.mTvNickName.setText(this.mNickName);
		this.mTvTime.setText(this.mTime);
		this.mTvCont.setText(this.mCont);
		this.mDefaultImage.setVisibility(this.mHasPic?View.VISIBLE:View.GONE);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.public_top_save://发表
			SendTopicUtils topUtils=new SendTopicUtils(this, mClient);
			topUtils.sendTopic(this.mTopId,this.mEtReplyCont.getText().toString(),null, new SendTopCallback() {
				
				@Override
				public void onSuccess() {
					//发表成功
					Toast.makeText(FellHelpReplyActivity.this, "回复成功",Toast.LENGTH_LONG).show();
					//返回页面
					Intent data=new Intent();
					data.putExtra(CONT, mEtReplyCont.getText().toString());
					setResult(RESULT_OK, data);
					finish();
					
				}
				
				@Override
				public void onFailed(String msg) {
					Toast.makeText(FellHelpReplyActivity.this, msg, Toast.LENGTH_SHORT).show();
					
				}
			});
			break;
		default:
			break;
		}
		
	}

    
    private void setSendBtnStatus(){
		if(!TextUtils.isEmpty(mEtReplyCont.getText().toString())){
			mPubTitleView.mBtnRight.setEnabled(true);
			mPubTitleView.mBtnRight.setTextColor(Color.parseColor("#46A680"));
		}else{
			mPubTitleView.mBtnRight.setEnabled(false);
			mPubTitleView.mBtnRight.setTextColor(Color.parseColor("#D8D8D8"));
		}
    }


    /**
     * edittext valuechanle listener
     * @param s
     * @param start
     * @param count
     * @param after
     */
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}


	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		setSendBtnStatus();
	}
}
