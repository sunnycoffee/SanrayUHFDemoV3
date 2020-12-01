package com.example.testuhfapi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

public class SerialPortConfig {

	public static String ReadConfig(Context _context) {
		String content = "";
		Context context = _context;
		String dir = "uhf";
		String directoryPath = "/storage/emulated/0";

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			directoryPath = context.getExternalFilesDir(dir).getAbsolutePath();
		}

		File filedir = new File(directoryPath);
		if (!filedir.exists()) {// 判断文件目录是否存在
			filedir.mkdirs();
		}
		String filename = directoryPath + "/UHF_DEMO_CONFIG.txt";
		File file = new File(filename);

		if (!file.exists()) {
			try {
				file.createNewFile(); // 创建文件
				FileWriter writer = null;
				try {
					writer = new FileWriter(filename, true);// true表示在文件尾部追加内容
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String s;
				try {
					writer.write("/dev/ttyS3");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			InputStream instream = new FileInputStream(file);
			InputStreamReader inputreader = new InputStreamReader(instream,
					"GBK");
			BufferedReader buffreader = new BufferedReader(inputreader);
			String line = ""; // 分行读取
			while ((line = buffreader.readLine()) != null) {
				content += line;
			}
			instream.close(); // 关闭输入流			
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
		}
		return content;
	}
	
	
	public static void WriteConfig(Context _context,String Value) {
		Context context = _context;
		String dir = "uhf";
		String directoryPath = "/storage/emulated/0";

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			directoryPath = context.getExternalFilesDir(dir).getAbsolutePath();
		}

		File filedir = new File(directoryPath);
		if (!filedir.exists()) {// 判断文件目录是否存在
			filedir.mkdirs();
		}
		String filename = directoryPath + "/UHF_DEMO_CONFIG.txt";
		File file = new File(filename);

		if (file.exists()) {
			file.delete();
		}

		if (!file.exists()) {
			try {
				file.createNewFile(); // 创建文件
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd- HH:mm:ss");
		Date date = new Date(System.currentTimeMillis());
		String times = simpleDateFormat.format(date);

		FileWriter writer = null;
		try {
			writer = new FileWriter(filename, true);// true表示在文件尾部追加内容
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String s;

		try {
			writer.write(Value);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Toast.makeText(context, "saved succeed", 0).show();
		// setTitle(filename);
		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		// File files = new File(directoryPath);
		Uri uri = Uri.fromFile(file);
		intent.setData(uri);
		context.sendBroadcast(intent);
	}
	
}
