package com.example.connectutils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

import com.example.shaketosend.MainActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class RecieveThread extends AsyncTask<Void, Integer, Void>{
	Socket socket=null;
	String ip;
	Context context;
	int time;
	double speed=0;
	Date d1,d2;
	long length;
	
	public void setIp(String s,Context c)
	{
		ip=s;
		context=c;
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		d1=new Date();
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
    	try
    	{
    		Log.e("msg", "Client active");
    		socket = new Socket(ip, ServerThread.PORT);
    		InputStream in = null;
            OutputStream out = null;
            in = socket.getInputStream();
            OutputStream outp=socket.getOutputStream();
            DataInputStream d = new DataInputStream(in);
            DataOutputStream dout=new DataOutputStream(outp);
            dout.writeUTF(MainActivity.user);
            String filenm=d.readUTF();
            length=Long.parseLong(d.readUTF());
            Log.e("Nm", filenm);
            File f = new File(Environment.getExternalStorageDirectory()+"//Shake_And_Send");
            if(!f.exists()) 
            	f.mkdir();
    		out = new FileOutputStream(Environment.getExternalStorageDirectory()+"//Shake_And_Send//"+filenm);
    		byte[] bytes = new byte[16*1024];

            int count;
            long total=0;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
                total+=count;
                
                MainActivity.prog=(int) (total*100/length);                
                publishProgress(MainActivity.prog);                
            }           
            out.close();
            in.close();
            socket.close();
            Log.e("transferred", "file");
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		Log.e("transferred", "file error");
    	//	Toast.makeText(context, "Error in server read", Toast.LENGTH_SHORT).show();
    	}
    	
    	return null;
	}
	
	@Override
	protected void onProgressUpdate(Integer... progress) {
	     MainActivity.setProgressPercent(progress[0]);
	 }
	
	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		MainActivity.prog=0;
		d2=new Date();
		long diff = d2.getTime() - d1.getTime();
		Log.e("Time Difference", diff+" seconds");
		double diffSeconds = diff / 1000;
		speed=length/diffSeconds;
		speed/=1024;
		MainActivity.setSpeed(speed);
	//	MainActivity.enableButton();
	}

}
