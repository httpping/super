package com.vp.loveu.my.widget;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;
import com.vp.loveu.R;
import com.vp.loveu.bean.AreaBean;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.my.activity.HeartShowActivity;
import com.vp.loveu.my.activity.LoveInfoActivity;
import com.vp.loveu.my.activity.MyInfoActivity;
import com.vp.loveu.my.bean.NewIntergralBean.NewIntergralDataBean;
import com.vp.loveu.my.bean.WalletBean.WalletDataBean;
import com.vp.loveu.my.dialog.LocationPickerDialogs;
import com.vp.loveu.my.dialog.LocationPickerDialogs.ClickListener;
import com.vp.loveu.my.dialog.SeclectSexDialog;
import com.vp.loveu.my.dialog.SeclectThreeDialog;
import com.vp.loveu.my.dialog.SeclectThreeDialog.OnOneChickedListener;
import com.vp.loveu.util.LoginStatus;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年12月1日上午10:56:03
 * @功能 我的钱包底部部分的item
 * @作者 mi
 */

public class WalletBottomItemRelativeLayout extends RelativeLayout implements OnDismissListener, OnClickListener {

	private TextView mTvTitle;
	private TextView mTvTime;
	private TextView mTvIntergral;
	private ImageView mIvMore;
	private View mLine;
	private boolean isEnable = true;
	private RelativeLayout mBackGround;
	private Context mContext;
	private String[] mDatas;
	public static final JsonObject obj = new JsonObject();
	public static final JSONObject infoObj = new JSONObject();
	private SeclectSexDialog selectDialog;
	private SeclectThreeDialog seclectThreeDialog;
	private LocationPickerDialogs locationPickerDialog;
	private LocationPickerDialogs locationThreePickerDialog;
	private MyInfoActivity mActivity;
	private LoveInfoActivity mLoveInfoActivity;
	private ArrayList<String> list;
	private int position;
	private int oneBirthday;
	private int twoBirthday;
	private int threeBirthday;

	public WalletBottomItemRelativeLayout(Context context) {
		this(context, null);
	}

