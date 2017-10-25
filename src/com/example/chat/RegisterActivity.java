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

	//չʾprogress��dialog
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
			 pDialog.setTitleText("�û���Ϊ��!")  
             .setConfirmText("OK")  
             .changeAlertType(SweetAlertDialog.ERROR_TYPE); 
			MyToastUtils.show(RegisterActivity.this, "�û���Ϊ��");
			return;
		} else if (TextUtils.isEmpty(pwd)) {
			 pDialog.setTitleText("����Ϊ��!")  
             .setConfirmText("OK")  
             .changeAlertType(SweetAlertDialog.ERROR_TYPE); 
			MyToastUtils.show(RegisterActivity.this, "����Ϊ��");
			return;
		} else if (TextUtils.isEmpty(repwd)) {
			 pDialog.setTitleText("�ٴ���������Ϊ��!")  
             .setConfirmText("OK")  
             .changeAlertType(SweetAlertDialog.ERROR_TYPE); 
			MyToastUtils.show(RegisterActivity.this, "�ٴ���������Ϊ��");
			return;
		} else {
			if (pwd.equals(repwd)) {
				Register(name, pwd);
			} else {
				 pDialog.setTitleText("������������벻һ��!")  
	             .setConfirmText("OK")  
	             .changeAlertType(SweetAlertDialog.ERROR_TYPE); 
				MyToastUtils.show(RegisterActivity.this, "������������벻һ�£�����������");
				return;
			}
		}

	}

	private void Register(final String name, final String pwd) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					// ����sdkע�᷽��
					EMChatManager.getInstance()
							.createAccountOnServer(name, pwd);
					runOnUiThread(new Runnable() {
						public void run() {

							 pDialog.setTitleText("��ϲ��,ע��ɹ�!")  
	                          .setConfirmText("OK")  
	                          .changeAlertType(SweetAlertDialog.SUCCESS_TYPE); 
							MyToastUtils.show(RegisterActivity.this, "ע��ɹ�");
						}
					});
					handler.sendEmptyMessageDelayed(1, 500);
				} catch (final EaseMobException e) {
					// ע��ʧ��
					final int errorCode = e.getErrorCode();
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if (errorCode == EMError.NONETWORK_ERROR) {
								 pDialog.setTitleText("�����쳣���������磡")  
					             .setConfirmText("OK")  
					             .changeAlertType(SweetAlertDialog.ERROR_TYPE); 
								MyToastUtils.show(RegisterActivity.this,
										"�����쳣���������磡");

							} else if (errorCode == EMError.USER_ALREADY_EXISTS) {
								 pDialog.setTitleText("�û��Ѵ��ڣ�")  
					             .setConfirmText("OK")  
					             .changeAlertType(SweetAlertDialog.ERROR_TYPE); 
								MyToastUtils.show(RegisterActivity.this,
										"�û��Ѵ��ڣ�");
							} else if (errorCode == EMError.UNAUTHORIZED) {

								 pDialog.setTitleText("ע��ʧ�ܣ���Ȩ�ޣ�")  
					             .setConfirmText("OK")  
					             .changeAlertType(SweetAlertDialog.ERROR_TYPE); 
								MyToastUtils.show(RegisterActivity.this,
										"ע��ʧ�ܣ���Ȩ�ޣ�");
							} else {

								 pDialog.setTitleText("ע��ʧ��: " + e.getMessage())  
					             .setConfirmText("OK")  
					             .changeAlertType(SweetAlertDialog.ERROR_TYPE); 
								MyToastUtils.show(RegisterActivity.this,
										"ע��ʧ��: " + e.getMessage());
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
