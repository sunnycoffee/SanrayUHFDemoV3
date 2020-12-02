package com.example.testuhfapi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.example.testuhfapi.R;
import com.example.z_android_sdk.Reader;
import com.rscja.deviceapi.Module;
import com.rscja.deviceapi.exception.ConfigurationException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import uhf.api.ActiveTag;
import uhf.api.CommandType;
import uhf.api.MultiLableCallBack;
import uhf.api.Multi_query_epc;
import uhf.api.Power;
import uhf.api.Query_epc;
import uhf.api.ShareData;
import uhf.api.Tags_data;
import uhf.api.Temperature;
import uhf.api.Version9200;
import uhf.api.Ware;

public class MainActivity extends Activity implements MultiLableCallBack {

    public static final String Str_DevMode = "P6070";
    public static final boolean Language_English = false;//中英文版版本切换

    // InventorActivity
    private final int Max_Tags_TempBuff = 10000;
    private final String[] tag_str_tmp = new String[Max_Tags_TempBuff];
    private final String[] tag_str_rssi = new String[Max_Tags_TempBuff];
    private final String[] tag_str_tid = new String[Max_Tags_TempBuff];
    private final String[] tag_str_temp = new String[Max_Tags_TempBuff];
    public static int tag_str_tmp_write = 0;
    public static int tag_str_tmp_Read = 0;
    public static int tagsTimes_count = 0;
    public static int newtagFlag = 0;

    private static final String TAG = "MainActivity";
    public Sound mSound;

    private ListView receptionLV;
    private TextView countTXT;
    private SimpleAdapter recptionSimpleAdapter;
    private final ArrayList<Map<String, String>> receptionArrayList = new ArrayList<Map<String, String>>();
    private final String lvAdptrlabData = "Data";
    private final String lvAdptrlabRssi = "Rssi";
    private final String lvAdptrlabTemp = "Temp";
    private final String lvAdptrlabTimes = "Times";
    // private CheckBox mCb_sound=null;
    private SoundPool soundPool;
    private static Boolean isStart = false;

    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date curDate = null;// = new Date(System.currentTimeMillis());
    // PROCESSING
    private Date endDate = null;// = new Date(System.currentTimeMillis());
    private long diff = 0;// = endDate.getTime() - curDate.getTime();

    private final int int_power_read = 15;
    public static String ClickEPC = ""; // 点击行EPC号
    private Button tempBtn;
    private CheckBox mCb_temp = null;

    private Spinner mSp_power;
    private ArrayAdapter<String> adapter_power;
    private final int int_power = 0;
    private int int_power_temp = 0;
    private static String[] st_power = {"5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"};

    //private TextView mText_threshold_value;
    //private SeekBar mSb_threshold_value;
    public Boolean IsInView = false;// 是否初次加载页面

    public int NO_Filter_Mode = 0;
    public int EPC_Filter_Mode = 1;
    public int TID_Filter_Mode = 2;
    public int SoundMode = NO_Filter_Mode;

    public String SoundData = "";// 读到什么才响
    public Boolean SoundState = false;// 是否开启读标声音

    // 双击事件记录最近一次点击的ID
    private int lastClickId;
    // 双击事件记录最近一次点击的时间
    private long lastClickTime;
    public Button soundBtn;

    //ASCII CheckBox
    private CheckBox mCb_ascii = null;
    public static boolean isAscii = false;

    public static Reader ReaderController;

    public String Str_Action = "开始循环";
    public String Str_MuiltEPC = "开始循环";
    public String Str_StopMuiltEPC = "停止循环";
    public String GetVersionSuccess = Str_DevMode + " V3版本: ";
    public String GetVersionFail = "连接失败，请重启设备";
    public String Str_ReadTags = "正在读标...";
    public String Str_Prompt = "提示";
    public String Str_Confirm = "确认";
    public String Str_Cancel = "取消";
    public String Str_Get = "获取";
    public String Str_Set = "设置";
    public String Str_Power = "功率";
    public String Str_Success = "成功";
    public String Str_Fail = "失败";
    public String Str_TagsNum = "标签个数:";
    public String Str_ReadTagsTimeClick = "当前处于读标状态，请勿进行其他操作。";
    public String Str_IsExitDemo = "是否退出系统?";
    public String Str_SoundMode0 = "读所有标签不响";
    public String Str_SoundMode1 = "读所有标签都响";
    public String Str_SoundMode2 = "读到指定标签响";
    public String StrSelectSoundMode = "选择你要设置的声音模式";
    public String StrInputData = "输入指定EPC或TID";

