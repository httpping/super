/**   
* @Title: VpConstants.java 
* @Package com.vp.loveu.comm 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zeus   
* @date 2015-10-20 下午4:28:14 
* @version V1.0   
*/
package com.vp.loveu.comm;

import android.os.Environment;

/**
 *
 * 
 * @ClassName: 常量
 * @Description:
 * @author ping
 * @date
 */
public interface VpConstants {

	public static final boolean DEVELOPER_MODE = false; // 开发模式，

	
	 
	
	// 服务器配置test
//	public static final int PORT = 9711; // 端口
//	public static final String HOST = "http://115.29.244.85:" + PORT; // IP
//	public static final String FILE_HOST = "http://115.29.244.85:9713"; // 文件上传
//
//	public static final int WEB_PORT = 9712;
//	public static final String WEB_HOST = "http://115.29.244.85:" + WEB_PORT;
//	public static final String KEY_DD = "1234567891234567"; 
 
	
	// 服务器配置product
	public static final int PORT = 80; // 端口
	public static final String HOST = "http://api.iuapp.cn:" + PORT; // IP
	public static final int WEB_PORT = 80;
	public static final String WEB_HOST = "http://g.iuapp.cn:" + WEB_PORT;
	public static final String FILE_HOST = "http://img1.iuapp.cn"; // 文件上传
	public static final String KEY_DD = "&^(*3^$#@!fdsf!#";

	
	public static final int VERSION = 10;// 版本号，用来跟服务器关联
	public static final String API_START = "/api/v1";
	
	
	public static final String KEY_QR_PASS ="lvu@&*^%#2016;<>";
	

	// 文件上传服务器
	public static final String FILE_UPLOAD = FILE_HOST + "/upload"; // 文件上传path
	public static final String FILE_UPLOAD_PATH_PORTRAIT = "portrait"; // 文件上传
																		// 头像地址
	public static final String FILE_UPLOAD_PATH_PIC_FILE = "pic_file"; // 图片文件上传地址
	public static final String FILE_UPLOAD_PATH_AMR_FILE = "amr_file"; // 录音文件上传地址
	public static final String aaa = HOST + API_START + "/activities/list";

	// test URL
	public static final String TEST_REQUEST = "https://httpbin.org/get";// 版本号，用来跟服务器关联
	public static final String TEST_REQUEST_POST = "https://httpbin.org/post";// 版本号，用来跟服务器关联

	public static final String TEST_REQUEST_DEMO = HOST + "/api/v1/demo/get_demo";// 版本号，用来跟服务器关联
	public static final String TEST_REQUEST_DEMO_ENC = HOST + "/api/v1/demo/get_enc";// 版本号，用来跟服务器关联

	public static final String TEST_REQUEST_DEMO_POST = HOST + "/api/v1/demo/post_demo";// 版本号，用来跟服务器关联
	public static final String TEST_REQUEST_DEMO_POST_ENC = HOST + API_START + "/demo/post_enc";// 版本号，用来跟服务器关联

	public static final String USER_ONLINE_REMIND = HOST + API_START + "/user/set_online_remind";// 3.3.1.15.设置上线提醒接口
	public static final String USER_IS_ONLINE_REMIND = HOST + API_START + "/user/is_online_remind";// 3.3.1.40.检查是否设置用户上线提醒接口
	public static final String USER_TRIGGER_EXP = HOST + API_START + "/user/trigger_exp";// 3.3.1.31.触发积分接口
	public static final String USER_CHAT_TRIGGER_EXP = HOST + API_START + "/user/chat_report";// 3.3.1.41.聊天上报接口

	public static final String PAY_SUCCESS_NOTIFY = HOST + API_START + "/payment/pay_notify";// 3.3.12.1.
																								// 支付完成通知接口

	// 活动报名
	public static final String ACTIVITY_PAY = HOST + API_START + "/activities/apply";// 3.3.3.3.活动报名接口
	public static final String HELP_APPLY = HOST + API_START + "/help/apply";// 3.3.4.2.发布求助接口
	public static final String CLASSROOM_APPLY = HOST + API_START + "/classroom/apply";// 3.3.11.3.在线课程购买接口
	
	public static final String ACTIVITY_SIGN_IN = HOST + API_START + "/activities/qr_scan";// 3.3.3.5.活动扫描接口
	
	//new 
	public static final String ENJOY_PAY_URL = HOST + API_START + "/general/add_reward";// 3.3.15.6.打赏接口
	

	// end
	

	// 动态模块的接口地址 ADD BY PING
	public static final String DYNAMIC_ADD = HOST + API_START + "/timeline/add";// 3.4.1.发布动态接口
	public static final String DYNAMIC_HOME_INDEX = HOST + API_START + "/timeline/get_home_timeline";// 3.4.2.获取首页动态接口
	public static final String DYNAMIC_GET_USER_TIMELINE = HOST + API_START + "/timeline/get_user_timeline";// 3.4.3.获取用户动态接口
	public static final String DYNAMIC_POST_UPDATE_PRAISE = HOST + API_START + "/timeline/update_praise";// 3.4.4.上报赞接口
	public static final String DYNAMIC_POST_UPDATE_SHARE = HOST + API_START + "/timeline/update_share";// 3.4.5.上报分享接口
	public static final String DYNAMIC_POST_DELETE = HOST + API_START + "/timeline/delete";// 3.4.6.删除动态接口
	// end 动态模块接口

