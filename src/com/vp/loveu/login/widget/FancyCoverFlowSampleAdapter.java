/*
 * Copyright 2013 David Schreiber
 *           2013 John Paul Nalog
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.vp.loveu.login.widget;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.vp.loveu.R;
import com.vp.loveu.login.ui.AddOtherUserInfoActivity.FancyCoverFlowCallBack;
import com.vp.loveu.util.UIUtils;
import com.vp.loveu.widget.CircleImageView;

public class FancyCoverFlowSampleAdapter extends FancyCoverFlowAdapter {
	private ArrayList<String> mDatas;
	private DisplayImageOptions options;
	private FancyCoverFlowCallBack mCallback;
	private boolean isFirstEnter=true;//是否首次进入，默认选中第二个
	
	public FancyCoverFlowSampleAdapter(ArrayList<String> paths,FancyCoverFlowCallBack callBack) {
		mDatas=paths;
		mCallback=callBack;
		 options = new DisplayImageOptions.Builder()
		         .showImageOnLoading(R.drawable.overlayer) // resource or
		        .showImageForEmptyUri(R.drawable.overlayer) // resource or
		        .showImageOnFail(R.drawable.overlayer) // resource or
		        .resetViewBeforeLoading(false) // default
		        .cacheInMemory(true) // default
		        .cacheOnDisk(true) // default
		        .bitmapConfig(Bitmap.Config.RGB_565)  
		        .considerExifParams(false) // default
		        .displayer(new SimpleBitmapDisplayer())
		        .build();
	}

    // =============================================================================
    // Private members
    // =============================================================================

//    private int[] images = {R.drawable.overlayer, R.drawable.overlayer, R.drawable.overlayer, R.drawable.overlayer, R.drawable.overlayer, R.drawable.overlayer,};

    // =============================================================================
    // Supertype overrides
    // =============================================================================

    @Override
    public int getCount() {
    	if(mDatas!=null){
    		return mDatas.size();
    	}
        return 0;
    }

    @Override
    public Integer getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getCoverFlowItem(int i, View reuseableView, ViewGroup viewGroup) {
    	CircleImageView imageView = null;

        if (reuseableView != null) {
            imageView = (CircleImageView) reuseableView;
        } else {
            imageView = new CircleImageView(viewGroup.getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new FancyCoverFlow.LayoutParams(UIUtils.dp2px(60),UIUtils.dp2px(60)));

        }

//        imageView.setImageResource(images[i]);
        final int tempIndex=i;
        ImageLoader.getInstance().displayImage(mDatas.get(i), imageView,options,new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String arg0, View arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				//加载完成,
				if(mCallback!=null && tempIndex==1  && isFirstEnter){
					isFirstEnter=false;
					mCallback.onFinish();
					
				}
				
			}
			
			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				// TODO Auto-generated method stub
				
			}
		});
        return imageView;
    }
}
