package com.example.chat;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.packet.Message;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMNotifier;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.NetUtils;
import com.example.adapter.MyFragmentPagerAdapter;
import com.example.bean.TalkInfo;
import com.example.fragment.DynamicFragment;
import com.example.fragment.FriendsFragment;
import com.example.fragment.MessageFragment;
import com.example.utils.MyToastUtils;
import com.example.utils.SlideSwitch;

public class TalkingActivity extends FragmentActivity implements
		OnClickListener {

	private ViewPager viewpager;
	private EMMessage message;
	private String content;
	private String msgFrom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_talk);
		initEMC();
		initView();

	}
	
	
	private void initEMC() {

		EMChatManager.getInstance().getChatOptions().setUseRoster(true);
		EMChatManager.getInstance().addConnectionListener(
				new MyConnectionListener());
		// ��ȡ��EMChatOptions����
		EMChatOptions options = EMChatManager.getInstance().getChatOptions();
		// Ĭ����Ӻ���ʱ���ǲ���Ҫ��֤�ģ��ĳ���Ҫ��֤
		options.setAcceptInvitationAlways(false);
		// �����յ���Ϣ�Ƿ�������Ϣ֪ͨ��Ĭ��Ϊtrue
		options.setNotificationEnable(false);
		// �����յ���Ϣ�Ƿ���������ʾ��Ĭ��Ϊtrue
		options.setNoticeBySound(false);
		// �����յ���Ϣ�Ƿ��� Ĭ��Ϊtrue
		options.setNoticedByVibrate(false);
		// ����������Ϣ�����Ƿ�����Ϊ���������� Ĭ��Ϊtrue
		options.setUseSpeaker(false);

		// ������ϵ�˵ı仯
		EMContactManager.getInstance().setContactListener(
				new MyContactListener());
		initView();
		// ���Ҫ֪ͨsdk��UI �Ѿ���ʼ����ϣ�ע������Ӧ��receiver��listener, ���Խ���broadcast��
		EMChat.getInstance().setAppInited();

		// ������Ϣ ��Ҫ�߹㲥�����Ե������� SDK �ر� notification ֪ͨ����������Ϣ�����߷��͹㲥����ʽ
		EMChatManager.getInstance().getChatOptions()
				.setShowNotificationInBackgroud(false);

		// ע�������Ϣ�Ĺ㲥
		NewMessageBroadcastReceiver msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager
				.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		registerReceiver(msgReceiver, intentFilter);

	}

	private class NewMessageBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// ��Ϣid
			String msgId = intent.getStringExtra("msgid");
			msgFrom = intent.getStringExtra("from");
			// ��Ϣ���ͣ��ı���ͼƬ��������Ϣ�ȣ����ﷵ�ص�ֵΪmsg.type.ordinal()��
			// ������Ϣtypeʵ��Ϊ��enum����
			int msgType = intent.getIntExtra("type", 0);
			Log.d("main", "new message id:" + msgId + " from:" + msgFrom
					+ " type:" + msgType);
			message = EMChatManager.getInstance().getMessage(msgId);
			MyToastUtils.show(TalkingActivity.this, "���յ�" + msgFrom + "��Ϣ");
			// MyToastUtils.show(context, content);
			// ������ķ�����ͨ��msgIdֱ�ӻ�ȡ����message
		}
	}

	private void initView() {
		ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();

		Button bt_message = (Button) findViewById(R.id.bt_message);
		Button bt_friend = (Button) findViewById(R.id.bt_friend);
		Button bt_setting = (Button) findViewById(R.id.bt_setting);
		viewpager = (ViewPager) findViewById(R.id.vp_viewpager);

		FriendsFragment friendsFragment = new FriendsFragment(this);
		MessageFragment messageFragment = new MessageFragment(this);
		DynamicFragment dynamicFragment = new DynamicFragment(
				TalkingActivity.this);
		fragmentList.add(messageFragment);
		fragmentList.add(friendsFragment);
		fragmentList.add(dynamicFragment);
		bt_message.setOnClickListener(this);
		bt_friend.setOnClickListener(this);
		bt_setting.setOnClickListener(this);
		viewpager.setAdapter(new MyFragmentPagerAdapter(
				getSupportFragmentManager(), fragmentList));
		viewpager.setCurrentItem(0);
		viewpager.setOffscreenPageLimit(1);

	}
	
	private class MyConnectionListener implements EMConnectionListener {
		@Override
		public void onConnected() {
			// �����ӵ�������
		}

		@Override
		public void onDisconnected(final int error) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (error == EMError.USER_REMOVED) {
						// ��ʾ�ʺ��Ѿ����Ƴ�
						MyToastUtils.show(TalkingActivity.this, "�˺��Ѿ����Ƴ�");
					} else if (error == EMError.CONNECTION_CONFLICT) {
						// ��ʾ�ʺ��������豸��½
						MyToastUtils.show(TalkingActivity.this, "�˺��Ѿ���½");
					} else {
						if (NetUtils.hasNetwork(TalkingActivity.this)) {
							// ���Ӳ������������
							MyToastUtils.show(TalkingActivity.this, "����ʧ��111");
						} else {
							// ��ǰ���粻���ã�������������
							MyToastUtils.show(TalkingActivity.this, "���粻����");
						}

					}
				}
			});
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// �˷���Ϊͬ������ �˳���½
		EMChatManager.getInstance().logout();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_message:
			viewpager.setCurrentItem(0);
			break;
		case R.id.bt_friend:
			viewpager.setCurrentItem(1);
			break;
		case R.id.bt_setting:
			viewpager.setCurrentItem(2);
			break;

		default:
			break;
		}
	}

	private class MyContactListener implements EMContactListener {

		@Override
		public void onContactAdded(List<String> usernameList) {
			// �������ӵ���ϵ��

		}

		@Override
		public void onContactDeleted(final List<String> usernameList) {
			// ��ɾ��

		}

		@Override
		public void onContactInvited(String username, String reason) {
			// �ӵ��������Ϣ�����������(ͬ���ܾ�)�����ߺ󣬷��������Զ��ٷ����������Կͻ��˲�Ҫ�ظ�����
			showAgreedDialog(username, reason);
			EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();

		}

		@Override
		public void onContactAgreed(String username) {
			// ͬ���������

			EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();
			MyToastUtils.show(TalkingActivity.this, username + "ͬ�����������");

		}

		@Override
		public void onContactRefused(String username) {
			// �ܾ���������

		}

	}

	private void showAgreedDialog(final String user, String reason) {
		new AlertDialog.Builder(TalkingActivity.this)
				.setTitle("Ӧ����ʾ")
				.setMessage(
						"�û� " + user + " ��Ҫ�����Ϊ���ѣ��Ƿ�ͬ�⣿\n" + "��֤��Ϣ��" + reason)
				.setPositiveButton("ͬ��", new DialogInterface.OnClickListener() {
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
				.setNegativeButton("�ܾ�", new DialogInterface.OnClickListener() {
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
				.setNeutralButton("����", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				}).show();

	}

}
