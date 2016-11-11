package com.openshare.download;
 

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import android.util.Log;

/**
 *文件下载状态和信息 状态
 * @author tanping
 *
 */
public class StateInfo  extends Observable{

	float speed ;// 速度
	float progress;// 下载进度
	long  size ;// 下载大小
	String filePath ;// 下载完成的目录
	boolean complete ;// 是否完成
	boolean error ;// 下载错误
	boolean isRepeatDown;// 是否重新下载
	String errorInfo ;//错误信息
	String url;
	String type ; // 文件类型
	String extendName ;// 文件扩展名
	String resultCode ;//请求返回
	
	public boolean stop = false ;//手动是否停止
	public boolean mustWifi = false; // 必须wifi
	
	
	long lastStartTime = System.currentTimeMillis();// 上一次开始时间
	
	long startTime  = System.currentTimeMillis();// 开始时间
	long endTime ;//结束时间
	long maxWaitTime = -1;// 最长下载时间 -1 表示无穷大
	
	public Map<String, List<String>> reqestHead ;//请求返回头
	
	
	public StateInfo(String url){
		if (url == null) {
			throw new IllegalArgumentException("url 不允许为null");
		}
		
		String[] arrs = url.split("\\.");
		if (arrs.length !=0) {
			extendName = arrs[arrs.length -1];
			if (extendName!=null && extendName.trim().toLowerCase().startsWith("com")) {
				extendName = null;
			}
		}
		this.url = url;
	}
	
	
	/**
	 * 打印头信息
	 */
	public void printHeadInfo(){
		
		if (reqestHead == null) {
			return ;
		}
		
		Iterator<String> it =reqestHead.keySet().iterator();
		while (it.hasNext()){
			String key = it.next();
			Log.d("reqestHead",key +""+reqestHead.get(key));
		}
		
	}
	

	public float getSpeed() {
		return speed;
	}

	public float getProgress() {
		return progress;
	}

	public String getFilePath() {
		return filePath;
	}

	public boolean isComplete() {
		return complete;
	}

	public String getUrl() {
		return url;
	}
	
	
	
	
	
	public boolean isError() {
		return error;
	}



	public String getErrorInfo() {
		return errorInfo;
	}



	public long getStartTime() {
		return startTime;
	}



	public long getEndTime() {
		return endTime;
	}



	/**
	 * 更新了数据
	 */
	void update(){
		
		if (error || complete) {
			endTime = System.currentTimeMillis();
		}
		
		setChanged();
		notifyObservers(); //提醒
	}
	
	
	
}
