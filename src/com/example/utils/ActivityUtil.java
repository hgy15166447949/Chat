package com.example.utils;

import android.content.Context;
import android.content.Intent;

public class ActivityUtil {
	public static void goToActivity(Context context, Class<?> cls) {
		Intent intent = new Intent();
		intent.setClass(context, cls);
		context.startActivity(intent);

	}

}
