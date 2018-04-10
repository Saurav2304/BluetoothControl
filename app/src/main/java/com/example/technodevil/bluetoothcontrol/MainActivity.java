package com.example.technodevil.bluetoothcontrol;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends Activity {

    int MESSAGE_READ = 5;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_PAIRED_DEVICE = 2;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    /*UI elements */
    Button deviceButton, connectButton, f1Button, f2Button, f3Button, f4Button, clearButton;
    ToggleButton toggleButton1, toggleButton2, toggleButton3, toggleButton4, toggleButton5, toggleButton6, toggleButton7, toggleButton8;
    TextView deviceList, statusText, outputText;
    SeekBar seekBar;

    /*Bluetooth declarations*/
    BluetoothAdapter mBluetoothAdapter = null;
    BluetoothDevice mmDevice = null;
    BluetoothSocket mmSocket = null;

    /*Threads used for BT*/
    ConnectThread mConnectThread = null;
    ConnectedThread mConnectedThread = null;

    /*Handler to handle data from read*/
    Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            byte[] writeBuf = (byte[]) msg.obj;
            int begin = msg.arg1;
            int end = msg.arg2;
            switch(msg.what) {
                case 1: String writeMessage = new String(writeBuf);
                    writeMessage = writeMessage.substring(begin, end);
                    outputText.setText(writeMessage);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Define Buttons*/
        deviceButton = (Button)findViewById(R.id.deviceButton);
        connectButton = (Button)findViewById(R.id.connectButton);
        f1Button = (Button)findViewById(R.id.f1Button);
        f2Button = (Button)findViewById(R.id.f2Button);
        f3Button = (Button)findViewById(R.id.f3Button);
        f4Button = (Button)findViewById(R.id.f4Button);
        clearButton = (Button)findViewById(R.id.clearButton);

        /*Define ToggleButtons*/
        toggleButton1 = (ToggleButton)findViewById(R.id.toggleButton1);
        toggleButton2 = (ToggleButton)findViewById(R.id.toggleButton2);
        toggleButton3 = (ToggleButton)findViewById(R.id.toggleButton3);
        toggleButton4 = (ToggleButton)findViewById(R.id.toggleButton4);
        toggleButton5 = (ToggleButton)findViewById(R.id.toggleButton5);
        toggleButton6 = (ToggleButton)findViewById(R.id.toggleButton6);
        toggleButton7 = (ToggleButton)findViewById(R.id.toggleButton7);
        toggleButton8 = (ToggleButton)findViewById(R.id.toggleButton8);
        toggleButton1.setText("ON");

        /*Define TextViews*/
        deviceList = (TextView)findViewById(R.id.deviceList);
        statusText = (TextView)findViewById(R.id.statusText);
        outputText = (TextView)findViewById(R.id.outputText);

        /*Get Bluetooth Adapter*/
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        /*Set SeekBar*/
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mConnectedThread.write(Integer.toString(i/11));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mConnectedThread.write(Integer.toString(seekBar.getProgress()/11));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mConnectedThread.write(Integer.toString(seekBar.getProgress()/11));
            }
        });

        /*Iniatialise*/
        connectButton.setEnabled(false);
        deviceList.setText("");
        statusText.setText("");
        toggleButton1.setChecked(false);
        toggleButton2.setChecked(false);
        toggleButton3.setChecked(false);
        toggleButton4.setChecked(false);
        toggleButton5.setChecked(false);
        toggleButton6.setChecked(false);
        toggleButton7.setChecked(false);
        toggleButton8.setChecked(false);
        toggleButton1.setEnabled(false);
        toggleButton2.setEnabled(false);
        toggleButton3.setEnabled(false);
        toggleButton4.setEnabled(false);
        toggleButton5.setEnabled(false);
        toggleButton6.setEnabled(false);
        toggleButton7.setEnabled(false);
        toggleButton8.setEnabled(false);
        toggleButton1.setText("ON");
        toggleButton2.setText("ON");
        toggleButton3.setText("ON");
        toggleButton4.setText("ON");
        toggleButton5.setText("ON");
        toggleButton6.setText("ON");
        toggleButton7.setText("ON");
        toggleButton8.setText("ON");
        f1Button.setEnabled(false);
        f2Button.setEnabled(false);
        f3Button.setEnabled(false);
        f4Button.setEnabled(false);
        seekBar.setEnabled(false);
        clearButton.setEnabled(false);
        outputText.setText(Integer.toString(seekBar.getProgress()));

        if(!mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.enable();
    }

    /*Check Activity Result*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       /* if(requestCode == REQUEST_ENABLE_BT){
            CheckBlueToothState();
        }*/
        if (requestCode == REQUEST_PAIRED_DEVICE) {
            if (resultCode == RESULT_OK) {
                int pos = data.getIntExtra(ListPairedDevicesActivity.NAME,0), i = 0;
                Set<BluetoothDevice> pairedDevices
                        = mBluetoothAdapter.getBondedDevices();
                for(BluetoothDevice device : pairedDevices)
                    if(i == pos) {
                        mmDevice = device;
                        break;
                    }
                    else
                        i++;
                if(mConnectThread  != null)
                    mConnectThread.cancel();
                if(mConnectedThread != null)
                    mConnectedThread.cancel();
                connectButton.setEnabled(true);
                deviceList.setText(mmDevice.getName());
                deviceButton.setText("Change Device");
                statusText.setText("");
                toggleButton1.setChecked(false);
                toggleButton2.setChecked(false);
                toggleButton3.setChecked(false);
                toggleButton4.setChecked(false);
                toggleButton5.setChecked(false);
                toggleButton6.setChecked(false);
                toggleButton7.setChecked(false);
                toggleButton8.setChecked(false);
                toggleButton1.setEnabled(false);
                toggleButton2.setEnabled(false);
                toggleButton3.setEnabled(false);
                toggleButton4.setEnabled(false);
                toggleButton5.setEnabled(false);
                toggleButton6.setEnabled(false);
                toggleButton7.setEnabled(false);
                toggleButton8.setEnabled(false);
                seekBar.setEnabled(false);
                toggleButton1.setText("ON");
                toggleButton2.setText("ON");
                toggleButton3.setText("ON");
                toggleButton4.setText("ON");
                toggleButton5.setText("ON");
                toggleButton6.setText("ON");
                toggleButton7.setText("ON");
                toggleButton8.setText("ON");
                f1Button.setEnabled(false);
                f2Button.setEnabled(false);
                f3Button.setEnabled(false);
                f4Button.setEnabled(false);
                clearButton.setEnabled(false);
                outputText.setText("");
            }
        }
    }

    /*What will deviceButton do*/
    public void deviceSelect(View view){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, ListPairedDevicesActivity.class);
        startActivityForResult(intent, REQUEST_PAIRED_DEVICE);
    }

    /*What will connectButton do*/
    public void connectBT(View view){
        connectButton.setEnabled(false);
        statusText.setText("");
        mConnectThread = new ConnectThread(mmDevice);
        mConnectThread.start();
    }

    /*What will toggleButton1 do*/
    public void line1(View view){
        if(toggleButton1.isChecked()) {
            mConnectedThread.write("a");
            toggleButton1.setText("OFF");
        }
        else {
            mConnectedThread.write("b");
            toggleButton1.setText("ON");
        }
    }

    /*What will toggleButton2 do*/
    public void line2(View view){
        if(toggleButton2.isChecked()) {
            mConnectedThread.write("c");
            toggleButton2.setText("OFF");
        }
        else {
            mConnectedThread.write("d");
            toggleButton3.setText("ON");
        }
    }

    /*What will toggleButton3 do*/
    public void line3(View view){
        if(toggleButton3.isChecked()) {
            mConnectedThread.write("e");
            toggleButton3.setText("OFF");
        }
        else {
            mConnectedThread.write("f");
            toggleButton3.setText("ON");
        }
    }

    /*What will toggleButton4 do*/
    public void line4(View view){
        if(toggleButton4.isChecked()) {
            mConnectedThread.write("g");
            toggleButton4.setText("OFF");
        }
        else {
            mConnectedThread.write("h");
            toggleButton4.setText("ON");
        }
    }

    /*What will toggleButton5 do*/
    public void line5(View view){
        if(toggleButton5.isChecked()) {
            mConnectedThread.write("i");
            toggleButton5.setText("OFF");
        }
        else {
            mConnectedThread.write("j");
            toggleButton5.setText("ON");
        }
    }

    /*What will toggleButton6 do*/
    public void line6(View view){
        if(toggleButton6.isChecked()) {
            mConnectedThread.write("k");
            toggleButton6.setText("OFF");
        }
        else {
            mConnectedThread.write("l");
            toggleButton6.setText("ON");
        }    }

    /*What will toggleButton7 do*/
    public void line7(View view){
        if(toggleButton7.isChecked()) {
            mConnectedThread.write("m");
            toggleButton7.setText("OFF");
        }
        else {
            mConnectedThread.write("n");
            toggleButton7.setText("ON");
        }
    }

    /*What will toggleButton8 do*/
    public void line8(View view){
        if(toggleButton8.isChecked()) {
            mConnectedThread.write("o");
            toggleButton8.setText("OFF");
        }
        else {
            mConnectedThread.write("p");
            toggleButton8.setText("ON");
        }
    }

    /*What will f1Button do*/
    public void f1(View view){
        mConnectedThread.write("q");
    }

    /*What will f2Button do*/
    public void f2(View view){
        mConnectedThread.write("r");
    }

    /*What will f3Button do*/
    public void f3(View view){
        mConnectedThread.write("s");
    }


    /*What will f4Button do*/
    public void f4(View view){
        mConnectedThread.write("t");
    }

    /*What will clearButton do*/
    public void clearText(View view){
        outputText.setText("");
    }

    /*Action on exiting app*/
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(mConnectThread != null)
            mConnectThread.cancel();
        if(mConnectedThread != null)
            mConnectThread.cancel();
        if(mBluetoothAdapter != null)
            mBluetoothAdapter.disable();
    }

    /*Thread to create a Bluetooth connection Socket*/
    private class ConnectThread extends Thread {

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {

            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        statusText.setText("Connected");
                        toggleButton1.setEnabled(true);
                        toggleButton2.setEnabled(true);
                        toggleButton3.setEnabled(true);
                        toggleButton4.setEnabled(true);
                        toggleButton5.setEnabled(true);
                        toggleButton6.setEnabled(true);
                        toggleButton7.setEnabled(true);
                        toggleButton8.setEnabled(true);
                        f1Button.setEnabled(true);
                        f2Button.setEnabled(true);
                        f3Button.setEnabled(true);
                        f4Button.setEnabled(true);
                        clearButton.setEnabled(true);
                        seekBar.setEnabled(true);
                    }
                };
                runOnUiThread(runnable);

                // Do work to manage the connection (in a separate thread)
                mConnectedThread = new ConnectedThread(mmSocket);
                mConnectedThread.start();



            } catch (IOException connectException) {
                try {

                    try {
                        mmSocket.close();
                        try {
                            // Connect the device through the socket. This will block
                            // until it succeeds or throws an exception
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    deviceButton.setText("Change Device");
                                    connectButton.setText("Reconnect");
                                    statusText.setText("Failed");
                                    connectButton.setEnabled(true);
                                }
                            };
                            runOnUiThread(runnable);
                        }catch (Exception e){}
                    } catch (IOException closeException) {
                    }
                    return;
                }catch (Exception e){}
            }
        }

        /* Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    /*Thread to manage the active connection*/
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int begin = 0;
            int bytes = 0;
            while (true) {
                try {
                    bytes += mmInStream.read(buffer, bytes, buffer.length - bytes);
                    for(int i = begin; i < bytes; i++) {
                        if(buffer[i] == "$".getBytes()[0]) {
                            mHandler.obtainMessage(1, begin, i, buffer).sendToTarget();
                            begin = i + 1;
                            if(i == bytes - 1) {
                                bytes = 0;
                                begin = 0;
                            }
                        }
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String string) {
            try {
                mmOutStream.write(string.getBytes());
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}
