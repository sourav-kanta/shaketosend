package com.example.connectutils;

import com.example.shaketosend.ApManager;
import com.example.shaketosend.WManager;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class SendHandler extends AsyncTask<Void, Void, Void>
{
	Context context=null;
	public void getContext(Context c)
	{
		context=c;
	}
	
	@Override
    protected void onPreExecute() {
        super.onPreExecute();        
       }
	
	 @Override
     protected Void doInBackground(Void... arg0) {
		 if(ApManager.isApOn(context))
				ApManager.ApStateOff(context);		
			WManager.enableWifi(context);
		 return null;
	 }
	 protected void onPostExecute(Void result) {
         super.onPostExecute(result);
      //   Toast.makeText(context, "Done ", Toast.LENGTH_LONG).show();
         WManager.connectToServer(context);
	 }          
}