package com.example.fragment;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class JSToast {
	private Context context;

	public JSToast(Context context) {
		super();
		this.context = context;
	}

	@JavascriptInterface
	public void javaScriptInter(String name) {
		Toast.makeText(context, name, 0).show();
	}

}
