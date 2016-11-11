package com.vp.loveu.index.bean;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.vp.loveu.R;
import com.vp.loveu.comm.ShowImagesViewPagerActivity;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.pay.bean.PayBindViewBean;
import com.vp.loveu.util.UIUtils;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月23日下午2:47:03
 * @功能 情感求助支付页面的bean
 * @作者 mi
 */

public class FellHelpVipPlayBean extends PayBindViewBean {

	public String content;//内容
	public ArrayList<String> mSelectPath;//展示的相册
	public String json;
	private int width = 103;
	private int height = 63;
	private int margin = 10;

	@Override
	public View createShowView(Context context) {
		View mView = View.inflate(context, R.layout.publi_fell_help_my_help, null);
		mView.findViewById(R.id.public_fell_help_logoname_container).setVisibility(View.GONE);
		initData(mView,context);
		return mView;
	}

	private void initData(View mView,Context context) {
		TextView mTvContent = (TextView) mView.findViewById(R.id.public_fell_help_tv_content);
		mTvContent.setText(content);
		Button mBtFlag = (Button) mView.findViewById(R.id.public_fell_help_bt_packet_flag);
		mBtFlag.setVisibility(View.VISIBLE);
		String str = Double.toString(payMoney);
		mBtFlag.setText(str+ "红包");
		if (mSelectPath == null || mSelectPath.size() <= 0) {
			mView.findViewById(R.id.public_fell_help_ly_iv_container).setVisibility(View.GONE);
		}else{
			LinearLayout layout = (LinearLayout) mView.findViewById(R.id.public_fell_help_ly_iv_container);
			for (int i = 0; i < mSelectPath.size(); i++) {
				String path = mSelectPath.get(i);
				ImageView iv = new ImageView(context);
				iv.setScaleType(ScaleType.CENTER_CROP);
				LayoutParams params = new LinearLayout.LayoutParams(UIUtils.dp2px(width), UIUtils.dp2px(height));
				if (i !=0) {
					params.leftMargin = margin;
				}
				iv.setImageBitmap(MyUtils.compressBitmap(path));
				final int position = i;
				iv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						selectPhoto(position);
					}
				});
				layout.addView(iv,params);
			}
		}
	}

	protected void selectPhoto(int position) {
		Intent intent = new Intent(UIUtils.getContext(), ShowImagesViewPagerActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(ShowImagesViewPagerActivity.IMAGES, mSelectPath);
		intent.putExtra(ShowImagesViewPagerActivity.POSITION,position);
		UIUtils.getContext().startActivity(intent);
	}

	@Override
	public JSONObject getParams() {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
}
