package com.google.st_apk_install;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class JiaoCheng extends Activity {
	
//	MainActivity mainActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
//		mainActivity = new MainActivity();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
//		mainActivity.show_apk_list();
	}
	
//	private void finish(View v){
//		switch (v.getId()) {
//		case R.id.finish:
//			
//			
//			finish();
//			break;
//
//		default:
//			finish();
//			break;
//		}
//	}
		
	

}
