package com.vp.loveu.message.view;

import android.content.Context;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.vp.loveu.R;
import com.vp.loveu.message.bean.ChatEmoji;
import com.vp.loveu.message.utils.FaceConversionUtil;
import com.vp.loveu.message.view.EmojiPopupWindow.OnEmojiItemClick;
import com.vp.loveu.util.VPLog;

public class ChatInputView  extends LinearLayout implements OnEmojiItemClick{

	public static final String TAG ="ChatInputView";
	
 
	EmojiPopupWindow mEmojiPopupWindow;
	PlusPopupWindow mPlusPopupWindow;
	
	private InputMethodManager imm;

	/**
	 * 隐藏和现实的 容器view
	 */
	
	ImageButton mFaceButton;
	
	EditText replyEditDetail;
	boolean  editIsClick;//输入框被点击了
	
	
	Button mBtnSend;
	ImageButton mBtnPlus;
	
	/**
	 * plus 模块
	 */

	public boolean isKeyBoardVisible;
	public LinearLayout parentLayout;

	
	/**
	 * 存放 emoji 和  plus view 的容器
	 */
	public RelativeLayout mContainerView;
	public ChatInputView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}
	public ChatInputView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	public ChatInputView(Context context ) {
		super(context);
		initView();
	}
	
	
	
	
	
	
	/**
	 * init view 
	 */
	private void initView() {
		inflate(getContext(), R.layout.message_chat_input_view, this);
		
			
		if (isInEditMode()) {
			return;
		}
		setBackgroundResource(R.color.face_bg_nomal);
		imm = (InputMethodManager) getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		initEmojiAll();
		
		initPlusView();
	}
	
	
	/**
	 * init plus 模块
	 */
	private void initPlusView() {
		 
		mBtnPlus = (ImageButton) findViewById(R.id.btn_plus);
		mBtnPlus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//show
				if (mPlusPopupWindow==null || mPlusPopupWindow.isShowing() == false ) {
					if (mPlusPopupWindow==null) {
						mPlusPopupWindow = new PlusPopupWindow(getContext());
					}
					
					if (isKeyBoardVisible) {
						mContainerView.setVisibility(GONE);
					}else {
						mContainerView.setVisibility(VISIBLE);
					}
					 
					mPlusPopupWindow.setHeight(mContainerView.getLayoutParams().height);
					mPlusPopupWindow.showAtLocation(parentLayout, Gravity.BOTTOM, 0, 0);
				}else {
					mPlusPopupWindow.dismiss();
					if (!isKeyBoardVisible) {
						mContainerView.setVisibility(GONE);
					}
					
					if (!isKeyBoardVisible) {
						opKeyBoard();
					}
					
				}
				
				if (mEmojiPopupWindow!=null) {
					parentLayout.postDelayed(new  Runnable() {
						public void run() {
							mEmojiPopupWindow.dismiss();
						}
					}, 200);
				}
			}
		});
		
		
		mBtnSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mOnSendListener!=null) {
					mOnSendListener.onSend(replyEditDetail.getText().toString());
					replyEditDetail.setText(null);
				}
			}
		});
	}

	
	private void initEmojiAll() {
		initEmojiView();
	}
	

 
 
	/**
	 * 
	 */
	private void initEmojiView() {
		mFaceButton = (ImageButton) findViewById(R.id.btn_emoji);
		replyEditDetail = (EditText) findViewById(R.id.edit);
		mContainerView  = (RelativeLayout) findViewById(R.id.plus_view_container);
		
		mBtnSend = (Button) findViewById(R.id.btn_send);
		mBtnPlus = (ImageButton) findViewById(R.id.btn_plus);
		
		 
		/*replyEditDetail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				VPLog.d(TAG, "replyEditDetail");
				//被点击了
				//mFaceButton.setChecked(false);
				editIsClick = true;
				mContainerView.setVisibility(GONE);
				mChangeLayout.setVisibility(GONE);
				mPlusGridView.setVisibility(GONE);
			}
		});*/
		
		replyEditDetail.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				VPLog.d(TAG, "touch");
				if (event.getAction() ==MotionEvent.ACTION_DOWN) {
					replyEditDetail.requestFocus();
					if (mEmojiPopupWindow!=null) {
						mEmojiPopupWindow.dismiss();
						mContainerView.setVisibility(GONE);
					}
					if (mPlusPopupWindow!=null) {
						mPlusPopupWindow.dismiss();
						mContainerView.setVisibility(GONE);
					}
				}
				return false;
			}
		});
		
		mBtnSend.setEnabled(false);
		replyEditDetail.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable editable) {
				if (editable.length() == 0 || TextUtils.isEmpty(editable.toString().trim())) {
					mBtnSend.setEnabled(false);
					/*Animation goneAnim=AnimationUtils.loadAnimation(getContext(), R.anim.message_send_alpha_action_gone);
					mBtnSend.startAnimation(goneAnim);
					Animation inAnim=AnimationUtils.loadAnimation(getContext(), R.anim.message_send_alpha_action_in);
					mBtnPlus.startAnimation(inAnim);*/
					
				} else {
					/*if (mBtnSend.getVisibility() == VISIBLE) {
						return;
					}*/
					mBtnSend.setEnabled(true);
					/*Animation inAnim=AnimationUtils.loadAnimation(getContext(), R.anim.message_send_alpha_action_in);
					mBtnSend.startAnimation(inAnim);*/
				}
			}
		});
		
		
		mFaceButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				//没有显示
				if (mEmojiPopupWindow == null || mEmojiPopupWindow.isShowing() == false) {
					if (mEmojiPopupWindow == null) {
						mEmojiPopupWindow = new EmojiPopupWindow(getContext(),ChatInputView.this);
					}
					if (isKeyBoardVisible) {
						mContainerView.setVisibility(GONE);
					}else {
						mContainerView.setVisibility(VISIBLE);
					}
					 
					VPLog.d(TAG, "ppppp");
					mEmojiPopupWindow.setHeight(mContainerView.getLayoutParams().height);
					mEmojiPopupWindow.showAtLocation(parentLayout, Gravity.BOTTOM, 0, 0);
					
				}else {
					mEmojiPopupWindow.dismiss();
					if (!isKeyBoardVisible) {
						mContainerView.setVisibility(GONE);
					}
					if (!isKeyBoardVisible) {
						opKeyBoard();
					}
				}
				if (mPlusPopupWindow!=null) {
					parentLayout.postDelayed(new  Runnable() {
						public void run() {
							mPlusPopupWindow.dismiss();
						}
					}, 200);
				}
			}
		});
		
	}
	
	
	
	/**
	 * 隐藏软键盘
	 */
	public void opKeyBoard() {
		// 隐藏软键盘
		if (imm.isActive()) {
			imm.toggleSoftInput(0,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}

	}

	/**
	 * 强制隐藏软键盘
	 */
	public void hideKeyBoard() {
		if (imm.isActive()) {
			((InputMethodManager) getContext().getSystemService(
					Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
					 replyEditDetail.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * EditTxt获取焦点并弹出软键盘
	 */
	public void getFocusable() {
		opKeyBoard();
	}
	
	
	
	
	/**
	 * send 按钮监听器
	 * @author tanping
	 * 2015-11-10
	 */
	public interface OnSendListener{
		void onSend(String txt);
	}
	
	private OnSendListener mOnSendListener;
	public void setSendClickListener(OnSendListener sendListener){
		this.mOnSendListener = sendListener;
	}
	
	/**
	 * 隐藏编辑模式
	 */
	public void hideEditMode(){
		
		hideKeyBoard();
		if (mContainerView.getVisibility() != GONE) {
			mFaceButton.postDelayed(new Runnable() {
				@Override
				public void run() {
					mContainerView.setVisibility(GONE);
				}
			}, 100);
		}
		
		if (mEmojiPopupWindow!=null) {
			mEmojiPopupWindow.dismiss();
		}
		if (mPlusPopupWindow!=null) {
			mPlusPopupWindow.dismiss();
		}
		if (mPlusPopupWindow!=null) {
			mPlusPopupWindow.dismiss();
		}
	}
	
	public boolean isShowPopup(){
		if (mEmojiPopupWindow!=null && mEmojiPopupWindow.isShowing()) {
			return true;
		}
		
		if (mPlusPopupWindow!=null && mPlusPopupWindow.isShowing()) {
			return true;
		}
		return false;
	}
	
	/*
	 * emoji 
	 * @see com.vp.loveu.message.view.EmojiPopupWindow.OnEmojiItemClick#onEmojiClick(com.vp.loveu.message.bean.ChatEmoji)
	 */
	@Override
	public void onEmojiClick(ChatEmoji emoji) {
		VPLog.d(TAG, "item click :" );
		if (emoji.getId() == R.drawable.face_del_icon) {
			int selection = replyEditDetail.getSelectionStart();
			String text = replyEditDetail.getText().toString();
			if (selection > 0) {
				String text2 = text.substring(selection - 1,selection);
				//VPLog.d(TAG, "text2 :" +text2);

				if ("]".equals(text2)) {
					int start = text.lastIndexOf("[");
					int end = selection;
					//VPLog.d(TAG, "start :" +start +" end:"+end);
					if (start >= end) {
						VPLog.d(TAG, "start :" +start);

						for (int i = end-1; i >=0 ; i--) {
							if (text.charAt(i)=='[') {
								start = i;
								break;
							} 
						}
					}
					if (start == end) {
						start--;
					}
					if (start <0) {
						start =0;
					}
					replyEditDetail.getText().delete(start, end);
					return;
				}
				replyEditDetail.getText().delete(selection - 1, selection);
			}
			
		}
		if (!TextUtils.isEmpty(emoji.getCharacter())) {
			SpannableString spannableString = FaceConversionUtil.getInstace()
					.addFace(getContext(), emoji.getId(), emoji.getCharacter());
			
			replyEditDetail.append(spannableString);
		}
	}
}