    public int PowerMinValue = 5;
    public int PowerMaxValue = 30;
    public String PowerTempValue = "-5";
    public static Boolean IsMoudle9200 = false;
    public Boolean IsShowTemp = false;
    public Boolean LastTempIsCheck = false;
    public String SaveTagsNum = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewrite_inventory);
        // ======================================


        ReaderController = new Reader(Str_DevMode);
        ReaderController.SetCallBack(this);// 注册回调使能

        LoadInventorView();
        LoadMainView();

        IsInView = true;

        LoadDevModeMethod();

        LoadViewHandle();
        GetPowerThread_start();
        LoadDevPowerToViewMessage();
    }


    private GetPowerThread GetPowerThread;

    private class GetPowerThread extends Thread {
        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
            GetPowerRange();
        }

    }

    /**
     * 启动线程
     */
    private void GetPowerThread_start() {
        if (GetPowerThread == null) {
            GetPowerThread = new GetPowerThread();
            GetPowerThread.start();
        }
    }


    public void GetPowerRange() {
        Version9200 ver = new Version9200();
        Version9200.com_type = CommandType.GET_MODULE_VERSION_9200;
        Version9200.RssiState = (byte) 0x00;
        int count = 0;
        try {
            Boolean ret = ReaderController.UHF_CMD(CommandType.GET_MODULE_VERSION_9200, ver);
            if (ret) {
                IsMoudle9200 = true;
                if (CommandType.MODULE_VERSION_9200_POWER == (byte) 0x15) {//15-25
                    PowerMinValue = 15;
                    PowerMaxValue = 25;
                }
                if (CommandType.MODULE_VERSION_9200_POWER == (byte) 0x25) {//5-25
                    PowerMinValue = 5;
                    PowerMaxValue = 25;
                }
                if (CommandType.MODULE_VERSION_9200_POWER == (byte) 0x30) {//20-30
                    PowerMinValue = 20;
                    PowerMaxValue = 30;
                }
            } else {
                IsMoudle9200 = false;
                PowerMinValue = 5;
                PowerMaxValue = 30;
            }

            //PowerTempValue = String.valueOf(0 - PowerMinValue);
            //mSb_threshold_value.setMax(PowerMaxValue - PowerMinValue);
            Message msg = new Message();
            String[] StrMsg = new String[]{"SetPowerRange"};
            msg.obj = StrMsg;
            mHandler.sendMessage(msg);// 向Handler发送消息，

            msg = new Message();
            StrMsg = new String[]{"GetPower"};
            msg.obj = StrMsg;
            mHandler.sendMessage(msg);// 向Handler发送消息，
        } catch (Exception ex) {

        }
    }


    public void LoadDevPowerToViewMessage() {
        Message msg = new Message();
        String[] StrMsg = new String[]{"LoadDevPower"};
        msg.obj = StrMsg;
        mHandler.sendMessage(msg);// 向Handler发送消息，
    }


    public static Handler mHandler;// 页面消息处理器

    public void LoadViewHandle() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // 操作界面
                String[] StrMsg = (String[]) msg.obj;
                if (StrMsg[0].equals("GetPower")) handleGetPower();
                if (StrMsg[0].equals("SetPowerRange")) SetPowerRange();
                if (StrMsg[0].equals("LoadDevPower")) {
                    if (!Language_English) ShowToast("正在初始化...");
                    if (Language_English) ShowToast("Initializing...");
                }
                super.handleMessage(msg);
            }
        };
    }


    public void SetPowerRange() {
        st_power = new String[PowerMaxValue - PowerMinValue + 1];
        int AddStartIndex = PowerMinValue;
        for (int i = 0; i < st_power.length; i++) {
            st_power[i] = AddStartIndex + "";
            AddStartIndex++;
        }

        mSp_power = (Spinner) this.findViewById(R.id.cB_Power);
        adapter_power = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, st_power);
        adapter_power.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSp_power.setAdapter(adapter_power);
        mSp_power.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //int_power_temp = arg2;
                int_power_temp = Integer.parseInt((String) mSp_power.getSelectedItem());
            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }


    public void LoadDevModeMethod() {
        try {
            Module.getInstance().ioctl_gpio(66, true);
        } catch (ConfigurationException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        PMStateThread_start();

    }


    //此线程为手持P6070特有
    private PMStateThread PMStateThread;

    private class PMStateThread extends Thread {
        public boolean stop;

        public void run() {
            while (!stop) {
                // 处理功能
                // 通过睡眠线程来设置定时时间
                try {
                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    //如果不是在Activity里面需要得到当时的上下文句柄 用context.getSystemService...
                    boolean isScreenOn = pm.isScreenOn();

                    if (!isScreenOn) {
                        Module.getInstance().ioctl_gpio(66, true);
                    } else {
                        Thread.sleep(20);
                    }

                } catch (Exception e) {

                    //e.printStackTrace();
                }
            }
        }

    }


    /**
     * 启动线程
     */
    private void PMStateThread_start() {
        if (PMStateThread == null) {
            PMStateThread = new PMStateThread();
            PMStateThread.start();
        }
    }

    /**
     * 停止线程
     */
    private void PMStateThread_stop() {
        if (PMStateThread != null) {
            PMStateThread.stop = true;
            PMStateThread = null;
        }
    }

    //==================================================================

    public void LoadMainView() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Ware mWare = new Ware(CommandType.GET_FIRMWARE_VERSION, 0, 0, 0);
                int count = 0;
                while (true) {
                    Boolean rett = ReaderController.UHF_CMD(CommandType.GET_FIRMWARE_VERSION, mWare);
                    if (rett) {
                        Log.e("TAG", "Ver." + mWare.major_version + "." + mWare.minor_version + "."
                                + mWare.revision_version);
                        String Str_Ver = "Ver." + mWare.major_version + "." + mWare.minor_version + "."
                                + mWare.revision_version;
                        setTitle(GetVersionSuccess + Str_Ver);
                        break;
                    }

                    count++;
                    if (count > 5) {
                        setTitle(GetVersionFail);
                        break;
                    }
                }
            }
        }, 300);
    }

    public void LoadInventorView() {
        initView();
        mSound = new Sound(this);
        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 0);
        soundPool.load(this, R.raw.duka3, 1);

        SoundThread_start();// 线程定时检查是否发读标声
        epclist_Thread_start();// 线程定时检查刷新读标显示
    }

    protected void onDestroy() {
        try {
            ReaderController.UHF_CMD(CommandType.Read_TagTemp_Stop, null);

            Thread.sleep(100);// 等thread退出

            Boolean ret = ReaderController.UHF_CMD(CommandType.STOP_MULTI_QUERY_TAGS_EPC, null);
            if (!ret) {
                ReaderController.UHF_CMD(CommandType.STOP_MULTI_QUERY_TAGS_EPC, null);
            }

            soundPool.release();
            SoundThread_stop();
            epclist_Thread_stop();
            Thread.sleep(100);// 等thread退出

            ReaderController.CloseSerialPort();

        } catch (InterruptedException e) {

            e.printStackTrace();
        }
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //if (isStart)
        //Test((Button) findViewById(R.id.CycleBtn));
    }

    private SoundThread Soundthread;

    private class SoundThread extends Thread {
        public boolean stop;

        public void run() {
            while (!stop) {
                // 处理功能

                // 通过睡眠线程来设置定时时间
                try {
                    Thread.sleep(40);
                    if (newtagFlag > 25)
                        newtagFlag = 25;// 一秒最多响25次，避免读标结束后，读标声延时
                    if (newtagFlag > 0) {
                        newtagFlag--;
                        // if(mCb_sound.isChecked())
                        if (SoundState) {
                            soundPool.play(1, 1, 1, 1, 0, 1);
                        }
                    }
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 启动线程
     */
    private void SoundThread_start() {
        if (Soundthread == null) {
            Soundthread = new SoundThread();
            Soundthread.start();
        }
    }

    /**
     * 停止线程
     */
    private void SoundThread_stop() {
        if (Soundthread != null) {
            Soundthread.stop = true;
            Soundthread = null;
        }
    }


    private epclist_Thread epclist_thread;

    private class epclist_Thread extends Thread {
        public boolean stop;
        private int count = 0;

        public void run() {
            while (!stop) {
                // 处理功能

                // 通过睡眠线程来设置定时时间
                try {
                    Thread.sleep(25);
                    count = 0;
                    while (tag_str_tmp_Read != tag_str_tmp_write) {

                        showMessage(tag_str_tmp[tag_str_tmp_Read],
                                tag_str_rssi[tag_str_tmp_Read],
                                tag_str_temp[tag_str_tmp_Read],
                                tag_str_tid[tag_str_tmp_Read], false);

                        tag_str_tmp_Read++;
                        if (tag_str_tmp_Read >= Max_Tags_TempBuff)
                            tag_str_tmp_Read = 0;
                        count++;
                        if (count > 50)
                            break;// 每次显示20条，每秒800张足够，刷新频率过于频繁会导致丢失串口接收的EPC数据
                    }
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 启动线程
     */
    private void epclist_Thread_start() {
        if (epclist_thread == null) {
            epclist_thread = new epclist_Thread();
            epclist_thread.start();
        }
    }

    /**
     * 停止线程
     */
    private void epclist_Thread_stop() {
        if (epclist_thread != null) {
            epclist_thread.stop = true;
            epclist_thread = null;
        }
    }

    private void initView() {
//		mSb_threshold_value = (SeekBar) this.findViewById(R.id.threshold_value);
//		mSb_threshold_value.setOnSeekBarChangeListener(this);
//		mText_threshold_value = (TextView) this.findViewById(R.id.Text_threshold_value);

        Button setpowerBtn = (Button) findViewById(R.id.Bn_SetPower);
        if (Language_English) setpowerBtn.setText("Set");
        setpowerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Str_Action.equals(Str_StopMuiltEPC)) {
                    ShowToast(Str_ReadTagsTimeClick);
                } else {
                    handleSetPower(int_power_temp);
                }

            }
        });

        receptionLV = (ListView) findViewById(R.id.lvReception);

        recptionSimpleAdapter = new SimpleAdapter(this, receptionArrayList, R.layout.activity_main_recodes_element_notemp,
                new String[]{lvAdptrlabData, lvAdptrlabRssi, lvAdptrlabTimes},
                new int[]{R.id.idActivityMain_RecodesElement_tvData_NoTemp, R.id.idActivityMain_RecodesElement_rssi_NoTemp,
                        R.id.idActivityMain_RecodesElement_tvTimes_NoTemp});
        receptionLV.setAdapter(recptionSimpleAdapter);

        receptionLV.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (Str_Action.equals(Str_StopMuiltEPC)) {
                    ShowToast(Str_ReadTagsTimeClick);
                } else {
                    // position 这个就是你选择的是哪个行
                    // 如果是双击,1秒内连续点击判断为双击
                    if ((position == lastClickId) && (Math.abs(lastClickTime - System.currentTimeMillis()) < 1000)) {
                        lastClickId = 0;
                        lastClickTime = 0;

                        ClickEPC = receptionArrayList.get(position).get(lvAdptrlabData);
                        if (!ClickEPC.equals("EPC")) {
                            // setTitle(ClickEPC);
                            Intent intent;
                            intent = new Intent(MainActivity.this, AccessTagActivity.class);
                            startActivityForResult(intent, R.id.SetBtn);
                        } else {
                            ClickEPC = "";
                        }
                    } else // 单击
                    {
                        lastClickId = position;
                        lastClickTime = System.currentTimeMillis();

                        ClickEPC = receptionArrayList.get(position).get(lvAdptrlabData);
                        if (ClickEPC.equals("EPC")) {
                            ClickEPC = "";
                        }
                    }
                }
            }
        });

        countTXT = (TextView) findViewById(R.id.countTXT);
        if (Language_English) {
            countTXT.setText(Str_TagsNum + "0");

            TextView powerTXT = (TextView) findViewById(R.id.textView1);
            powerTXT.setText("Power");
        }


        // 参数设置
        Button setBtn = (Button) findViewById(R.id.SetBtn);
        if (Language_English) setBtn.setText("Config");
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Str_Action.equals(Str_StopMuiltEPC)) {
                    ShowToast(Str_ReadTagsTimeClick);
                } else {
                    Intent intent;
                    intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivityForResult(intent, R.id.SetBtn);
                }

            }
        });

        // 声音设置
        soundBtn = (Button) findViewById(R.id.SoundBtn);
        if (!Language_English) soundBtn.setText("声音:关");
        if (Language_English) {
            ViewGroup.LayoutParams layoutParams = soundBtn.getLayoutParams();
            if (Str_DevMode.equals("P6070")) {
                layoutParams.width = 200;
            } else {
                layoutParams.width = 70;
            }
            soundBtn.setText("S:N");
        }
        soundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectSoundMode();
            }
        });

        // 获取温度
