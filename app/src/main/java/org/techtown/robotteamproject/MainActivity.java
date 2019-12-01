package org.techtown.robotteamproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {

    // for sending data
    public static final int REQUEST_CODE = 101;
    public static final String KEY_SIMPLE_DATA = "data";


    private BluetoothSPP bt;
    private TextView txtResult;
    String temp;
    String humidity;
    String mymessage;
    TextView textView;
    String t;
    String h;
    ImageView imageviewsun;
    ImageView imageviewrainbowcloud;
    ImageView imageviewrainbowpeople;
    int tem;
    int hum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("err2","Start");

        bt = new BluetoothSPP(this); //Initializing

        textView = (TextView) findViewById(R.id.textView);
        imageviewsun = (ImageView) findViewById(R.id.sun);
        imageviewrainbowcloud = (ImageView) findViewById(R.id.rainbowcloud);
        imageviewrainbowpeople = (ImageView) findViewById(R.id.rainbowpeople);


        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
            public void onDataReceived(byte[] data, String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnConnect = findViewById(R.id.btnConnect); //연결시도
        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });


        // gc
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신


            public void onDataReceived(byte[] data, String message) {


                if(message.length() > 5) {
                    message = message.substring(0,5);
                    mymessage="";
                    mymessage += "온도,습도:";
                    mymessage += message;
                    textView.setText(mymessage);

                    String[] words = message.split(",");
                    t = words[0];
                    h = words[1];

                    tem = Integer.parseInt(t);
                    hum = Integer.parseInt(h);

                    if ( tem >20 && hum < 50) {
                        // 날씨 조아
                        imageviewsun.setVisibility(View.VISIBLE);
                        imageviewrainbowcloud.setVisibility(View.INVISIBLE);
                        imageviewrainbowpeople.setVisibility(View.INVISIBLE);


                    }
                    else if ( tem < 5) {
                        // 날씨 눈와
                        imageviewsun.setVisibility(View.INVISIBLE);
                        imageviewrainbowcloud.setVisibility(View.INVISIBLE);
                        imageviewrainbowpeople.setVisibility(View.VISIBLE);

                    }
                    else {
                        //날씨 img_raincloud
                        imageviewsun.setVisibility(View.INVISIBLE);
                        imageviewrainbowcloud.setVisibility(View.VISIBLE);
                        imageviewrainbowpeople.setVisibility(View.INVISIBLE);
                    }





                }
                else {
                    Toast.makeText(MainActivity.this, "Error 다시 START를 눌러주세요", Toast.LENGTH_SHORT).show();
                }





                /*
                String[] words = message.split(",");
                temp = words[0];
                humidity = words[1];

                txtResult.setText(temp + "\n" + humidity + "\n");

                */
                // sending
                //Intent intent = new Intent (getApplicationContext(), ResultActivity.class);
                //SimpleData simpledata = new SimpleData(Double.parseDouble("10") , Double.parseDouble("10"));
                //intent.putExtra(KEY_SIMPLE_DATA, data);
                //startActivityForResult(intent,REQUEST_CODE);




            }


                /*
            public void onDataReceived (byte[] data, String message) {
                    try {
                        Log.d("err2",message);

                        JSONArray jsonArray = new JSONArray(message);
                        for (int k = 0; k < jsonArray.length(); k++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(k);
                            temp = jsonObject.getString("temperature");
                            humidity = jsonObject.getString("humidity");
                        }
                    } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

             */

        });

    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService(); //블루투스 중지
    }

    public void onStart() {
        super.onStart();

        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
                setup();
            }
        }
    }

    public void setup() {
        Button btnSend = findViewById(R.id.btnSend); //데이터 전송
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.send("Text", true);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}

