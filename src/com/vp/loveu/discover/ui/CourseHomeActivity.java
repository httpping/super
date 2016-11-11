package com.vp.loveu.discover.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.discover.bean.CourseOnlineBean;
import com.vp.loveu.discover.bean.CourseOnlineBeanVo;
import com.vp.loveu.discover.bean.CourseOnlineBeanVo.Lesson;
import com.vp.loveu.discover.bean.CourseOnlineBeanVo.User;
import com.vp.loveu.util.VpDateUtils;
import com.vp.loveu.widget.CircleImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;

/**
 * @author：pzj
 * @date: 2015年11月18日 上午10:13:03
 * @Description: 在线课程首页
 */
public class CourseHomeActivity extends VpActivity {
	private ListView mListView;
	private CourseHomeAdapter mAdapter;
	private static final int TYPE_LESSON = 0;
	private static final int TYPE_DESC = 1;
	private static final int TYPE_USER_COUNT = 2;
	private static final int TYPE_USER = 3;
	private List<CourseOnlineBean> mDatas;
	private int user_num;//累计报名人数
	private DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.discover_course_home_activity);
		this.mClient = new VpHttpClient(this);
		initView();
		initDatas();
	}

	private void initView() {
		initPublicTitle();
		mPubTitleView.mBtnLeft.setText("");
		mPubTitleView.mBtnLeft.setBackgroundResource(R.drawable.icon_back);
		mPubTitleView.mTvTitle.setText("在线课程");
		this.mListView = (ListView) findViewById(R.id.discover_course_listview);
		this.mAdapter = new CourseHomeAdapter();
		this.mListView.setAdapter(this.mAdapter);
		
		options = new DisplayImageOptions.Builder()
		         .showImageOnLoading(R.color.frenchgrey) // resource or
		        .showImageForEmptyUri(R.drawable.ic_launcher) // resource or
		        .showImageOnFail(R.drawable.ic_launcher) // resource or
		        .resetViewBeforeLoading(false) // default
		        .cacheInMemory(true) // default
		        .cacheOnDisk(true) // default
		        .bitmapConfig(Bitmap.Config.RGB_565)  
		        .considerExifParams(false) // default
		        .displayer(new SimpleBitmapDisplayer())
		        .build();
		
		this.mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				position=position-mListView.getHeaderViewsCount();
				int dataType = mDatas.get(position).getType();
				//点击的是课程
				if(dataType==TYPE_LESSON){
					Intent intent=new Intent(CourseHomeActivity.this,CourseDetailActivity.class);
					intent.putExtra(CourseDetailActivity.COURSE_ID, mDatas.get(position).getId());
					startActivity(intent);
				}
				
			}
		});

	}

	private void initDatas() {
		String url = VpConstants.DISCOVER_HOME;

		mClient.get(url, new RequestParams(), new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String result = ResultParseUtil.deAesResult(responseBody);
				
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString(VpConstants.HttpKey.CODE);

					if ("0".equals(code)) {// 返回成功
						JSONObject jsonData=json.getJSONObject(VpConstants.HttpKey.DATA);
						CourseOnlineBeanVo homeBean = CourseOnlineBeanVo.parseJson(jsonData.toString());
						setListViewData(homeBean);
					} else {
						String message = json.getString(VpConstants.HttpKey.MSG);
						Toast.makeText(CourseHomeActivity.this, message, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(CourseHomeActivity.this, "网络访问异常", Toast.LENGTH_SHORT).show();

			}
		});

	}

	protected void setListViewData(CourseOnlineBeanVo homeBeanVo) {
		mDatas=new ArrayList<CourseOnlineBean>();
		if(homeBeanVo!=null){
			this.user_num=homeBeanVo.getUser_num();
			
			CourseOnlineBean bean=null;
			//课程列表
			for(int i=0;i<homeBeanVo.getLessons().size();i++){
				bean=new CourseOnlineBean();
				Lesson lesson = homeBeanVo.getLessons().get(i);
				bean.setType(TYPE_LESSON);
				bean.setId(lesson.getId());
				bean.setName(lesson.getName());
				bean.setPic(lesson.getPic());
				bean.setPrice(lesson.getPrice());
				bean.setRebate_day(lesson.getRebate_day());
				bean.setRemark(lesson.getRemark());
				bean.setUser_num(lesson.getUser_num());
				bean.setIs_official(lesson.getIs_official());
				bean.setOriginal_price(lesson.getOriginal_price());
				mDatas.add(bean);
			}
			
			CourseOnlineBean authorBean=new CourseOnlineBean();
			authorBean.setType(TYPE_DESC);
			authorBean.setName("官方担保课程");
			CourseOnlineBean userCountBean=new CourseOnlineBean();
			userCountBean.setType(TYPE_USER_COUNT);
			userCountBean.setName("累计报名"+this.user_num+"人");
			mDatas.add(authorBean);
			mDatas.add(userCountBean);
			
			//课程列表
			for(int i=0;i<homeBeanVo.getUsers().size();i++){
				bean=new CourseOnlineBean();
				User user = homeBeanVo.getUsers().get(i);
				bean.setType(this.TYPE_USER);
				bean.setUid(user.getUid());
				bean.setCreate_time(user.getCreate_time());
				bean.setLesson_name(user.getLesson_name());
				bean.setNickname(user.getNickname());
				bean.setPortrait(user.getPortrait());
				bean.setPrice(user.getPrice());
				mDatas.add(bean);
			}
			
		}else{
			CourseOnlineBean authorBean=new CourseOnlineBean();
			authorBean.setType(this.TYPE_DESC);
			authorBean.setName("官方担保课程");
			CourseOnlineBean userCountBean=new CourseOnlineBean();
			userCountBean.setType(this.TYPE_USER_COUNT);
			userCountBean.setName("累计报名"+this.user_num+"人");
			mDatas.add(authorBean);
			mDatas.add(userCountBean);
			
		}
		this.mAdapter.notifyDataSetChanged();
	}

	private class CourseHomeAdapter extends BaseAdapter {
		private boolean isFirstUserView=true;
		@Override
		public int getCount() {
			if (mDatas != null)
				return mDatas.size();
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getItemViewType(int position) {
			return mDatas.get(position).getType();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int currentType = getItemViewType(position);
			CourseOnlineBean bean = mDatas.get(position);
			
			if(currentType==TYPE_LESSON){
				LessonViewHolder lessHolder=null;
				if(convertView==null || !(convertView.getTag() instanceof LessonViewHolder)){
					lessHolder=new LessonViewHolder();
					convertView=View.inflate(CourseHomeActivity.this, R.layout.discover_home_item_view_lesson, null);
					lessHolder.ivIcon=(ImageView) convertView.findViewById(R.id.discover_lesson_icon_left);
					lessHolder.tvTitle=(TextView) convertView.findViewById(R.id.discover_lesson_title);
					lessHolder.tvPrice=(TextView) convertView.findViewById(R.id.discover_lesson_price);
					lessHolder.tvListener=(TextView) convertView.findViewById(R.id.discover_lesson_listener);
					lessHolder.tvRemark=(TextView) convertView.findViewById(R.id.discover_lesson_remark);
					lessHolder.ivCourseRemarkIcon=(TextView)convertView.findViewById(R.id.discover_lesson_seven);
					lessHolder.emptyTop=convertView.findViewById(R.id.discover_lesson_empty_top);
					lessHolder.emptyBottom=convertView.findViewById(R.id.discover_lesson_empty_bottom);
					lessHolder.flagContainer = (LinearLayout) convertView.findViewById(R.id.discover_lesson_container);
					convertView.setTag(lessHolder);
				}else{
					lessHolder=(LessonViewHolder) convertView.getTag();
				}
				
				//是否是官方课程
				if (bean.getIs_official() == 1) {//是否官方课程,1=是，0=不是
					//TODO:
				}
				
				//setvalue
				ImageLoader.getInstance().displayImage(bean.getPic(), lessHolder.ivIcon, options);
				lessHolder.tvTitle.setText(bean.getName());
				lessHolder.tvPrice.setText("￥"+bean.getPrice());
				lessHolder.tvListener.setText(bean.getUser_num()+"人在学");
				//lessHolder.tvRemark.setText(bean.getRemark());
				if(bean.getRebate_day()>0){
					lessHolder.flagContainer.setVisibility(View.VISIBLE);
					lessHolder.ivCourseRemarkIcon.setVisibility(View.VISIBLE);
					lessHolder.ivCourseRemarkIcon.setText(bean.getRebate_day()+"");
					lessHolder.tvRemark.setText(bean.getRemark());
				}else{
					lessHolder.flagContainer.setVisibility(View.INVISIBLE);
				}
				
				if(position==0){
					lessHolder.emptyTop.setVisibility(View.VISIBLE);
				}else{
					lessHolder.emptyTop.setVisibility(View.GONE);	
				}
				try {
					if(mDatas.get(position+1).getType()==TYPE_DESC){
						lessHolder.emptyBottom.setVisibility(View.VISIBLE);
					}
				} catch (Exception e) {
					lessHolder.emptyBottom.setVisibility(View.GONE);
				}
						
			}else if(currentType==TYPE_DESC){
				convertView=View.inflate(CourseHomeActivity.this, R.layout.discover_home_item_authority_view, null);
			}else if(currentType==TYPE_USER_COUNT){			
				convertView=View.inflate(CourseHomeActivity.this, R.layout.discover_pua_item_title, null);
				TextView tv=(TextView) convertView.findViewById(R.id.discover_pua_item_title);
				tv.setText("累计报名"+user_num+"人");
			}else if(currentType==TYPE_USER){
				UserViewHolder userHolder=null;
				if(convertView==null || !(convertView.getTag() instanceof UserViewHolder) ){
					userHolder=new UserViewHolder();
					convertView=View.inflate(CourseHomeActivity.this, R.layout.discover_home_item_view_user, null);
//					convertView.setBackground(null);
					userHolder.ivIcon=(CircleImageView) convertView.findViewById(R.id.discover_user_icon_left);
					userHolder.tvNickName=(TextView) convertView.findViewById(R.id.discover_user_nickname);
					userHolder.tvLessonName=(TextView) convertView.findViewById(R.id.discover_user_lesson_name);
					userHolder.tvTime=(TextView) convertView.findViewById(R.id.discover_user_time);
					userHolder.tvPrice=(TextView) convertView.findViewById(R.id.discover_user_price);
					userHolder.mEmptyView=convertView.findViewById(R.id.discover_empty_view);
					userHolder.mLineView=convertView.findViewById(R.id.discover_user_line);
					convertView.setTag(userHolder);
				}else{
					userHolder=(UserViewHolder) convertView.getTag();
				}
				//setvalue
				ImageLoader.getInstance().displayImage(bean.getPortrait(), userHolder.ivIcon, options);
				userHolder.tvNickName.setText(bean.getNickname());
				userHolder.tvLessonName.setText("购买: "+bean.getLesson_name());
				userHolder.tvTime.setText(changeFormatTime(bean.getCreate_time()));
				userHolder.tvPrice.setText("支付： ￥"+bean.getPrice());
				
				if(position==mDatas.size()-1){
					userHolder.mEmptyView.setVisibility(View.VISIBLE);
				}else{
					userHolder.mEmptyView.setVisibility(View.GONE);
				}
				
				try {
					if((mDatas.get(position-1)).getType()==TYPE_USER_COUNT){
						userHolder.mLineView.setVisibility(View.GONE);
					}else{
						userHolder.mLineView.setVisibility(View.VISIBLE);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}			
			return convertView;
		}

	}
	/**
	 * //TODO 返回的是时间戳，需要转换
	 * @param create_time
	 * @return
	 */
	public String changeFormatTime(String create_time) {
		
		return VpDateUtils.getStandardDate(create_time);
	}
	
	
	class LessonViewHolder{
		public ImageView ivIcon;
		public TextView ivCourseRemarkIcon;
		public TextView tvTitle;
		public TextView tvPrice;
		public TextView tvListener;
		public TextView tvRemark;
		public View emptyTop;
		public View emptyBottom;
		public LinearLayout flagContainer;
	}
	class UserViewHolder{
		public CircleImageView ivIcon;
		public TextView tvNickName;
		public TextView tvLessonName;
		public TextView tvTime;
		public TextView tvPrice;
		public View mEmptyView;
		public View mLineView;
	}

	
	
}
