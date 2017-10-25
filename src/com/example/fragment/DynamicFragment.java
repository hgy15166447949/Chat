package com.example.fragment;

import com.example.chat.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class DynamicFragment extends Fragment {

	private Context context;
	private WebView wv_web;
	private Button bt;

	public DynamicFragment(Context context) {
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

		View view = inflater.inflate(R.layout.dynamic_fragment, container,
				false);
		wv_web = (WebView) view.findViewById(R.id.wv_web);
		bt = (Button) view.findViewById(R.id.bt);
		WebSettings settings = wv_web.getSettings();
		settings.setBuiltInZoomControls(true);// �����Ƿ���ʾ���Ź���
		settings.setSupportZoom(true);// �����Ƿ�֧������
		settings.setJavaScriptEnabled(true);
		settings.setDefaultFontSize(15);
		wv_web.loadUrl("file:///android_asset/pdf.html");
		wv_web.addJavascriptInterface(new JSToast(context), "JSADD");
		wv_web.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				super.onProgressChanged(view, newProgress);
				
				//����UIִ���������
			}
		});
		wv_web.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// ���´򿪵���ҳ�ڵ�ǰwebview��ʾ
				view.loadUrl(url);
				return true;
			}
			
			
		});
		wv_web.setDownloadListener(new DownloadListener() {
			
			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype, long contentLength) {
				 System.out.println("=========>��ʼ���� url =" + url);  
                 Uri uri = Uri.parse(url);     
                 Intent intent = new Intent(Intent.ACTION_VIEW, uri);     
                 startActivity(intent);  
				
			}
		});
		
		
		bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				wv_web.loadUrl("javaScript:toJS('����!!')");
				
			}
		});
		return view;
	}
}
