package com.vp.loveu.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vp.loveu.R;

/**
 * 
 * @ClassName: UpdateSinglePal
 * @Description: 提醒
 * @author lisy
 * @date 2014年6月10日 下午3:11:25
 * 
 */
public class QaPeopleRemDialog extends Dialog implements android.view.View.OnClickListener{

	private Button btn_one, btn_two;
	
	private TextView tv_remind;

	private Context context;

	private ClickListener clickListener;
	
	private String remind,btnText1,btnText2;
	
	
	public QaPeopleRemDialog(Context context ,
			 ClickListener mClickListener ) {
		super(context,R.style.dialog);
		this.context = context;
		this.clickListener = mClickListener;
	}
	
	public QaPeopleRemDialog(Context context,String remind,
			String btnText1, String btnText2, ClickListener mClickListener){
		super(context,R.style.dialog);
		this.context = context;
		this.clickListener = mClickListener;
		this.remind = remind;
		this.btnText1 = btnText1;
		this.btnText2 = btnText2;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_ab_rem);
		initView();
		initValue();
	}

	private void initView() {
		btn_one=(Button) findViewById(R.id.btn_one);
		btn_two=(Button) findViewById(R.id.btn_two);
		tv_remind = (TextView) findViewById(R.id.tv_remind);
	}

	private void initValue() {
		btn_one.setOnClickListener(this);
		btn_two.setOnClickListener(this);
		if(null != remind ){
			tv_remind.setText(remind);
			btn_one.setText(btnText1);
			if(TextUtils.isEmpty(btnText2)){
				btn_two.setVisibility(View.GONE);
			}else{
				btn_two.setText(btnText2);
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_one:
			clickListener.yesClick(1);
			break;
		case R.id.btn_two:
			clickListener.yesClick(2);
			break;
		default:
			break;
		}
		QaPeopleRemDialog.this.dismiss();
		QaPeopleRemDialog.this.cancel();
	}

	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		clickListener.yesClick(2);
	}
	public interface  ClickListener {
		void yesClick(int item);
		void noClick();
		
	}

	

}
