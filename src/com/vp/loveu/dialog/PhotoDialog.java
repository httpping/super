package com.vp.loveu.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.vp.loveu.R;

/**
 * 
 * @ClassName: UpdateSinglePal
 * @Description: 选择相册
 * @author lisy
 * @date 2014年6月10日 下午3:11:25
 * 
 */
public class PhotoDialog extends Dialog implements android.view.View.OnClickListener{

	private Button btn_one, btn_two,btn_three;

	private Context context;

	private ClickListener clickListener;
	
	
	public PhotoDialog(Context context ,
			 ClickListener mClickListener ) {
		super(context,R.style.dialog);
		this.context = context;
		this.clickListener = mClickListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_photo);
		initView();
		initValue();
	}

	private void initView() {
		btn_one=(Button) findViewById(R.id.btn_one);
		btn_two=(Button) findViewById(R.id.btn_two);
		btn_three=(Button) findViewById(R.id.btn_three);
	}

	private void initValue() {
		btn_one.setOnClickListener(this);
		btn_two.setOnClickListener(this);
//		btn_three.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_one:
			clickListener.yesClick(2);
			break;
		case R.id.btn_two:
			clickListener.yesClick(1);
			break;
		case R.id.btn_three:
			clickListener.yesClick(3);
			break;
		default:
			break;
		}
		
		PhotoDialog.this.dismiss();
		PhotoDialog.this.cancel();
	}

	public interface  ClickListener {
		void yesClick(int item);
		void noClick();
	}
}
