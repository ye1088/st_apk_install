package com.google.st_apk_install;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class ApkJiaoCheng extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apk_jiao_cheng);
		
	}
	
	public void finish(View v){
		switch (v.getId()) {
		case R.id.finish_2:
			finish();
			
			break;

		default:
			break;
		}
	}

}
