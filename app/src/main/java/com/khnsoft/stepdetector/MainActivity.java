package com.khnsoft.stepdetector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
	Button Bcancel;
	boolean sensing = false;
	SensorManager sm;
	Sensor AcceSensor;
	Sensor GyroSensor;
	Sensor StepSensor;
	StringBuffer sb;
	float[] mValues = new float[7];
	float[] defValue = {0, 0, 0, 0, 0, 0, 0};
	int count = 0;
	long timeStart = 0;
	long timeDuration = 0;
	final long SCANNING_DURATION = 30000;
	String curStatus = "";
	
	TextView Ttime;
	
	Button Cforward;
	Button Cleft;
	Button Cright;
	Button Cstop;
	Button Cleftright;
	Button Cupdown;
	Button Ctyping;
	String[] mode = {"", ""};
	
	boolean acceReady = false;
	boolean gyroReady = false;
	boolean stepReady = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Bcancel = findViewById(R.id.Bcancel);
		Bcancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sensing) {
					sensing = false;
					Toast.makeText(MainActivity.this, "Cancel data scanning.", Toast.LENGTH_SHORT).show();
					timer.removeMessages(0);
				}
			}
		});
		
		Ttime = findViewById(R.id.Ttime);
		
		Cforward = findViewById(R.id.Cforward);
		Cleft = findViewById(R.id.Cleft);
		Cright = findViewById(R.id.Cright);
		Cstop = findViewById(R.id.Cstop);
		Cleftright = findViewById(R.id.Cleftright);
		Cupdown = findViewById(R.id.Cupdown);
		Ctyping = findViewById(R.id.Ctyping);
		
		Cforward.setOnClickListener(this);
		Cleft.setOnClickListener(this);
		Cright.setOnClickListener(this);
		Cstop.setOnClickListener(this);
		Cleftright.setOnClickListener(this);
		Cupdown.setOnClickListener(this);
		Ctyping.setOnClickListener(this);
		
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		AcceSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		GyroSensor = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		StepSensor = sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
	}
	
	SensorEventListener sensorlistener = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent event) {
			if (!sensing) return;
			
			float[] value = event.values;
			
			switch (event.sensor.getType()) {
				case Sensor.TYPE_ACCELEROMETER:
					mValues[0] = value[0];
					mValues[1] = value[1];
					mValues[2] = value[2];
					acceReady = true;
					break;
				
				case Sensor.TYPE_GYROSCOPE:
					mValues[3] = value[0];
					mValues[4] = value[1];
					mValues[5] = value[2];
					gyroReady = true;
					break;
				
				case Sensor.TYPE_STEP_DETECTOR:
					mValues[6] = value[0];
					stepReady = true;
					break;
			}
			
			if (acceReady && gyroReady) {
				if (!stepReady) mValues[6] = 0;
				else stepReady = false;
				sb.append(String.format("%d\t%s\t%s\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\t%.1f\n",
						timeDuration, mode[0], mode[1], mValues[0], mValues[1], mValues[2], mValues[3], mValues[4], mValues[5], mValues[6]));
				acceReady = false;
				gyroReady = false;
			}
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
		}
	};
	
	Handler timer = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			timeDuration = System.currentTimeMillis() - timeStart;
			if (timeDuration < SCANNING_DURATION) {
				Ttime.setText(String.format("%02d:%02d:%02d", timeDuration / 1000 / 60, timeDuration / 1000 % 100, timeDuration / 10 % 100));
				timer.sendEmptyMessage(0);
			} else {
				sensing = false;
				
				String FILE_NAME = "Log.log";
				File file = new File(MainActivity.this.getFilesDir(), FILE_NAME);
				
				try {
					FileWriter fileWriter = new FileWriter(file.getAbsoluteFile(), true);
					BufferedWriter bw = new BufferedWriter(fileWriter);
					bw.write(sb.toString() + "\n");
					bw.close();
					Toast.makeText(MainActivity.this, "Log Saved", Toast.LENGTH_SHORT).show();
					count++;
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(MainActivity.this, "Error while Saving", Toast.LENGTH_SHORT).show();
				}
				timer.removeMessages(0);
			}
			return false;
		}
	});
	
	@Override
	protected void onResume() {
		super.onResume();
		sm.registerListener(sensorlistener, AcceSensor, SensorManager.SENSOR_DELAY_UI);
		sm.registerListener(sensorlistener, GyroSensor, SensorManager.SENSOR_DELAY_UI);
		sm.registerListener(sensorlistener, StepSensor, SensorManager.SENSOR_DELAY_UI);
	}
	
	@Override
	protected void onPause() {
		sm.unregisterListener(sensorlistener);
		super.onPause();
	}
	
	@Override
	public void onClick(View v) {
		if (sensing) return;
		
		mode[0] = v.getTag().toString();
		mode[1] = ((Button)v).getText().toString();
		
		sb = new StringBuffer();
		sensing = true;
		timeStart = System.currentTimeMillis();
		timer.sendEmptyMessage(0);
	}
}
