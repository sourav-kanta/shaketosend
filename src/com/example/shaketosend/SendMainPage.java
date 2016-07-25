package com.example.shaketosend;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SendMainPage extends Fragment{
	
	public void shownameselect() {
	    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.context);
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    final View dialogView = inflater.inflate(R.layout.uni_name, null);
	    dialogBuilder.setView(dialogView);

	    final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
	    dialogBuilder.setTitle("Select Receiver");
	    dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	            //do something with edt.getText().toString();
	        	String alusr=edt.getText().toString();
	        	MainActivity.Uniuser=alusr;
	        	Log.e("Allowed", alusr);
	        }
	    });	    
	    AlertDialog b = dialogBuilder.create();
	    b.show();
	}
	
	public void showgroupselect()
	{
		AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.context);
		SQLiteDatabase myDB = MainActivity.myDB;
        final Cursor c2 = myDB.rawQuery("SELECT distinct Field1 FROM " + "Groups;" , null);		   
		   int Column = c2.getColumnIndex("Field1");
		   final String s[]=new String[c2.getCount()];
		   final boolean add[]=new boolean[c2.getCount()];
		   Arrays.fill(add, Boolean.FALSE);
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
     	build.setMultiChoiceItems(s, null, new DialogInterface.OnMultiChoiceClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					// TODO Auto-generated method stub
					if(add[which]==false)
						add[which]=true;
					else
						add[which]=true;
				//	Log.e("Selected", add[which].);
				}
			});
     	build.setTitle("Choose Group");
     	build.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					for(int i=0;i<c2.getCount();i++)
					{
						if(add[i]==true){
							MainActivity.grp.add(s[i]);
							Log.e("Added", s[i]);
						}
					}					
				}
			});
     	build.setCancelable(false);
     	AlertDialog dialog = build.create();
         // Display the alert dialog on interface
         dialog.show();
	}
	
	public SendMainPage()
	{
		//  empty constructor
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		View vfrag=inflater.inflate(R.layout.sendchoice,container,false);
		MainActivity.vfrag=vfrag;
		RadioGroup rg = (RadioGroup) vfrag.findViewById(R.id.radioGroup1);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch(checkedId)
                {               
                case R.id.radio2:
                    // TODO Something
                	Log.e("Clicked", "Unicast");
                	shownameselect();
                    break;
                
                case R.id.radio1:
                	Log.e("Clicked", "MultiCast");
                	MainActivity.grp=new ArrayList<String>();
                	try{
                		showgroupselect();
                	}catch(Exception e)
                	{
                		e.printStackTrace();
                		Toast.makeText(getActivity(), "No groups created", Toast.LENGTH_SHORT).show();;
                	}
                }	
            }
        });
		return vfrag;		
	}

}
