package com.example.bean;

public class TalkInfo {

	// 消息
	private String message;
	// 标识
	private String flag;

	private String msgId;
	//图片路径
	private String imagePath;
	//图片uri
	private String imageUri;
	public String getImageUri() {
		return imageUri;
	}

	public void setImageUri(String imageUri) {
		this.imageUri = imageUri;
	}

	//语音路径
	private String voicePath;
	

	public String getVoicePath() {
		return voicePath;
	}

	public void setVoicePath(String voicePath) {
		this.voicePath = voicePath;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public TalkInfo() {
		super();
	}

	public TalkInfo(String message, String flag) {
		super();
		this.message = message;
		this.flag = flag;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
