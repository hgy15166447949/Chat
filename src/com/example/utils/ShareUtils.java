package com.example.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ShareUtils {
	private static SharedPreferences sp;
	private static Editor edit;
	
	public static void putStates(Context context,boolean state){
		sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
		edit = sp.edit();
		edit.putBoolean("gesture_state", state);
		edit.commit();
	}
	
	public static boolean getStates(Context context){
		sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
		return sp.getBoolean("gesture_state", false);
	}
}

