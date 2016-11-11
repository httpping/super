package com.vp.loveu.index.widget;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vp.loveu.R;
import com.vp.loveu.index.activity.ArticleActivity;
import com.vp.loveu.index.bean.IndexBean;
import com.vp.loveu.index.bean.IndexBean.IndexArtBean;
import com.vp.loveu.index.myutils.DisplayOptionsUtils;
import com.vp.loveu.my.bean.ArticleBean.ArticleDataBean;
import com.vp.loveu.util.UIUtils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月16日下午4:57:34
 * @功能 视频item样式
 * @作者 mi
 */

public class IndexVideoRelativeLayout extends RelativeLayout implements OnClickListener {

	private ImageView mIvBackGround;
	private TextView mTvTitle;
	private ImageView mIvPlayer;
	private TextView mTvAuthor;
	private FrameLayout mIconContainer;
	private IndexArtBean mData;

	public IndexVideoRelativeLayout(Context context) {
		this(context, null);
	}
	
	public IndexVideoRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		View.inflate(context, R.layout.index_public_video_item, this);
		mIvBackGround = (ImageView) findViewById(R.id.index_video_iv_logo);
		mTvTitle = (TextView) findViewById(R.id.index_video_tv_title);
		mIvPlayer = (ImageView) findViewById(R.id.index_video_play_start);
		mTvAuthor = (TextView) findViewById(R.id.index_video_tv_author);
		mIconContainer = (FrameLayout) findViewById(R.id.index_video_flag);
		setOnClickListener(this);
	}

	public void setdata(final IndexArtBean data) {
		if (data == null) {
			return;
		}
		mData = data;
		mIvPlayer.setVisibility(data.has_video == 1 ? View.VISIBLE : View.GONE);
		mTvTitle.setText(data.title);
		mTvAuthor.setText(data.nickname);
		if (!TextUtils.isEmpty(data.pic)) {
			mIconContainer.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(data.pic, mIvBackGround, DisplayOptionsUtils.getOptionsConfig());
		}else{
			mIconContainer.setVisibility(View.GONE);
		}
	}

	public void setdata(final ArticleDataBean data) {
		if (data == null) {
			return;
		}
		IndexArtBean index = new IndexBean().new IndexArtBean();
		index.title = data.title;
		index.nickname = data.author_nickname;
		index.small_pic = data.pics != null && data.pics.size() > 0 ? data.pics.get(0) : "";
		index.has_video = data.has_video;
		index.portrait = data.author_portrait;
		index.id = data.target_id;
		setdata(index);
	}

	@Override
	public void onClick(View v) {
		startArticle(mData);
	}
	
	private void startArticle(IndexArtBean data) {
		Intent intent = new Intent(UIUtils.getContext(), ArticleActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(ArticleActivity.KEY_FLAG_SHARE_TITLE,data.title);
		intent.putExtra(ArticleActivity.KEY_FLAG_SHARE_ICONPATH, data.pic);
		intent.putExtra(ArticleActivity.KEY_FLAG, data.id);
		UIUtils.getContext().startActivity(intent);
	}
}
