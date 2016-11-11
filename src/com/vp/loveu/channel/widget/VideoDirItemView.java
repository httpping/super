package com.vp.loveu.channel.widget;

import java.util.HashMap;

import com.vp.loveu.R;
import com.vp.loveu.base.VpApplication;
import com.vp.loveu.channel.bean.VideoDetailBean.Video;
import com.vp.loveu.channel.ui.VideoDetailActivity;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.my.activity.IntergralActivity;
import com.vp.loveu.my.bean.NewIntergralBean.NewIntergralDataBean;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.ShareCompleteUtils;
import com.vp.loveu.util.UIUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.custom.ShareDialogFragment;
import cn.sharesdk.onekeyshare.custom.ShareModel;
import cn.sharesdk.sina.weibo.SinaWeibo;

/**
 * @author：pzj
 * @date: 2015年12月1日 上午10:09:09
 * @Description:
 */
public class VideoDirItemView extends RelativeLayout implements OnClickListener, PlatformActionListener {
	private TextView mTvSeq;
	private TextView mTvTitle;
	private ShareView mBtnShare;
	private FrameLayout click;

	public VideoDirItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public VideoDirItemView(Context context) {
		super(context);
		initView(context);
	}

	private void initView(Context context) {
		View.inflate(context, R.layout.channel_video_detail_dir_item, this);
		mTvSeq = (TextView) findViewById(R.id.channel_video_dir_seq);
		mTvTitle = (TextView) findViewById(R.id.channel_video_dir_title);
		mBtnShare = (ShareView) findViewById(R.id.channel_btn_share);
		mBtnShare.setDrawable(R.drawable.icon_shares);
		click = (FrameLayout) findViewById(R.id.click);
		click.setOnClickListener(this);
	}

	public void setViewStatus(int index, String title, boolean isLearned) {
		mTvSeq.setText(index + "");
		mTvTitle.setText(title);
		if (isLearned) {
			mTvSeq.setTextColor(Color.parseColor("#D8D8D8"));
			mTvSeq.setBackgroundResource(R.drawable.channel_video_dir_circle_shape_read);
		} else {
			mTvSeq.setTextColor(Color.parseColor("#10BB7D"));
			mTvSeq.setBackgroundResource(R.drawable.channel_video_dir_circle_shape);
		}
	}

	public void setVideoData(Video video) {
		if (video == null) {
			return;
		}
		int share_exp = video.share_exp;//分享可获得积分
		mBtnShare.setText(share_exp);
		model = new ShareModel();
		model.setId(video.getId());// 分享信息ID, 若分享APP本身ID值为0
		model.setTitle(video.getName());
		model.setImageUrl(video.getPic());
		if (video.bean != null) {
			model.setTag(IntergralActivity.TAG);
			model.setObj(video.bean);
		}
		String url = VpConstants.VIDEO_SHARE + video.videoId + "&vid=" + video.getId();
		model.setUrl(url);
		model.setType(4);// 分享类型 999=分享APP本身 1=长文推荐
							// 2=PUA课堂3=大家都在聊4=新手视频5=恋爱电台[(v1.2.1)增加type值4和5]
	}

	private ShareModel model;
	private ShareDialogFragment dialog;

	@Override
	public void onClick(View v) {
		// 弹出分享
		if (dialog == null) {
			dialog = new ShareDialogFragment();
			dialog.setShowCopy(false);
			dialog.setPlatformActionListener(this);
		}
		VideoDetailActivity acitvity = (VideoDetailActivity) getContext();
		// 临时记录当前分享的内容信息
		VpApplication.getInstance().setShareModel(model);
		dialog.show(acitvity.getSupportFragmentManager(), "dialog", model);
	}

	@Override
	public void onCancel(Platform arg0, int arg1) {
		Toast.makeText(getContext(), "分享取消", 0).show();
	}

	@Override
	public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
		// 微信分享成功回调需要在WXEntryActivity 处理
		if (arg0.getName().equals(SinaWeibo.NAME)) {
			Toast.makeText(getContext(), "分享成功", Toast.LENGTH_SHORT).show();
			LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
			ShareModel tempModel = VpApplication.getInstance().getShareModel();
			if (tempModel != null && loginInfo != null) {
				if (IntergralActivity.TAG.equals(tempModel.getTag()) && tempModel.getObj() != null) {// ==0
					NewIntergralDataBean obj = (NewIntergralDataBean) tempModel.getObj(); // 新手任务
					// 发送广播通知
					Intent intent = new Intent(IntergralActivity.ACTION);
					intent.putExtra("obj", obj);
					UIUtils.getContext().sendBroadcast(intent);
				}
				ShareCompleteUtils utils = new ShareCompleteUtils(getContext());
				utils.reportData(loginInfo.getUid(), tempModel.getId(), tempModel.getType());
				VpApplication.getInstance().setShareModel(null);
			}
		}
	}

	@Override
	public void onError(Platform arg0, int arg1, Throwable arg2) {
		Toast.makeText(getContext(), "分享错误", 0).show();
	}
}
