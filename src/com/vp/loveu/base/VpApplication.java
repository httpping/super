/**   
* @Title: VpApplication.java 
* @Package com.vp.loveu.base 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zeus   
* @date 2015-10-20 下午4:25:29 
* @version V1.0   
*/
package com.vp.loveu.base;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.custom.ShareModel;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.vp.loveu.bean.AppconfigBean;
import com.vp.loveu.bean.ChatItem;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.login.bean.WechatUserBean;
import com.vp.loveu.pay.bean.EnjoyPayBean;
import com.vp.loveu.pay.bean.PayBindViewBean;
import com.vp.loveu.util.SharedPreferencesHelper;

/**

 * 
 * @ClassName:
 * @Description: 基础的APPication 
 * @author 平
 * @date 
 */
public class VpApplication extends  Application{

	private Bitmap myAvatar;
	private Bitmap uAvatar;
	private static VpApplication mInstance;
	private String Avatar;
	private LoginUserInfoBean user=null;
	private Map<String, AppconfigBean> appInfoBean=null;
	private JSONObject natural;
	private JSONObject community;
	private JSONObject criteria;

	private JSONObject friendcondition;
	private JSONObject expectMarry;
	/** 会话列表 **/
	private ArrayList<ChatItem> chatList = new ArrayList<ChatItem>();
	public static int msgCount;
	public static int notifyMsgCount;
	public static int msgAllCount;
	private String pageIndex_dynamics;
	
	
	public PayBindViewBean mPayBindViewBean;//微信支付需要用到
	public EnjoyPayBean mEnjoyPayBindBean;//微信支付需要用到

	public boolean payresult; 

	private static Context	mContext;
	private static Handler	mHandler;
	
	private WechatUserBean wechatUserBean;
	private ShareModel shareModel;
	
	//正播放的电台ID
	private int mRadioId=-1;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		/*GSHandler crashHandler = GSHandler.getInstance();
		crashHandler.init(this);*/
		mContext=this;
		mHandler=new Handler();
//		SpeakDBManager.init(getApplicationContext());
//		ShareSDK.initSDK(getApplicationContext());
//		JPushInterface.setDebugMode(true);
//		JPushInterface.init(this);
//		ImageLoaderInit.initImageLoader(this);
		File fileapp = new File(VpConstants.HOME_DIR);
		File filememo = new File(VpConstants.IMAGEPATH);
		if (!fileapp.exists()) {
			sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
			sharedPreferencesHelper.putBooleanValue(VpConstants.SharedKey.cb_news,
					true);
			sharedPreferencesHelper.putBooleanValue(
					VpConstants.SharedKey.cb_shake, true);
			sharedPreferencesHelper.putBooleanValue(
					VpConstants.SharedKey.cb_voice, true);

			fileapp.mkdirs();
		}
		if (!filememo.exists()) {
			filememo.mkdirs();
		}
		
		//add by ping  检查所有不规范的代码 开发模式使用
		/*if (VpConstants.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
		}*/
		//end
		
		//初始化imageloader
		ImageLoaderConfiguration conf = ImageLoaderConfiguration.createDefault(this);
		ImageLoader.getInstance().init(conf);
		
