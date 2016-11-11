package com.vp.loveu.dialog;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vp.loveu.R;
import com.vp.loveu.bean.AreaBean;
import com.vp.loveu.util.FileUtils;
import com.vp.loveu.widget.ScrollerNumberPicker;
import com.vp.loveu.widget.ScrollerNumberPicker.OnSelectListener;

public class LocationPickerDialog extends Dialog {

	private String typename;
	
	/** 标题 **/
	private TextView title;
	
	/** 取消按钮**/
	private Button frist_btn;
	
	/** 确定按钮**/
	private Button two_btn;
	
	/** picker for province or state  **/
	private ScrollerNumberPicker pick_province;
	
	/** picker for city **/
	private ScrollerNumberPicker pick_city;
	
	/** picker for district  **/
	private ScrollerNumberPicker pick_district;
	
	private ClickListener clickListener;

	Context context;
	private String key;
	String value;
	private TextView textview;
	
	private JSONArray jsonArray = new JSONArray();
	
	
	List<AreaBean> areas;
	ArrayList<String> listProvince = new ArrayList<String>();
	ArrayList<String> listCity = new ArrayList<String>();
	ArrayList<String> listDistrict = new ArrayList<String>();

	/** current province postion **/
	private int itemProvince = 0;
	
	/** current city postion **/
	private int itemCity = 0;
	
	/** current district postion **/
	private int itemDistrict = 0;
	
	
	public LocationPickerDialog (Context context, String typename,String key,
			ClickListener mClickListener, TextView textview ) {
		super(context, R.style.dialog);
		this.context = context;
		this.typename = typename;
		this.key = key;
		this.clickListener = mClickListener;
		this.textview = textview;
		value = textview.getText().toString();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_picker_loctation);
		
