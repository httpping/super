package com.vp.loveu.channel.ui;

import java.io.File;
import java.text.Format;
import java.util.ArrayList;
import java.util.regex.Matcher;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.me.nereo.multi_image_selector.MultiImageSelectorActivity;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.bean.InwardAction;
import com.vp.loveu.channel.utils.MediaFile;
import com.vp.loveu.channel.utils.MediaFile.AudioFile;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.login.ui.ClipImageActivity;
import com.vp.loveu.util.LoginStatus;
import com.vp.loveu.widget.CustomProgressDialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * @时间2016年2月26日下午2:28:23
 * @功能 上传音频的管理界面
 * @作者 mi
 */

public class UploadAudioManagerActivity extends VpActivity implements OnClickListener {

	EditText mEtTitle;
	RelativeLayout mSelectPhoto;
	TextView mBtnSelectFile;
	TextView mTvNotice;
	ImageView mIvSelected;
	TextView mTvPhoto;
	ProgressDialog progressDialog;

	Gson gson = new Gson();
	String audioPath;
	long audioSize;
	String audioTitle;
	String coverPath;
	String httpAudioUrl;
	String httpCoverUrl;
	String title;
	//private CustomProgressDialog createDialog;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_audio_manager);
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(1);
		progressDialog.setButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (mClient!=null) {
					mClient.cancelRequests(UploadAudioManagerActivity.this, true);
				}
				dialog.dismiss();
			}
		});
		progressDialog.setCanceledOnTouchOutside(false);
		initPublicTitle();
		initView();
	}

	private void initView() {
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mTvTitle.setText("上传音频");
		mPubTitleView.mBtnRight.setText("上传");
		mPubTitleView.mBtnRight.setOnClickListener(this);
		mPubTitleView.mBtnRight.setTextColor(Color.parseColor("#10BB7D"));
		mBtnSelectFile = (TextView) findViewById(R.id.upload_audio_btn_select_file);
		mEtTitle = (EditText) findViewById(R.id.upload_audio_title);
		mSelectPhoto = (RelativeLayout) findViewById(R.id.select_photo);
		mIvSelected = (ImageView) findViewById(R.id.selected_photo);
		mTvNotice = (TextView) findViewById(R.id.audio_notice);
		mTvPhoto = (TextView) findViewById(R.id.photo_tv);

		mTvNotice.setOnClickListener(this);
		mBtnSelectFile.setOnClickListener(this);
		mSelectPhoto.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.audio_notice:
			// 恋爱电台上传续知
			InwardAction parseAction = InwardAction.parseAction(VpConstants.AUDIO_UPLOAD_NOTICE);
			if (parseAction != null) {
				parseAction.toStartActivity(this);
			}

			break;
		case R.id.upload_audio_btn_select_file:
			// 选择音频
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("*/*");
			startActivityForResult(intent, 0);
			break;
		case R.id.select_photo:
			// 选择封面
			Intent intents = new Intent(UploadAudioManagerActivity.this, MultiImageSelectorActivity.class);
			intents.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
			intents.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, 0);
			intents.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
			startActivityForResult(intents, 1);
			break;
		case R.id.public_top_save:
			// 提交
			title = mEtTitle.getText().toString().trim();
			if (TextUtils.isEmpty(title)) {
				Toast.makeText(this, "请输入标题内容", Toast.LENGTH_SHORT).show();
				break;
			}
			if (TextUtils.isEmpty(audioPath)) {
				Toast.makeText(this, "请选择音频文件", Toast.LENGTH_SHORT).show();
				break;
			}
			if (TextUtils.isEmpty(coverPath)) {
				Toast.makeText(this, "请选择封面照片", Toast.LENGTH_SHORT).show();
				break;
			}
			// 提交
			commit();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if (arg0 == 0 && arg1 == Activity.RESULT_OK) {
			Uri uri = arg2.getData();
			AudioFile bean = MediaFile.getPath(this, uri);
			if (TextUtils.isEmpty(bean.path)) {
				Toast.makeText(getApplicationContext(), "无法识别的音频文件,请重新选择!", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!MediaFile.isAudioFileTypeMp3AndAmr(bean.path)) {
				Toast.makeText(this, "请选择mp3、amr或者wma格式的音频文件!", Toast.LENGTH_SHORT).show();
				return;
			}
			audioPath = bean.path;
			audioSize = bean.size;
			audioTitle = bean.title;
			File file = new File(audioPath);
			if (file.exists()) {
				mBtnSelectFile.setTextColor(Color.parseColor("#222222"));
				mBtnSelectFile.setText(file.getName() + " (" + getLong(file.length()) + ")");
			}
		} else if (arg0 == 1 && arg1 == Activity.RESULT_OK) {
			ArrayList<String> list = arg2.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
			if (list != null && list.size() > 0) {
				String path = list.get(0);
				Intent intent = new Intent(UploadAudioManagerActivity.this, ClipImageActivity.class);
				intent.putExtra("path", path);
				startActivityForResult(intent, 2);
			}
		} else if (arg0 == 2 && arg1 == Activity.RESULT_OK) {
			// 裁剪后返回的相片
			String path = arg2.getStringExtra("path");
			if (!TextUtils.isEmpty(path)) {
				coverPath = path;
				mTvPhoto.setVisibility(View.GONE);
				mIvSelected.setImageURI(Uri.parse(coverPath));
			}
		}
		super.onActivityResult(arg0, arg1, arg2);
	}

	private String getLong(long l) {
		return Formatter.formatFileSize(this, l);
	}

	public void reset() {
		audioPath = null;
		audioSize = 0;
		audioTitle = null;
		httpAudioUrl = null;
		httpCoverUrl = null;
		mBtnSelectFile.setText("");
		mEtTitle.getText().clear();
		mIvSelected.setImageBitmap(null);
	}

	private void commit() {
//		if (createDialog == null) {
//			createDialog = CustomProgressDialog.createDialog(this);
//		}
//		createDialog.show();
		progressDialog.setTitle("正在上传音频...");
		progressDialog.show();
		startUploadAudio(audioPath);
	}

	/**
	 * 开始上传音频
	 * 
	 * @param path
	 */
	private void startUploadAudio(String path) {
		if (mClient == null) {
			mClient = new VpHttpClient(this);
			mClient.setShowProgressDialog(false);
		}
		mClient.postFile(VpConstants.FILE_UPLOAD, VpConstants.FILE_UPLOAD_PATH_AMR_FILE, path, false, false, false,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						String deAesResult = ResultParseUtil.deAesResult(responseBody);
						try {
							JSONObject obj = new JSONObject(deAesResult);
							int state = obj.optInt("state");
							if (state == 1) {
								// 上传成功
								httpAudioUrl = obj.optString("url");
								startUploadAudioCover(coverPath);
							} else {
								// 上传失败
								Toast.makeText(getApplicationContext(), "音频文件上传失败,请重试!", Toast.LENGTH_SHORT).show();
//								if (createDialog.isShowing()) {
//									createDialog.dismiss();
//								}
								if (progressDialog.isShowing()) {
									progressDialog.dismiss();
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onProgress(long bytesWritten, long totalSize) {
						if (progressDialog.getMax() == 100) {
							progressDialog.setMax((int) (totalSize + totalSize / 100));
						}
						progressDialog.setProgress((int) bytesWritten);
						
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
						Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
						// if (createDialog.isShowing()) {
						// createDialog.dismiss();
						// }
						if (progressDialog.isShowing()) {
							progressDialog.dismiss();
						}
					}
				});
	}

	/**
	 * 开始上传音频封面
	 * 
	 * @param path
	 */
	private void startUploadAudioCover(String path) {
		if (mClient == null) {
			mClient = new VpHttpClient(this);
			mClient.setShowProgressDialog(false);
		}
		progressDialog.setTitle("正在上传封面...");
		mClient.postFile(VpConstants.FILE_UPLOAD, VpConstants.FILE_UPLOAD_PATH_PIC_FILE, path, true, true, true,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						String deAesResult = ResultParseUtil.deAesResult(responseBody);
						try {
							JSONObject obj = new JSONObject(deAesResult);
							int state = obj.optInt("state");
							if (state == 1) {
								// 上传成功
								httpCoverUrl = obj.optString("url");
								uploadAudio();
							} else {
								// 上传失败
								Toast.makeText(getApplicationContext(), "音频封面上传失败,请重试!", Toast.LENGTH_SHORT).show();
								// if (createDialog.isShowing()) {
								// createDialog.dismiss();
								// }
								if (progressDialog.isShowing()) {
									progressDialog.dismiss();
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					
					@Override
					public void onProgress(long bytesWritten, long totalSize) {
						progressDialog.setProgress((int) bytesWritten);
						if (progressDialog.getMax() != totalSize + totalSize / 100) {
							progressDialog.setMax((int) (totalSize + totalSize / 100));
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
						Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
						// if (createDialog.isShowing()) {
						// createDialog.dismiss();
						// }
						if (progressDialog.isShowing()) {
							progressDialog.dismiss();
						}
					}
				});
	}

	/**
	 * 开始上传电台 void
	 */
	private void uploadAudio() {
		if (mClient == null) {
			mClient = new VpHttpClient(this);
			mClient.setShowProgressDialog(false);
		}
		LoginUserInfoBean loginInfo = LoginStatus.getLoginInfo();
		int uid = 5;
		if (loginInfo != null) {
			uid = loginInfo.getUid();
		}
		JsonObject obj = new JsonObject();
		obj.addProperty("name", title);
		obj.addProperty("uid", uid);
		obj.addProperty("url", httpAudioUrl);
		obj.addProperty("cover", httpCoverUrl);
		mClient.post(VpConstants.ADD_AUDIO, new RequestParams(), obj.toString(), new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String deAesResult = ResultParseUtil.deAesResult(responseBody);
				try {
					JSONObject obj = new JSONObject(deAesResult);
					if (obj.optInt("code") == 0) {
						// 上传成功
						Toast.makeText(getApplicationContext(), "提交成功，请等待管理员审核", Toast.LENGTH_LONG).show();
						reset(); // 恢复默认项
						mTvNotice.postDelayed(new Runnable() {// 延时2秒关闭界面
							@Override
							public void run() {
								finish();
							}
						}, 2000);
					} else {
						// 上传失败
						Toast.makeText(getApplicationContext(), "上传失败,请重试!", Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 销毁dialog
				// if (createDialog.isShowing()) {
				// createDialog.dismiss();
				// }

				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
				// if (createDialog.isShowing()) {
				// createDialog.dismiss();
				// }
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			}
		});
	}
}
