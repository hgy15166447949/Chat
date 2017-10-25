package com.example.chat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.example.bean.TalkInfo;
import com.example.utils.ImageUtil;
import com.example.utils.MyToastUtils;

public class ChatActivity extends Activity implements OnClickListener {
	private ImageView iv_back;
	private EditText et_sendmessage;
	private Button tv_sendmessage;
	private TextView tv_title;
	private String username;
	private ListView lv_talkmessage;
	private String msgFrom;
	private ArrayList<TalkInfo> list = new ArrayList<TalkInfo>();
	private final int MY_TEXT = 0;
	private final int OTHER_TEXT = 1;
	private final int RESULT_LOAD_IMAGE = 201;
	private int voiceflag = 0;
	private int touchflag = 0;
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 200:
				setMessageAdapter();
				lv_talkmessage.setSelection(lv_talkmessage.getBottom());// 刷新到底部
				break;
			default:
				break;
			}
		}

	};
	private LinearLayout ll_list;
	private String otherMessage;

	private EMConversation conversation;
	private EMMessage messagebody;
	private MyAdapter myAdapter;
	private ImageView iv_imagemessage;
	private String picturePath;
	private String remotrurl;
	private ImageView iv_voice;
	private LinearLayout ll_voice;
	private ImageView iv_sendvoice;
	private TextView tv_voice;
	private MediaRecorder recorder;
	private MediaPlayer player;
	private String format;
	private String path;
	private File fpath;
	private File audioFile;
	// 语音文件保存路径
	private String FileName = null;
	private ImageView iv_map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat);
		initView();
		initMessage();
		initReceiver();

	}

	/**
	 * 设置语音路径
	 */
	private void getVoicePath() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String format = dateFormat.format(date);
		System.out.println("format:" + format);
		// 设置sdcard的路径
		FileName = Environment.getExternalStorageDirectory().getAbsolutePath();
		FileName += "/" + format + ".amr";

	}

	/**
	 * 获取历史消息
	 */
	private void initMessage() {
		conversation = EMChatManager.getInstance().getConversation(username);

		Thread thread = new Thread() {
			@Override
			public void run() {
				super.run();
				getOldMessage();
			}
		};
		thread.start();
	}

	private void initReceiver() {
		// 获取消息
		EMChat.getInstance().setAppInited();
		EMChatManager.getInstance().getChatOptions()
				.setShowNotificationInBackgroud(false);
		NewMessageBroadcastReceiver msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager
				.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		registerReceiver(msgReceiver, intentFilter);
	}

	/**
	 * 隐藏键盘
	 */
	public void Hidesoftkeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		if (imm != null) {
			if (ChatActivity.this.getCurrentFocus() != null) {

				imm.hideSoftInputFromWindow(ChatActivity.this.getCurrentFocus()
						.getWindowToken(), 0);
			}
		}
	}

	private void getOldMessage() {

		EMConversation conversation = EMChatManager.getInstance()
				.getConversation(username);
		// EMMessage firstMsg = conversation.getAllMessages().get(0);
		// String startMsgId = firstMsg.getMsgId();
		// 获取此会话的所有消息
		List<EMMessage> messages = conversation.getAllMessages();
		// sdk初始化加载的聊天记录为20条，到顶时需要去db里获取更多
		// 获取startMsgId之前的pagesize条消息，此方法获取的messages
		// sdk会自动存入到此会话中，app中无需再次把获取到的messages添加到会话中
		// String startMsgID = msg.get(0).getMsgId();
		// List<EMMessage> messages = conversation
		// .loadMoreMsgFromDB(startMsgID, 20);
		// String textaaa = messages.get(2).getBody().toString();
		// MyToastUtils.show(ChatActivity.this, "历史消息"+textaaa);
		// // 如果是群聊，调用下面此方法
		// List<EMMessage> messages = conversation.loadMoreGroupMsgFromDB(
		// startMsgId, pagesize);
		for (int i = 0; i < messages.size(); i++) {

			String from = conversation.getMessage(i).getFrom();
			String text = messages.get(i).getBody().toString();
			String content = text.substring(5, text.length() - 1);
			TalkInfo info = new TalkInfo();
			if (username.equals(from)) {
				info.setFlag("1");
			} else {
				info.setFlag("0");
			}
			info.setMessage(content);
			list.add(info);
		}
		handler.sendEmptyMessage(200);

	}

	/**
	 * 接受消息
	 * 
	 * @author Administrator
	 *
	 */
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
			// 更方便的方法是通过msgId直接获取整个message
			EMMessage message = EMChatManager.getInstance().getMessage(msgId);
			System.out.println("msgType:::" + msgType);
			// msgType = 4的时候是接收到的语音
			// msgType = 3的时候是地图
			if (msgFrom.equals(username)) {
				TalkInfo info = new TalkInfo();
				String text = message.getBody().toString();
				if (msgType == 1) {
					String content = text.substring(0, text.length() - 1);
					// 截取接收到的图片信息，拿到远程路径
					String remotrurl = content.substring(
							content.indexOf("remoteurl:") + 10,
							content.indexOf(",thumbnial"));
					info.setImagePath(remotrurl);
					info.setFlag("3");
					list.add(info);
				} else if (msgType == 0) {
					String content = text.substring(5, text.length() - 1);
					info.setMessage(content);
					info.setFlag("1");
					list.add(info);
				} else if (msgType == 4) {
					remotrurl = text.substring(text.indexOf("localurl:") + 9,
							text.indexOf(",remoteurl"));
					info.setVoicePath(remotrurl);
					info.setFlag("5");
					list.add(info);
				}
				handler.sendEmptyMessage(200);

			}
		}

	}

	/**
	 * 发送文字消息
	 */
	private void sendMessage() {

		final String mycontent = et_sendmessage.getText().toString();

		if (!TextUtils.isEmpty(mycontent)) {
			messagebody = EMMessage.createSendMessage(EMMessage.Type.TXT);
			// 如果是群聊，设置chattype,默认是单聊
			// message.setChatType(ChatType.GroupChat);
			// 设置消息body
			TextMessageBody txtBody = new TextMessageBody(mycontent);
			messagebody.addBody(txtBody);
			// 设置接收人
			messagebody.setReceipt(username);
			// final String msgId = messagebody.getMsgId();
			// 把消息加入到此会话对象中
			conversation.addMessage(messagebody);

			// 发送消息
			EMChatManager.getInstance().sendMessage(messagebody,
					new EMCallBack() {

						@Override
						public void onError(int arg0, String arg1) {
							MyToastUtils.show(ChatActivity.this, "发送失败");

						}

						@Override
						public void onProgress(int arg0, String arg1) {
							MyToastUtils.show(ChatActivity.this, "正在发送");

						}

						@Override
						public void onSuccess() {
							TalkInfo info = new TalkInfo();
							info.setMessage(mycontent);
							info.setFlag("0");
							list.add(info);
							handler.sendEmptyMessage(200);

						}

					});
		} else {
			MyToastUtils.show(ChatActivity.this, "消息不能为空！");
		}

	}

	/**
	 * 设置adapter
	 */
	private void setMessageAdapter() {
		myAdapter = new MyAdapter();
		lv_talkmessage.setAdapter(myAdapter);
		myAdapter.notifyDataSetChanged();
	}

	public class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			int itemViewType = getItemViewType(position);
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				switch (itemViewType) {
				case MY_TEXT:
					convertView = View.inflate(ChatActivity.this,
							R.layout.mytextmessage, null);
					holder.my_text = (TextView) convertView
							.findViewById(R.id.tv_mytext);
					break;
				case OTHER_TEXT:
					convertView = View.inflate(ChatActivity.this,
							R.layout.othertextmessage, null);
					holder.other_text = (TextView) convertView
							.findViewById(R.id.tv_othertext);
					break;
				case 2:
					convertView = View.inflate(ChatActivity.this,
							R.layout.myimagemessage, null);
					holder.my_image = (ImageView) convertView
							.findViewById(R.id.iv_myimage);
					holder.my_image.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							String imagePath = list.get(position)
									.getImagePath();
							Intent intent = new Intent(ChatActivity.this,
									ImageShowActivity.class);
							intent.putExtra("picpath", imagePath);
							int[] location = new int[2];
							holder.my_image.getLocationOnScreen(location);
							intent.putExtra("locationX", location[0]);// 必须
							intent.putExtra("locationY", location[1]);// 必须
							intent.putExtra("width", holder.my_image.getWidth());// 必须
							intent.putExtra("height",
									holder.my_image.getHeight());// 必须
							startActivity(intent);
							overridePendingTransition(0, 0);

						}
					});
					break;
				case 3:
					convertView = View.inflate(ChatActivity.this,
							R.layout.otherimagemessage, null);
					holder.other_image = (ImageView) convertView
							.findViewById(R.id.iv_otherimage);
