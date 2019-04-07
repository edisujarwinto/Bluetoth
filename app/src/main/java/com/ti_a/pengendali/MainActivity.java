package com.ti_a.pengendali;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter bluetooth = null;
    ListView devLis;
    ArrayList lis;
    private Set<BluetoothDevice> devices;
    private BluetoothSocket bs = null;
    public static String EXTRA_ADDRESS = "device_address";
    Button btnPair;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnPair = (Button)findViewById(R.id.pair_but);
        devLis = (ListView)findViewById(R.id.list_devices);
        bluetooth = BluetoothAdapter.getDefaultAdapter();
        deviceOn();
        btnPair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDevice();
            }
        });

    }
    public void deviceOn(){
        if (bluetooth==null){
            Toast.makeText(getApplicationContext(),"bluetooth tidak ada",Toast.LENGTH_LONG).show();
        } else {
            if (bluetooth.isEnabled()){
                Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_LONG).show();
            } else {
                Intent activ = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(activ,1);
            }
        }
    }
    public void listDevice(){
        devices = bluetooth.getBondedDevices();
        lis = new ArrayList();
        if (devices.size()>0){
            for (BluetoothDevice bdev : devices){
                lis.add(bdev.getName()+"\n"+bdev.getAddress());
            }
        } else {
            Toast.makeText(getApplicationContext(),"No paired devices",Toast.LENGTH_LONG).show();
        }
        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,lis);
        devLis.setAdapter(adapter);
        devLis.setOnItemClickListener(listClik);
    }
    private AdapterView.OnItemClickListener listClik = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);
            // Make an intent to start next activity.
            Intent i = new Intent(MainActivity.this, MainBluetooth.class);
            //Change the activity.
            i.putExtra(EXTRA_ADDRESS, address); //this will be received at ledControl (class) Activity
            startActivity(i);
        }
    };

}
