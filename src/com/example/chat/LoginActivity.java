package com.example.chat;

import cn.pedant.SweetAlert.SweetAlertDialog;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.example.utils.CustomerInformation;
import com.example.utils.MyToastUtils;
import com.example.utils.ShareUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity implements OnClickListener {

	private Button bt_login;
	private Button bt_register;
	private AutoCompleteTextView et_name;
	private EditText et_pwd;
	private SweetAlertDialog pDialog;
	private int i = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		//设置自动登陆
		EMChat.getInstance().setAutoLogin(false);
		initSDK();

		initView();
		initData();
	}

	/**
	 * 初始化环信SDK
	 */
	private void initSDK() {
		EMChat.getInstance().init(getApplicationContext());

	}

	private void initData() {

	}

	/**
	 * 初始化布局
	 */
	private void initView() {
		bt_login = (Button) findViewById(R.id.bt_login);
		bt_register = (Button) findViewById(R.id.bt_register);
		et_name = (AutoCompleteTextView) findViewById(R.id.et_name);
		initAutoComplete("fastLoad", et_name);
		
		et_pwd = (EditText) findViewById(R.id.et_pwd);
		bt_login.setOnClickListener(this);
		bt_register.setOnClickListener(this);
	}
	
	private void initAutoComplete(String field, AutoCompleteTextView auto) {
		try {
			if (TextUtils.isEmpty(auto + "")) {
				return;
			}
			SharedPreferences sp = getSharedPreferences("fastLoading", 0);
			String longhistory = sp.getString(field, "");
			String[] hisArrays = longhistory.split(",");

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					getApplicationContext(),
					R.layout.simple_dropdown_item_1line, hisArrays);
			// 只保留最近的50条的记录
			if (hisArrays.length > 50) {
				String[] newArrays = new String[50];
				System.arraycopy(hisArrays, 0, newArrays, 0, 50);
				adapter = new ArrayAdapter<String>(getApplicationContext(),
						R.layout.simple_dropdown_item_1line, newArrays);
			}
			auto.setAdapter(adapter);
			auto.setDropDownHeight(350);
			auto.setThreshold(1);
			auto.setDropDownVerticalOffset(1);
			auto.setDropDownHorizontalOffset(2);
			auto.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					AutoCompleteTextView view = (AutoCompleteTextView) v;
					if (hasFocus) {
						view.dismissDropDown();
					} else {
						view.showDropDown();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private void saveHistory(String field, AutoCompleteTextView auto) {
		String text = auto.getText().toString();
		SharedPreferences sp = getSharedPreferences("fastLoading", 0);
		String longhistory = sp.getString(field, " ");
		if (!longhistory.contains(text + ",")) {
			StringBuilder sb = new StringBuilder(longhistory);
			sb.insert(0, text + ",");
			sp.edit().putString("fastLoad", sb.toString()).commit();
		}
	}
	
	public void progress() {
		pDialog = new SweetAlertDialog(this,
				SweetAlertDialog.PROGRESS_TYPE).setTitleText("Loading");
		pDialog.show();
		pDialog.setCancelable(false);
		pDialog.showCancelButton(true);
		new CountDownTimer(800 * 7, 800) {
			public void onTick(long millisUntilFinished) {
				// you can change the progress bar color by ProgressHelper every
				// 800 millis
				i++;
				switch (i) {
				case 0:
					pDialog.getProgressHelper().setBarColor(
							getResources().getColor(R.color.blue_btn_bg_color));
					break;
				case 1:
					pDialog.getProgressHelper().setBarColor(
							getResources().getColor(
									R.color.material_deep_teal_50));
					break;
				case 2:
					pDialog.getProgressHelper().setBarColor(
							getResources().getColor(
									R.color.success_stroke_color));
					break;
				case 3:
					pDialog.getProgressHelper().setBarColor(
							getResources().getColor(
									R.color.material_deep_teal_20));
					break;
				case 4:
					pDialog.getProgressHelper().setBarColor(
							getResources().getColor(
									R.color.material_blue_grey_80));
					break;
				case 5:
					pDialog.getProgressHelper().setBarColor(
							getResources().getColor(
									R.color.warning_stroke_color));
					break;
				case 6:
					pDialog.getProgressHelper().setBarColor(
							getResources().getColor(
									R.color.success_stroke_color));
					break;
				}
			}

			@Override
			public void onFinish() {
				  i = -1;  
                 

			}
		}.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_login:
			String name = et_name.getText().toString();
			String pwd = et_pwd.getText().toString();
			progress();
			// 如果用户名密码不为空 登陆请求
			if (TextUtils.isEmpty(name)) {
				 pDialog.setTitleText("用户名不能为空!")  
	             .setConfirmText("OK")  
	             .changeAlertType(SweetAlertDialog.WARNING_TYPE); 
				MyToastUtils.show(LoginActivity.this, "用户名不能为空");
				return;
			} else if (TextUtils.isEmpty(pwd)) {
				 pDialog.setTitleText("密码不能为空!")  
	             .setConfirmText("OK")  
	             .changeAlertType(SweetAlertDialog.ERROR_TYPE); 
				MyToastUtils.show(LoginActivity.this, "密码不能为空");
				return;
			} else {
				Loading(name, pwd);
			}
			break;

		case R.id.bt_register:
			startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
			finish();
			break;

		default:
			break;
		}

	}

	private void Loading(String name, String pwd) {
		EMChatManager.getInstance().login(name, pwd, new EMCallBack() {// 回调
					@Override
					public void onSuccess() {
						runOnUiThread(new Runnable() {
							public void run() {
								EMGroupManager.getInstance().loadAllGroups();
								EMChatManager.getInstance()
										.loadAllConversations();
								MyToastUtils.show(LoginActivity.this,
										"登陆聊天服务器成功！");
								saveHistory("fastLoad", et_name);
								 pDialog.dismiss();
								 boolean state = ShareUtils.getStates(LoginActivity.this);
								 if(state == true){
									 startActivity(new Intent(LoginActivity.this,
												UnLockActivity.class));
									 finish();
								 }else{
									 
									 startActivity(new Intent(LoginActivity.this,
											 TalkingActivity.class));
									 finish();
								 }
							}
						});
					}

					@Override
					public void onProgress(int progress, String status) {

					}

					@Override
					public void onError(int code, String message) {
						runOnUiThread(new Runnable() {
							public void run() {
								
								 pDialog.setTitleText("登陆服务器失败!")  
					             .setConfirmText("OK")  
					             .changeAlertType(SweetAlertDialog.ERROR_TYPE); 
								MyToastUtils.show(LoginActivity.this,
										"登陆聊天服务器失败！请核对用户名密码");
							}
						});
					}
				});

	}

}
