package com.vp.loveu.my.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResultParseUtil;
import com.loopj.android.http.VpHttpClient;
import com.vp.loveu.R;
import com.vp.loveu.base.VpActivity;
import com.vp.loveu.bean.InwardAction;
import com.vp.loveu.comm.VpConstants;
import com.vp.loveu.index.myutils.CacheFileUtils;
import com.vp.loveu.index.myutils.MyUtils;
import com.vp.loveu.index.widget.EmptyTextView;
import com.vp.loveu.login.bean.LoginUserInfoBean;
import com.vp.loveu.my.bean.ExpHistoryBean;
import com.vp.loveu.my.bean.NewIntergralBean;
import com.vp.loveu.my.bean.NewIntergralBean.NewIntergralDataBean;
import com.vp.loveu.my.widget.IntegralHeadView;
import com.vp.loveu.my.widget.WalletBottomItemRelativeLayout;
import com.vp.loveu.util.LoginStatus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;

/**
 * @项目名称nameloveu1.0
 * 
 * @时间2015年12月1日下午2:25:30
 * @功能 积分的界面 1.2版本
 * @作者 mi
 */

public class IntergralActivity extends VpActivity implements OnClickListener, OnRefreshListener2<ListView> {

	public static final String TAG = "IntergralActivity";
	public static final String ACTION = "update_view";

	private ImageView mIvBack;
	private TextView mTvRule;
	private TextView mTvIntergral;
	private TextView mTvRanking;
	private TextView mTvAlias;
	private PullToRefreshListView mListView;
	public final static String KEY_RANKING = "ranking";// 排名
	public final static String KEY_GRADE = "grade";// 等级
	public final static String KEY_INTERGRAL = "intergral";// 积分
	public static final String[] names = new String[] { "登陆", "发表评论", "情感解答", "发表求助", "点赞" };

	public int totalExp = 10000;// 总积分
	public int animExp;// 动画积分
	public int ranking;
	public String grade;
	List<ExpHistoryBean> exps;
	private Gson gson = new Gson();

	private int mLimit = 10;
	private int mPage = 1;
	// private MyAdapter mAdapter; 1.0版本的
	private NewAdapter newAdapter;
	private static final String FILE_NAME = "IntergralActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		grade = intent.getStringExtra(KEY_GRADE);// 等级
		ranking = intent.getIntExtra(KEY_RANKING, -1);// 排名
		totalExp = intent.getIntExtra(KEY_INTERGRAL, -1);// 积分
		setContentView(R.layout.intergral_activity);
		initView();
		initData();
		if (totalExp > 1000) {
			totalExp = 1000; // 为了动画效果
		} else if (totalExp < 0 && totalExp < -999) {
			totalExp = -999;
		}

