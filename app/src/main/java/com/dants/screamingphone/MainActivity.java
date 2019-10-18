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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    //Below are variables associated with the accelerometer
    //private static final String TAG = "Main Activity";
    private SensorManager sensorManager;
    Sensor accelerometer;
    float acceleration = 0;
    int sensorType = Sensor.TYPE_LINEAR_ACCELERATION;
    int speed = SensorManager.SENSOR_DELAY_FASTEST;
    float threshold = (float) 11;
    ArrayList<Float> accelerationList;
    int arrayCapacity = 10;
    int ignoreCounter = 0;

    //Below are variables associated with text boxes
    TextView textBox2;
    TextView textBox3;
    Switch screaming;
    boolean yesNo = true;

    //Below are variables associated with playing music and random integers
    MediaPlayer mediaPlayer;
    final int numberOfFiles = 4;
    int[] indexList = new int[numberOfFiles];
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Defining text boxes
        textBox2 = findViewById(R.id.textBox2);
        textBox3 = findViewById(R.id.textBox3);

        screaming = findViewById(R.id.switch1);
        screaming.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    yesNo = false;
                }
                else{
                    yesNo = true;
                    ignoreCounter = 0;
                }
            }
        });

        //Defining accelerometer sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(MainActivity.this,accelerometer, speed);
        accelerometer = sensorManager.getDefaultSensor(sensorType);
        accelerationList = new ArrayList();

        //Defining media player
        mediaPlayer = MediaPlayer.create(this, R.raw.scream1);

        //Defining random integers
        for (int i=0; i<numberOfFiles; i++){
            indexList[i] = i;
        }
        index = 0;
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
        if (mySensor.getType() == sensorType && accelerationList.size() < arrayCapacity && yesNo) {

            accelerationList.add((float) Math.sqrt(Math.pow(sensorEvent.values[0], 2) + Math.pow(sensorEvent.values[1], 2) + Math.pow(sensorEvent.values[2], 2)));
            acceleration = accelerationList.get(0);
            textBox2.setText("Acceleration: " + acceleration);


            if (acceleration >= threshold && ignoreCounter == 0) {

                textBox3.setText("Peak Acceleration:" + acceleration);
                Log.d("Drop", "I FELL!!!");
                ignoreCounter = 1000;
                mediaPlayer.start();
                nextMedia();

            }
            if (ignoreCounter > 0){
                ignoreCounter -= 1;
            }
            Log.d("Drop", "Acceleration: " + acceleration + " Ignore Factor: " + ignoreCounter);
        }
        else if (yesNo) {
            accelerationList.remove(0);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
