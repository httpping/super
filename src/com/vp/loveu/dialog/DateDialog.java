package com.vp.loveu.dialog;

import java.util.ArrayList;
import java.util.Calendar;

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

public class DateDialog extends Dialog implements
		android.view.View.OnClickListener {

	private String typename;

	private TextView title;

	private Button frist_btn, two_btn;

	/** pick data for year  **/
	private ScrollerNumberPicker pick_year;
	
	/** pick data for month  **/
	private ScrollerNumberPicker pick_month;
	
	/** pick data for date  **/
	private ScrollerNumberPicker pick_date;
	
	private Context context;

	private ClickListener clickListener;

	private TextView textview;

	private String value;

	private int itemyear = 0;

	private int itemmonth = 0;

	private int itemdate = 0;

	private String year;

	private String month = "1";

	private String date = "1";

	private ArrayList<String> years = new ArrayList<String>();
	private ArrayList<String> months = new ArrayList<String>();
	private ArrayList<String> dates = new ArrayList<String>();

	private int nowyear;
	private int startyear, endyear;

	private String key;
	
	public DateDialog(Context context, String typename,String key,

	ClickListener mClickListener, TextView textview, int startyear, int endyear) {
		super(context, R.style.dialog);
		this.context = context;
		this.typename = typename;
		this.key = key;
		this.startyear = startyear;
		this.endyear = endyear;
		this.clickListener = mClickListener;
		this.textview = textview;
		value = textview.getText().toString();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_date);

		Calendar calendar = Calendar.getInstance();
		nowyear = calendar.get(Calendar.YEAR);
		year = String.valueOf(nowyear + startyear);
		

		if (!TextUtils.isEmpty(value)) {
			String[] cbdates = value.split("-");
			year = cbdates[0];
			month = cbdates[1];
			date = cbdates[2];

			itemmonth = Integer.valueOf(month) - 1;
			itemdate = Integer.valueOf(date) - 1;
		} else{
			if (startyear!=0) {
				year="1985";
				month="6";
				date="15";
				itemmonth = Integer.valueOf(month) - 1;
				itemdate = Integer.valueOf(date) - 1;
			}
		}

		// month = String.valueOf(d.getMonth() + 1);
		// date = String.valueOf(d.getDate());

		initView();
		initValue();
	}

	private void initView() {
		title = (TextView) findViewById(R.id.title);
		frist_btn = (Button) findViewById(R.id.frist_btn);
		two_btn = (Button) findViewById(R.id.two_btn);
		pick_year = (ScrollerNumberPicker) findViewById(R.id.pick_year);
		pick_month = (ScrollerNumberPicker) findViewById(R.id.pick_month);
		pick_date = (ScrollerNumberPicker) findViewById(R.id.pick_date);

	}

	private void initValue() {
		frist_btn.setOnClickListener(this);
		two_btn.setOnClickListener(this);
		title.setText(typename);

		setYearPick();
		setMonthPick();
		setDatePick();
		
		// 年份选择器
		pick_year.setOnSelectListener(new OnSelectListener() {

			@Override
			public void selecting(int id, String text) {

			}

			@Override
			public void endSelect(int id, String text) {
				if (!TextUtils.isEmpty(text)) {
					itemyear = id;
					year = text.replace("年", "");

//					itemmonth = 0;
//					month = "1";
//					date = "1";
//					itemdate = 0;
//					setMonthPick();
//					setDatePick();
				}
			}
		});
		
		// 月份选择器 
		pick_month.setOnSelectListener(new OnSelectListener() {

			@Override
			public void selecting(int id, String text) {

			}

			@Override
			public void endSelect(int id, String text) {
				if (!TextUtils.isEmpty(text)) {
					itemmonth = id;
					month = text.replace("月", "");

//					date = "1";
//					itemdate = 0;
//					setDatePick();
				}
			}
		});
		
		// 选择日期
		pick_date.setOnSelectListener(new OnSelectListener() {

			@Override
			public void selecting(int id, String text) {

			}

			@Override
			public void endSelect(int id, String text) {
				if (!TextUtils.isEmpty(text)) {
					itemdate = id;
					date = text.replace("日", "");
				}
			}
		});
	}

	private void setYearPick() {
		getYearsList();
		pick_year.setData(years);
		pick_year.setDefault(itemyear);

	}

	private void setMonthPick() {
		getMonthList();
		pick_month.setData(months);
		pick_month.setDefault(itemmonth);

	}

	private void setDatePick() {
		getDateList();
		pick_date.setData(dates);
		pick_date.setDefault(itemdate);
	}

	private void getYearsList() {

		years.clear();
		if (startyear > endyear) {
			for (int i = (nowyear + startyear); i > (nowyear + endyear); i--) {
				years.add(String.valueOf(i) + "年");
			}
		} else {
			for (int i = (nowyear + startyear); i < (nowyear + endyear); i++) {
				years.add(String.valueOf(i) + "年");
			}
		}
		for (int i = 0; i < years.size(); i++) {
			if (years.get(i).contains(year)) {
				itemyear = i;
			}
		}

	}

	private void getMonthList() {
		months.clear();
		for (int i = 1; i < 13; i++) {
			months.add(judInt(i) + "月");
		}
	}

	private void getDateList() {
		int chyear = Integer.parseInt(year);
		int chmonth = Integer.parseInt(month);
		int turns = 30;
		boolean flag = false;
		if (chyear % 4 == 0 || chyear % 400 == 0)
			flag = true;
		if (chmonth == 2) {
			if (flag == true) {
				turns = 29;
			} else {
				turns = 28;
			}
		} else if (chmonth == 1 || chmonth == 3 || chmonth == 5 || chmonth == 7
				|| chmonth == 8 || chmonth == 10 || chmonth == 12) {
			turns = 31;
		} else {
			turns = 30;
		}
		dates.clear();
		for (int i = 0; i < turns; i++) {
			dates.add(judInt(i + 1) + "日");
		}
	}

	public interface ClickListener {
		void yesClick(TextView textview, String year, String month, String date,String key);

		void noClick();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.frist_btn:
			DateDialog.this.dismiss();
			DateDialog.this.cancel();
			break;
		case R.id.two_btn:
			clickListener.yesClick(textview, year, month, date,key);
			DateDialog.this.dismiss();
			DateDialog.this.cancel();
			break;

		default:
			break;
		}

	}

	private String judInt(int i) {
		if (i < 10) {
			return "0" + String.valueOf(i);
		} else {
			return String.valueOf(i);
		}
	}

}
