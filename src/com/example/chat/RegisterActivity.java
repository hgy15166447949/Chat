package com.example.chat;

import cn.pedant.SweetAlert.SweetAlertDialog;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.example.utils.MyToastUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends Activity implements OnClickListener {

	private int i = -1;
	private EditText et_name;
	private EditText et_pwd;
	private EditText et_repwd;
	private Button bt_register;
	private SweetAlertDialog pDialog;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 1:
				startActivity(new Intent(RegisterActivity.this,
						LoginActivity.class));
				finish();
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initView();
		initData();

	}

	private void initView() {
		et_name = (EditText) findViewById(R.id.re_et_name);
		et_pwd = (EditText) findViewById(R.id.re_et_pwd);
		et_repwd = (EditText) findViewById(R.id.re_et_repwd);
		bt_register = (Button) findViewById(R.id.re_bt_register);
		bt_register.setOnClickListener(this);

	}

	private void initData() {
		// TODO Auto-generated method stub

	}

	//展示progress的dialog
	public void progress() {
		pDialog = new SweetAlertDialog(this,
				SweetAlertDialog.PROGRESS_TYPE).setTitleText("Loading");
		pDialog.show();
		pDialog.setCancelable(false);
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
		case R.id.re_bt_register:

			progress();
			String name = et_name.getText().toString();
			String pwd = et_pwd.getText().toString();
			String repwd = et_repwd.getText().toString();
			CheckPwd(name, pwd, repwd);

			break;

		default:
			break;
		}

	}

	private void CheckPwd(String name, String pwd, String repwd) {
		if (TextUtils.isEmpty(name)) {
			 pDialog.setTitleText("用户名为空!")  
             .setConfirmText("OK")  
             .changeAlertType(SweetAlertDialog.ERROR_TYPE); 
			MyToastUtils.show(RegisterActivity.this, "用户名为空");
			return;
		} else if (TextUtils.isEmpty(pwd)) {
			 pDialog.setTitleText("密码为空!")  
             .setConfirmText("OK")  
             .changeAlertType(SweetAlertDialog.ERROR_TYPE); 
			MyToastUtils.show(RegisterActivity.this, "密码为空");
			return;
		} else if (TextUtils.isEmpty(repwd)) {
			 pDialog.setTitleText("再次输入密码为空!")  
             .setConfirmText("OK")  
             .changeAlertType(SweetAlertDialog.ERROR_TYPE); 
			MyToastUtils.show(RegisterActivity.this, "再次输入密码为空");
			return;
		} else {
			if (pwd.equals(repwd)) {
				Register(name, pwd);
			} else {
				 pDialog.setTitleText("两次输入的密码不一致!")  
	             .setConfirmText("OK")  
	             .changeAlertType(SweetAlertDialog.ERROR_TYPE); 
				MyToastUtils.show(RegisterActivity.this, "两次输入的密码不一致，请重新输入");
				return;
			}
		}

	}

	private void Register(final String name, final String pwd) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					// 调用sdk注册方法
					EMChatManager.getInstance()
							.createAccountOnServer(name, pwd);
					runOnUiThread(new Runnable() {
						public void run() {

							 pDialog.setTitleText("恭喜您,注册成功!")  
	                          .setConfirmText("OK")  
	                          .changeAlertType(SweetAlertDialog.SUCCESS_TYPE); 
							MyToastUtils.show(RegisterActivity.this, "注册成功");
						}
					});
					handler.sendEmptyMessageDelayed(1, 500);
				} catch (final EaseMobException e) {
					// 注册失败
					final int errorCode = e.getErrorCode();
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if (errorCode == EMError.NONETWORK_ERROR) {
								 pDialog.setTitleText("网络异常，请检查网络！")  
					             .setConfirmText("OK")  
					             .changeAlertType(SweetAlertDialog.ERROR_TYPE); 
								MyToastUtils.show(RegisterActivity.this,
										"网络异常，请检查网络！");

							} else if (errorCode == EMError.USER_ALREADY_EXISTS) {
								 pDialog.setTitleText("用户已存在！")  
					             .setConfirmText("OK")  
					             .changeAlertType(SweetAlertDialog.ERROR_TYPE); 
								MyToastUtils.show(RegisterActivity.this,
										"用户已存在！");
							} else if (errorCode == EMError.UNAUTHORIZED) {

								 pDialog.setTitleText("注册失败，无权限！")  
					             .setConfirmText("OK")  
					             .changeAlertType(SweetAlertDialog.ERROR_TYPE); 
								MyToastUtils.show(RegisterActivity.this,
										"注册失败，无权限！");
							} else {

								 pDialog.setTitleText("注册失败: " + e.getMessage())  
					             .setConfirmText("OK")  
					             .changeAlertType(SweetAlertDialog.ERROR_TYPE); 
								MyToastUtils.show(RegisterActivity.this,
										"注册失败: " + e.getMessage());
							}
						}
					});
				}

			}
		}).start();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(null);
	}
}
