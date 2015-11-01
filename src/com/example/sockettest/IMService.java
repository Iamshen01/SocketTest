package com.example.sockettest;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public class IMService extends Service implements IMManager {

	public static interface OnRecieveMessageListener
	{
		void OnRecieve(Message message);
	}
	
	public class IMManagerImp extends Binder implements IMManager
	{
		
		IMService mIMService;
		
		public IMManagerImp(IMService IMService)
		{
			mIMService = IMService;
		}

		@Override
		public void sendMessage(Message message, CallBack callBack) {
			// TODO Auto-generated method stub
			mIMService.sendMessage(message, callBack);
		}

		@Override
		public void addOnMessageRecieveListener(OnRecieveMessageListener onMessageRecieveListener) {
			// TODO Auto-generated method stub
			mListenerList.add(onMessageRecieveListener);
		}
	}
	
	private IMManagerImp mIMManagerImp;
	private List<OnRecieveMessageListener> mListenerList;
	private SocketClient socketClient;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mIMManagerImp = new IMManagerImp(this);
		mListenerList = new ArrayList<OnRecieveMessageListener>();
		socketClient = new SocketClient("192.168.1.102", 5555);
		socketClient.start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		return mIMManagerImp;
	}


	@Override
	public void sendMessage(Message message, CallBack callBack) {
		// TODO Auto-generated method stub
		socketClient.SendMessage(message, callBack);
	}


	@Override
	public void addOnMessageRecieveListener(OnRecieveMessageListener onMessageRecieveListener) {
		// TODO Auto-generated method stub
		
	}
}
