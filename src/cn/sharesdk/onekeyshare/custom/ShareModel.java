package cn.sharesdk.onekeyshare.custom;

import java.io.Serializable;

import android.graphics.Bitmap;

/**
 * @author：pzj
 * @date: 2015-11-9 上午11:06:56
 * @Description:分享内容实体
 */
public class ShareModel {
	//标题
	private String title;
	//文本内容
	private String text ;
	//分享url
	private String url;
	//图片url
	private String imageUrl;
	//默认本地图片
	private Bitmap imageLocal;
	
	//分享信息ID, 若分享APP本身ID值为0
	private int id;
	
	//分享类型 999=分享APP本身 1=长文推荐 2=PUA课堂 3=大家都在聊
	private int type;
	
	private String tag;
	private Serializable obj;
	
	
	public Serializable getObj() {
		return obj;
	}
	public void setObj(Serializable obj) {
		this.obj = obj;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public Bitmap getImageLocal() {
		return imageLocal;
	}
	public void setImageLocal(Bitmap imageLocal) {
		this.imageLocal = imageLocal;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}
