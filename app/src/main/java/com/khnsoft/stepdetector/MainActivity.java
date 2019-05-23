package com.khnsoft.stepdetector;

import android.content.Intent;
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
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
	Button Bcancel;
	Button Bdata;
	boolean sensing = false;
	SensorManager sm;
	Sensor AcceSensor;
	Sensor GyroSensor;
	Sensor StepSensor;
	StringBuffer sb;
	float[] mValues = new float[7];
	int count = 0;
	long timeStart = 0;
	long timeDuration = 0;
	final long SCANNING_DURATION_LONG = 30000;
	final long SCANNING_DURATION_SHORT = 5000;
	long curDuration;
	
	TextView Ttime;
	TextView Tstatus;
	
	Button Cforward;
	Button Cleft;
	Button Cright;
	Button Cstop;
	Button Cstopleft;
	Button Cstopright;
	Button Cup;
	Button Cdown;
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
					Ttime.setText("00:00:00");
					Tstatus.setText("Not running");
					Toast.makeText(MainActivity.this, "Cancel data scanning.", Toast.LENGTH_SHORT).show();
					timer.removeMessages(0);
				}
			}
		});
		Bdata = findViewById(R.id.Bdata);
		Bdata.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!sensing) {
					Intent intent = new Intent(MainActivity.this, DataBrowser.class);
					startActivity(intent);
				}
			}
		});
		
		Ttime = findViewById(R.id.Ttime);
		Tstatus = findViewById(R.id.Tstatus);
		
		Cforward = findViewById(R.id.Cforward);
		Cleft = findViewById(R.id.Cleft);
		Cright = findViewById(R.id.Cright);
		Cstop = findViewById(R.id.Cstop);
		Cstopleft = findViewById(R.id.Cstopleft);
		Cstopright = findViewById(R.id.Cstopright);
		Cup = findViewById(R.id.Cup);
		Cdown = findViewById(R.id.Cdown);
		Ctyping = findViewById(R.id.Ctyping);
		
		Cforward.setOnClickListener(this);
		Cleft.setOnClickListener(this);
		Cright.setOnClickListener(this);
		Cstop.setOnClickListener(this);
		Cstopleft.setOnClickListener(this);
		Cstopright.setOnClickListener(this);
		Cup.setOnClickListener(this);
		Cdown.setOnClickListener(this);
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
			if (timeDuration < curDuration) {
				Ttime.setText(String.format("%02d:%02d:%02d", timeDuration / 1000 / 60, timeDuration / 1000 % 100, timeDuration / 10 % 100));
				timer.sendEmptyMessage(0);
			} else {
				sensing = false;
				Ttime.setText("00:00:00");
				Tstatus.setText("Not running");
				
				SimpleDateFormat date = new SimpleDateFormat("yyMMdd_HHmmss");
				
				String FILE_NAME = "data_" + date.format(new Date()) + ".tsv";
				File folder = new File(MainActivity.this.getFilesDir(), mode[0]+"/"+mode[1]);
				File file = new File(folder, FILE_NAME);
				
				try {
					if (!folder.exists()) folder.mkdirs();
					FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fileWriter);
					bw.write(sb.toString());
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
		if (sensing) {
			Toast.makeText(MainActivity.this, "Please wait until sensing process end.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		mode[0] = v.getTag().toString();
		mode[1] = ((Button)v).getText().toString();
		
		sb = new StringBuffer();
		sensing = true;
		Tstatus.setText(String.format("%s : %s", mode[0], mode[1]));
		if (mode[0].equals("Stop") && (mode[1].equals("Left") || mode[1].equals("Right") || mode[1].equals("Up") || mode[1].equals("Down")))
			curDuration = SCANNING_DURATION_SHORT;
		else
			curDuration = SCANNING_DURATION_LONG;
		timeStart = System.currentTimeMillis();
		timer.sendEmptyMessage(0);
	}
}
