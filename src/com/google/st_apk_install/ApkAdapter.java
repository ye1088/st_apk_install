package com.google.st_apk_install;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ApkAdapter extends BaseAdapter{
	
	ArrayList modTime;
	Map apkMap;
	Context context;
	LayoutInflater mInflater;
	
	public ApkAdapter(Context context, ArrayList modTime, Map apkMap){
		this.context = context;
		this.modTime = modTime;
		this.apkMap = apkMap;
		this.mInflater = LayoutInflater.from(context);
	}

	public void setItemList(ArrayList modTime, Map apkMap) {
		this.modTime = modTime;
		this.apkMap = apkMap;
    }
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return modTime.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return apkMap.get(String.valueOf(modTime.get(arg0)));
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (arg1 == null){
			arg1 = mInflater.inflate(R.layout.apk_info, null);
			holder = new ViewHolder();
			holder.icon = (ImageView) arg1.findViewById(R.id.icon);
			holder.apkName = (TextView) arg1.findViewById(R.id.apkname);
			holder.packName = (TextView) arg1.findViewById(R.id.packname);
			arg1.setTag(holder);
		}else{
			holder = (ViewHolder) arg1.getTag();
		}
		holder.icon.setImageBitmap(((ApkInfo)getItem(arg0)).icon);
		holder.apkName.setText(((ApkInfo)getItem(arg0)).apk_name);
		holder.packName.setText(((ApkInfo)getItem(arg0)).pack_name);
		return arg1;
		
	}
	
	static class ViewHolder{
		ImageView icon;
		TextView apkName;
		TextView packName;
	}

}
