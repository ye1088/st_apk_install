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
	ArrayList real_modTime;
	Map real_apkMap;
	//����modTime�е��ļ�����޸ĵ�ʱ��� ����ȡ��Ӧ ��apk��Ϣ
	Map apkMap ;
	ApkAdapter adapter;
	static Utils util ;
	ProgressBar delay_bar;
	InstalledReceiver receiver;
	Button refresh_btn;
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			Collections.sort(real_modTime,new MyLongCompare());
//			for (Object time : real_modTime) {
//				Log.i("copyOtherMap", "key = "+time.toString());
//			}
			switch (msg.arg1) {
			case 100:
				refresh_btn.setEnabled(false);
				delay_bar.setVisibility(View.INVISIBLE);
				//���ļ����޸�ʱ�䰴�Ӵ�С��˳������
				
				adapter = new ApkAdapter(MainActivity.this,real_modTime,real_apkMap);
				apkList.setAdapter(adapter);
				
				Toast.makeText(MainActivity.this, "�󲿷ְ�װ��������ɣ����ڿ��а�װ��Ҫ��װ�������~~", Toast.LENGTH_LONG).show();
				break;
			case 101:
				//���ļ����޸�ʱ�䰴�Ӵ�С��˳������
//				Collections.sort(modTime,new MyLongCompare());
				adapter.setItemList(real_modTime, real_apkMap);
				adapter.notifyDataSetChanged();
				Toast.makeText(MainActivity.this, "���а�װ��������ɣ�֮ǰû���ҵ��İ�װ�������ڶ�������Ŷ~", Toast.LENGTH_LONG).show();
				refresh_btn.setEnabled(true);
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
		apkList = (ListView) findViewById(R.id.apklistbview);
		real_modTime = new ArrayList<Long>();
//        Intent intent = getIntent();
//        Uri data = intent.getData();
//        Log.i("uri", data.toString().substring(7));
        Intent start_intent = getIntent();
        boolean is_service_awake = start_intent.getBooleanExtra("is_service_awake", false);
        if (is_service_awake){
        	onBackPressed();
        }
        util = new Utils();
        //���ֻ��ڴ��е�����apk����ʾ����
        show_apklist();
        apkList.setOnItemClickListener(new OnItemClickListener() {
        	
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				// TODO Auto-generated method stub
				Log.i("List_Length", pos+" "+real_modTime.size());
				String apk_name = ((ApkInfo)real_apkMap.get(String.valueOf(real_modTime.get(pos)))).apk_name;
				final String apkPath = ((ApkInfo)real_apkMap.get(String.valueOf(real_modTime.get(pos)))).apkPath;
				if (util.is_file_exist(apkPath)){
					
					if (apkPath.endsWith(".apk")){
						util.install_apk(apkPath,MainActivity.this);
					}else if(apkPath.endsWith(".xapk")||apkPath.endsWith(".xpk")
							||apkPath.endsWith(".dpk")||apkPath.endsWith(".tpk")||apkPath.endsWith(".zip")){
						
//						new Thread(){
//							public void run() {
//								xapk_install(apkPath,MainActivity.this);
//								
//							};
//						}.start();
						Intent intent = new Intent(MainActivity.this,SDCardInstall.class);
						intent.putExtra("is_main_intall", true);
						intent.putExtra("tpk_path", apkPath);
						if (apkPath.endsWith(".zip")){
							intent.putExtra("is_zip", true);
						}
						startActivity(intent);
					}
				}else{
					util.show_dialog_tip("����", "���ʵİ�װ�������ڣ�����ˢ�°�װ���б�~", "ȷ��", MainActivity.this);
				}
				
			}

			
		});
    }
    

 







//����ʱ��ʼ����������  
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();

    	refresh_btn = (Button) findViewById(R.id.refresh);
