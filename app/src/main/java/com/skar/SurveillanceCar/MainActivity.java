package com.skar.SurveillanceCar;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button carf, carb, carl, carr, camf, camb, caml, camr;
    private TextView carStatus, cameraStatus;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private ProgressDialog progress;
    // SPP UUID service - this should work for most devices
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private boolean isBtConnected = false;
    private static String address;
    private static String name;
    private static String ipaddr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get MAC address and device name from the DeviceList via EXTRA after creating Intent
        Intent intent = getIntent();
        address = intent.getStringExtra(DeviceList.EXTRA_ADDRESS);
        name = intent.getStringExtra(DeviceList.EXTRA_NAME);
        ipaddr = intent.getStringExtra(DeviceList.EXTRA_IP);

        new ConnectBT().execute();

        //for WebView component
        String url="http://"+ipaddr+":8000/";
        WebView vid= this.findViewById(R.id.webView);
        vid.setWebViewClient(new WebViewClient());
        vid.getSettings().setJavaScriptEnabled(true);
        vid.loadUrl(url);

        //Car Controls
        carf = findViewById(R.id.carf);
        carb = findViewById(R.id.carb);
        carl = findViewById(R.id.carl);
        carr = findViewById(R.id.carr);
        carStatus = findViewById(R.id.carStatus);

        //Camera Controls
        camf = findViewById(R.id.camf);
        camb = findViewById(R.id.camb);
        caml = findViewById(R.id.caml);
        camr = findViewById(R.id.camr);
        cameraStatus = findViewById(R.id.cameraStatus);

        // Car Button Listeners
        carf.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event){
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    carCommand("F");
                    carStatus.setText("Car going forwards");
                }
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    carCommand("S"); //to stop car when button is released
                    carStatus.setText("Car at rest");
                }
                return true;
            }
        });

        carb.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event){
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    carCommand("B");
                    carStatus.setText("Car going backwards");
                }
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    carCommand("S"); //to stop car when button is released
                    carStatus.setText("Car at rest");
                }
                return true;
            }
        });

        carl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event){
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    carCommand("L");
                    carStatus.setText("Car going left");
                }
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    carCommand("S"); //to stop car when button is released
                    carStatus.setText("Car at rest");
                }
                return true;
            }
        });

        carr.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event){
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    carCommand("R");
                    carStatus.setText("Car going right");
                }
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    carCommand("S"); //to stop car when button is released
                    carStatus.setText("Car at rest");
                }
                return true;
            }
        });

        // Camera Button Listeners
        camf.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event){
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    camCommand("U");
                    cameraStatus.setText("Camera going upwards");
                }
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    camCommand("X");
                    cameraStatus.setText("Camera at rest");
                }
                return true;
            }
        });

        camb.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event){
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    camCommand("D");
                    cameraStatus.setText("Camera going downwards");
                }
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    camCommand("X");
                    cameraStatus.setText("Camera at rest");
                }
                return true;
            }
        });

        caml.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event){
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    camCommand("W");
                    cameraStatus.setText("Camera going left");
                }
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    camCommand("X");
                    cameraStatus.setText("Camera at rest");
                }
                return true;
            }
        });

        camr.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event){
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    camCommand("E");
                    cameraStatus.setText("Camera going right");
                }
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    camCommand("X");
                    cameraStatus.setText("Camera at rest");
                }
                return true;
            }
        });

        // Old button code (Not being used as car keeps continuing action even after button is released)
        /*carf.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(btSocket!=null) {
                    try {
                        btSocket.getOutputStream().write("F".getBytes());
                        msg("CAR F");
                    } catch (IOException E) {
                        msg("Error");                   }
                }
            }
        });

        carb.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(btSocket!=null) {
                    try {
                        btSocket.getOutputStream().write("B".getBytes());
                        msg("CAR B");
                    } catch (IOException E) {
                        msg("Error");
                    }
                }
            }
        });

        carl.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(btSocket!=null) {
                    try {
                        btSocket.getOutputStream().write("L".getBytes());
                        msg("CAR L");
                    } catch (IOException E) {
                        msg("Error");                         }
                }
            }
        });

        carr.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(btSocket!=null) {
                    try {
                        btSocket.getOutputStream().write("R".getBytes());
                        msg("CAR R");
                    } catch (IOException E) {
                        msg("Error");                         }
                }
            }
        });

        cars.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(btSocket!=null) {
                    try {
                        btSocket.getOutputStream().write("S".getBytes());
                        msg("CAR S");
                    } catch (IOException E) {
                        msg("Error");                         }
                }
            }
        });*/

    }

    @Override
    protected void onPause() {
        super.onPause();
        setContentView(R.layout.activity_main);

        try {
            btSocket.close();
        } catch(java.io.IOException E) {
            msg("Error while closing BT connection.");
        }
    }

    // For Car Commands
    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(MainActivity.this, "Connecting...", "Please wait!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    btAdapter = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice btDevice = btAdapter.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = btDevice.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                finish();
                msg("Connection Failed. Please try again.");
            } else {
                msg("Connected to " + name);
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }


    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

    // For Car Commands: (Forward - "F" Backward - "B" Left - "L" Right - "R" Stop = "S")
    private void carCommand(String s) {
        if(btSocket!=null) {
            try {
                btSocket.getOutputStream().write(s.getBytes()); // sends Command
            } catch (IOException E) {
                msg("Error");
            }
        }
    }

    // For Camera Commands: (Forward - "U" Backward - "D" Left - "W" Right - "E" Stop = "X")
    private void camCommand(String s) {
        if(btSocket!=null) {
            try {
                btSocket.getOutputStream().write(s.getBytes()); // sends Command
            } catch (IOException E) {
                msg("Error");
            }
        }
    }
}
