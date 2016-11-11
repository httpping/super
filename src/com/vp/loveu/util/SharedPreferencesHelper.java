package com.vp.loveu.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * **********************************************************
 * 
 * @ClassName: SharedPreferencesHelper
 * @Description:Shared方式的数据存储
 * @author liyq
 * @date 
 * @version V1.0.0
 * @Function  
 ********************************************************** 
 */
@SuppressLint("CommitPrefEdits")
public class SharedPreferencesHelper
{
    private SharedPreferences sp;
    
    private SharedPreferences.Editor editor;
    
    private static final long DEFUALT_LONG_VALUES = 0L;
    
    private static final int DEFUALT_INTEGER_VALUES = 0;
    
    private static final String PRES_NAME = "loveu";
    
    private static SharedPreferencesHelper helper;
    
    private SharedPreferencesHelper(Context context)
    {
        sp = context.getSharedPreferences(PRES_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }
    
    public static SharedPreferencesHelper getInstance(Context context)
    {
        if (helper == null)
        {
            helper = new SharedPreferencesHelper(context);
        }
        return helper;
    }
    
    /**
     * 
     * @Title: putStringValue
     * @Description: 
     * @param @param key
     * @param @param value
     * @return void
     * @throws
     * @author liyq
     * @date
     * @History: author time version desc
     */
    public void putStringValue(String key, String value)
    {
        editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }
    
    public void removeKey(String key){
    	editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }
    
    /**
     * 
     * @Title: putBooleanValue
     * @Description: 
     * @param @param key
     * @param @param value
     * @return void
     * @throws
     * @author liyq
     * @date  
     * @History: author time version desc
     */
    public void putBooleanValue(String key, Boolean value)
    {
        editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    
    /**
     * 
     * @Title: putIntegerValue
     * @Description:  
     * @param @param key
     * @param @param value
     * @return void
     * @throws
     * @author liyq
     * @date  
     * @History: author time version desc
     */
    public void putIntegerValue(String key, Integer value)
    {
        editor = sp.edit();
        if (value == null)
        {
            value = DEFUALT_INTEGER_VALUES;
        }
        editor.putInt(key, value);
        editor.commit();
    }
    
    /**
     * 
     * @Title: putLongValue
     * @Description: 
     * @param @param key
     * @param @param value
     * @return void
     * @throws
     * @author liyq
     * @date 
     * @History: author time version desc
     */
    public void putLongValue(String key, Long value)
    {
        editor = sp.edit();
        if (value == null)
        {
            value = DEFUALT_LONG_VALUES;
        }
        editor.putLong(key, value);
        editor.commit();
    }
    
    /**
     * 
     * @Title: getBooleanValue
     * @Description:  
     * @param @param key
     * @param @return
     * @return Boolean
     * @throws
     * @author liyq
     * @date  
     * @History: author time version desc
     */
    public Boolean getBooleanValue(String key)
    {
        return sp.getBoolean(key, false);
    }
    
    /**
     * 
     * @Title: getStringValue
     * @Description: 
     * @param @param key
     * @param @return
     * @return String
     * @throws
     * @author liyq
     * @date 2013-1-9 9:39:10
     * @History: author time version desc
     */
    public String getStringValue(String key)
    {
        return sp.getString(key, null);
    }
    
    /**
     * 
     * @Title: getIntegerValue
     * @Description: 
     * @param @param key
     * @param @return
     * @return Integer
     * @throws
     * @author liyq
     * @date 2013-1-9 ����9:39:24
     * @History: author time version desc
     */
    public int getIntegerValue(String key)
    {
        return sp.getInt(key, DEFUALT_INTEGER_VALUES);
    }
    public int getIntegerValue(String key,int defaultValue)
    {
    	return sp.getInt(key, defaultValue);
    }
    
    /**
     * 
     * @Title: getLongValue
     * @Description: ��ȡLong���
     * @param @param key
     * @param @return
     * @return Long
     * @throws
     * @author liyq
     * @date 2013-1-9 ����9:40:28
     * @History: author time version desc
     */
    public long getLongValue(String key)
    {
        return sp.getLong(key, DEFUALT_LONG_VALUES);
    }
    
    public void removeValue(String key){
    	if(key!=null){
    		
    		editor = sp.edit();
    		editor.remove(key);
    		editor.commit();
    	}
    }
    
    public void removeAllValue(){
    	editor = sp.edit();
    	editor.clear();
    	editor.commit();
    }
    
}
