package com.example.testuhfapi;

import android.os.Environment;
import android.util.Log;
  
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

public class FileUtils {

	
	// ���ַ���д�뵽�ı��ļ���
	  public static void writeTxtToFile(String strcontent, String filePath, String fileName) {
	    //�����ļ���֮���������ļ�����Ȼ�����
	    makeFilePath(filePath, fileName);
	  
	    String strFilePath = filePath + fileName;
	    // ÿ��д��ʱ��������д
	    String strContent = strcontent + "\r\n";
	    try {
	      File file = new File(strFilePath);
	      if (!file.exists()) {
	        Log.d("TestFile", "Create the file:" + strFilePath);
	        file.getParentFile().mkdirs();
	        file.createNewFile();
	      }
	      RandomAccessFile raf = new RandomAccessFile(file, "rwd");
	      raf.seek(file.length());
	      raf.write(strContent.getBytes());
	      raf.close();
	    } catch (Exception e) {
	      Log.e("TestFile", "Error on write File:" + e);
	    }
	  }
	  
	  //�����ļ�
	  public static File makeFilePath(String filePath, String fileName) {
	    File file = null;
	    makeRootDirectory(filePath);
	    try {
	      file = new File(filePath + fileName);
	      if (!file.exists()) {
	        file.createNewFile();
	      }
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return file;
	  }
	  
	  //�����ļ���
	  public static void makeRootDirectory(String filePath) {
	    File file = null;
	    try {
	      file = new File(filePath);
	      if (!file.exists()) {
	        file.mkdir();
	      }
	    } catch (Exception e) {
	      Log.i("error:", e + "");
	    }
	  }
	  
	  //��ȡָ��Ŀ¼�µ�����TXT�ļ����ļ�����
	  public static String getFileContent(File file) {
	    String content = "";
	    if (!file.isDirectory()) { //����·�������ļ��Ƿ���һ��Ŀ¼(�ļ���)
	      if (file.getName().endsWith("txt")) {//�ļ���ʽΪ""�ļ�
	        try {
	          InputStream instream = new FileInputStream(file);
	          if (instream != null) {
	            InputStreamReader inputreader
	                = new InputStreamReader(instream, "UTF-8");
	            BufferedReader buffreader = new BufferedReader(inputreader);
	            String line = "";
	            //���ж�ȡ
	            while ((line = buffreader.readLine()) != null) {
	              content += line + "\n";
	            }
	            instream.close();//�ر�������
	          }
	        } catch (java.io.FileNotFoundException e) {
	          Log.d("TestFile", "The File doesn't not exist.");
	        } catch (IOException e) {
	          Log.d("TestFile", e.getMessage());
	        }
	      }
	    }
	    return content;
	  }
	
	
	
	
}
