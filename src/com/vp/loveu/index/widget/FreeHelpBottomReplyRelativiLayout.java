package com.vp.loveu.index.widget;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vp.loveu.R;
import com.vp.loveu.channel.widget.TopicPicContainer;
import com.vp.loveu.index.bean.MySeekHelpBean.MySeekAudioBean;
import com.vp.loveu.index.bean.MySeekHelpBean.MySeekDataBean;
import com.vp.loveu.index.myutils.DisplayOptionsUtils;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.message.utils.DensityUtil;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.VpDateUtils;
import com.vp.loveu.widget.CircleImageView;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月20日下午5:01:27
 * @功能 我的求助的askweritem
 * @作者 mi
 */

public class FreeHelpBottomReplyRelativiLayout extends RelativeLayout {

	private CircleImageView mIvIcon;// 头像
	private TextView mTvNickName;// 名称
	private TextView mTvRightTime;// 右边的回复时间
	private View mLine;// 线
	private int width = 103;
	private int height = 63;
	private LinearLayout mAskContainer;
	private String tag = "FreeHelpBottomReplyRelativiLayout";

	public FreeHelpBottomReplyRelativiLayout(Context context) {
		this(context, null);
	}

	public FreeHelpBottomReplyRelativiLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		View.inflate(context, R.layout.free_help_bottom_reply_item, this);
		initView();
	}

	private void initView() {
		mIvIcon = (CircleImageView) findViewById(R.id.free_help_bottom_iv_icon);
		mTvRightTime = (TextView) findViewById(R.id.free_help_bottom_tv_time);
		mTvNickName = (TextView) findViewById(R.id.free_help_bottom_item_tv_author);
		mLine = findViewById(R.id.free_help_bottom_item_line);
		mAskContainer = (LinearLayout) findViewById(R.id.free_help_bottom_content_container);
	}

	public void setData(MySeekDataBean bean) {
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		if (bean == null || loginInfo == null) {
			return;
		}
		String nickname = "";
		String portrait = "";
		if (bean.uid == loginInfo.getUid()) {
			nickname = loginInfo.getNickname();
			portrait = loginInfo.getPortrait();
		} else {
			nickname = bean.nickname;
			portrait = bean.portrait;
		}

		ImageLoader.getInstance().displayImage(portrait, mIvIcon, DisplayOptionsUtils.getOptionsConfig());
		mTvNickName.setText(nickname);
		mTvRightTime.setVisibility(View.VISIBLE);
		mTvRightTime.setText(VpDateUtils.getStandardDate(bean.create_time));
		mAskContainer.removeAllViews();
		if (!TextUtils.isEmpty(bean.cont)) {
			TextView textView = new TextView(getContext());
			textView.setTextColor(Color.parseColor("#000000"));
			textView.setText(bean.cont);
			// add by ping
			textView.setLineSpacing(DensityUtil.dip2px(getContext(), 2), 1);
			mAskContainer.addView(textView);
			final List<String> pics = bean.pics;// 图片列表
			if (pics != null && pics.size() > 0) {

				mAskContainer.post(new Runnable() {
					@Override
					public void run() {
						TopicPicContainer ivContainer = new TopicPicContainer(getContext());
						mAskContainer.addView(ivContainer);
						ivContainer.setDatas((ArrayList<String>) pics, mAskContainer.getWidth());
					}
				});
			}

		} else {
			// 是音频 或者带有图片
			List<MySeekAudioBean> audios = bean.audios;// 音频列表

			if (audios != null && audios.size() > 0) {
				// 说明有音频
				for (int i = 0; i < audios.size(); i++) {
					RecoderFrameLayout item = items = new RecoderFrameLayout(getContext());
					item.setData(audios.get(i));
					mAskContainer.addView(item);
				}
			}
		}
	}
	
	RecoderFrameLayout items;
	public RecoderFrameLayout getRecoderItem(){
		return items;
	}

	public void setIvIcon(String url) {
		ImageLoader.getInstance().displayImage(url, mIvIcon, DisplayOptionsUtils.getOptionsConfig());
	}

	public void setTvNickName(String name) {
		mTvNickName.setText(name);
	}

	public void setIvLineIsshow(boolean isshow) {
		mLine.setVisibility(isshow ? View.VISIBLE : View.GONE);
	}

	public void setTvRightTime(String time) {
		mTvRightTime.setText(com.vp.loveu.index.myutils.MyUtils.dateFromLong(time));
	}
}
