package com.vp.loveu.bean;

import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.custom.ShareDialogFragment;
import cn.sharesdk.onekeyshare.custom.ShareModel;
import cn.sharesdk.sina.weibo.SinaWeibo;

import java.util.HashMap;

import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.base.VpApplication;
import com.vp.loveu.channel.ui.ChannelDetailActivity;
import com.vp.loveu.channel.ui.ChannelTopicListActivity;
import com.vp.loveu.channel.ui.VideoDetailActivity;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.comm.WebViewActivity;
import com.vp.loveu.discover.ui.CourseDetailActivity;
import com.vp.loveu.discover.ui.PuaCourseListActivity;
import com.vp.loveu.index.activity.ActiveWebActivity;
import com.vp.loveu.index.activity.ArticleActivity;
import com.vp.loveu.index.activity.CityActiveActivity;
import com.vp.loveu.index.activity.CityActiveListActivity;
import com.vp.loveu.index.activity.FellHelpActivity;
import com.vp.loveu.index.activity.MoreContentActivity;
import com.vp.loveu.index.activity.NearByActivity;
import com.vp.loveu.index.holder.IndexNavigationHolder;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.login.ui.WelcomeActivity;
import com.vp.loveu.my.activity.IntergralActivity;
import com.vp.loveu.my.activity.MyCenterActivity;
import com.vp.loveu.my.activity.UserIndexActivity;
import com.vp.loveu.my.bean.NewIntergralBean.NewIntergralDataBean;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.util.ShareCompleteUtils;
import com.vp.loveu.util.SharedPreferencesHelper;
import com.vp.loveu.util.UIUtils;
import com.vp.loveu.util.VPLog;
import com.vp.loveu.wxapi.WXEntryActivity;

/**
 * 内部行为 跳转
 * 
 * @author tanping 2015-12-9
 */
public class InwardAction implements PlatformActionListener {

	public ActionType mActionType;// 行为type
	public Uri mUri;
	private String tag = "InwardAction";

	/**
	 * 行为分类
	 * 
	 * @author tanping 2015-12-9 详情 action 看客户端文档
	 */

	public static enum ActionType {
		nearby(1), activity(2), emotion_help(3), video(4), voice(5), person_index(6), news_list(6), online_course(7), chat_topic(
				8), pua_course(9), pua_course_detail(10), http_web_url(11), news_detail(12), launch(13), activity_sign_in(14), share_app(
						15), favour_app(16),app_sign_in(17);

		final int value;

		ActionType(int v) {
			this.value = v;
		}
	}

