package com.google.st_apk_install;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class JiaoCheng extends Activity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jiao_cheng);
	}


	private void finish(View v){
		switch (v.getId()) {
		case R.id.finish:
			finish();
			
			
			break;

		default:
//			finish();
			break;
		}
	}
		
	

}
