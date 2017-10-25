package com.example.fragment;


import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.example.app.MyApplaction;
import com.example.chat.LockActivity;
import com.example.chat.R;
import com.example.chat.ShareActivity;
import com.example.utils.ActivityUtil;
import com.example.utils.CustomerInformation;
import com.example.utils.ShareUtils;
import com.example.utils.SlideSwitch;
import com.example.utils.SlideSwitch.SlideListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.Toast;

public class MessageFragment extends Fragment implements OnClickListener{
	private Button bt_share;
	Context context;
	private SlideSwitch swit3;
	private SharedPreferences sharedPreferences;
	
	public MessageFragment(Context context) {
		this.context = context;

	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		System.out.println("onAttach......");
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		System.out.println("onCreat......");
	}
	

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.message_fragment, container, false);
		sharedPreferences = context.getSharedPreferences("����", 0);
		bt_share = (Button) view.findViewById(R.id.bt_share);
		swit3 = (SlideSwitch) view.findViewById(R.id.swit3);
		boolean states = ShareUtils.getStates(context);
		swit3.setState(states);
		swit3.setShapeType(2);
		swit3.setSlideListener(new SlideListener() {
			
			@Override
			public void open() {
				ActivityUtil.goToActivity(context, LockActivity.class);
				ShareUtils.putStates(context, true);
				
				
			}
			
			@Override
			public void close() {
				ShareUtils.putStates(context, false);
				MyApplaction.getInstance().getLockPatternUtils().clearLock();
				
			}
		});
		bt_share.setOnClickListener(this);
		System.out.println("onCreatView......");
		
		
		
		return view;
	}
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		System.out.println("onActivityCreat......");
	}
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		System.out.println("onStart......");
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("onResume......");
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		System.out.println("onPause......");
	}
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		System.out.println("onStop......");
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.out.println("onDestroy......");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_share:
			showShare();
			
			
			break;

		default:
			break;
		}
		
	}
	
	private void showShare() {
		 ShareSDK.initSDK(context);
		 OnekeyShare oks = new OnekeyShare();
		 //�ر�sso��Ȩ
		 oks.disableSSOWhenAuthorize(); 
		 
		// ����ʱNotification��ͼ�������  2.5.9�Ժ�İ汾�����ô˷���
		 //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		 // title���⣬ӡ��ʼǡ����䡢��Ϣ��΢�š���������QQ�ռ�ʹ��
		 oks.setTitle("����");
		 // titleUrl�Ǳ�����������ӣ�������������QQ�ռ�ʹ��
		 oks.setTitleUrl("http://www.baidu.com");
		 // text�Ƿ����ı�������ƽ̨����Ҫ����ֶ�
		 oks.setText("д������Ĺ�������");
		 //��������ͼƬ������΢����������ͼƬ��Ҫͨ����˺�����߼�д��ӿڣ�������ע�͵���������΢��
		 oks.setImageUrl("http://pic72.nipic.com/file/20150710/15184802_185230751182_2.jpg");
//		 oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
		 // imagePath��ͼƬ�ı���·����Linked-In�����ƽ̨��֧�ִ˲���
		 //oks.setImagePath("/sdcard/test.jpg");//ȷ��SDcard������ڴ���ͼƬ
		 // url����΢�ţ��������Ѻ�����Ȧ����ʹ��
		 oks.setUrl("http://www.baidu.com");
		 // comment���Ҷ�������������ۣ�������������QQ�ռ�ʹ��
		 oks.setComment("���ǲ��������ı�");
		 // site�Ƿ�������ݵ���վ���ƣ�����QQ�ռ�ʹ��
		 oks.setSite("ShareSDK");
		 // siteUrl�Ƿ�������ݵ���վ��ַ������QQ�ռ�ʹ��
		 oks.setSiteUrl("http://sharesdk.cn");
		 
		// ��������GUI
		 oks.show(context);
		 }
	
	

}
