package com.vp.loveu.channel.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.vp.loveu.R;

/**
 * @author：pzj
 * @date: 2015年11月30日 上午9:24:53
 * @Description:更多
 */
public class MoreDialogFragment extends DialogFragment implements OnClickListener {
	private  TextView mTvAttention;
	private  TextView mTvChat;
	private  TextView mTvReport;
	private  TextView mTvCancel;
	private  MorePopItemClickListener mCallBack;
	
	
public MoreDialogFragment(MorePopItemClickListener callBack) {
	mCallBack=callBack;
}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);  
        View view = inflater.inflate(R.layout.channel_topic_more_pop, container);  
        this.mTvAttention=(TextView) view.findViewById(R.id.channel_topic_more_attention);
        this.mTvChat=(TextView) view.findViewById(R.id.channel_topic_more_chat);
        this.mTvReport=(TextView) view.findViewById(R.id.channel_topic_more_report);
        this.mTvCancel=(TextView) view.findViewById(R.id.channel_topic_more_cancel);
        this.mTvAttention.setOnClickListener(this);
        this.mTvChat.setOnClickListener(this);
        this.mTvReport.setOnClickListener(this);
        this.mTvCancel.setOnClickListener(this);
        return view;
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics( dm );
		getDialog().getWindow().setLayout( dm.widthPixels, getDialog().getWindow().getAttributes().height );
		
		WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
		layoutParams.width = dm.widthPixels;
		layoutParams.gravity = Gravity.BOTTOM;
		getDialog().getWindow().setAttributes(layoutParams);
	}

	@Override
	public void onClick(View v) {
//		MorePopItemClickListener listener = (MorePopItemClickListener) getActivity();   
//		listener.onMoreItemClick(v);
		if(mCallBack!=null)
			mCallBack.onMoreItemClick(v);
		dismiss();
	}
	
	public interface MorePopItemClickListener{
		public void onMoreItemClick(View v);
	}
}
