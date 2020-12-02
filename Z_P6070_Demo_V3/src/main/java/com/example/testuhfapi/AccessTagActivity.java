package com.example.testuhfapi;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import com.example.testuhfapi.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import uhf.api.CommandType;
import uhf.api.ShareData;
import uhf.api.Tags_data;

public class AccessTagActivity extends Activity implements OnClickListener {

	private Spinner mSp_acc_filter, mSp_acc_bank, mSp_acc_offset, mSp_acc_length, mSp_acc_operate;

	private EditText mEt_acc_pwd, mEt_acc_filter;
	private TextView mTv_acc_filter_count;

	private EditText mEt_acc_write_read;
	private Button mBt_acc_write, mBt_acc_read;

	private ArrayAdapter<String> adapter_filter;
	private ArrayAdapter<String> adapter_bank;
	private ArrayAdapter<String> adapter_offset;
	private ArrayAdapter<String> adapter_length;
	private ArrayAdapter<String> adapter_operate;

	private static final String[] st_acc_filter = { "EPC", "TID" };

	private static final String[] st_acc_bank = { "RFU", "EPC", "TID", "USER" };

	private static final String[] st_acc_offset = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
			"13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
			"31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48",
			"49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66",
			"67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84",
			"85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "100", "101",
			"102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116",
			"117", "118", "119", "120", "121", "122", "123", "124", "125", "126", "127", "128", "145", "256", };

	private static final String[] st_acc_length = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13",
			"14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
			"32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49",
			"50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67",
			"68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85",
			"86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "100", "101", "102",
			"103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116", "117",
			"118", "119", "120", "121", "122", "123", "124", "125", "126", "127", "128", "145", "256", };

	private static final String[] st_acc_operate = { "read", "write" };
	private int int_filter = 0, int_bank, int_offset, int_length;
	public Sound mSound;

	private int count_filter = 0;
	private TextView mTv_acc_data_count;
	private int count_data = 0;
	
	public String Str_Len = "长度";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rewrite_accesstag);
		
		LoadStrActionAndView();
		
		initFrame();
		mSound = new Sound(this);

		InitializeAccessView();
	}

	
	
	public void LoadStrActionAndView() {	
		if(MainActivity.Language_English) {
			Str_Len = "Len";
			
			((TextView)this.findViewById(R.id.textViewPwd)).setText("Pwd");//密码			
			((EditText)this.findViewById(R.id.et_acc_pwd)).setHint("Enter 8-digit password");//填密码文本框,输入8位密码
			((TextView)this.findViewById(R.id.countTXT)).setText("Filter");//过滤
			((TextView)this.findViewById(R.id.tv_acc_fileter_count)).setText("Len=0");//长度=0
			((EditText)this.findViewById(R.id.et_acc_filter)).setHint("Enter filtered data, spaced out Min Unit：Word");//输入过滤的数据，使用间隔隔开,Min Unit：Word
			((TextView)this.findViewById(R.id.bank_countTXT)).setText("TagsZone");//标签区域
			((TextView)this.findViewById(R.id.addrstart_countTXT)).setText("StartAddress");//起始地址
			((TextView)this.findViewById(R.id.Length_countTXT)).setText("Len");//长度
			((TextView)this.findViewById(R.id.tv_acc_data_count)).setText("Len=0");//长度=0
			((EditText)this.findViewById(R.id.et_acc_write_read)).setHint("Enter filtered data, spaced out Min Unit：Word");//输入过滤的数据，使用间隔隔开,Min Unit：Word
			((Button)this.findViewById(R.id.bt_acc_read)).setText("Read");//读
			((Button)this.findViewById(R.id.bt_acc_write)).setText("Write");//写		
		}
	}
	
	
