package com.vp.loveu.index.myutils;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.widget.VideoView;

import com.vp.loveu.util.UIUtils;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年11月27日下午5:31:22
 * @功能 音频播放
 * @作者 mi
 */

public class MediaPlayerRecordUtils {

	private MediaPlayer mediaPlayer;
	private OnCompletionListenerCallBack mCallback;

	public MediaPlayerRecordUtils() {
		AudioManager audio = (AudioManager) UIUtils.getContext().getSystemService(Context.AUDIO_SERVICE);
		int streamMaxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		audio.setStreamVolume(AudioManager.STREAM_MUSIC, streamMaxVolume / 2, 0);
	}

	public void startPlay(String path) {
		stopPlayer();
		mediaPlayer = new MediaPlayer();
		try {
			mediaPlayer.setDataSource(path);
			mediaPlayer.prepare();
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					if (mCallback != null) {
						mCallback.onPrepared();
					}
					mp.start();
				}
			});
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					mediaPlayer.release();
					mediaPlayer = null;
					if (mCallback != null)
						mCallback.onCompletionListenerCallBack();
				}
			});

			mediaPlayer.setOnErrorListener(new OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					if (mCallback != null) {
						mCallback.onError();
					}
					return false;
				}
			});
			
		} catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
			e.printStackTrace();
			if (mCallback != null) {
				mCallback.onError();
			}
		}
	}

	public void getDuration() {
		if (mediaPlayer != null) {
			mediaPlayer.getDuration();
		}
	}

	public int getPosition() {
		if (mediaPlayer != null) {
			return mediaPlayer.getCurrentPosition();
		}
		return -1;
	}

	public void pausePlayer() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
		}
	}

	public void stopPlayer() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	public boolean isPlaying() {
		boolean flag = false;
		try {
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				flag = true;
			}
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	public interface OnCompletionListenerCallBack {
		public void onCompletionListenerCallBack();
		void onError();
		void onPrepared();
	}

	public OnCompletionListenerCallBack getmCallback() {
		return mCallback;
	}

	public void setmCallback(OnCompletionListenerCallBack mCallback) {
		this.mCallback = mCallback;
	}
}
