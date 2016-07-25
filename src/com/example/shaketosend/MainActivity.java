package com.example.shaketosend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.connectutils.SendHandler;
import com.example.connectutils.ServerMulticastThread;
import com.example.connectutils.ServerThread;
import com.example.connectutils.ServerUniThread;
import com.example.shaketosend.ApManager;
import com.example.shaketosend.MainActivity;
import com.example.shaketosend.Misc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	public static boolean isConnected=false;
	public static Context context;
	public static boolean isFired=false;
	public static volatile int prog=0;
	public static View vfrag;
	public static int BROADCAST=1;
	public static int MULTICAST=2;
	public static int UNICAST=3;
	public static int MODE;
	public static Handler mHandler;
	public static String user;
	public static String Uniuser;
	public static SQLiteDatabase myDB;
	public static ArrayList<String> grp=new ArrayList<String>();
	
	private SensorManager mSensorManager;
	  private float mAccel; // acceleration apart from gravity
	  private float mAccelCurrent; // current acceleration including gravity
	  private float mAccelLast; // last acceleration including gravity
	  
	  
	  public String[] getGroups() throws Exception
	  {
		  
		   Cursor c2 = myDB.rawQuery("SELECT distinct Field1 FROM " + "Groups;" , null);		   
		   int Column = c2.getColumnIndex("Field1");
		   String s[]=new String[Column];
		   c2.moveToFirst();
		   int i=0;
		   if (c2 != null) {
			    // Loop through all Results
			    do {
			     String Name = c2.getString(Column);
			     s[i++]=Name;
			     Log.e("Stored groups", Name);
			    }while(c2.moveToNext());
			   }
		   return s;
	  }
	  

	  private final SensorEventListener mSensorListener = new SensorEventListener() {

	    public void onSensorChanged(SensorEvent se) {
	      float x = se.values[0];
	      float y = se.values[1];
	      float z = se.values[2];
	      mAccelLast = mAccelCurrent;
	      mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
	      float delta = mAccelCurrent - mAccelLast;
	      mAccel = mAccel * 0.9f + delta; // perform low-cut filter
	      if (mAccel > 12) {
		        Toast toast = Toast.makeText(getApplicationContext(), "Device has been shaken.", Toast.LENGTH_LONG);
		        toast.show();
		        sendMode(null);
		    }
	    }

	    public void onAccuracyChanged(Sensor sensor, int accuracy) {
	    }
	  };
	
	public static void setProgressPercent(int i)
	{
		 TextView t=(TextView) vfrag.findViewById(R.id.progset);
		 t.setText(i+"%");
	}
	
	public static void setSpeed(double i)
	{
		 TextView t=(TextView) vfrag.findViewById(R.id.speed);
		 DecimalFormat df = new DecimalFormat();
		 df.setMaximumFractionDigits(2);		 
		 String s=df.format(i);
		 t.setText("Speed : "+s+" KBps");
	}
	
	public void selectFile()
	{
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("file/*");
		startActivityForResult(intent, 1);
	}
	
	
	public void openFolder(View v)
	{
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse(Environment.getExternalStorageDirectory().getPath()), "*/*");
		if (intent.resolveActivityInfo(getPackageManager(), 0) != null)
        {
            startActivity(intent);
        }
		else 
        {
          Toast.makeText(this, "Your file manager doesnt support this functionality.", Toast.LENGTH_SHORT).show();
        }
	
	}
	
	private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context c, Intent intent) {
	        if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) && isConnected==false && isFired==true) {
	        	SendHandler s=new SendHandler();
	    		s.getContext(MainActivity.this);
	    		s.execute();	    		
	            // add your logic here
	        }
	    }
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		
		if (resultCode == Activity.RESULT_CANCELED || data==null ) {
            //Write your code if there's no result
        	Toast.makeText(MainActivity.this, "no result", Toast.LENGTH_SHORT).show();
        	return;
        }
		
		else if (requestCode == 1) {
	        if(resultCode == Activity.RESULT_OK){
	        	Uri currFileURI = data.getData();
                final String path=Misc.getRealPathFromURI(MainActivity.this, currFileURI);
	        //	String path=currFileURI.getPath();
	        	if(path==null) return;
                Toast.makeText(MainActivity.this, path+"", Toast.LENGTH_SHORT).show();                
                if(MODE==BROADCAST){
                	Log.e("MODE", "BROADCAST");
                	ServerThread s=new ServerThread();
                    s.setPath(path,MainActivity.this);
                    s.initServer();
                    s.start();
                }
                
                else if(MainActivity.MODE==MainActivity.MULTICAST)
                {
                   Log.e("MODE", "MULTICAST");
                   ServerMulticastThread s=new ServerMulticastThread();
                   s.setPath(path,MainActivity.context);
                   s.initServer();
                   s.start();
                	
                }
                else if(MainActivity.MODE==MainActivity.UNICAST)
                {
                	Log.e("MODE", "UNICAST");
                	ServerUniThread s=new ServerUniThread();
                    s.setPath(path,MainActivity.this);
                    s.initServer();
                    s.start();
                }
                Fragment fragment = new Transmit();	
        		FragmentManager fragmentManager = getFragmentManager();
        		fragmentManager.beginTransaction()
        		.addToBackStack(null)
                .replace(R.id.mainContent, fragment)
                .commit();
                
	        }
	        
	    } 
	}
	
	

	public void send(View v)
	{
		Fragment fragment = new SendMainPage();	
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
		.addToBackStack(null)
        .replace(R.id.mainContent, fragment)
        .commit();
	}
	
	public void receiveMode(View v)
	{
		Log.w("User selected : ", " Receive ");		
		SendHandler s=new SendHandler();
		s.getContext(MainActivity.this);
		s.execute();
		isFired=true;
		Fragment fragment = new ProgressPage();	
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
		.addToBackStack(null)
        .replace(R.id.mainContent, fragment)
        .commit();
		
	}
	
	public void sendMode(View v)
	{
		Log.w("User selected : ", " Send ");
		WifiConfiguration hots=ApManager.setHotspotName(Misc.getHotName(), MainActivity.this);
		ApManager.ApStateOn(this,hots);	
		RadioGroup r=(RadioGroup) vfrag.findViewById(R.id.radioGroup1);
		int index=0;
		if(r!=null){
			index = r.indexOfChild(findViewById(r.getCheckedRadioButtonId()));
			MODE=index+1;
		}else
			MODE=1;
		Log.e("index", index+"");
		selectFile();
		isFired=false;
	}
	
	public void Adduser(View v)
	{
		EditText t=(EditText) vfrag.findViewById(R.id.editText1);
		String name=t.getText().toString();
		EditText g=(EditText) vfrag.findViewById(R.id.group);
		String group=g.getText().toString();
		if(name.isEmpty() || (!name.contains(""))){
			t.setError("Enter a valid name");
			return;
		}
		if(group.isEmpty() || (!group.contains(""))){
			g.setError("Enter a valid group name");
			return;
		}
	//	SharedPreferences.Editor editor = getSharedPreferences("Preferences", MODE_PRIVATE).edit();
	//	editor.putString(name.toLowerCase(), "allow")
	//	.commit();
	//	Log.e("added", name);
		t.setText("");
		
		
		myDB  = this.openOrCreateDatabase("DatabaseName", MODE_PRIVATE, null);
		myDB.execSQL("CREATE TABLE IF NOT EXISTS "
			     + "Groups"
			     + " (Field1 VARCHAR, Field2 VARCHAR);");
		myDB.execSQL("INSERT INTO "
			     + "Groups"
			     + " (Field1, Field2)"
			     + " VALUES ('"+group+"','"+name+"');");
		 
		Log.e("Added", name);
		   
		   // Check if our result was valid.
		  

	}
	
	public class ExtractApk extends AsyncTask<Void, String, Void>
	{
		File file;
		File dest;
		ArrayList<File> filecpy;
		ArrayList<File> filefin;
		ProgressDialog mProgressDialog;
		
		void store(ArrayList<File> cp)
		{
			filecpy=cp;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			for(File file : filecpy){
				Log.e("Copying", file.getName());
				publishProgress(file.getName());
				InputStream input = null;
				OutputStream output = null;
				try {
					File dest=new File(Environment.getExternalStorageDirectory()+"//APK's//"+file.getName());
					input = new FileInputStream(file);
					output = new FileOutputStream(dest);		    
					byte[] buf = new byte[1024];		   
					int bytesRead;		    
					while ((bytesRead = input.read(buf)) > 0) {   
							output.write(buf, 0, bytesRead);		   
					}		 
					input.close();		    
					output.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}					
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			mProgressDialog.setMessage("Extracting "+values[0]);
		}
		
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mProgressDialog.dismiss();			
			Toast.makeText(MainActivity.this, "Extraction complete. Extracted files are in APK's folder.", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	
	
	public void extractApk(View v)
	{
		Log.e("apk", "called");	
		
		File file;
		final ArrayList<File> filecpy=new ArrayList<File>();
		final ArrayList<File> filefin=new ArrayList<File>();
		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		final List pkgAppsList = getPackageManager().queryIntentActivities(mainIntent, 0);	
		
		
		
		File theDir = new File(Environment.getExternalStorageDirectory()+"//APK's");
		// if the directory does not exist, create it
		if (!theDir.exists()) {
			try{
		        theDir.mkdir();
		    } 
		    catch(SecurityException se){
		        //handle it
		    	Log.e("Folder", "Not Created");
		    }   
		}
		String ap[]=new String[pkgAppsList.size()];
		final boolean add[]=new boolean[pkgAppsList.size()];
		Arrays.fill(add, Boolean.FALSE);
		int i=0;
		for (Object object : pkgAppsList) {
			ResolveInfo info = (ResolveInfo) object;
			file = new File(info.activityInfo.applicationInfo.publicSourceDir);	
		//	Log.e("APKS", file.getName());
			
			PackageManager pm = getPackageManager();
			PackageInfo    pi = pm.getPackageArchiveInfo(file.getAbsolutePath(), 0);
			 pi.applicationInfo.sourceDir       = file.getAbsolutePath();
			 pi.applicationInfo.publicSourceDir = file.getAbsolutePath();
			 String   AppName = (String)pi.applicationInfo.loadLabel(pm);
			 Log.e("APKS", AppName);
			 
			 
			ap[i++]=AppName;
			filefin.add(file);
		}		
		AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.context);
		build.setMultiChoiceItems(ap, null, new DialogInterface.OnMultiChoiceClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				// TODO Auto-generated method stub
				if(add[which]==false)
					add[which]=true;
				else
					add[which]=false;				
			}
		});
 	build.setTitle("Choose Apps");
 	build.setPositiveButton("Extract", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				for(int i=0;i<pkgAppsList.size();i++)
				{
					if(add[i]==true){
						filecpy.add(filefin.get(i));
						Log.e("Copied", filefin.get(i).getName());
					}
				}
				ExtractApk ex= new ExtractApk();
				ex.store(filecpy);
				ex.execute();
			}
		});
 	build.setCancelable(false);
 	AlertDialog dialog = build.create();
     // Display the alert dialog on interface
     dialog.show();
    }
	
	
	
	
	
	public void login(View v)
	{
		EditText t=(EditText) vfrag.findViewById(R.id.editText1);
		String name=t.getText().toString();
		if(name.isEmpty() || (!(name.length()==10))){
			t.setError("Enter a valid phone number");
			return;
		}
		SharedPreferences.Editor editor = getSharedPreferences("Preferences", MODE_PRIVATE).edit();
		editor.putString("name", name)
		.commit();
		user=name;
		Fragment fragment = new Home();	
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
		.addToBackStack(null)
        .replace(R.id.mainContent, fragment)
        .commit();


	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));  
		getActionBar().setDisplayHomeAsUpEnabled(true);
		myDB  = this.openOrCreateDatabase("DatabaseName", MODE_PRIVATE, null);
		context=MainActivity.this;
		setContentView(R.layout.main_page);
		registerReceiver(mWifiScanReceiver,
		        new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		
		SharedPreferences prefs = getSharedPreferences("Preferences", MODE_PRIVATE); 
		if (!prefs.contains("name")) {
			Fragment fragment = new Login();	
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()			
	        .replace(R.id.mainContent, fragment)
	        .commit();		  
		}
		else
		{
			String s=prefs.getString("name", null);
			if(s!=null)
				Log.e("User",s);
			user=s;
			Fragment fragment = new Home();	
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
			.addToBackStack(null)
			.replace(R.id.mainContent, fragment)
			.commit();
		}
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	    mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
	    mAccel = 0.00f;
	    mAccelCurrent = SensorManager.GRAVITY_EARTH;
	    mAccelLast = SensorManager.GRAVITY_EARTH;
	    mHandler=new Handler(Looper.getMainLooper());
	    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		if(id == android.R.id.home)
		{
			onBackPressed();
		}
		if  (id == R.id.action_group)
		{
			Fragment fragment = new Creategroup();	
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()			
	        .replace(R.id.mainContent, fragment)
	        .addToBackStack(null)
	        .commit();
			return true;
		}
		if(id==R.id.reset_group)
		{
			SharedPreferences prefs = getSharedPreferences("Preferences", MODE_PRIVATE); 
			Editor editor = prefs.edit();
			String name=prefs.getString("name", null);
			editor.clear();
			editor.putString("name", name);
			editor.commit();
			
			
			myDB  = this.openOrCreateDatabase("DatabaseName", MODE_PRIVATE, null);
			try{
				myDB.execSQL("Delete from Groups;");	
				Toast.makeText(this, "All groups successfully reset.", Toast.LENGTH_SHORT).show();
			}catch(Exception e)
			{
				Toast.makeText(this, "No groups present", Toast.LENGTH_SHORT).show();
			}			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		int fragcount=getFragmentManager().getBackStackEntryCount();
		if(fragcount==0){
			if(ApManager.isApOn(this))
				ApManager.ApStateOff(this);
			finish();			
		}
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		unregisterReceiver(mWifiScanReceiver);	
		mSensorManager.unregisterListener(mSensorListener);
		super.onStop();		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		context=MainActivity.this;
		registerReceiver(mWifiScanReceiver,
		        new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
	}

	public static void enableButton() {
		// TODO Auto-generated method stub
		Button btn=(Button) vfrag.findViewById(R.id.fold);
		btn.setClickable(true);
		btn.setEnabled(true);
	}
}
