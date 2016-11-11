package com.vp.loveu.message.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *  时间
 * @author tp
 *
 */
public class VlinkTimeUtil {

	
	/**
	 * 根据本地事件来算
	 * @param date
	 * @return
	 */
	public static final String getexpiredDate(Date date){
		
		Date date2 = new Date();
		
		/*if (date2.after(date)) {
			return "已过期";
		}*/
		
	 	long day = 0; 
        long hour = 0; 
        long min = 0; 
        long sec = 0; 
        try { 
            long time1 = date.getTime(); 
            long time2 = date2.getTime(); 
            long diff ; 
            if(time1<time2) { 
                diff = time2 - time1; 
            } else { 
                diff = time1 - time2; 
            } 
            day = diff / (24 * 60 * 60 * 1000); 
            hour = (diff / (60 * 60 * 1000) - day * 24); 
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60); 
            sec = (diff/1000-day*24*60*60-hour*60*60-min*60); 
            
//            if (day > 30) { // 日期
//            	SimpleDateFormat formatBuilder = new SimpleDateFormat("yyyy-MM-dd");
//            	String string = formatBuilder.format(date);
//				return string;
//			}
            
            if (day >7) {//一周外
            	SimpleDateFormat formatBuilder = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            	String string = formatBuilder.format(date);
            	return string;
			}
            
            
            if( isSameDay(date, date2)){//同一天
				SimpleDateFormat formatBuilder = new SimpleDateFormat("今天 HH:mm");
            	String string = formatBuilder.format(date);
            	return string;
            }
            if (day>=0 && day<=7  ) {//一周
            	String week = getWeekStr(date);
            	SimpleDateFormat formatBuilder = new SimpleDateFormat("HH:mm");
            	String string = formatBuilder.format(date);
            	
				return week +" "+string;
			}
            
            
            
            
//            if ( day == 0) {
//					SimpleDateFormat formatBuilder = new SimpleDateFormat("HH:mm");
//	            	String string = formatBuilder.format(date);
//	            	return string;
//			}
           
        }catch (Exception e) {
        	e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	public static boolean isSameDay(Date day1, Date day2) {
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    String ds1 = sdf.format(day1);
	    String ds2 = sdf.format(day2);
	    if (ds1.equals(ds2)) {
	        return true;
	    } else {
	        return false;
	    }
	}
 
	
	public static String fromatYYYYMMDDHHMM(Date day1) {
		if (day1 == null) {
			return null;
		}
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	    String ds1 = sdf.format(day1);
	    return sdf.format(day1);
	}
	public static String fromatMMDDHHMM(Date day1) {
		if (day1 == null) {
			return null;
		}
	    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
	    String ds1 = sdf.format(day1);
	    return sdf.format(day1);
	}
	
	public static Date parseYYYYMMDDHHMM(String day1) {
		if (day1 == null) {
			return null;
		}
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	    Date ds1 = null;
		try {
			ds1 = sdf.parse(day1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	    return ds1;
	}
	
	public static Date parseDateChina(String day1) {
		if (day1 == null) {
			return null;
		}
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
	    Date ds1 = null;
		try {
			ds1 = sdf.parse(day1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	    return ds1;
	}
	public static String fromatDateChina(Date day1) {
		if (day1 == null) {
			return null;
		}
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
	    String ds1 = null;
		ds1 = sdf.format(day1);
	    return ds1;
	}

	 /**
	  * 根据一个日期，返回是星期几的字符串
	  * 
	  * @param sdate
	  * @return
	  */
	 public static String getWeek(Date date) {
	  // 再转换为时间
	  Calendar c = Calendar.getInstance();
	  c.setTime(date);
	  // int hour=c.get(Calendar.DAY_OF_WEEK);
	  // hour中存的就是星期几了，其范围 1~7
	  // 1=星期日 7=星期六，其他类推
	  return new SimpleDateFormat("EEEE").format(c.getTime());
	 }
	 public static String getWeekStr(Date date){
	  String str = "";
	  str = getWeek(date);
	  if("1".equals(str)){
	   str = "星期日";
	  }else if("2".equals(str)){
	   str = "星期一";
	  }else if("3".equals(str)){
	   str = "星期二";
	  }else if("4".equals(str)){
	   str = "星期三";
	  }else if("5".equals(str)){
	   str = "星期四";
	  }else if("6".equals(str)){
	   str = "星期五";
	  }else if("7".equals(str)){
	   str = "星期六";
	  }
	  return str;
	 }
}