//					holder.other_image
//							.setOnClickListener(new OnClickListener() {
//
//								@Override
//								public void onClick(View v) {
//									String imagePath = list.get(position)
//											.getImagePath();
//									Intent intent = new Intent(
//											ChatActivity.this,
//											ImageShowActivity.class);
//									intent.putExtra("picpath", imagePath);
//									startActivity(intent);
//
//								}
//							});

					break;
				case 4:
					convertView = View.inflate(ChatActivity.this,
							R.layout.my_voice_message, null);
					holder.my_voice = (ImageView) convertView
							.findViewById(R.id.iv_myvoice);
					holder.my_voice.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							PlayVoice(list.get(position));

						}

						private void PlayVoice(TalkInfo talkInfo) {
							String voicePath = list.get(position)
									.getVoicePath();
							player = new MediaPlayer();
							try {
								player.setDataSource(voicePath);
								player.prepare();
								player.start();
							} catch (IOException e) {
								Log.e("play", "播放失败");
							}
						}

					});

					break;
				case 5:
					convertView = View.inflate(ChatActivity.this,
							R.layout.other_voice_message, null);
					holder.other_voice = (ImageView) convertView
							.findViewById(R.id.iv_othervoice);
					holder.other_voice
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {

									PlayOtherVoice(list.get(position));
								}

								private void PlayOtherVoice(TalkInfo talkInfo) {
									// 获取到远程播放路径
									String voicePath = list.get(position)
											.getVoicePath();
									player = new MediaPlayer();
									try {
										player.setDataSource(voicePath);
										player.prepare();
										player.start();
									} catch (IOException e) {
										Log.e("play", "播放失败");
									}
								}
							});

					break;

				}
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			switch (itemViewType) {
			case MY_TEXT:
				holder.my_text.setText(list.get(position).getMessage());

				break;
			case OTHER_TEXT:
				holder.other_text.setText(list.get(position).getMessage());

				break;
			case 2:
				Uri uri = Uri.parse(list.get(position).getImageUri());
				//图片缩放处理
				Bitmap scaledBitmap = ImageUtil.getScaledBitmap(ChatActivity.this, uri, 120, 160);
				//对缩放后的图片进行质量压缩
				Bitmap bitmap = ImageUtil.qualityCompress(scaledBitmap, 80);
				holder.my_image.setImageBitmap(bitmap);

				break;
			case 3:

				
				

				String imagePath2 = list.get(position).getImagePath();
				RequestQueue queue = Volley.newRequestQueue(ChatActivity.this);
				ImageRequest request = new ImageRequest(imagePath2,
						new Response.Listener<Bitmap>() {
							@Override
							public void onResponse(Bitmap arg0) {
								
								holder.other_image.setImageBitmap(arg0);
							}
						}, 0, 0, Config.RGB_565, new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError arg0) {
							}
						});
				queue.add(request);

				break;
			case 4:
				holder.my_voice.setBackgroundResource(R.drawable.myvoice);

				break;
			case 5:
				holder.other_voice.setBackgroundResource(R.drawable.othervoice);

				break;
			}
			return convertView;
		}

		/**
		 * 返回条目类型的总数量
		 */
		@Override
		public int getViewTypeCount() {

			return 6;
		}

		/**
		 * 条目类型
		 */
		@Override
		public int getItemViewType(int position) {
			String flag = list.get(position).getFlag();
			if (flag.equals("0")) {
				return MY_TEXT;
			} else if (flag.equals("1")) {
				return OTHER_TEXT;
			} else if (flag.equals("2")) {
				return 2;
			} else if (flag.equals("3")) {
				return 3;
			} else if (flag.equals("4")) {
				return 4;
			} else {
				return 5;
			}
		}

	}

	class ViewHolder {

		private TextView my_text;
		private TextView other_text;
		private ImageView my_image;
		private ImageView other_image;
		private ImageView my_voice;
		private ImageView other_voice;
	}

	private void initView() {
		// 接收传过来的参数
		Intent intent = getIntent();
		username = intent.getStringExtra("username");
		iv_back = (ImageView) findViewById(R.id.iv_back);
		et_sendmessage = (EditText) findViewById(R.id.et_sendmessage);
		tv_sendmessage = (Button) findViewById(R.id.bt_sendmessage);
		tv_title = (TextView) findViewById(R.id.tv_title_chat);
		lv_talkmessage = (ListView) findViewById(R.id.lv_talkmessage);
		lv_talkmessage.setDivider(null);
		ll_list = (LinearLayout) findViewById(R.id.ll_list);
		iv_voice = (ImageView) findViewById(R.id.iv_voice);
		iv_sendvoice = (ImageView) findViewById(R.id.iv_sendvoice);
		ll_voice = (LinearLayout) findViewById(R.id.ll_voice);
		iv_imagemessage = (ImageView) findViewById(R.id.iv_imagemessage);
		tv_voice = (TextView) findViewById(R.id.tv_voice);
		tv_title.setText("与 " + username + " 聊天中。。。");
		iv_map = (ImageView) findViewById(R.id.iv_map);
		initEvent();
	}

	private void initEvent() {
		iv_voice.setOnClickListener(this);
		iv_back.setOnClickListener(this);
		tv_sendmessage.setOnClickListener(this);
		iv_imagemessage.setOnClickListener(this);
		iv_map.setOnClickListener(this);
		iv_sendvoice.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					flushDownUI();
					getVoicePath();
					recorder = new MediaRecorder();
					recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
					recorder.setOutputFile(FileName);
					recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
					try {
						recorder.prepare();
					} catch (IOException e) {
						Log.e("failed", "prepare() failed");
					}
					recorder.start();
					break;
				case MotionEvent.ACTION_UP:
					flushUpUI();
					recorder.stop();
					recorder.release();
					recorder = null;
					sendVoiceMessage();
					// File file = new File(FileName);
					// long length = file.length();
					// System.out.println("length:" + length);
					// System.out.println("FileName:" + FileName);
					break;

				default:
					break;
				}
				return true;
			}

			/**
			 * 发送语音消息
			 */
			private void sendVoiceMessage() {
				// EMConversation conversation =
				// EMChatManager.getInstance().getConversation(username);
				EMMessage message = EMMessage
						.createSendMessage(EMMessage.Type.VOICE);
				// 如果是群聊，设置chattype，默认是单聊
				// message.setChatType(ChatType.GroupChat);
				VoiceMessageBody body = new VoiceMessageBody(
						new File(FileName), 1024 * 1024);
				message.addBody(body);
				message.setReceipt(username);
				conversation.addMessage(message);
				EMChatManager.getInstance().sendMessage(message,
						new EMCallBack() {

							@Override
							public void onError(int arg0, String arg1) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onProgress(int arg0, String arg1) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onSuccess() {
								TalkInfo talkInfo = new TalkInfo();
								talkInfo.setVoicePath(FileName);
								talkInfo.setFlag("4");
								list.add(talkInfo);
								handler.sendEmptyMessage(200);

							}
						});

			}

			private void flushUpUI() {
				// recorder.stop();// 停止刻录
				// recorder.release(); // 刻录完成一定要释放资源
				// // 文件存储路径为path
				// File file = new File(path);
				// long length = file.length();
				// System.out.println("length:" + length);
				iv_sendvoice.setImageResource(R.drawable.animlist);
				AnimationDrawable animationDrawable1 = (AnimationDrawable) iv_sendvoice
						.getDrawable();
				animationDrawable1.stop();
				tv_voice.setText("按住说话");
				// TalkInfo info = new TalkInfo();
				// info.setMessage(path);
				// info.setFlag("4");
				// list.add(info);

			}

			private void flushDownUI() {
				iv_sendvoice.setImageResource(R.drawable.animlist);
				AnimationDrawable animationDrawable = (AnimationDrawable) iv_sendvoice
						.getDrawable();
				animationDrawable.start();
				tv_voice.setText("正在说话");
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:

			finish();
			break;
		case R.id.bt_sendmessage:
			// 发送消息
			sendMessage();
			et_sendmessage.setText("");

			break;
		case R.id.iv_imagemessage:
			// 发送消息
			Intent i = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(i, RESULT_LOAD_IMAGE);

			break;
		case R.id.iv_voice:
			if (voiceflag == 0) {
				Hidesoftkeyboard();

				ll_voice.setVisibility(View.VISIBLE);
				et_sendmessage.setVisibility(View.INVISIBLE);
				tv_sendmessage.setClickable(false);

				voiceflag = 1;
			} else {
				et_sendmessage.setVisibility(View.VISIBLE);
				ll_voice.setVisibility(View.GONE);
				voiceflag = 0;
				tv_sendmessage.setClickable(true);
			}

			break;
		case R.id.iv_map:
			Intent intent = new Intent(ChatActivity.this,
					BaiDuMapActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}

	}

	private void sendImageMessage(final String imageUri) {
		// // 发送图片消息
		// EMConversation conversation = EMChatManager.getInstance()
		// .getConversation(username);
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
		// 如果是群聊，设置chattype，默认是单聊
		// message.setChatType(ChatType.GroupChat);
		ImageMessageBody body = new ImageMessageBody(new File(picturePath));
		// 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
		// body.setSendOriginalImage(true);
		message.addBody(body);
		message.setReceipt(username);
		conversation.addMessage(message);
		EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

			@Override
			public void onError(int arg0, String arg1) {

			}

			@Override
			public void onProgress(int arg0, String arg1) {

			}

			@Override
			public void onSuccess() {

				TalkInfo info = new TalkInfo();
				info.setImagePath(picturePath);
				info.setImageUri(imageUri);
				// info.setMessage(picturePath);
				info.setFlag("2");
				list.add(info);
				handler.sendEmptyMessage(200);

			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 通过回调获取到图片的uri
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImageUri = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImageUri,
					filePathColumn, null, null, null);
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			cursor.moveToFirst();
			picturePath = cursor.getString(columnIndex);
			System.out.println("picturePath:" + picturePath);
			String imageUri = selectedImageUri.toString();
			sendImageMessage(imageUri);
			// handler.sendEmptyMessage(300);
			// iv.setImageBitmap(BitmapFactory.decodeFile(picturePath));
			cursor.close();
		}
	}

}