	// 登录注册模块 add by pzj
	public static final String USER_SEND_SMS_CODE = HOST + API_START + "/basis/post_mt_auth_code";// 短信验证码发送接口
	public static final String USER_REGISTER = HOST + API_START + "/user/reg";// 3.3.2.注册接口
	public static final String USER_LOGIN = HOST + API_START + "/user/login";// 3.3.3.登陆接口
	public static final String USER_RESETPWD = HOST + API_START + "/user/find_psw";// 3.3.7.回密码接口
	public static final String USER_APP_CONFIG = HOST + API_START + "/basis/get_config";// 3.2.5.获取软件基本配置接口
	public static final String USER_LOGIN_INFO = HOST + API_START + "/user/login_info";// 3.3.1.2.获取登录完善资料信息接口
	public static final String USER_LOGIN_FILL_INFO = HOST + API_START + "/user/fill_login_info";// 3.3.1.3.完善登陆资料接口

	public static final String USER_LOGIN_USER_PROTOTOL = WEB_HOST + "/go/2?v=";
	public static final String APP_SIGN_IN_GO = WEB_HOST + "/go/107?token=";// 1.2.4.PUA课堂详情跳转

	// end 登录注册模块

	// 频道模块 add by pzj
	public static final String CHANNEL_HOME = HOST + API_START + "/home/channel";// 3.3.6.频道首页接口
	public static final String CHANNEL_LIST = HOST + API_START + "/radio/list";// 3.3.9.1.电台列表接口
	public static final String CHANNEL_RADIO_DETAIL = HOST + API_START + "/radio/detail";// 3.3.9.2.电台详情接口
	public static final String CHANNEL_RADIO_LISTEN = HOST + API_START + "/radio/listen";// 上报用户收听电台接口
	public static final String CHANNEL_RADIO_LEAVE = HOST + API_START + "/radio/leave";// 3.3.9.3.用户退出电台接口
	public static final String CHANNEL_VIDEO_DETAIL = HOST + API_START + "/video/detail";// 3.3.8.2.视频详情接口
	public static final String CHANNEL_FORUM_LIST = HOST + API_START + "/forum/list";// 3.3.7.1.获取主题列表接口
	public static final String CHANNEL_FORUM_DETAIL = HOST + API_START + "/forum/detail";// 3.3.7.2.获取主题的回复列表接口
	public static final String CHANNEL_FORUM_REPLY = HOST + API_START + "/forum/reply";// 3.3.7.3.发布回复接口
	public static final String CHANNEL_USER_CREATE_FOLLOW = HOST + API_START + "/user/create_follow";// 3.3.1.8.关注用户接口
	public static final String CHANNEL_USER_COMPLAIN = HOST + API_START + "/user/complain";// 3.3.1.17.举报用户接口
	public static final String CHANNEL_GENERAL_ADD_PRAISE = HOST + API_START + "/general/add_praise";// 3.3.15.3.点赞接口
	public static final String CHANNEL_GENERAL_ADD_FAVORITE = HOST + API_START + "/general/add_favorite";// 3.3.15.3.点赞接口
	public static final String CHANNEL_VIDEO_LEARN = HOST + API_START + "/video/learn";// 3.3.8.3.上报视频学习接口
	public static final String CHANNEL_FORUM_DELETE = HOST + API_START + "/forum/delete";// 3.3.7.4.删除回复接口
	// end频道模块

	// 发现模块 add by pzj
	public static final String DISCOVER_HOME = HOST + API_START + "/classroom/index";// 3.3.11.1.获取在线课堂首页接口
	public static final String DISCOVER_COURSE_DETAIL = HOST + API_START + "/classroom/detail";// 3.3.11.2.获取在线课堂详情接口
	public static final String DISCOVER_NEWS_PUA = HOST + API_START + "/news/pua";// 3.3.5.1.PUA课堂接口
	public static final String DISCOVER_NEWS_LIST = HOST + API_START + "/news/list";// 3.3.5.2.资讯列表接口
	public static final String DISCOVER_MATCHMARKER_LIST = HOST + API_START + "/matchmaker/get_list";// 3.3.10.1.获取红娘接口
	public static final String DISCOVER_MATCHMARKER_APPLY = HOST + API_START + "/matchmaker/apply";// 3.3.10.2.提交需求接口
	public static final String DISCOVER_SUPERVISOR = HOST + API_START + "/discover/supervisor";// 3.3.13.2.在线导师接口
	public static final String DISCOVER_PUA_DETAIL = WEB_HOST + "/go/103?id=";// 1.2.4.PUA课堂详情跳转
	public static final String DISCOVER_PUA_DETAIL_INFO = HOST + API_START + "/news/detail";// 3.3.5.3.获取资讯详情接口
	public static final String DISCOVER_COURSE_WEB_DETAIL = WEB_HOST + "/go/104?";// 1.2.5.在线课堂详情跳转
	public static final String DISCOVER_INDEX = HOST + API_START + "/discover/index";// 3.3.13.4.发现首页接口

