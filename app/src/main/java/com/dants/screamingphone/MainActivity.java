package com.dants.screamingphone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    //Below are variables associated with the accelerometer
    //private static final String TAG = "Main Activity";
    private SensorManager sensorManager;
    Sensor accelerometer;
    float acceleration = 0;
    int sensorType = Sensor.TYPE_LINEAR_ACCELERATION;
    int speed = SensorManager.SENSOR_DELAY_NORMAL;
    float threshold = (float) 11;
    int ignoreFactor;
    int ignoreCounter;
    ArrayList<Float> accelerationList;

    //Below are variables associated with text boxes
    TextView textBox1;
    TextView textBox2;
    TextView textBox3;

    //Below are variables associated with playing music and random integers
    MediaPlayer mediaPlayer;
    final int numberOfFiles = 4;
    int[] indexList = new int[numberOfFiles];
    int index;


    //Below are variables associated with buttons
    Button simulateButton;
    Button switchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Defining text boxes
        textBox1 = findViewById(R.id.textBox1);
        textBox2 = findViewById(R.id.textBox2);
        textBox3 = findViewById(R.id.textBox3);
        //textBox4 = findViewById(R.id.textBox4);

        //Defining accelerometer sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(MainActivity.this,accelerometer, speed);
        accelerometer = sensorManager.getDefaultSensor(sensorType);

        //Defining media player
        mediaPlayer = MediaPlayer.create(this, R.raw.scream1);

        //Defining random integers
        for (int i=0; i<numberOfFiles; i++){
            indexList[i] = i;
        }
        index = 0;

        switchButton = findViewById(R.id.SwitchButton);
        switchButton.setText("Next Sound");//set the text on button
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMedia();
            }
        });

        simulateButton = findViewById(R.id.SimulateButton);
        simulateButton.setText("Simulate Fall");//set the text on button
        simulateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accelerationList.set(0, threshold);
            }
        });

        ignoreFactor = 100;
        ignoreCounter = 0;
        accelerationList = new ArrayList();
    }
    public void nextMedia(){
        index++;
        if (index == 4){
            index = 0;
        }

        if (index==0){
            mediaPlayer = MediaPlayer.create(this, R.raw.scream1);
        }
        else if (index==1){
            mediaPlayer = MediaPlayer.create(this, R.raw.scream2);
        }
        else if (index==2){
            mediaPlayer = MediaPlayer.create(this, R.raw.scream3);
        }
        else if (index==3){
            mediaPlayer = MediaPlayer.create(this, R.raw.scream4);
        }
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, speed);
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        if (mySensor.getType() == sensorType) {
            textBox2.setText("Acceleration: " + acceleration);

            if (accelerationList.size() >= ignoreFactor) {
                accelerationList.remove(0);
            }
            accelerationList.add(acceleration);
            Log.i("TAG", ""+accelerationList.toString());

            if (accelerationList.get(0) >= threshold) {
                textBox3.setText("Peak Acceleration:"+acceleration);
                mediaPlayer.start();
                Log.i("TAG", "OH GOD THE PAIN " + acceleration);
                nextMedia();
                //ignoreCounter = 0;
            }
            if (ignoreCounter < ignoreFactor){
                ignoreCounter++;
            }
        }
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];
        acceleration = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
