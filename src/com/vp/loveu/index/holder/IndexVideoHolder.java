package com.vp.loveu.index.holder;

import java.util.List;

import com.vp.loveu.R;
import com.vp.loveu.index.activity.ArticleActivity;
import com.vp.loveu.index.activity.MoreContentActivity;
import com.vp.loveu.index.bean.IndexBean.IndexArtBean;
import com.vp.loveu.index.widget.IndexVideoRelativeLayout;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月17日上午11:19:17
 * @功能 热点文章模块的holder
 * @作者 mi
 */

public class IndexVideoHolder extends BaseHolder<List<IndexArtBean>> implements OnClickListener {

	private ImageView mVideoPlayer;
	private TextView mVideoTitle;
	private LinearLayout mVideoContainer;
	private TextView mVideoTvMore;
	private ImageView mIvPlayer;
	private FrameLayout mBackGround;
	private String tag = "IndexVideoHolder";
	private IndexArtBean bigData;

	public IndexVideoHolder(Context context) {
		super(context);
	}
	
	@Override
	protected View initView() {
		return  View.inflate(mContext, R.layout.index_public_video_show, null);
	}
	@Override
	protected void findView() {
		mVideoPlayer = (ImageView) mRootView.findViewById(R.id.index_video_player);
		mVideoTitle = (TextView) mRootView.findViewById(R.id.index_video_tv_title);
		mVideoContainer = (LinearLayout) mRootView.findViewById(R.id.index_video_hot_container);
		mVideoTvMore = (TextView) mRootView.findViewById(R.id.index_video_more);
		mIvPlayer = (ImageView) mRootView.findViewById(R.id.index_public_video_iv_background);
		mBackGround = (FrameLayout) mRootView.findViewById(R.id.index_public_video);
		mVideoTvMore.setOnClickListener(this);
		mBackGround.setOnClickListener(this);
	}
	

	@Override
	protected void initData(List<IndexArtBean> data) {
		mBackGround.setVisibility(View.GONE);
		
		for (int i = 0; i < data.size(); i++) {
			IndexArtBean temp = data.get(i);
			IndexVideoRelativeLayout item = new IndexVideoRelativeLayout(mContext);
			item.setdata(temp);
			mVideoContainer.addView(item);
		}
		
		/*bigData = data.get(0);//大图
		if (!TextUtils.isEmpty(bigData.pic)) {
			//有大图
			ImageLoader.getInstance().displayImage(bigData.pic, mIvPlayer, DisplayOptionsUtils.getOptionsConfig());
			mVideoPlayer.setVisibility(bigData.has_video == 1 ? View.VISIBLE : View.GONE);
			mVideoTitle.setText(bigData.title);
			mBackGround.setVisibility(View.VISIBLE);
			for (int i = 1; i < data.size(); i++) {
				IndexArtBean temp = data.get(i);
				IndexVideoRelativeLayout item = new IndexVideoRelativeLayout(mContext);
				item.setdata(temp);
				mVideoContainer.addView(item);
			}
		}else{
			//没有大图
			mBackGround.setVisibility(View.GONE);
			for (int i = 0; i < data.size(); i++) {
				IndexArtBean temp = data.get(i);
				IndexVideoRelativeLayout item = new IndexVideoRelativeLayout(mContext);
				item.setdata(temp);
				mVideoContainer.addView(item);
			}
		}*/

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.index_video_more:
			Intent intent = new Intent(mContext, MoreContentActivity.class);
			intent.putExtra(IndexNavigationHolder.KEY_FLAG, 1);
			mContext.startActivity(intent);
			break;
		case R.id.index_public_video:
			//跳转到详情页面
			startArticle(bigData);
			break;
		default:
			break;
		}
	}

	private void startArticle(IndexArtBean data) {
		Intent intent = new Intent(mContext,ArticleActivity.class);
		intent.putExtra(ArticleActivity.KEY_FLAG_SHARE_TITLE,data.title);
		intent.putExtra(ArticleActivity.KEY_FLAG_SHARE_ICONPATH, data.pic);
		intent.putExtra(ArticleActivity.KEY_FLAG, data.id);
		mContext.startActivity(intent);
	}
}
