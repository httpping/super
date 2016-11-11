package com.vp.loveu.message.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.vp.loveu.R;
import com.vp.loveu.message.bean.ChatEmoji;
import com.vp.loveu.message.utils.DensityUtil;
import com.vp.loveu.message.view.EmojiPopupWindow.OnEmojiItemClick;
import com.vp.loveu.util.ScreenBean;
import com.vp.loveu.util.ScreenUtils;
import com.vp.loveu.util.VPLog;

/**
 * 

 *
 * @ClassName:
 * @Description: 表情填充器
 * @author ping
 * @date
 */
public class FaceAdapter extends BaseAdapter {

	
	
	private List<ChatEmoji> data;
	private LayoutInflater inflater;
	private Context mContext;
	
	OnEmojiItemClick mEmojiItemClick;
	private int size = 0;
	
	OnClickListener mClickListener;

	public FaceAdapter(Context context, List<ChatEmoji> list,OnEmojiItemClick itemClick) {
		this.inflater = LayoutInflater.from(context);
		this.data = list;
		this.size = list.size();
		mContext = context;
		ScreenUtils.initScreen((Activity) context);
		mEmojiItemClick = itemClick;
		
		 
	}

	@Override
	public int getCount() {
		return this.size;
	}


	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		  ChatEmoji emoji = data.get(position);
		ViewHolder viewHolder = null;
		viewHolder = new ViewHolder();
		VPLog.d("tag", "face adapter");
		//外壳
		RelativeLayout pview = new RelativeLayout(mContext);
		LayoutParams pparams = new LayoutParams(1, 1);
		pparams.width = ScreenBean.screenWidth/7;
		pparams.height = DensityUtil.dip2px(mContext, 58);
		pview.setLayoutParams(pparams);
		
		//内容
		ImageView img  = new ImageView(mContext);
		pview.addView(img);
		android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) img.getLayoutParams();
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		params.width =  ScreenBean.screenWidth/9;
		params.height = params.width;
		img.setBackgroundResource(R.drawable.message_face_item_bg);
		convertView =pview;
		viewHolder.iv_face = (ImageView) img;
		img.setPadding(params.width/8, params.width/8, params.width/8, params.width/8);
		 
		if (emoji.getId() == R.drawable.face_del_icon) {
			viewHolder.iv_face.setImageResource(emoji.getId());
		} else if (TextUtils.isEmpty(emoji.getCharacter())) {
			viewHolder.iv_face.setImageDrawable(null);
		} else {
			viewHolder.iv_face.setTag(emoji);
			viewHolder.iv_face.setImageResource(emoji.getId());
		}
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mEmojiItemClick!=null) {
					mEmojiItemClick.onEmojiClick(data.get(position));
				}
			}
		});

		return convertView;
	}

	class ViewHolder {
		public ImageView iv_face;
	}

	@Override
	public Object getItem(int position) {
		return data==null?null : data.get(position);
	}
}