		// 注册广播
		IntentFilter filter = new IntentFilter(ACTION);
		registerReceiver(receiver, filter);
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			NewIntergralDataBean bean = (NewIntergralDataBean) intent.getSerializableExtra("obj");
			if (bean == null) {
				return;
			}
			int indexOf = mIndexListData.indexOf(bean);
			if (indexOf != -1) {
				mIndexListData.remove(bean);
				bean.is_retrieved = 1;
				mIndexListData.add(indexOf, bean);
				newAdapter.notifyDataSetChanged();
				// 做动画
				totalExp += bean.exp;
				if (totalExp > 1000) {
					totalExp = 1000; // 为了动画效果
				} else if (totalExp < 0 && totalExp < -999) {
					totalExp = -999;
				}
				mTvIntergral.setText(totalExp + "");
			}
		}
	};

	private float progress = 100; // 一圈360的进度
	private float tmpProgress = 0;
	Handler timeHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			tmpProgress++;
			if (tmpProgress >= progress) {
				tmpProgress = progress;
				if (totalExp >= 1000) {
					mTvIntergral.setText("999+");
				} else if (totalExp <= -999) {
					mTvIntergral.setText("-999+");
				} else {
					mTvIntergral.setText(totalExp + "");
				}
				return;
			}

			animExp = (int) (totalExp * tmpProgress * 1.0 / 100);
			if (animExp >= 1000) {
				mTvIntergral.setText("999+");
			} else {
				mTvIntergral.setText(animExp + "");
			}

			timeHandler.postDelayed(timeThread, 20);
		}

	};
	Runnable timeThread = new Runnable() {
		public void run() {
			timeHandler.sendEmptyMessage(0);
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy(); // 取消注册
		unregisterReceiver(receiver);
	}

	private void initData() {
		String readCache = CacheFileUtils.readCache(FILE_NAME);
		if (!TextUtils.isEmpty(readCache)) {
			try {
				setDataIndex(readCache);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (MyUtils.isNetword(this)) {
			startNewHttp();
		}
		timeHandler.sendEmptyMessage(0);// 运行动画
	}

	// 1.0版本的
	// private void setData(String readCache) {
	// try {
	// JSONObject data = new JSONObject(readCache);
	// exps = ExpHistoryBean.createFromJsonArray(data.getString("data"));
	// if (mAdapter == null) {
	// mAdapter = new MyAdapter(this, mListView.getRefreshableView(), exps);
	// mListView.setAdapter(mAdapter);
	// } else {
	// mAdapter.notifyDataSetChanged();
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// timeHandler.sendEmptyMessage(0);// 运行动画
	// }

	/**
	 * 我的积分首页接口
	 */
	private void startNewHttp() {
		// 请求网络
		String url = VpConstants.MY_INTEGERAL;
		LoginUserInfoBean loginuser = LoginStatus.getLoginInfo();
		if (loginuser == null) {
			return;
		}
		if (mClient == null) {
			mClient = new VpHttpClient(this);
		}
		mClient.setShowProgressDialog(true);
		url = url + "/" + loginuser.getUid();
		RequestParams params = new RequestParams();
		params.put("limit", mLimit);
		mClient.get(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				mListView.onRefreshComplete();
				String result = ResultParseUtil.deAesResult(responseBody);
				try {
					JSONObject obj = new JSONObject(result);
					if (obj.optInt("code") == 0) {
						CacheFileUtils.writeCache(FILE_NAME, result);// 写入缓存
						setDataIndex(result);
					} else {
						Toast.makeText(getApplicationContext(), obj.optString("msg"), Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				mListView.onRefreshComplete();
				Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
			}
		});
	}

	// 设置首页数据
	protected void setDataIndex(String result) throws JSONException {
		JSONObject obj = new JSONObject(result);
		mIndexListData = NewIntergralBean.parserJson(obj.optString("data"));
		if (newAdapter == null) {
			newAdapter = new NewAdapter();
			mListView.setAdapter(newAdapter);
		} else {
			newAdapter.notifyDataSetChanged();
		}
	}

	// 1.2版本新的适配器---增加了类型
	private class NewAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			if (mIndexListData != null) {
				return mIndexListData.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (mIndexListData != null) {
				return mIndexListData.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getViewTypeCount() {
			return 3;
		}

		@Override
		public int getItemViewType(int position) {
			return mIndexListData.get(position).type;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			NewIntergralDataBean bean = mIndexListData.get(position);
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = createHolder(type, holder);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			setHolderData(type, bean, holder);

			return convertView;
		}

		private void setHolderData(int type, final NewIntergralDataBean bean, final ViewHolder holder) {
			switch (type) {
			case 0:
				holder.mTvOne.setText(bean.titile);
				break;
			case 1:
				holder.mTvTwoName.setText(bean.remark);
				if (bean.is_retrieved == 1) {// 1=已获取，0=未获取
					holder.mTvTwoIntegral.setText("已完成");
					holder.mIvTwoMore.setVisibility(View.GONE);
					holder.mTvTwoIntegral.setTextColor(Color.parseColor("#9B9B9B"));
				} else {
					holder.mIvTwoMore.setVisibility(View.VISIBLE);
					holder.mTvTwoIntegral.setText(bean.exp > 0 ? "+" + bean.exp : "-" + bean.exp);
					holder.mTvTwoIntegral.setTextColor(Color.parseColor("#10BB7D"));
				}
				// 1=跳转 2=点击触发 2暂时先不管
				holder.mRlContainerTwo.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) { // 分享完成后需要通知回来
						InwardAction parseAction = InwardAction.parseAction(bean.url);
						if (parseAction != null) {
							parseAction.setDataBean(bean);
							parseAction.toStartActivity(IntergralActivity.this);
						}
					}
				});

				break;
			case 2:
				holder.mItemThree.setData(bean);
				break;
			default:
				break;
			}
		}

		public View createHolder(int type, ViewHolder holder) {
			View view = null;
			if (type == 0) {
				View inflate = View.inflate(IntergralActivity.this, R.layout.integer_item_one, null);
				view = inflate;
				holder.mTvOne = (TextView) view.findViewById(R.id.integer_item_one_tv);
			} else if (type == 1) {
				View inflate = View.inflate(IntergralActivity.this, R.layout.my_integral_item_two, null);
				view = inflate;
				holder.mRlContainerTwo = (RelativeLayout) inflate;
				holder.mTvTwoName = (TextView) view.findViewById(R.id.integral_tv_name);
				holder.mTvTwoIntegral = (TextView) view.findViewById(R.id.integral_tv_integral);
				holder.mIvTwoMore = (ImageView) view.findViewById(R.id.integral_iv_more);
			} else if (type == 2) {
				view = new WalletBottomItemRelativeLayout(IntergralActivity.this);
				holder.mItemThree = (WalletBottomItemRelativeLayout) view;
				holder.mItemThree.setTvValuesColor("#9B9B9B");
			}
			return view;
		}

	}

	static class ViewHolder {
		TextView mTvOne;
		TextView mTvTwoName;
		RelativeLayout mRlContainerTwo;
		TextView mTvTwoIntegral;
		ImageView mIvTwoMore;
		WalletBottomItemRelativeLayout mItemThree;
	}

	/**
	 * 我的积分历史接口
	 * 
	 * @param page
	 */
	private void startHttp(final int page) {
		// 请求网络
		String url = VpConstants.MY_EXP_HISTORY;
		LoginUserInfoBean loginuser = LoginStatus.getLoginInfo();
		if (loginuser == null) {
			return;
		}
		if (mClient == null) {
			mClient = new VpHttpClient(this);
		}
		mClient.setShowProgressDialog(false);
		url = url + "/" + loginuser.getUid();
		RequestParams params = new RequestParams();
		params.put("limit", mLimit);
		params.put("page", page);
		mClient.get(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				mListView.onRefreshComplete();
				String result = ResultParseUtil.deAesResult(responseBody);
				try {
					NewIntergralBean obj = gson.fromJson(result, NewIntergralBean.class);
					if (obj.code == 0) {
						// 数据获取成功
						List<NewIntergralDataBean> data2 = obj.data;
						if (data2 != null && data2.size() > 0) {
							for (int i = 0; i < data2.size(); i++) {
								data2.get(i).type = 2;
							}
							mIndexListData.addAll(data2);
							newAdapter.notifyDataSetChanged();
						} else {
							Toast.makeText(getApplicationContext(), R.string.not_more_data, Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(getApplicationContext(), obj.msg, Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				mListView.onRefreshComplete();
				Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private IntegralHeadView mHeadView;
	private List<NewIntergralDataBean> mIndexListData;

	private void initView() {
		mListView = (PullToRefreshListView) findViewById(R.id.integral_pulltorefreshlistview);
		mHeadView = new IntegralHeadView(this);
		mIvBack = (ImageView) findViewById(R.id.intergral_iv_back);
		mTvRule = (TextView) findViewById(R.id.intergral_tv_rule);
		mTvIntergral = (TextView) mHeadView.findViewById(R.id.intergral_tv_intergral);
		mTvRanking = (TextView) mHeadView.findViewById(R.id.intergral_tv_ranking);
		mTvAlias = (TextView) mHeadView.findViewById(R.id.intergral_tv_alias);
		mListView.setMode(Mode.BOTH);
		mListView.setOnRefreshListener(this);
		EmptyTextView tv = new EmptyTextView(this);
		tv.setWidth(MyUtils.getScreenWidthAndHeight(this)[0]);
		tv.setHeight(MyUtils.getScreenWidthAndHeight(this)[1]);
		tv.setGravity(Gravity.CENTER);
		tv.setText("你还没有任何积分");
		mListView.setEmptyView(tv);
		mListView.getRefreshableView().addHeaderView(mHeadView);
		mTvRanking.setText(ranking + "位");
		mTvAlias.setText(grade);
		mIvBack.setOnClickListener(this);
		mTvRule.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.intergral_iv_back:
			finish();
			break;
		case R.id.intergral_tv_rule:
			// 规则
			InwardAction.parseAction(VpConstants.INTERGRAL).toStartActivity(this);
			break;

		default:
			break;
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		startNewHttp();
		mPage = 1;
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		startHttp(++mPage);
	}

	// 1.0版本的
	// private class MyAdapter extends VpBaseAdapter<ExpHistoryBean> {
	//
	// public MyAdapter(Context context, AbsListView listView,
	// List<ExpHistoryBean> data) {
	// super(context, listView, data);
	// }
	//
	// @Override
	// public BaseHolder<ExpHistoryBean> getHolder() {
	// return new IntergralHolder(IntergralActivity.this);
	// }
	// }
}
