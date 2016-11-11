package com.vp.loveu.message.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.vp.loveu.R;

/**
 * @author Administrator
 * 
 */
public class DialogHelper {

    /**
     * 提示对话框显示
     * 
     * @param context
     *            : 调用的activity
     * @param errCode
     *            : 错误码
     * @param errMsg
     *            : 错误提示
     */
    public static void show(Context context, int errCode, String errMsg) {
	switch (errCode) {
	case 0:
	case 1004:
	case 1112:
	case 1113:
	    createHint(context, errMsg, "", R.string.txt_sure);
	    break;

	default:
	    createHint(context, errMsg, "", R.string.txt_sure);
	    break;
	}
    }

    /**
     * 标准对话框(标题、确定、取消)
     * 
     * @param context
     *            : 调用的activity
     * @param resIdTitle
     *            : title资源ID
     * @param resIdSure
     *            : 确认按钮资源ID
     * @param resIdCancel
     *            : 取消按钮资源ID
     * @param onResponseListener
     *            : 响应回调
     */
    public static void createStandard(Context context, int resIdTitle, int resIdSure, int resIdCancel,
	    final OnResponseListener onResponseListener) {
	final AlertDialog dialog = new AlertDialog.Builder(context).create();
	dialog.show();

	Window window = dialog.getWindow();
	int w = 3 * ScreenHelper.getScreenWidth(context) / 5;
	int h = ScreenHelper.getScreenHeight(context) / 5;
	window.setContentView(R.layout.dialog_standard);
	window.setLayout(w, h);

	TextView title = (TextView) window.findViewById(R.id.dialog_standard_title);
	TextView sure = (TextView) window.findViewById(R.id.dialog_standard_sure);
	TextView cancel = (TextView) window.findViewById(R.id.dialog_standard_cancel);
	View split = window.findViewById(R.id.dialog_standard_act_div);
	 

	sure.setOnClickListener(new OnClickListener() {

	    public void onClick(View view) {
		onResponseListener.onSure();
		dialog.dismiss();
	    }

	});
	cancel.setOnClickListener(new OnClickListener() {

	    public void onClick(View view) {
		onResponseListener.onCancel();
		dialog.dismiss();
	    }

	});
    }

    /**
     * 标准对话框(标题、确定、取消)
     * 
     * @param context
     *            : 调用的activity
     * @param titleTxt
     *            : title字符串
     * @param resIdSure
     *            : 确认按钮资源ID
     * @param resIdCancel
     *            : 取消按钮资源ID
     * @param onResponseListener
     *            : 响应回调
     */
    public static void createStandard(Context context, String titleTxt, int resIdSure, int resIdCancel,
	    final OnResponseListener onResponseListener) {
	final AlertDialog dialog = new AlertDialog.Builder(context).create();
	dialog.show();

	Window window = dialog.getWindow();
	int w = 3 * ScreenHelper.getScreenWidth(context) / 5;
	int h = ScreenHelper.getScreenHeight(context) / 5;
	window.setContentView(R.layout.dialog_standard);
	window.setLayout(w, h);

	TextView title = (TextView) window.findViewById(R.id.dialog_standard_title);
	TextView sure = (TextView) window.findViewById(R.id.dialog_standard_sure);
	TextView cancel = (TextView) window.findViewById(R.id.dialog_standard_cancel);
	title.setText(titleTxt);
	 

	sure.setOnClickListener(new OnClickListener() {

	    public void onClick(View view) {
		onResponseListener.onSure();
		dialog.dismiss();
	    }

	});
	cancel.setOnClickListener(new OnClickListener() {

	    public void onClick(View view) {
		onResponseListener.onCancel();
		dialog.dismiss();
	    }

	});
    }

    /**
     * 提醒对话框(标题、副标题、关闭)
     * 
     * @param context
     *            : 调用的activity
     * @param txtTitle
     *            : 标题txt
     * @param txtSubTitle
     *            : 副标题txt,当不需要显示副标题时参数为null或者""
     * @param resIdClose
     *            : 关闭按钮资源ID
     * @param onResponseListener
     *            : 响应回调
     */
    public static void createHint(Context context, String txtTitle, String txtSubTitle, int resIdClose) {
	final AlertDialog dialog = new AlertDialog.Builder(context).create();
	dialog.show();

	Window window = dialog.getWindow();
	int w = 3 * ScreenHelper.getScreenWidth(context) / 5;
	int h = ScreenHelper.getScreenHeight(context) / 5;
	window.setContentView(R.layout.dialog_hint);
	window.setLayout(w, h);

	TextView title = (TextView) window.findViewById(R.id.dialog_hint);
	TextView subTitle = (TextView) window.findViewById(R.id.dialog_hint_extra);
	TextView close = (TextView) window.findViewById(R.id.dialog_hint_close);

	title.setText(txtTitle);
	if (txtSubTitle == null || txtSubTitle.equals("")) {
	    subTitle.setVisibility(View.GONE);
	} else {
	    subTitle.setText(txtSubTitle);
	}
	close.setText(resIdClose);

	close.setOnClickListener(new OnClickListener() {

	    public void onClick(View view) {
		dialog.dismiss();
	    }

	});
    }

