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
		// 获取到EMChatOptions对象
		EMChatOptions options = EMChatManager.getInstance().getChatOptions();
		// 默认添加好友时，是不需要验证的，改成需要验证
		options.setAcceptInvitationAlways(false);
		// 设置收到消息是否有新消息通知，默认为true
		options.setNotificationEnable(false);
		// 设置收到消息是否有声音提示，默认为true
		options.setNoticeBySound(false);
		// 设置收到消息是否震动 默认为true
		options.setNoticedByVibrate(false);
		// 设置语音消息播放是否设置为扬声器播放 默认为true
		options.setUseSpeaker(false);

		// 监听联系人的变化
		EMContactManager.getInstance().setContactListener(
				new MyContactListener());
		initView();
		// 最后要通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
		EMChat.getInstance().setAppInited();

		// 接受消息 需要走广播，可以调用以下 SDK 关闭 notification 通知，这样新消息还是走发送广播的形式
		EMChatManager.getInstance().getChatOptions()
				.setShowNotificationInBackgroud(false);

		// 注册接受消息的广播
		NewMessageBroadcastReceiver msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager
				.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		registerReceiver(msgReceiver, intentFilter);

	}

	private class NewMessageBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 消息id
			String msgId = intent.getStringExtra("msgid");
			msgFrom = intent.getStringExtra("from");
			// 消息类型，文本、图片、语音消息等，这里返回的值为msg.type.ordinal()。
			// 所以消息type实际为是enum类型
			int msgType = intent.getIntExtra("type", 0);
			Log.d("main", "new message id:" + msgId + " from:" + msgFrom
					+ " type:" + msgType);
			message = EMChatManager.getInstance().getMessage(msgId);
			MyToastUtils.show(TalkingActivity.this, "接收到" + msgFrom + "消息");
			// MyToastUtils.show(context, content);
			// 更方便的方法是通过msgId直接获取整个message
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
			// 已连接到服务器
		}

		@Override
		public void onDisconnected(final int error) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (error == EMError.USER_REMOVED) {
						// 显示帐号已经被移除
						MyToastUtils.show(TalkingActivity.this, "账号已经被移除");
					} else if (error == EMError.CONNECTION_CONFLICT) {
						// 显示帐号在其他设备登陆
						MyToastUtils.show(TalkingActivity.this, "账号已经登陆");
					} else {
						if (NetUtils.hasNetwork(TalkingActivity.this)) {
							// 连接不到聊天服务器
							MyToastUtils.show(TalkingActivity.this, "连接失败111");
						} else {
							// 当前网络不可用，请检查网络设置
							MyToastUtils.show(TalkingActivity.this, "网络不可用");
						}

					}
				}
			});
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 此方法为同步方法 退出登陆
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
			// 保存增加的联系人

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
			MyToastUtils.show(TalkingActivity.this, username + "同意了你的请求");

		}

		@Override
		public void onContactRefused(String username) {
			// 拒绝好友请求

		}

	}

	private void showAgreedDialog(final String user, String reason) {
		new AlertDialog.Builder(TalkingActivity.this)
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
