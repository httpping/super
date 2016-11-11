package com.vp.loveu.login.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.me.nereo.multi_image_selector.MultiImageSelectorActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.login.bean.UserOtherInfo;
import com.vp.loveu.login.ui.WelcomeActivity.SaveUserInfoCallBack;
import com.vp.loveu.login.widget.FancyCoverFlow;
import com.vp.loveu.login.widget.FancyCoverFlowSampleAdapter;
import com.vp.loveu.util.MUtil;
import com.vp.loveu.util.Prints;
import com.vp.loveu.widget.CircleImageView;

import cz.msebera.android.httpclient.Header;

/**
 * @author：pzj
 * @date: 2015-10-21 下午3:30:56
 * @Description:完善资料页面
 */
public class AddOtherUserInfoActivity extends VpActivity implements OnClickListener {
	 private FancyCoverFlow fancyCoverFlow;
	 private CircleImageView mIvPortrain;
	 private EditText mEtNickName;
	 private Button mBtnGetNickName;
	 private ArrayList<String> mSelectPath;//已选择图片
	 private static final int REQUEST_IMAGE = 2;
	/** 截取结束标志 */
	private static final int FLAG_MODIFY_FINISH = 1;
	private ArrayList<String> nicknames;
	private ArrayList<String> portraits;
	private String uid;
	private String xmppUser;
	private String xmppPwd;
	private float lat;
	private float lng;
	private boolean isCustomPortrait=false;//是否为自定义图片
	private String portraitUploadPath;//压缩后头像本地地址
	private String serverPortraitUrl;//上传到服务器中的地址
	private boolean isUploadSuccess=false;
	