	/**
	 * 解析action
	 * 
	 * @param spec
	 * @return
	 */
	public static InwardAction parseAction(String spec) {
		InwardAction inwardAction = new InwardAction();
		VPLog.d("inward", spec + "");
		try {
			if (spec.startsWith("http://") || spec.startsWith("https://")) {
				inwardAction.mActionType = ActionType.http_web_url;
				inwardAction.mUri = Uri.parse(spec);
				return inwardAction;
			}
			inwardAction.mUri = Uri.parse(spec);
			String action = inwardAction.mUri.getAuthority();
			inwardAction.mActionType = ActionType.valueOf(action);
			if (inwardAction.mActionType == null) {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return inwardAction;

	}

	/**
	 * 通过key取值
	 * 
	 * @param key
	 * @return
	 */
	public String getValueForKey(String key) {
		if (mUri != null || TextUtils.isEmpty(key)) {
			return mUri.getQueryParameter(key);
		}
		return null;
	}

	/**
	 * start activity的功能根据类型 public 公用
	 * 
	 * @return
	 */
	public boolean toStartActivity(Context context) {
		if (context == null) {
			context = UIUtils.getContext();
		}

		try {
			if (mActionType == ActionType.activity) {// 活动
				goActivity(context);
			} else if (mActionType == ActionType.nearby) {
				goNearActivity(context);
			} else if (mActionType == ActionType.emotion_help) {
				goEmotionHelp(context);
			} else if (mActionType == ActionType.person_index) {
				goPersonIndex(context);
			} else if (mActionType == ActionType.news_list) {
				goMoreContentList(context);
			} else if (mActionType == ActionType.video) {
				goVideoActivity(context);
			} else if (mActionType == ActionType.voice) {
				goVoiceActivity(context);
			} else if (mActionType == ActionType.online_course) {
				goOnlineCourseActivity(context);
			} else if (mActionType == ActionType.chat_topic) {
				goChatTopicActivity(context);
			} else if (mActionType == ActionType.pua_course) {
				goPuaCourseActivity(context);
			} else if (mActionType == ActionType.pua_course_detail) {
				goPuaCourseDetailActivity(context);
			} else if (mActionType == ActionType.http_web_url) {
				goWebViewActivity(context);
			} else if (mActionType == ActionType.news_detail) {
				goNewsDetail(context);
			} else if (mActionType == ActionType.launch) {
				goLaunch(context);
			} else if (mActionType == ActionType.share_app) {
				goSharApp(context);
			} else if (mActionType == ActionType.favour_app) {
				goFavour();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	/**
	 * 给app好评 void TODO 安卓暂时不做
	 */
	private void goFavour() {

	}

	/**
	 * 分享app
	 * 
	 * @param context
	 *            void
	 */
	ShareDialogFragment dialog;
	SharedPreferencesHelper sp;

	private void goSharApp(Context context) {
		if (dialog == null) {
			dialog = new ShareDialogFragment();
			dialog.setShowCopy(false);
			dialog.setPlatformActionListener(this);
			sp = SharedPreferencesHelper.getInstance(context);
		}
		ShareModel model = new ShareModel();
		model.setText(sp.getStringValue("app_share_describe")); // 从配置文件里读取配置的描述语以及分享的图标
		model.setUrl(VpConstants.APP_SHARE);
		model.setImageUrl(sp.getStringValue("app_share_icon"));
		model.setId(0);
		model.setType(999);
		model.setObj(bean);
		model.setTag(IntergralActivity.TAG);
		if (context instanceof VpActivity) {
			VpActivity ac = (IntergralActivity) context;
			VpApplication.getInstance().setShareModel(model);
			dialog.show(ac.getSupportFragmentManager(), "share_app", model);
		} 
	}

	@Override
	public void onCancel(Platform arg0, int arg1) {
		Toast.makeText(UIUtils.getContext(), "分享取消", 0).show();
	}

	@Override
	public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
		// 微信分享成功回调需要在WXEntryActivity 处理
		if (arg0.getName().equals(SinaWeibo.NAME)) {
			Toast.makeText(UIUtils.getContext(), "分享成功", Toast.LENGTH_SHORT).show();
			LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
			ShareModel tempModel = VpApplication.getInstance().getShareModel();
			if (tempModel != null && loginInfo != null) {
				if (IntergralActivity.TAG.equals(tempModel.getTag()) && bean.is_retrieved == 0) {// ==0 新手任务  ==1 已完成新手任务 不需要发送广播
					//发送广播通知
					Intent intent = new Intent(IntergralActivity.ACTION);
					intent.putExtra("obj", tempModel.getObj());
					UIUtils.getContext().sendBroadcast(intent);
				}
				
				ShareCompleteUtils utils = new ShareCompleteUtils(UIUtils.getContext());
				utils.reportData(loginInfo.getUid(), tempModel.getId(), tempModel.getType());
				VpApplication.getInstance().setShareModel(null);
			}
		}
	}

	@Override
	public void onError(Platform arg0, int arg1, Throwable arg2) {
		Toast.makeText(UIUtils.getContext(), "分享错误", 0).show();
	}

	private void goLaunch(Context context) {
		Intent intent = new Intent(context, WelcomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	private void goWebViewActivity(Context context) {
		VPLog.d(tag, "web vilew：" + mUri.toString());
		Intent intent = new Intent(context, WebViewActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("url", mUri.toString());
		context.startActivity(intent);
	}

	/**
	 * 长文列表
	 * 
	 * @param context
	 */
	private void goMoreContentList(Context context) {
		String cid = getValueForKey("id");

		int cidi = 0;
		if (!TextUtils.isEmpty(cid)) {
			try {
				cidi = Integer.parseInt(cid);
			} catch (Exception e) {
			}
		}
		Intent intent = new Intent(context, MoreContentActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(IndexNavigationHolder.KEY_FLAG, cidi);
		intent.putExtra("title", getValueForKey("title"));
		context.startActivity(intent);
	}

	/**
	 * 长文详情
	 * 
	 * @param context
	 */
	private void goNewsDetail(Context context) {
		String id = getValueForKey("id");
		if (!TextUtils.isEmpty(id)) {
			Intent intent = new Intent(context, ArticleActivity.class);
			intent.putExtra(ArticleActivity.KEY_FLAG, Integer.valueOf(id));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}

	private void goPersonIndex(Context context) {
		String id = getValueForKey("id");
		if (!TextUtils.isEmpty(id)) {
			Intent intent = new Intent(context, UserIndexActivity.class);
			intent.putExtra(UserIndexActivity.KEY_UID, Integer.valueOf(id));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} else {
			Intent intent = new Intent(context, MyCenterActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}

	private void goEmotionHelp(Context context) {
		Intent intent = new Intent(context, FellHelpActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	private void goNearActivity(Context context) {
		Intent intent = new Intent(context, NearByActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public void goActivity(Context context) {
		String id = getValueForKey("id");
		if (TextUtils.isEmpty(id)) {
			// Intent intent = new Intent(context, CityActiveActivity.class);
			Intent intent = new Intent(context, CityActiveListActivity.class);
			context.startActivity(intent);
		} else {
			Intent intent = new Intent(context, ActiveWebActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(ActiveWebActivity.KEY_WEB, Integer.valueOf(id));
			context.startActivity(intent);
		}
	}

	private void goVideoActivity(Context context) {
		String vid = getValueForKey("v_id");
		String chatper = getValueForKey("chatper");
		if (!TextUtils.isEmpty(vid)) {
			if (chatper == null || chatper.equals(""))
				chatper = "0";
			Intent intent = new Intent(context, VideoDetailActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(VideoDetailActivity.VIDEO_ID, Integer.valueOf(vid));
			intent.putExtra(VideoDetailActivity.VIDEO_V_ID, Integer.valueOf(chatper));
			intent.putExtra("obj", bean);
			context.startActivity(intent);
		}
	}

	private void goVoiceActivity(Context context) {
		String id = getValueForKey("id");
		if (!TextUtils.isEmpty(id)) {
			Intent intent = new Intent(context, ChannelDetailActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(ChannelDetailActivity.RADIOID, Integer.valueOf(id));
			intent.putExtra(ChannelDetailActivity.IS_OPEN_FROM_OTHER, true);
			intent.putExtra("obj", bean);
			context.startActivity(intent);
		}
	}

	//在线课程
	private void goOnlineCourseActivity(Context context) {
		String id = getValueForKey("id");
		if (!TextUtils.isEmpty(id)) {
			Intent intent = new Intent(context, CourseDetailActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(CourseDetailActivity.COURSE_ID, Integer.valueOf(id));
			if (!TextUtils.isEmpty(promoCode)) {
				intent.putExtra(CourseDetailActivity.ISPROMO_CODE, promoCode);
			}
			context.startActivity(intent);
		}
	}

	private void goChatTopicActivity(Context context) {
		String id = getValueForKey("id");
		String title = getValueForKey("title");
		if (!TextUtils.isEmpty(id)) {
			Intent intent = new Intent(context, ChannelTopicListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(ChannelTopicListActivity.TOPICID, Integer.valueOf(id));
			intent.putExtra(ChannelTopicListActivity.TOPICNAME, title);
			context.startActivity(intent);
		}
	}

	private void goPuaCourseActivity(Context context) {
		String id = getValueForKey("id");
		String title = getValueForKey("title");
		if (!TextUtils.isEmpty(id)) {
			Intent intent = new Intent(context, PuaCourseListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(PuaCourseListActivity.ID, Integer.valueOf(id));
			intent.putExtra(PuaCourseListActivity.NAME, title);
			context.startActivity(intent);
		}
	}

	private void goPuaCourseDetailActivity(Context context) {
		String id = getValueForKey("id");
		if (!TextUtils.isEmpty(id)) {
			Intent intent = new Intent(context, ArticleActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(ArticleActivity.KEY_FLAG, Integer.valueOf(id));
			intent.putExtra(ArticleActivity.KEY_FLAG_TYPE, ArticleActivity.TYPE_PUA);
			context.startActivity(intent);
		}
	}

	@Override
	public String toString() {
		return "InwardAction [mActionType=" + mActionType + ", mUri=" + mUri + "]";
	}

	
	private String promoCode;
	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

	NewIntergralDataBean bean;
	public void setDataBean(NewIntergralDataBean bean) {
		this.bean = bean;
	}
}
