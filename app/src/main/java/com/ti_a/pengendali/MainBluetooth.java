package com.ti_a.pengendali;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainBluetooth extends AppCompatActivity {
    Button dis,kekanan,kekeri,maju,con;
    private BluetoothAdapter bluetooth = null;
    TextView tvSuhu;
    private BluetoothSocket bs = null;
    OutputStream outSteram = null;
    public static final int STATE_MESSAGE_RECEIVED =0;
    InputStream inputStream = null;
    private final static UUID myUU = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_bluetooth);
        con =  (Button)findViewById(R.id.conne);
        dis = (Button)findViewById(R.id.dis);
        maju = (Button)findViewById(R.id.go);
        kekanan = (Button)findViewById(R.id.butKan);
        kekeri = (Button)findViewById(R.id.butKiri);
        tvSuhu =  (TextView)findViewById(R.id.tx_suhu) ;
        address = getIntent().getStringExtra(MainActivity.EXTRA_ADDRESS);
        bluetooth= BluetoothAdapter.getDefaultAdapter();
        maju.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sendData("1");
                return true;
            }
        });
        kekeri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData("A");
            }
        });
        kekanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData("D");
            }
        });
        dis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disc();
            }
        });
    }
    public void onResume(){
        super.onResume();

        BluetoothDevice device = bluetooth.getRemoteDevice(address);
        try {
            bs = device.createRfcommSocketToServiceRecord(myUU);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }
        bluetooth.cancelDiscovery();
        // Establish the connection.  This will block until it connects.
        Log.d("welcome", "...Connecting to Remote...");
        try {
            bs.connect();
            Log.d("oke", "...Connection established and data link opened...");
        } catch (IOException e) {
            try {
                bs.close();
                finish();
                Toast.makeText(getApplicationContext(),"hidupkan pereangkat bloutooth",Toast.LENGTH_LONG).show();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.d("oke", "...Creating Socket...");

        try {
            outSteram = bs.getOutputStream();
            inputStream = bs.getInputStream();
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
        }
    }
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case STATE_MESSAGE_RECEIVED:
                    tvSuhu.setText("connect");
                    break;
            }
            return false;
        }
    });
    public void recive(){
        byte[] data = new byte[1028];
        int bytes;
        while (true){
            try {
                bytes = inputStream.read(data);
                handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-2,data).sendToTarget();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void sendData(String message) {
        byte[] msgBuffer = message.getBytes();
        try {
            outSteram.write(msgBuffer);
        } catch (IOException e) {
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            msg = msg +  ".\n\nCheck that the SPP UUID: " + myUU.toString() + " exists on server.\n\n";
            errorExit("Fatal Error", msg);
        }
    }

    private void disc(){
        if (bs!=null) //If the btSocket is busy
        {
            try
            {
                bs.close(); //close connection
            }
            catch (IOException e)
            { errorExit("Error","tidak bisa");}
        }
        finish();
    }
    private void errorExit(String title, String message){
        Toast msg = Toast.makeText(getBaseContext(),
                title + " - " + message, Toast.LENGTH_SHORT);
        msg.show();
        finish();
    }

    @Override
    public void onBackPressed() {
        disc();
    }
}
