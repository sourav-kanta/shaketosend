package com.example.connectutils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import com.example.shaketosend.MainActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class EchoUniThread extends Thread {
    protected Socket socket;
    String filepath;
    static Context context;
    boolean allowed=true;

    public EchoUniThread(Socket clientSocket) {
        this.socket = clientSocket;
    }
    
    public void setPath(String s,Context c)
    {
    	filepath=s;
    	context=c;
    }
    
    public static boolean isPresentUser(String name)
	{
		SharedPreferences prefs = context.getSharedPreferences("Preferences", 0); 
		Log.e("name", name);
		if (prefs.contains(name))
			return true;
		return false;
	}

    public void run() {
    	try
    	{
    	File file = new File(filepath);
        // Get the size of the file
    	final String filename=file.getName();
    	Log.e("Name", filename);
        final long length = file.length();
        final byte[] bytes = new byte[16 * 1024];
        final InputStream in = new FileInputStream(file);
        final OutputStream out = socket.getOutputStream();
        InputStream inp=socket.getInputStream();
        final DataOutputStream d=new DataOutputStream(out);
        DataInputStream din=new DataInputStream(inp);
        final String user=din.readUTF();
        Log.e("User Connected", user);               
			// set dalog message
			if(user.equals(MainActivity.Uniuser)) {					
						// if this button is clicked, close
						// current activity
						try{
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
						catch(Exception e)
						{
							e.printStackTrace();
						}
					
			}
			else {
						// if this button is clicked, just close
						// the dialog box and do nothing						
						try {
							out.close();
					        in.close();
							socket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			}

				// create alert dialog				
        }
    	catch(Exception e){
    		e.printStackTrace();
    	//	Toast.makeText(context, "Error in server write", Toast.LENGTH_SHORT).show();
    	}
    }
}