		ShareSDK.initSDK(this);
      //  SmackAndroid.init(this);

	}


	public Bitmap getMyAvatar() {
		return myAvatar;
	}

	public void setMyAvatar(Bitmap myAvatar) {
		this.myAvatar = myAvatar;
	}

	public Bitmap getuAvatar() {
		return uAvatar;
	}

	public void setuAvatar(Bitmap uAvatar) {
		this.uAvatar = uAvatar;
	}

	public JSONObject getFriendcondition() {
		return friendcondition;
	}

	public void setFriendcondition(JSONObject friendcondition) {
		this.friendcondition = friendcondition;
	}

	public JSONObject getMarryHope() {
		return marryHope;
	}

	public void setMarryHope(JSONObject marryHope) {
		this.marryHope = marryHope;
	}

	private JSONObject marryHope;

	public JSONObject getAnotherpart() {
		return anotherpart;
	}

	public void setAnotherpart(JSONObject anotherpart) {
		this.anotherpart = anotherpart;
	}

	private JSONObject anotherpart;
	private JSONObject phonecode;

	private String appid;
	private String orderID;

	// 个人中心
	private Boolean base = true;
	private boolean condition = true;
	private boolean relationship = true;
	private boolean base1 = true;
	private boolean condition1 = true;
	private boolean relationship1 = true;

	private String CusID = "";

	private String CookCode = "";

	private int screenWidth;
	private int screenHeight;
	private String nickName;

	private Boolean isUserData = true;
	private Boolean isChooseData = true;
	private Boolean isMarryData = true;
	// 活动
	private String pageIndex;
	private String pageSize;
	private String activityID;
	private String pageIndex_apply;
	private String pageSize_apply;
	private String fee;
	private int i;

	// 恋爱宝典
	private String pageIndex_lovebook;
	private String pageSize_lovebook;
	private String pageIndex_lovestory;
	private String pageSize_lovestory;
	private String pageIndex_loveword;
	private String pageSize_loveword;

	// 交友
	private String pageIndex_surrend;
	private int aa;// 用来计算需要删除的position

	// 个人动态
	private String pagedynamicsIndex;
	private String pagedynamicsSize;

	public Boolean getIsUserData() {
		return isUserData;
	}

	public void setIsUserData(Boolean isUserData) {
		this.isUserData = isUserData;
	}

	public Boolean getIsChooseData() {
		return isChooseData;
	}

	public void setIsChooseData(Boolean isChooseData) {
		this.isChooseData = isChooseData;
	}

	public Boolean getIsMarryData() {
		return isMarryData;
	}

	public void setIsMarryData(Boolean isMarryData) {
		this.isMarryData = isMarryData;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getHigh() {
		return high;
	}

	public void setHigh(String high) {
		this.high = high;
	}

	public String getEdu() {
		return edu;
	}

	public void setEdu(String edu) {
		this.edu = edu;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getPlate() {
		return plate;
	}

	public void setPlate(String plate) {
		this.plate = plate;
	}

	public String getMarried() {
		return married;
	}

	public void setMarried(String married) {
		this.married = married;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	private String gender;
	private String high;
	private String edu;
	private String province;
	private String city;
	private String region;
	private String plate;
	private String married;
	private String salary;

	private float density;

	private List<Activity> mLists = new LinkedList<Activity>();

	private ArrayList<String> friends = new ArrayList<String>();

	private SharedPreferencesHelper sharedPreferencesHelper;

	public static VpApplication getInstance() {
		return mInstance;
	}

	public void addActivity(Activity activity) {
		mLists.add(activity);
	}

	public ArrayList<ChatItem> getChatList() {
		return chatList;
	}

	public void setChatList(ArrayList<ChatItem> chatList, int count) {
		this.chatList = chatList;
		msgCount = count;
	}

	public static void setNotifyMsgCount(int count) {
		notifyMsgCount = count;
	}

	public static int getMsgCount() {
		return msgCount;
	}

	public static int getNotifyMsgCount() {
		return notifyMsgCount;
	}

	public static int getMsgAllCount() {
		msgAllCount = notifyMsgCount + msgCount;
		return msgAllCount;
	}

	public void exit() {
		try {
			for (Activity activity : mLists) {
				if (activity != null)
					activity.finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	public LoginUserInfoBean getUser() {
		return user;
	}

	public void setUser(LoginUserInfoBean user) {
		this.user = user;
	}

	public String getCuid() {
		if (null == CusID || CusID.equals("") || CusID.length() <= 0) {

			if (null == sharedPreferencesHelper) {
				sharedPreferencesHelper = SharedPreferencesHelper
						.getInstance(this);
			}
			return sharedPreferencesHelper
					.getStringValue(VpConstants.HttpKey.CusID);
		}
		return CusID;
	}

	public void setCuid(String cuid) {
		this.CusID = cuid;
	}

	public JSONObject getNatural() {
		return natural;
	}

	public void setNatural(JSONObject natural) {
		this.natural = natural;
	}

	public JSONObject getCommunity() {
		return community;
	}

	public void setCommunity(JSONObject community) {
		this.community = community;
	}

	public JSONObject getPhonecode() {
		return phonecode;
	}

	public void setPhonecode(JSONObject phonecode) {
		this.phonecode = phonecode;
	}

	public JSONObject getCriteria() {
		return criteria;
	}

	public void setCriteria(JSONObject criteria) {
		this.criteria = criteria;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public float getDensity() {
		return density;
	}

	public void setDensity(float density) {
		this.density = density;
	}

	public ArrayList<String> getFriends() {
		return friends;
	}

	public void setFriends(ArrayList<String> friends) {
		this.friends = friends;
	}

	public String getCookCode() {

		if (null == CookCode || CookCode.equals("") || CookCode.length() <= 0) {

			if (null == sharedPreferencesHelper) {
				sharedPreferencesHelper = SharedPreferencesHelper
						.getInstance(this);
			}
			return sharedPreferencesHelper
					.getStringValue(VpConstants.HttpKey.CookCode);
		}
		return CookCode;
	}

	public void setCookCode(String cookCode) {
		CookCode = cookCode;
	}

	public String getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(String pageIndex) {
		this.pageIndex = pageIndex;
	}

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	public String getActivityID() {
		return activityID;
	}

	public void setActivityID(String activityID) {
		this.activityID = activityID;
	}

	public String getPageIndex_apply() {
		return pageIndex_apply;
	}

	public void setPageIndex_apply(String pageIndex_apply) {
		this.pageIndex_apply = pageIndex_apply;
	}

	public String getPageSize_apply() {
		return pageSize_apply;
	}

	public void setPageSize_apply(String pageSize_apply) {
		this.pageSize_apply = pageSize_apply;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getPageIndex_lovebook() {
		return pageIndex_lovebook;
	}

	public void setPageIndex_lovebook(String pageIndex_lovebook) {
		this.pageIndex_lovebook = pageIndex_lovebook;
	}

	public String getPageSize_lovebook() {
		return pageSize_lovebook;
	}

	public void setPageSize_lovebook(String pageSize_lovebook) {
		this.pageSize_lovebook = pageSize_lovebook;
	}

	public String getPageIndex_lovestory() {
		return pageIndex_lovestory;
	}

	public void setPageIndex_lovestory(String pageIndex_lovestory) {
		this.pageIndex_lovestory = pageIndex_lovestory;
	}

	public String getPageSize_lovestory() {
		return pageSize_lovestory;
	}

	public void setPageSize_lovestory(String pageSize_lovestory) {
		this.pageSize_lovestory = pageSize_lovestory;
	}

	public String getPageIndex_loveword() {
		return pageIndex_loveword;
	}

	public void setPageIndex_loveword(String pageIndex_loveword) {
		this.pageIndex_loveword = pageIndex_loveword;
	}

	public String getPageSize_loveword() {
		return pageSize_loveword;
	}

	public void setPageSize_loveword(String pageSize_loveword) {
		this.pageSize_loveword = pageSize_loveword;
	}

	public JSONObject getExpectMarry() {
		return expectMarry;
	}

	public void setExpectMarry(JSONObject expectMarry) {
		this.expectMarry = expectMarry;
	}

	public String getPageIndex_dynamics() {
		return pageIndex_dynamics;
	}

	public void setPageIndex_dynamics(String pageIndex_dynamics) {
		this.pageIndex_dynamics = pageIndex_dynamics;
	}

	public String getPageIndex_surrend() {
		return pageIndex_surrend;
	}

	public void setPageIndex_surrend(String pageIndex_surrend) {
		this.pageIndex_surrend = pageIndex_surrend;
	}

	public int getAa() {
		return aa;
	}

	public void setAa(int aa) {
		this.aa = aa;
	}

	public String getAvatar() {
		return Avatar;
	}

	public void setAvatar(String avatar) {
		Avatar = avatar;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getOrderID() {
		return orderID;
	}

	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public Boolean getBase() {
		return base;
	}

	public void setBase(boolean base) {
		this.base = base;
	}

	public boolean isCondition() {
		return condition;
	}

	public void setCondition(boolean condition) {
		this.condition = condition;
	}

	public boolean isRelationship() {
		return relationship;
	}

	public void setRelationship(boolean relationship) {
		this.relationship = relationship;
	}

	public boolean isBase1() {
		return base1;
	}

	public void setBase1(boolean base1) {
		this.base1 = base1;
	}

	public boolean isCondition1() {
		return condition1;
	}

	public void setCondition1(boolean condition1) {
		this.condition1 = condition1;
	}

	public boolean isRelationship1() {
		return relationship1;
	}

	public void setRelationship1(boolean relationship1) {
		this.relationship1 = relationship1;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getPagedynamicsIndex() {
		return pagedynamicsIndex;
	}

	public void setPagedynamicsIndex(String pagedynamicsIndex) {
		this.pagedynamicsIndex = pagedynamicsIndex;
	}

	public String getPagedynamicsSize() {
		return pagedynamicsSize;
	}

	public void setPagedynamicsSize(String pagedynamicsSize) {
		this.pagedynamicsSize = pagedynamicsSize;
	}	

	public static Context getContext() {
		return mContext;
	}

	public static Handler getHandler() {
		return mHandler;
	}


	public Map<String, AppconfigBean> getAppInfoBean() {
		return appInfoBean;
	}


	public void setAppInfoBean(Map<String, AppconfigBean> appInfoBean) {
		this.appInfoBean = appInfoBean;
	}


	public WechatUserBean getWechatUserBean() {
		return wechatUserBean;
	}


	public void setWechatUserBean(WechatUserBean wechatUserBean) {
		this.wechatUserBean = wechatUserBean;
	}


	public ShareModel getShareModel() {
		return shareModel;
	}


	public void setShareModel(ShareModel shareModel) {
		this.shareModel = shareModel;
	}


	public int getmRadioId() {
		return mRadioId;
	}


	public void setmRadioId(int mRadioId) {
		this.mRadioId = mRadioId;
	}
	
	
	
	
}
