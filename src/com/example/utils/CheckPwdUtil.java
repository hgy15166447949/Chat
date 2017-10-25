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
			MyToastUtils.show(context, "�û���λ��");
			return false;
		} else if (TextUtils.isEmpty(pwd)) {
			MyToastUtils.show(context, "����λ��");
			return false;
		} else if (TextUtils.isEmpty(repwd)) {
			MyToastUtils.show(context, "�ٴ���������λ��");
			return false;
		} else {
			if (!pwd.equals(repwd)) {
				MyToastUtils.show(context, "������������벻һ�£�����������");
				return false;
			}
		}
		return true;

	}

	public static boolean CheckPwd(String name, String pwd, Context context) {
		if (TextUtils.isEmpty(name)) {
			MyToastUtils.show(context, "�û���λ��");
			return false;
		} else if (TextUtils.isEmpty(pwd)) {
			MyToastUtils.show(context, "����λ��");
			return false;
		}
		return true;

	}

}
