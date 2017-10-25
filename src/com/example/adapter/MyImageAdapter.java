package com.example.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.bean.TalkInfo;
import com.example.chat.R;

public class MyImageAdapter extends BaseAdapter{
	
	private Context context;
	private ArrayList<TalkInfo> list ;
	private String picturePath;
	public MyImageAdapter(Context context,ArrayList<TalkInfo> list,String picturePath){
		super();
		this.context = context;
		this.list = list;
		this.picturePath = picturePath;
		
	}



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
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			switch (itemViewType) {
			case 0:
				convertView = View.inflate(context,
						R.layout.myimagemessage, null);
				holder.my_image = (ImageView) convertView
						.findViewById(R.id.iv_myimage);

				break;
			case 1:
				convertView = View.inflate(context,
						R.layout.otherimagemessage, null);
				holder.other_image = (ImageView) convertView
						.findViewById(R.id.iv_otherimage);

				break;

			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		switch (itemViewType) {
		case 0:
			holder.my_image.setImageBitmap(BitmapFactory.decodeFile(picturePath));

			break;
		case 1:
			holder.other_image.setImageBitmap(BitmapFactory.decodeFile(picturePath));
			break;
		default:
			break;
		}

		// lv_talkmessage.smoothScrollToPosition(list.size() - 1);
		return convertView;

	}

	/**
	 * 返回条目类型的总数量
	 */
	@Override
	public int getViewTypeCount() {

		return 2;
	}

	// 条目类型
	@Override
	public int getItemViewType(int position) {
		String flag = list.get(position).getFlag();
		System.out.println("flag1:"+flag);
		
		if ("0".equals(flag)) {
			return 0;
		} else {
			return 1;
		}
	}
	
	class ViewHolder {

		private ImageView my_image;
		private ImageView other_image;
	}




}
