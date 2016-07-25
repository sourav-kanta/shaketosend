package com.example.connectutils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ServerThread extends Thread {

	public static final int PORT=4444;
	public static ServerSocket server=null;
	Socket ser;
	Context c;
	String path;
	
	public void setPath(String p,Context con)
	{
		c=con;
		path=p;
	}
	
	public void initServer() 
	{
		try {
			server=new ServerSocket(PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true)
		{
		//	Toast.makeText(c, "Error in server setup 1", Toast.LENGTH_SHORT).show();
			try {
				Log.e("Msg", "Server Listening");
				ser=server.accept();
				EchoThread e=new EchoThread(ser);	
				e.setPath(path, c);
				e.start();
		//		Toast.makeText(c, "Error in server setup 2", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				try {
					server.close();
					ser.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Toast.makeText(c, "Error in server setup", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}
	}

}