    /**
     * 提醒对话框(标题、副标题、确定、关闭)
     * 
     * @param context
     *            : 调用的activity
     * @param txtTitle
     *            : 标题txt
     * @param txtSubTitle
     *            : 副标题txt,当不需要显示副标题时参数为null或者""
     * @param resIdClose
     *            : 关闭按钮资源ID
     * @param onResponseListener
     *            : 响应回调
     */
    public static AlertDialog createHint(Context context, String txtTitle, String txtSubTitle, int resIdClose,
	    final OnResponseSureListener onResponseSureListener) {
	final AlertDialog dialog = new AlertDialog.Builder(context).create();
	dialog.show();

	Window window = dialog.getWindow();
	int w = 3 * ScreenHelper.getScreenWidth(context) / 5;
	int h = ScreenHelper.getScreenHeight(context) / 5;
	window.setContentView(R.layout.dialog_hint);
	window.setLayout(w, h);

	TextView title = (TextView) window.findViewById(R.id.dialog_hint);
	TextView subTitle = (TextView) window.findViewById(R.id.dialog_hint_extra);
	TextView close = (TextView) window.findViewById(R.id.dialog_hint_close);

	title.setText(txtTitle);
	if (txtSubTitle == null || txtSubTitle.equals("")) {
	    subTitle.setVisibility(View.GONE);
	} else {
	    subTitle.setText(txtSubTitle);
	}
	close.setText(resIdClose);

	close.setOnClickListener(new OnClickListener() {

	    public void onClick(View view) {
		onResponseSureListener.onSure();
		dialog.dismiss();
	    }

	});
	return dialog;
    }

    /**
     * 提醒对话框(标题、副标题、确定、关闭，按钮字体颜色)
     * 
     * @param context
     *            : 调用的activity
     * @param txtTitle
     *            : 标题txt
     * @param txtSubTitle
     *            : 副标题txt,当不需要显示副标题时参数为null或者""
     * @param resIdSure
     *            : 确定按钮资源ID
     * @param resIdClose
     *            : 关闭按钮资源ID
     * @param onResponseListener
     *            : 响应回调
     */
    public static AlertDialog createHint(Context context, String txtTitle, String txtSubTitle, int resIdSure,
	    int resIdClose, String sureColor, String closeColor, final OnResponseListener onResponseListener) {
	final AlertDialog dialog = new AlertDialog.Builder(context).create();
	dialog.show();

	Window window = dialog.getWindow();
	int w = 4 * ScreenHelper.getScreenWidth(context) / 5;
	int h = ScreenHelper.getScreenHeight(context) / 5;
	window.setContentView(R.layout.dialog_hint_sure_put_activity);
	window.setLayout(w, -2);

	TextView title = (TextView) window.findViewById(R.id.dialog_hint);
	TextView subTitle = (TextView) window.findViewById(R.id.dialog_hint_extra);
	TextView sure = (TextView) window.findViewById(R.id.dialog_hint_sure);
	TextView close = (TextView) window.findViewById(R.id.dialog_hint_close);

	subTitle.setGravity(Gravity.CENTER);

	if (!TextUtils.isEmpty(sureColor)) {
	    sure.setTextColor(Color.parseColor(sureColor));
	}
	if (!TextUtils.isEmpty(closeColor)) {
	    close.setTextColor(Color.parseColor(closeColor));
	}

	title.setText(txtTitle);
	if (txtSubTitle == null || txtSubTitle.equals("")) {
	    subTitle.setVisibility(View.GONE);
	} else {
	    subTitle.setText(txtSubTitle);
	}
	sure.setText(resIdSure);

	sure.setOnClickListener(new OnClickListener() {

	    public void onClick(View view) {
		onResponseListener.onSure();
		dialog.dismiss();
	    }

	});
	close.setText(resIdClose);

	close.setOnClickListener(new OnClickListener() {

	    public void onClick(View view) {
		onResponseListener.onCancel();
		dialog.dismiss();
	    }

	});
	return dialog;
    }

