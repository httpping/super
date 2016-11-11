package com.vp.loveu.index.adapter;

import java.util.ArrayList;
import java.util.List;

import com.vp.loveu.R;
import com.vp.loveu.channel.widget.TopicPicContainer;
import com.vp.loveu.comm.ShowImagesViewPagerActivity;
import com.vp.loveu.index.bean.MySeekHelpBean.MySeekAudioBean;
import com.vp.loveu.index.bean.MySeekHelpBean.MySeekDataBean;
import com.vp.loveu.index.holder.BaseHolder;
import com.vp.loveu.index.widget.FreeHelpBottomReplyRelativiLayout;
import com.vp.loveu.util.VpDateUtils;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2016年1月4日下午6:18:59
 * @功能TODO
 * @作者 mi
 */

public class MyFellHelpHolder extends BaseHolder<MySeekDataBean> {

	public LinearLayout mMyIcon;// 我的头像
	public LinearLayout mAskPicture;// 我的提问的图片 如果有显示 没有隐藏
	public LinearLayout mAswerContainer;// 回复的容器
	public Button mPacket;// 红包的标识
	public TextView mTvCreateTime;// 对应的是提问创建的时间
	public TextView mTvContent;// 提问的内容
	public TextView mTvFlag;
	public View mLine;
	private int width = 103;
	private int height = 63;

	public MyFellHelpHolder(Context context) {
		super(context);
	}

	@Override
	protected View initView() {
		return View.inflate(mContext, R.layout.my_fell_help_listview_item, null);
	}

	@Override
	protected void findView() {
		mMyIcon = (LinearLayout) mRootView.findViewById(R.id.public_fell_help_logoname_container);
		mMyIcon.setVisibility(View.GONE);// 不需要显示个人头像
		mPacket = (Button) mRootView.findViewById(R.id.public_fell_help_bt_packet_flag);// 红包
		mAskPicture = (LinearLayout) mRootView.findViewById(R.id.public_fell_help_ly_iv_container);// 提问的图片容器
		mAswerContainer = (LinearLayout) mRootView.findViewById(R.id.publi_aswer_ly_container);// 回复的容器
		mTvCreateTime = (TextView) mRootView.findViewById(R.id.public_fell_help_tv_nickname);// 创建的时间
		mTvContent = (TextView) mRootView.findViewById(R.id.public_fell_help_tv_content);// 内容
		mTvFlag = (TextView) mRootView.findViewById(R.id.publi_fell_flag);
		mLine = mRootView.findViewById(R.id.publi_aswer_line);
		mTvContent.setMaxLines(Integer.MAX_VALUE);
	}

	private void setView(int position, MySeekDataBean bean) {
		int id = bean.id;// 问题的id
		String content = bean.cont;// 问题的内容
		String create_time = bean.create_time;// 求助创建的时间
		List<MySeekAudioBean> audios = bean.audios;// 求助的语音 暂时未做
		final List<String> pics = bean.pics;// 求助的图片
		int type = bean.type;// 求助的类型
		int status = bean.status;// 求助的状态 1等待回复 2已经回复3被拒绝
		List<MySeekDataBean> replys = bean.replys;// 回复的
		double price = bean.price;// 支付的费用
		if (position == mDatas.size() - 1) {
			mLine.setVisibility(View.GONE);
		} else {
			mLine.setVisibility(View.VISIBLE);
		}

		mTvContent.setText(content);
		mTvCreateTime.setText(VpDateUtils.getStandardDate(create_time));
		mAskPicture.setVisibility(View.GONE);
		if (pics != null && pics.size() > 0) {
			mAskPicture.removeAllViews();
			mAskPicture.setVisibility(View.VISIBLE);
			/*
			 * for (int i = 0; i < pics.size(); i++) { ImageView iv = new
			 * ImageView(mContext); iv.setScaleType(ScaleType.CENTER_CROP);
			 * LayoutParams params = new
			 * LinearLayout.LayoutParams(UIUtils.dp2px(width),
			 * UIUtils.dp2px(height)); if (i != 0) { params.leftMargin =
			 * UIUtils.dp2px(10); }
			 * ImageLoader.getInstance().displayImage(pics.get(i), iv,
			 * DisplayOptionsUtils.getOptionsConfig()); mAskPicture.addView(iv,
			 * params); setImageViewOnclick(iv, pics, i); }
			 */

			mAskPicture.post(new Runnable() {
				@Override
				public void run() {
					TopicPicContainer ivContainer = new TopicPicContainer(mContext);
					mAskPicture.addView(ivContainer);
					ivContainer.setDatas((ArrayList<String>) pics, mAskPicture.getWidth());
				}
			});

		}

		if (type == 1) {
			// 普通用户
			mPacket.setVisibility(View.GONE);
		} else if (type == 2 || type == 3) {
			// 情感达人
			mPacket.setVisibility(View.VISIBLE);
			mPacket.setText(bean.price + "元红包");// 设置金额
		}

		mAswerContainer.removeAllViews();
		mTvFlag.setText("解答回复");
		if (status == 1) {
			// 等待回复
			mTvFlag.setText("等待回复...");
		} else if (status == 2) {
			// 已经回复
			if (replys != null && replys.size() > 0) {
				for (int i = 0; i < replys.size(); i++) {
					MySeekDataBean seekBean = replys.get(i);
					FreeHelpBottomReplyRelativiLayout item = items = new FreeHelpBottomReplyRelativiLayout(mContext);
					item.setIvLineIsshow(false);
					item.setData(seekBean);
					mAswerContainer.addView(item);
				}
			}
		} else if (status == 3) {
			// 被拒绝
			mTvFlag.setText("被拒绝...");
		}
	}
	
	FreeHelpBottomReplyRelativiLayout items;
	public FreeHelpBottomReplyRelativiLayout getHolderItem(){
		return items;
	}

	public void setImageViewOnclick(ImageView iv, final List<String> pics, final int i) {
		iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ShowImagesViewPagerActivity.class);
				intent.putStringArrayListExtra(ShowImagesViewPagerActivity.IMAGES, (ArrayList<String>) pics);
				intent.putExtra(ShowImagesViewPagerActivity.POSITION, i);
				mContext.startActivity(intent);
			}
		});
	}

	@Override
	protected void initData(MySeekDataBean t) {
		setView(mPosition, t);
	}
}
