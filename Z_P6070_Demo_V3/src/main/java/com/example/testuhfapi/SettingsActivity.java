package com.example.testuhfapi;


import com.example.testuhfapi.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import uhf.api.CommandType;
import uhf.api.EPCAndTID;
import uhf.api.Frequency_region;
import uhf.api.Multi_interval;
import uhf.api.RfLink;
import uhf.api.Version9200;

public class SettingsActivity extends Activity implements OnClickListener, OnSeekBarChangeListener {

	private Button mBt_rflink_get, mBt_rflink_set;

	private Spinner rflink_list;
	private ArrayAdapter<String> adapter_rflink_list;
	private static final String[] st_rflink_type = { "DSB/FM0/40KHz", "PR/M4/250KHz", "PR/M4/300KHz",
			"DSB/FM0/400KHz" };
	private int int_rflink_temp;

	private Spinner mSp_save, mSp_region;
	private ArrayAdapter<String> adapter_save;
	private ArrayAdapter<String> adapter_region;
	private static String[] st_save = { "不保存", "保存" };
	private static final String[] st_region = { "China1", "China2", "Europe", "USA", "Korea", "Japan" };
	private int int_save = 0, int_region, int_region_tmp;
	private Button mBt_region_get, mBt_region_set;

	private SeekBar mSb_worktime, mSb_interval;
	private EditText mEt_worktime, mEt_interval;
	private TextView mTv_worktime, mTv_interval;
	private Button mBt_timer_get, mBt_timer_set;
	private int int_worktime = 0, int_interval = 0;
	private int int_worktime_temp = 0, int_interval_temp = 0;

	private Button mBt_control_io;

	private Spinner mSp_startq;
	private ArrayAdapter<String> adapter_startq;
	private final int int_startq = 0;
	private int int_startq_temp = 0;
	private static String[] st_q = { "关闭", "打开" };
	
	private Spinner mSp_rssi;
	private ArrayAdapter<String> adapter_rssi;
	private final int int_rssi = 0;
	private int int_rssi_temp = 0;

	private Button mBt_tid_get, mBt_tid_set;
	private Button mBt_rssi_get, mBt_rssi_set;
	public Sound mSound;

