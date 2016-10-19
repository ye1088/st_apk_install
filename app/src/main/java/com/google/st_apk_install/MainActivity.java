package com.google.st_apk_install;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipException;

import com.google.UtilsPack.InstallFinishAskCopyObb;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.st_apk_install.InstalledReceiver.IRInterface;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.support.annotation.RequiresApi;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity implements IRInterface {
	ListView apkList;
	ArrayList modTime;
	ApkInfo apkInfo;
	ArrayList real_modTime;
	Map real_apkMap;
	//根据modTime中的文件最后修改的时间戳 来获取对应 的apk信息
	Map apkMap;
	ApkAdapter adapter;
	static Utils util;
	ProgressBar delay_bar;
	InstalledReceiver receiver;
	Button refresh_btn;
	AlertDialog copy_obb_dialog;
	SharedPreferences settings;


	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Collections.sort(real_modTime, new MyLongCompare());
//			for (Object time : real_modTime) {
//				Log.i("copyOtherMap", "key = "+time.toString());
//			}
			switch (msg.arg1) {
				case 100:
					refresh_btn.setEnabled(false);
					delay_bar.setVisibility(View.INVISIBLE);
					//将文件的修改时间按从大到小的顺序排列

					adapter = new ApkAdapter(MainActivity.this, real_modTime, real_apkMap);
					apkList.setAdapter(adapter);

					Toast.makeText(MainActivity.this, "大部分安装包加载完成，现在可有安装需要安装的软件啦~~", Toast.LENGTH_LONG).show();
					save_apk_list();
					break;
				case 101:
					//将文件的修改时间按从大到小的顺序排列
//				Collections.sort(modTime,new MyLongCompare());
					adapter.setItemList(real_modTime, real_apkMap);
					adapter.notifyDataSetChanged();
					Toast.makeText(MainActivity.this, "所有安装包加载完成，之前没有找到的安装包，现在都出现了哦~", Toast.LENGTH_LONG).show();
					refresh_btn.setEnabled(true);
					break;
				case 200:
					copy_obb_dialog.dismiss();
				case 201:
					Toast.makeText(MainActivity.this, "数据包拷贝完成，可以打开游戏玩耍啦~~", Toast.LENGTH_LONG).show();
					break;

				default:
					break;
			}

		}

		;
	};

	

	private NotificationManager notificationManager;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		apkList = (ListView) findViewById(R.id.apklistbview);
		real_modTime = new ArrayList<Long>();
