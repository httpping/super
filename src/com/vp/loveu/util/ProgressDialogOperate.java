package com.vp.loveu.util;

 


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

 
public class ProgressDialogOperate
{
    private static Context context; 
    
    private static ProgressDialog mProgressDialog;
    
    private static final int SHOW_PROGRESS_DIALOG = 1;
    
    private static final int DISMISS_PROGRESS_DIALOG = 2;
    
    
    private static final Handler handler = new Handler()
    {
        
        @Override
        public void handleMessage(Message msg)
        {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            try
            {
                switch (msg.what)
                {
                    case SHOW_PROGRESS_DIALOG:
                        try
                        {
                            if (mProgressDialog != null)
                            {
                                if (mProgressDialog.isShowing())
                                {
                                    mProgressDialog.dismiss();
                                }
                                mProgressDialog = null;
                            }
                            mProgressDialog = new ProgressDialog(context);
                            mProgressDialog.setIndeterminate(true);
                            mProgressDialog.setCanceledOnTouchOutside(false);
                            mProgressDialog.setMessage("加载数据中...");
                            mProgressDialog.show();
                        }
                        catch (Exception e)
                        {
                            // TODO: handle exception
                        }
                        break;
                    case DISMISS_PROGRESS_DIALOG:
                        try
                        {
                            if (mProgressDialog != null || mProgressDialog.isShowing()
                                || mProgressDialog.isIndeterminate())
                            {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }
                        }
                        catch (Exception e)
                        {
                        }
                        break;
                    default:
                        break;
                }
            }
            catch (Exception e)
            {
                // TODO: handle exception
            }
        }
    };
    
  
    public static void showProgressDialog(Context context)
    {
        try
        {
            ProgressDialogOperate.context = context;
                handler.sendEmptyMessage(SHOW_PROGRESS_DIALOG);
        }
        catch (Exception e)
        {
        	
        }
    }
    
  /**
   * 
  * @Title: dismissProgressDialog 
  * @Description: TODO(这里用一句话描述这个方法的作用) 
  * @param     设定文件 
  * @return void    返回类型 
  * @throws
   */
    public static void dismissProgressDialog()
    {
        try
        {
            handler.sendEmptyMessage(DISMISS_PROGRESS_DIALOG);
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
    }
    
}
