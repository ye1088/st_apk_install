package com.google.st_apk_install;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class ApkInfo {
	
	Bitmap icon;
	String apk_name;
	String pack_name;
	long time;
	
	public ApkInfo(BitmapDrawable icon,String apk_name,String pack_name,long time){
		this.icon = icon.getBitmap();
		this.apk_name = apk_name;
		this.pack_name = pack_name;
		this.time = time;
				
	}
	

}
