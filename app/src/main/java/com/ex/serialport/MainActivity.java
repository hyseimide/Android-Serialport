package com.ex.serialport;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ex.serialport.adapter.FloatUtils;
import com.ex.serialport.adapter.LogListAdapter;
import com.ex.serialport.adapter.SpAdapter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import android_serialport_api.SerialPortFinder;
import tp.xmaihh.serialport.SerialHelper;
import tp.xmaihh.serialport.bean.ComBean;
import tp.xmaihh.serialport.utils.ByteUtil;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recy;
    private Spinner spSerial;
    private EditText edInput;
    private Button btSend;
    private RadioGroup radioGroup;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private SerialPortFinder serialPortFinder;
    private SerialHelper serialHelper;
    private Spinner spBote;
    private Button btOpen;
    private Button btReadOnceWeight;
    private Button btReadManyWeight;
    private Button btOpenCleanDoor;
    private LogListAdapter logListAdapter;
    private Spinner spDatab;
    private Spinner spParity;
    private Spinner spStopb;
    private Spinner spFlowcon;
    private TextView customBaudrate;
    private TextView centerText;
    public long weight0;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serialHelper.close();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        recy = findViewById(R.id.recyclerView);
//        spSerial = findViewById(R.id.sp_serial);
//        edInput = findViewById(R.id.ed_input);
//        btSend = findViewById(R.id.btn_send);
//        spBote = findViewById(R.id.sp_baudrate);
//        btOpen = findViewById(R.id.btn_open);
//
//        radioGroup = findViewById(R.id.radioGroup);
//        radioButton1 = findViewById(R.id.radioButton1);
//        radioButton2 = findViewById(R.id.radioButton2);
        centerText = findViewById(R.id.centerText);

//        btReadOnceWeight = findViewById(R.id.btn_readOnceWeight);
//        btReadManyWeight = findViewById(R.id.btn_readManyWeight);
//        btOpenCleanDoor = findViewById(R.id.btn_OpenCleanDoor);

//        spDatab = findViewById(R.id.sp_databits);
//        spParity = findViewById(R.id.sp_parity);
//        spStopb = findViewById(R.id.sp_stopbits);
//        spFlowcon = findViewById(R.id.sp_flowcon);
//        customBaudrate = findViewById(R.id.tv_custom_baudrate);

