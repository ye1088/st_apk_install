package com.google.st_apk_install;

import java.io.File;
import java.util.ArrayList;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class InstalledReceiver extends BroadcastReceiver {
	
	private IRInterface irinterface;
	ArrayList obb_path = new ArrayList<String>();
	ArrayList leixing = new ArrayList<String>();
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")){
			Log.i("receiver", "���յ���Ϣ��");
			final String packageName = intent.getDataString().substring(8);
			Log.i("receiver", packageName);
			obb_path.clear();
			leixing.clear();
			receive_copyObb("/sdcard", packageName);
			if (!obb_path.isEmpty()){
				irinterface.show_obb_dialog(obb_path, leixing, packageName);
			}
//			Intent install_back = new Intent(context, InstalledReceiver.class);
//			install_back.putStringArrayListExtra("obb_path", obb_path);
//			install_back.putStringArrayListExtra("leixing", leixing);
//			install_back.putExtra("packageName", packageName);
//			install_back.putExtra("is_install_back", true);
//			install_back.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			context.startActivity(install_back);
			
//			new AlertDialog.Builder(MainActivity.this).setTitle("���ָ���Ϸ�������ݰ�����ѡ��Ҫ���������ݰ�")
//			.setItems((String[])obb_path.toArray(), new OnClickListener() {
//				
//				@Override
//				public void onClick(DialogInterface arg0, int pos) {
//					// TODO Auto-generated method stub
//					copyObb((String)obb_path.get(pos),packageName,(String)leixing.get(pos));
//				}
//			}).setNegativeButton("ȡ��", null).show();
//			new File("")
		}
	}
	
	//����Ӧ�ð�װ��ɺ� ��ɨ����dir_path ����û����ص�obb�ļ�
	public void receive_copyObb(String dir_path,String packageName){

		File dirs = new File(dir_path);
		File[] listFiles = dirs.listFiles();
		if (listFiles.length>0){
			
			for (File file : listFiles) {
				if (file.getName().equals(packageName)&&file.isDirectory()){
					obb_path.add(file.getAbsolutePath());
					leixing.add("dir");
//					copyObb(file.getAbsolutePath(),packageName,"dir");
					Log.i("receiver", "��ʼ������");
				}else if (file.getName().endsWith(".obb")&&file.getName().contains(packageName)){
					obb_path.add(file.getAbsolutePath());
					leixing.add("file");
//					copyObb(file.getAbsolutePath(),packageName,"file");
				}
			}
		}
	}
	
	public interface IRInterface {
		public void show_obb_dialog(ArrayList<String> obb_path, ArrayList<String> leixing, String packageName);
	}
	
	public void setIRInterfaceListener(IRInterface irinterface){
		this.irinterface = irinterface;
	}
//	public void copyObb(String obbPath,String packName,String tag){
//		File obb_dir = new File("/sdcard/Android/obb/"+packName);
//		if (!obb_dir.exists()){
//			obb_dir.mkdirs();
//		}
//		if (tag.equals("file")){
//			util.copyFile(obbPath, obb_dir.getAbsolutePath()+File.separator+obbPath.split(File.separator)[-1]);
//		}else {
//			File dir_file = new File(obbPath);
//			File[] listFiles = dir_file.listFiles();
//			for (File file : listFiles) {
//				Log.i("receiver", file.getAbsolutePath());
//				Log.i("receiver", obb_dir.getAbsolutePath()+File.separator+"11111111111111111");
//				util.copyFile(file.getAbsolutePath(), obb_dir.getAbsolutePath()+File.separator+file.getName());
//			}
//		}
//	}

}