//        Intent intent = getIntent();
//        Uri data = intent.getData();
//        Log.i("uri", data.toString().substring(7));
		Intent start_intent = getIntent();
		boolean is_service_awake = start_intent.getBooleanExtra("is_service_awake", false);
		if (is_service_awake) {
			onBackPressed();
		}

		util = new Utils();
		//将手机内存中的所有apk等显示出来
		show_apklist();
		apkList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
									long arg3) {
				// TODO Auto-generated method stub
				Log.i("List_Length", pos + " " + real_modTime.size());
				String apk_name = ((ApkInfo) real_apkMap.get(String.valueOf(real_modTime.get(pos)))).apk_name;
				final String apkPath = ((ApkInfo) real_apkMap.get(String.valueOf(real_modTime.get(pos)))).apkPath;
				if (util.is_file_exist(apkPath)) {

					if (apkPath.endsWith(".apk")) {
						util.install_apk(apkPath, MainActivity.this);
					} else if (apkPath.endsWith(".xapk") || apkPath.endsWith(".xpk")
							|| apkPath.endsWith(".dpk") || apkPath.endsWith(".tpk") || apkPath.endsWith(".zip")) {

//						new Thread(){
//							public void run() {
//								xapk_install(apkPath,MainActivity.this);
//
//							};
//						}.start();
						Intent intent = new Intent(MainActivity.this, SDCardInstall.class);
						intent.putExtra("is_main_intall", true);
						intent.putExtra("tpk_path", apkPath);
						if (apkPath.endsWith(".zip")) {
							intent.putExtra("is_zip", true);
						}
						startActivity(intent);
					}
				} else {
					util.show_dialog_tip("错误", "访问的安装包不存在！！请刷新安装包列表！~", "确定", MainActivity.this);
				}

			}


		});
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	//将apk列表保存到本地文件中
	private void save_apk_list() {
		try {
			JSONArray apk_list_ja = new JSONArray();
			for (Object sub_real_modTime:real_modTime
				 ) {
				JSONObject jo = new JSONObject();
				Long tmp_long_time = (long)sub_real_modTime;
				jo.put("time",tmp_long_time);
				ApkInfo tmp_apkInfo = (ApkInfo) real_apkMap.get(String.valueOf(tmp_long_time));
				jo.put("apk_name",tmp_apkInfo.apk_name);
				jo.put("apkPath",tmp_apkInfo.apkPath);
				jo.put("pack_name",tmp_apkInfo.pack_name);
				apk_list_ja.put(jo);
			}
			File exist_dir = new File("/sdcard/st_unZip");
			if (!exist_dir.exists()) exist_dir.mkdir();
			BufferedWriter bw = new BufferedWriter(new FileWriter("/sdcard/st_unZip/apk_list.json"));
			bw.write(apk_list_ja.toString());
			bw.flush();
			bw.close();
		} catch (JSONException e) {
			Log.d("save_apk_list","jsonarray没有创建成功");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	//启动时初始化各项配置
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		settings = getSharedPreferences("Config.xml",MODE_PRIVATE);
		boolean isResgisterReceiver = settings.getBoolean("isResgisterReceiver",false);
		refresh_btn = (Button) findViewById(R.id.refresh);
//    	Intent intent = new Intent(this ,UnDieService.class);
//		this.startService(intent);
		if (!isResgisterReceiver){
			Editor edit = settings.edit();
			edit.putBoolean("isResgisterReceiver",true);
			receiver = new InstalledReceiver();
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
			intentFilter.addDataScheme("package");
			this.registerReceiver(receiver, intentFilter);
			receiver.setIRInterfaceListener(this);
			this.registerReceiver(new UnDieReceiver(), intentFilter);
		}

		delay_bar = (ProgressBar) findViewById(R.id.delay);
		settings = getSharedPreferences("Config.xml", MODE_PRIVATE);
		boolean isfirst = settings.getBoolean("isFirst", true);
		if (isfirst) {
			init();
			Editor edit = settings.edit();
			edit.putBoolean("isFirst", false);
			edit.commit();
		}

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		AppIndex.AppIndexApi.start(client, getIndexApiAction());
	}

	public void copyObb(String obbPath, String packName, String tag) {
		File obb_dir = new File("/sdcard/Android/obb/" + packName);
		if (!obb_dir.exists()) {
			obb_dir.mkdirs();
		}
		if (tag.equals("file")) {
			util.copyFile(obbPath, obb_dir.getAbsolutePath() + File.separator + obbPath.split(File.separator)[obbPath.split(File.separator).length-1]);
		} else {
			File dir_file = new File(obbPath);
			File[] listFiles = dir_file.listFiles();
			for (File file : listFiles) {
				Log.i("receiver", file.getAbsolutePath());
				Log.i("receiver", obb_dir.getAbsolutePath() + File.separator + "11111111111111111");
				util.copyFile(file.getAbsolutePath(), obb_dir.getAbsolutePath() + File.separator + file.getName());
			}
		}
	}


	private void init() {
		// TODO Auto-generated method stub

		util.makeDirs("/sdcard/st_unZip");
		util.makeDirs("/sdcard/Android/obb");
		Intent intent = new Intent(this, ApkJiaoCheng.class);
		startActivity(intent);

	}

	//各个按钮的点击事件
	public void btclick(View v) {
		switch (v.getId()) {
			case R.id.apklist:
				SharedPreferences settings = getSharedPreferences("Config.xml", MODE_PRIVATE);
				boolean jiaocheng = settings.getBoolean("jiaocheng", true);
				Log.i("info", "教程：" + jiaocheng);
				if (jiaocheng) {

					show_jiaocheng();

				} else {
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


	//xapk和xpk的安装
	public static void xapk_install(String apkPath, Context context) {
		do {
			try {
				String unZipDir = String.valueOf((new File(apkPath)).lastModified());
				util.upZipFile(new File(apkPath), "/sdcard/st_unZip/" + unZipDir);
				File uZipDir = new File("/sdcard/st_unZip/" + unZipDir);
				//			util.show_dialog_tip("错误", uZipDir.getAbsolutePath(), "确定", context);
				File[] listFiles = uZipDir.listFiles();
				String apkPackage = "";
				String apkPath_obb = "";
				if (listFiles != null) {
					for (File file : listFiles) {
						//					util.show_dialog_tip("错误", file.getName(), "确定", context);
						if ((file.getName()).endsWith(".apk")) {
							//						util.show_dialog_tip("错误", file.getAbsolutePath(), "确定", context);
							apkPath_obb = file.getAbsolutePath();

							apkPackage = util.getApkPackageName(file.getAbsolutePath(), context);
						}
					}
				}

				if (listFiles.length == 1) {
//					util.show_dialog_tip("错误：", "这个安装包有点问题，请到"+uZipDir.getAbsolutePath()+"下，手动安装，Sorry....", "确定", context);
					break;
				}

				File obb_dir_exist = new File("/sdcard/Android/obb/" + apkPackage);
				if (!(obb_dir_exist.exists())) {
					obb_dir_exist.mkdirs();
				}

				util.copyObb(uZipDir.getAbsolutePath(), obb_dir_exist.getAbsolutePath());

				Log.i("apkPath", apkPath_obb);
				util.install_apk(apkPath_obb, context);


			} catch (ZipException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (false);
	}


	private void show_apklist() {
		// TODO Auto-generated method stub
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				apkMap = new HashMap();
				modTime = new ArrayList<Long>();
				apkMap.clear();
				modTime.clear();
				list_file("/sdcard", true, false);
				list_file("/storage", true, true);
				real_apkMap = util.copyOtherMap(apkMap, modTime, "ApkInfo");

				real_modTime = util.copyOtherList(modTime, "Long");
//				for (Object time : real_modTime) {
//					Log.i("copyOtherMap", "key = "+time.toString());
//				}
				Message msg = handler.obtainMessage();
				msg.arg1 = 100;
				handler.sendMessage(msg);
				apkMap.clear();
				modTime.clear();
				list_file_al_package("/sdcard", true, false);
				list_file_al_package("/storage", true, true);
				real_apkMap = util.copyOtherMap(apkMap, modTime, "ApkInfo");

				real_modTime = util.copyOtherList(modTime, "Long");

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


	//遍历文件夹 找出安装包
	private void list_file(String path, boolean is_root_dir, boolean is_mnt_added_storage) {
		// TODO Auto-generated method stub
		File dir = new File(path);
		if (dir.exists()) {
			File[] listFiles = dir.listFiles();
			if (listFiles != null) {
				for (File file : listFiles) {
					if (file.isDirectory() && (file.getName().toLowerCase().contains("download") ||
							file.getName().toLowerCase().contains("file_recv") || is_root_dir || is_mnt_added_storage)) {
						list_file(file.getAbsolutePath(), is_mnt_added_storage, false);
					} else {
						//判断文件是不是我们需要的东东
						if (file.getName().endsWith(".apk")) {
							getApkInfo(file);
						} else if (file.getName().endsWith(".xapk") || file.getName().endsWith(".dpk")
								|| file.getName().endsWith(".xpk") || file.getName().endsWith(".tpk")) {
							getZipIndo(file);
						}
					}
				}
			}

		}

	}

	//遍历文件夹 找出所有安装包
	private void list_file_al_package(String path, boolean is_root_dir, boolean is_mnt_added_storage) {
		// TODO Auto-generated method stub
		File dir = new File(path);
		if (dir.exists()) {
			File[] listFiles = dir.listFiles();
			if (listFiles != null) {
				for (File file : listFiles) {
					if (file.isDirectory()) {//&&!(file.getName().toLowerCase().contains("download")||
						//file.getName().toLowerCase().contains("file_recv"))
						list_file_al_package(file.getAbsolutePath(), is_mnt_added_storage, false);
					} else {
						//判断文件是不是我们需要的东东
						if (file.getName().endsWith(".apk")) {
							getApkInfo(file);
						} else if (file.getName().endsWith(".xapk") || file.getName().endsWith(".dpk")
								|| file.getName().endsWith(".xpk") || file.getName().endsWith(".tpk")) {
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
		if (apkMap.get(String.valueOf(file.lastModified())) == null) {
			Bitmap bmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher);
			//	Log.i("info", "执行了么2");
			apkInfo = new ApkInfo(bmp, file.getName().toString(), "Unknow",
					file.lastModified(), file.getAbsolutePath());
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
			if (apkMap.get(String.valueOf(file.lastModified())) == null) {

				ApplicationInfo applicationInfo = pi.applicationInfo;
				applicationInfo.sourceDir = file.getAbsolutePath();
				applicationInfo.publicSourceDir = file.getAbsolutePath();
				apkInfo = new ApkInfo((BitmapDrawable) pm.getApplicationIcon(applicationInfo), (String) pm.getApplicationLabel(applicationInfo), applicationInfo.packageName,
						file.lastModified(), file.getAbsolutePath());
				apkMap.put(String.valueOf(file.lastModified()), apkInfo);
				modTime.add(file.lastModified());
			}
		} catch (Exception e) {
			// TODO: handle exception
//		Toast.makeText(this, file.getAbsolutePath()+" is bad file!.", Toast.LENGTH_LONG).show();
			Log.d("badApk", file.getAbsolutePath() + " is bad file!.");
		}

	}


	private void show_jiaocheng() {
		// TODO Auto-generated method stub
		Builder dialog = new Builder(this);
		dialog.setTitle("教程？");
		dialog.setMessage("需要查看下清除应用数据的教程么？");
		dialog.setPositiveButton("好", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				show_apkInfo_list();
				Intent intent = new Intent(MainActivity.this, JiaoCheng.class);

				startActivity(intent);
			}
		});
		dialog.setNeutralButton("不用", new OnClickListener() {

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
				Log.i("info", "教程：" + jiaocheng);
				if (jiaocheng) {
					Editor edit = settings.edit();
					edit.putBoolean("jiaocheng", false);
					edit.commit();
				}

				show_apkInfo_list();
			}
		});
		dialog.create().show();
	}

	public void show_apkInfo_list() {

		Intent intent = new Intent();
		ComponentName comp = new ComponentName("com.android.settings",
				"com.android.settings.ManageApplications");
		intent.setComponent(comp);
		intent.setAction("android.intent.action.VIEW");
		startActivityForResult(intent, 0);
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
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void show_obb_dialog(final ArrayList<String> obb_path,
								final ArrayList<String> leixing, final String packageName) {
		// TODO Auto-generated method stub
//	ArrayList<String> obb_path = start_intent.getStringArrayListExtra("obb_path");
//	ArrayList<String> leixing = start_intent.getStringArrayListExtra("leixing");
//	String packageName = start_intent.getStringExtra("packageName");
		//(String[])obb_path.toArray()
		showNotification("刚安装的软件可能有数据包", "安装器提示:", "可能有数据包，需要拷贝么？", 1, R.drawable.ic_launcher);
		copy_obb_dialog = new Builder(MainActivity.this).setTitle("发现该游戏存在数据包，请选择要拷贝的数据包")
				.setItems(util.listTOArray(obb_path), new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, final int pos) {
						// TODO Auto-generated method stub
						new Thread() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								super.run();
								Message msg = handler.obtainMessage();
								msg.arg1 = 200;
								handler.sendMessage(msg);
								copyObb(obb_path.get(pos), packageName, leixing.get(pos));
								msg = handler.obtainMessage();
								msg.arg1 = 201;
								handler.sendMessage(msg);

							}
						}.start();


					}
				}).setNegativeButton("取消", null).show();

//	Toast.makeText(this, "该软件可能有数据包，请点开通知栏查看详情！", Toast.LENGTH_LONG).show();


//	Intent install_back = new Intent(MainActivity.this, InstallFinishAskCopyObb.class);
//	install_back.putStringArrayListExtra("obb_path", obb_path);
//	install_back.putStringArrayListExtra("leixing", leixing);
//	install_back.putExtra("packageName", packageName);
//	install_back.putExtra("is_install_back", true);
//	install_back.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//	MainActivity.this.startActivity(install_back);
	}

	//显示系统通知
	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
	private void showNotification(String tickerText, String contentTitle, String contentText, int id, int resId) {

//		new Notification

//		resId, tickerText, System.currentTimeMillis()
//		Notification.Builder notification = new Notification.Builder(this);
//		notification.defaults = Notification.DEFAULT_SOUND;
//		notification.flags = Notification.FLAG_AUTO_CANCEL;
//		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, getIntent(), 0);
//		notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
//		notificationManager.notify(id, notification);
		Notification.Builder builder = new Notification.Builder(this);
//		builder.setContentInfo(tickerText);
		builder.setContentText(contentText);
		builder.setContentTitle(contentTitle);
		builder.setSmallIcon(resId);
		builder.setTicker(tickerText);
		builder.setAutoCancel(true);
		builder.setWhen(System.currentTimeMillis());
//		Intent intent = this.getIntent();
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, getIntent(), 0);
		builder.setContentIntent(pendingIntent);
		Notification notification = builder.build();

		notificationManager.notify(id, notification);



	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(receiver);
		Editor edit = settings.edit();
		edit.putBoolean("isResgisterReceiver",false);
		super.onDestroy();
	}

	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	public Action getIndexApiAction() {
		Thing object = new Thing.Builder()
				.setName("Main Page") // TODO: Define a title for the content shown.
				// TODO: Make sure this auto-generated URL is correct.
				.setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
				.build();
		return new Action.Builder(Action.TYPE_VIEW)
				.setObject(object)
				.setActionStatus(Action.STATUS_TYPE_COMPLETED)
				.build();
	}

	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		AppIndex.AppIndexApi.end(client, getIndexApiAction());
		client.disconnect();
	}
}
