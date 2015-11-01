package com.example.sockettest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class SocketClient extends Thread {
	private Socket mSocket;
	String mIp = "";
	int mPort = 0;
	boolean isStop = false;
	InputStream in;
	OutputStream out;
	HandlerThread sendThread;
	Handler sendHand;
	WatchDog watchDog;
	HashMap<Integer, CallBack> callBacks = new HashMap<Integer, CallBack>();
	
	public SocketClient(String ip, int port)
	{
		mIp = ip;
		mPort = port;
		init();
	}
	private void init()
	{
		sendThread = new HandlerThread("sendThread");
		sendThread.start();
		sendHand = new Handler(sendThread.getLooper());
		watchDog = new WatchDog();
	}
	
	@Override
	public void run ()
	{
		try 
		{
			Log.d("aaaa", "tid:" + Thread.currentThread().getId());
			mSocket = new Socket(mIp, mPort);
			in = mSocket.getInputStream();
			out = mSocket.getOutputStream();
			
			
			if(!mSocket.isClosed() && mSocket.isConnected())
			{
				watchDog.StartHeartBeat();
			}
			while(!mSocket.isClosed() && mSocket.isConnected())
			{
				try
				{
					byte[] buffer = new byte[5];
					int redlen = -1;
					Log.d("aaaa", "start read");
					
					while ((redlen = in.read(buffer)) != -1)
					{
						Log.d("aaaa", "readlen:" + redlen + "");
						byte flag = buffer[0];
						Log.d("aaaa", "flag:" + flag);
						if(flag == WatchDog.REC_HEARTBEAT)
						{
							Log.d("aaaa", "flag == REC_HEARTBEAT");
						}
						else if (flag == WatchDog.REC_MESSAGE) 
						{
							byte[] b_length = Arrays.copyOfRange(buffer, 1, 5);
							int content_length = byte2int(b_length);
							Log.d("aaaa", "content_length:" + content_length);
							byte[] b_content = new byte[content_length];
							int red_datalength = in.read(b_content);
							if(red_datalength == content_length)
							{
								String data = new String(b_content, Charset.forName("UTF-8"));
								watchDog.Rec(flag, data);
							}
							else
							{
								//TODO 服务器发送数据有误！
							}

						}
						Log.d("aaaa", "end read");
					}

				}
				catch(SocketTimeoutException e)
				{
					e.printStackTrace();
				}
			}
		} 
		catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void SendMessage(final Message message, CallBack callBack)
	{
		watchDog.SendMessage(message, callBack);
	}
	
	public void close()
	{
		isStop = true;
		try 
		{
			in.close();
			out.close();
			mSocket.close();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private class WatchDog {
		
		private final int ShutDownTimeOut = 2000;
		private final int HeartBeatTime = 10000;
		
		Runnable ShutDownRun = new Runnable() {
			@Override
			public void run() {
				try {
					mSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		Runnable HeartBeatRun = new Runnable() {
			@Override
			public void run() {
				try {
					Log.d("aaaa", "HeartBeatRun exec");
					out.write(new byte[]{SEND_HEARTBEAT});
					out.flush();
					sendHand.postDelayed(HeartBeatRun, HeartBeatTime);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		public static final byte SEND_HEARTBEAT = 1;
		public static final byte REC_HEARTBEAT = 2;
		public static final byte SEND_MESSAGE = 3;
		public static final byte REC_MESSAGE = 4;
		
		public void StartHeartBeat()
		{
			//sendHand.post(HeartBeatRun);
		}
		
		public void Rec(int flag, String data)
		{
			sendHand.removeCallbacks(ShutDownRun);
			if (flag == WatchDog.REC_MESSAGE) 
			{
				final JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(data);
					int MessageID = jsonObject.optInt("MessageID", 0);
					if(callBacks.containsKey(MessageID))
					{
						final CallBack callBack = callBacks.get(MessageID);
						callBacks.remove(MessageID);
						UIHandler.post(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								callBack.Success(jsonObject);
							}
						}); 
						
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		}
		
		public void SendMessage(final Message msg, final CallBack callBack)
		{
			sendHand.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					JSONObject jsonObject = new JSONObject();
					try {
						jsonObject.put("MessageID", msg.getMessageID());
						jsonObject.put("Message", msg.getMessage());
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String send_data = jsonObject.toString();
					Log.d("aaaa","send data:" +  send_data);
					byte[] buffer = send_data.getBytes(Charset.forName("UTF-8"));
					try {
						callBacks.put(msg.getMessageID(), callBack);
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						stream.write(new byte[]{SEND_MESSAGE});
						stream.write(int2byte(buffer.length));
						stream.write(buffer);
						out.write(stream.toByteArray());
						out.flush();
						stream.close();
						//sendHand.postDelayed(ShutDownRun, ShutDownTimeOut);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						callBacks.remove(msg.getMessageID());
					}
				}
			});
		}

		
	}
	
	public static byte[] int2byte(int res) {
		byte[] targets = new byte[4];

		targets[0] = (byte) (res & 0xff);// 最低位 
		targets[1] = (byte) ((res >> 8) & 0xff);// 次低位 
		targets[2] = (byte) ((res >> 16) & 0xff);// 次高位 
		targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。 
		return targets; 
	} 
	
	public static int byte2int(byte[] res) {
		int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) // | 表示安位或   
				| ((res[2] << 24) >>> 8) | (res[3] << 24);   
				return targets;  
		//return  res[0] * 8 + res[1] * 4 + res[2] * 1 + res[3];
	} 

}
