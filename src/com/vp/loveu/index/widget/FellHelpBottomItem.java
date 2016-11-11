package com.vp.loveu.index.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vp.loveu.R;
import com.vp.loveu.channel.widget.TopicPicContainer;
import com.vp.loveu.comm.ShowImagesViewPagerActivity;
import com.vp.loveu.index.bean.FellHelpBean.FellHelpBeanAskedBean;
import com.vp.loveu.index.bean.FellHelpBean.FellHelpBeanAudiosBean;
import com.vp.loveu.index.bean.FellHelpBean.FellHelpBeanData;
import com.vp.loveu.index.myutils.DisplayOptionsUtils;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.my.activity.MyCenterActivity;
import com.vp.loveu.my.activity.UserIndexActivity;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.UIUtils;
import com.vp.loveu.util.VPLog;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月19日下午5:21:00
 * @功能 情感求助底部的item样式
 * @作者 mi
 */

public class FellHelpBottomItem extends RelativeLayout implements OnClickListener {

	private ImageView mIvIcon;
	private TextView mTvName;
	private LinearLayout mIvContainer;
	private Button mBtPacket;
	private LinearLayout mAskContainer;
	private TextView mTvContent;
	private TextView mTvTeacher;
	private int width = 103;
	private int height = 63;
	private FellHelpBeanData data;
	private FellHelpBeanAskedBean bean;
	private String tag = "FellHelpBottomItem";

	public FellHelpBottomItem(Context context) {
		this(context, null);
	}

	public FellHelpBottomItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		View.inflate(context, R.layout.fell_help_bottom_item, this);
		mIvIcon = (ImageView) findViewById(R.id.fell_help_bottom_item_iv_logo);
		mTvName = (TextView) findViewById(R.id.fell_help_bottom_item_tv_name);
		mBtPacket = (Button) findViewById(R.id.fell_help_bottom_item_packet);
		mAskContainer = (LinearLayout) findViewById(R.id.fell_help_bottom_bottom_flg);
		mIvContainer = (LinearLayout) findViewById(R.id.fell_help_bottom_item_iv_acontainer);
		mTvContent = (TextView) findViewById(R.id.fell_help_bottom_item_tv_content);
		mTvTeacher = (TextView) findViewById(R.id.fell_help_bottom_tv_teacher);
	}

	public void setData(FellHelpBeanData data, FellHelpBeanAskedBean bean) {
		if (bean == null || data == null) {
			return;
		}
		this.data = data;
		this.bean = bean;
		
		mIvContainer.removeAllViews();
		mAskContainer.removeAllViews();
		
		mIvIcon.setOnClickListener(this);
		setHelpData(data, bean);//求助的内容

		List<FellHelpBeanAskedBean> replys = bean.replys;// 回答的内容
		if (replys != null && replys.size() > 0) {
			mTvTeacher.setVisibility(View.VISIBLE);
			mAskContainer.setVisibility(View.VISIBLE);
			setAnswerData(replys);
		}
	}

	/**
	 * 设置回复的内容
	 * @param replys
	 *            void TODO
	 */
	private void setAnswerData(List<FellHelpBeanAskedBean> data) {
		for (int i = 0; i < data.size(); i++) {
			final FellHelpBeanAskedBean bean = data.get(i);
			if (!TextUtils.isEmpty(bean.cont)) {
				TextView tv = new TextView(getContext());
				tv.setText(bean.cont);
				tv.setTextColor(Color.parseColor("#222222"));
				mAskContainer.addView(tv);
			}
			if (bean.pics != null && bean.pics.size() > 0) {
				LinearLayout layout = new LinearLayout(getContext());
				layout.setOrientation(LinearLayout.HORIZONTAL);
				mAskContainer.post(new Runnable() {
					@Override
					public void run() {
						TopicPicContainer ivContainer = new TopicPicContainer(getContext());
						mAskContainer.addView(ivContainer);
						ivContainer.setDatas((ArrayList<String>) bean.pics,mAskContainer.getWidth());
					}
				});
			}
			
			List<FellHelpBeanAudiosBean> audios = bean.audios;// 回复的语音 
			if (audios != null && audios.size() >0) {
				for (int z = 0; z < audios.size(); z++) {
					RecoderFrameLayout item = new RecoderFrameLayout(getContext());
					item.setIsplayer(false);
					item.setData(audios.get(z));
					mAskContainer.addView(item);
				}
			}
		}
	}

	/**
	 * 设置求助的内容
	 * @param data
	 * @param bean
	 *            void
	 */
	private void setHelpData(FellHelpBeanData data, final FellHelpBeanAskedBean bean) {
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (loginInfo == null) {
			return;
		}
		
		String nickname = "";
		String portrait = "";
		if (bean.uid == loginInfo.getUid()) {
			nickname = loginInfo.getNickname();
			portrait = loginInfo.getPortrait();
		}else{
			nickname = bean.nickname;
			portrait = bean.portrait;
		}
		
		mTvName.setText(nickname);
		ImageLoader.getInstance().displayImage(portrait, mIvIcon, DisplayOptionsUtils.getOptionsConfig());
		if (!TextUtils.isEmpty(bean.cont)) {
			mTvContent.setText(bean.cont);
		}
		// 加载求助的图片
		if (bean.pics != null && bean.pics.size() > 0) { //说明有图片
			mIvContainer.setVisibility(View.VISIBLE);
			mIvContainer.post(new Runnable() {
				@Override
				public void run() {
					TopicPicContainer ivContainer = new TopicPicContainer(getContext());
					mIvContainer.addView(ivContainer,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
					ivContainer.setDatas((ArrayList<String>) bean.pics,mIvContainer.getWidth());
				}
			});
		}

		// 求助的语音 TODO：
		List<FellHelpBeanAudiosBean> audios = bean.audios;
		int type = bean.type;
		if (type == 1) {
			mBtPacket.setVisibility(View.GONE);
		} else if (type == 2) {
			mBtPacket.setVisibility(View.VISIBLE);
			mBtPacket.setText(data.talent_price + "元红包");
		} else if (type == 3) {
			mBtPacket.setText(data.expert_price + "元红包");
			mBtPacket.setVisibility(View.VISIBLE);
		}
	}

	private int getThreeIvWidth(LinearLayout mIvContainer) {
		int width = mIvContainer.getMeasuredWidth() - UIUtils.dp2px(10) * 2;
		return width / 3;
	}

	private void setIvOnclick(ImageView iv, final List<String> pics, final int i) {
		iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), ShowImagesViewPagerActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putStringArrayListExtra(ShowImagesViewPagerActivity.IMAGES, (ArrayList<String>) pics);
				intent.putExtra(ShowImagesViewPagerActivity.POSITION, i);
				getContext().startActivity(intent);
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v.equals(mIvIcon)) {
			if (bean == null) {
				return;
			}
			LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
			if (loginInfo == null) {
				return;
			}
			if (bean.uid == loginInfo.getUid()) {
				Intent intent = new Intent(getContext(), MyCenterActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getContext().startActivity(intent);
			} else {
				Intent intent = new Intent(getContext(), UserIndexActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(UserIndexActivity.KEY_UID, bean.uid);
				getContext().startActivity(intent);
			}
		}
	}
}
