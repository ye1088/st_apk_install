package com.google.st_apk_install;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

public class Utils {
	
	private static final int BUFF_SIZE = 1024 * 1024;

	
	 /**
     * 解压缩一个文件
     *
     * @param zipFile 压缩文件
     * @param folderPath 解压缩的目标目录
     * @throws IOException 当解压缩过程出错时抛出
     */
    public  void upZipFile(File zipFile, String folderPath) throws ZipException, IOException {
        File desDir = new File(folderPath);
        if (!desDir.exists()) {
            desDir.mkdirs();
        }
        ZipFile zf = new ZipFile(zipFile);
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
            ZipEntry entry = ((ZipEntry)entries.nextElement());
            InputStream in = zf.getInputStream(entry);
            String str = folderPath + File.separator + entry.getName();
            str = new String(str.getBytes("8859_1"), "GB2312");
            File desFile = new File(str);
            if (!desFile.exists()) {
                File fileParentDir = desFile.getParentFile();
                if (!fileParentDir.exists()) {
                    fileParentDir.mkdirs();
                }
                desFile.createNewFile();
            }
            OutputStream out = new FileOutputStream(desFile);
            byte buffer[] = new byte[BUFF_SIZE];
            int realLength;
            while ((realLength = in.read(buffer)) > 0) {
                out.write(buffer, 0, realLength);
            }
            in.close();
            out.close();
        }
    }
    
    public void makeDirs(String path){
    	File file = new File(path);
    	if (!file.exists()){
    		file.mkdirs();
    	}
    }
    
    
    //获取apk的包名
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
    

    //安装apk的代码
    public void install_apk(String apkPath,Context context) {
		// TODO Auto-generated method stub
    	
    	Intent intent = new Intent(Intent.ACTION_VIEW);
    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	intent.setAction(android.content.Intent.ACTION_VIEW);
    	intent.setDataAndType(Uri.parse("file://"+apkPath),"application/vnd.android.package-archive");
    	context.startActivity(intent);
		
	}
    
    public void copyFile(String srcPath, String dstPath){
    	try {
			FileInputStream fis = new FileInputStream(srcPath);
			FileOutputStream fos = new FileOutputStream(dstPath);
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