	// end发现模块

	// 对话 评论回复
	public static final String MESSAGE_USER_REPLY_LIST = HOST + API_START + "/user/reply_list";// 3.3.1.26.我的回复列表
	public static final String MESSAGE_USER_LIKED_LIST = HOST + API_START + "/user/liked";// 3.3.1.34.赞我的列表
	public static final String MESSAGE_USER_HELP_HISTORY = HOST + API_START + "/user/help_history";// 3.3.1.27.被邀请情感解答列表接口
	public static final String MESSAGE_USER_RESOLOVE_HELP = HOST + API_START + "/user/resolve_help";// 3.3.1.28.解答情感问题接口
	
	//分享上传接口
	public static final String PUBLIC_ADD_SHARE = HOST + API_START + "/general/add_share";// 3.3.15.5.分享完成上报接口

	//获取用户账户绑定信息接口
	public static final String USER_BIND_INFO = HOST + API_START + "/user/bind_info";
	//我的积分首页接口
	public static final String MY_INTEGERAL = HOST + API_START + "/user/exp_index";
	//绑定用户接口
	public static final String USER_BINDING = HOST + API_START + "/user/binding";
	//账号解除绑定
	public static final String USER_UNBINDING = HOST + API_START + "/user/unbinding";
	
	// 我的收藏 删除收藏
	public static final String My_DELETE_FAVORITE = HOST + API_START + "/general/delete_favorite";// 3.3.15.2.删除收藏接口
	// 我的模块 获取用户基本资料
	public static final String My_INFO = HOST + API_START + "/user/user_info";
	// 我的模块 保存用户基本资料
	public static final String My_SAVE_INFO = HOST + API_START + "/user/save_baisc_info";
	// 提交建议
	public static final String SEND_FEEKBACK = HOST + API_START + "/basis/add_suggestion";
	// 获取用户的统计信息
	public static final String MY_COUNT_INFO = HOST + API_START + "/user/get_statistics";
	// index 首页接口
	public static final String INDEX_URL = HOST + API_START + "/home/index";
	// 我的钱包接口
	public static final String MY_CENTER_PAY = HOST + API_START + "/user/wallet";
	// 获取附近的人接口
	public static final String NEARBY_URL = HOST + API_START + "/discover/nearby";
	// 更多精选接口
	public static final String MORE_CONTENT_URL = HOST + API_START + "/news/list";
	// 同城活动列表接口
	public static final String CITY_ACTIVE_LIST_URL = HOST + API_START + "/activities/index";
	// 情感求助接口
	public static final String FELL_HELP_URL = HOST + API_START + "/help/index";
	// 我的支付列表接口
	public static final String MY_PALYER_LIST_URL = HOST + API_START + "/user/orders";
	// 我关注的接口
	public static final String MY_ME_FOLLOW_URL = HOST + API_START + "/user/friends";
	// 关注我的接口
	public static final String MY_FOLLOW_ME_URL = HOST + API_START + "/user/follows";
	//获取apk是否升级
	public static final String APK_UPGRADE = HOST + API_START + "/basis/upgrade";
	// 活跃用户排行
	public static final String HOT_USER_SORT = HOST + API_START + "/discover/exp_rank";
	// 我的收藏的接口
	public static final String COLLECT_ARTICLE_URL = HOST + API_START + "/user/favorites";
	// 我的情感求助接口
	public static final String MY_FELL_URL = HOST + API_START + "/user/help";
	// 用户个人主页信息
	public static final String USER_INDEX_INFO_URL = HOST + API_START + "/user/home";
	// 获取用户婚恋资料
	public static final String LOVE_INFO_URL = HOST + API_START + "/user/user_dating/";
	// 保存用户婚恋资料
	public static final String LOVE_INFO_SAVE_URL = HOST + API_START + "/user/save_user_dating";
	// 积分3.3.1.20.我的积分历史接口
	public static final String MY_EXP_HISTORY = HOST + API_START + "/user/exp_history";
	// 发布情感求助接口
	public static final String SEND_HELP = HOST + API_START + "/help/apply";
	// 我的活动列表接口
	public static final String MY_ACTIVE_LIST_RUL = HOST + API_START + "/user/activities";
	// 保存用户基本信息资料
	public static final String MY_SAVE_USER_INFO_RUL = HOST + API_START + "/user/save_user_info";
	// 在线获取情感解答接口
	public static final String GET_FREE_FELL_HELP = HOST + API_START + "/help/answers";
	// 获取活动详情接口
	public static final String GET_ACTIVE_INFO = HOST + API_START + "/activities/detail";
	// 申请再次举办
	public static final String CONTINUE_ACTIVE = HOST + API_START + "/activities/apply_again";
	// 我的动态列表接口
	public static final String MY_TIME_LINE = HOST + API_START + "/user/timeline";
	// 检测用户是否已经被关注接口
	public static final String SELECT_FOLLOW_USER = HOST + API_START + "/user/is_follow";
	// 取消关注用户接口
	public static final String CANCLE_FOLLOW_USER = HOST + API_START + "/user/destroy_follow";
	//推荐用户接口
	public static final String INDEX_HOT_URL = HOST + API_START + "/home/index_user";
	// 活动详情的外链地址
	public static final String ACTIVE_WEB_URL = WEB_HOST + "/go/101?id=";
	// 精选长文的外链地址
	public static final String ARTICLE_WEB_URL = WEB_HOST + "/go/102?id=";
	// 应用分享
	public static final String APP_SHARE = WEB_HOST + "/go/1?app_is_installed=1";
	// 关于我们外链地址
	public static final String ABOUT_US = WEB_HOST + "/go/4?v=" + VERSION;
	// 电台上传须知  
	public static final String AUDIO_UPLOAD_NOTICE = WEB_HOST + "/go/6?v=" + VERSION;
	// 常见问题外链地址
	public static final String FAQ = WEB_HOST + "/go/5?v=" + VERSION;
	// 积分规则页面
	public static final String INTERGRAL = WEB_HOST + "/go/3?v=" + VERSION;
	// 取消关注用户接口
	public static final String PAY_ZXING = WEB_HOST + "/go/104";
	//个人主页web
	public static final String USER_WEB = WEB_HOST + "/go/100?id=";
	//视频分享详情跳转  
	public static final String VIDEO_SHARE = WEB_HOST + "/go/105?id=";	
	//电台分享详情跳转
	public static final String AUDIO_SHARE = WEB_HOST + "/go/106?id=";	
	//3.3.1.42.获取我的课程列表接口
	public static final String MY_COURSE =  HOST + API_START + "/user/lessons";
	//添加优惠码接口
	public static final String ADD_PROMO_CODE =  HOST + API_START + "/user/add_coupon";
	//我添加的优惠码接口
	public static final String MY_ADD_PROMO_CODE =  HOST + API_START + "/user/add_coupon_list";
	//编辑优惠码接口
	public static final String EDIT_PROMO_CODE =  HOST + API_START + "/user/edit_coupon";
	//删除优惠码接口
	public static final String DELETE_PROMO_CODE =  HOST + API_START + "/user/remove_coupon";
	//使用优惠码接口
	public static final String USE_PROMO_CODE =  HOST + API_START + "/user/use_coupon";
	//上传恋爱电台接口
	public static final String ADD_AUDIO =  HOST + API_START + "/radio/add";

	
	
	
	
