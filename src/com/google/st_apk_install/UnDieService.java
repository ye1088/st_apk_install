package com.google.st_apk_install;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class UnDieService extends Service {
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
//		Intent main_intent = new Intent(this,MainActivity.class);
//		main_intent.putExtra("is_service_awake", true);
//		startActivity(main_intent);
		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Intent service = new Intent(this,UnDieService.class);
		Toast.makeText(this, "服务启动了", Toast.LENGTH_LONG).show();
		
		startService(service);
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
