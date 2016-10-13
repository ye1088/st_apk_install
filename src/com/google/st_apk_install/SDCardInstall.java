package com.google.st_apk_install;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.zip.ZipException;

import com.google.st_apk_install.MainActivity.InstalledReceiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class SDCardInstall extends Activity{
	Utils util;
	InstalledReceiver receiver;
	UnDieReceiver undieReceiver;
	protected void onStart() {
		super.onStart();
		receiver = new InstalledReceiver();
    	IntentFilter intentFilter = new IntentFilter();
    	intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
    	intentFilter.addDataScheme("package");
    	this.registerReceiver(receiver, intentFilter);
    	undieReceiver = new UnDieReceiver();
	}
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.arg1) {
			case 1100:
				onBackPressed();
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
		boolean is_main_intall = intent.getBooleanExtra("is_main_intall", false);
		
		if (!is_main_intall){
	    	IntentFilter intentFilter = new IntentFilter();
	    	intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
	    	intentFilter.addDataScheme("package");
	    	this.registerReceiver(undieReceiver, intentFilter);
			
		
//		Log.i("info", data.toString());
//		try {
//			Log.i("info", URLDecoder.decode(data.toString(),"UTF-8"));
//		} catch (UnsupportedEncodingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
			try {
				final String xapkPath = URLDecoder.decode(data.toString(),"UTF-8").substring(7);
			
			if (xapkPath.endsWith(".xapk")||xapkPath.endsWith(".xpk")
					||xapkPath.endsWith(".dpk")||xapkPath.endsWith(".tpk")){
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
				onBackPressed();
			}
			
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			final String tpk_path = intent.getStringExtra("tpk_path");
			new Thread(){
				public void run() {
					xapk_install(tpk_path,SDCardInstall.this);
					Message msg = handler.obtainMessage();
					msg.arg1 = 1100;
					handler.sendMessage(msg);
				};
			}.start();
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

    public class InstalledReceiver extends BroadcastReceiver {
    	

    	@Override
    	public void onReceive(Context arg0, Intent intent) {
    		// TODO Auto-generated method stub
    		if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")){
    			Log.i("receiver", "接收到信息了");
    			String packageName = intent.getDataString().substring(8);
    			Log.i("receiver", packageName);
    			File dirs = new File("/sdcard");
    			File[] listFiles = dirs.listFiles();
    			for (File file : listFiles) {
    				if (file.getName().equals(packageName)&&file.isDirectory()){
    					copyObb(file.getAbsolutePath(),packageName,"dir");
    					Log.i("receiver", "开始拷贝了");
    					break;
    				}else if (file.getName().endsWith(".obb")&&file.getName().contains(packageName)){
    					copyObb(file.getAbsolutePath(),packageName,"file");
    					break;
    				}
    			}
    		}
    	}
    	
    	private void copyObb(String obbPath,String packName,String tag){
    		File obb_dir = new File("/sdcard/Android/obb/"+packName);
    		if (!obb_dir.exists()){
    			obb_dir.mkdirs();
    		}
    		if (tag.equals("file")){
    			util.copyFile(obbPath, obb_dir.getAbsolutePath()+File.separator+obbPath.split(File.separator)[-1]);
    		}else {
    			File dir_file = new File(obbPath);
    			File[] listFiles = dir_file.listFiles();
    			for (File file : listFiles) {
    				Log.i("receiver", file.getAbsolutePath());
    				Log.i("receiver", obb_dir.getAbsolutePath()+File.separator+"11111111111111111");
    				util.copyFile(file.getAbsolutePath(), obb_dir.getAbsolutePath()+File.separator+file.getName());
    			}
    		}
    	}

    }
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	unregisterReceiver(receiver);
    	unregisterReceiver(undieReceiver);
    	super.onDestroy();
    }
   


}
