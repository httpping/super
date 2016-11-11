/**   
* @Title: ReportedSubmitItem.java 
* @Package com.zngoo.pczylove.model 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zeus   
* @date 2015-9-28 上午11:13:36 
* @version V1.0   
*/
package com.vp.loveu.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**

 *
 * @ClassName:
 * @Description:提交红娘信息实体类
 * @author zeus
 * @date 
 */
public class ReportedSubmitItem implements Parcelable {
	// 成员变量

	// 1.必须实现Parcelable.Creator接口,否则在获取Person数据的时候，会报错，如下：
	// android.os.BadParcelableException:
	// Parcelable protocol requires a Parcelable.Creator object called CREATOR
	// on class com.um.demo.Person
	// 2.这个接口实现了从Percel容器读取Person数据，并返回Person对象给逻辑层使用
	// 3.实现Parcelable.Creator接口对象名必须为CREATOR，不如同样会报错上面所提到的错；
	// 4.在读取Parcel容器里的数据事，必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
	// 5.反序列化对象
	private String mSex;
	private String mBirthday;
	private String mHeight;
	private String mProvince;
	private String mCity;
	private String mRegion;
	private String mRelation;
	private String mBrief;
	public static final Parcelable.Creator<ReportedSubmitItem> CREATOR = new Creator() {
		@Override
		public ReportedSubmitItem createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			// 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
			ReportedSubmitItem p = new ReportedSubmitItem();
			p.setmSex(source.readString());
			p.setmBirthday(source.readString());
			p.setmHeight(source.readString());
			p.setmProvince(source.readString());
			p.setmCity(source.readString());
			p.setmRegion(source.readString());
			p.setmRelation(source.readString());
			p.setmBrief(source.readString());
			return p;
		}

		@Override
		public ReportedSubmitItem[] newArray(int size) {
			// TODO Auto-generated method stub
			return new ReportedSubmitItem[size];
		}
	};

	public String getmSex() {
		return mSex;
	}

	public void setmSex(String mSex) {
		this.mSex = mSex;
	}

	public String getmBirthday() {
		return mBirthday;
	}

	public void setmBirthday(String mBirthday) {
		this.mBirthday = mBirthday;
	}

	public String getmHeight() {
		return mHeight;
	}

	public void setmHeight(String mHeight) {
		this.mHeight = mHeight;
	}

	public String getmProvince() {
		return mProvince;
	}

	public void setmProvince(String mProvince) {
		this.mProvince = mProvince;
	}

	public String getmCity() {
		return mCity;
	}

	public void setmCity(String mCity) {
		this.mCity = mCity;
	}

	public String getmRegion() {
		return mRegion;
	}

	public void setmRegion(String mRegion) {
		this.mRegion = mRegion;
	}

	public String getmRelation() {
		return mRelation;
	}

	public void setmRelation(String mRelation) {
		this.mRelation = mRelation;
	}

	public String getmBrief() {
		return mBrief;
	}

	public void setmBrief(String mBrief) {
		this.mBrief = mBrief;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	// mSex, mBirthday, mHeight, mProvince, mCity, mRegion, mRelation,
	// mBrief
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		// 1.必须按成员变量声明的顺序封装数据，不然会出现获取数据出错
		// 2.序列化对象
		dest.writeString(mSex);
		dest.writeString(mBirthday);
		dest.writeString(mHeight);
		dest.writeString(mProvince);
		dest.writeString(mCity);
		dest.writeString(mRegion);
		dest.writeString(mRelation);
		dest.writeString(mBrief);
	}
}
