package com.google.st_apk_install;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.util.Log;

public class UnDieReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		// TODO Auto-generated method stub
		Log.i("service11", "广播已启动");
		Intent intent = new Intent(context ,UnDieService.class);
		context.startService(intent);

	}


}
