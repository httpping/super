package com.vp.loveu.my.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vp.loveu.R;

import cz.msebera.android.httpclient.util.TextUtils;

/**
 * @项目名称nameloveu1.0
 * @时间2015年11月2日下午2:26:30
 * @功能TODO
 * @作者Administrator
 */

public class EditNameDialog extends Dialog implements android.view.View.OnClickListener {
	
	private TextView mTvTitle;
	private EditText mEditContent;
	private Button mBtCancle;
	private Button mBtOk;
	private OnChickedDialogListener mListener;
	private String title;
	private String normalValue;

	public EditNameDialog(Context context,String title,String normalValue,OnChickedDialogListener listener) {
		super(context,R.style.dialog);
		mListener = listener;
		this.title = title;
		this.normalValue = normalValue;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.dialog_edit_pal);
		initView();
		initData();
	}

	private void initData() {
		if (!TextUtils.isEmpty(title)) {
			mTvTitle.setText(title);
		}
		if (!TextUtils.isEmpty(normalValue)) {
			mEditContent.setText(normalValue);
			mEditContent.setSelection(normalValue.length());
		}
	}

	private void initView() {
		mTvTitle = (TextView) findViewById(R.id.title);
		mEditContent = (EditText) findViewById(R.id.edit);
		mBtCancle = (Button) findViewById(R.id.frist_btn);
		mBtOk = (Button) findViewById(R.id.two_btn);
		mBtCancle.setOnClickListener(this);
		mBtOk.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.frist_btn:
			this.dismiss();
			break;
		case R.id.two_btn:
			if (TextUtils.isEmpty(mEditContent.getText().toString().trim())) {
				Toast.makeText(getContext(), "姓名不能为空", Toast.LENGTH_SHORT).show();
				break;
			}
			mListener.onChicked(mEditContent.getText().toString().trim());
			this.dismiss();
			break;

		default:
			break;
		}
	}
	
	public interface OnChickedDialogListener{
		public abstract void onChicked(String content);
	}
}
