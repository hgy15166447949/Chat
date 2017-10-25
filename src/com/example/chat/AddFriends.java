package com.example.chat;

import java.util.List;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMNotifier;
import com.easemob.exceptions.EaseMobException;
import com.example.utils.MyToastUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddFriends extends Activity implements OnClickListener {
	private EditText et_addfriends;
	private Button bt_addfriends;
	private Button bt_addback;
	private String toAddUsername;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addfriends);
		// 监听联系人的变化
		EMContactManager.getInstance().setContactListener(
				new MyContactListener());
		initView();
		// 最后要通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
		EMChat.getInstance().setAppInited();
	}

	private void initView() {
		et_addfriends = (EditText) findViewById(R.id.et_addfriends);
		bt_addfriends = (Button) findViewById(R.id.bt_addfriends);
		bt_addback = (Button) findViewById(R.id.bt_addback);
		bt_addback.setOnClickListener(this);
		bt_addfriends.setOnClickListener(this);
	}

	@SuppressLint("HandlerLeak")
	private void AddFriends() {

		toAddUsername = et_addfriends.getText().toString();
		if (toAddUsername != null) {
			HandlerThread thread = new HandlerThread("add");
			thread.start();
			Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					try {
						EMContactManager.getInstance().addContact(
								toAddUsername, "hello");
					} catch (Exception e) {
						e.printStackTrace();
					}
					super.handleMessage(msg);
				}
			};
			handler.sendEmptyMessage(1);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_addfriends:
			AddFriends();

			break;
		case R.id.bt_addback:

//			startActivity(new Intent(AddFriends.this, TalkingActivity.class));
			finish();

			break;

		default:
			break;
		}

	}

	private class MyContactListener implements EMContactListener {

		@Override
		public void onContactAdded(List<String> usernameList) {
			// 保存增加的联系人
			usernameList.add(toAddUsername);

		}

		@Override
		public void onContactDeleted(final List<String> usernameList) {
			// 被删除

		}

		@Override
		public void onContactInvited(String username, String reason) {
			// 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不要重复提醒
			showAgreedDialog(username, reason);
			EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();

		}

		@Override
		public void onContactAgreed(String username) {
			// 同意好友请求

			EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();
			MyToastUtils.show(AddFriends.this, username + "同意了你的请求");

		}

		@Override
		public void onContactRefused(String username) {
			// 拒绝好友请求

		}

	}

	private void showAgreedDialog(final String user, String reason) {
		new AlertDialog.Builder(AddFriends.this)
				.setTitle("应用提示")
				.setMessage(
						"用户 " + user + " 想要添加您为好友，是否同意？\n" + "验证信息：" + reason)
				.setPositiveButton("同意", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							EMChatManager.getInstance().acceptInvitation(user);
							// initList();
						} catch (EaseMobException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Log.i("TAG",
									"showAgreedDialog1==>" + e.getErrorCode());
						}
					}
				})
				.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						try {
							EMChatManager.getInstance().refuseInvitation(user);
						} catch (EaseMobException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Log.i("TAG",
									"showAgreedDialog2==>" + e.getErrorCode());
						}
					}
				})
				.setNeutralButton("忽略", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				}).show();

	}
}
