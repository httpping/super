package com.vp.loveu.wxapi;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import cn.sharesdk.onekeyshare.custom.ShareModel;
import cn.sharesdk.wechat.utils.WechatHandlerActivity;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.base.VpApplication;
import com.vp.loveu.login.bean.WechatUserBean;
import com.vp.loveu.login.utils.ThirdLoginUtils;
import com.vp.loveu.my.activity.IntergralActivity;
import com.vp.loveu.my.bean.NewIntergralBean.NewIntergralDataBean;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.ShareCompleteUtils;
import com.vp.loveu.util.UIUtils;
import com.vp.loveu.util.VPLog;

import cz.msebera.android.httpclient.Header;

public class WXEntryActivity extends VpActivity implements IWXAPIEventHandler {
	private final static String TAG = "WXEntryActivity";
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	private static final int MSG_LOGIN_WX = 6;
	private int SUCCESS = 0;
	private int ERROR = 50;
	private IWXAPI api;
	private Activity mActivity;
	private Context mContext;
	private String strAccess_token = null;
	private String strOpenid = null;
	private String userinfo = null;
	private String strUserinfo = null;
	private static final String WECHAT_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
	private static final String WECHAT_REFRESH_URL = "https://api.weixin.qq.com/sns/oauth2/refresh_token";
	private static final String WECHAT_CHECK_VALID = "https://api.weixin.qq.com/sns/auth";
	private static final String WECHAT_USER_INFO = "https://api.weixin.qq.com/sns/userinfo";
	private String accessToken;
	private String openId;
	private String refreshToken;

	boolean isFinish = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mClient = new VpHttpClient(mContext);
		api = WXAPIFactory.createWXAPI(this, WxConsants.APP_ID, true);
		api.handleIntent(getIntent(), this);
		VPLog.d("--wx2app--", getIntent().toString());
	}

	@Override
	public void onReq(BaseReq resp) {
		Log.d(TAG, "--resp--" + resp.openId);
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "--resp--" + resp.errCode);
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:

			break;
		case BaseResp.ErrCode.ERR_SENT_FAILED:
		case BaseResp.ErrCode.ERR_UNSUPPORT:
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			finish();
			Toast.makeText(this, "操作失败", 0).show();
			break;
		default:
			break;
		}

		if (resp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {// 分享
			String result = "";
			switch (resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				result = "分享成功";
				ShareModel tempModel = VpApplication.getInstance().getShareModel();
				if (tempModel != null) {
					if (IntergralActivity.TAG.equals(tempModel.getTag()) && tempModel.getObj() != null) {// ==0
						NewIntergralDataBean obj = (NewIntergralDataBean) tempModel.getObj(); // 新手任务
						// 发送广播通知
						Intent intent = new Intent(IntergralActivity.ACTION);
						intent.putExtra("obj", obj);
						UIUtils.getContext().sendBroadcast(intent);
					}
					ShareCompleteUtils utils = new ShareCompleteUtils(this);
					utils.reportData(LoginStatus.getLoginInfo().getUid(), tempModel.getId(), tempModel.getType());
					VpApplication.getInstance().setShareModel(null);
				}
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				result = "取消分享";
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				result = "认证失败";
				break;
			default:
				result = "errcode_unknown";
				break;
			}
			Toast.makeText(this, result, Toast.LENGTH_SHORT).show();

			finish();
		}
		// 微信登录
		if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH)

		{
			switch (resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:// 同意授权

				SendAuth.Resp authResp = (SendAuth.Resp) resp;
				// step1 拿到code
				String code = authResp.code;

				// step2 通过code拿到accessToken
				getAccessToken(code);
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:// 同意取消授权
				Toast.makeText(this, "取消授权", Toast.LENGTH_SHORT).show();
				isFinish = true;
				VpApplication.getInstance().setWechatUserBean(null);
				finish();
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:// 拒绝授权
				Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
				finish();
				break;
			default:
				finish();
				break;
			}

		}

	}

	public void onGetMessageFromWXReq(WXMediaMessage msg) {
		Intent iLaunchMyself = getPackageManager().getLaunchIntentForPackage(getPackageName());
		startActivity(iLaunchMyself);
	}

	/**
	 * 处理微信向第三方应用发起的消息
	 * <p>
	 * 此处用来接收从微信发送过来的消息，比方说本demo在wechatpage里面分享
	 * 应用时可以不分享应用文件，而分享一段应用的自定义信息。接受方的微信 客户端会通过这个方法，将这个信息发送回接收方手机上的本demo中，当作 回调。
	 * <p>
	 * 本Demo只是将信息展示出来，但你可做点其他的事情，而不仅仅只是Toast
	 */
	public void onShowMessageFromWXReq(WXMediaMessage msg) {
		if (msg != null && msg.mediaObject != null && (msg.mediaObject instanceof WXAppExtendObject)) {
			WXAppExtendObject obj = (WXAppExtendObject) msg.mediaObject;
			Toast.makeText(this, obj.extInfo, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 获取accessToken
	 * 
	 * @param code
	 */
	private void getAccessToken(String code) {
		RequestParams params = new RequestParams();
		params.put("appid", ThirdLoginUtils.WECHAT_APP_ID);
		params.put("secret", ThirdLoginUtils.WECHAT_APP_SECRET);
		params.put("code", code);
		params.put("grant_type", "authorization_code");
		mClient.get(WECHAT_URL, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);
				try {
					JSONObject json = new JSONObject(result);
					accessToken = json.getString("access_token");
					openId = json.getString("openid");
					refreshToken = json.getString("refresh_token");
					checkTokenValid();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 检查token有效性
	 */
	private void checkTokenValid() {
		if (accessToken != null && openId != null) {
			RequestParams params = new RequestParams();
			params.put("access_token", accessToken);
			params.put("openid", openId);
			mClient.get(WECHAT_CHECK_VALID, params, new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String result = ResultParseUtil.deAesResult(responseBody);
					try {
						JSONObject json = new JSONObject(result);
						int errorCode = json.getInt("errcode");
						if (errorCode == 0) {
							// 有效
							getUserInfo();
						} else {
							// 刷新accessToken
							refreshAccessToken();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	/**
	 * 刷新accessToken
	 * 
	 * @param code
	 */
	private void refreshAccessToken() {
		RequestParams params = new RequestParams();
		params.put("appid", ThirdLoginUtils.WECHAT_APP_ID);
		params.put("grant_type", "refresh_token");
		params.put("refresh_token", refreshToken);
		mClient.get(WECHAT_REFRESH_URL, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);
				try {
					JSONObject json = new JSONObject(result);
					accessToken = json.getString("access_token");
					getUserInfo();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 获取用户信息
	 */
	private void getUserInfo() {
		RequestParams params = new RequestParams();
		params.put("access_token", accessToken);
		params.put("openid", openId);
		mClient.get(WECHAT_USER_INFO, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);
				try {
					JSONObject json = new JSONObject(result);
					if (json.has("openid")) {
						WechatUserBean bean = WechatUserBean.parseJson(json.toString());
						if (bean != null) {
							// Intent intent =new
							// Intent(WXEntryActivity.this,LoginActivity.class);
							// intent.putExtra(LoginActivity.LOGIN_WECHAT,
							// bean);
							// startActivity(intent);
							if (!isFinish) {
								VpApplication.getInstance().setWechatUserBean(bean);
							}
							finish();
						}
					} else {
						Toast.makeText(getApplicationContext(), "获取用户信息出错", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
			}
		});
	}
}