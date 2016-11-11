package com.vp.loveu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.Button;
import android.widget.Toast;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.google.gson.Gson;
import com.vp.loveu.base.VpActivity;

/**
 * @author：pzj
 * @date: 2015-11-5 下午5:34:16
 * @Description:
 */
public class JsWebviewBridgeDemo extends VpActivity implements OnClickListener {

	private final String TAG = "MainActivity";

	BridgeWebView webView;

	Button button;

	int RESULT_CODE = 0;

	ValueCallback<Uri> mUploadMessage;

    static class Location {
        String address;
    }

    static class User {
        String name;
        Location location;
        String testStr;
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jswebview);

        webView = (BridgeWebView) findViewById(R.id.webView);

		button = (Button) findViewById(R.id.button);

		button.setOnClickListener(this);

		webView.setDefaultHandler(new DefaultHandler());

		webView.setWebChromeClient(new WebChromeClient() {

			@SuppressWarnings("unused")
			public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType, String capture) {
				this.openFileChooser(uploadMsg);
			}

			@SuppressWarnings("unused")
			public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType) {
				this.openFileChooser(uploadMsg);
			}

			public void openFileChooser(ValueCallback<Uri> uploadMsg) {
				mUploadMessage = uploadMsg;
				pickFile();
			}
		});

//		webView.loadUrl("file:///android_asset/demo.html");
		webView.loadUrl("file:///android_asset/js_bridge.html");


//		webView.loadUrl("http://www.baidu.com");
		webView.registerHandler("submitFromWeb", new BridgeHandler() {

			@Override
			public void handler(String data, CallBackFunction function) {
				Log.i(TAG, "handler = submitFromWeb, data from web = " + data);
                function.onCallBack("submitFromWeb exe, response data from Java");
			}

		});
		
		webView.registerHandler("add_favorite", new BridgeHandler() {

			@Override
			public void handler(final String data, final CallBackFunction function) {
				Toast.makeText(JsWebviewBridgeDemo.this, "js_bridge传递的参数"+data, 0).show();
				
				new Thread(){
					
					public void run() {
						SystemClock.sleep(3000);
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								Toast.makeText(JsWebviewBridgeDemo.this, "3s到了", 0).show();
								function.onCallBack(data);
								
							}
						});

					};
				}.start();
			}

		});
		webView.registerHandler("remove_favorite", new BridgeHandler() {
			
			@Override
			public void handler(String data, CallBackFunction function) {
				Toast.makeText(JsWebviewBridgeDemo.this, "js_bridge传递的参数"+data, 0).show();
				function.onCallBack(data);
			}
			
		});

        User user = new User();
        Location location = new Location();
        location.address = "SDU";
        user.location = location;
        user.name = "Bruce";

        webView.callHandler("functionInJs", new Gson().toJson(user), new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
				Log.i(TAG, "onCallBack = " + data);
            }
        });

        webView.send("hello");

	}

	public void pickFile() {
		Intent chooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
		chooserIntent.setType("image/*");
		startActivityForResult(chooserIntent, RESULT_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == RESULT_CODE) {
			if (null == mUploadMessage){
				return;
			}
			Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;
		}
	}

	@Override
	public void onClick(View v) {
		if (button.equals(v)) {
            webView.callHandler("functionInJs", "data from Java", new CallBackFunction() {

				@Override
				public void onCallBack(String data) {
					// TODO Auto-generated method stub
					Log.i(TAG, "reponse data from js " + data);
				}

			});
		}

	}

}