//		tempBtn = (Button) findViewById(R.id.bt_temp_get);
//		tempBtn.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				GetMoudleTemp();
//			}
//		});


        //保存数据
        Button SaveBtn = (Button) findViewById(R.id.SaveBtn);
        if (Language_English) {
            ViewGroup.LayoutParams layoutParams = SaveBtn.getLayoutParams();
            if (Str_DevMode.equals("P6070")) {
                layoutParams.width = 190;
            } else {
                layoutParams.width = 80;
            }
        }

        SaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                String dir = "uhf";
                String directoryPath = "/storage/emulated/0";

                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    directoryPath = context.getExternalFilesDir(dir).getAbsolutePath();
                }

                File filedir = new File(directoryPath);
                if (!filedir.exists()) {//判断文件目录是否存在
                    filedir.mkdirs();
                }
                String filename = directoryPath + "/uhf.txt";
                File file = new File(filename);
                if (!file.exists()) {
                    try {
                        file.createNewFile();  //创建文件
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd- HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                String times = simpleDateFormat.format(date);

                FileWriter writer = null;


                try {
                    writer = new FileWriter(filename, true);//true表示在文件尾部追加内容
                } catch (IOException e) {

                    e.printStackTrace();
                }
                try {
                    writer.write(times + "  Tags:" + SaveTagsNum + "\r\n");
                } catch (IOException e1) {

                    e1.printStackTrace();
                }
                String s;
                for (int size = 0; size < receptionArrayList.size(); size++) {
                    try {
                        s = receptionArrayList.get(size) + "\r\n";
                        writer.write(s);
                    } catch (IOException e) {

                        e.printStackTrace();
                    }

                }
                try {
                    writer.close();
                } catch (IOException e) {

                    e.printStackTrace();
                }
                //Toast.makeText(context, "saved succeed", 0).show();
                ShowToast("saved succeed");

                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                //File files = new File(directoryPath);
                Uri uri = Uri.fromFile(file);
                intent.setData(uri);
                context.sendBroadcast(intent);

            }
        });

        // 清除信息
        Button cleanBtn = (Button) findViewById(R.id.CleanBtn);
        if (Language_English) {
            ViewGroup.LayoutParams layoutParams = cleanBtn.getLayoutParams();
            if (Str_DevMode.equals("P6070")) {
                layoutParams.width = 200;
            } else {
                layoutParams.width = 90;
            }
            cleanBtn.setText("Clean");
        }
        cleanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataClear();
            }
        });

        mCb_temp = (CheckBox) findViewById(R.id.checkBox1);
        if (Language_English) mCb_temp.setText("Temp");
        mCb_temp.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (Str_Action.equals(Str_StopMuiltEPC)) {
                    mCb_temp.setChecked(LastTempIsCheck);
                    ShowToast(Str_ReadTagsTimeClick);
                } else {
                    // TODO 自动生成的方法存根
                    if (isChecked) {
                        LastTempIsCheck = true;
                        TempCheckToTrue();
                    } else {
                        LastTempIsCheck = false;
                        TempCheckToFlase();
                    }
                    showDataClear();
                }
            }
        });


        mCb_ascii = (CheckBox) findViewById(R.id.checkBox2);//isAscii
        mCb_ascii.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO 自动生成的方法存根
                isAscii = isChecked;
            }
        });

        Button ActionBtn = (Button) findViewById(R.id.CycleBtn);
        ActionBtn.setText(Str_MuiltEPC);

        showMessage("EPC", lvAdptrlabRssi, "Temp", "", true);
    }

    public void TempCheckToTrue() {
        IsShowTemp = true;
        recptionSimpleAdapter = new SimpleAdapter(this, receptionArrayList, R.layout.activity_main_recodes_element,
                new String[]{lvAdptrlabData, lvAdptrlabRssi, lvAdptrlabTemp, lvAdptrlabTimes},
                new int[]{R.id.idActivityMain_RecodesElement_tvData, R.id.idActivityMain_RecodesElement_rssi,
                        R.id.idActivityMain_RecodesElement_temp, R.id.idActivityMain_RecodesElement_tvTimes});
        receptionLV.setAdapter(recptionSimpleAdapter);
    }


    public void TempCheckToFlase() {
        IsShowTemp = false;
        recptionSimpleAdapter = new SimpleAdapter(this, receptionArrayList, R.layout.activity_main_recodes_element_notemp,
                new String[]{lvAdptrlabData, lvAdptrlabRssi, lvAdptrlabTimes},
                new int[]{R.id.idActivityMain_RecodesElement_tvData_NoTemp, R.id.idActivityMain_RecodesElement_rssi_NoTemp,
                        R.id.idActivityMain_RecodesElement_tvTimes_NoTemp});
        receptionLV.setAdapter(recptionSimpleAdapter);
    }

    Toast toast = null;

    public void ShowToast(String Msg) {

        if (toast != null) {
            toast.cancel();// 注销之前显示的消息
            toast = null;
        }
        toast = Toast.makeText(MainActivity.this, Msg, 0);// .show();
        toast.setGravity(Gravity.TOP | Gravity.CENTER, 3, 1350);
        // 屏幕居中显示，X轴和Y轴偏移量都是0
        // toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void Test(View view) {
        int count = 0;

        Button ActionBtn = (Button) findViewById(R.id.CycleBtn);

        if (mCb_temp.isChecked()) {
            if (Str_Action.equals(Str_MuiltEPC)) {

                ReaderController.UHF_CMD(CommandType.Read_TagTemp_Start, null);

                setTitle(Str_ReadTags);
                ActionBtn.setText(Str_StopMuiltEPC);
                Str_Action = Str_StopMuiltEPC;
            } else {
                ReaderController.UHF_CMD(CommandType.Read_TagTemp_Stop, null);

                if (SoundState)
                    mSound.callAlarm(true, 100);
                setTitle("Stop Ok");
                isStart = false;

                ActionBtn.setText(Str_MuiltEPC);
                Str_Action = Str_MuiltEPC;
            }
        } else {
            if (Str_Action.equals(Str_MuiltEPC)) {
                // MULTI_QUERY_TAGS_EPC
                Multi_query_epc mMulti_query_epc = new Multi_query_epc();
                mMulti_query_epc.query_total = 0;

                curDate = new Date(System.currentTimeMillis());
                ReaderController.UHF_CMD(CommandType.MULTI_QUERY_TAGS_EPC, mMulti_query_epc);

                isStart = true;
                setTitle(Str_ReadTags);

                ActionBtn.setText(Str_StopMuiltEPC);
                Str_Action = Str_StopMuiltEPC;
            } else {
                Boolean ret = ReaderController.UHF_CMD(CommandType.STOP_MULTI_QUERY_TAGS_EPC, null);
                if (ret) {
                    if (SoundState)
                        mSound.callAlarm(true, 100);
                    setTitle("Stop Ok");
                    isStart = false;

                    ActionBtn.setText(Str_MuiltEPC);
                    Str_Action = Str_MuiltEPC;
                } else {
                    if (count == 0) {
                        ReaderController.UHF_CMD(CommandType.STOP_MULTI_QUERY_TAGS_EPC, null);
                        count++;
                    } else {
                        if (SoundState)
                            mSound.callAlarm(false, 100);
                        setTitle("Stop Fail");
                    }
                }
            }
        }
    }

    /*
     * 如果嵌入网页，网页监听了按钮，那么使用这个更底层的监听才可以，有可能网页监听完将按钮丢弃了，导致下一层无法监听到按钮
     *
     * @Override public boolean dispatchKeyEvent(KeyEvent event) { int keyCode =
     * event.getKeyCode(); if (keyCode == 280) { if(isStart==true) { UHFClient
     * info=UHFClient.getInstance(); if(info!=null) { Boolean
     * ret=UHFClient.mUHF.command(CommandType.STOP_MULTI_QUERY_TAGS_EPC, null);
     * if(ret) { if(mCb_sound.isChecked()) mSound.callAlarm(true, 100);
     * //setTitle("Scan Stop"); setTitle("已停止读标"); isStart=false; } else {
     * if(mCb_sound.isChecked()) mSound.callAlarm(false, 100);
     * setTitle("Stop Fail"); } } } else Test((Button) findViewById(R.id.CycleBtn));
     * return true; } return super.onKeyDown(keyCode, event); }
     */
    // 按键捕获
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //ShowToast("" + keyCode);

        if ((keyCode == 139) || (keyCode == 280) || (keyCode == 24)) {// 139/280 scan button for p6070//keyCode ==
            Test((Button) findViewById(R.id.CycleBtn));
            return true;
        }


        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exit();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    //=========以下函数为手持P6051特有==============================

    private KeyReceiver keyReceiver;

    private class KeyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int keyCode = intent.getIntExtra("keyCode", 0);
            if (keyCode == 0) {//锟斤拷锟斤拷H941
                keyCode = intent.getIntExtra("keycode", 0);
            }
            boolean keyDown = intent.getBooleanExtra("keydown", false);
            if (keyDown) {

                switch (keyCode) {
                    case KeyEvent.KEYCODE_F1:

                        break;
                    case KeyEvent.KEYCODE_F2:

                        break;
                    case KeyEvent.KEYCODE_F3:
                        Test((Button) findViewById(R.id.CycleBtn));
                        break;
                    case KeyEvent.KEYCODE_F4:
                        break;
                    case KeyEvent.KEYCODE_F5:

                        break;
                }
            }
        }
    }


    private void unregisterReceiver() {
        unregisterReceiver(keyReceiver);
    }


