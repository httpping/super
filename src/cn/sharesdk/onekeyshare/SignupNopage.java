package cn.sharesdk.onekeyshare;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;

import com.vp.loveu.util.Prints;

public class SignupNopage implements Callback {
	private Handler mHandler;
	private Context mContext;

	/** 修改用户信息dialog,如用户信息，备注信息 */
	private enum ChangeUserType {
		USER_NAME, USER_NOTE
	};

	/** 加载对话框 */
	private static final int SHOW_PROGRESS_DIALOG = 1;

	private OnLoginListener signupListener;
	private static Platform platform;
	public static String userId;
	public static String accountName;
	public static String platformId;

	private UserInfo userInfo = new UserInfo();

	public SignupNopage(Context context, Handler handler) {
		this.mHandler = handler;
		this.mContext = context;
	}

	/** 设置授权回调，用于判断是否进入注册 */
	public void setOnLoginListener(OnLoginListener l) {
		this.signupListener = l;
	}

	public void setPlatform(String platName) {
		platform = ShareSDK.getPlatform(platName);
	}

	public static void clearPlatfor() {
		if (platform != null) {
			platform.getDb().removeAccount();
		}
	}

	/** 初始化数据 */
	public void initData() {
		if (platform != null) {
			signupListener.onSignUp(userInfo);
			accountName = platform.getDb().getUserName();
			String platformName = platform.getName();
			platformId = getPId(platformName);
			userId = platform.getDb().getUserId();
			String token = platform.getDb().getToken();
			Prints.i("zeus", "--@@==" + accountName + "**" + platformName + "**"
					+ userId + "**" + token);
			
//			new LoginByOtherPlatformThread(mHandler, mContext, accountName,
//					userId, platformId).start();
		}
	}

	public static String getPId(String platformName) {
		String pId = "";
		if (platformName.equals("QQ")) {
			pId = "0";
		}
		if (platformName.equals("Wechat")) {
			pId = "1";
		}
		if (platformName.equals("SinaWeibo")) {
			pId = "2";
		}
		return pId;
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case SHOW_PROGRESS_DIALOG:

			break;
		default:
			break;
		}
		return false;
	}

}
