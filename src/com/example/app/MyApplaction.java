package com.example.app;


import com.example.utils.LockPatternUtils;

import android.app.Application;


public class MyApplaction extends Application{

	private static MyApplaction mInstance;
	private LockPatternUtils mLockPatternUtils;
	private boolean isDownload;

	public static MyApplaction getInstance() {
		return mInstance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		mLockPatternUtils = new LockPatternUtils(this);
		isDownload = false;
	}

	public LockPatternUtils getLockPatternUtils() {
		return mLockPatternUtils;
	}

	public boolean isDownload() {
		return isDownload;
	}

	public void setDownload(boolean isDownload) {
		this.isDownload = isDownload;
	}



}