	public static final String ip = "120.24.250.66";
	public static final String openfire_id = "120.24.250.66";

	//
	public static final String openfire_server_name = "iz949hl0mxgz";
	public static final String Action = "http://" + ip;
	public static final String Action_type = Action + "/";
	public static final String Action_file = "http://" + ip;
	public static final String ERROR_INT = "网络连接超时";
	public static final String ERROR_SERVER = "服务器数据错误";

	/** 目录根路径 */
	public static final String HOME_DIR = Environment.getExternalStorageDirectory() + "/loveu/";

	/**
	 * 图片存放更目录
	 */
	public static final String IMAGEPATH = HOME_DIR + "image/";

	/**
	 * 拍照缓存文件名
	 */
	public static final String CACHEIMAGENAME = "cacheimage.jpg";

	/**
	 * 数据更变的广播
	 */
	public static final String INTENT_ACTION_VIEW_CHANGE = "Intent_Action_View_Change";

	public static final int FAIL = 1000;

	public class IntentValue {
		public static final int VIEW_CHAGE_AB = 11;
		public static final int VIEW_CHAGE_WORKUNIT = 12;
		public static final int VIEW_CHAGE_DY = 13;
		public static final int VIEW_CHAGE_NICKNAME = 14;

	}

	/**
	 * 
	 * @ClassName: IntentKey
	 * @Description: Intent传递数据的KEY
	 * @author A18ccms a18ccms_gmail_com
	 * @date 2015年10月23日 上午10:22:15
	 * 
	 */
	public class IntentKey {
		public static final String iscuid = "iscuid";
		public static final String friends = "friends";
		public static final String type = "type";
		public static final String msgtype = "msgtype";
		public static final String msgstate = "msgstate";
		public static final String msgUniqueId = "msgUniqueId";
		public static final String msgAction = "msgAction";
		public static final String msg = "msg";
		public static final String NickName = "NickName";
		public static final String AnswerDate = "AnswerDate";
		public static final String AnswerTest = "AnswerTest";
		public static final String filename = "filename";
		public static final String abInfo = "abInfo";
		public static final String ceid = "ceid";
		public static final String quid = "quid";
		public static final String Question = "Question";
		public static final String JudgeTitle = "JudgeTitle";
		public static final String Judge = "Judge";
		public static final String cusID = "cusID";
		public static final String MsgBody = "MsgBody";
		public static final String MsgTime = "MsgTime";
		public static final String MsgCount = "MsgCount";
		public static final String MsgAvatar = "MsgAvatar";
		public static final String qatype = "qatype";
		public static final String qatypename = "qatypename";
		public static final String qaname = "qaname";
		public static final String qacontent = "qacontent";
		public static final String qainfo = "qainfo";
		public static final String qcid = "qcid";
		public static final String user = "user";
		public static final String action = "action";
		public static final String dy = "dy";
		public static final String view_change = "view_change";
		public static final String view_change_msg = "view_change_msg";
		public static final String joinType = "joinType";
		public static final String thisQuestion = "thisQuestion";
		public static final String cuid = "cuid";
		public static final String QcID = "QcID";
	}

