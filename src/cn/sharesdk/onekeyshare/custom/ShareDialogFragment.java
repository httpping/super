package cn.sharesdk.onekeyshare.custom;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.vp.loveu.R;
import com.vp.loveu.base.VpApplication;
import com.vp.loveu.util.SharedPreferencesHelper;
import com.vp.loveu.util.VPLog;

/**
 * @author：pzj
 * @date: 2015年11月30日 上午9:24:53
 * @Description: 分享弹出页面
 */
public class ShareDialogFragment extends DialogFragment {
	private Context mContext;
	private PlatformActionListener platformActionListener;
	private ShareModel mShareModel;
	private ShareParams shareParams;
	
	public void setShowCopy(boolean isShowCopy) {
		this.isShowCopy = isShowCopy;
	}
	private boolean isShowCopy = true;//是否显示复制链接按钮
	
	private String[] shareDes=new String[]{"微博","朋友圈","微信","复制链接"};
	private int[] shareIcons= new int[] { R.drawable.public_share_sina,
			R.drawable.public_share_comments, R.drawable.public_share_wechat,R.drawable.public_share_copylink };

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);  
        this.mContext=getActivity();
		View view=View.inflate(mContext, R.layout.share_layout, null);
		GridView gridView = (GridView) view.findViewById(R.id.share_gridview);
		if (!isShowCopy) {
			gridView.setNumColumns(3);
		}
		gridView.setAdapter(new ShareAdapter());
		gridView.setOnItemClickListener(new ShareItemClickListener());
        return view;
	}
	public void show(FragmentManager manager, String tag,ShareModel model){
		this.mShareModel=model;
		this.show(manager, tag);
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
	
	private class ShareItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			dismiss();
			openSharePage(position);
			
		}
		
	}
	
	public void openSharePage(int position) {
		if(mShareModel==null){
			Toast.makeText(mContext, "参数为空", Toast.LENGTH_SHORT).show();
			return;
		}
		initShareParams(mShareModel);
		switch (position) {
		case 0://微博
			sinaShare();
			break;
		case 1://"朋友圈",,,
			wechatMomentsShare();
			break;
		case 2://"微信"
			wechatShare();
			break;
		case 3://"复制链接"
			// 得到剪贴板管理器 
			ClipboardManager cmb = (ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE); 
			cmb.setPrimaryClip(ClipData.newPlainText(null, mShareModel.getUrl()));
			Toast.makeText(mContext, "复制链接成功", Toast.LENGTH_SHORT).show();
			break;
		case 4://"QQ空间"
//			qqZoneShare();
			break;

		default:
			break;
		}
		
	}
	

	private void sinaShare() {
		Platform sinaWeibo = ShareSDK.getPlatform(mContext, SinaWeibo.NAME);
		sinaWeibo.setPlatformActionListener(platformActionListener);
		sinaWeibo.share(shareParams);
		
	}
	
	private void wechatShare() {
		Platform pf = ShareSDK.getPlatform(mContext, Wechat.NAME);
		pf.setPlatformActionListener(platformActionListener);
		pf.share(shareParams);
		
	}


	private void wechatMomentsShare() {
		Platform pf = ShareSDK.getPlatform(mContext, WechatMoments.NAME);
		pf.setPlatformActionListener(platformActionListener);
		pf.share(shareParams);
		
	}

	private void qqShare() {
		ShareParams sp =getShareParams();
		Platform qq = ShareSDK.getPlatform(mContext, QQ.NAME);
		qq.setPlatformActionListener(platformActionListener);
		qq.share(sp);
	}
	
/*	private void qqZoneShare() {
		ShareParams sp =getShareParams();
		Platform qzone = ShareSDK.getPlatform(mContext, QZone.NAME);
		qzone.setPlatformActionListener(platformActionListener); // 设置分享事件回调 //
		qzone.share(sp);
		
	}*/
	
	private ShareParams getShareParams(){
		ShareParams sp = new ShareParams();
		sp.setImageUrl(shareParams.getImageUrl());
		sp.setImageData(shareParams.getImageData());
		sp.setTitle(shareParams.getTitle());
		sp.setTitleUrl(shareParams.getUrl()); // 标题的超链接
		sp.setText(shareParams.getText());
//		sp.setComment("我对此分享内容的评论");
		sp.setSite(shareParams.getTitle());
		sp.setSiteUrl(shareParams.getUrl());
		return sp;
	}
	
	/**
	 * 初始化分享参数
	 * 
	 * @param shareModel
	 */
	public void initShareParams(ShareModel shareModel) {
		if (shareModel != null) {
			ShareParams sp = new ShareParams();
			sp.setShareType(Platform.SHARE_TEXT);
			sp.setShareType(Platform.SHARE_WEBPAGE);
			if(shareModel.getTitle()!=null)
				sp.setTitle(shareModel.getTitle());
			if(shareModel.getText()!=null)
				sp.setText(shareModel.getText());
			else {
				try {
					SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(VpApplication.getInstance());
					sp.setText(sharedPreferencesHelper.getStringValue("app_share_describe"));	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(shareModel.getUrl()!=null)
				sp.setUrl(shareModel.getUrl());
			if (shareModel.getImageUrl() != null) {
				sp.setImageUrl(shareModel.getImageUrl());
			} else{
				Bitmap bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_logo);//default icon
				sp.setImageData(bitmap);
			}
			shareParams = sp;
			VPLog.d("share", shareParams.getText()+"");
		}
	}
	
	private class ShareAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			if (isShowCopy) {
				return shareDes.length;
			}else{
				return shareDes.length - 1;
			}
		}

		@Override
		public Object getItem(int positon) {
			return shareDes[positon];
		}

		@Override
		public long getItemId(int positon) {
			return positon;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view=null;
			if(convertView==null){				
				view=View.inflate(mContext, R.layout.share_item, null);
			}else{
				view=convertView;
			}
			ImageView iv=(ImageView) view.findViewById(R.id.share_icon);
			TextView tv=(TextView) view.findViewById(R.id.share_title);
			iv.setImageResource(shareIcons[position]);
			tv.setText(shareDes[position]);
			
			return view;
		}
		
	}

	public PlatformActionListener getPlatformActionListener() {
		return platformActionListener;
	}
	public void setPlatformActionListener(PlatformActionListener platformActionListener) {
		this.platformActionListener = platformActionListener;
	}
	
	
}
