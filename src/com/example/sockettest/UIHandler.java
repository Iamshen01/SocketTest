package com.example.sockettest;

import android.os.Handler;
import android.os.Looper;

public class UIHandler {
	private static final Handler handler = new Handler(Looper.getMainLooper());
	
	public static void post(Runnable r) {
		handler.post(r);
	}
	
	public static void postDelayed(Runnable r, int delayMillis) {
		handler.postDelayed(r, delayMillis);
	}
	public static void removeCallbacks(Runnable r) {
		handler.removeCallbacks(r);
	}
}