	/**
	 * 
	 * @ClassName: SharedKey
	 * @Description: Shared存储方式的key
	 * @author A18ccms a18ccms_gmail_com
	 * @date 2015年10月23日 上午10:22:15
	 * 
	 */

	public class SharedKey {
		public static final String isFrist = "isFrist";
		public static final String username = "username";
		public static final String password = "password";
		public static final String isShowWorkUnit = "isShowWorkUnit";
		public static final String HDFileImg = "HDFileImg";
		public static final String myHDFileImg = "MyHDFileImg";

		/**
		 * 设置界面的缓存key
		 */
		public static final String cb_news = "cb_news";
		public static final String cb_shake = "cb_shake";
		public static final String cb_clear = "cb_clear";
		public static final String cb_voice = "cb_voice";

		public static final String VERSION_TIME = "versiontime";

	}

	/**
	 * 
	 * @ClassName: HttpKey
	 * @Description: http中传人的参数的key和传出的json的key
	 * @author A18ccms a18ccms_gmail_com
	 * @date 2015年10月23日 下午2:19:04
	 * 
	 */
	public class HttpKey {
		// login add by pzj
		public static final String STATE = "state";
		public static final String DATA = "data";
		public static final String CODE = "code";
		public static final String MSG = "msg";

		// login end

		public static final String ResultCode = "ResultCode";
		public static final String IfBasicSave = "IfBasicSave";
		public static final String personId = "personId";
		public static final String Avatar = "Avatar";
		public static final String PersonalBasic = "PersonalBasic";
		public static final String Gender = "Gender";
		public static final String High = "High";
		public static final String Edu = "Edu";
		public static final String province = "province";
		public static final String city = "city";
		public static final String region = "region";
		public static final String plate = "plate";
		public static final String Married = "Married";
		public static final String Salary = "Salary";
		public static final String Local = "Local";

		public static final String CusIDs = "CusIDs";
		public static final String CusID = "CusID";
		public static final String CookCode = "CookCode";
		public static final String DynamicId = "DynamicId";
		public static final String commentId = "commentId";
		public static final String PageIndex = "PageIndex";
		public static final String PageSize = "PageSize";
		public static final String toId = "toId";
		public static final String message = "message";

		public static final String login = "login";
		public static final String Opinion = "Opinion";
		public static final String IsLike = "IsLike";
		public static final String LikeType = "LikeType";
		public static final String UnitName = "UnitName";
		public static final String UID = "uid";
		public static final String JobNum = "JobNum";
		public static final String FriendsList = "FriendsList";
		public static final String FriendCuID = "FriendCuID";
		public static final String HDFileImg = "HDFileImg";
		public static final String HDFileAddress = "HDFileAddress";
		public static final String AnswerDate = "AnswerDate";
		public static final String IsAttention = "IsAttention";
		public static final String CcID = "CcID";
		public static final String elCuID = "elCuID";
		public static final String elType = "elType";
		public static final String IsDisplay = "IsDisplay";
		public static final String SiID = "SiID";
		public static final String ChatRecord = "ChatRecord";
		public static final String JudgeTitle = "JudgeTitle";
		public static final String EventsDetails = "EventsDetails";
		public static final String IslikeNo = "IslikeNo";
		public static final String IslikeCount = "IslikeCount";
		public static final String Educationa = "Edu";
		public static final String EventsPersonal = "EventsPersonal";
		public static final String EventsMessage = "EventsMessage";
		public static final String NewPassword = "NewPassword";
		public static final String Tolive = "Tolive";
		public static final String ToAge = "ToAge";
		public static final String JoinID = "JoinID";
		public static final String Message = "Message";
		public static final String Publisher = "Publisher";
		public static final String CeCategory = "CeCategory";
		public static final String AnswerTest = "AnswerTest";
		public static final String Judge = "Judge";
		public static final String CuAge = "Age";
		public static final String GoodNum = "GoodNum";
		public static final String filename = "filename";
		public static final String Replyno = "Replyno";
		public static final String ViewCount = "ViewCount";
		public static final String Reply = "Reply";
		public static final String CityEvents = "CityEvents";
		public static final String Partor = "Partor";
		public static final String CeLocation = "CeLocation";
		public static final String StartDate = "StartDate";
		public static final String EndDate = "EndDate";
		public static final String CeContent = "CeContent";
		public static final String RelpyContent = "RelpyContent";
		public static final String CeID = "CeID";
		public static final String Category = "Category";
		public static final String ActivityDate = "ActivityDate";
		public static final String Title = "Title";

