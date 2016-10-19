package com.google.UtilsPack;

import java.io.File;
import java.util.ArrayList;

import com.google.st_apk_install.MainActivity;
import com.google.st_apk_install.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;

public class InstallFinishAskCopyObb extends Activity {

	Utils util = new Utils();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent start_intent = getIntent();

		if (start_intent.getBooleanExtra("is_install_back", false)){

			final ArrayList<String> obb_path = start_intent.getStringArrayListExtra("obb_path");
			final ArrayList<String> leixing = start_intent.getStringArrayListExtra("leixing");
			final String packageName = start_intent.getStringExtra("packageName");
			new AlertDialog.Builder(this).setTitle("发现该游戏存在数据包，请选择要拷贝的数据包")
					.setItems(new String[]{"1","2"}, new OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int pos) {
							// TODO Auto-generated method stub
							copyObb(obb_path.get(pos),packageName,leixing.get(pos));
						}
					}).setNegativeButton("取消", null).show();
		}
	}

	public void copyObb(String obbPath,String packName,String tag){
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
