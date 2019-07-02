package com.khnsoft.stepdetector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RFTest extends AppCompatActivity {
	TextView Tversion;
	TextView Tlog;
	ScrollView Slog;
	Button Bswitch;
	HttpAsyncTask httpTask;
	boolean testing = false;
	
	boolean acceReady = false;
	boolean gyroReady = false;
	boolean stepReady = false;
	SensorManager sm;
	Sensor AcceSensor;
	Sensor GyroSensor;
	Sensor StepSensor;
	float[] mValues = new float[7];
	long timeStart = 0;
	long timeDuration = 0;
	int count = 0;
	StringBuffer sb;
	String[] mode = {"Test", "Test"};
	
	final static int WindowSize = 20;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_testing);
		
		Tversion = findViewById(R.id.Tversion);
		Tlog = findViewById(R.id.Tlog);
		Slog = findViewById(R.id.scrollLog);
		Bswitch = findViewById(R.id.Bswitch);
		
		Bswitch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (testing) {
					// Stop testing
					Bswitch.setText("Test");
					Tlog.append("Stop testing.\n");
					Slog.fullScroll(View.FOCUS_DOWN);
					testing = !testing;
					count = 0;
					timer.removeMessages(0);
				} else {
					// Start testing
					Bswitch.setText("Stop");
					Tlog.append("Start testing.\n");
					Slog.fullScroll(View.FOCUS_DOWN);
					testing = !testing;
					
					sb = new StringBuffer();
					acceReady = false;
					gyroReady = false;
					stepReady = false;
					sb.append("ElapsedTime\tMode0\tMode1\tAcce0\tAcce1\tAcce2\tGyro0\tGyro1\tGyro2\tStep\n");
					timeStart = System.currentTimeMillis();
					timer.sendEmptyMessage(0);
				}
			}
		});
		
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		AcceSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		GyroSensor = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		StepSensor = sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
		
		Bswitch.setEnabled(true);
	}
	
	Handler timer = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			timeDuration = System.currentTimeMillis() - timeStart;
			timer.sendEmptyMessage(0);
			return false;
		}
	});
	
	SensorEventListener sensorlistener = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent event) {
			if (!testing) return;
			
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
				
				if (++count == WindowSize) {
					count = 0;
					String data = prepareData(sb.toString());
					Log.i("@@@", data);
					try {
						httpTask = new HttpAsyncTask(RFTest.this);
						httpTask.execute(data);
					} catch (Exception e) {
						e.printStackTrace();
						Tlog.append("Prediction failed\n");
						Slog.fullScroll(View.FOCUS_DOWN);
					}
					sb = new StringBuffer();
					sb.append("ElapsedTime\tMode0\tMode1\tAcce0\tAcce1\tAcce2\tGyro0\tGyro1\tGyro2\tStep\n");
				}
			}
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
		}
	};
	
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
	
	class HttpAsyncTask extends AsyncTask<String, Void, String> {
		RFTest ui;
		String mode;
		
		HttpAsyncTask(RFTest ui) {
			this.ui = ui;
		}
		
		@Override
		protected String doInBackground(String... strs) {
			mode = strs[0];
			return POST(strs[0]);
		}
		
		@Override
		protected void onPostExecute(final String rec) {
			super.onPostExecute(rec);
			ui.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Tlog.append(rec + "\n");
					Slog.fullScroll(View.FOCUS_DOWN);
				}
			});
		}
	}
	
	String POST(String rawdata) {
		String url = "http://svclaw.ipdisk.co.kr:8002/shopCharacter/getrfresult/";
		
		String result = "";
		InputStream is;
		
		try {
			URL urlCon = new URL(url);
			HttpURLConnection httpCon = (HttpURLConnection) urlCon.openConnection();
			httpCon.setDoOutput(true);
			
			OutputStream os = httpCon.getOutputStream();
			os.write(rawdata.getBytes("utf-8"));
			os.flush();
			
			int status = httpCon.getResponseCode();
			if (status != HttpURLConnection.HTTP_OK)
				is = httpCon.getErrorStream();
			else
				is = httpCon.getInputStream();
			if (is != null)
				result = convertInputStreamToString(is);
			else
				result = "Error : Did not work!";
			httpCon.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			return "Error! Failed to connect!";
		}
		
		return result;
	}
	
	String convertInputStreamToString(InputStream inputStream) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null) {
			result += line;
		}
		inputStream.close();
		return result;
	}
	
	String prepareData(String raw) {
		BufferedReader br = new BufferedReader(new StringReader(raw));
		StringBuffer result = new StringBuffer();
		try {
			String line = br.readLine();
			int lineNum = 0;
			float[][] datas = new float[WindowSize][];
			while ((line = br.readLine()) != null && !line.isEmpty()) {
				String[] colums = line.split("\t");
				datas[lineNum] = new float[] {
						//	Float.parseFloat(colums[3]), Float.parseFloat(colums[4]), Float.parseFloat(colums[5]),
						Float.parseFloat(colums[6]), Float.parseFloat(colums[7]), Float.parseFloat(colums[8]) };
				if (++lineNum >= WindowSize) {
					float[] sum = new float[datas[0].length];
					float[] sqrsum = new float[datas[0].length];
					for (float[] data : datas) {
						for (int i=0; i<data.length; i++) {
							sum[i] += data[i];
							sqrsum[i] += data[i] * data[i];
						}
					}
					for (int i=0; i<datas[0].length; i++) {
						float mean = sum[i] / WindowSize;
						float var = sqrsum[i] - mean*mean;
						result.append(" " + mean + " " + var);
					}
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}
	
	
}
