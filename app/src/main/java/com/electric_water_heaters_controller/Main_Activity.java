package com.electric_water_heaters_controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.electric_water_heaters_controller.widget.TosAdapterView;
import com.electric_water_heaters_controller.widget.TosGallery;
import com.electric_water_heaters_controller.widget.WheelView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;

public class Main_Activity extends AppCompatActivity {
    //public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    private String[] hoursArray = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
    private String[] minsArray = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23",
            "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37",
            "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
            "51", "52", "53", "54", "55", "56", "57", "58", "59"};

    private WheelView mHours = null;
    private WheelView mMins = null;
    private WheelView mHours_1 = null;
    private WheelView mMins_1 = null;
    private TextView mTextView = null;
    private View mDecorView = null;

    private NumberAdapter hourAdapter;
    private NumberAdapter minAdapter;
    private NumberAdapter hourAdapter_1;
    private NumberAdapter minAdapter_1;

    private final String DEBUG_TAG = "Activity01";
    private static String SERVERIP = "192.168.1.185";
    private static int SERVERPORT = 8087;
    private Thread mThread = null;
    private Thread mThread_1 = null;
    private Socket mSocket = null;
    private Button mButton_In = null;
    private Button mButton_On = null;
    private Button Btn_Timer1_PopupWindow;
    private Button Btn_Timer2_PopupWindow;
    private Button Btn_Timer3_PopupWindow;
    private EditText mEditText01 = null;
    private EditText mEditText_ip = null;
    private EditText mEditText_port = null;
    private TextView mTimer1_Start = null;
    private TextView mTimer1_End = null;
    private TextView mTimer2_Start = null;
    private TextView mTimer2_End = null;
    private TextView mTimer3_Start = null;
    private TextView mTimer3_End = null;
    private TextView mHumidity = null;
    private TextView mTemperature = null;
    private TextView mHeat_index = null;
    private TextView mIn_Water = null;
    private TextView mOut_Water = null;
    private CheckBox mCheckBox = null;
    private CheckBox mCheckBox2 = null;
    private CheckBox mCheckBox3 = null;
    private BufferedReader mBufferedReader = null;
    private PrintWriter mPrintWriter = null;
    private String mStrMSG = "";
    private boolean Socket_conning;
    private boolean RUN_THREAD = true;
    private boolean Write_Str_ = false;
    private String Write_Str = "";
    private int Group_;

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        //取得SharedPreference設定("Preference"為設定檔的名稱)
        SharedPreferences settings = getSharedPreferences("Preference", 0);
        //取出name屬性的字串
        String IP = settings.getString("IP", "");
        SERVERIP = IP;
        String Port = settings.getString("Port", "");
        SERVERPORT =Integer.parseInt(Port);
    }
    protected void onStart () {
        //取得SharedPreference設定("Preference"為設定檔的名稱)
        SharedPreferences settings = getSharedPreferences("Preference", 0);
        //取出name屬性的字串
        String IP = settings.getString("IP", "");
        SERVERIP = IP;
        String Port = settings.getString("Port", "");
        SERVERPORT =Integer.parseInt(Port);
        Log.e(DEBUG_TAG, "Settings onStart ");
        super.onStart();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton_In = (Button) findViewById(R.id.Button_In);
        mEditText01 = (EditText) findViewById(R.id.EditText01);
        mHumidity = (TextView) findViewById(R.id.Humidity);
        mTemperature = (TextView) findViewById(R.id.Temperature);
        mHeat_index = (TextView) findViewById(R.id.Heat_index);
        mTimer1_Start = (TextView) findViewById(R.id.timer1_start);
        mTimer1_End = (TextView) findViewById(R.id.timer1_end);
        mTimer2_Start = (TextView) findViewById(R.id.timer2_start);
        mTimer2_End = (TextView) findViewById(R.id.timer2_end);
        mTimer3_Start = (TextView) findViewById(R.id.timer3_start);
        mTimer3_End = (TextView) findViewById(R.id.timer3_end);
        mIn_Water = (TextView) findViewById(R.id.textView7);
        mOut_Water = (TextView) findViewById(R.id.textView8);
        mCheckBox = (CheckBox) findViewById(R.id.checkBox);
        mCheckBox2 = (CheckBox) findViewById(R.id.checkBox2);
        mCheckBox3 = (CheckBox) findViewById(R.id.checkBox3);

        //定义按钮
        mButton_On = (Button) findViewById(R.id.Button_On);
        mButton_On.setOnClickListener(new ClickEvent());

        Btn_Timer1_PopupWindow = (Button) this.findViewById(R.id.Btn_Timer1);
        Btn_Timer1_PopupWindow.setOnClickListener(new ClickEvent());
        Btn_Timer2_PopupWindow = (Button) this.findViewById(R.id.Btn_Timer2);
        Btn_Timer2_PopupWindow.setOnClickListener(new ClickEvent());
        Btn_Timer3_PopupWindow = (Button) this.findViewById(R.id.Btn_Timer3);
        Btn_Timer3_PopupWindow.setOnClickListener(new ClickEvent());

        mButton_In.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    //���ӷ�����
                    if (Socket_conning == false) {
                        // 以新的執行緒來讀取資料
                        Thread msocket = new Thread(socket_conning);
                        // 啟動執行緒
                        msocket.start();

                        Thread.sleep(30);

                        if (Socket_conning == true) {
                            Toast.makeText(getApplicationContext(), "已連線!", Toast.LENGTH_SHORT).show();
                            mButton_In.setText("離線");
                        } else {
                            Toast.makeText(getApplicationContext(), "連線失敗!!!!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Socket_conning_close();
                        Toast.makeText(getApplicationContext(), "已離線!", Toast.LENGTH_SHORT).show();
                        mButton_In.setText("連線");
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    Log.e(DEBUG_TAG, e.toString());
                }
            }
        });

        mCheckBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String str = "";
                str = mTimer1_Start.getText().toString() + ":" + mTimer1_End.getText().toString();      //合併字串
                String[] AfterSplit = str.split("[:\\s]+"); //切割字元
                if (((CheckBox) v).isChecked()) {
                    Write_Str = "timer_set=1_1_" + AfterSplit[0] + "," + AfterSplit[1] + "," + AfterSplit[2] + "," + AfterSplit[3] + "\n";
                } else {
                    Write_Str = "timer_set=1_0_" + AfterSplit[0] + "," + AfterSplit[1] + "," + AfterSplit[2] + "," + AfterSplit[3] + "\n";
                }
                Write_Str_ = true;
            }
        });

        mCheckBox2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String str = "";
                str = mTimer2_Start.getText().toString() + ":" + mTimer2_End.getText().toString();      //合併字串
                String[] AfterSplit = str.split("[:\\s]+"); //切割字元
                if (((CheckBox) v).isChecked()) {
                    Write_Str = "timer_set=2_1_" + AfterSplit[0] + "," + AfterSplit[1] + "," + AfterSplit[2] + "," + AfterSplit[3] + "\n";
                } else {
                    Write_Str = "timer_set=2_0_" + AfterSplit[0] + "," + AfterSplit[1] + "," + AfterSplit[2] + "," + AfterSplit[3] + "\n";
                }
                Write_Str_ = true;
            }
        });

        mCheckBox3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String str = "";
                str = mTimer3_Start.getText().toString() + ":" + mTimer3_End.getText().toString();      //合併字串
                String[] AfterSplit = str.split("[:\\s]+"); //切割字元
                if (((CheckBox) v).isChecked()) {
                    Write_Str = "timer_set=3_1_" + AfterSplit[0] + "," + AfterSplit[1] + "," + AfterSplit[2] + "," + AfterSplit[3] + "\n";
                } else {
                    Write_Str = "timer_set=3_0_" + AfterSplit[0] + "," + AfterSplit[1] + "," + AfterSplit[2] + "," + AfterSplit[3] + "\n";
                }
                Write_Str_ = true;
            }
        });
    }

    public void Socket_conning_close() {
        try {
            RUN_THREAD = false;
            if (mThread != null) {
                if (!mThread.isInterrupted()) {
                    mThread.interrupt();
                    mThread = null;
                    Log.e(DEBUG_TAG, "mThread.interrupt");
                }
            }
            if (mThread_1 != null) {
                if (!mThread_1.isInterrupted()) {
                    mThread_1.interrupt();
                    mThread_1 = null;
                    Log.e(DEBUG_TAG, "mThread_1.interrupt");
                }
            }
            if (Socket_conning == true) {
                String str = "off_line\r\n";
                mPrintWriter.print(str);
                mPrintWriter.flush();
                Thread.sleep(1000);
                mBufferedReader.close();
                mPrintWriter.close();
                mSocket.close();
                Socket_conning = false;
                Log.e(DEBUG_TAG, "mSocket is Close");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        if (Socket_conning == true) {
            Socket_conning_close();
            //android.os.Process.killProcess(android.os.Process.myPid());
            finish();
        }
        super.onStop();
    }

    protected void onDestroy() {
        if (Socket_conning == true) {
            RUN_THREAD = false;
            mThread.interrupt();
            mThread = null;
            mThread_1.interrupt();
            mThread_1 = null;
        }
        super.onDestroy();
    }

    private Runnable socket_conning = new Runnable() {
        public void run() {
            try {
                mSocket = new Socket(SERVERIP, SERVERPORT);
                mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                mPrintWriter = new PrintWriter(mSocket.getOutputStream(), true);
                if (mSocket.isConnected()) {
                    Socket_conning = true;
                    RUN_THREAD = true;
                    mThread = new Thread(mRunnable);
                    mThread.start();
                    mThread_1 = new Thread(mRunnable_1);
                    mThread_1.start();
                    Log.e(DEBUG_TAG, "已連線");
                }
            } catch (Exception e) {
                Log.e(DEBUG_TAG, e.toString());
            }
        }
    };

    //�߳�:�����������������Ϣ
    private Runnable mRunnable = new Runnable() {
        public void run() {
            while (RUN_THREAD) {
                try {
                    Thread.sleep(500);
                    if ((mStrMSG = mBufferedReader.readLine()) != null) {
                        Log.v(DEBUG_TAG, mStrMSG.toString());
                        //��Ϣ����
                        mStrMSG += "\n";
                        mHandler.sendMessage(mHandler.obtainMessage());
                    }
                    // ������Ϣ
                } catch (Exception e) {
                    Log.e(DEBUG_TAG, "mRunnable: " + e.toString());
                }
            }
        }
    };

    private Runnable mRunnable_1 = new Runnable() {
        public void run() {
            while (RUN_THREAD) {
                try {
                    //Log.v(DEBUG_TAG, "server_status");
                    if (Write_Str_) {
                        mPrintWriter.print(Write_Str);
                        mPrintWriter.flush();
                    } else {
                        String str = "server_status\r\n";
                        mPrintWriter.print(str);
                        mPrintWriter.flush();
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    Log.e(DEBUG_TAG, "mRunnable_1: " + e.toString());
                }
            }
        }
    };

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // ˢ��
            try {
                //�������¼��ӽ���
                if (mStrMSG.indexOf("Timer_Set_OK") != -1 || mStrMSG.indexOf("ON") != -1 || mStrMSG.indexOf("OFF") != -1) {
                    Write_Str_ = false;
                } else if (mStrMSG.indexOf("server_status") != -1) {
                    String[] AfterSplit = mStrMSG.split("[,\\s]+"); //切割字元

                    mHumidity.setText(AfterSplit[1] + " %");
                    mTemperature.setText(AfterSplit[2] + "℃");
                    mHeat_index.setText(AfterSplit[3] + "℃");
                    mIn_Water.setText(AfterSplit[4] + "℃");
                    mOut_Water.setText(AfterSplit[5] + "℃");
                    if (Integer.valueOf(AfterSplit[21]) == 1) {
                        mButton_On.setText("關");
                    } else {
                        mButton_On.setText("開");
                    }
                    if (Integer.valueOf(AfterSplit[6]) == 1) {
                        mCheckBox.setChecked(true);
                    } else {
                        mCheckBox.setChecked(false);
                    }
                    if (Integer.valueOf(AfterSplit[11]) == 1) {
                        mCheckBox2.setChecked(true);
                    } else {
                        mCheckBox2.setChecked(false);
                    }
                    if (Integer.valueOf(AfterSplit[16]) == 1) {
                        mCheckBox3.setChecked(true);
                    } else {
                        mCheckBox3.setChecked(false);
                    }
                    mTimer1_Start.setText(String.format("%02d", Integer.parseInt(AfterSplit[7])) + " : " + String.format("%02d", Integer.parseInt(AfterSplit[8])));
                    mTimer1_End.setText(String.format("%02d", Integer.parseInt(AfterSplit[9])) + " : " + String.format("%02d", Integer.parseInt(AfterSplit[10])));
                    mTimer2_Start.setText(String.format("%02d", Integer.parseInt(AfterSplit[12])) + " : " + String.format("%02d", Integer.parseInt(AfterSplit[13])));
                    mTimer2_End.setText(String.format("%02d", Integer.parseInt(AfterSplit[14])) + " : " + String.format("%02d", Integer.parseInt(AfterSplit[15])));
                    mTimer3_Start.setText(String.format("%02d", Integer.parseInt(AfterSplit[17])) + " : " + String.format("%02d", Integer.parseInt(AfterSplit[18])));
                    mTimer3_End.setText(String.format("%02d", Integer.parseInt(AfterSplit[19])) + " : " + String.format("%02d", Integer.parseInt(AfterSplit[20])));
                }
                mEditText01.setText(mStrMSG);

            } catch (Exception e) {
                Log.e(DEBUG_TAG, e.toString());
            }
        }
    };

    //统一处理按键事件
    class ClickEvent implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (v == Btn_Timer1_PopupWindow) {
                showPopupWindow(Main_Activity.this,
                        Main_Activity.this.findViewById(R.id.Btn_Timer1), 1);
                Group_ = 1;
            } else if (v == Btn_Timer2_PopupWindow) {
                showPopupWindow(Main_Activity.this,
                        Main_Activity.this.findViewById(R.id.Btn_Timer2), 2);
                Group_ = 2;
            } else if (v == Btn_Timer3_PopupWindow) {
                showPopupWindow(Main_Activity.this,
                        Main_Activity.this.findViewById(R.id.Btn_Timer3), 3);
                Group_ = 3;
            } else if (v == mButton_On) {
                if (Socket_conning == true) {
                    if (mButton_On.getText().toString().equals("開")) {
                        Write_Str = "output_set" + "\n";
                    } else {
                        Write_Str = "output_rst" + "\n";
                    }
                    Write_Str_ = true;
                }
            }
        }
    }

    public void showPopupWindow(Context context, View parent, int Group) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View vPopupWindow = inflater.inflate(R.layout.wheel_time, null, false);
        final PopupWindow pw = new PopupWindow(vPopupWindow, 1050, 900, true);
        // 使其聚集
        pw.setFocusable(true);
        // 设置允许在外点击消失
        pw.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        pw.setBackgroundDrawable(new BitmapDrawable());
        //WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        //OK按钮及其处理事件
        Button btnOK = (Button) vPopupWindow.findViewById(R.id.BtnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置文本框内容
                int pos1 = mHours.getSelectedItemPosition();
                int pos2 = mMins.getSelectedItemPosition();
                int pos3 = mHours_1.getSelectedItemPosition();
                int pos4 = mMins_1.getSelectedItemPosition();
                String text = String.format("%02d", pos1) + " : " + String.format("%02d", pos2);
                String text_ = String.format("%02d", pos3) + " : " + String.format("%02d", pos4);
                if (Group_ == 1) {
                    mTimer1_Start.setText(text);
                    mTimer1_End.setText(text_);
                    if (mCheckBox.isChecked()) {
                        Write_Str = "timer_set=1_1_" + String.format("%02d", pos1) + "," + String.format("%02d", pos2)
                                + "," + String.format("%02d", pos3) + "," + String.format("%02d", pos4) + "\n";
                    } else {
                        Write_Str = "timer_set=1_0_" + String.format("%02d", pos1) + "," + String.format("%02d", pos2)
                                + "," + String.format("%02d", pos3) + "," + String.format("%02d", pos4) + "\n";
                    }
                } else if (Group_ == 2) {
                    mTimer2_Start.setText(text);
                    mTimer2_End.setText(text_);
                    if (mCheckBox2.isChecked()) {
                        Write_Str = "timer_set=2_1_" + String.format("%02d", pos1) + "," + String.format("%02d", pos2)
                                + "," + String.format("%02d", pos3) + "," + String.format("%02d", pos4) + "\n";
                    } else {
                        Write_Str = "timer_set=2_0_" + String.format("%02d", pos1) + "," + String.format("%02d", pos2)
                                + "," + String.format("%02d", pos3) + "," + String.format("%02d", pos4) + "\n";
                    }
                } else {
                    mTimer3_Start.setText(text);
                    mTimer3_End.setText(text_);
                    if (mCheckBox3.isChecked()) {
                        Write_Str = "timer_set=3_1_" + String.format("%02d", pos1) + "," + String.format("%02d", pos2)
                                + "," + String.format("%02d", pos3) + "," + String.format("%02d", pos4) + "\n";
                    } else {
                        Write_Str = "timer_set=3_0_" + String.format("%02d", pos1) + "," + String.format("%02d", pos2)
                                + "," + String.format("%02d", pos3) + "," + String.format("%02d", pos4) + "\n";
                    }
                }
                Write_Str_ = true;
                //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                pw.dismiss();//关闭
            }
        });

        //Cancel按钮及其处理事件
        Button btnCancel = (Button) vPopupWindow.findViewById(R.id.BtnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();//关闭
            }
        });
        //显示popupWindow对话框
        pw.showAtLocation(parent, Gravity.CENTER, 0, 0);

        mTextView = (TextView) vPopupWindow.findViewById(R.id.sel_password);

        mHours = (WheelView) vPopupWindow.findViewById(R.id.wheel1);
        mMins = (WheelView) vPopupWindow.findViewById(R.id.wheel2);
        mHours_1 = (WheelView) vPopupWindow.findViewById(R.id.wheel1_1);
        mMins_1 = (WheelView) vPopupWindow.findViewById(R.id.wheel2_1);

        mHours.setScrollCycle(true);
        mMins.setScrollCycle(true);
        mHours_1.setScrollCycle(true);
        mMins_1.setScrollCycle(true);

        hourAdapter = new NumberAdapter(hoursArray);
        minAdapter = new NumberAdapter(minsArray);
        hourAdapter_1 = new NumberAdapter(hoursArray);
        minAdapter_1 = new NumberAdapter(minsArray);

        mHours.setAdapter(hourAdapter);
        mMins.setAdapter(minAdapter);
        mHours_1.setAdapter(hourAdapter_1);
        mMins_1.setAdapter(minAdapter_1);

        String str = "";
        if (Group == 1) {
            str = mTimer1_Start.getText().toString() + ":" + mTimer1_End.getText().toString();
        } else if (Group == 2) {
            str = mTimer2_Start.getText().toString() + ":" + mTimer2_End.getText().toString();
        } else {
            str = mTimer3_Start.getText().toString() + ":" + mTimer3_End.getText().toString();
        }
        String[] AfterSplit = str.split("[:\\s]+"); //切割字元

        mHours.setSelection(Integer.parseInt(AfterSplit[0]), true);
        mMins.setSelection(Integer.parseInt(AfterSplit[1]), true);
        mHours_1.setSelection(Integer.parseInt(AfterSplit[2]), true);
        mMins_1.setSelection(Integer.parseInt(AfterSplit[3]), true);

        ((WheelTextView) mHours.getSelectedView()).setTextSize(30);
        ((WheelTextView) mMins.getSelectedView()).setTextSize(30);
        ((WheelTextView) mHours.getSelectedView()).setTextColor(0xffffffff);
        ((WheelTextView) mMins.getSelectedView()).setTextColor(0xffffffff);
        ((WheelTextView) mHours.getSelectedView()).setGravity(Gravity.CENTER_HORIZONTAL);
        ((WheelTextView) mMins.getSelectedView()).setGravity(Gravity.CENTER_HORIZONTAL);

        ((WheelTextView) mHours_1.getSelectedView()).setTextSize(30);
        ((WheelTextView) mMins_1.getSelectedView()).setTextSize(30);
        ((WheelTextView) mHours_1.getSelectedView()).setTextColor(0xffffffff);
        ((WheelTextView) mMins_1.getSelectedView()).setTextColor(0xffffffff);
        ((WheelTextView) mHours_1.getSelectedView()).setGravity(Gravity.CENTER_HORIZONTAL);
        ((WheelTextView) mMins_1.getSelectedView()).setGravity(Gravity.CENTER_HORIZONTAL);

        mHours.setOnItemSelectedListener(mListener);
        mMins.setOnItemSelectedListener(mListener);
        mHours_1.setOnItemSelectedListener(mListener);
        mMins_1.setOnItemSelectedListener(mListener);

        mHours.setUnselectedAlpha(0.5f);
        mMins.setUnselectedAlpha(0.5f);
        mHours_1.setUnselectedAlpha(0.5f);
        mMins_1.setUnselectedAlpha(0.5f);

        mDecorView = getWindow().getDecorView();

    }

    private TosAdapterView.OnItemSelectedListener mListener = new TosAdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(TosAdapterView<?> parent, View view, int position, long id) {
            ((WheelTextView) view).setTextSize(30);

            int index = Integer.parseInt(view.getTag().toString());
            int count = parent.getChildCount();
            //Log.e(DEBUG_TAG, view.getTag().toString());

            if (index < count - 1) {
                ((WheelTextView) parent.getChildAt(index + 1)).setTextSize(20);
            }
            if (index > 0) {
                ((WheelTextView) parent.getChildAt(index - 1)).setTextSize(20);
            }

            formatData();
        }

        @Override
        public void onNothingSelected(TosAdapterView<?> parent) {

        }
    };

    private void formatData() {
        int pos1 = mHours.getSelectedItemPosition();
        int pos2 = mMins.getSelectedItemPosition();

        String text = String.format("%d%d", pos1, pos2);
        mTextView.setText(text);
    }

    private class NumberAdapter extends BaseAdapter {
        int mHeight = 50;
        String[] mData = null;

        public NumberAdapter(String[] data) {
            mHeight = (int) Utils.dipToPx(Main_Activity.this, mHeight);
            this.mData = data;
        }

        @Override
        public int getCount() {
            return (null != mData) ? mData.length : 0;
        }

        @Override
        public View getItem(int arg0) {
            return getView(arg0, null, null);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            WheelTextView textView = null;

            if (null == convertView) {

                convertView = new WheelTextView(Main_Activity.this);
                convertView.setLayoutParams(new TosGallery.LayoutParams(-1, mHeight));
                textView = (WheelTextView) convertView;
                if (position == 0) {        //修正分鐘為0 時字型大小錯誤
                    textView.setTextSize(30);
                    textView.setGravity(Gravity.CENTER_HORIZONTAL);
                } else {
                    textView.setTextSize(20);
                    textView.setGravity(Gravity.CENTER);
                }
                textView.setTextColor(0xffffffff);
            }

            String text = mData[position];
            if (null == textView) {
                textView = (WheelTextView) convertView;
            }

            textView.setText(text);
            return convertView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int item_id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (item_id) {
            case R.id.action_settings:
                Setting_Activity();
                break;
            default:
                return false;
        }

        return super.onOptionsItemSelected(item);
    }

    public void Setting_Activity(){
        Intent intent = new Intent(this, setting.class);
        startActivity(intent);
    }
}
