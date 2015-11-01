package com.example.sockettest;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import org.json.JSONObject;

import android.media.tv.TvContentRating;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
	EditText et;
	Button btn;
	TextView tv;
	SocketClient client;
	int messageID = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
					client.SendMessage(data, new CallBack() {
						
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
		
		client = new SocketClient("192.168.1.102", 5555);
		client.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
}