//=========以上函数为手持P6051特有==============================	


    @Override
    public void finish() {

        super.finish();

        int count = 0;


        ReaderController.UHF_CMD(CommandType.Read_TagTemp_Stop, null);

        Boolean ret = ReaderController.UHF_CMD(CommandType.STOP_MULTI_QUERY_TAGS_EPC, null);
        if (ret) {
            if (SoundState)
                mSound.callAlarm(true, 100);
            setTitle("Stop Ok");
            // setTitle("已停止读标");

            isStart = false;

            Button ActionBtn = (Button) findViewById(R.id.CycleBtn);
            ActionBtn.setText(Str_MuiltEPC);
            Str_Action = Str_MuiltEPC;
        } else {
            if (count == 0) {
                ReaderController.UHF_CMD(CommandType.STOP_MULTI_QUERY_TAGS_EPC, null);
                count++;
            } else {
                if (SoundState)
                    mSound.callAlarm(false, 100);
                setTitle("Stop Fail");
            }
        }

    }

    public void SoundHandle(String epc, String tid) {
        if (SoundState && SoundData.equals(""))// 都响
        {
            newtagFlag++;
        } else if (!SoundState && SoundData.equals(""))// 都不响
        {

        } else if (SoundState && !SoundData.equals(""))// 指定数据才响
        {
            if (SoundMode == EPC_Filter_Mode) {
                if (SoundData.equals(epc)) {
                    newtagFlag++;
                }
            }

            if (SoundMode == TID_Filter_Mode) {
                if (SoundData.equals(tid)) {
                    newtagFlag++;
                }
            }

        }
    }


    private void showMessage(final String epc, final String rssi, final String temp, final String tid,
                             final Boolean ishead) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String StrEPC = "";
                try {
                    StrEPC = epc;
                    if (isAscii) {//如果勾选ASCII
                        byte[] Byt_EPC = toBytes(epc);
                        StrEPC = new String(Byt_EPC, "GBK");
//					byte[] Byt_EPC = toBytes(epc.substring(0,12));
//					StrEPC= new String(Byt_EPC,"GBK") + epc.substring(12,epc.length());
                    }
                } catch (Exception ex) {

                }

                String FilterEPC = "";

                for (int i = 0; i < receptionArrayList.size(); i++) {
                    try {
                        FilterEPC = receptionArrayList.get(i).get(lvAdptrlabData).substring(0, 2);
                    } catch (Exception ex) {
                        FilterEPC = "";
                    }
                    if (FilterEPC.equals("EP") || FilterEPC.equals("[T")) {

                    } else {
                        if (!receptionArrayList.get(i).get(lvAdptrlabData).contains("TID")) {// 只找EPC行

                            if (StrEPC.equals(receptionArrayList.get(i).get(lvAdptrlabData))) {// 如果EPC和页面EPC相同

                                if (i + 1 != receptionArrayList.size()) { //该EPC号不为最后一行,可能存在TID
                                    String ViewMsg = receptionArrayList.get(i + 1).get(lvAdptrlabData);
                                    if (ViewMsg.contains("TID")) { //有TID
                                        String Tid = ViewMsg.substring(6);
                                        if (Tid.equals(tid)) {//有TID,TID相同更新
                                            String t = receptionArrayList.get(i).get(lvAdptrlabTimes);
                                            if (TextUtils.isEmpty(t)) {
                                                t = "1";
                                            }
                                            t = String.valueOf(Integer.valueOf(t) + 1);
                                            UpdateListView(i, t, rssi, temp);
                                            return;
                                        }
                                    } else {//无TID但存在该EPC直接更新
                                        String t = receptionArrayList.get(i).get(lvAdptrlabTimes);
                                        if (TextUtils.isEmpty(t)) {
                                            t = "1";
                                        }
                                        t = String.valueOf(Integer.valueOf(t) + 1);
                                        UpdateListView(i, t, rssi, temp);
                                        return;
                                    }
                                } else {//该EPC号为最后一行,表示不存在TID,直接更新
                                    String t = receptionArrayList.get(i).get(lvAdptrlabTimes);
                                    if (TextUtils.isEmpty(t)) {
                                        t = "1";
                                    }
                                    t = String.valueOf(Integer.valueOf(t) + 1);
                                    UpdateListView(i, t, rssi, temp);
                                    return;
                                }
                            }
                        }
                    }
                }

                if (curDate != null) {
                    endDate = new Date(System.currentTimeMillis());
                    diff = endDate.getTime() - curDate.getTime();
                } else
                    diff = 0;

                String Str_Num = "";
                if (receptionArrayList.size() != 0) {
                    Str_Num = String.valueOf((receptionArrayList.size() / 2) + 1);
                } else {
                    Str_Num = String.valueOf((receptionArrayList.size() / 2));
                }

                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put(lvAdptrlabData, StrEPC);
                hashMap.put(lvAdptrlabRssi, rssi);
                if (LastTempIsCheck) hashMap.put(lvAdptrlabTemp, temp);
                if (receptionArrayList.size() == 0) {
                    hashMap.put(lvAdptrlabTimes, "Count");
                } else {
                    hashMap.put(lvAdptrlabTimes, "1");
                }
                receptionArrayList.add(hashMap);
                recptionSimpleAdapter.notifyDataSetChanged();

                if (receptionArrayList.size() > 1) {
                    HashMap<String, String> hashMapS = new HashMap<String, String>();
                    if (tid.equals("[TID]")) {
                        //hashMapS.put(lvAdptrlabData, tid + ":无");
                        Str_Num = String.valueOf((receptionArrayList.size() - 1));
                        String countMessage = Str_TagsNum + Str_Num + "   " + diff + "ms";
                        countTXT.setText(countMessage);
                    } else {
                        hashMapS.put(lvAdptrlabData, "[TID]:" + tid);
                        String countMessage = Str_TagsNum + Str_Num + "   " + diff + "ms";
                        countTXT.setText(countMessage);
                        receptionArrayList.add(hashMapS);
                        recptionSimpleAdapter.notifyDataSetChanged();
                    }
                    SaveTagsNum = Str_Num;
                }
            }
        });
    }


    public void UpdateListView(int Index, String Count, String Rssi, String Temp) {
        receptionArrayList.get(Index).put(lvAdptrlabTimes, Count);
        receptionArrayList.get(Index).put(lvAdptrlabRssi, Rssi);

        if (LastTempIsCheck) {
            if (Temp.equals("无") != true && Language_English != true) {
                receptionArrayList.get(Index).put(lvAdptrlabTemp, Temp);
            } else if (Temp.equals("No") != true && Language_English == true) {
                receptionArrayList.get(Index).put(lvAdptrlabTemp, Temp);
            }
        }
        recptionSimpleAdapter.notifyDataSetChanged();
    }


    private void showDataClear() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String countMessage = Str_TagsNum + "0";
                countTXT.setText(countMessage);
                receptionArrayList.clear();
                showMessage("EPC", lvAdptrlabRssi, "Temp", "", true);
                recptionSimpleAdapter.notifyDataSetChanged();
                curDate = new Date(System.currentTimeMillis());
                tag_str_tmp_Read = tag_str_tmp_write = tagsTimes_count = newtagFlag = 0;
                SaveTagsNum = "0";
            }
        });
    }

    /**
     * 请确认demo中的这个算法是否正确
     *
     * @param rssi_msb
     * @param rssi_lsb
     * @return
     */
    private double rssi_calculate(char rssi_msb, char rssi_lsb) {
        int temp_rssi = (int) (((rssi_msb & 0xFF) << 8) + (rssi_lsb & 0xFF));
        double sh_rssi = (double) (short) temp_rssi / 10;

        return sh_rssi;
    }

    private void handleGetPower() {
        int count = 0;

        Power mPower = new Power();
        mPower.com_type = CommandType.GET_POWER;
        mPower.loop = 0;
        mPower.read = 0;
        mPower.write = 0;

        Boolean ret = ReaderController.UHF_CMD(CommandType.GET_POWER, mPower);
        if (ret) {
            int read = mPower.read;
            mSp_power.setSelection(read - PowerMinValue);
            //mText_threshold_value.setText(mPower.read + "dBm");
            //mSb_threshold_value.setProgress(mPower.read);
            ShowToast(Str_Get + Str_Power + Str_Success + ":" + mPower.read + "dBm");
            mSound.callAlarm(true, 100);
        } else {
            if (count < 3) {
                count++;
                handleGetPower();
            } else {
                // this.setTitle("Get Power Fail");
                // mSb_read.setProgress(int_power_read);
                ShowToast(Str_Get + Str_Power + Str_Fail);
                mSound.callAlarm(false, 100);
            }
        }
    }

    private void handleSetPower(int ReadPower) {
        int count = 0;

        Power mPower = new Power();
        mPower.com_type = CommandType.SET_POWER;
        mPower.loop = 0;
        mPower.read = ReadPower;
        mPower.write = ReadPower;
        //mPower.write = 25;

        Boolean ret = ReaderController.UHF_CMD(CommandType.SET_POWER, mPower);
        if (ret) {
            this.setTitle(Str_Set + Str_Power + Str_Success + ":" + ReadPower + "dBm");
            //this.setTitle(Str_Set + Str_Power + Str_Success+":"+ ReadPower+"dBm" + "[写功率为25dBm]");
            // mText_threshold_value.setText(int_power_read + "dBm");
            // mSb_threshold_value.setProgress(int_power_read);
            mSound.callAlarm(true, 100);
        } else {
            this.setTitle(Str_Set + Str_Power + Str_Fail);
            mSound.callAlarm(false, 100);
        }
    }

    public void GetMoudleTemp() {
        int count = 0;

        Temperature mTemp = new Temperature();
        mTemp.com_type = CommandType.GET_MODULE_TEMPERATURE;
        mTemp.temp_msb = 0;
        mTemp.temp_lsb = 0;
        mTemp.temp = 0;

        Boolean ret = ReaderController.UHF_CMD(CommandType.GET_MODULE_TEMPERATURE, mTemp);
        if (ret) {
            ShowToast("查看温度成功,当前模块温度为:" + mTemp.temp + "°C");
            mSound.callAlarm(true, 100);
        } else {
            if (count < 3) {
                count++;
                GetMoudleTemp();
            } else {
                setTitle("查看温度失败");
                mSound.callAlarm(false, 100);
            }
        }
    }


    // 退出系统提示
    private void exit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(Str_Prompt).setMessage(Str_IsExitDemo).setCancelable(true).setIcon(R.drawable.ic_launcher)
                .setPositiveButton(Str_Confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ExitDemo();
                        System.exit(0);// 20190508乔佳为页面初始化新增
                        finish();
                    }
                }).setNegativeButton(Str_Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void ExitDemo() {
        try {
            ReaderController.UHF_CMD(CommandType.Read_TagTemp_Stop, null);

            Thread.sleep(100);// 等thread退出

            Boolean ret = ReaderController.UHF_CMD(CommandType.STOP_MULTI_QUERY_TAGS_EPC, null);
            if (!ret) {
                ReaderController.UHF_CMD(CommandType.STOP_MULTI_QUERY_TAGS_EPC, null);
            }

            soundPool.release();
            SoundThread_stop();
            epclist_Thread_stop();

            Thread.sleep(100);// 等thread退出

            ReaderController.CloseSerialPort();
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
    }

    public byte[] chartobyte(char[] chr_data) {
        byte[] byt_data = new byte[chr_data.length];
        for (int i = 0; i < chr_data.length; ++i) {
            byt_data[i] = (byte) chr_data[i];
        }
        return byt_data;
    }

    // ===========================================================

    /* 声音模式对话框 */

    // 数据源
    String[] Select_Array = new String[]{"读所有标签不响", "读所有标签都响", "读到指定标签响"};

    // 显示一个菜单的对话框选项，点击选择菜单后，菜单会消失

    // 匿名类去创建
    public void SelectSoundMode() {

        ChangeSelectSource();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 设置标题
        builder.setTitle(StrSelectSoundMode).
                // 设置可选择的内容，并添加点击事件
                        setItems(Select_Array, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // which代表的是选择的标签的序列号
//                        Toast.makeText(ReWrite_Inventor.this, "选择" + Select_Array[which],
//                                0).show();  						
                        if (which == 0) {
                            SoundData = "";
                            SoundState = false;
                            if (!Language_English)
                                Toast.makeText(MainActivity.this, "当前声音模式为:读所有标签不响", 0).show();
                            if (!Language_English) soundBtn.setText("声音:关");

                            if (Language_English)
                                Toast.makeText(MainActivity.this, "SoundMode:no alarm when reading any tags", 0).show();
                            if (Language_English) soundBtn.setText("S:N");
                        } else if (which == 1) {
                            SoundData = "";
                            SoundState = true;
                            if (!Language_English)
                                Toast.makeText(MainActivity.this, "当前声音模式为:读所有标签都响", 0).show();
                            if (!Language_English) soundBtn.setText("声音:开");

                            if (Language_English)
                                Toast.makeText(MainActivity.this, "SoundMode:alarm when reading any tags", 0).show();
                            if (Language_English) soundBtn.setText("S:Y");
                        } else {
                            SoundState = true;
                            InputFilterData();
                        }
                    }
                }).
                // 产生对话框，并显示出来
                        create().show();
    }

    public void ChangeSelectSource() {
        if (!SoundData.equals("")) {
            if (SoundMode == EPC_Filter_Mode) {
                if (!Language_English)
                    Select_Array[2] = "读到指定标签响" + "(" + "当前为指定EPC:" + SoundData + ")";
                if (Language_English)
                    Select_Array[2] = "alarm when reading specific tags" + "(" + "FilterEPC:" + SoundData + ")";
            }
            if (SoundMode == TID_Filter_Mode) {
                if (!Language_English)
                    Select_Array[2] = "读到指定标签响" + "(" + "当前为指定TID:" + SoundData + ")";
                if (Language_English)
                    Select_Array[2] = "alarm when reading specific tags" + "(" + "FilterTid:" + SoundData + ")";
            }
        } else {
            if (!Language_English) Select_Array[2] = "读到指定标签响" + "(" + "当前无指定EPC或TID" + ")";
            if (Language_English)
                Select_Array[2] = "alarm when reading specific tags" + "(" + "No Filter EPC And Tid" + ")";
            SoundMode = NO_Filter_Mode; // 无过滤
        }
    }

    public int RadioIndex = 0;

    String[] Mode_Array = new String[]{"EPC", "TID"};

    // 设置一个有输入文本的对话框---->builder的setView方法
    // 输入数据后，对数据进行处理
    // 这里要设置按钮，才能对数据的数据进行处理
    public void InputFilterData() {

        // 创建一个EditText对象
        final EditText et = new EditText(this);
//		if (SoundMode != NO_Filter_Mode) {
//			if(!Language_English) et.setText(Select_Array[2].substring(17, Select_Array[2].length() - 1));
//			if(Language_English) et.setText(Select_Array[2].substring(43, Select_Array[2].length() - 1));
//		}

        et.setText(GetTextStr());

        RadioIndex = SoundMode - 1;
        if (SoundMode == 0) RadioIndex = 0;
        // 创建对话框对象
        new AlertDialog.Builder(this).
                // 设置标题
                        setTitle(StrInputData).
                // RadioButton
                        setSingleChoiceItems(Mode_Array, RadioIndex, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(ReWrite_Inventor.this, "选择" + Mode_Array[which],
//                                0).show();
                        if (which == 0) SoundMode = EPC_Filter_Mode;
                        if (which == 1) SoundMode = TID_Filter_Mode;
                    }
                }).

                // 添加输入的文本框
                        setView(et).
                // 添加确定按钮
                        setPositiveButton(Str_Confirm, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 获取输入的字符
                        String in = et.getText().toString();
                        SoundData = in;
                        // Toast.makeText(ReWrite_Inventor.this, "输入；" + in, 0).show();
                        if (SoundData.length() % 2 != 0) {
                            SoundData = "";
                            if (!Language_English)
                                Toast.makeText(MainActivity.this, "输入数据长度有误!", 0).show();
                            if (Language_English)
                                Toast.makeText(MainActivity.this, "Input data len error!", 0).show();
                        } else {

                            if (SoundMode == NO_Filter_Mode) SoundMode = EPC_Filter_Mode;//默认为指定EPC

                            if (SoundData.equals("")) {
                                if (!Language_English)
                                    Toast.makeText(MainActivity.this, "当前声音模式为:读所有标签都响", 0).show();
                                if (!Language_English) soundBtn.setText("声音:开");

                                if (Language_English)
                                    Toast.makeText(MainActivity.this, "SoundMode:alarm when reading any tags", 0).show();
                                if (Language_English) soundBtn.setText("S:Y");
                            } else {
                                if (!Language_English)
                                    Toast.makeText(MainActivity.this, "当前声音模式为:读到指定标签响", 0).show();
                                if (!Language_English) soundBtn.setText("声音:开");

                                if (Language_English)
                                    Toast.makeText(MainActivity.this, "SoundMode:alarm when reading specific tags", 0).show();
                                if (Language_English) soundBtn.setText("S:Y");
                            }
                        }
                    }
                }).

                setNegativeButton(Str_Cancel, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // SoundState = false;
                    }
                }).

                // 产生并显示
                        create().show();
    }


    public String GetTextStr() {
        String StrResult = "";

        if (!SoundData.equals("")) {
            StrResult = SoundData;
        } else {
            if (ClickEPC.length() != 0) {
                String Str_HeadData = ClickEPC.substring(0, 5);
                if (Str_HeadData.equals("[TID]")) { // 点击TID行
                    if (ClickEPC.length() > 5) { // 存在TID数据
                        String Str_FilterData = ClickEPC.substring(6);
                        String Str_data = "";
                        for (int i = 0; i < Str_FilterData.length() / 2; i++) {

                            if (i + 1 != Str_FilterData.length() / 2) {
                                Str_data += Str_FilterData.substring((i * 2), (Str_data.length() + 2));
                            } else {
                                Str_data += Str_FilterData.substring((i * 2), (Str_data.length() + 2));
                            }
                        }
                        StrResult = Str_data;
                        SoundMode = TID_Filter_Mode;
                    }
                } else {
                    String Str_data = "";
                    for (int i = 0; i < ClickEPC.length() / 2; i++) {
                        if (i + 1 != ClickEPC.length() / 2) {
                            Str_data += ClickEPC.substring((i * 2), (Str_data.length() + 2));
                        } else {
                            Str_data += ClickEPC.substring((i * 2), (Str_data.length() + 2));
                        }
                    }
                    StrResult = Str_data;
                    SoundMode = EPC_Filter_Mode;
                }
            }
        }
        return StrResult;
    }


    @Override
    public void BlueToothBtn() {
        // TODO 自动生成的方法存根

    }

    @Override
    public void BlueToothVoltage(int arg0) {
        // TODO 自动生成的方法存根

    }

    @Override
    public void RecvActiveTag(ActiveTag arg0) {
        // TODO 自动生成的方法存根

    }

    long currenttime = 0;

    @Override
    public void method(byte[] data) {
        // TODO 自动生成的方法存根
        if (data.length <= 0) {
            return;
        }
        tagsTimes_count++;
        // soundPool.play(1, 1, 1, 0, 0, 1);      //3+Len+3
        /*
         * //声音不能放在串口接收来处理，导致大量串口数据丢失20190322 if(mCb_sound.isChecked())soundPool.play(1,
         * 1, 1, 1, 0, 1);
         */

        // String StrCmd = bytes2HexString(data);
        // 把EPC拷贝出来显示
        // 3000 E28011052000590BC06D0897 E28011052000590BC06D0897 0000 01
        // 3000 E280110520005A8BC06D0897 E280110520005A8BC06D0897 0000 01

        byte msb = data[0];
        byte lsb = data[1];
        int pc = (msb & 0x00ff) << 8 | (lsb & 0x00ff);
        pc = (pc & 0xf800) >> 11;

        byte[] tmp = new byte[pc * 2];
        System.arraycopy(data, 2, tmp, 0, tmp.length);
        String str_tmp = ShareData.CharToString(tmp, tmp.length);
        str_tmp = str_tmp.replace(" ", "");

        byte[] tid = new byte[data.length - 2 - (pc * 2) - 3];
        System.arraycopy(data, 2 + (pc * 2), tid, 0, tid.length);
        String str_tid = ShareData.CharToString(tid, tid.length);
        str_tid = str_tid.replace(" ", "");

        // String str_rssi = "" + rssi_calculate(data[2 + pc * 2 + tid.length], data[2 +
        // pc * 2 + 1 + tid.length]);
        String str_rssi = "" + (~((short) (((data[2 + pc * 2 + tid.length] & 0xFF) << 8)
                | (data[2 + pc * 2 + 1 + tid.length] & 0xFF) - 1)) / -10.0);

        tag_str_tmp[tag_str_tmp_write] = str_tmp;
        tag_str_rssi[tag_str_tmp_write] = str_rssi;
        if (!Language_English) tag_str_temp[tag_str_tmp_write] = "无";
        if (Language_English) tag_str_temp[tag_str_tmp_write] = "No";

        if (str_tid.length() == 0) {
            tag_str_tid[tag_str_tmp_write] = "[TID]";
        } else {
            tag_str_tid[tag_str_tmp_write] = str_tid;
        }

        tag_str_tmp_write++;
        if (tag_str_tmp_write >= Max_Tags_TempBuff)
            tag_str_tmp_write = 0;
        // showMessage(str_tmp,str_rssi,false);//禁止在串口接收中直接做显示，会导致大量串口数据丢失20190322简忠辉
        SoundHandle(str_tmp, str_tid);
    }

    /**
     * 请确认demo中的这个算法是否正确
     *
     * @param data
     * @param data2
     * @return
     */
    private double rssi_calculate(byte data, byte data2) {
        int temp_rssi = (int) (((data & 0xFF) << 8) + (data2 & 0xFF));
        double sh_rssi = (double) (short) temp_rssi / 10;

        return sh_rssi;
    }

    public static String bytes2HexString(byte[] b) {
        StringBuffer result = new StringBuffer();
        String hex;
        for (int i = 0; i < b.length; i++) {
            hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            result.append(hex.toUpperCase());
        }
        return result.toString();
    }


    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str
     * @return
     */
    public static byte[] toBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }


    @Override
    public void CmdRespond(String[] result) {
        // TODO 自动生成的方法存根
        if (result[1].equals("F8"))// LTU31读温度
        {
            tagsTimes_count++;

            tag_str_tmp[tag_str_tmp_write] = result[2];
            tag_str_rssi[tag_str_tmp_write] = result[3];
            tag_str_temp[tag_str_tmp_write] = result[4];
            tag_str_tid[tag_str_tmp_write] = "[TID]";

            tag_str_tmp_write++;
            if (tag_str_tmp_write >= Max_Tags_TempBuff)
                tag_str_tmp_write = 0;

            SoundHandle(result[2], "");
        }
    }
}
