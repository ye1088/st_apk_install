package com.google.st_apk_install;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class SDCardInstall extends Activity{
	Utils util;
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.arg1) {
			case 1100:
				finish();
				break;

			default:
				break;
			}
			
		};
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sdcard_install);
		util = new Utils();
		Intent intent = getIntent();
		Uri data = intent.getData();
		try {
			final String xapkPath = new String((data.toString().substring(7)).getBytes(),"utf-8");
		
		if (xapkPath.endsWith(".xapk")||xapkPath.endsWith(".xpk")||xapkPath.endsWith(".dpk")){
			new Thread(){
				public void run() {
					xapk_install(xapkPath,SDCardInstall.this);
					Message msg = handler.obtainMessage();
					msg.arg1 = 1100;
					handler.sendMessage(msg);
				};
			}.start();
			
		}else{
			Toast.makeText(this, xapkPath+" 不是一个安装包", Toast.LENGTH_LONG).show();
		}
		
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		finish();
	}
	
	

	//xapk和xpk的安装
    public  void xapk_install(String apkPath,Context context){
    	try {
			String unZipDir = String.valueOf((new File(apkPath)).lastModified()+1);
			util.upZipFile(new File(apkPath), "/sdcard/st_unZip/"+unZipDir);
			File uZipDir = new File("/sdcard/st_unZip/"+unZipDir);
			File[] listFiles = uZipDir.listFiles();
			String apkPackage = "";
			String apkPath_obb = "";
			if (listFiles!=null){
				for (File file : listFiles) {
					if ((file.getName()).endsWith(".apk")){
						apkPath_obb = file.getAbsolutePath();
						
						apkPackage = util.getApkPackageName(file.getAbsolutePath(), context);
					}
				}
			}
			
			File obb_dir_exist = new File("/sdcard/Android/obb/"+apkPackage);
			if (!(obb_dir_exist.exists())){
				obb_dir_exist.mkdirs();
			}
			
			util.copyObb(uZipDir.getAbsolutePath(),obb_dir_exist.getAbsolutePath());
			util.install_apk(apkPath_obb,context);
			
			
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
   


}
