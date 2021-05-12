package com.khnsoft.stepdetector;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class DataBrowser extends AppCompatActivity {
	String curPath;
	String rootPath;
	
	TextView TcurPath;
	ListView LcurDir;
	
	ArrayList<String> files;
	ArrayAdapter<String> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_browser);
		
		rootPath = getFilesDir().getPath();
		curPath = "";
		TcurPath = findViewById(R.id.curPath);
		TcurPath.setText("/" + curPath);
		
		LcurDir = findViewById(R.id.curDir);
		
		files = new ArrayList<String>();
		
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, files);
		LcurDir.setAdapter(adapter);
		LcurDir.setOnItemClickListener(itemClickListener);
		LcurDir.setOnItemLongClickListener(itemLongClickListener);
		
		refreshFiles();
	}
	
	AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			String name = files.get(position);
			
			if (name.startsWith("[") && name.endsWith("]"))
				name = name.substring(1, name.length()-1);
			String path = curPath + "/" + name;
			File f = new File(rootPath + "/" + path);
			if (f.isDirectory()) {
				curPath = path;
				refreshFiles();
			}
		}
	};
	
	AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			String name = files.get(position);
			if (name.startsWith("[") && name.endsWith("]"))
				name = name.substring(1, name.length()-1);
			String path = curPath + "/" + name;
			removeAlert(path);
			return false;
		}
	};
	
	void refreshFiles() {
		TcurPath.setText("/" + curPath);
		files.clear();
		File current = new File(rootPath + "/" + curPath);
		String[] tmpFiles = current.list();
		
		if (tmpFiles != null) {
			for (int i=0; i<tmpFiles.length; i++) {
				String path = rootPath + "/" + curPath + "/" + tmpFiles[i];
				String Name = "";
				File f = new File(path);
				if (f.isDirectory())
					Name = "[" + tmpFiles[i] + "]";
				else
					Name = tmpFiles[i];
				files.add(Name);
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	void removeAlert(final String path) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(DataBrowser.this);
		dialog.setTitle("삭제 확인")
				.setMessage("정말 파일 " + path + "를 삭제하시겠습니까?")
				.setCancelable(true)
				.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						File f = new File(rootPath + "/" + path);
						if (f.isDirectory()) {
							removeDirectory(f.getPath());
							int lastSlash = curPath.lastIndexOf("/");
							String path = curPath.substring(0, lastSlash);
							curPath = path;
						}
						else f.delete();
						refreshFiles();
					}
				})
				.setNegativeButton("취소", null)
				.show();
	}
	
	void removeDirectory(String path) {
		File dir = new File(path);
		File[] childFileList = dir.listFiles();
		
		if (dir.exists()) {
			for (File childFile : childFileList) {
				if (childFile.isDirectory()) removeDirectory(childFile.getPath());
				else childFile.delete();
			}
			dir.delete();
		}
	}
	
	@Override
	public void onBackPressed() {
		if (curPath.isEmpty()) super.onBackPressed();
		else {
			int lastSlash = curPath.lastIndexOf("/");
			String path = curPath.substring(0, lastSlash);
			curPath = path;
			refreshFiles();
		}
	}
}
