package com.vp.loveu.channel.ui;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.VpHttpClient;
import com.me.nereo.multi_image_selector.MultiImageSelectorActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.channel.utils.SendTopicUtils;
import com.vp.loveu.channel.utils.SendTopicUtils.SendTopCallback;
import com.vp.loveu.comm.ShowImagesViewPagerActivity;
import com.vp.loveu.index.bean.FellHelpBean.FellHelpBeanAudiosBean;
import com.vp.loveu.index.widget.RecoderFrameLayout;
import com.vp.loveu.my.activity.MyCenterActivity;
import com.vp.loveu.my.activity.UserIndexActivity;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.UIUtils;
import com.vp.loveu.widget.CircleImageView;

/**
 * @author：pzj
 * @date: 2015年11月26日 下午4:11:05
 * @Description:大家一起聊回复界面
 */
public class ChannelTopicReplyActivity extends VpActivity implements OnClickListener, TextWatcher {
	public static final String TOPIC_ID="topic_id";
	public static final String TOPIC_RID="topic_rid";
	public static final String TOPIC_UID="topic_uid";
	public static final String TOPIC_PORTRAIT="portrait";
	public static final String TOPIC_NAME="nikeName";
	public static final String TOPIC_CONT="cont";
	public static final String TOPIC_PIC="pic";
	public static final String TOPIC_TIME="time";	
	private int mTopId;
	private int mTopRid;
	private int mTopUid;
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
	private ArrayList<String> mSelectedPicList;
	private RecoderFrameLayout mRadio;//音频
	private FellHelpBeanAudiosBean  mAudio;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channel_topic_reply_activity);
		this.mClient=new VpHttpClient(this);
		this.mTopId=getIntent().getIntExtra(TOPIC_ID, -1);
		this.mTopRid=getIntent().getIntExtra(TOPIC_RID, -1);
		this.mTopUid=getIntent().getIntExtra(TOPIC_UID, -1);
		this.mPortrait=getIntent().getStringExtra(TOPIC_PORTRAIT);
		this.mNickName=getIntent().getStringExtra(TOPIC_NAME);
		this.mCont=getIntent().getStringExtra(TOPIC_CONT);
		this.mTime=getIntent().getStringExtra(TOPIC_TIME);
		this.mHasPic=getIntent().getBooleanExtra(TOPIC_PIC, false);
		mAudio = (FellHelpBeanAudiosBean) getIntent().getSerializableExtra("audio");
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
		this.mPubTitleView.mTvTitle.setText("回复");		
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
		mRadio = (RecoderFrameLayout) findViewById(R.id.radio);
		
		if (mAudio!=null) {
			//视音频的话  直接显示
			mRadio.setVisibility(View.VISIBLE);
			mRadio.setData(mAudio);
			mTvCont.setVisibility(View.GONE);
		}else{
			mRadio.setVisibility(View.GONE);
			mTvCont.setVisibility(View.VISIBLE);
		}
		
		this.mUploadImage.setOnClickListener(this);
		this.mIvPortrait.setOnClickListener(this);
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
		case R.id.channel_topic_reply_iv_upload :
        	Intent intent = new Intent(this, MultiImageSelectorActivity.class);
        // 是否显示拍摄图片
        	intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        // 最大可选择图片数量
        	intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 3);
        // 选择模式
        	intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        // 默认选择
        	if(mSelectedPicList != null && mSelectedPicList.size()>0){
        		intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectedPicList);
        	}
        	startActivityForResult(intent, REQUEST_IMAGE);
			
			break;
		case R.id.public_top_save://发表
			SendTopicUtils topUtils=new SendTopicUtils(this, mClient);
			topUtils.sendTopic(this.mTopId, this.mTopRid, this.mEtReplyCont.getText().toString(), this.mSelectedPicList, new SendTopCallback() {
				
				@Override
				public void onSuccess() {
					//发表成功
					Toast.makeText(ChannelTopicReplyActivity.this, "回复成功",Toast.LENGTH_LONG).show();
					//返回页面
					setResult(RESULT_OK);
					finish();
					
				}
				
				@Override
				public void onFailed(String msg) {
					Toast.makeText(ChannelTopicReplyActivity.this, msg, Toast.LENGTH_SHORT).show();
					
				}
			});
			break;
			
		case R.id.channel_topic_reply_iv_portrait:
			int currentUid = mTopUid;
			//点击头像
			if (currentUid == LoginStatus.getLoginInfo().getUid()) {
				startActivity(new Intent(UIUtils.getContext(), MyCenterActivity.class));
			}else{
				Intent intent1 = new Intent(UIUtils.getContext(), UserIndexActivity.class);
				intent1.putExtra(UserIndexActivity.KEY_UID,currentUid);
				startActivity(intent1);
			}
			break;
		default:
			break;
		}
		
	}
    

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE){
            if(resultCode == RESULT_OK){
            	mSelectedPicList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            	if(mSelectedPicList!=null){
            		mLlpicContainer.removeAllViews();
            		LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(UIUtils.dp2px(65),UIUtils.dp2px(65));
            		for (int i=0;i<mSelectedPicList.size();i++) {
        				ImageView iv=new ImageView(ChannelTopicReplyActivity.this);
        				Options options=new Options();
        				options.inSampleSize=4;
        				Bitmap bitmap=BitmapFactory.decodeFile(mSelectedPicList.get(i),options);
        				iv.setImageBitmap(bitmap);
        				iv.setScaleType(ScaleType.CENTER_CROP);        				
        				params.rightMargin=UIUtils.dp2px(10);
        				mLlpicContainer.addView(iv,params);
        				
        				createPicClickListener(iv, mSelectedPicList, i);
        				
					}
            		ImageView uploadView=new ImageView(ChannelTopicReplyActivity.this);
            		uploadView.setId(R.id.channel_topic_reply_iv_upload);
            		uploadView.setOnClickListener(this);
            		uploadView.setImageResource(R.drawable.channel_upload_pic);
            		mLlpicContainer.addView(uploadView, params);
            	}
            	//设置发送按钮状态
            	setSendBtnStatus();
            }
        }
    }
    
    private void setSendBtnStatus(){
		if((mSelectedPicList!=null && mSelectedPicList.size()>0) || !TextUtils.isEmpty(mEtReplyCont.getText().toString())){
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
	
	
	public void createPicClickListener(ImageView imageView, final ArrayList<String> pics,final int position) {
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(ChannelTopicReplyActivity.this,ShowImagesViewPagerActivity.class);
				intent.putStringArrayListExtra(ShowImagesViewPagerActivity.IMAGES, pics);
				intent.putExtra(ShowImagesViewPagerActivity.POSITION, position);
				startActivity(intent);
				
			}
		});
	}
}
