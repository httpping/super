package com.vp.loveu.channel.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * @author：pzj
 * @date: 2015年12月22日 下午4:54:18
 * @Description:
 */
public class ToastUtils {
	
	private static Toast toast = null;
    
    public static void showTextToast(Context context,String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

}