		public static final String personnel = "personnel";
		public static final String evaluation = "evaluation";
		public static final String goodcount = "goodcount";
		public static final String QuID = "QuID";
		public static final String HaAnswer = "HaAnswer";
		public static final String anSelect = "anSelect";
		public static final String Question = "Question";
		public static final String ValidateCode = "ValidateCode";
		public static final String PhoneType = "PhoneType";
		public static final String ClassName = "ClassName";
		public static final String Classify = "Classify";
		public static final String LikeCuID = "LikeCuID";
		public static final String FileAddress = "FileAddress";
		public static final String recommended = "recommended";
		public static final String criteria = "criteria";
		public static final String AcType = "AcType";
		public static final String FileImg = "FileImg";
		public static final String liketype = "liketype";
		public static final String CuMobile = "CuMobile";
		public static final String QuType = "QuType";
		public static final String QcID = "QcID";
		public static final String answer = "answer";

		public static final String DyContent = "DyContent";
		public static final String dynamic = "dynamic";
		public static final String customer = "customer";

		public static final String CuEmail = "CuEmail";
		public static final String CuName = "CuName";
		public static final String Cuqq = "Cuqq";
		public static final String natural = "natural";
		public static final String community = "community";
		public static final String result = "result";
		public static final String status = "status";
		public static final String returns = "returns";
		public static final String Paper = "Paper";
		public static final String phonecode = "phonecode";
		public static final String username = "username";
		public static final String password = "Password";
		public static final String Longitude = "Longitude";
		public static final String Latitude = "Latitude";
		public static final String mobile = "Mobile";
		public static final String ToType = "ToType";
		public static final String weight = "weight";
		public static final String height = "height";
		public static final String userId = "userId";
		public static final String user = "user";
		public static final String loginName = "loginName";

		public static final String hometown = "hometown";
		public static final String Hometown = "Hometown";
		public static final String nation = "nation";
		public static final String Nation = "Nation";
		public static final String faith = "faith";
		public static final String educationa = "educationa";
		public static final String Graduated = "Graduated";
		public static final String Scholarship = "Scholarship";
		public static final String Provinces = "Provinces";
		public static final String Area = "Area";
		public static final String Address = "Address";
		public static final String WorkUnit = "WorkUnit";
		public static final String Post = "Post";
		public static final String AnnualIncome = "Salary";
		public static final String Marital = "Marital";
		public static final String Housing = "Housing";
		public static final String Car = "Car";
		public static final String Content = "Content";
		public static final String FileID = "FileID";
		public static final String quType = "quType";
		public static final String quTitle = "quTitle";
		public static final String answers = "answers";

		public static final String fileImg = "FileImg";
		public static final String Age = "Age";
		public static final String infor = "infor";
		public static final String Create_Time = "Create_Time";
		public static final String postStatus = "postStatus";
		public static final String DcID = "DcID";
		public static final String vreid = "vreid";
		public static final String ReId = "EeId";
		public static final String CiId = "CiId";
		public static final String reply = "reply";
		public static final String interest = "interest";
		public static final String partor = "partor";
		public static final String joinuserId = "joinuserId";
		public static final String image = "image";
		public static final String userCount = "userCount";
		public static final String tableType = "tableType";
		public static final String CnID = "CnID";
		public static final String CuSex = "CuSex";
		public static final String BirthDate = "BirthDate";
		public static final String CnHeight = "High";
		public static final String CnWeight = "CnWeight";
		public static final String CnZodiac = "CnZodiac";
		public static final String CnBlood = "CnBlood";
		public static final String Constellation = "Constellation";
		public static final String City = "City";
		public static final String ReplyContent = "ReplyContent";

		// 活动
		public static final String pageIndex = "pageIndex";
		public static final String pageSize = "pageSize";
		public static final String name = "Name";
		public static final String dateType = "DateType";
		public static final String dateFrom = "DateFrom";
		public static final String dateTo = "DateTo";
		public static final String address = "Address";
		public static final String maleCount = "MaleCount";
		public static final String femaleCount = "FemaleCount";
		public static final String avatar = "Avatar";
		public static final String activityID = "ActivityID";
		public static final String category = "Category";
		public static final String appliedMaleCount = "AppliedMaleCount";
		public static final String appliedFemaleCount = "AppliedFemaleCount";
		public static final String publishNickName = "PublishNickName";
		public static final String fee = "Fee";
		public static final String feeType = "FeeType";
		public static final String mark = "Mark";
		public static final String publishDate = "PublishDate";
		public static final String applyEndDate = "ApplyEndDate";
		public static final String applies = "Applies";
		public static final String userID = "UserID";
		public static final String isAppied = "isAppied";
		public static final String validateType = "validateType";
		public static final String mobile1 = "mobile";

		public static final String Phone = "Phone";
		public static final String orgName = "CompanyName";
		public static final String registerName = "UserName";
		public static final String shareUrl = "ShareUrl";
		public static final String URL = "url";
		public static final String status1 = "Status";

