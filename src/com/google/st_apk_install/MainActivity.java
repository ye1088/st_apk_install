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
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {
	ListView apkList ;
	ArrayList modTime;
	ApkInfo apkInfo;
	//根据modTime中的文件最后修改的时间戳 来获取对应 的apk信息
	Map apkMap ;
	ApkAdapter adapter;
	static Utils util ;
	ProgressBar delay_bar;
	InstalledReceiver receiver;
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {

			switch (msg.arg1) {
			case 100:
				delay_bar.setVisibility(View.INVISIBLE);
				//将文件的修改时间按从大到小的顺序排列
				Collections.sort(modTime,new MyLongCompare());
				adapter = new ApkAdapter(MainActivity.this,modTime,apkMap);
				apkList.setAdapter(adapter);
				break;

			default:
				break;
			}
			
		};
	};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Intent intent = getIntent();
//        Uri data = intent.getData();
//        Log.i("uri", data.toString().substring(7));
        util = new Utils();
        //将手机内存中的所有apk等显示出来
        show_apklist();
        apkList.setOnItemClickListener(new OnItemClickListener() {
        	
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				// TODO Auto-generated method stub
				Log.i("List_Length", pos+" "+modTime.size());
				String apk_name = ((ApkInfo)apkMap.get(String.valueOf(modTime.get(pos)))).apk_name;
				final String apkPath = ((ApkInfo)apkMap.get(String.valueOf(modTime.get(pos)))).apkPath;
				if (util.is_file_exist(apkPath)){
					
					if (apkPath.endsWith(".apk")){
						util.install_apk(apkPath,MainActivity.this);
					}else if(apkPath.endsWith(".xapk")||apkPath.endsWith(".xpk")||apkPath.endsWith(".dpk")){
						
//						new Thread(){
//							public void run() {
								xapk_install(apkPath,MainActivity.this);
								
//							};
//						}.start();
						
					}
				}else{
					util.show_dialog_tip("错误", "访问的安装包不存在！！请刷新安装包列表！~", "确定", MainActivity.this);
				}
				
			}

			
		});
    }
    

 


//启动时初始化各项配置  
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    	Intent intent = new Intent(this ,UnDieService.class);
		this.startService(intent);
    	receiver = new InstalledReceiver();
    	IntentFilter intentFilter = new IntentFilter();
    	intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
    	intentFilter.addDataScheme("package");
    	this.registerReceiver(receiver, intentFilter);
    	this.registerReceiver(new UnDieReceiver(), intentFilter);
    	
    	delay_bar = (ProgressBar) findViewById(R.id.delay);
    	SharedPreferences settings = getSharedPreferences("Config.xml", MODE_PRIVATE);
    	boolean isfirst = settings.getBoolean("isFirst", true);
    	if (isfirst){
    		init();
    		Editor edit = settings.edit();
    		edit.putBoolean("isFirst", false);
    		edit.commit();
    	}
    	
    }
    
   
    class InstalledReceiver extends BroadcastReceiver {
    	

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
    
    private void init() {
		// TODO Auto-generated method stub
    	
		util.makeDirs("/sdcard/st_unZip");
		util.makeDirs("/sdcard/Android/obb");
		Intent intent = new Intent(this,ApkJiaoCheng.class);
		startActivity(intent);
		
	}

//各个按钮的点击事件
public void btclick(View v){
	   switch (v.getId()) {
	case R.id.apklist:
		SharedPreferences settings = getSharedPreferences("Config.xml", MODE_PRIVATE);
		boolean jiaocheng = settings.getBoolean("jiaocheng", true);
		Log.i("info", "教程："+jiaocheng);
		if (jiaocheng){
			
			show_jiaocheng();
			
		}else{
			show_apkInfo_list();
		}
		
		break;
		
	case R.id.refresh:
		delay_bar.setVisibility(View.VISIBLE);
		show_apklist();
		break;

	default:
		break;
	}
	   
   }


	//xapk和xpk的安装
    public static void xapk_install(String apkPath,Context context){
    	do{
	    	try {
				String unZipDir = String.valueOf((new File(apkPath)).lastModified());
				util.upZipFile(new File(apkPath), "/sdcard/st_unZip/"+unZipDir);
				File uZipDir = new File("/sdcard/st_unZip/"+unZipDir);
	//			util.show_dialog_tip("错误", uZipDir.getAbsolutePath(), "确定", context);
				File[] listFiles = uZipDir.listFiles();
				String apkPackage = "";
				String apkPath_obb = "";
				if (listFiles!=null){
					for (File file : listFiles) {
	//					util.show_dialog_tip("错误", file.getName(), "确定", context);
						if ((file.getName()).endsWith(".apk")){
	//						util.show_dialog_tip("错误", file.getAbsolutePath(), "确定", context);
							apkPath_obb = file.getAbsolutePath();
							
							apkPackage = util.getApkPackageName(file.getAbsolutePath(), context);
						}
					}
				}
				
				if (listFiles.length==1){
					util.show_dialog_tip("错误：", "这个安装包有点问题，请到"+uZipDir.getAbsolutePath()+"下，手动安装，Sorry....", "确定", context);
					break;
				}
				
				File obb_dir_exist = new File("/sdcard/Android/obb/"+apkPackage);
				if (!(obb_dir_exist.exists())){
					obb_dir_exist.mkdirs();
				}
				
				util.copyObb(uZipDir.getAbsolutePath(),obb_dir_exist.getAbsolutePath());
				
				Log.i("apkPath", apkPath_obb);
				util.install_apk(apkPath_obb,context);
				
				
			} catch (ZipException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}while(false);
    }
    
   



private void show_apklist() {
		// TODO Auto-generated method stub
		apkList = (ListView) findViewById(R.id.apklistbview);
		apkMap = new HashMap();
		modTime = new ArrayList<Long>();
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				list_file("/sdcard");
				Message msg = handler.obtainMessage();
				msg.arg1 = 100;
				handler.sendMessage(msg);
			}
		}.start();
		
	}



