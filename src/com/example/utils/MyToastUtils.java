package com.example.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;


public class MyToastUtils {
	private static Toast mToast;
	
	public static void show(Context context,String text){
		int height = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();
		if(null == mToast){
			mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		}
		mToast.setDuration(Toast.LENGTH_SHORT);
		mToast.setText(text);
		mToast.setGravity(Gravity.TOP, 0, 3*height/4);
		mToast.show();
	}
}
