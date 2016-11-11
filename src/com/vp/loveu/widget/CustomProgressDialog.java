package com.vp.loveu.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.vp.loveu.R;
import com.vp.loveu.util.ScreenUtils;

/**
 * @Title: CustomProgressDialog.java
 * @Package com.eachgame.android.common.view
 * @Description: 进度条
 * @author EachGame Android Team, tanping
 */
public class CustomProgressDialog extends Dialog {
    Context context = null;
    private static CustomProgressDialog customProgressDialog = null;

    public CustomProgressDialog(Context context) {
	super(context);
	this.context = context;
    }

    public CustomProgressDialog(Context context, int theme) {
	super(context, theme);
    }

    public static CustomProgressDialog createDialog(Context context) {
	customProgressDialog = new CustomProgressDialog(context, R.style.CustomProgressDialog);
	customProgressDialog.setContentView(R.layout.custom_progress_dialog);
	customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
   /* com.vp.loveu.widget.MetaballView metaballView = (MetaballView) customProgressDialog.findViewById(R.id.metaball);
    metaballView.setPaintMode(1);*/
	//customProgressDialog.getWindow().setBackgroundDrawableResource(R.color.write_trans_90);
	ScreenUtils.initScreen((Activity) context);
	WindowManager.LayoutParams params = customProgressDialog.getWindow().getAttributes();  
       params.dimAmount = 0f;  
     /*  params.width = ScreenBean.screenWidth;
       params.height = ScreenBean.scrrenHeight;*/
       customProgressDialog.getWindow().setAttributes(params); 
	
	return customProgressDialog;
    }

    public void onWindowFocusChanged(boolean hasFocus) {

	if (customProgressDialog == null) {
	    return;
	}

	
    }
    
    @Override
    public void show() {
    	ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
    	AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
    	animationDrawable.start();
    	super.show();
    }

    /**
     * 
     * [Summary] setTitile 标题
     * 
     * @param strTitle
     * @return
     * 
     */
    public CustomProgressDialog setTitile(String strTitle) {
	return customProgressDialog;
    }

    /**
     * 
     * [Summary] setMessage 提示内容
     * 
     * @param strMessage
     * @return
     * 
     */
    public CustomProgressDialog setMessage(String strMessage) {
	TextView tvMsg = (TextView) customProgressDialog.findViewById(R.id.id_tv_loadingmsg);

	if (tvMsg != null) {
	    tvMsg.setText(strMessage);
	}

	return customProgressDialog;
    }

}
