package com.vp.loveu.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vp.loveu.R;

/**
 * 
 * @ClassName: UpdateSinglePal
 * @Description: 修改信息
 * @author lisy
 * @date 2014年6月10日 下午3:11:25
 * 
 */
public class EditPalDialog extends Dialog {

	private String typename, key,value;

	private TextView title;

	private EditText edit;

	private Button frist_btn, two_btn;

	private Context context;

	private ClickListener clickListener;
	
	private TextView textview;
	
	private InputMethodManager imm;
	
	private int length=20;
	
	public EditPalDialog(Context context, String typename, String key,
			 ClickListener mClickListener,TextView textview,int length) {
		super(context,R.style.dialog);
		this.context = context;
		this.typename = typename;
		this.key = key;
		this.value = textview.getText().toString();
		this.clickListener = mClickListener;
		this.textview=textview;
		if (length!=0) {
			this.length=length;
		}
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_edit_pal);
		imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		
		initView();
		initValue();
	}

	private void initView() {
		title = (TextView) findViewById(R.id.title);
		edit = (EditText) findViewById(R.id.edit);
		frist_btn = (Button) findViewById(R.id.frist_btn);
		two_btn = (Button) findViewById(R.id.two_btn);
		frist_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditPalDialog.this.dismiss();
				EditPalDialog.this.cancel();
			}
		});
		two_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				clickListener.yesClick(textview, edit.getText().toString(),key);
				EditPalDialog.this.dismiss();
				EditPalDialog.this.cancel();
			}
		});
	}

	private void initValue() {
		title.setText(typename);
		edit.setText(value);
		edit.setSelection(value.length());
		InputFilter[] filters = {new InputFilter.LengthFilter(length)};  
		edit.setFilters(filters);  
		
		edit.setFocusable(true);   
		edit.setFocusableInTouchMode(true);   
		edit.requestFocus();  
		
		imm.showSoftInput(edit, 0);
	}

	
	@Override
	public void dismiss() {
		imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
		super.dismiss();
	
	}
	public interface  ClickListener {
		void yesClick(TextView textview,String org1,String key);
		void noClick();
		
	}

}