	public WalletBottomItemRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		if (context instanceof MyInfoActivity) {
			mActivity = (MyInfoActivity) context;
		}
		if (context instanceof LoveInfoActivity) {
			mLoveInfoActivity = (LoveInfoActivity) context;
		}
		View.inflate(context, R.layout.my_index_info_activity_item, this);
		initView();
	}

	private void initView() {
		mDatas = mContext.getResources().getStringArray(R.array.basic_info_name);
		mTvTitle = (TextView) findViewById(R.id.wallet_item_tv_name);
		mTvTime = (TextView) findViewById(R.id.wallet_item_tv_time);
		mTvIntergral = (TextView) findViewById(R.id.wallet_item_tv_intergral);
		mLine = (View) findViewById(R.id.wallet_item_iv_line);
		mIvMore = (ImageView) findViewById(R.id.wallet_item_iv_more);
		setOnClickListener(this);
	}

	public void setTvBackground() {
		mTvIntergral.setVisibility(View.GONE);
		mIvMore.setVisibility(View.VISIBLE);
	}

	
	public void setData(NewIntergralDataBean bean) {
		if (bean == null) {
			return;
		}
		setIsShowTvTime(false);//不用显示时间
		setIsShowLine(false);//不用显示底部分割线
		
		setTvTitle(bean.remark);
		if (bean.exp >= 0) {
			setTvIntergral("+" + bean.exp);
		} else {
			setTvIntergral("" + bean.exp);
		}
	}
	
	public void setData(WalletDataBean bean) {
		if (bean == null) {
			return;
		}
		setIsShowLine(false);
		setTvTitle(bean.remark);
		setTvTime(MyUtils.dateFromLong(bean.create_time));
		if (bean.type == 1) {
			// 收入
			setTvIntergral("+" + bean.money);
		} else if (bean.type == 2) {
			// 支出
			setTvIntergral("-" + bean.money);
		}
	}

	public void setTvTitle(String title) {
		mTvTitle.setText(title);
	}


	public void setTvTime(String time) {
		mTvTime.setText(time);
	}

	public void setTvIntergral(String intergral) {
		mTvIntergral.setText(intergral == null ? "" : intergral);
	}

	public void setIsShowLine(boolean isShow) {
		mLine.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
	}

	public void setIsShowTvTime(boolean isShow) {
		mTvTime.setVisibility(isShow ? View.VISIBLE : View.GONE);
	}

	public void setBackGround(String color) {
		mBackGround.setBackgroundColor(Color.parseColor(color));
	}

	public void setTvValuesColor(String color) {
		mTvIntergral.setTextColor(Color.parseColor(color));
	}

	public void setTvTitleColor(String color) {
		mTvTitle.setTextColor(Color.parseColor(color));
	}

	public String getTvValues() {
		return mTvIntergral.getText().toString().trim();
	}

	public String getTvIntegral() {
		return mTvIntergral.getText().toString().trim();
	}

	public void setValuesID(int position) {
		this.position = position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	public void setOnePosition(int position){
		this.oneBirthday = position;
	}

	/**
	 * 设置是否可以点击
	 * 
	 * @param enable
	 *            void
	 */
	public void setIsEnable(boolean enable) {
		isEnable = enable;
	}

	@Override
	public void onClick(View v) {
		String name = mTvTitle.getText().toString().trim();
		if (name.equals(mDatas[16])) {
			// 内心独白
			Intent intent = new Intent(getContext(), HeartShowActivity.class);
			if (isEnable) {
				intent.putExtra(HeartShowActivity.KEY, true);
				intent.putExtra(HeartShowActivity.KEY_JSON, obj.toString());
			}
			intent.putExtra(HeartShowActivity.CONTENT, getTvIntegral());
			getContext().startActivity(intent);
		}

		if (!isEnable) {
			return;
		}

		if (name.equals(MyInfoActivity.namesTwo[0])) {
			// 所在地
			TextView textView = new TextView(mContext);
			textView.setText(stringPosition+"");
			locationPickerDialog = new LocationPickerDialogs(mContext, "所在地", "key", 0,0, true, new ClickListener() {
				@Override
				public void yesClick(TextView textview, AreaBean province, AreaBean city, AreaBean district, String key) {
					try {
						infoObj.put("area_code", city.id);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					mTvIntergral.setText(city.name);
					stringPosition = province.name+city.name;
				}
				@Override
				public void noClick() {
				};
			}, textView);
			locationPickerDialog.show();
		} else if (name.equals(MyInfoActivity.namesTwo[1])) {
			// 交友状态
			list = resourceList(R.array.ChooseState);
			selectDialog = new SeclectSexDialog(mContext, name, position, list, new SeclectSexDialog.OnOneChickedListener() {
				@Override
				public void Onchicked(int positionID, String positionContent) {
					position = positionID;
					mTvIntergral.setText(positionContent);
					try {
						infoObj.put("dating_status", positionID + 1);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			selectDialog.show();
		} else if (name.equals(MyInfoActivity.namesTwo[2])) {
			// 婚恋资料
			Intent intent = new Intent(mContext, LoveInfoActivity.class);
			intent.putExtra(LoveInfoActivity.KEY_UID, LoginStatus.getLoginInfo().getUid());
			mContext.startActivity(intent);
		} else if (name.equals(mDatas[0])) {
			// ID
		} else if (name.equals(mDatas[1])) {
			// 出生日期
			ArrayList<String> one = getYearData();
			ArrayList<String> two = getMonthData();
			ArrayList<String> three = getDayData();
			if (oneBirthday == 0 || twoBirthday == 0 || threeBirthday == 0) {
				if (!TextUtils.isEmpty(stringPosition)) {
					try {
						String[] split = stringPosition.split("-");
						oneBirthday = getBirthdayPision(one, split[0]);
						twoBirthday = getBirthdayPision(two, split[1]);
						threeBirthday = getBirthdayPision(three, split[2]);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			seclectThreeDialog = new SeclectThreeDialog(mContext, name, one, two, three, oneBirthday, twoBirthday, threeBirthday,
					new OnOneChickedListener() {
						@Override
						public void Onchicked(String content) {
							obj.addProperty("birthday", content);
							stringPosition = content;
							mTvIntergral.setText(content);
						}

						@Override
						public void Onchicked(int onePositionID, String onePositionContent, int twoPositionID, String twoContent,
								int threePosition, String ThreeContent) {
							oneBirthday = onePositionID;
							twoBirthday = twoPositionID;
							threeBirthday = threePosition;
						}
					});
			seclectThreeDialog.show();
		} else if (name.equals(mDatas[2])) {
			// 星座
			list = resourceList(R.array.Constellation);
			selectDialog = new SeclectSexDialog(mContext, name, position, list, new SeclectSexDialog.OnOneChickedListener() {
				@Override
				public void Onchicked(int positionID, String positionContent) {
					position = positionID;
					obj.addProperty("constellations", positionID + 1);
					mTvIntergral.setText(positionContent);
				}
			});
			selectDialog.show();
		} else if (name.equals(mDatas[3])) {
			// 生肖
			list = resourceList(R.array.ZodiacSign);
			selectDialog = new SeclectSexDialog(mContext, name, position, list, new SeclectSexDialog.OnOneChickedListener() {
				@Override
				public void Onchicked(int positionID, String positionContent) {
					position = positionID;
					obj.addProperty("zodiak", positionID + 1);
					mTvIntergral.setText(positionContent);
				}
			});
			selectDialog.show();
		} else if (name.equals(mDatas[4])) {
			// 学历
			list = resourceList(R.array.Education_copy);
			selectDialog = new SeclectSexDialog(mContext, name, position, list, new SeclectSexDialog.OnOneChickedListener() {
				@Override
				public void Onchicked(int positionID, String positionContent) {
					position = positionID;
					if (positionID == 0) {
						obj.addProperty("edu", 3);
					} else if (positionID == 1) {
						obj.addProperty("edu", 5);
					} else if (positionID == 2) {
						obj.addProperty("edu", 6);
					} else if (positionID == 3) {
						obj.addProperty("edu", 9);
					}
					mTvIntergral.setText(positionContent);
				}
			});
			selectDialog.show();
		} else if (name.equals(mDatas[5])) {
			// 薪水
			list = resourceList(R.array.Salary);
			selectDialog = new SeclectSexDialog(mContext, name, position, list, new SeclectSexDialog.OnOneChickedListener() {
				@Override
				public void Onchicked(int positionID, String positionContent) {
					position = positionID;
					obj.addProperty("income", positionID + 10);
					mTvIntergral.setText(positionContent);
				}
			});
			selectDialog.show();
		} else if (name.equals(mDatas[6])) {
			if (heightList == null) {
				heightList = new ArrayList<>();
				for (int i = 0; i < 60; i++) {
					heightList.add(150 + i + "CM");
				}
				for (int i = 0; i < heightList.size(); i++) {
					String s = heightList.get(i);
					String substring = s.substring(0, s.indexOf("CM"));
					if (Integer.valueOf(substring) == position) {
						position = i;
					}
				}
			}

			selectDialog = new SeclectSexDialog(mContext, name, position, heightList, new SeclectSexDialog.OnOneChickedListener() {
				@Override
				public void Onchicked(int positionID, String positionContent) {
					position = positionID;
					int lastIndex = positionContent.lastIndexOf("CM");
					String substring = positionContent.substring(0, lastIndex);
					obj.addProperty("height", Integer.valueOf(substring));
					mTvIntergral.setText(positionContent);
				}
			});
			selectDialog.show();
		} else if (name.equals(mDatas[7])) {
			if (weightList == null) {
				weightList = new ArrayList<>();
				for (int i = 0; i < 60; i++) {
					weightList.add(40 + i + "KG");
				}
				for (int i = 0; i < weightList.size(); i++) {
					String s = weightList.get(i);
					String substring = s.substring(0, s.indexOf("KG"));
					if (Integer.valueOf(substring) == position) {
						position = i;
					}
				}
			}
			selectDialog = new SeclectSexDialog(mContext, name, position, weightList, new SeclectSexDialog.OnOneChickedListener() {
				@Override
				public void Onchicked(int positionID, String positionContent) {
					position = positionID;
					int lastIndex = positionContent.lastIndexOf("KG");
					String substring = positionContent.substring(0, lastIndex);
					obj.addProperty("weight", Integer.valueOf(substring));
					mTvIntergral.setText(positionContent);
				}
			});
			selectDialog.show();
		} else if (name.equals(mDatas[8])) {
			// 有无子女
			list = resourceList(R.array.Kid);
			selectDialog = new SeclectSexDialog(mContext, name, position, list, new SeclectSexDialog.OnOneChickedListener() {
				@Override
				public void Onchicked(int positionID, String positionContent) {
					position = positionID;
					obj.addProperty("child_status", positionID + 1);
					if (!TextUtils.isEmpty(positionContent)) {
						mTvIntergral.setText(positionContent);
					}
				}
			});
			;
			selectDialog.show();
		} else if (name.equals(mDatas[9])) {
			// 婚姻状况
			list = resourceList(R.array.Married);
			selectDialog = new SeclectSexDialog(mContext, name, position, list, new SeclectSexDialog.OnOneChickedListener() {
				@Override
				public void Onchicked(int positionID, String positionContent) {
					position = positionID;
					obj.addProperty("marital_status", positionID + 1);
					if (!TextUtils.isEmpty(positionContent)) {
						mTvIntergral.setText(positionContent);
					}
				}
			});
			selectDialog.show();
		} else if (name.equals(mDatas[10])) {
			// 购车情况
			list = resourceList(R.array.HasCar);
			selectDialog = new SeclectSexDialog(mContext, name, position, list, new SeclectSexDialog.OnOneChickedListener() {
				@Override
				public void Onchicked(int positionID, String positionContent) {
					position = positionID;
					obj.addProperty("car_status", positionID + 1);
					if (!TextUtils.isEmpty(positionContent)) {
						mTvIntergral.setText(positionContent);
					}
				}
			});
			selectDialog.show();
		} else if (name.equals(mDatas[11])) {
			// 购房情况
			list = resourceList(R.array.House);
			selectDialog = new SeclectSexDialog(mContext, name, position, list, new SeclectSexDialog.OnOneChickedListener() {
				@Override
				public void Onchicked(int positionID, String positionContent) {
					position = positionID;
					obj.addProperty("house_status", positionID + 1);
					if (!TextUtils.isEmpty(positionContent)) {
						mTvIntergral.setText(positionContent);
					}
				}
			});
			selectDialog.show();
		} else if (name.equals(mDatas[12])) {
			// 血型
			list = resourceList(R.array.BloodType);
			selectDialog = new SeclectSexDialog(mContext, name, position, list, new SeclectSexDialog.OnOneChickedListener() {
				@Override
				public void Onchicked(int positionID, String positionContent) {
					position = positionID;
					obj.addProperty("blood_type", positionID + 1);
					if (!TextUtils.isEmpty(positionContent)) {
						mTvIntergral.setText(positionContent);
					}
				}
			});
			selectDialog.show();
		} else if (name.equals(mDatas[13])) {
			// 民族
			list = resourceList(R.array.National_copy);
			selectDialog = new SeclectSexDialog(mContext, name, position, list, new SeclectSexDialog.OnOneChickedListener() {
				@Override
				public void Onchicked(int positionID, String positionContent) {
					position = positionID;
					obj.addProperty("nation", positionID + 1);
					if (!TextUtils.isEmpty(positionContent)) {
						mTvIntergral.setText(positionContent);
					}
				}
			});
			selectDialog.show();
		} else if (name.equals(mDatas[14])) {
			// 职业
			list = resourceList(R.array.Job);
			selectDialog = new SeclectSexDialog(mContext, name, position, list, new SeclectSexDialog.OnOneChickedListener() {
				@Override
				public void Onchicked(int positionID, String positionContent) {
					position = positionID;
					obj.addProperty("job", positionID + 1);
					if (!TextUtils.isEmpty(positionContent)) {
						mTvIntergral.setText(positionContent);
					}
				}
			});
			selectDialog.show();
		} else if (name.equals(mDatas[15])) {
			// 籍贯
			if (locationThreePickerDialog == null) {
				TextView textView = new TextView(mContext);
				textView.setText("文字tv");
				locationThreePickerDialog = new LocationPickerDialogs(mContext, "籍贯", "key", position, 0, false, new ClickListener() {
					@Override
					public void yesClick(TextView textview, AreaBean province, AreaBean city, AreaBean district, String key) {
						if (province != null) {
							obj.addProperty("home_area_code", province.id + "");
							mTvIntergral.setText(province.name);
						}
					}

					@Override
					public void noClick() {

					};
				}, textView);
			}
			locationThreePickerDialog.show();
		}

		if (mActivity != null && !name.equals(MyInfoActivity.namesTwo[2])) {
			mActivity.notifyClick();
		}
		if (mLoveInfoActivity != null && !name.equals(mDatas[16])) {
			mLoveInfoActivity.notifyClick();
		}

		if (selectDialog != null) {
			selectDialog.setOnDismissListener(this);
		}
	}

	private int getBirthdayPision(ArrayList<String> list, String string) {
		int temp = 0;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals(string)) {
				temp = i;
				break;
			}
		}
		return temp;
	}

	private ArrayList<String> getYearData() {
		Time time = new Time();
		time.setToNow();
		int year = time.year - 18;
		ArrayList<String> arrayList = new ArrayList<String>();
		for (int i = year - 60; i <= year; i++) {
			arrayList.add(i + "");
		}
		return arrayList;
	}

	private ArrayList<String> getMonthData() {
		ArrayList<String> arrayList = new ArrayList<String>();
		for (int i = 1; i <= 12; i++) {
			if (i < 10) {
				arrayList.add("0" + i + "");
			} else {
				arrayList.add(i + "");
			}
		}
		return arrayList;
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

	private ArrayList<String> containerData;
	private ArrayList<String> heightList;
	private ArrayList<String> weightList;

	private ArrayList<String> resourceList(int constellation) {
		if (containerData == null) {
			containerData = new ArrayList<String>();
		} else {
			containerData.clear();
		}
		String[] stringArray = mContext.getResources().getStringArray(constellation);
		for (int i = 0; i < stringArray.length; i++) {
			containerData.add(stringArray[i]);
		}
		return containerData;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		if (dialog.equals(selectDialog)) {
			selectDialog = null;
			dialog = null;
		}
	}

	public void addJsonPreferences(String key, String value) {
		try {
			Integer valueOf = Integer.valueOf(value);
			obj.addProperty(key, valueOf);
		} catch (Exception e) {
			obj.addProperty(key, value);
		}
	}

	private String stringPosition;

	public void setStringPosition(String string) {
		stringPosition = string;
	}
}