//===================================================================
	
	public void InitializeAccessView() {
		mSp_acc_bank.setSelection(1);
		mSp_acc_offset.setSelection(2);

		String Str_FilterData = MainActivity.ClickEPC;

		try {
			if (Str_FilterData.length() != 0) {
				String FirstStr = Str_FilterData.substring(0, 5);

				if (FirstStr.equals("[TID]")) { // 点击TID行
					if (Str_FilterData.length() > 5) { // 存在TID数据
						Str_FilterData = Str_FilterData.substring(6);

						String Str_data = "";
						for (int i = 0; i < Str_FilterData.length() / 2; i++) {

							if (i + 1 != Str_FilterData.length() / 2) {
								Str_data += Str_FilterData.substring((i * 2), (Str_data.length() + 2 - (i * 1))) + " ";
							} else {
								Str_data += Str_FilterData.substring((i * 2), (Str_data.length() + 2 - (i * 1)));
							}

						}

						mEt_acc_filter.setText(Str_data);

						int Len = Str_FilterData.length() / 4;
						mSp_acc_length.setSelection(Len - 1);

						mSp_acc_filter.setSelection(1);
					} else {
						mSp_acc_length.setSelection(5);
					}
				} else // 点击EPC行
				{
					String Str_data = "";
					for (int i = 0; i < Str_FilterData.length() / 2; i++) {

						if (i + 1 != Str_FilterData.length() / 2) {
							Str_data += Str_FilterData.substring((i * 2), (Str_data.length() + 2 - (i * 1))) + " ";
						} else {
							Str_data += Str_FilterData.substring((i * 2), (Str_data.length() + 2 - (i * 1)));
						}
					}

					mEt_acc_filter.setText(Str_data);

					int Len = Str_FilterData.length() / 4;
					mSp_acc_length.setSelection(Len - 1);
				}
			}

			if (MainActivity.isAscii) {
				try {
					byte[] Byt_Ascii = Str_FilterData.getBytes("GBK");
					mSp_acc_length.setSelection((Byt_Ascii.length / 2) - 1);
				} catch (Exception e) {
				}
			}
		} catch (Exception ex) {
			//mEt_acc_filter.setText(Str_FilterData);
			String Str_data = "";
			for (int i = 0; i < Str_FilterData.length() / 2; i++) {

				if (i + 1 != Str_FilterData.length() / 2) {
					Str_data += Str_FilterData.substring((i * 2), (Str_data.length() + 2 - (i * 1))) + " ";
				} else {
					Str_data += Str_FilterData.substring((i * 2), (Str_data.length() + 2 - (i * 1)));
				}
			}

			mEt_acc_filter.setText(Str_data);
			
			mSp_acc_length.setSelection((Str_FilterData.length() / 2) - 2);			
		}

	}

	private void initFrame() {
		// TODO Auto-generated method stub
		mSp_acc_filter = (Spinner) this.findViewById(R.id.sp_acc_filter);
		mSp_acc_bank = (Spinner) this.findViewById(R.id.sp_acc_bank);
		mSp_acc_offset = (Spinner) this.findViewById(R.id.sp_acc_offset);
		mSp_acc_length = (Spinner) this.findViewById(R.id.sp_acc_length);
		mSp_acc_operate = (Spinner) this.findViewById(R.id.sp_acc_oper);

		mEt_acc_pwd = (EditText) this.findViewById(R.id.et_acc_pwd);
		mEt_acc_filter = (EditText) this.findViewById(R.id.et_acc_filter);
		mTv_acc_filter_count = (TextView) this.findViewById(R.id.tv_acc_fileter_count);
		mTv_acc_data_count = (TextView) this.findViewById(R.id.tv_acc_data_count);

		mEt_acc_filter.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

				if (count >= 1) {
					if ((s.length() + 1) % 3 == 0) {
						s = s + " ";
						mEt_acc_filter.setText(s);
						mEt_acc_filter.setSelection(s.length());
					}
				} else if (count == 0) {
					if (s.length() > 0) {
						if ((s.length()) % 3 != 0) {
							s = s.subSequence(0, s.length() - 2);
							mEt_acc_filter.setText(s);
							mEt_acc_filter.setSelection(s.length());
						}
					} else if (s.length() == 0) {
						s = "";
						mEt_acc_filter.setSelection(s.length());
					}
				}

				if (s.length() <= 0) {
					mTv_acc_filter_count.setText(Str_Len +"=0");
					count_filter = 0;
				} else if (s.length() > 0 && s.length() <= 2) {
					count_filter = 1;
					mTv_acc_filter_count.setText(Str_Len +"=0");
				} else {
					// mTv_acc_filter_count.setText("Len="+(int)((s.length() + 1)/3));
					count_filter = (int) ((s.length() + 1) / 3);
					mTv_acc_filter_count.setText(Str_Len +"=" + count_filter / 2);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		mEt_acc_write_read = (EditText) this.findViewById(R.id.et_acc_write_read);
		mEt_acc_write_read.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

				if (count >= 1) {
					if ((s.length() + 1) % 3 == 0) {
						s = s + " ";
						mEt_acc_write_read.setText(s);
						mEt_acc_write_read.setSelection(s.length());
					}
				} else if (count == 0) {
					if (s.length() > 0) {
						if ((s.length()) % 3 != 0) {
							s = s.subSequence(0, s.length() - 2);
							mEt_acc_write_read.setText(s);
							mEt_acc_write_read.setSelection(s.length());
						}
					} else if (s.length() == 0) {
						s = "";
						mEt_acc_write_read.setSelection(s.length());
					}
				}

				if (s.length() <= 0) {
					mTv_acc_data_count.setText(Str_Len +"=0");
					count_data = 0;
				} else if (s.length() > 0 && s.length() <= 2) {
					// mTv_acc_data_count.setText("Len=1");
					count_data = 1;
					mTv_acc_data_count.setText(Str_Len +"=0");
				} else {
					// mTv_acc_data_count.setText("Len="+(int)((s.length() + 1)/3));
					count_data = (int) ((s.length() + 1) / 3);
					mTv_acc_data_count.setText(Str_Len +"=" + count_data / 2);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		mBt_acc_read = (Button) this.findViewById(R.id.bt_acc_read);
		mBt_acc_write = (Button) this.findViewById(R.id.bt_acc_write);

		mBt_acc_read.setOnClickListener(this);
		mBt_acc_write.setOnClickListener(this);

		adapter_operate = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, st_acc_operate);
		adapter_operate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSp_acc_operate.setAdapter(adapter_operate);
		mSp_acc_operate.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				if (arg2 == 0) {
					// mEt_acc_write_read.setEnabled(false);
					// mBt_acc_write.setEnabled(false);
					// mBt_acc_read.setEnabled(true);
				} else {
					// mEt_acc_write_read.setEnabled(true);
					// mBt_acc_write.setEnabled(true);
					// mBt_acc_read.setEnabled(false);
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});

		adapter_filter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, st_acc_filter);
		adapter_filter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSp_acc_filter.setAdapter(adapter_filter);
		mSp_acc_filter.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				int_filter = arg2;
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});

		adapter_bank = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, st_acc_bank);
		adapter_bank.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSp_acc_bank.setAdapter(adapter_bank);
		mSp_acc_bank.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				int_bank = arg2;
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});

		adapter_offset = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, st_acc_offset);
		adapter_offset.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSp_acc_offset.setAdapter(adapter_offset);
		mSp_acc_offset.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// int_offset=arg2;
				// 20181130简忠辉修改
				String selectText = mSp_acc_offset.getSelectedItem().toString();
				int_offset = Integer.parseInt(selectText);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});

		adapter_length = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, st_acc_length);
		adapter_length.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSp_acc_length.setAdapter(adapter_length);
		mSp_acc_length.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				int_length = arg2 + 1;
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});

	}

	int count = 0;

	@Override
	public void onClick(View v) {
		count = 0;
		// TODO Auto-generated method stub
		if (v == mBt_acc_read) {
			handleAccRead();
		} else if (v == mBt_acc_write) {
			handleAccWrite();
		}
	}
	
	
	Toast toast = null;

	public void ShowToast(String Msg) {

		if (toast != null) {
			toast.cancel();// 注销之前显示的消息
			toast = null;
		}
		toast = Toast.makeText(AccessTagActivity.this, Msg, 0);// .show();
		toast.setGravity(Gravity.TOP | Gravity.CENTER, 3, 1350);
		// 屏幕居中显示，X轴和Y轴偏移量都是0
		// toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	
	
	public long LastRespondTime = 0;
	public long IntervalTime = 0;

	private void handleAccWrite() {
		// TODO Auto-generated method stub
		
		if(LastRespondTime != 0){
			IntervalTime = System.currentTimeMillis() - LastRespondTime;
			if(IntervalTime < 200)
			{
				if(!MainActivity.Language_English) ShowToast("当前点击过于频繁，请等待上次处理结果后再试。");
				if(MainActivity.Language_English) ShowToast("The current click is too frequent, please wait for the last processing result and try again.");
				return;
			}
		}
		

		String psd = mEt_acc_pwd.getText().toString();
		if (psd.length() != 8) {
			// Toast.makeText(this,"输入密码必须为8位" Toast.LENGTH_SHORT);
			if(!MainActivity.Language_English)this.setTitle("输入密码必须为8位");
			if(MainActivity.Language_English)this.setTitle("The password must be 8 digits");
			return;
		}

		Tags_data mTags_data = new Tags_data();
		mTags_data.password = psd;
		mTags_data.FMB = int_filter;

		String str_filter = mEt_acc_filter.getText().toString();
		if (str_filter.equals("")) {
			mTags_data.filterData_len = 0;
			// mTags_data.filterData=new char[]{0x11,0x22,0x33,0x44,0x55,0x66};
		} else {
			String[] strArray = str_filter.split(" ");
			byte[] data = new byte[strArray.length];
						
			if(MainActivity.isAscii) {
				try
				{
				//str_data = str_data.replace(" ","");
				byte[] Byt_Ascii = str_filter.replace(" ","").getBytes("GBK");//String转换为byte[]
				String StrHex = byteArrayToHexString(Byt_Ascii);
                int len = StrHex.length() / 2;
                strArray = new String[len];
                data = new byte[strArray.length];
                str_filter = "";
                for(int i = 0;i<len;i++) {
                	strArray[i] = StrHex.substring((i*2), (i*2)+2);
                	str_filter += strArray[i] + " ";
                }
                str_filter = str_filter.substring(0, str_filter.length() - 1);
				}
				catch(Exception ex)
				{
					
				}
			}
					
			if (!ShareData.StringToChar(str_filter, data, strArray.length)) {
				if(!MainActivity.Language_English)this.setTitle("请输入十六进制数据，以空格间隔，例如：00 11 22 33 44");
				if(MainActivity.Language_English)this.setTitle("Input HexData，Space out，For example：00 11 22 33 44");
				return;
			}

			if(!MainActivity.isAscii) {		
			if (count_filter % 2 != 0) {
				this.setTitle("Filter Hex number must be multiples of 4");
				return;
			}
			}

			mTags_data.filterData_len = data.length;
			mTags_data.filterData = data;

		}

		mTags_data.start_addr = int_offset;
		mTags_data.data_len = int_length;
		mTags_data.mem_bank = int_bank;

		String str_data = mEt_acc_write_read.getText().toString();
		if (str_data.equals("")) {
			mTags_data.filterData_len = 0;
			// mTags_data.filterData=new char[]{0x11,0x22,0x33,0x44,0x55,0x66};
			if(!MainActivity.Language_English)this.setTitle("输入的长度不对，请输入大于等于" + mTags_data.data_len);
			if(MainActivity.Language_English)this.setTitle("Input Data Len Error,Please be greater than or equal to" + mTags_data.data_len);
			return;
		} else {
			
			String[] strArray = str_data.split(" ");//得到十六进制字符串数组
			byte[] data = new byte[strArray.length];
			
			if(MainActivity.isAscii) {
				try
				{
				//str_data = str_data.replace(" ","");
				byte[] Byt_Ascii = str_data.replace(" ","").getBytes("GBK");//String转换为byte[]
				String StrHex = byteArrayToHexString(Byt_Ascii);
                int len = StrHex.length() / 2;
                strArray = new String[len];
                data = new byte[strArray.length];
                str_data = "";
                for(int i = 0;i<len;i++) {
                	strArray[i] = StrHex.substring((i*2), (i*2)+2);
                	str_data += strArray[i] + " ";
                }
                str_data = str_data.substring(0, str_data.length() - 1);
				}
				catch(Exception ex)
				{
					
				}
			}
			
			
			if (!ShareData.StringToChar(str_data, data, strArray.length)) {
				if(!MainActivity.Language_English)this.setTitle("请输入十六进制数据，以空格间隔，例如：00 11 22 33 44");
				if(MainActivity.Language_English)this.setTitle("Input HexData，Space out，For example：00 11 22 33 44");
				return;
			}
			if (mTags_data.data_len * 2 > data.length) {
				if(!MainActivity.Language_English)this.setTitle("输入的长度不对，请输入大于等于" + mTags_data.data_len);
				if(MainActivity.Language_English)this.setTitle("Input Data Len Error,Please be greater than or equal to" + mTags_data.data_len);
				return;
			}
			
			if(!MainActivity.isAscii) {			
			if (count_data % 2 != 0 || ((count_data / 2) != int_length)) {
				this.setTitle("Write Data Hex number must be multiples of 4 and = Length");
				return;
			}
			}
			
			mTags_data.data = data;
			
		}

		Boolean ret = MainActivity.ReaderController.UHF_CMD(CommandType.WRITE_TAGS_DATA, mTags_data);
		if (ret) {
			setTitle("Write TAG OK");
			mSound.callAlarm(true, 100);
		} else {
//			if (count < 3) {
//				count++;
//				handleAccWrite();
//			} else {
				setTitle("Write TAG Fail");
				mSound.callAlarm(false, 100);
			//}
		}
		LastRespondTime = System.currentTimeMillis();
	}

	private void handleAccRead() {
		// TODO Auto-generated method stub

		String psd = mEt_acc_pwd.getText().toString();
		if (psd.length() != 8) {
			// Toast.makeText(this,"输入密码必须为8位" Toast.LENGTH_SHORT);
			if(!MainActivity.Language_English)this.setTitle("输入密码必须为8位");
			if(MainActivity.Language_English)this.setTitle("The password must be 8 digits");
			return;
		}

		Tags_data mTags_data = new Tags_data();
		mTags_data.password = psd;
		mTags_data.FMB = int_filter;

		String str_filter = mEt_acc_filter.getText().toString();
		if (str_filter.equals("")) {
			mTags_data.filterData_len = 0;
			// mTags_data.filterData=new char[]{0x11,0x22,0x33,0x44,0x55,0x66};
		} else {
			
			String[] strArray = str_filter.split(" ");
			byte[] data = new byte[strArray.length];
			
			if(MainActivity.isAscii) {
				try
				{
				//str_data = str_data.replace(" ","");
				byte[] Byt_Ascii = str_filter.replace(" ","").getBytes("GBK");//String转换为byte[]
				String StrHex = byteArrayToHexString(Byt_Ascii);
                int len = StrHex.length() / 2;
                strArray = new String[len];
                data = new byte[strArray.length];
                str_filter = "";
                for(int i = 0;i<len;i++) {
                	strArray[i] = StrHex.substring((i*2), (i*2)+2);
                	str_filter += strArray[i] + " ";
                }
                str_filter = str_filter.substring(0, str_filter.length() - 1);
				}
				catch(Exception ex)
				{
					
				}
			}
			
			
			if (!ShareData.StringToChar(str_filter, data, strArray.length)) {
				if(!MainActivity.Language_English)this.setTitle("请输入十六进制数据，以空格间隔，例如：00 11 22 33 44");
				if(MainActivity.Language_English)this.setTitle("Input HexData，Space out，For example：00 11 22 33 44");
				return;
			}

			if(!MainActivity.isAscii) {	
			if (count_filter % 2 != 0) {
				this.setTitle("Filter Hex number must be multiples of 4");
				return;
			}
			}

			mTags_data.filterData_len = data.length;
			mTags_data.filterData = data;
		}
		mTags_data.start_addr = int_offset;
		mTags_data.data_len = int_length;
		mTags_data.mem_bank = int_bank;

		Boolean ret = MainActivity.ReaderController.UHF_CMD(CommandType.READ_TAGS_DATA, mTags_data);
		if (ret) {
			// 显示
			setTitle("Read TAG OK");
			String str = ShareData.CharToString(mTags_data.data, mTags_data.data.length);
			
			if(MainActivity.isAscii) {		
				str = str.replace(" ","");
				byte[] Byt_EPC = toBytes(str);
				try {
					str = new String(Byt_EPC,"GBK");
					mEt_acc_write_read.setText(str);
				} catch (UnsupportedEncodingException e) {
				}				
			}else {
				mEt_acc_write_read.setText(str);
			}			
			mSound.callAlarm(true, 100);

		} else {
//			if (count < 3) {
//				count++;
//				handleAccRead();
//			} else {
				setTitle("Read TAG Fail");
				mSound.callAlarm(false, 100);
			//}
		}
	}

	
	
	/**
     * 将16进制字符串转换为byte[]
     * 
     * @param str
     * @return
     */
    public static byte[] toBytes(String str) {
        if(str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    } 
    
	 /**
     * byte[]数组转换为16进制的字符串
     *
     * @param data 要转换的字节数组
     * @return 转换后的结果
     */
    public static final String byteArrayToHexString(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            int v = b & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase(Locale.getDefault());
    }
    
	
}
