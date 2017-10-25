package com.example.utils;

import com.example.chat.RegisterActivity;

import android.content.Context;
import android.text.TextUtils;

public class CheckPwdUtil {
	// private String name;
	// private String pwd;
	// private String repwd;
	// private Context context;
	//
	// public CheckPwdUtil(String name, String pwd, String repwd, Context
	// context) {
	// this.name = name;
	// this.pwd = pwd;
	// this.repwd = repwd;
	// this.context = context;
	// }

	public static boolean CheckPwd(String name, String pwd, String repwd,
			Context context) {
		if (TextUtils.isEmpty(name)) {
			MyToastUtils.show(context, "用户名位空");
			return false;
		} else if (TextUtils.isEmpty(pwd)) {
			MyToastUtils.show(context, "密码位空");
			return false;
		} else if (TextUtils.isEmpty(repwd)) {
			MyToastUtils.show(context, "再次输入密码位空");
			return false;
		} else {
			if (!pwd.equals(repwd)) {
				MyToastUtils.show(context, "两次输入的密码不一致，请重新输入");
				return false;
			}
		}
		return true;

	}

	public static boolean CheckPwd(String name, String pwd, Context context) {
		if (TextUtils.isEmpty(name)) {
			MyToastUtils.show(context, "用户名位空");
			return false;
		} else if (TextUtils.isEmpty(pwd)) {
			MyToastUtils.show(context, "密码位空");
			return false;
		}
		return true;

	}

}
