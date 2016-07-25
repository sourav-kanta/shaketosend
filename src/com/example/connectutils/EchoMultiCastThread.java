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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

public class EchoMultiCastThread extends Thread {
    protected Socket socket;
    String filepath;
    static Context context;
    boolean allowed=true;

    public EchoMultiCastThread(Socket clientSocket) {
        this.socket = clientSocket;
    }
    
    public void setPath(String s,Context c)
    {
    	filepath=s;
    	context=c;
    }
    
    public boolean isPresentUser(String name)
	{
		/*SharedPreferences prefs = context.getSharedPreferences("Preferences", 0); 
		Log.e("name", name);
		if (prefs.contains(name))
		*/
    	try{
    	 SQLiteDatabase myDB = MainActivity.myDB;
    //	 Cursor c = myDB.rawQuery("SELECT * FROM " + "Groups where Field1='"+group+"'" , null);
    	 Cursor c = myDB.rawQuery("SELECT * FROM " + "Groups ;" , null);
    	 if(c==null)
    		 return false;
    	 int Column1 = c.getColumnIndex("Field1");
		 int Column2 = c.getColumnIndex("Field2");
		 c.moveToFirst();
		   if (c != null) {
		    // Loop through all Results
		    do {
		     String Name = c.getString(Column1);
		     String Phone = c.getString(Column2);
		     String Data = Name+" : "+Phone+"";
		     if(Phone.equals(name)){
		    	 for(String group : MainActivity.grp)
		    	 {
		    		 if(Name.equals(group))
		    			 return true;
		    	 }
		     }
		     Log.e("Stored", Data);
		    }while(c.moveToNext());
		   }	
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return false;
    	}
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
        if(isPresentUser(user.toLowerCase()))
        {
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
        	MainActivity.mHandler.post(new Runnable(){
			    public void run(){
			        //Be sure to pass your Activity class, not the Thread
			    	Toast.makeText(context, user+" granted permission automatically as he is included in your group", Toast.LENGTH_SHORT).show();
			        //... setup dialog and show
			    }
			});	
        	return;
        }
   		else{
   			Log.e(user, "Denied");
        				try {        		
							out.close();
					        in.close();
							socket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
   		}
    	}
	   	catch(Exception e){
    		e.printStackTrace();
    	//	Toast.makeText(context, "Error in server write", Toast.LENGTH_SHORT).show();
    	}    
    }
}
