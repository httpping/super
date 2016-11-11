package com.vp.loveu.my.dialog;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vp.loveu.R;
import com.vp.loveu.widget.ScrollerNumberPicker;

/**
 * @项目名称nameloveu1.0
 * @时间2015年11月2日下午2:56:42
 * @功能 单列选择的dialog
 * @作者 mi
 */

public class SeclectSexDialog extends Dialog implements android.view.View.OnClickListener {
	
	private TextView mTvTitle;
	private Button mBtCancle;
	private Button mBtOk;
	private ScrollerNumberPicker mPicker;
	private String title;
	private int position;
	private ArrayList<String> data;
	private OnOneChickedListener listener;

	public SeclectSexDialog(Context context,String title,int position,ArrayList<String> data,OnOneChickedListener listener) {
		super(context, R.style.dialog);
		this.title = title;
		this.data = data;
		this.position = position;
		this.listener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.my_index_info_one_picker_dialog);
		initView();
		initData();
	}

	private void initData() {
		if (!TextUtils.isEmpty(title)) {
			mTvTitle.setText(title);
		}
		mPicker.setData(data);
		mPicker.setDefault(position);
	}

	private void initView() {
		mTvTitle = (TextView) findViewById(R.id.my_index_info_one_dialog_title);
		mBtCancle = (Button) findViewById(R.id.my_index_info_one_dialog_frist_btn);
		mBtOk = (Button) findViewById(R.id.my_index_info_one_dialog_two_btn);
		mPicker = (ScrollerNumberPicker) findViewById(R.id.my_index_info_one_dialog_picker);
		mBtCancle.setOnClickListener(this);
		mBtOk.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.my_index_info_one_dialog_frist_btn:
			this.dismiss();
			break;
		case R.id.my_index_info_one_dialog_two_btn:
			listener.Onchicked(mPicker.getSelected(), mPicker.getSelectedText());
			this.dismiss();
			break;

		default:
			break;
		}
	}
	
	/**
	 * 接口回调
	 * @author Administrator
	 *
	 */
	public interface OnOneChickedListener{
		void Onchicked(int positionID,String positionContent);
	}
}
