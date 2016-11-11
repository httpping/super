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
import com.vp.loveu.widget.ScrollerNumberPicker.OnSelectListener;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月2日下午2:56:42
 * @功能 单列选择的dialog
 * @作者 mi
 */

public class SeclectThreeDialog extends Dialog implements android.view.View.OnClickListener {

	private TextView mTvTitle;
	private Button mBtCancle;
	private Button mBtOk;
	private ScrollerNumberPicker mPickerOne;
	private ScrollerNumberPicker mPickerTwo;
	private ScrollerNumberPicker mPickerThree;
	private String title;
	private ArrayList<String> oneData;
	private ArrayList<String> twoData;
	private ArrayList<String> threeData;
	private OnOneChickedListener listener;
	private int one;
	private int two;
	private int three;

	public SeclectThreeDialog(Context context, String title, ArrayList<String> oneData, ArrayList<String> twoData,
			ArrayList<String> threeData,int one,int two,int three, OnOneChickedListener listener) {
		super(context, R.style.dialog);
		this.title = title;
		this.oneData = oneData;
		this.twoData = twoData;
		this.threeData = threeData;
		this.listener = listener;
		this.one = one;
		this.two = two;
		this.three = three;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.my_index_info_three_picker_dialog);
		initView();
		initData();
	}

	private void initData() {
		if (!TextUtils.isEmpty(title)) {
			mTvTitle.setText(title);
		}
		mPickerOne.setData(oneData);
		mPickerOne.setDefault(one);
		mPickerTwo.setData(twoData);
		mPickerTwo.setDefault(two);
		final String s = "135781012";
		mPickerTwo.setOnSelectListener(new OnSelectListener() {
			@Override
			public void selecting(int id, String text) {
				
			}
			@Override
			public void endSelect(int id, String text) {
				if (!TextUtils.isEmpty(text)) {
					if ((id + 1) == 2) {
						//二月份
						mPickerThree.setData(getDayDatasss());
						mPickerThree.setDefault(three);
					}else if(s.contains((id + 1)+"")){
						mPickerThree.setData(getDayData());
						mPickerThree.setDefault(three);
					}else {
						mPickerThree.setData(getDayDatas());
						mPickerThree.setDefault(three);
					}
				}
			}
		});
		
		mPickerThree.setData(threeData);
		mPickerThree.setDefault(three);
	}
	
	private ArrayList<String> getDayData() {
		ArrayList<String> arrayList = new ArrayList<String>();
		for (int i = 1; i <= 31; i++) {
			if (i < 10) {
				arrayList.add("0" + i + "");
			} else {
				arrayList.add(i + "");
			}
		}
		return arrayList;
	}
	private ArrayList<String> getDayDatas() {
		ArrayList<String> arrayList = new ArrayList<String>();
		for (int i = 1; i <= 30; i++) {
			if (i < 10) {
				arrayList.add("0" + i + "");
			} else {
				arrayList.add(i + "");
			}
		}
		return arrayList;
	}
	private ArrayList<String> getDayDatasss() {
		ArrayList<String> arrayList = new ArrayList<String>();
		for (int i = 1; i <= 28; i++) {
			if (i < 10) {
				arrayList.add("0" + i + "");
			} else {
				arrayList.add(i + "");
			}
		}
		return arrayList;
	}

	private void initView() {
		mTvTitle = (TextView) findViewById(R.id.my_index_info_three_dialog_title);
		mBtCancle = (Button) findViewById(R.id.my_index_info_three_one_dialog_frist_btn);
		mBtOk = (Button) findViewById(R.id.my_index_info_three_one_dialog_two_btn);
		mPickerOne = (ScrollerNumberPicker) findViewById(R.id.my_index_info_three_one_dialog_picker);
		mPickerTwo = (ScrollerNumberPicker) findViewById(R.id.my_index_info_three_two_dialog_picker);
		mPickerThree = (ScrollerNumberPicker) findViewById(R.id.my_index_info_three_three_dialog_picker);
		mBtCancle.setOnClickListener(this);
		mBtOk.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.my_index_info_three_one_dialog_frist_btn:
			this.dismiss();
			break;
		case R.id.my_index_info_three_one_dialog_two_btn:
			listener.Onchicked(mPickerOne.getSelectedText()+"-"+mPickerTwo.getSelectedText()+"-"+mPickerThree.getSelectedText());
			listener.Onchicked(mPickerOne.getSelected(), mPickerOne.getSelectedText(), 
					mPickerTwo.getSelected(), mPickerTwo.getSelectedText(),
					mPickerThree.getSelected(), mPickerThree.getSelectedText());
			this.dismiss();
			break;

		default:
			break;
		}
	}

	/**
	 * 接口回调
	 * 
	 * @author Administrator
	 *
	 */
	public interface OnOneChickedListener {
		void Onchicked(int onePositionID, String onePositionContent, int twoPositionID, String twoContent, int threePosition,
				String ThreeContent);
		void Onchicked(String content);
	}
}