    /**
     * 取消报名对话框(标题、确定、取消)
     * 
     * @param context
     *            : 调用的activity
     * @param resIdTitle
     *            : title资源ID
     * @param resIdSure
     *            : 确认按钮资源ID
     * @param resIdCancel
     *            : 取消按钮资源ID
     * @param onResponseListener
     *            : 响应回调
     */
    public static void createCancleReport(Context context, int resIdTitle, int resIdSure, int resIdCancel,
	    final OnResponseListener onResponseListener) {
	final AlertDialog dialog = new AlertDialog.Builder(context).create();
	dialog.show();

	Window window = dialog.getWindow();
	int w = 3 * ScreenHelper.getScreenWidth(context) / 5;
	int h = ScreenHelper.getScreenHeight(context) / 5;
	window.setContentView(R.layout.dialog_standard);
	window.setLayout(w, h);

	TextView title = (TextView) window.findViewById(R.id.dialog_standard_title);
	TextView sure = (TextView) window.findViewById(R.id.dialog_standard_sure);
	TextView cancel = (TextView) window.findViewById(R.id.dialog_standard_cancel);
	 

	sure.setOnClickListener(new OnClickListener() {

	    public void onClick(View view) {
		onResponseListener.onSure();
		dialog.dismiss();
	    }

	});
	cancel.setOnClickListener(new OnClickListener() {

	    public void onClick(View view) {
		onResponseListener.onCancel();
		dialog.dismiss();
	    }

	});
    }

   

    /**
     * 提醒对话框(标题、副标题、确定、关闭)
     * 
     * @param context
     *            : 调用的activity
     * @param txtTitle
     *            : 标题txt
     * @param txtSubTitle
     *            : 副标题txt,当不需要显示副标题时参数为null或者""
     * @param resIdSure
     *            : 确定按钮资源ID
     * @param resIdClose
     *            : 关闭按钮资源ID
     * @param onResponseListener
     *            : 响应回调
     */
    public static AlertDialog createHint(Context context, String txtTitle, String txtSubTitle, int resIdSure,
	    int resIdClose, final OnResponseListener onResponseListener) {
	final AlertDialog dialog = new AlertDialog.Builder(context).create();
	dialog.show();

	Window window = dialog.getWindow();
	int w = 3 * ScreenHelper.getScreenWidth(context) / 5;
	int h = ScreenHelper.getScreenHeight(context) / 5;
	window.setContentView(R.layout.dialog_hint_sure);
	window.setLayout(w, h);

	TextView title = (TextView) window.findViewById(R.id.dialog_hint);
	TextView subTitle = (TextView) window.findViewById(R.id.dialog_hint_extra);
	TextView sure = (TextView) window.findViewById(R.id.dialog_hint_sure);
	TextView close = (TextView) window.findViewById(R.id.dialog_hint_close);

	title.setText(txtTitle);
	if (txtSubTitle == null || txtSubTitle.equals("")) {
	    subTitle.setVisibility(View.GONE);
	} else {
	    subTitle.setText(txtSubTitle);
	}
	sure.setText(resIdSure);

	sure.setOnClickListener(new OnClickListener() {

	    public void onClick(View view) {
		onResponseListener.onSure();
		dialog.dismiss();
	    }

	});
	close.setText(resIdClose);

	close.setOnClickListener(new OnClickListener() {

	    public void onClick(View view) {
		onResponseListener.onCancel();
		dialog.dismiss();
	    }

	});
	return dialog;
    }

    

    public static AlertDialog createScrollContent(Context context, String resIdTitle, String contents, String sureText,
	    String cancelText, final OnResponseListener onResponseListener) {
	final AlertDialog dialog = new AlertDialog.Builder(context).create();
	dialog.show();

	Window window = dialog.getWindow();
	window.setContentView(R.layout.dialog_scroll_content);
	window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

	TextView title = (TextView) window.findViewById(R.id.title);
	if (resIdTitle != null) {
	    title.setText(resIdTitle);
	} else {
	    title.setVisibility(View.GONE);
	}

	TextView contentText = (TextView) window.findViewById(R.id.message);
	if (contents != null) {
	    contentText.setText(contents);
	}

	TextView sure = (TextView) window.findViewById(R.id.dialog_standard_sure);
	if (!TextUtils.isEmpty(sureText)) {
	    sure.setText(sureText);
	}
	sure.setOnClickListener(new OnClickListener() {

	    public void onClick(View view) {
		onResponseListener.onSure();
		dialog.dismiss();
	    }

	});

	TextView cancel = (TextView) window.findViewById(R.id.dialog_standard_cancel);
	if (!TextUtils.isEmpty(cancelText)) {
	    cancel.setText(cancelText);
	    cancel.setOnClickListener(new OnClickListener() {

		public void onClick(View view) {
		    onResponseListener.onCancel();
		    dialog.dismiss();
		}

	    });
	} else {
	    cancel.setVisibility(View.GONE);
	}

	return dialog;
    }

    /**
     * 对话框操作响应接口
     * 
     * @author Administrator
     * 
     */
    public interface OnResponseListener {
	public void onSure();

	public void onCancel();
    }

    public interface OnResponseSureListener {
	public void onSure();
    }

}
