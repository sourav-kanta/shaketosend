package com.example.connectutils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class EchoThread extends Thread {
    protected Socket socket;
    String filepath;
    Context context;

    public EchoThread(Socket clientSocket) {
        this.socket = clientSocket;
    }
    
    public void setPath(String s,Context c)
    {
    	filepath=s;
    	context=c;
    }

    public void run() {
    	try
    	{
    	File file = new File(filepath);
        // Get the size of the file
    	String filename=file.getName();
    	Log.e("Name", filename);
        long length = file.length();
        byte[] bytes = new byte[16 * 1024];
        InputStream in = new FileInputStream(file);
        OutputStream out = socket.getOutputStream();
        InputStream inp=socket.getInputStream();
        DataOutputStream d=new DataOutputStream(out);
        DataInputStream din=new DataInputStream(inp);
        String user=din.readUTF();
        Log.e("User Connected", user);
        d.writeUTF(filename);
        d.writeUTF(length+"");
        int count;
        while ((count = in.read(bytes)) > 0) {
            out.write(bytes, 0, count);
        }
        Log.e("File", "transferred");
        out.close();
        in.close();
        socket.close();   
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	//	Toast.makeText(context, "Error in server write", Toast.LENGTH_SHORT).show();
    	}
    }
}
