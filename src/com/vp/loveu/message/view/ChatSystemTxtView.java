package com.vp.loveu.message.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vp.loveu.R;
import com.vp.loveu.message.bean.ChatMessage;
import com.vp.loveu.message.bean.ChatMessage.MsgShowType;
import com.vp.loveu.util.VpDateUtils;

/**
 * 用户接收到system消息的view
 * 
 * @author tanping
 * 
 */
public class ChatSystemTxtView extends RelativeLayout implements IMsgUpdater {

	private TextView msgBodyView;

	public ChatMessage message;// 消息体

	public ChatSystemTxtView(Context context) {
		super(context);
		initView();
	}

	public ChatSystemTxtView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {
		setGravity(Gravity.CENTER);
		inflate(getContext(), R.layout.message_formclient_chat_timestamp, this);
		msgBodyView = (TextView) findViewById(R.id.timestamp_text);
		
	}

	@Override
	public void setChatData(ChatMessage message) {
		if (this.message!=null) {
			this.message.viewUpdate = null;
		}
		this.message = message;
		drawView();
	}

	@Override
	public void drawView() {
 
		
		if (message.showType == MsgShowType.timestamp.ordinal()) {
			 msgBodyView.setText(VpDateUtils.getStandardDate(message.timestamp+""));
		}
		
		
	}
 
    /**
     * 去掉回车带来的空白字符
     * 
     * @param content
     * @return
     */
    public String replaceBlank(String content) {
	String dest = "";
	if (content != null) {
	    Pattern p = Pattern.compile("\\s*|\t|\r|\n");
	    Matcher m = p.matcher(content);
	    dest = m.replaceAll("");
	}
	return dest;

    }
}