		// 发现---婚恋指导
		public static final String id = "id";
		public static final String news_title = "news_title";
		public static final String news_content = "news_content";
		public static final String news_typeId = "news_typeId";
		public static final String news_sequenceId = "news_sequenceId";
		public static final String news_hits = "news_hits";
		public static final String news_addtime = "news_addtime";
		public static final String news_source = "news_source";
		public static final String news_picpath = "news_picpath";
		public static final String news_keywords = "news_keywords";
		public static final boolean news_isout = true;
		public static final boolean news_istop = true;
		public static final boolean news_isred = true;
		public static final String news_addUser = "news_addUser";
		public static final String unitId = "unitId";
		public static final String seoAbout = "seoAbout";
		public static final boolean news_ishot = true;
		public static final String details_name = "details_name";
		public static final String Name = "Name";
		public static final String cusId = "cusId";
		public static final String answerJson = "answerJson";
		public static final String QuTest = "QuTest";

		// 交友---附近的人
		public static final String CusId = "CusId";
		public static final String Weight = "Weight";
		public static final String IsIdentity = "IsIdentity";
		public static final String IsUnit = "IsUnit";
		public static final String Focused = "Focused";
		public static final String Level = "Level";
		public static final String accountName = "accountName";
		public static final String token = "token";
		public static final String platformId = "platformId";

		// 认证
		public static final String UnitID = "UnitID";
		public static final String Brief = "Brief";
		public static final String Summary = "Summary";
		public static final String AuthenMaleCount = "AuthenMaleCount";
		public static final String AuthenFemaleCount = "AuthenFemaleCount";
		public static final String Logo = "Logo";
	}

	/**
	 * 
	 * @ClassName: Type
	 * @Description: 接口参数意义
	 * @author lisy
	 * @date 2014年6月9日 上午11:04:36
	 * 
	 */
	public class Type {

		public static final int success_code = 1;
		public static final int fail_code = 2;
		/**
		 * 发送手机验证码
		 */
		public static final int sendPhone = 1;
		/**
		 * 验证码验证
		 */
		public static final int phoneCode = 2;
		/**
		 * 注册
		 */
		public static final int register = 3;

		/**
		 * 登录
		 */
		public static final int userLogin = 4;

		/**
		 * 个人信息
		 */
		public static final int personal = 5;
		/**
		 * 修改个人信息
		 */
		public static final int updatepersonal = 6;
		/**
		 * 上传图片
		 */
		public static final int updatepic = 7;
		/**
		 * 动态列表
		 */
		public static final int listdynamic = 8;
		/**
		 * 发表动态列表
		 */
		public static final int senddynamic = 9;

		/**
		 * 评论动态 评论他人信息
		 */
		public static final int repliesInfor = 10;
		/**
		 * 删除动态信息
		 */
		public static final int deletedynamic = 11;
		/**
		 * 点赞
		 */
		public static final int gooddynamic = 12;

		/**
		 * type=13 问题类别
		 * 
		 * 添加参数QuType（0:习惯、1:访谈、2:性格）
		 * 
		 * 
		 */
		public static final int allQuType = 13;
		/**
		 * type=14 类别下的所有题目 参数 QcID(问题类别ID)
		 */
		public static final int allQu = 14;
		/**
		 * type=15 提交答案 参数CuID, 参数CuID QcID(问题类别ID) answer
		 * (json：{“问题ID”：“答案序号”})
		 */
		public static final int answer = 15;

		/**
		 * 推荐人员列表
		 * 
		 * 搭讪（有眼缘，习惯好，个性配） 参数 Paper（第几页）， CuID=1
		 * AcType（0：有眼缘（及推荐人员列表），1：习惯好，2：个性配）
		 */
		public static final int rec_people = 17;
		/**
		 * 关注人员动态列表
		 */
		public static final int rec_dy = 18;

		/**
		 * 聊天记录查询
		 */
		public static final int news = 19;

		/**
		 * 添加到喜欢 CuID,LikeCuID(喜欢人ID)
		 */
		public static final int addlove = 20;
		/**
		 * 同城详情
		 */
		public static final int abInfo = 22;
		/**
		 * 添加参加同城活动的人
		 */
		public static final int addAbPeople = 23;

		/**
		 * 同城活动 人员列表
		 */
		public static final int abPeople = 24;

		/**
		 * 添加同城活动
		 */
		public static final int addAb = 25;
		/**
		 * 删除同城活动
		 */
		public static final int deleteAb = 26;

		/**
		 * 修改密码
		 */
		public static final int changepassword = 27;

		/**
		 * 个性配 习惯配
		 */
		public static final int QaPeople = 28;

		/**
		 * 定位
		 */
		public static final int location = 209;

		/**
		 * 添加到关注动态
		 */
		public static final int AddDyRec = 29;
		/**
		 * 隐藏和显示
		 */
		public static final int QaShow = 30;
		/**
		 * 隐藏和显示
		 */
		public static final int InterShow = 31;

