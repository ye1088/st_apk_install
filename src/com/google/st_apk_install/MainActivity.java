package com.google.st_apk_install;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	ListView apkList ;
	ArrayList modTime;
	ApkInfo apkInfo;
	Map apkMap ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        show_apklist();
    }


   private void show_apklist() {
		// TODO Auto-generated method stub
		apkList = (ListView) findViewById(R.id.apklistbview);
		apkMap = new HashMap();
		modTime = new ArrayList<Long>();
		list_file("/sdcard");
		
	}

//遍历文件夹 找出安装包
private void list_file(String path) {
	// TODO Auto-generated method stub
	File dir = new File(path);
	File[] listFiles = dir.listFiles();
	for (File file : listFiles) {
		if (file.isDirectory()){
			list_file(file.getAbsolutePath());
		}else{
			//判断文件是不是我们需要的东东
			if(file.getName().endsWith(".apk")||file.getName().endsWith(".xapk")||file.getName().endsWith(".dpk")
					||file.getName().endsWith(".xpk")){
				getApkInfo(file);
			}
		}
	}
}


private void getApkInfo(File file) {
	// TODO Auto-generated method stub
	PackageManager pm = getPackageManager();
	PackageInfo pi = pm.getPackageArchiveInfo(file.getAbsolutePath(), 0);
	ApplicationInfo applicationInfo = pi.applicationInfo;
	applicationInfo.sourceDir = file.getAbsolutePath();
	applicationInfo.publicSourceDir = file.getAbsolutePath();
	
	apkInfo = new ApkInfo((BitmapDrawable)pm.getApplicationIcon(applicationInfo), applicationInfo.name, applicationInfo.packageName, file.lastModified());
	apkMap.put(String.valueOf(file.lastModified()), apkInfo);
	modTime.add(file.lastModified());
}


public void btclick(View v){
	   switch (v.getId()) {
	case R.id.apklist:
		SharedPreferences settings = getSharedPreferences("Config.xml", MODE_PRIVATE);
		boolean jiaocheng = settings.getBoolean("jiaocheng", true);
		Log.i("info", "教程："+jiaocheng);
		if (jiaocheng){
			
			show_jiaocheng();
			
		}else{
			show_apk_list();
		}
		
		break;

	default:
		break;
	}
	   
   }


private void show_jiaocheng() {
	// TODO Auto-generated method stub
	Builder dialog = new AlertDialog.Builder(this);
	dialog.setTitle("教程？");
	dialog.setMessage("需要查看下清除应用数据的教程么？");
	dialog.setPositiveButton("好", new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			show_apk_list();
			Intent intent = new Intent(MainActivity.this,JiaoCheng.class);
			
			startActivity(intent);
		}
	});
	dialog.setNeutralButton("不用",new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			show_apk_list();
		}
	});
	dialog.setNegativeButton("不要再出现", new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			SharedPreferences settings = getSharedPreferences("Config.xml", MODE_PRIVATE);
			boolean jiaocheng = settings.getBoolean("jiaocheng", true);
			Log.i("info", "教程："+jiaocheng);
			if (jiaocheng){
				Editor edit = settings.edit();
				edit.putBoolean("jiaocheng", false);
				edit.commit();
			}
			
			show_apk_list();
		}
	});
	dialog.create().show();
}

public void show_apk_list(){

	Intent intent = new Intent();
	ComponentName comp = new ComponentName("com.android.settings",
	"com.android.settings.ManageApplications");
	intent.setComponent(comp);
	intent.setAction("android.intent.action.VIEW");
	startActivityForResult( intent , 0);
}
    
}
