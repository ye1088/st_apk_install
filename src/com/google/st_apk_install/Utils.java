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
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

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
    	
    	 try {  
             ZipInputStream Zin=new ZipInputStream(new FileInputStream(zipFile));//输入源zip路径  
             BufferedInputStream Bin=new BufferedInputStream(Zin);  
             String Parent=folderPath; //输出路径（文件夹目录）  
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
                     System.out.println(Fout+"解压成功");      
                 }  
                 Bin.close();  
                 Zin.close();  
             } catch (IOException e) {  
                 // TODO Auto-generated catch block  
            	 Log.i("error", "内部出错");
                 e.printStackTrace();  
             }  
         } catch (FileNotFoundException e) {  
             // TODO Auto-generated catch block  
        	 Log.i("error", "外部出错");
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
// 		   Log.i("obb_file", "找到一个obb"+file.getAbsolutePath());
 		   if ((file.getName()).endsWith(".obb")){
 			   
 					   copyFile(file.getAbsolutePath(), dstPath+File.separator+file.getName());
 			
 			   
 		   }
 			
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