//        logListAdapter = new LogListAdapter(null);
//        recy.setLayoutManager(new LinearLayoutManager(this));
//        recy.setAdapter(logListAdapter);
//        recy.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        serialPortFinder = new SerialPortFinder();
        serialHelper = new SerialHelper("dev/ttyS1", 115200) {
            @Override
            protected void onDataReceived(final ComBean comBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        if (radioGroup.getCheckedRadioButtonId() == R.id.radioButton1) {
//                            logListAdapter.addData(comBean.sRecTime + " Rx:<==" + new String(comBean.bRec, StandardCharsets.UTF_8));
//                            if (logListAdapter.getData() != null && logListAdapter.getData().size() > 0) {
//                                recy.smoothScrollToPosition(logListAdapter.getData().size());
//                            }
//                        } else {
//                            logListAdapter.addData(comBean.sRecTime + " Rx:<==" + ByteUtil.ByteArrToHex(comBean.bRec));
                            if (comBean.bRec.length == 9 && comBean.bRec[0] == 0x01 && comBean.bRec[1] == 0x03 && comBean.bRec[2] == 0x04) {
                                byte[] weightBytes = Arrays.copyOfRange(comBean.bRec, 3, 7);

                                    weight0 = FloatUtils.gramsFromBytes(weightBytes);
                                    centerText.setText(String.valueOf(weight0));
                                    Log.d("Rx", "weight: " + weight0);

                                }
//                            if (logListAdapter.getData() != null && logListAdapter.getData().size() > 0) {
//                                recy.smoothScrollToPosition(logListAdapter.getData().size());
//                            }
                        }
//                    }
                });
            }
        };
        // 直接设置固定的串口参数
        serialHelper.setPort("/dev/ttyS3");  // 串口端口
        serialHelper.setBaudRate("9600");  // 波特率
        serialHelper.setDataBits(8);         // 数据位
        serialHelper.setParity(0);           // 校验位（0 = NONE）
        serialHelper.setStopBits(1);         // 停止位
        serialHelper.setFlowCon(0);          // 流控（0 = NONE）
        try {
            serialHelper.open();
//            btOpen.setEnabled(false);
        } catch (IOException | SecurityException e) {
            Toast.makeText(this, getString(R.string.tips_cannot_be_opened, e.getMessage()), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


//        btReadOnceWeight.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                readOnceWeight();
//            }
//        });
//        btReadManyWeight.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                readManyWeight();
//            }
//        });
//        btOpenCleanDoor.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openCleanDoor();
//            }
//        });
        readCloseStatus();

    }
    Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable repeatReadStatus = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            sendHexData("010300000002C40B");
            handler.postDelayed(repeatReadStatus, 500);
        }
    };

    private void readCloseStatus() {
        handler.postDelayed(repeatReadStatus, 1000);
    }
    @Override
    protected void onResume() {
        super.onResume();
//        openSerialPort();
    }
    private void openSerialPort() {
        try {
            serialHelper.open();
            btOpen.setEnabled(false);
        } catch (IOException | SecurityException e) {
            Toast.makeText(this, getString(R.string.tips_cannot_be_opened, e.getMessage()), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {

        }
    }
    public void readManyWeight(){
        for (long i=0;i<5;i++){
            readOnceWeight();
            sleep(100);
            Log.d("TAG", "readMany: "+i);
        }
    }
    public void openCleanDoor(){
        sleep(100);
//        sendHexData("01050003FF007C3A");
        sendHexData("01050002FF002DFA");
        sleep(1500);
        sendHexData("0105000200006C0A");
        sleep(100);
        sendHexData("010300000002C40B");
    }
    public void readOnceWeight(){
        sleep(100);
        sendHexData("01050003FF007C3A");
        sleep(1500);
        sendHexData("0105000300003DCA");
        sleep(100);
        sendHexData("010300000002C40B");
    }
    private void sendHexData(String command) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("hh:mm:ss.SSS");
        if (command.length() > 0) {
            if (serialHelper.isOpen()) {
                try {
                    Long.parseLong(command, 16); // Try to parse hex value
                } catch (NumberFormatException e) {
                    // Show error message if it's not a valid hex number
                    Toast.makeText(getBaseContext(), R.string.tips_formatting_hex_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                // Send the hex data via serial port
                serialHelper.sendHex(command);
                // Log the transaction
//                logListAdapter.addData(sDateFormat.format(new Date()) + " Tx:==>" + command);
                // Smooth scroll to the latest log entry
//                if (logListAdapter.getData() != null && logListAdapter.getData().size() > 0) {
//                    recy.smoothScrollToPosition(logListAdapter.getData().size());
//                }
            } else {
                // Show error if serial port is not open
                Toast.makeText(getBaseContext(), R.string.tips_serial_port_not_open, Toast.LENGTH_SHORT).show();
            }
        } else {
            // Show error if no data is entered
            Toast.makeText(getBaseContext(), R.string.tips_please_enter_a_data, Toast.LENGTH_SHORT).show();
        }
    }

    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.tips_please_enter_custom_baudrate);

        final EditText inputField = new EditText(this);
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };
        inputField.setFilters(new InputFilter[]{filter});
        builder.setView(inputField);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String userInput = inputField.getText().toString().trim();
                try {
                    int value = Integer.parseInt(userInput);
                    if (value >= 0 && value <= 4000000) {
                        customBaudrate.setVisibility(View.VISIBLE);
                        customBaudrate.setText(getString(R.string.title_custom_buardate, userInput));
                        serialHelper.close();
                        serialHelper.setBaudRate(userInput);
                        btOpen.setEnabled(true);
                    }
                } catch (NumberFormatException e) {
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_clean) {
            logListAdapter.clean();
        }
        return super.onOptionsItemSelected(item);
    }
}
