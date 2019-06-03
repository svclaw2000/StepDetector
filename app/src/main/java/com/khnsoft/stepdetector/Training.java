package com.khnsoft.stepdetector;

import android.content.SharedPreferences;
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
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

import libsvm.*;

public class Training extends AppCompatActivity {
	TextView Tversion;
	TextView Tlog;
	Button Bswitch;
	SharedPreferences sp;
	SharedPreferences.Editor editor;
	String localModelVersion;
	String serverModelVersion;
	HttpAsyncTask httpTask;
	String modelName;
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
		setContentView(R.layout.activity_training);
		
		Tversion = findViewById(R.id.Tversion);
		Tlog = findViewById(R.id.Tlog);
		Bswitch = findViewById(R.id.Bswitch);
		sp = getSharedPreferences("Settings", MODE_PRIVATE);
		editor = sp.edit();
		checkVersion();
		
		Bswitch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (testing) {
					// Stop testing
					Bswitch.setText("Test");
					Tlog.append("Stop testing.\n");
					testing = !testing;
					count = 0;
					timer.removeMessages(0);
				} else {
					// Start testing
					Bswitch.setText("Stop");
					Tlog.append("Start testing.\n");
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
						String result = predictData(data);
						if (result.equals("0.0")) result = "Stop";
						else if (result.equals("1.0")) result = "Walk";
						Tlog.append(result + "\n");
					} catch (Exception e) {
						e.printStackTrace();
						Tlog.append("Prediction failed\n");
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
		Training ui;
		String mode;
		
		HttpAsyncTask(Training ui) {
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
					if (rec.startsWith("v")) {
						serverModelVersion = rec;
						Tlog.append("Server model version is " + serverModelVersion + "\n");
						getModel();
					}
					else {
						Tlog.append(rec + "\n");
					}
				}
			});
		}
	}
	
	String POST(String mode) {
		String url;
		if (mode.equals("getVersion"))
			url = "http://svclaw.ipdisk.co.kr:8002/shopCharacter/getversion/";
		else if (mode.equals("getModel"))
			url = "http://svclaw.ipdisk.co.kr:8002/shopCharacter/getmodel/";
		else return "Error : Incorrect mode!";
		
		String result = "";
		InputStream is;
		
		try {
			URL urlCon = new URL(url);
			HttpURLConnection httpCon = (HttpURLConnection) urlCon.openConnection();
			
			if (mode.equals("getVersion")) {
				int status = httpCon.getResponseCode();
				if (status != HttpURLConnection.HTTP_OK) is = httpCon.getErrorStream();
				else is = httpCon.getInputStream();
				if (is != null) result = convertInputStreamToString(is);
				else result = "Error : Did not work!";
			} else if (mode.equals("getModel")) {
				int len = httpCon.getContentLength();
				byte[] tmpByte = new byte[len];
				is = httpCon.getInputStream();
				File folder = new File(Training.this.getFilesDir(), "Models");
				File file = new File(folder, localModelVersion + ".model");
				if (!folder.exists()) folder.mkdirs();
				FileOutputStream fos = new FileOutputStream(file);
				int Read;
				
				while ((Read = is.read(tmpByte)) >= 0) {
					fos.write(tmpByte, 0, Read);
				}
				
				is.close();
				fos.close();
				
				result = "Model saved to " + file.getPath();
			}
			
			httpCon.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
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
	
	void checkVersion() {
		localModelVersion = sp.getString("version", "v0.0");
		Tlog.append("Checking version.\n");
		httpTask = new HttpAsyncTask(this);
		httpTask.execute("getVersion");
	}
	
	void getModel() {
		if (serverModelVersion != null) {
			if (serverModelVersion.equals(localModelVersion)) Tlog.append("Model is already up to date.\n");
			else {
				httpTask = new HttpAsyncTask(this);
				httpTask.execute("getModel");
				localModelVersion = serverModelVersion;
				editor.putString("version", localModelVersion);
				editor.apply();
				Tlog.append("Get the newest version of model from server.\n");
			}
			modelName = localModelVersion + ".model";
			Bswitch.setEnabled(true);
		}
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
				datas[lineNum] = new float[] { Float.parseFloat(colums[3]),
						Float.parseFloat(colums[4]), Float.parseFloat(colums[5]), Float.parseFloat(colums[6]),
						Float.parseFloat(colums[7]), Float.parseFloat(colums[8]) };
				if (++lineNum >= WindowSize) {
					float[] sum = new float[datas[0].length];
					float[] sqrsum = new float[datas[0].length];
					for (float[] data : datas) {
						for (int i=0; i<data.length; i++) {
							sum[i] += data[i];
							sqrsum[i] += data[i] * data[i];
						}
					}
					result.append("2");
					for (int i=0; i<datas[0].length; i++) {
						float mean = sum[i] / WindowSize;
						float var = sqrsum[i] - mean*mean;
						result.append(" " + (i * 2 + 1) + ":" + mean + " " + (i * 2 + 2) + ":" + var);
					}
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}
	
	String predictData(String data) throws IOException{
		String input = data;
		String output;
		svm_model model = svm.svm_load_model((new File(Training.this.getFilesDir(), "Models/"+localModelVersion+".model")).getPath());
		if (model == null) {
			return "Can't open model file.\n";
		}
		StringTokenizer st = new StringTokenizer(input, " \t\n\r\f:");
		
		double target = atof(st.nextToken());
		int m = st.countTokens() / 2;
		svm_node[] x = new svm_node[m];
		for (int j=0; j<m; j++) {
			x[j] = new svm_node();
			x[j].index = atoi(st.nextToken());
			x[j].value = atof(st.nextToken());
		}
		
		double v = svm.svm_predict(model, x);
		output = String.valueOf(v);
		return output;
	}
	
	private static double atof(String s) {
		return Double.valueOf(s).doubleValue();
	}
	
	private static int atoi(String s) {
		return Integer.parseInt(s);
	}
}
