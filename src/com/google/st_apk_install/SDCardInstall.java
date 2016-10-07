package com.google.st_apk_install;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class SDCardInstall extends Activity{
	Utils utils;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		utils = new Utils();
		Intent intent = getIntent();
		Uri data = intent.getData();
		String xapkPath = data.toString().substring(7);
		if (xapkPath.endsWith(".xapk")||xapkPath.endsWith(".xpk")||xapkPath.endsWith(".dpk")){
			MainActivity.xapk_install(xapkPath,SDCardInstall.this);
		}
		
		finish();
	}

}
