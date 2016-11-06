package com.google.st_apk_install;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileAdapter extends BaseAdapter{

	List filePathList;
	Context context;
	LayoutInflater mInflater;

	public FileAdapter(Context context,List filePathList){
		this.filePathList = filePathList;
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}



	public void setItemList(List filePathList) {
		this.filePathList = filePathList;
    }
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return filePathList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return filePathList.get(arg0);
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
			arg1 = mInflater.inflate(R.layout.file_list_info, null);
			holder = new ViewHolder();
			holder.icon = (ImageView) arg1.findViewById(R.id.icon);
			holder.apkName = (TextView) arg1.findViewById(R.id.apkname);
//			holder.packName = (TextView) arg1.findViewById(R.id.packname);
			arg1.setTag(holder);
		}else{
			holder = (ViewHolder) arg1.getTag();
		}

		if (new File((String)filePathList.get(arg0)).isDirectory()){
			holder.icon.setImageResource(R.drawable.dir);
		}else {
			holder.icon.setImageResource(R.drawable.ic_launcher);
		}
		holder.apkName.setText(((String)filePathList.get(arg0)).substring(((String)filePathList.get(arg0)).lastIndexOf(File.separator)));
//		holder.packName.setText(((ApkInfo)getItem(arg0)).apkPath);
		return arg1;
		
	}
	
	static class ViewHolder{
		ImageView icon;
		TextView apkName;
		TextView packName;
	}

}
