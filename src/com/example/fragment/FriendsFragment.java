package com.example.fragment;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMMessage;
import com.example.bean.TalkInfo;
import com.example.chat.AddFriends;
import com.example.chat.ChatActivity;
import com.example.chat.R;
import com.example.utils.MyToastUtils;

public class FriendsFragment extends Fragment implements OnClickListener {
	private View view;
	private ListView lv_friend;
	// private ArrayList<Info> friendList;
	private List<String> usernames;
	Context context;
	private Handler h = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 2:
				if (usernames == null) {
					Intent intent = new Intent(context, AddFriends.class);
					startActivity(intent);
				} else {

					initData();
				}
				break;
			// case 1:
			//
			// getFriends();
			// adapter.notifyDataSetChanged();
			// // MyToastUtils.show(context, "正在刷新好友列表。。。");
			// break;

			default:
				break;
			}
		}

	};

	private Button bt_add;
	private MyAdapter adapter;
	private Timer time;
	private TimerTask task;

	private String otherMessage;
	private String msgFrom;

	private void initData() {

		adapter = new MyAdapter();
		if (adapter != null) {
			lv_friend.setAdapter(adapter);
		}

		adapter.notifyDataSetChanged();
		// MyToastUtils.show(context, "正在刷新。。。");
	}

	public FriendsFragment(Context context) {
		this.context = context;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// EMChatManager.getInstance().getChatOptions().setUseRoster(true);

		view = inflater.inflate(R.layout.friends_fragment, container, false);
		// getFriends();
		initView();
		startTimer();
		return view;
	}

	private void getFriends() {

		HandlerThread thread = new HandlerThread("friend");
		thread.start();
		final Handler handler = new Handler(thread.getLooper()) {

			@Override
			public void handleMessage(Message m) {

				try {

					usernames = EMContactManager.getInstance()
							.getContactUserNames();
					h.sendEmptyMessage(2);

				} catch (Exception e) {
					e.printStackTrace();
				}
				super.handleMessage(m);
			}

		};
		handler.sendEmptyMessage(1);

	}

	private void startTimer() {
		time = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				getFriends();

			}
		};
		time.schedule(task, 100, 3000);
	}

	private void initView() {
		lv_friend = (ListView) view.findViewById(R.id.lv_friend);
		bt_add = (Button) view.findViewById(R.id.bt_add);

		bt_add.setOnClickListener(this);

	}

	public class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return usernames.size();
		}

		@Override
		public Object getItem(int position) {
			return usernames.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(context, R.layout.item, null);
				holder = new ViewHolder();
				holder.iv_title = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				holder.iv_title = (ImageButton) convertView
						.findViewById(R.id.iv_delete);

				holder.tv_text = (TextView) convertView
						.findViewById(R.id.tv_text);
				convertView.setTag(holder);
				holder.iv_title.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						DeleteFriends(usernames.get(position));
					}

				});
				lv_friend.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						TalkInfo info = new TalkInfo();
						// MyToastUtils.show(context, "传过来的信息为：" + otherMessage
						// + info.getFlag());
						Intent intent = new Intent(context, ChatActivity.class);
						String username = usernames.get(position);
						intent.putExtra("username", username);
						intent.putExtra("otherMessage", otherMessage);
						intent.putExtra("msgFrom", msgFrom);
						startActivity(intent);

					}
				});
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv_text.setText(usernames.get(position));

			return convertView;
		}

	}

	class ViewHolder {

		private ImageView iv_title;
		private TextView tv_text;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_add:
			Intent intent = new Intent(context, AddFriends.class);

			startActivity(intent);
			break;

		default:
			break;
		}

	}

	/**
	 * 删除好友
	 * 
	 * @param name
	 */
	private void DeleteFriends(final String name) {

		if (name != null) {
			HandlerThread thread = new HandlerThread("add");
			thread.start();
			Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					try {
						EMContactManager.getInstance().deleteContact(name);// 需异步处理
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
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		time.cancel();
	}

	@Override
	public void onResume() {

		super.onResume();

	}

}
