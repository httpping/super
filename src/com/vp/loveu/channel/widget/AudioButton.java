package com.vp.loveu.channel.widget;

import java.io.File;

import com.vp.loveu.R;
import com.vp.loveu.index.myutils.MediaRecorderUtils;
import com.vp.loveu.login.ui.AddOtherUserInfoActivity;
import com.vp.loveu.util.VPLog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.Toast;

/**
 * @项目名称nameloveu1.0
 * @时间2016年3月4日下午1:56:23
 * @功能 仿微信录音的button
 * @作者 mi
 */

public class AudioButton extends Button {

	public static final int STATE_NORMAL = 0;
	public static final int STATE_RECORDERING = 1;
	public static final int STATE_WANT_TO_CANCEL = 2;// 想要取消
	int mCurrentState = STATE_NORMAL;
	
	int timestamp = 3;// 最短时间为3
	final long id = System.currentTimeMillis();
	long startTime;
	long endTime;
	int dx = 20;// 差值

	OnSendAudioListener listener;
	boolean isRecordering;// 是否开始录音
	
	public AudioButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		updateView();
	}

	public AudioButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AudioButton(Context context) {
		this(context, null);
	}

	MediaRecorderUtils recorder = new MediaRecorderUtils() {
		@Override
		public void onStart() {
			mCurrentState = STATE_RECORDERING;
			startTime = System.currentTimeMillis();
			isRecordering = true;
			updateView();
			if (listener != null) {
				listener.onStartRecoder();
			}
			// 开始轮训音量
			handler.sendEmptyMessageDelayed(548, 350);
		};
	};
	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 548) {
				int volume = recorder.getCurrentVolume();

				if (listener != null) {
					listener.updateVolume(volume);
				}
				handler.removeMessages(548);
				handler.sendEmptyMessageDelayed(548, 350);
			} else if (msg.what == 2) {
				if (vibrator!=null) {
					vibrator.vibrate(300);
				}
				recorder.startRecord(id);// 开始录音
			}
		};
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: // 按下的时候 恢复状态
			mCurrentState = STATE_NORMAL;
			handler.sendEmptyMessageDelayed(2, 300);//长按
			updateView();
			break;
		case MotionEvent.ACTION_MOVE:
			if (isRecordering && isWantToCancel(event)) {
				mCurrentState = STATE_WANT_TO_CANCEL;
			} else if (isRecordering && !isWantToCancel(event)) {
				mCurrentState = STATE_RECORDERING;
			} else {
				mCurrentState = STATE_NORMAL;
			}
			if (listener != null) {
				listener.updateView(mCurrentState);
			}
			updateView();
			break;
		case MotionEvent.ACTION_UP:
			VPLog.d("audio", "up");
			recorder.stopRecord();
			if (!isRecordering) {// 如果没有录音 则恢复
				reset();
				break;
			}
			endTime = System.currentTimeMillis();// 记录结束时间
			
			String path = recorder.getPath(id);
			File file = new File(path);
			if (isWantToCancel(event)) {
				// 取消发送
				if (file.exists() && file.isFile()) {
					file.delete(); // 取消发送 删除文件
				}
				reset();
				break;
			}
			if (!isWantToCancel(event)) {
				if ((endTime - startTime) / 1000 < timestamp) {
					Toast.makeText(getContext(), "录音时间过短,请重试", Toast.LENGTH_SHORT).show();
					if (file.exists() && file.isFile()) {
						file.delete(); // 取消发送 删除文件
					}
					reset();
					break;
				} else {
					// 发送语音
					if (listener != null) {
						listener.sendAudio(path, (int) ((endTime - startTime) / 1000));
					}
				}
			}
			reset();
			break;
//		case MotionEvent.ACTION_OUTSIDE:
//			mCurrentState = STATE_WANT_TO_CANCEL;
//			if (listener != null) {
//				listener.updateView(mCurrentState);
//			}
//			updateView();
//			break;
		default:
			break;
		}
		return true;
	}

	private void updateView() {// 更新ui
		int identifier = getResources().getIdentifier("audio_state_" + mCurrentState, "string", getContext().getPackageName());
		setText(identifier);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if (hasWindowFocus) {
			if (isRecordering) {
				recorder.stopRecord();
			}
			reset();
		}
		super.onWindowFocusChanged(hasWindowFocus);
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
		if (focused) {
			if (isRecordering) {
				recorder.stopRecord();
			}
			reset();
		}
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
	}

	private void reset() {
		isRecordering = false;
		mCurrentState = STATE_NORMAL;
		handler.removeCallbacksAndMessages(null);
		updateView();
		if (listener != null) {
			listener.onStopRecoder();
		}
	}

	public boolean isOutside = false;
	private Vibrator vibrator;

	// 是否想要取消
	private boolean isWantToCancel(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		if (x < 0 - dx || x > getMeasuredWidth() + dx) {
			return isOutside = true;
		}
		if (y < 0 - dx) {
			return isOutside = true;
		}
		isOutside = false;
		return false;
	}

	public void setListener(OnSendAudioListener listener) {
		this.listener = listener;
	}

	public interface OnSendAudioListener {
		void sendAudio(String path, int s);// 正常结束 开始发送

		void onStopRecoder();

		void onStartRecoder();// 开始录音

		void updateVolume(int volume);// 音量改变

		void updateView(int status);
	}

	public void onPasue() {
		try {
			recorder.stopRecord();
			handler.removeCallbacksAndMessages(null);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
