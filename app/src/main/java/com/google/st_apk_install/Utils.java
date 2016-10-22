package com.google.st_apk_install;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

public class Utils {
	
	private static final int BUFF_SIZE = 1024 * 1024;

	
	public String[] listTOArray(ArrayList<String> list){
		String[] result = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				result[i] = (String) list.get(i);
			}
		
		return result;
		
	}
	
	 /**
     * ��ѹ��һ���ļ�
     *
     * @param zipFile ѹ���ļ�
     * @param folderPath ��ѹ����Ŀ��Ŀ¼
     * @throws IOException ����ѹ�����̳���ʱ�׳�
     */
    public  void upZipFile(File zipFile, String folderPath) throws ZipException, IOException {
    	
    	 try {  
             ZipInputStream Zin=new ZipInputStream(new FileInputStream(zipFile));//����Դzip·��  
             BufferedInputStream Bin=new BufferedInputStream(Zin);  
             String Parent=folderPath; //���·�����ļ���Ŀ¼��  
             File Fout=null;  
             ZipEntry entry;  
             try {  
                 while((entry = Zin.getNextEntry())!=null  ){  
                	 if (entry.isDirectory()){
                		 continue;
                	 }
                     Fout=new File(Parent,entry.getName());  
                     if(!Fout.exists()){  
                         (new File(Fout.getParent())).mkdirs();  
                     }  
                     FileOutputStream out=new FileOutputStream(Fout);  
                     BufferedOutputStream Bout=new BufferedOutputStream(out);  
                     byte[] buffer = new byte[BUFF_SIZE];
                     int b;  
                     while((b=Bin.read(buffer))!=-1){  
                         Bout.write(buffer,0,b);  
                     }  
                     Bout.close();  
                     out.close();  
                     System.out.println(Fout+"��ѹ�ɹ�");      
                 }  
                 Bin.close();  
                 Zin.close();  
             } catch (IOException e) {  
                 // TODO Auto-generated catch block  
            	 Log.i("error", "�ڲ�����");
                 e.printStackTrace();  
             }  
         } catch (FileNotFoundException e) {  
             // TODO Auto-generated catch block  
        	 Log.i("error", "�ⲿ����");
             e.printStackTrace();  
         }  
//        File desDir = new File(folderPath);
//        if (!desDir.exists()) {
//            desDir.mkdirs();
//        }
//        ZipFile zf = new ZipFile(zipFile);
//        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
//            ZipEntry entry = ((ZipEntry)entries.nextElement());
//            InputStream in = zf.getInputStream(entry);
//            String str = folderPath + File.separator + entry.getName();
//            str = new String(str.getBytes("8859_1"), "GB2312");
//            File desFile = new File(str);
//            if (!desFile.exists()) {
//                File fileParentDir = desFile.getParentFile();
//                if (!fileParentDir.exists()) {
//                    fileParentDir.mkdirs();
//                }
//                desFile.createNewFile();
//            }
//            OutputStream out = new FileOutputStream(desFile);
//            byte buffer[] = new byte[BUFF_SIZE];
//            int realLength;
//            while ((realLength = in.read(buffer)) > 0) {
//                out.write(buffer, 0, realLength);
//            }
//            in.close();
//            out.close();
//        }
    }


	public String fileToString(String path){
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
		while (scanner.hasNext()){
			sb.append(scanner.nextLine());
		}

		return sb.toString();
	}
    
    protected boolean is_file_exist(String apkPath) {
		// TODO Auto-generated method stub
		return new File(apkPath).exists();
	}
    
    AlertDialog show_dialog_tip(String title,String message,String bt_text,Context context){
	AlertDialog dialog = new AlertDialog(context){
	    		
	    	} ;
    	dialog.setTitle(title);
    	dialog.setMessage(message);
    	dialog.setButton(DialogInterface.BUTTON_POSITIVE,bt_text, new DialogInterface.OnClickListener(){
    		@Override
    		public void onClick(DialogInterface arg0, int arg1) {
    			// TODO Auto-generated method stub
    			
    		}
    	});
    	dialog.show();
    	
    	return dialog;
    	
    }
    
