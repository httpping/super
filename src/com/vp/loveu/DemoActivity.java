package com.vp.loveu;

import java.util.ArrayList;

import com.me.nereo.multi_image_selector.MultiImageSelectorActivity;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.login.bean.UserBaseInfoBean;
import com.vp.loveu.widget.ZanAllHeadView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.onekeyshare.custom.ShareDialogFragment;
import cn.sharesdk.onekeyshare.custom.ShareModel;

/**
 * @author：pzj
 * @date: 2015-10-21 下午3:30:56
 * @Description:测试..
 */
public class DemoActivity extends VpActivity {
	private Handler mHandler=new Handler();
	private ZanAllHeadView mHeadView;
	private TextView mTvphotoPath;
	private Button mBtnShare;
	
	private ArrayList<String> mSelectPath;//已选择图片
	private static final int REQUEST_IMAGE = 2;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo_activity);
		this.mBtnShare=(Button) findViewById(R.id.btn_share);
		initPublicTitle();
		initView();
	}
	
	public void aaa(View v){
		Toast.makeText(this, "aaa", 0).show();
	}
	
	//图片选择测试 
	public void selectPhoto(View v){
        int selectedMode = MultiImageSelectorActivity.MODE_MULTI;

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
        if(requestCode == REQUEST_IMAGE){
            if(resultCode == RESULT_OK){
                mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
               mTvphotoPath.setText(mSelectPath.toString());

            }
        }
    }


    //测试自定义view
	private void initView() {
//		this.mPubTitleView.mTvTitle.setText("Loading");
		this.mPubTitleView.mBtnRight.setBackground(getResources().getDrawable(R.drawable.ic_launcher));
		this.mHeadView=(ZanAllHeadView) findViewById(R.id.headView);
		this.mTvphotoPath=(TextView) findViewById(R.id.photo_path);
		
		//TODO test
		ArrayList<UserBaseInfoBean> list=new ArrayList<UserBaseInfoBean>();
		UserBaseInfoBean u1=new UserBaseInfoBean();
		UserBaseInfoBean u2=new UserBaseInfoBean();
		UserBaseInfoBean u3=new UserBaseInfoBean();
		UserBaseInfoBean u4=new UserBaseInfoBean();
		UserBaseInfoBean u5=new UserBaseInfoBean();
		UserBaseInfoBean u6=new UserBaseInfoBean();
		UserBaseInfoBean u7=new UserBaseInfoBean();
		UserBaseInfoBean u8=new UserBaseInfoBean();
		UserBaseInfoBean u9=new UserBaseInfoBean();
		UserBaseInfoBean u10=new UserBaseInfoBean();
		UserBaseInfoBean u11=new UserBaseInfoBean();
		UserBaseInfoBean u12=new UserBaseInfoBean();
		u1.setPortrait("http://172.16.0.86:8080/bb.png");
		u1.setPortrait("http://172.16.0.86:8080/bb.png");
		u3.setPortrait("http://172.16.0.86:8080/bb.png");
		u4.setPortrait("http://172.16.0.86:8080/bb.png");
		u5.setPortrait("http://172.16.0.86:8080/bb.png");
		u6.setPortrait("http://172.16.0.86:8080/bb.png");
		u7.setPortrait("http://172.16.0.86:8080/bb.png");
		u8.setPortrait("http://172.16.0.86:8080/bb.png");
		u9.setPortrait("http://172.16.0.86:8080/bb.png");
		u10.setPortrait("http://172.16.0.86:8080/bb.png");
		u11.setPortrait("http://172.16.0.86:8080/bb.png");
		u12.setPortrait("http://172.16.0.86:8080/bb.png");
		list.add(u1);
		list.add(u2);
		list.add(u3);
		list.add(u4);
//		list.add(u5);
//		list.add(u6);
//		list.add(u7);
//		list.add(u8);
//		list.add(u9);
//		list.add(u10);
//		list.add(u11);
//		list.add(u12);
//		mHeadView.setDatas(list,true);
		mHeadView.setDatas(list);
		
//		mHandler.postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				Intent intent=new Intent(WelcomeActivity.this, LoginActivity.class);
//				startActivity(intent);
//				finish();
//				
//			}
//		}, 3000);
		
	}
	
	public void share(View v){	
		ShareModel model=new ShareModel();
	model.setTitle("test_title");
	model.setText("test_content");
	model.setUrl("http://www.baidu.com");
	Bitmap bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
	model.setImageLocal(bitmap);
	model.setImageUrl("http://e.hiphotos.baidu.com/image/pic/item/d1a20cf431adcbef326c603ba8af2edda3cc9fb6.jpg");
	
	ShareDialogFragment dialog=new ShareDialogFragment();
	dialog.show(getSupportFragmentManager(), "share", model);
	}
	
	

}
