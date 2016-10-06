package com.google.st_apk_install;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipException;

import android.net.Uri;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	ListView apkList ;
	ArrayList modTime;
	ApkInfo apkInfo;
	//����modTime�е��ļ�����޸ĵ�ʱ��� ����ȡ��Ӧ ��apk��Ϣ
	Map apkMap ;
	ApkAdapter adapter;
	Utils util ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        util = new Utils();
        //���ֻ��ڴ��е�����apk����ʾ����
        show_apklist();
        apkList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				// TODO Auto-generated method stub
				String apk_name = ((ApkInfo)apkMap.get(String.valueOf(modTime.get(pos)))).apk_name;
				String apkPath = ((ApkInfo)apkMap.get(String.valueOf(modTime.get(pos)))).apkPath;
				if (apkPath.endsWith(".apk")){
					util.install_apk(apkPath,MainActivity.this);
				}else if(apkPath.endsWith(".xapk")||apkPath.endsWith(".xpk")||apkPath.endsWith(".dpk")){
					xapk_install(apkPath);
				}
			}

			
		});
    }
    
    
    
    //xapk��xpk�İ�װ
    private void xapk_install(String apkPath){
    	try {
			String unZipDir = String.valueOf(new Date());
			util.upZipFile(new File(apkPath), "/sdcard/st_unZip/"+unZipDir);
			File uZipDir = new File("/sdcard/st_unZip/"+unZipDir);
			File[] listFiles = uZipDir.listFiles();
			String apkPackage = "";
			for (File file : listFiles) {
				if ((file.getName()).endsWith(".apk")){
					util.install_apk(file.getAbsolutePath(),MainActivity.this);
					apkPackage = util.getApkPackageName(file.getAbsolutePath(), this);
				}
			}
			File obb_dir_exist = new File("/sdcard/Android/obb/"+apkPackage);
			if (!(obb_dir_exist.exists())){
				obb_dir_exist.mkdirs();
			}
			
			copyObb(uZipDir.getAbsolutePath(),obb_dir_exist.getAbsolutePath());
			
			
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
   

   private void copyObb(String srcPath, String dstPath) {
		// TODO Auto-generated method stub
	   File dirs = new File(srcPath);
	   File[] listFiles = dirs.listFiles();
	   for (File file : listFiles) {
		   if (file.isDirectory()){
			   copyObb(file.getAbsolutePath(), dstPath);
		   }
		   if ((file.getName()).endsWith(".obb")){
			   util.copyFile(file.getAbsolutePath(), dstPath);
		   }
			
		}
	}



private void show_apklist() {
		// TODO Auto-generated method stub
		apkList = (ListView) findViewById(R.id.apklistbview);
		apkMap = new HashMap();
		modTime = new ArrayList<Long>();
		list_file("/sdcard");
		//���ļ����޸�ʱ�䰴�Ӵ�С��˳������
		Collections.sort(modTime,new MyLongCompare());
		adapter = new ApkAdapter(this,modTime,apkMap);
		apkList.setAdapter(adapter);
		
	}

//�����ļ��� �ҳ���װ��
private void list_file(String path) {
	// TODO Auto-generated method stub
	File dir = new File(path);
	File[] listFiles = dir.listFiles();
	for (File file : listFiles) {
		if (file.isDirectory()){
			list_file(file.getAbsolutePath());
		}else{
			//�ж��ļ��ǲ���������Ҫ�Ķ���
			if(file.getName().endsWith(".apk")){
				getApkInfo(file);
			}else if (file.getName().endsWith(".xapk")||file.getName().endsWith(".dpk")
					||file.getName().endsWith(".xpk")){
				getZipIndo(file);
			}
		}
	}
}


private void getZipIndo(File file) {
	// TODO Auto-generated method stub
//	Log.i("info", "ִ����ô1");
	Bitmap bmp=BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher);
//	Log.i("info", "ִ����ô2");
	apkInfo = new ApkInfo(bmp, file.getName().toString(), "Unknow",
			file.lastModified(),file.getAbsolutePath());
	apkMap.put(String.valueOf(file.lastModified()), apkInfo);
	modTime.add(file.lastModified());
}

private void getApkInfo(File file) {
	// TODO Auto-generated method stub
	Log.i("info", file.getAbsolutePath());
	PackageManager pm = getPackageManager();
	PackageInfo pi = pm.getPackageArchiveInfo(file.getAbsolutePath(), 0);
	try {
		ApplicationInfo applicationInfo = pi.applicationInfo;
		applicationInfo.sourceDir = file.getAbsolutePath();
		applicationInfo.publicSourceDir = file.getAbsolutePath();
		apkInfo = new ApkInfo((BitmapDrawable)pm.getApplicationIcon(applicationInfo), (String)pm.getApplicationLabel(applicationInfo), applicationInfo.packageName,
				file.lastModified(),file.getAbsolutePath());
		apkMap.put(String.valueOf(file.lastModified()), apkInfo);
		modTime.add(file.lastModified());
	} catch (Exception e) {
		// TODO: handle exception
//		Toast.makeText(this, file.getAbsolutePath()+" is bad file!.", Toast.LENGTH_LONG).show();
		Log.d("badApk", file.getAbsolutePath()+" is bad file!.");
	}
	
}


public void btclick(View v){
	   switch (v.getId()) {
	case R.id.apklist:
		SharedPreferences settings = getSharedPreferences("Config.xml", MODE_PRIVATE);
		boolean jiaocheng = settings.getBoolean("jiaocheng", true);
		Log.i("info", "�̳̣�"+jiaocheng);
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
	dialog.setTitle("�̳̣�");
	dialog.setMessage("��Ҫ�鿴�����Ӧ�����ݵĽ̳�ô��");
	dialog.setPositiveButton("��", new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			show_apk_list();
			Intent intent = new Intent(MainActivity.this,JiaoCheng.class);
			
			startActivity(intent);
		}
	});
	dialog.setNeutralButton("����",new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			show_apk_list();
		}
	});
	dialog.setNegativeButton("��Ҫ�ٳ���", new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			SharedPreferences settings = getSharedPreferences("Config.xml", MODE_PRIVATE);
			boolean jiaocheng = settings.getBoolean("jiaocheng", true);
			Log.i("info", "�̳̣�"+jiaocheng);
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
