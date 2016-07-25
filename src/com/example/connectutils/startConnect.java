package com.example.connectutils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.example.shaketosend.MainActivity;
import com.example.shaketosend.WManager;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class startConnect extends AsyncTask<Void, Void, Void>{
	
	String ipStr=null;
	Context context;
	int cnt=10000,i=1;
	private startConnect() {
	}
	
	public startConnect(String ip,Context c)
	{
		ipStr=ip;
		context=c;
	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		boolean str=false;
        try {
			while((!str) && i<=cnt){
				str=InetAddress.getByName(ipStr).isReachable(2000);
				i++;
				Log.e(str+"", str+"");				
			}
			
				
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("MSG", "Not connected");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if(i>=cnt){
        	WManager.connectToServer(context);
        	return;
        }        
        	RecieveThread r=new RecieveThread();
        	r.setIp(ipStr,context);
        	r.execute();        
        MainActivity.isConnected=true;
	}	
	
	
}