//    	Intent intent = new Intent(this ,UnDieService.class);
//		this.startService(intent);
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
    
   
    public class InstalledReceiver extends BroadcastReceiver {
    	

    	@Override
    	public void onReceive(Context arg0, Intent intent) {
    		// TODO Auto-generated method stub
    		if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")){
    			Log.i("receiver", "���յ���Ϣ��");
    			String packageName = intent.getDataString().substring(8);
    			Log.i("receiver", packageName);
    			File dirs = new File("/sdcard");
    			File[] listFiles = dirs.listFiles();
    			for (File file : listFiles) {
    				if (file.getName().equals(packageName)&&file.isDirectory()){
    					copyObb(file.getAbsolutePath(),packageName,"dir");
    					Log.i("receiver", "��ʼ������");
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

//������ť�ĵ���¼�
public void btclick(View v){
	   switch (v.getId()) {
	case R.id.apklist:
		SharedPreferences settings = getSharedPreferences("Config.xml", MODE_PRIVATE);
		boolean jiaocheng = settings.getBoolean("jiaocheng", true);
		Log.i("info", "�̳̣�"+jiaocheng);
		if (jiaocheng){
			
			show_jiaocheng();
			
		}else{
			show_apkInfo_list();
		}
		
		break;
		
	case R.id.refresh:
		refresh_btn.setEnabled(false);
		delay_bar.setVisibility(View.VISIBLE);
		modTime.clear();
		apkMap.clear();
		
		show_apklist();
		
		break;

	default:
		break;
	}
	   
   }


	//xapk��xpk�İ�װ
    public static void xapk_install(String apkPath,Context context){
    	do{
	    	try {
				String unZipDir = String.valueOf((new File(apkPath)).lastModified());
				util.upZipFile(new File(apkPath), "/sdcard/st_unZip/"+unZipDir);
				File uZipDir = new File("/sdcard/st_unZip/"+unZipDir);
	//			util.show_dialog_tip("����", uZipDir.getAbsolutePath(), "ȷ��", context);
				File[] listFiles = uZipDir.listFiles();
				String apkPackage = "";
				String apkPath_obb = "";
				if (listFiles!=null){
					for (File file : listFiles) {
	//					util.show_dialog_tip("����", file.getName(), "ȷ��", context);
						if ((file.getName()).endsWith(".apk")){
	//						util.show_dialog_tip("����", file.getAbsolutePath(), "ȷ��", context);
							apkPath_obb = file.getAbsolutePath();
							
							apkPackage = util.getApkPackageName(file.getAbsolutePath(), context);
						}
					}
				}
				
				if (listFiles.length==1){
//					util.show_dialog_tip("����", "�����װ���е����⣬�뵽"+uZipDir.getAbsolutePath()+"�£��ֶ���װ��Sorry....", "ȷ��", context);
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
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub

				apkMap = new HashMap();
				modTime = new ArrayList<Long>();
				apkMap.clear();
				modTime.clear();
				list_file("/sdcard",true,false);
				list_file("/storage",true,true);
				real_apkMap = util.copyOtherMap(apkMap, modTime,"ApkInfo");
				
				real_modTime = util.copyOtherList(modTime,"Long");
//				for (Object time : real_modTime) {
//					Log.i("copyOtherMap", "key = "+time.toString());
//				}
				Message msg = handler.obtainMessage();
				msg.arg1 = 100;
				handler.sendMessage(msg);
				apkMap.clear();
				modTime.clear();
				list_file_al_package("/sdcard",true,false);
				list_file_al_package("/storage",true,true);
				real_apkMap = util.copyOtherMap(apkMap, modTime,"ApkInfo");
				
				real_modTime = util.copyOtherList(modTime,"Long");
				
				msg = handler.obtainMessage();
				msg.arg1 = 101;
				handler.sendMessage(msg);
			}
		}.start();
//		
//		new Thread(){
//			public void run() {
//				
//				
//			}
//		}.start();
		
	}



//�����ļ��� �ҳ���װ��
private void list_file(String path,boolean is_root_dir,boolean is_mnt_added_storage) {
	// TODO Auto-generated method stub
	File dir = new File(path);
	if (dir.exists()){
		File[] listFiles = dir.listFiles();
		if (listFiles!=null){
			for (File file : listFiles) {
				if (file.isDirectory()&&(file.getName().toLowerCase().contains("download")||
						file.getName().toLowerCase().contains("file_recv")||is_root_dir||is_mnt_added_storage)){
					list_file(file.getAbsolutePath(),is_mnt_added_storage,false);
				}else{
					//�ж��ļ��ǲ���������Ҫ�Ķ���
					if(file.getName().endsWith(".apk")){
						getApkInfo(file);
					}else if (file.getName().endsWith(".xapk")||file.getName().endsWith(".dpk")
							||file.getName().endsWith(".xpk")||file.getName().endsWith(".tpk")){
						getZipIndo(file);
					}
				}
			}
		}
		
	}
	
}
//�����ļ��� �ҳ����а�װ��
private void list_file_al_package(String path,boolean is_root_dir,boolean is_mnt_added_storage) {
	// TODO Auto-generated method stub
	File dir = new File(path);
	if (dir.exists()){
		File[] listFiles = dir.listFiles();
		if (listFiles!=null){
			for (File file : listFiles) {
				if (file.isDirectory()){//&&!(file.getName().toLowerCase().contains("download")||
					//file.getName().toLowerCase().contains("file_recv"))
					list_file_al_package(file.getAbsolutePath(),is_mnt_added_storage,false);
				}else{
					//�ж��ļ��ǲ���������Ҫ�Ķ���
					if(file.getName().endsWith(".apk")){
						getApkInfo(file);
					}else if (file.getName().endsWith(".xapk")||file.getName().endsWith(".dpk")
							||file.getName().endsWith(".xpk")||file.getName().endsWith(".tpk")){
						getZipIndo(file);
					}
				}
			}
		}
		
	}
	
}


private void getZipIndo(File file) {
	// TODO Auto-generated method stub
//	Log.i("info", "ִ����ô1");
	if (apkMap.get(String.valueOf(file.lastModified()))==null){
		Bitmap bmp=BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher);
	//	Log.i("info", "ִ����ô2");
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
	dialog.setTitle("�̳̣�");
	dialog.setMessage("��Ҫ�鿴�����Ӧ�����ݵĽ̳�ô��");
	dialog.setPositiveButton("��", new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			show_apkInfo_list();
			Intent intent = new Intent(MainActivity.this,JiaoCheng.class);
			
			startActivity(intent);
		}
	});
	dialog.setNeutralButton("����",new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			show_apkInfo_list();
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
