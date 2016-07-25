package com.example.shaketosend;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProgressPage extends Fragment{

	public ProgressPage()
	{
		//  empty constructor
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		View vfrag=inflater.inflate(R.layout.transfrag,container,false);
		MainActivity.vfrag=vfrag;
		return vfrag;
	}

}
