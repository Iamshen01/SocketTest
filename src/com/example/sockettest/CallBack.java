package com.example.sockettest;

import org.json.JSONObject;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public abstract class CallBack {
	
	private static final String TAG = "CallBack";
	private static final int TIMEOUT_MILLIS = 5000;
	private static final int TIMEOUT_CODE = 1;
	Runnable TimeOutRun = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Error(TIMEOUT_CODE);
		}
	};
	
	public  CallBack()
	{
		UIHandler.postDelayed(TimeOutRun, TIMEOUT_MILLIS);
	}

	public void Success(JSONObject data)
	{
		UIHandler.removeCallbacks(TimeOutRun);
	}

	public void Error(int errorCode)
	{
		switch (errorCode) {
		case TIMEOUT_CODE:
			Log.i(TAG, "接收数据超时");
			break;

		default:
			break;
		}
	}

}
