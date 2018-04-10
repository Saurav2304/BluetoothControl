package com.example.technodevil.bluetoothcontrol;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Set;


public class ListPairedDevicesActivity extends ListActivity {

    /*Variables*/
    public final static String NAME = "com.example.technodevil.trial.NAME";
    ArrayAdapter<String> btArrayAdapter;

    /*Bluetooth Adapter*/
    BluetoothAdapter mBluetoothAdapter;

    /*Activity on creation*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Define new ArrayAdapter*/
        btArrayAdapter
                = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);

        /*Define Bluetooth Adapter*/
        mBluetoothAdapter
                = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices
                = mBluetoothAdapter.getBondedDevices();

        /*Get paired devices list*/
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceBTName = device.getName();
                String deviceBTMajorClass
                        = getBTMajorDeviceClass(device
                        .getBluetoothClass()
                        .getMajorDeviceClass());
                btArrayAdapter.add(deviceBTName
                        + "\n" + deviceBTMajorClass + "\n" + device.getAddress().toString());
            }
        }
        setListAdapter(btArrayAdapter);
    }

    /*Get device type*/
    private String getBTMajorDeviceClass(int major){
        switch(major){
            case BluetoothClass.Device.Major.AUDIO_VIDEO:
                return "AUDIO_VIDEO";
            case BluetoothClass.Device.Major.COMPUTER:
                return "COMPUTER";
            case BluetoothClass.Device.Major.HEALTH:
                return "HEALTH";
            case BluetoothClass.Device.Major.IMAGING:
                return "IMAGING";
            case BluetoothClass.Device.Major.MISC:
                return "MISC";
            case BluetoothClass.Device.Major.NETWORKING:
                return "NETWORKING";
            case BluetoothClass.Device.Major.PERIPHERAL:
                return "PERIPHERAL";
            case BluetoothClass.Device.Major.PHONE:
                return "PHONE";
            case BluetoothClass.Device.Major.TOY:
                return "TOY";
            case BluetoothClass.Device.Major.UNCATEGORIZED:
                return "UNCATEGORIZED";
            case BluetoothClass.Device.Major.WEARABLE:
                return "AUDIO_VIDEO";
            default: return "unknown!";
        }
    }

    /*On List Item selection*/
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);

        /*Return selection item position*/
        Intent intent = new Intent();
        intent.putExtra(NAME,position);
        setResult(RESULT_OK, intent);
        finish();
    }

    /*On cancel*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if(requestCode == RESULT_CANCELED)
            finish();
    }

    /*cancel Device discovery on completion*/
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(mBluetoothAdapter != null)
            mBluetoothAdapter.cancelDiscovery();
    }
}