	public String Str_Get = "获取";
	public String Str_Set = "设置";
	public String Str_RFArea = "频率区域";
	public String Str_RFLink = "RF链路";
	public String Str_Save = "存储";
	public String Str_Area = "区域";
	public String Str_WorkTime = "工作时间";
	public String Str_IntervalTime = "间隔时间";
	public String Str_And = "和";
	public String Str_save = "保存";
	public String Str_nosave = "不保存";
	public String Str_Open = "打开";
	public String Str_Close = "关闭";
	public String Str_Success = "成功";
	public String Str_Fail = "失败";
	public String Str_State = "状态";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rewrite_setting);
		
		LoadStrActionAndView();
		
		if(!MainActivity.IsMoudle9200) {//手持不为9200模块不显示rssi设置
			LinearLayout llayout = (LinearLayout)findViewById(R.id.RssiLayout);
			llayout.setVisibility(View.GONE); // 隐藏
		}

		initFrame();
		mSound = new Sound(this);

		try {
		LoadRegion();
		Thread.sleep(50);
		LoadTimer();
		Thread.sleep(50);
		LoadRfLink();
		Thread.sleep(50);
		LoadTID();
		
		if(MainActivity.IsMoudle9200) {//手持为9200模块获取rssi状态参数
			Thread.sleep(50);
			LoadRssi();
		}
		
		} catch (Exception ex) {

		}
	}
	
	
	public void LoadStrActionAndView() {	
		if(MainActivity.Language_English) {
			Str_Get = "Get";
			Str_Set = "Set";
			Str_RFArea = "RFArea";
			Str_RFLink = "RFLink";
			Str_Save = "Save";
			Str_Area = "Area";
			Str_WorkTime = "WorkTime";
			Str_IntervalTime = "IntervalTime";
			Str_And = "And";
			Str_save = "save";
			Str_nosave = "not save";
			Str_Open = "Open";
			Str_Close = "Close";
			Str_Success = "Success";
			Str_Fail = "Fail";
			Str_State = "State";
			
			st_save = new String[]{ Str_nosave, Str_save };
			st_q = new String[]{ Str_Close, Str_Open };
			
			((TextView)this.findViewById(R.id.textViewArea)).setText(Str_RFArea);//频点区域
			((TextView)this.findViewById(R.id.textViewlink)).setText(Str_RFLink);//RF链路
			((TextView)this.findViewById(R.id.textViewSave)).setText(Str_Save);//存储
			((TextView)this.findViewById(R.id.textViewarea)).setText(Str_Area);//区域
			((Button)this.findViewById(R.id.bt_rflink_get)).setText(Str_Get);//获取		
			((Button)this.findViewById(R.id.bt_rflink_set)).setText(Str_Set);//设置
			((Button)this.findViewById(R.id.bt_region_get)).setText(Str_Get);//获取		
			((Button)this.findViewById(R.id.bt_region_set)).setText(Str_Set);//设置
			((TextView)this.findViewById(R.id.TimerTitle)).setText(Str_WorkTime + Str_And + Str_IntervalTime);//工作时间和间隔时间
			((TextView)this.findViewById(R.id.worktimer)).setText("Work");//工作时间
			((TextView)this.findViewById(R.id.countTXT)).setText("Interval");//间隔时间		
			((Button)this.findViewById(R.id.bt_timer_get)).setText(Str_Get);//获取		
			((Button)this.findViewById(R.id.bt_timer_set)).setText(Str_Set);//设置		
			((TextView)this.findViewById(R.id.EPCAndTidTitle)).setText("EPC And TID");//同时读取EPC和TID		
			((Button)this.findViewById(R.id.bt_tid_get)).setText(Str_Get);//获取		
			((Button)this.findViewById(R.id.bt_tid_set)).setText(Str_Set);//设置	
			
			((Button)this.findViewById(R.id.bt_rssi_get)).setText(Str_Get);//获取		
			((Button)this.findViewById(R.id.bt_rssi_set)).setText(Str_Set);//设置	
			
			((TextView)this.findViewById(R.id.RssiTitle)).setText("9200 ReadTagsRssi");//9200模块读取标签RSSI	
		}
	}
	

	public void LoadRegion() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				Frequency_region mFrequency_region = new Frequency_region(CommandType.GET_FREQUENCY_REGION, int_save,
						0);
				int count = 0;
				while (true) {
					try
					{
					Boolean ret = MainActivity.ReaderController.UHF_CMD(CommandType.GET_FREQUENCY_REGION,
							mFrequency_region);
					if (ret) {
						if (mFrequency_region.region > 0) {
							int_region = mFrequency_region.region - 1;
						} else {
							int_region = mFrequency_region.region;
						}
						mSp_region.setSelection(int_region);
						setTitle(Str_Get +" "+ Str_RFArea +" "+  Str_Success);
						//mSound.callAlarm(true, 100);
						break;
					}
					count++;
					if (count > 1) {
						mSp_region.setSelection(int_region);
						setTitle(Str_Get +" "+  Str_RFArea + " "+ Str_Fail);
						//mSound.callAlarm(false, 100);
						break;
					}
					} catch (Exception ex) {

					}
				}
			}
		}, 300);
	}

	public void LoadTimer() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				Multi_interval mMulti_interval = new Multi_interval();
				mMulti_interval.com_type = CommandType.GET_MULTI_QUERY_TAGS_INTERVAL;
				mMulti_interval.work_time = 0;
				mMulti_interval.interval = 0;
				int count = 0;
				while (true) {
					try
					{
					Boolean ret = MainActivity.ReaderController.UHF_CMD(CommandType.GET_MULTI_QUERY_TAGS_INTERVAL,
							mMulti_interval);
					if (ret) {
						int_worktime = mMulti_interval.work_time;
						mSb_worktime.setProgress(int_worktime);
						int_interval = mMulti_interval.interval;
						mSb_interval.setProgress(int_interval);
						setTitle(Str_Get +" "+  Str_WorkTime +" "+  Str_Success);
						//mSound.callAlarm(true, 100);
						break;
					}
					count++;
					if (count > 1) {
						mSb_interval.setProgress(int_interval);
						mSb_worktime.setProgress(int_worktime);
						setTitle(Str_Get +" "+  Str_WorkTime +" "+  Str_Fail);
						//mSound.callAlarm(false, 100);
						break;
					}
					} catch (Exception ex) {

					}
				}
			}
		}, 300);
	}

	public void LoadRfLink() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				RfLink mRfLink = new RfLink();
				mRfLink.com_type = CommandType.GET_RF_LINK;
				mRfLink.rflink_Type = 0;
				int count = 0;
				while (true) {
					try
					{
					Boolean ret = MainActivity.ReaderController.UHF_CMD(CommandType.GET_RF_LINK, mRfLink);
					if (ret) {
						int_rflink_temp = mRfLink.rflink_Type;
						//mSound.callAlarm(true, 100);
						rflink_list.setSelection(int_rflink_temp);
						setTitle(Str_Get + " "+ Str_RFLink + " "+ Str_Success);
						break;
					}
					count++;
					if (count > 1) {
						setTitle(Str_Get + " "+ Str_RFLink + " "+ Str_Fail);
						//mSound.callAlarm(false, 100);
						break;
					}
					} catch (Exception ex) {

					}
				}
			}
		}, 300);
	}

	public void LoadTID() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				EPCAndTID mepcandtid = new EPCAndTID();
				EPCAndTID.com_type = CommandType.GET_EPCAndTID;
				EPCAndTID.state = 0;
				int count = 0;
				while (true) {
					try
					{
					Boolean ret = MainActivity.ReaderController.UHF_CMD(CommandType.GET_EPCAndTID, mepcandtid);
					if (ret) {
						setTitle(Str_Get + " "+ "EPC" + Str_And + "TID" + " "+ Str_Success);
						mSp_startq.setSelection(EPCAndTID.state);
						//mSound.callAlarm(true, 100);
						break;
					}
					count++;
					if (count > 1) {
						setTitle(Str_Get + " "+ "EPC" + Str_And + "TID" + " "+ Str_Fail);
						//mSound.callAlarm(false, 100);
						break;
					}
					} catch (Exception ex) {

					}
				}
			}
		}, 300);
	}
	
	
	public void LoadRssi() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				 Version9200 ver = new Version9200();
			     Version9200.com_type = CommandType.GET_MODULE_VERSION_9200;
			     Version9200.RssiState = (byte)0x00;
			     int count = 0;
			     while(true) {
			    	 try {
			    	  Boolean ret = MainActivity.ReaderController.UHF_CMD(CommandType.GET_MODULE_VERSION_9200, ver);
					 	if (ret) {
					 		if(Version9200.RssiState == (byte)0xAA) {
					 			mSp_rssi.setSelection(1);
					 		}
					 		else if(Version9200.RssiState == (byte)0x55) {
					 			mSp_rssi.setSelection(0);
					 		}	
					 		setTitle(Str_Get + " "+ "Rssi"+Str_State + " "+ Str_Success);
					 		break;
					 	}
					 	count++;
						if (count > 1) {
							setTitle(Str_Get + " "+ "Rssi"+Str_State + " "+ Str_Fail);
							//mSound.callAlarm(false, 100);
							break;
						}				 	
			    	 }catch(Exception ex) {
			    		 
			    	 }
			     }
			}
		}, 300);
	}
	


	private void initFrame() {
		mBt_tid_get = (Button) this.findViewById(R.id.bt_tid_get);
		mBt_tid_set = (Button) this.findViewById(R.id.bt_tid_set);
		mBt_tid_get.setOnClickListener(this);
		mBt_tid_set.setOnClickListener(this);
		
		mBt_rssi_get = (Button) this.findViewById(R.id.bt_rssi_get);
		mBt_rssi_set = (Button) this.findViewById(R.id.bt_rssi_set);
		mBt_rssi_get.setOnClickListener(this);
		mBt_rssi_set.setOnClickListener(this);

		mSp_startq = (Spinner) this.findViewById(R.id.sp_set_startq);
		adapter_startq = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, st_q);
		adapter_startq.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSp_startq.setAdapter(adapter_startq);
		mSp_startq.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				int_startq_temp = arg2;
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});
		
		mSp_rssi = (Spinner) this.findViewById(R.id.sp_set_rssi);
		adapter_rssi = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, st_q);
		adapter_rssi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSp_rssi.setAdapter(adapter_rssi);
		mSp_rssi.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				int_rssi_temp = arg2;
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});

		mBt_rflink_get = (Button) this.findViewById(R.id.bt_rflink_get);
		mBt_rflink_set = (Button) this.findViewById(R.id.bt_rflink_set);

		mBt_rflink_get.setOnClickListener(this);
		mBt_rflink_set.setOnClickListener(this);
		rflink_list = (Spinner) this.findViewById(R.id.sp_rflinklist);
		adapter_rflink_list = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, st_rflink_type);
		adapter_rflink_list.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		rflink_list.setAdapter(adapter_rflink_list);
		rflink_list.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				int_rflink_temp = arg2;
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});

		//////////////////////////////
		mSp_save = (Spinner) this.findViewById(R.id.sp_region_save);
		adapter_save = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, st_save);
		adapter_save.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSp_save.setAdapter(adapter_save);
		mSp_save.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				int_save = arg2;
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});

		mSp_region = (Spinner) this.findViewById(R.id.sp_region_region);
		adapter_region = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, st_region);
		adapter_region.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSp_region.setAdapter(adapter_region);
		mSp_region.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				int_region_tmp = arg2 + 1;
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}

		});

		mBt_region_get = (Button) this.findViewById(R.id.bt_region_get);
		mBt_region_set = (Button) this.findViewById(R.id.bt_region_set);
		mBt_region_get.setOnClickListener(this);
		mBt_region_set.setOnClickListener(this);

		//////////////////////////////
		mSb_worktime = (SeekBar) this.findViewById(R.id.sb_worktime);
		mSb_interval = (SeekBar) this.findViewById(R.id.sb_inter);
		mBt_timer_get = (Button) this.findViewById(R.id.bt_timer_get);
		mBt_timer_set = (Button) this.findViewById(R.id.bt_timer_set);

		mEt_worktime = (EditText) this.findViewById(R.id.et_worktime);
		mEt_interval = (EditText) this.findViewById(R.id.et_inter);

		mEt_worktime.setSelection(mEt_worktime.getText().length());
		mEt_interval.setSelection(mEt_interval.getText().length());

		mEt_worktime.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				mEt_worktime.setSelection(s.length());
				String str_s = "" + s;
				if (!str_s.equals(""))
					mSb_worktime.setProgress(Integer.parseInt(str_s));
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

		mEt_interval.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				mEt_interval.setSelection(s.length());
				String str_s = "" + s;
				if (!str_s.equals(""))
					mSb_interval.setProgress(Integer.parseInt(str_s));
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

		mTv_worktime = (TextView) this.findViewById(R.id.tv_worktime);
		mTv_interval = (TextView) this.findViewById(R.id.tv_inter);

		mBt_timer_get.setOnClickListener(this);
		mBt_timer_set.setOnClickListener(this);
		mSb_worktime.setOnSeekBarChangeListener(this);
		mSb_interval.setOnSeekBarChangeListener(this);

	}

	int count = 0;

	@Override
	public void onClick(View v) {
		count = 0;
		switch (v.getId()) {
		case R.id.bt_region_set:
			handleSetRegion();
			break;
		case R.id.bt_region_get:
			handleGetRegion();
			break;

		case R.id.bt_timer_set:
			handleSetTimer();
			break;
		case R.id.bt_timer_get:
			handleGetTimer();
			break;

		case R.id.bt_rflink_get:
			handleGetRFLink();
			break;
		case R.id.bt_rflink_set:
			handleSetRFLink();
			break;

		case R.id.bt_tid_get:
			handleGetTID();
			break;
		case R.id.bt_tid_set:
			handleSetTID();
			break;
			
		case R.id.bt_rssi_get:
			if(MainActivity.IsMoudle9200) handleGetRssi();
			break;
        case R.id.bt_rssi_set:
        	if(MainActivity.IsMoudle9200) handleSetRssi();
			break;
		}
	}

	
	
	public void handleGetRssi() {
	     Version9200 ver = new Version9200();
	     Version9200.com_type = CommandType.GET_MODULE_VERSION_9200;
	     Version9200.RssiState = (byte)0x00;
	     Boolean ret = MainActivity.ReaderController.UHF_CMD(CommandType.GET_MODULE_VERSION_9200, ver);
	 	if (ret) {
	 		if(Version9200.RssiState == (byte)0xAA) {
	 			mSp_rssi.setSelection(1);
	 		}
	 		else if(Version9200.RssiState == (byte)0x55) {
	 			mSp_rssi.setSelection(0);
	 		}
	 		
	 		setTitle(Str_Get + " "+ "Rssi"+Str_State + " "+ Str_Success);
			//mSp_startq.setSelection(mepcandtid.state);
			mSound.callAlarm(true, 100);
		} else {
			setTitle(Str_Get + " "+ "Rssi"+Str_State + " "+ Str_Fail);
			mSound.callAlarm(false, 100);
		}
	}
	
  
	public void handleSetRssi() {
		Version9200 ver = new Version9200();
		Version9200.com_type = CommandType.SET_MODULE_VERSION_9200;
		if(int_rssi_temp == 1)//开启
		{
			Version9200.RssiState = (byte)0xAA;
		}
		else if(int_rssi_temp == 0)//关闭
		{
			Version9200.RssiState = (byte)0x55;
		}
		Boolean ret = MainActivity.ReaderController.UHF_CMD(CommandType.SET_MODULE_VERSION_9200, ver);
	     if (ret) {	 		
	    	 setTitle(Str_Set + " "+ "Rssi"+Str_State + " "+ Str_Success);
			//mSp_startq.setSelection(mepcandtid.state);
			mSound.callAlarm(true, 100);
		} else {
			 setTitle(Str_Set + " "+ "Rssi"+Str_State + " "+ Str_Fail);
			mSound.callAlarm(false, 100);
		}

	}
	
	

	private void handleGetTimer() {
		// TODO Auto-generated method stub
		Multi_interval mMulti_interval = new Multi_interval();
		mMulti_interval.com_type = CommandType.GET_MULTI_QUERY_TAGS_INTERVAL;
		mMulti_interval.work_time = 0;
		mMulti_interval.interval = 0;

		Boolean ret = MainActivity.ReaderController.UHF_CMD(CommandType.GET_MULTI_QUERY_TAGS_INTERVAL,
				mMulti_interval);
		if (ret) {
			setTitle(Str_Get + " "+ Str_WorkTime + " "+ Str_Success);
			int_worktime = mMulti_interval.work_time;
			mSb_worktime.setProgress(int_worktime);

			int_interval = mMulti_interval.interval;
			mSb_interval.setProgress(int_interval);

			mSound.callAlarm(true, 100);
		} else {

			if (count < 3) {
				count++;
				handleGetTimer();
			} else {
				setTitle(Str_Get + " "+ Str_WorkTime + " "+ Str_Fail);
				mSb_interval.setProgress(int_interval);
				mSb_worktime.setProgress(int_worktime);
				mSound.callAlarm(false, 100);
			}
		}
	}

	private void handleSetTimer() {
		// TODO Auto-generated method stub
		Multi_interval mMulti_interval = new Multi_interval();
		mMulti_interval.com_type = CommandType.SET_MULTI_QUERY_TAGS_INTERVAL;
		mMulti_interval.work_time = int_worktime_temp;
		mMulti_interval.interval = int_interval_temp;

		Boolean ret = MainActivity.ReaderController.UHF_CMD(CommandType.SET_MULTI_QUERY_TAGS_INTERVAL,
				mMulti_interval);
		if (ret) {
			setTitle(Str_Set + " "+ Str_WorkTime + " "+ Str_Success);
			int_worktime = int_worktime_temp;
			int_interval = int_interval_temp;
			mSound.callAlarm(true, 100);
		} else {
			if (count < 3) {
				count++;
				handleSetTimer();
			} else {
				setTitle(Str_Set + " "+ Str_WorkTime + " "+ Str_Fail);
				mSb_interval.setProgress(int_interval);
				mSb_worktime.setProgress(int_worktime);
				mSound.callAlarm(false, 100);
			}
		}
	}

	private void handleGetRegion() {
		// TODO Auto-generated method stub
		// GET FREQUENCY_REGION
		Frequency_region mFrequency_region = new Frequency_region(CommandType.GET_FREQUENCY_REGION, int_save, 0);

		Boolean ret = MainActivity.ReaderController.UHF_CMD(CommandType.GET_FREQUENCY_REGION, mFrequency_region);
		if (ret) {
			setTitle(Str_Get + " "+ Str_RFArea + " "+ Str_Success);
			if (mFrequency_region.region > 0) {
				int_region = mFrequency_region.region - 1;
			} else {
				int_region = mFrequency_region.region;
			}
			mSp_region.setSelection(int_region);
			mSound.callAlarm(true, 100);
		} else {
			if (count < 3) {
				count++;
				handleGetRegion();
			} else {
				setTitle(Str_Get + " "+ Str_RFArea + " "+ Str_Fail);
				mSp_region.setSelection(int_region);
				mSound.callAlarm(false, 100);
			}
		}
	}

	private void handleSetRegion() {
		// TODO Auto-generated method stub
		Frequency_region mFrequency_region = new Frequency_region(CommandType.SET_FREQUENCY_REGION, int_save,
				int_region_tmp);
		Boolean ret = MainActivity.ReaderController.UHF_CMD(CommandType.SET_FREQUENCY_REGION, mFrequency_region);
		if (ret) {
			setTitle(Str_Set + " "+ Str_RFArea + " "+ Str_Success);
			int_region = int_region_tmp;
			mSound.callAlarm(true, 100);
		} else {

			if (count < 3) {
				count++;
				handleSetRegion();
			} else {
				setTitle(Str_Set + " "+ Str_RFArea + " "+ Str_Fail);
				mSp_region.setSelection(int_region);
				mSound.callAlarm(false, 100);
			}
		}
	}

	private void handleGetRFLink() {
		// TODO Auto-generated method stub
		RfLink mRfLink = new RfLink();
		mRfLink.com_type = CommandType.GET_RF_LINK;
		mRfLink.rflink_Type = 0;

		Boolean ret = MainActivity.ReaderController.UHF_CMD(CommandType.GET_RF_LINK, mRfLink);
		if (ret) {
			int_rflink_temp = mRfLink.rflink_Type;
			setTitle(Str_Get + " "+ Str_RFLink + " "+ Str_Success);
			mSound.callAlarm(true, 100);
			rflink_list.setSelection(int_rflink_temp);

		} else {
			if (count < 3) {
				count++;
				handleGetRFLink();
			} else {
				setTitle(Str_Get + " "+ Str_RFLink + " "+ Str_Fail);
				mSound.callAlarm(false, 100);
			}

		}

	}

	private void handleSetRFLink() {
		RfLink mRfLink = new RfLink();
		mRfLink.com_type = CommandType.SET_RF_LINK;
		mRfLink.rflink_Type = int_rflink_temp;
		mRfLink.save = 1;

		Boolean ret = MainActivity.ReaderController.UHF_CMD(CommandType.SET_RF_LINK, mRfLink);
		if (ret) {
			setTitle(Str_Set + " "+ Str_RFLink + " "+ Str_Success);
			mSound.callAlarm(true, 100);

		} else {
			if (count < 3) {
				count++;
				handleSetRFLink();
			} else {
				setTitle(Str_Set + " "+ Str_RFLink + " "+ Str_Fail);
				mSound.callAlarm(false, 100);
			}

			// mSb_write.setProgress(int_power_write);
			// mSp_closeopen.setSelection(int_closeopen);
		}

	}

	private void handleSetTID() {
		EPCAndTID mepcandtid = new EPCAndTID();
		EPCAndTID.com_type = CommandType.SET_EPCAndTID;
		EPCAndTID.state = int_startq_temp;

		Boolean ret = MainActivity.ReaderController.UHF_CMD(CommandType.SET_EPCAndTID, mepcandtid);
		if (ret) {
			setTitle(Str_Set + " "+ "EPC" + Str_And + "TID" + " "+ Str_Success);
			mSp_startq.setSelection(int_startq_temp);
			mSound.callAlarm(true, 100);
			
	MainActivity.tag_str_tmp_Read = MainActivity.tag_str_tmp_write = MainActivity.tagsTimes_count = MainActivity.newtagFlag = 0;
		} else {

			if (count < 3) {
				count++;
				handleSetTID();
			} else {
				setTitle(Str_Set + " "+ "EPC" + Str_And + "TID" + " "+ Str_Fail);
				mSound.callAlarm(false, 100);
			}
		}

	}

	private void handleGetTID() {
		EPCAndTID mepcandtid = new EPCAndTID();
		EPCAndTID.com_type = CommandType.GET_EPCAndTID;
		EPCAndTID.state = 0;

		Boolean ret = MainActivity.ReaderController.UHF_CMD(CommandType.GET_EPCAndTID, mepcandtid);
		if (ret) {
			setTitle(Str_Get + " "+ "EPC" + Str_And + "TID" + " "+ Str_Success);
			mSp_startq.setSelection(EPCAndTID.state);
			mSound.callAlarm(true, 100);
		} else {

			if (count < 3) {
				count++;
				handleGetTID();
			} else {
				setTitle(Str_Get + " "+ "EPC" + Str_And + "TID" + " "+ Str_Fail);
				mSound.callAlarm(false, 100);
			}
		}

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// TODO 自动生成的方法存根
		if (seekBar == mSb_worktime) {
			int_worktime_temp = progress;
			mEt_worktime.setText("" + int_worktime_temp);
			mTv_worktime.setText("" + int_worktime_temp + "ms");
		} else if (seekBar == mSb_interval) {
			int_interval_temp = progress;
			mEt_interval.setText("" + int_interval_temp);
			mTv_interval.setText("" + int_interval_temp + "ms");
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO 自动生成的方法存根

	}

	// =====================================================================
}
