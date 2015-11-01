 package com.example.sockettest;

import android.support.v7.app.ActionBarActivity;

import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SecondActivity extends ActionBarActivity {
	
	private IMManager mIMManager;
	EditText et;
	Button btn;
	TextView tv;
	int messageID = 1;
	private ServiceConnection mIMConn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mIMManager = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mIMManager = (IMManager)service;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bindService(new Intent(this, IMService.class), mIMConn, BIND_AUTO_CREATE);
		et = (EditText)findViewById(R.id.et);
		btn = (Button)findViewById(R.id.btn);
		tv = (TextView)findViewById(R.id.tv);
		
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String msg = et.getText().toString();
				Toast.makeText(getApplicationContext(), msg, 1000).show();
				if(! msg.equals(""))
				{
					Message data = new Message();
					data.setMessageID(messageID++);
					data.setMessage(msg);
					mIMManager.sendMessage(data, new CallBack() {
						
						@Override
						public void Success(JSONObject data) {
							super.Success(data);
							// TODO Auto-generated method stub
							et.setText(data.toString());
						}
						
						@Override
						public void Error(int errorCode) {
							super.Error(errorCode);
							// TODO Auto-generated method stub
						}
					});
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.second, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unbindService(mIMConn);
		super.onDestroy();
	}
}
