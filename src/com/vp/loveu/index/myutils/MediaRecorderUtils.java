package com.vp.loveu.index.myutils;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.vp.loveu.util.UIUtils;
import com.vp.loveu.util.VPLog;

/**
 * @项目名称nameloveu1.0
 * @时间2015年11月27日下午3:30:47
 * @功能 音频录制
 * @作者 mi
 */

public class MediaRecorderUtils {

	public MediaRecorder media;
	private long startTime;
	private long endTime;
	private boolean isPrepare;

	public MediaRecorderUtils() {
	}

	public void startRecord(long id) {
		try {
			String path = getPath(id);
			File file = new File(path);
			if (file.exists() && file.isFile())
				file.delete();
			media = new MediaRecorder();
			media.setAudioSource(MediaRecorder.AudioSource.MIC);
			media.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			media.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			media.setOutputFile(path);
			media.prepare();
			media.start();
			isPrepare = true;
			onStart();
			startTime = System.currentTimeMillis();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void onStart() {
	}

	public String getPath(long id) {
		String path;
		if (isSdcardMounted()) {
			path = new File(Environment.getExternalStorageDirectory(), "record_" + id + ".amr").getAbsolutePath();
		} else {
			path = new File(UIUtils.getContext().getFilesDir(), "record_" + id + ".amr").getAbsolutePath();
		}
		return path;
	}

	public void stopRecord() {
		isPrepare = false;
		if (media != null) {
			media.stop();
			endTime = System.currentTimeMillis();
			media.release();
			media = null;
		}
	}

	/**
	 * 获取录音时长 单位 s
	 * 
	 * @return int
	 */
	public int getRecoderTimes() {
		return (int) ((endTime - startTime) / 1000);
	}

	private boolean isSdcardMounted() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public int getCurrentVolume() {
		if (isPrepare) {
			try {
				// 取证+1，否则去不到5
				int audioSourceMax = media.getAudioSourceMax();
				VPLog.d("aaa", audioSourceMax + "audioSourceMax");
				return 5 * media.getMaxAmplitude() / 32768 + 1;
			} catch (Exception e) {
				// TODO Auto-generated catch block

			}
		}
		return 1;
	}

}
