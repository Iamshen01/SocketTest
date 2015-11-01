package com.example.sockettest;

import com.example.sockettest.IMService.OnRecieveMessageListener;

public interface IMManager {
	
	void sendMessage(Message message, CallBack callBack);
	
	void addOnMessageRecieveListener(OnRecieveMessageListener onMessageRecieveListener);
}