		initView();
		initValue();
		
	}
	
	
	private void initValue() {
		title.setText(typename);
		
		
		areas = AreaBean.praseAreaCity(FileUtils.getLocationArae(context.getAssets()),null,0);
		
		/*jsonArray = FileUtils.getLocation(context.getAssets());
		for(int i = 0; i < jsonArray.length(); i++) {
			try {
				JSONObject obj=jsonArray.getJSONObject(i);
				String provinceName = obj.getString("Name");
//				String provinceName = jsonArray.getJSONObject(i).getString("Name");
				if(!TextUtils.isEmpty(value)){
					if(value.contains(provinceName)){
						itemProvince = i;
					}
				}
				listProvince.add(provinceName);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}*/
		
		for (int i = 0; i < areas.size(); i++) {
			String provinceName = areas.get(i).name;
			if(!TextUtils.isEmpty(value)){
				if(value.contains(provinceName)){
					itemProvince = i;
				}
			}
			listProvince.add(provinceName);			
		}
		
		pick_province.setData(listProvince);
		pick_province.setDefault(itemProvince);
		pick_province.setOnSelectListener(new OnSelectListener() {

			@Override
			public void endSelect(int id, String text) {
				if (!TextUtils.isEmpty(text)) {
					itemProvince = id;
					itemCity = 0;
					itemDistrict = 0;
					setCityPick();
					setDistrictPick();
				}
			}

			@Override
			public void selecting(int id, String text) {
				
			}
		});
		
		setCityPick();
	}

	protected void setCityPick() {
		
		/*try {
			JSONObject jsonObj = jsonArray.getJSONObject(itemProvince);
			JSONArray cityJsonArray = jsonObj.getJSONArray("Children");
			if(listCity.size() > 0) {
				listCity.clear();
			}
			for(int i = 0; i < cityJsonArray.length(); i++) {
				String cityName = cityJsonArray.getJSONObject(i).getString("Name");
				if(!TextUtils.isEmpty(value)){
					if(value.contains(cityName)){
						itemCity = i;
					}
				}
				listCity.add(cityName);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}*/
		
		if(listCity.size() > 0) {
			listCity.clear();
		}
		List<AreaBean> citys =  areas.get(itemProvince).children;
		for(int i = 0; i < citys.size(); i++) {
			String cityName = citys.get(i).name;
			if(!TextUtils.isEmpty(value)){
				if(value.contains(cityName)){
					itemCity = i;
				}
			}
			listCity.add(cityName);
		}
		
		
		Log.i("city", "city:" +listCity);
		
		pick_city.setData(listCity);
		pick_city.setDefault(itemCity);
		pick_city.setOnSelectListener(new OnSelectListener() {

			@Override
			public void endSelect(int id, String text) {
				if (!TextUtils.isEmpty(text)) {
					itemCity = id;
					itemDistrict = 0;
					setDistrictPick();
				}
			}

			@Override
			public void selecting(int id, String text) {
				
			}
		});
		
		setDistrictPick();
		
	}

	
	private void setDistrictPick() {
		/*try {
			JSONObject jsonObj = jsonArray.getJSONObject(itemProvince);
			JSONArray cityJsonArray = jsonObj.getJSONArray("Children");
			JSONObject json = cityJsonArray.getJSONObject(itemCity);
			JSONArray districtArray = json.getJSONArray("Children");
			if(listDistrict != null && listDistrict.size() > 0) {
				listDistrict.clear();
			}
			if(districtArray.length()==0)
				districtArray=cityJsonArray;
			for(int i = 0; i < districtArray.length(); i++) {
				String districtName = districtArray.getJSONObject(i).getString("Name");
				if(!TextUtils.isEmpty(value)){
					if(value.contains(districtName)){
						itemDistrict = i;
					}
				}
				listDistrict.add(districtName);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}*/
		
		Log.d("dis", "disccc ");
		if(listDistrict != null && listDistrict.size() > 0) {
			listDistrict.clear();
		}
		List<AreaBean> districts =  areas.get(itemProvince).children.get(itemCity).children;
		if (districts!=null) {
			for(int i = 0; i < districts.size(); i++) {
				String districtName = districts.get(i).name;
				if(!TextUtils.isEmpty(value)){
					if(value.contains(districtName)){
						itemDistrict = i;
					}
				}
				listDistrict.add(districtName);
			}
		}
		
		if (listDistrict.size()<=0) {
			listDistrict.add("");
			//pick_district.setVisibility(View.GONE);
		}
		
		pick_district.setData(listDistrict);
		pick_district.setDefault(itemDistrict);
		pick_district.setOnSelectListener(new OnSelectListener() {
			
			@Override
			public void selecting(int id, String text) {
				
			}
			
			@Override
			public void endSelect(int id, String text) {
				itemDistrict = id;
				
			}
		});
		
	}

	/** 初始化用户界面控件 **/
	private void initView() {
		title = (TextView) findViewById(R.id.title);
		frist_btn = (Button) findViewById(R.id.frist_btn);
		two_btn = (Button) findViewById(R.id.two_btn);
		
		pick_province = (ScrollerNumberPicker) findViewById(R.id.pick_province);
		pick_city = (ScrollerNumberPicker) findViewById(R.id.pick_city);
		pick_district = (ScrollerNumberPicker) findViewById(R.id.pick_district);
		
		frist_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				LocationPickerDialog.this.dismiss();
				LocationPickerDialog.this.cancel();
			}
		});
		two_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				AreaBean dirtict = null;
				if(!TextUtils.isEmpty(pick_district.getSelectedText())){
					dirtict = areas.get(itemProvince).children.get(itemCity).children.get(itemDistrict);
				}
				
				clickListener.yesClick(textview,
						areas.get(itemProvince),
						areas.get(itemProvince).children.get(itemCity),dirtict,key);
				LocationPickerDialog.this.dismiss();
				LocationPickerDialog.this.cancel();
			}
		});
	}


	public interface ClickListener {
		void yesClick(TextView textview,AreaBean province, AreaBean city, AreaBean district,String key);
		void noClick();

	}
}