//遍历文件夹 找出安装包
private void list_file(String path) {
	// TODO Auto-generated method stub
	File dir = new File(path);
	if (dir.exists()){
		File[] listFiles = dir.listFiles();
		if (listFiles!=null){
			for (File file : listFiles) {
				if (file.isDirectory()){
					list_file(file.getAbsolutePath());
				}else{
					//判断文件是不是我们需要的东东
					if(file.getName().endsWith(".apk")){
						getApkInfo(file);
					}else if (file.getName().endsWith(".xapk")||file.getName().endsWith(".dpk")
							||file.getName().endsWith(".xpk")){
						getZipIndo(file);
					}
				}
			}
		}
		
	}
	
}


private void getZipIndo(File file) {
	// TODO Auto-generated method stub
//	Log.i("info", "执行了么1");
	if (apkMap.get(String.valueOf(file.lastModified()))==null){
		Bitmap bmp=BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher);
	//	Log.i("info", "执行了么2");
		apkInfo = new ApkInfo(bmp, file.getName().toString(), "Unknow",
				file.lastModified(),file.getAbsolutePath());
		apkMap.put(String.valueOf(file.lastModified()), apkInfo);
		modTime.add(file.lastModified());
	}
}

private void getApkInfo(File file) {
	// TODO Auto-generated method stub
	Log.i("info", file.getAbsolutePath());
	PackageManager pm = getPackageManager();
	PackageInfo pi = pm.getPackageArchiveInfo(file.getAbsolutePath(), 0);
	try {
		if (apkMap.get(String.valueOf(file.lastModified()))==null){

			ApplicationInfo applicationInfo = pi.applicationInfo;
			applicationInfo.sourceDir = file.getAbsolutePath();
			applicationInfo.publicSourceDir = file.getAbsolutePath();
			apkInfo = new ApkInfo((BitmapDrawable)pm.getApplicationIcon(applicationInfo), (String)pm.getApplicationLabel(applicationInfo), applicationInfo.packageName,
					file.lastModified(),file.getAbsolutePath());
			apkMap.put(String.valueOf(file.lastModified()), apkInfo);
			modTime.add(file.lastModified());
		}
	} catch (Exception e) {
		// TODO: handle exception
//		Toast.makeText(this, file.getAbsolutePath()+" is bad file!.", Toast.LENGTH_LONG).show();
		Log.d("badApk", file.getAbsolutePath()+" is bad file!.");
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
			show_apkInfo_list();
			Intent intent = new Intent(MainActivity.this,JiaoCheng.class);
			
			startActivity(intent);
		}
	});
	dialog.setNeutralButton("不用",new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			show_apkInfo_list();
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
			
			show_apkInfo_list();
		}
	});
	dialog.create().show();
}

public void show_apkInfo_list(){

	Intent intent = new Intent();
	ComponentName comp = new ComponentName("com.android.settings",
	"com.android.settings.ManageApplications");
	intent.setComponent(comp);
	intent.setAction("android.intent.action.VIEW");
	startActivityForResult( intent , 0);
}

@Override
protected void onPause() {
	// TODO Auto-generated method stub
	super.onPause();
//	show_apklist();
}

@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
	// TODO Auto-generated method stub
	if (keyCode==KeyEvent.KEYCODE_BACK){
		moveTaskToBack(false);
		return true;
	}
	return super.onKeyDown(keyCode, event);
}
    
}
