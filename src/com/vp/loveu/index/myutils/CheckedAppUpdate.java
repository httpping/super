package com.vp.loveu.index.myutils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.my.bean.ApkUpgradeBean;
import com.vp.loveu.my.bean.ApkUpgradeBean.ApkUpgradeData;

import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * @时间2015年12月28日下午3:53:33
 * @功能 检测app升级的工具类
 * @作者 mi
 */

public class CheckedAppUpdate {

	public static final int REQUEST_CODE = 200;
	private VpHttpClient mClient;
	private Gson gson = new Gson();
	private Activity context;
	private boolean isShowToast;
	private final static String FILE_NAME = "upgradeNews.apk";
	private File file;
	private OnCheckedListener listener;

	/**
	 * @param context
	 * @param isShowToast
	 *            是否显示失败，成功的提示
	 */
	public CheckedAppUpdate(Activity context, boolean isShowToast,OnCheckedListener listener) {
		this.context = context;
		this.isShowToast = isShowToast;
		this.listener = listener;
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			file = new File(Environment.getExternalStorageDirectory(), FILE_NAME);
		} else {
			file = new File(context.getCacheDir(), FILE_NAME);
		}
	}

	public void upGradeApp() {
		if (mClient == null) {
			mClient = new VpHttpClient(context);
			mClient.setShowProgressDialog(false);
		}
		JsonObject obj = new JsonObject();
		obj.addProperty("soft_id", 1);
		obj.addProperty("build_ver", getVersionCode(context));
		mClient.post(VpConstants.APK_UPGRADE, new RequestParams(), obj.toString(), new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);
				Log.d("aaa", "result"+result);
				ApkUpgradeBean fromJson = gson.fromJson(result, ApkUpgradeBean.class);
				if (fromJson.code == 0) {
					if (fromJson.data == null) {
						if (listener!= null) {
							listener.onNotUpground();
						}
						return;
					}

					if (fromJson.data.build_ver <= getVersionCode(context)) {
						if (isShowToast) {
							Toast.makeText(context, "当前是最新版本,无需升级", Toast.LENGTH_SHORT).show();
						}
						if (listener!= null) {
							listener.onNotUpground();
						}
						return;
					}

					if (fromJson.data.upgrade_type == 0) {
						// 普通升级
						startUpGradeApk(fromJson.data, false);
					} else if (fromJson.data.upgrade_type == 1) {
						// 强制升级
						startUpGradeApk(fromJson.data, true);
					} else if (fromJson.data.upgrade_type == 2) {
						// 停止运营 TODO:
					} else if (fromJson.data.upgrade_type == 3) {
						// 停止运营并跳转到手机浏览器 TODO:
					} else {
						if (listener!= null) {
							listener.onNotUpground();
						}
						if (isShowToast) {
							Toast.makeText(context, "当前是最新版本,无需升级", Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					if (listener!= null) {
						listener.onNotUpground();
					}
					if (fromJson.code == 301) {
						if (isShowToast) {
							Toast.makeText(context, "当前是最新版本,无需升级", Toast.LENGTH_SHORT).show();
						}
					}else{
						if (isShowToast) {
							Toast.makeText(context, "当前是最新版本,无需升级", Toast.LENGTH_SHORT).show();
						}
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				if (isShowToast) {
					Toast.makeText(context, "下载出错!", Toast.LENGTH_SHORT).show();
				}
				if (listener!= null) {
					listener.onNotUpground();
				}
			}
		});
	}

	private void startUpGradeApk(final ApkUpgradeData data, final boolean isFours) {
		Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("发现新版本");
		builder.setMessage(data.summary);
		if (isFours) {
			// 强制下载
			builder.setCancelable(false);
			builder.setPositiveButton("立刻下载", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					startDown(data, isFours);
				}
			});
		} else {
			// 普通下载
			builder.setPositiveButton("立刻下载", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					startDown(data, isFours);
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					if (listener!= null) {
						listener.onDownCancle(isFours);
					}
				}
			});
		}
		builder.show();
	}

	private long total;
	private void startDown(final ApkUpgradeData data, final boolean isFours) {
		final ProgressDialog dialog = new ProgressDialog(context);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setTitle("正在下载");
		dialog.show();
		new AsyncTask<Void, Integer, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				DefaultHttpClient http = new DefaultHttpClient();
				HttpGet get = new HttpGet(data.url);
				try {
					HttpResponse response = http.execute(get);
					if (response.getStatusLine().getStatusCode() == 200) {
						InputStream in = response.getEntity().getContent();
						total = response.getEntity().getContentLength();
						dialog.setMax((int) (total * 1.0f / 1024 + 0.5f));
						FileOutputStream out = new FileOutputStream(file);
						int len =  0;
						byte[] bys = new byte[1024];
						int progress = 0;
						while ((len = in.read(bys)) != -1) {
							out.write(bys, 0, len);
							publishProgress((++progress));
						}
						in.close();
						out.close();
					}else{
						if (listener!= null) {
							listener.onDownError(isFours);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					if (listener!= null) {
						listener.onDownError(isFours);
					}
				}
				return null;
			}

			protected void onProgressUpdate(Integer[] values) {
				int value = values[0];
				dialog.setProgress(value);
				dialog.setProgressNumberFormat(Formatter.formatFileSize(context, value * 1024));
			};

			protected void onPostExecute(Void result) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				installApk(file, isFours);
			};
		}.execute();
	}
	
	private void installApk(File file, boolean isFours) {
		if (file == null || !file.exists()) {
			if (listener!= null) {
				listener.onDownError(isFours);
			}
			if (isShowToast) {
				Toast.makeText(context, "安装失败", Toast.LENGTH_SHORT).show();
			}
			return;
		}
		
		if (listener!= null) {
			listener.onDownFinish(isFours);
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		context.startActivity(intent);
		
		/*
		 * if (total == file.length()) { Intent intent = new
		 * Intent(Intent.ACTION_VIEW);
		 * intent.addCategory(Intent.CATEGORY_DEFAULT);
		 * intent.setDataAndType(Uri.fromFile(file),
		 * "application/vnd.android.package-archive"); //
		 * intent.putExtra("isFours", isFours);
		 * getContext().startActivity(intent); // ((SettingActivity)
		 * getContext()).startActivityForResult(intent, // 100); }
		 */
	}

	private int getVersionCode(Activity context) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
			if (info != null) {
				return info.versionCode;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public interface OnCheckedListener{
		void onDownCancle(boolean isFours);
		void onDownError(boolean isFours);
		void onDownFinish(boolean isFours);
		void onNotUpground();
	}
}
