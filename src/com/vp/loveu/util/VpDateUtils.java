package com.vp.loveu.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author：pzj
 * @date: 2015年12月4日 下午4:45:16
 * @Description:
 */
public class VpDateUtils {
	
    /** 
     * 将时间戳转为代表"距现在多久之前"的字符串 
     * @param timeStr   时间戳  单位:秒
     * @return 
     * n取值
     * 小于1分钟--》刚刚
     * 1-60分钟--》n分钟前
     * 1-24小时--》n小时前
     * 1-30天   --》n天前
     * >30天      --》显示具体日期
     */  
    public static String getStandardDate(String timeStr) {  
      
        StringBuffer sb = new StringBuffer();  
      
        long t = Long.parseLong(timeStr);  
        long time = System.currentTimeMillis() - (t*1000);  
        long mill = (long) Math.ceil(time /1000);//秒前  
      
        long minute = (long) Math.ceil(time/60/1000.0f);// 分钟前  
      
        long hour = (long) Math.ceil(time/60/60/1000.0f);// 小时  
      
        long day = (long) Math.ceil(time/24/60/60/1000.0f);// 天前  
      
        if (day - 1 > 0) {
        	if(day>30){
        		return formatTime(timeStr);
        	}else{       		
        		sb.append(day + "天");  
        	}
        } else if (hour - 1 > 0) {  
            if (hour >= 24) {  
                sb.append("1天");  
            } else {  
                sb.append(hour + "小时");  
            }  
        } else if (minute - 1 > 0) {  
            if (minute == 60) {  
                sb.append("1小时");  
            } else {  
                sb.append(minute + "分钟");  
            }  
        } else {  
            sb.append("刚刚");  
        }  
        if (!sb.toString().equals("刚刚")) {  
            sb.append("前");  
        }  
        return sb.toString();  
    } 
    
    
	/**
	 * 时间格式转换
	 * @param create_time
	 * @return
	 */
	public static String formatTime(String createTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
		String date = sdf.format(new Date((Long.parseLong(createTime))*1000));
		return date;
	}
	

}
