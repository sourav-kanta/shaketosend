package com.example.shaketosend;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Creategroup extends Fragment{

	

	public Creategroup()
	{
		//  empty constructor
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		View vfrag=inflater.inflate(R.layout.group,container,false);
		MainActivity.vfrag=vfrag;
		return vfrag;		
	}

	
}
