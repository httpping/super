package com.vp.loveu.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.text.TextUtils;

public class MUtil {
	/**
	 * 获取当前时间以时分显示
	 * 
	 * @return
	 */
	public static String getTimeHour(String orignalTime) {
		SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat dateSdf = new SimpleDateFormat("yy-MM-dd");
		SimpleDateFormat hourDfs = new SimpleDateFormat("HH:mm");
		String sss = "00:00";
		try {
			Calendar nowDay = Calendar.getInstance();
			Calendar oriDay = Calendar.getInstance();
			oriDay.setTime(fullSdf.parse(orignalTime));
			System.out.println("ori" + oriDay.getTime());
			Date dd;
			dd = fullSdf.parse(orignalTime);
			System.out.println("now " + nowDay.get(Calendar.DATE));
			System.out.println("ori " + oriDay.get(Calendar.DATE));
			if (nowDay.get(Calendar.DATE) != oriDay.get(Calendar.DATE)) {
				sss = dateSdf.format(dd);
				System.out.println("by Date" + sss);
			} else {
				sss = hourDfs.format(dd);
				System.out.println("by Hour" + sss);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return sss;
	}

	/**
	 * 比较两个时间差看是否满足大于5分钟
	 * 
	 * @param preTime
	 * @param nowTime
	 * @return
	 */
	public static boolean ifTime5Minute(String preTime, String nowTime) {
		boolean isEnough = false;
		SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date preDay = fullSdf.parse(preTime);
			Date nowDay = fullSdf.parse(nowTime);
			long diff = nowDay.getTime() - preDay.getTime();
			long min = diff / (1000 * 60);
			Calendar calPre = Calendar.getInstance();
			calPre.setTime(preDay);
			Calendar calNow = Calendar.getInstance();
			calNow.setTime(nowDay);
			long dPre = calPre.get(Calendar.DATE);
			long dNow = calNow.get(Calendar.DATE);
			if (dNow > dPre) {
				isEnough = true;
				return isEnough;
			}
			if (min >= 5) {
				isEnough = true;
			} else {
				isEnough = false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println("isEnough" + isEnough);
		return isEnough;
	}

	/**
	 * 获取当前时间以时分显示
	 * 
	 * @return
	 */
	public static String getTimeHourMinute() {
		String time = "";
		Date d = new Date();
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		String formatTime = df.format(d);
		System.out.println(formatTime);
		String addStr = "";
		String hourStr = formatTime.substring(0, formatTime.indexOf(":"));
		String minuteStr = formatTime.substring(formatTime.indexOf(":"),
				formatTime.length());
		System.out.println("hourStr&&" + hourStr);
		String newHourStr;
		int i = Integer.valueOf(hourStr);
		if (i > 12) {
			i = i - 12;
			addStr = " PM";
		} else if (i == 12) {
			i = 0;
			addStr = " PM";
		} else if (i < 12) {
			addStr = " AM";
		}
		if (i > 9)
			newHourStr = String.valueOf(i);
		else
			newHourStr = "0" + String.valueOf(i);
		System.out.println("newHourStr&&" + newHourStr);
		System.out.println("formateTime**" + formatTime);
		time = newHourStr + minuteStr + addStr;
		System.out.println(time);
		return time;
	}

	public static String getStringTime(String s) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd")
					.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.parse(s));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	@SuppressLint("SimpleDateFormat")
	public static String getStringDyTime(String s) {
		try {
			return new SimpleDateFormat("yyyy.MM.dd")
					.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.parse(s));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	@SuppressLint("SimpleDateFormat")
	public static String getStringNewsTime(String s) {
		if (!TextUtils.isEmpty(s)) {
			try {
				Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(s);
				long nowtime = new Date().getTime();
				long stime = date.getTime();
				long ctime = nowtime - stime;
				if (ctime < 86400000) {
					return new SimpleDateFormat("HH:mm").format(date);
				} else if (86400000 < ctime && ctime < 604800000) {
					return new SimpleDateFormat("EEEE").format(date);
				} else {
					return new SimpleDateFormat("yyyy-MM-dd").format(date);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return "";

	}

	// public static String getStringTime(String type,Date date) {
	// return new SimpleDateFormat(type).format(date);
	// }

	public static String getNoewStringTime() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	public static String getNowDate(String s) {
		return new SimpleDateFormat(s).format(new Date());
	}

	public static String getBirthDate(String s) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd")
					.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.parse(s));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getAge(String s) {
		long longs = 0;
		try {
			Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(s);
			longs = new Date().getTime() - date1.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long age = (longs / (24 * 60 * 60 * 1000) + 1) / 365l;
		return String.valueOf(Long.valueOf(age).intValue());
	}

}