//    public void go_home_page(Context context) {
//		// TODO Auto-generated method stub
//	Intent mHomeIntent =  new Intent(Intent.ACTION_MAIN, null);  
//	mHomeIntent.putExtra("GOHOME","GOHOME");  
//	mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);  
//	mHomeIntent.setClassName("com.android.launcher","com.android.launcher.HomeScreen");  
//	context.startActivity(mHomeIntent);   
//	}
    
    //��һ��list�е�����Ԫ�ؿ���������һ��list��
    public ArrayList copyOtherList(ArrayList src,String leixing){
    	ArrayList dst = new ArrayList();
    	for (Object object : src) {
    		if (leixing.equals("Long")){
    			dst.add((Long)object);
    		}else{
    			dst.add(object);
    		}
			
		}
    	return dst;
    }
  //��һ��map�е�����Ԫ�ؿ���������һ��map��
    public Map copyOtherMap(Map src,ArrayList keyList ,String leixing){
    	String key = "";
//    	String value = "";
    	Map dst = new HashMap();
    	dst.clear();
    	for (Object object : keyList) {
    		key = object.toString();
//    		
//    		Log.i("copyOtherMap", "key = "+key);
    		if (leixing.equals("ApkInfo")){
    			ApkInfo value =  (ApkInfo) src.get(key); 
    			dst.put(key, value);
    			Log.i("info", value.apk_name);
    		}
			
		}
    	
    	return dst;
    }

    
    public void makeDirs(String path){
    	File file = new File(path);
    	if (!file.exists()){
    		file.mkdirs();
    	}
    }
    
    

    void copyObb(String srcPath, final String dstPath) {
 		// TODO Auto-generated method stub
 	   File dirs = new File(srcPath);
 	   File[] listFiles = dirs.listFiles();
 	   for (final File file : listFiles) {
 		   if (file.isDirectory()){
 			   copyObb(file.getAbsolutePath(), dstPath);
 		   }
// 		   Log.i("obb_file", "�ҵ�һ��obb"+file.getAbsolutePath());
 		   if ((file.getName()).endsWith(".obb")){
 			   
 					   copyFile(file.getAbsolutePath(), dstPath+File.separator+file.getName());
 			
 			   
 		   }
 			
 		}
 	}

    
    //��ȡapk�İ���
    public String getApkPackageName(String apkPath,Context context){
		
    	File file = new File(apkPath);
    	PackageManager pm = context.getPackageManager();
    	PackageInfo pi = pm.getPackageArchiveInfo(file.getAbsolutePath(), 0);
    	try {
    		ApplicationInfo applicationInfo = pi.applicationInfo;
    		return applicationInfo.packageName;
    	}catch(Exception e){
    		Log.d("badApk", file.getAbsolutePath()+" is bad file!.");
    	}
    	return null;
    	
    }
    
    
    

    //��װapk�Ĵ���
    public void install_apk(String apkPath,Context context) {
		// TODO Auto-generated method stub
    	Log.i("apkPath", apkPath);
    	if(!(new File(apkPath).exists())){
    		show_dialog_tip("����", "���ʵİ�װ�������ڣ�����ˢ�°�װ���б�~"+apkPath+"1111", "ȷ��", context);
    	}
    	Intent intent = new Intent(Intent.ACTION_VIEW);
    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	intent.setAction(Intent.ACTION_VIEW);
//    	intent.setDataAndType(Uri.parse("file://"+apkPath),"application/vnd.android.package-archive");
    	intent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
    	
    	context.startActivity(intent);
		
	}
    
    public void copyFile(String srcPath, String dstPath){
    	try {
			FileInputStream fis = new FileInputStream(new File(srcPath));
			FileOutputStream fos = new FileOutputStream(new File(dstPath));
			byte[] buffer = new byte[BUFF_SIZE];
			int len;
			while ((len = fis.read(buffer, 0, buffer.length))!=-1){
				fos.write(buffer, 0, len);
			}
			
			fis.close();
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
   
}