	private TextView mTvSexMale;
	private TextView mTvSexFeMale;
	private int mSex=0;//1男 2女
	private DisplayImageOptions options;
	//第三方登录相关
	private int mLoginType;
	private String mThirdPortrait;
	private String mThirdNickName;
	private String mOpenUid;
	 
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_adduserinfo_activity);
		mClient=new VpHttpClient(this);
		mClient.setShowProgressDialog(false);
		this.uid=getIntent().getStringExtra(LoginUserInfoBean.LOGIN_UID);
		this.xmppUser=getIntent().getStringExtra(LoginUserInfoBean.XMPPUSER);
		this.xmppPwd=getIntent().getStringExtra(LoginUserInfoBean.XMPPPWD);
		this.mThirdPortrait=getIntent().getStringExtra(LoginUserInfoBean.THIRDPORTRAIT);
		this.mThirdNickName=getIntent().getStringExtra(LoginUserInfoBean.THIRDNICKNAME);
		this.mOpenUid=getIntent().getStringExtra(LoginUserInfoBean.THIRDOPENID);
		this.mLoginType=getIntent().getIntExtra(LoginUserInfoBean.LOGINTYPE, LoginUserInfoBean.LOGINTYPE_PHONE);
		this.lat=getIntent().getFloatExtra(LoginUserInfoBean.LAT, 0);
		this.lng=getIntent().getFloatExtra(LoginUserInfoBean.LNG, 0);
		options = new DisplayImageOptions.Builder()
		         .showImageOnLoading(R.drawable.default_portrait) // resource or
		        .showImageForEmptyUri(R.drawable.default_portrait) // resource or
		        .showImageOnFail(R.drawable.ic_launcher) // resource or
		        .resetViewBeforeLoading(false) // default
		        .cacheInMemory(true) // default
		        .cacheOnDisk(true) // default
		        .bitmapConfig(Bitmap.Config.RGB_565)  
		        .considerExifParams(false) // default
		        .displayer(new SimpleBitmapDisplayer())
		        .build();
		
		initView();
		initDatas();
		
		if(this.mLoginType!=LoginUserInfoBean.LOGINTYPE_PHONE){
            fancyCoverFlow.setVisibility(View.GONE);
            mBtnGetNickName.setVisibility(View.GONE);
            mIvPortrain.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(mThirdPortrait, mIvPortrain, options,new ImageLoadingListener() {
				
				@Override
				public void onLoadingStarted(String arg0, View arg1) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onLoadingComplete(String arg0, View arg1, Bitmap bitmap) {
					 //加载完成，把第三方图像下载到本地
					String desPath= VpConstants.IMAGEPATH
							+ MUtil.getNowDate("yyyyMMddHHmmss") + ".jpg";
					try {
						
						FileOutputStream out = new FileOutputStream(desPath);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
						out.close();
						portraitUploadPath =desPath;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				@Override
				public void onLoadingCancelled(String arg0, View arg1) {
					// TODO Auto-generated method stub
					
				}
			});
            
            mEtNickName.setText(mThirdNickName);
		}
	}



	private void initView() {
		initPublicTitle();
		this.mPubTitleView.mBtnLeft.setVisibility(View.GONE);
		this.mPubTitleView.mTvTitle.setText("补充资料");
		this.mPubTitleView.mBtnRight.setText("完成");
		
        this.fancyCoverFlow = (FancyCoverFlow) this.findViewById(R.id.login_fancyCoverFlow);
        this.mIvPortrain=(CircleImageView)this.findViewById(R.id.login_portrain_iv);
        this.mEtNickName=(EditText) this.findViewById(R.id.login_et_nickname);
        this.mBtnGetNickName=(Button) this.findViewById(R.id.login_btn_getnickname);
        this.mTvSexMale=(TextView) this.findViewById(R.id.login_tv_sex_male);
        this.mTvSexFeMale=(TextView) this.findViewById(R.id.login_tv_sex_female);
        mPubTitleView.mBtnRight.setOnClickListener(this);
        mTvSexMale.setOnClickListener(this);
        mTvSexFeMale.setOnClickListener(this);
        
        this.mBtnGetNickName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getNickNameRandom();
				
			}
		});
        		
        fancyCoverFlow.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectPhoto();
				
			}
		});
        mIvPortrain.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectPhoto();
				
			}
		});
        
        mEtNickName.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				setSendBtnStatus();
				
			}
		});
	}

	private void initDatas() {
		//
		String url=VpConstants.USER_LOGIN_INFO;
		mClient.get(url, new RequestParams(), new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result=ResultParseUtil.deAesResult(responseBody);
				try {
					JSONObject json  = new JSONObject(result);
					String code = json.getString(VpConstants.HttpKey.CODE);
					if ("0".equals(code)) {//返回成功
						JSONObject jsonData = json.getJSONObject(VpConstants.HttpKey.DATA);
						Gson gson=new Gson();
						UserOtherInfo bean=gson.fromJson(jsonData.toString(), UserOtherInfo.class);
						if(bean!=null){
							nicknames=bean.getNicknames();
							portraits=bean.getPortraits();
							showPortraitsView();
						}
					} else {
						String message = json.getString(VpConstants.HttpKey.MSG);
						Toast.makeText(AddOtherUserInfoActivity.this, message,
								Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
	/**
	 * 上传头像
	 */
	private void upLoadPortrait(final String nickName,final int sex) {
		if(this.portraitUploadPath!=null){
			//上传头像
			String locPortrait=this.portraitUploadPath;
			mClient.postFile(VpConstants.FILE_UPLOAD, VpConstants.FILE_UPLOAD_PATH_PORTRAIT, locPortrait, true, true, true, new AsyncHttpResponseHandler() {
				
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String result=ResultParseUtil.deAesResult(responseBody);
					if(result!=null){
						JSONObject json = null;
						try {
							json = new JSONObject(result);
							String state = json.getString(VpConstants.HttpKey.STATE);
							if("1".equals(state)){//上传成功
								isUploadSuccess=true;
								serverPortraitUrl = json.getString(VpConstants.HttpKey.URL);//头像文件在服务器的保存访问地址
								//保存信息
								save(nickName, serverPortraitUrl, sex);
								
								//删除压缩后图片
								File f=new File(portraitUploadPath);
								if(f.exists() && f.isFile())
									f.delete();
								
							}else{
								mClient.stopProgressDialog();
								String message = json.getString(VpConstants.HttpKey.MSG);
								Toast.makeText(AddOtherUserInfoActivity.this, message,
										Toast.LENGTH_LONG).show();
							}
						} catch (JSONException e1) {
							e1.printStackTrace();
							mClient.stopProgressDialog();
						};
					}
					
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					mClient.stopProgressDialog();
					
				}
			});
		}

	}



	/**
	 * 完成
	 * @param v
	 */
	private void save(String nickName,String portrait,int sex){
		String url=VpConstants.USER_LOGIN_FILL_INFO;
		JSONObject data= new JSONObject();
		try {
			data.put("uid", Integer.parseInt(uid));
			data.put("nickname", nickName);
			data.put("portrait", portrait);
			data.put("sex", sex);//1=男，2=女
			
			mClient.post(url, new RequestParams(), data.toString(), false, new AsyncHttpResponseHandler() {
				
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					mClient.stopProgressDialog();
					String result=ResultParseUtil.deAesResult(responseBody);
					JSONObject json = null;
					try {
						json = new JSONObject(result);
						String code = json.getString(VpConstants.HttpKey.CODE);
						if("0".equals(code)){//返回成功
							//获取用户的基本信息
							LoginUserInfoBean bean=new LoginUserInfoBean(AddOtherUserInfoActivity.this);
							bean.saveLoginUserInfo(mLoginType,mOpenUid,uid,xmppUser,xmppPwd,"1",lat,lng,mClient,new SaveUserInfoCallBack() {
								
								@Override
								public void onSuccess() {
									finish();
									
								}
								
								@Override
								public void onFailed() {
									// TODO Auto-generated method stub
									
								}
							});
						}else{
							String message = json.getString(VpConstants.HttpKey.MSG);
							Toast.makeText(AddOtherUserInfoActivity.this, message,
									Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e1) {
						e1.printStackTrace();
					};
					
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					mClient.stopProgressDialog();
					
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
			mClient.stopProgressDialog();
		} 
	}
	
	
	protected void showPortraitsView() {
        this.fancyCoverFlow.setAdapter(new FancyCoverFlowSampleAdapter(this.portraits,new FancyCoverFlowCallBack() {
			
			@Override
			public void onFinish() {
				fancyCoverFlow.setSelection(1);
				
			}
		}));
        this.fancyCoverFlow.setUnselectedAlpha(0.5f);
        this.fancyCoverFlow.setUnselectedSaturation(0.0f);
        this.fancyCoverFlow.setUnselectedScale(0.5f);
        this.fancyCoverFlow.setSpacing(25);
        this.fancyCoverFlow.setMaxRotation(0);
        this.fancyCoverFlow.setScaleDownGravity(0.2f);
        this.fancyCoverFlow.setActionDistance(FancyCoverFlow.ACTION_DISTANCE_AUTO);
		
	}
	
	public interface FancyCoverFlowCallBack{
		public void onFinish();
	}

	/**
	 * 随机获取昵称
	 */
	protected void getNickNameRandom() {
		if(this.nicknames!=null && this.nicknames.size()>0){
			String name=mEtNickName.getText().toString();
			if(!TextUtils.isEmpty(name) && !nicknames.contains(name)){
				//先添加都随机昵称列表
				nicknames.add(name);
			}
			//从列表中随机产生一个昵称
			Random ran=new Random();
			while(true){
				int index=ran.nextInt(nicknames.size());
				if(!nicknames.get(index).equals(name)){
					mEtNickName.setText(nicknames.get(index));
					break;
				}
			}
			
		}else{
			Toast.makeText(this, "没有可用的昵称", Toast.LENGTH_SHORT).show();
		}
		
	}


	private void selectPhoto(){
        int selectedMode = MultiImageSelectorActivity.MODE_SINGLE;

        boolean showCamera = true;

        int maxNum = 9;

        Intent intent = new Intent(this, MultiImageSelectorActivity.class);
        // 是否显示拍摄图片
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, showCamera);
        // 最大可选择图片数量
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, maxNum);
        // 选择模式
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, selectedMode);
        // 默认选择
        if(mSelectPath != null && mSelectPath.size()>0){
            intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
        }
        startActivityForResult(intent, REQUEST_IMAGE);
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {
        	//选择照片后
        	if(requestCode == REQUEST_IMAGE && resultCode == RESULT_OK){
        		mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
        		
        		//打开裁剪界面
        		if (mSelectPath!= null && mSelectPath.size() > 0) {
        			Intent intent = new Intent(AddOtherUserInfoActivity.this,
        					ClipImageActivity.class);
        			intent.putExtra("path", mSelectPath.get(0));
        			startActivityForResult(intent, FLAG_MODIFY_FINISH);
				}else{
					Toast.makeText(this, "请选择照片", Toast.LENGTH_SHORT).show();
				}
        	}else if(requestCode == FLAG_MODIFY_FINISH && resultCode == RESULT_OK){
        		// 对图片进行裁剪后
				final String path = data.getStringExtra("path");
				Bitmap photo = BitmapFactory.decodeFile(path);
				portraitUploadPath = VpConstants.IMAGEPATH
						+ MUtil.getNowDate("yyyyMMddHHmmss") + ".jpg";
				// Bitmap photo = BitmapFactory.decodeFile(picPath);
				try {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 1; //
					System.out.println("图片大小之前" + photo.getWidth() + "--"
							+ photo.getHeight());
					photo = compressionBigBitmap(photo);
					int w = photo.getWidth();
					int h = photo.getHeight();
					System.out.println("图片大小之后" + photo.getWidth() + "--"
							+ photo.getHeight());
					FileOutputStream out = new FileOutputStream(portraitUploadPath);
					photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
					out.close();
					
					// 压缩正方形处理
//					Bitmap resource = BitmapFactory.decodeFile(portraitUploadPath);
//					int toW =w > h ? h : w  ;
//					Bitmap bitmap = Bitmap.createBitmap(resource, 0, 0, toW, toW); 
//					out = new FileOutputStream(portraitUploadPath);
//					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//					out.close();
	                fancyCoverFlow.setVisibility(View.GONE);
	                mIvPortrain.setVisibility(View.VISIBLE);
	                mIvPortrain.setImageBitmap(photo);
	                isCustomPortrait=true;

				} catch (FileNotFoundException e) {
					Prints.i("zeus", "file not found");
					e.printStackTrace();
				} catch (IOException e) {
					Prints.i("zeus", "io exception");
					e.printStackTrace();
				}
        	}
        	
        }
    }
    
	private Bitmap compressionBigBitmap(Bitmap bitmat2) {
		Bitmap destBitmap = null;
		if (bitmat2.getWidth() > 300) {
			float scaleValue = (float) (300f / bitmat2.getWidth());
			Matrix matrix = new Matrix();
			matrix.postScale(scaleValue, scaleValue);
			destBitmap = Bitmap.createBitmap(bitmat2, 0, 0, bitmat2.getWidth(),
					bitmat2.getHeight(), matrix, true);
		} else {
			return bitmat2;
		}
		return destBitmap;
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.public_top_save://保存
			mClient.startProgressDialog();
			String nickName=mEtNickName.getText().toString();
			if(isCustomPortrait){
				//自定义头像，需要先上传头像到服务器 
				if(isUploadSuccess){
					//已经成功上传头像，直接调用完善资料接口
					save(nickName, serverPortraitUrl, mSex);
				}else{		
					//先上传头像,在调用完善资料接口
					upLoadPortrait(nickName,mSex);
				}
			}else{
				String portrait;
				if(this.mLoginType!=LoginUserInfoBean.LOGINTYPE_PHONE){
//					portrait=this.mThirdPortrait;
					//先上传头像,在调用完善资料接口
					upLoadPortrait(nickName,mSex);
				}else{
					portrait=portraits.get(fancyCoverFlow.getSelectedItemPosition());
					save(nickName, portrait, mSex);
				}
			}
			
			break;
		case R.id.login_tv_sex_male:
			mSex=1;
			mTvSexMale.setSelected(true);
			mTvSexFeMale.setSelected(false);
			setSendBtnStatus();
			break;
		case R.id.login_tv_sex_female:
			mSex=2;
			mTvSexMale.setSelected(false);
			mTvSexFeMale.setSelected(true);
			setSendBtnStatus();
			break;
		default:
			break;
		}
		
	}
	
	
    private void setSendBtnStatus(){
		if(mSex>0 && !TextUtils.isEmpty(mEtNickName.getText().toString())){
			mPubTitleView.mBtnRight.setEnabled(true);
			mPubTitleView.mBtnRight.setTextColor(Color.parseColor("#10BB7D"));
		}else{
			mPubTitleView.mBtnRight.setEnabled(false);
			mPubTitleView.mBtnRight.setTextColor(Color.parseColor("#D8D8D8"));
		}
    }
	
}
