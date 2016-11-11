package com.vp.loveu.message.bean;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.me.nereo.multi_image_selector.MultiImageSelectorActivity;
import com.vp.loveu.message.ui.PrivateChatActivity;

/**
 * plus 选择图片bean
 * @author tanping
 * 2015-11-18
 */
public class SelectImgageBean extends OperateDataBaseEntity{
	
	@Override
	public void onClick(View v) {
		//super.onClick(v);
		selectPhoto(v.getContext());
	}
	
	private void selectPhoto(Context context){
        int selectedMode = MultiImageSelectorActivity.MODE_SINGLE;

        boolean showCamera = true;

        int maxNum = 9;

        Intent intent = new Intent(context, MultiImageSelectorActivity.class);
        // 是否显示拍摄图片
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, showCamera);
        // 最大可选择图片数量
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, maxNum);
        // 选择模式
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, selectedMode);
        // 默认选择
        /*if(mSelectPath != null && mSelectPath.size()>0){
            intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
        }*/
        ((FragmentActivity) context).startActivityForResult(intent, PrivateChatActivity.SELECT_IMGAGE_REQUEST);
	}

}
