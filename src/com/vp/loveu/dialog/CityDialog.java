package com.vp.loveu.dialog;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vp.loveu.R;
import com.vp.loveu.util.FileUtils;
import com.vp.loveu.widget.ScrollerNumberPicker;
import com.vp.loveu.widget.ScrollerNumberPicker.OnSelectListener;

public class CityDialog extends Dialog {

	private String city, area,key,typename;

	private TextView title;

	private EditText edit;

	private Button frist_btn, two_btn;

	private ScrollerNumberPicker frist_picker, two_picker;
	private Context context;

	private ClickListener clickListener;

	private TextView textview;

	private JSONArray jsonArray = new JSONArray();

	private ArrayList<String> listcity;

	private String value;
	
	private int itemcity=0;
	
	private int itemarea=0;
	
	public CityDialog(Context context,String typename,String key,String city,
			String area, ClickListener mClickListener, TextView textview) {
		super(context, R.style.dialog);
		this.context = context;
		this.city = city;
		this.area = area;
		this.typename = typename;
		this.key = key;
		this.clickListener = mClickListener;
		this.textview = textview;
		value=textview.getText().toString();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_city);
		initView();
		initValue();
	}

	private void initView() {
		title = (TextView) findViewById(R.id.title);
		frist_btn = (Button) findViewById(R.id.frist_btn);
		two_btn = (Button) findViewById(R.id.two_btn);
		frist_picker = (ScrollerNumberPicker) findViewById(R.id.frist_picker);
		two_picker = (ScrollerNumberPicker) findViewById(R.id.two_picker);

		frist_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				CityDialog.this.dismiss();
				CityDialog.this.cancel();
			}
		});
		two_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				clickListener.yesClick(textview,
						frist_picker.getSelectedText(),
						two_picker.getSelectedText(),key);
				CityDialog.this.dismiss();
				CityDialog.this.cancel();
			}
		});
	}

	private void initValue() {
		title.setText(typename);
		jsonArray = FileUtils.getArea(context.getAssets());
		listcity = new ArrayList<String>();
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				String cityname=jsonArray.getJSONObject(i).getString("province");
				if(!TextUtils.isEmpty(value)){
					if(value.contains(cityname)){
						itemcity=i;
					}
				}
				listcity.add(cityname);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		frist_picker.setData(listcity);
		frist_picker.setDefault(itemcity);
		setTwoPicker(itemcity);

		frist_picker.setOnSelectListener(new OnSelectListener() {

			@Override
			public void selecting(int id, String text) {

			}

			@Override
			public void endSelect(int id, String text) {
				if (!TextUtils.isEmpty(text)) {
					setTwoPicker(id);

				}
			}
		});

	}

	private void setTwoPicker(int id) {
		if (id != -1) {
			ArrayList<String> item = new ArrayList<String>();
			JSONObject json;
			try {
				itemarea=0;
				if(id<=3){
					json = jsonArray.getJSONObject(id);
					JSONArray array = json.getJSONArray("citys").getJSONObject(0).getJSONArray("areas");
					Log.i("tag_area", array.toString());
					for (int i = 0; i < array.length(); i++) {
						String area=array.getJSONObject(i).getString("area");
						if(!TextUtils.isEmpty(value)  && id==itemcity){
							if(value.contains(area)){
								itemarea=i;
							}
						}
						item.add(area);
					}
				}else{
				json = jsonArray.getJSONObject(id);
				JSONArray array = json.getJSONArray("citys");
				for (int i = 0; i < array.length(); i++) {
					String area=array.getJSONObject(i).getString("city");
					if(!TextUtils.isEmpty(value)  && id==itemcity){
						if(value.contains(area)){
							itemarea=i;
						}
					}
					item.add(area);
				}
				}
				two_picker.setData(item);
				two_picker.setDefault(itemarea);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public interface ClickListener {
		void yesClick(TextView textview, String city, String area,String key);

		void noClick();

	}

}
