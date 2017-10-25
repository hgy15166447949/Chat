package com.example.chat;

import com.example.utils.SmoothImageView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class ImageShowActivity extends Activity {
	private SmoothImageView iv_showpic;
	private LinearLayout ll_picshow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_showpic);

		iv_showpic = (SmoothImageView) findViewById(R.id.iv_showpic);
		ll_picshow = (LinearLayout) findViewById(R.id.ll_picshow);
		String picpath = getIntent().getStringExtra("picpath");
		int mLocationX = getIntent().getIntExtra("locationX", 0);
		int mLocationY = getIntent().getIntExtra("locationY", 0);
		int mWidth = getIntent().getIntExtra("width", 0);
		int mHeight = getIntent().getIntExtra("height", 0);

		iv_showpic.setOriginalInfo(mWidth, mHeight, mLocationX, mLocationY);
		iv_showpic.transformIn();

		iv_showpic.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
		iv_showpic.setScaleType(ScaleType.FIT_CENTER);
		iv_showpic.setImageBitmap(BitmapFactory.decodeFile(picpath));
		ll_picshow.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				finish();
				return false;
			}
		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		overridePendingTransition(0, 0);
		int width = iv_showpic.getWidth();
		int height = iv_showpic.getHeight();
		iv_showpic.setOriginalInfo(width,height,0,0);
		return super.onKeyDown(keyCode, event);
	}

}