		/**
		 * 验证工作单位
		 */
		public static final int JobTest = 32;
		/**
		 * 朋友列表 type=33 参数 FriendCuID （如2#3 ID之间#分割） CuID（用户ID）
		 */
		public static final int FriendList = 33;
		/**
		 * 获取工作单位
		 */
		public static final int WorkUnitList = 34;
		/**
		 * 意见反馈 参数 CuID Opinion（内容）
		 */
		public static final int feedback = 35;
		/**
		 * 大搜索
		 */
		public static final int bitSeek = 38;
		/**
		 * 清理文件
		 */
		public static final int Clear_File = 201;
		/**
		 * 更新
		 */
		public static final int update = 202;
		/**
		 * 个人信息保存
		 */
		public static final int personalbasicsave = 40;
		/**
		 * 个人全部信息
		 */

		public static final int personall = 41;
		/**
		 * 个人信息，婚姻期望等信息
		 */
		public static final int personshow = 42;
		/**
		 * 择偶条件获得
		 */
		public static final int friendconditionget = 43;
		/**
		 * 择偶条件保存
		 */
		public static final int friendconditonsave = 44;
		/**
		 * 获取婚姻期望
		 */
		public static final int marryHopeGet = 45;
		/**
		 * 保存婚姻期望
		 */
		public static final int marryHopeSave = 46;
		/**
		 * 获取用户最新动态
		 */
		public static final int dynamicNew = 47;
		/**
		 * 获取关注我的
		 */
		public static final int getAttentionMe = 48;
		/**
		 * 获取我关注的
		 */
		public static final int getMyAttention = 49;
		/**
		 * 搜索好友
		 */
		public static final int search = 50;
		public static final int DynamicPersonal = 50;
		/**
		 * 回复动态
		 */
		public static final int ReplyDynamic = 51;
		/**
		 * 点赞
		 */
		public static final int Like = 52;
		/**
		 * 注册条款
		 */

		public static final int UserPolicy = 53;
		/**
		 * 重置密码
		 */
		public static final int ResetPassword = 54;
		/**
		 * 更新头像
		 */

		public static final int UpdateAvatar = 54;

		/**
		 * 内心独白获取
		 */
		public static final int IntroduceGet = 55;

		/**
		 * 内心独白保存
		 */
		public static final int IntroduceSave = 56;

		/**
		 * 活动
		 */
		public static final int offcialactive_success = 61;
		public static final int offcialactive_fail = 62;

		/**
		 * 活动详情
		 */
		public static final int active_detail_success = 63;
		public static final int active_detail_fail = 64;

		/**
		 * 已报名列表
		 */
		public static final int active_apply_success = 65;
		public static final int active_apply_fail = 66;
		/**
		 * 发布个人活动
		 */
		public static final int publish = 67;

		/**
		 * 重复报名提示
		 */
		public static final int active_detail_re = 68;

		/**
		 * 婚恋
		 */
		public static final int marriage_success = 69;
		public static final int marriage_fail = 70;
		/**
		 * 婚恋里的文章
		 */
		public static final int marriageGuidance_success = 71;
		public static final int marriageGuidance_fail = 72;

		/**
		 * 取消赞
		 */
		public static final int Unlike = 73;
		/**
		 * 订单支付
		 */
		public static final int ActivityApplyOrder = 74;
		/**
		 * 判断支付是否成功
		 */
		public static final int checkAppyPaid = 75;
		/**
		 * 
		 */
		public static final int GetUnitAuthenList = 76;
		/**
		 * 检查昵称是否唯一
		 */
		public static final int CheckNickName = 77;
		/**
		 * 推荐广告
		 */
		public static final int RecommendedTopBanner = 77;
		/**
		 * 启动页广告
		 */
		public static final int StartTheme = 78;

		/*
		 * 心理测试
		 */
		public static final int habit_test = 98;
		public static final int character_test = 99;
		public static final int inter_test = 101;

		/**
		 * 删除-我关注的
		 */
		public static final int active_friendDelete_success = 110;
		public static final int active_friendDelete_fail = 111;

		public static final int delete_comment_success = 102;
		public static final int delete_comment_failed = 103;
		public static final int comment_success = 104;

		/*
		 * 活动详情报名活动短信验证
		 */
		public static final int activite_registered_success = 112;
		public static final int activite_registered_failed = 113;
		public static final int activite_erificationcode_success = 114;
		public static final int activite_erificationcode_failed = 115;
		/*
		 * 活动详情的取消报名
		 */
		public static final int cancelActivity_success = 116;
		public static final int cancelActivity_fail = 117;

		public static final int checkversion_success = 118;
		public static final int checkversion_fail = 119;

		/*
		 * 单位认证
		 */
		public static final int unitlist_success = 120;
		public static final int select_company = 121;
		public static final int onrefsh_company_list = 122;
		public static final int unit_authen_success = 123;
		public static final int bind_tel = 124;
		public static final int bind_tel_fail = 125;
		public static final int bind_third = 126;
		public static final int bind_third_fail = 127;
		public static final int relieve_third_suc = 128;

		/**
		 * 我的 personshow
		 */
		public static final int myFragment = 129;

		public static final int cancel_unit_authen_success = 130;
		public static final int getunitmobile_success = 131;
		public static final int report_dynamic_success = 132;
		
	}
}
