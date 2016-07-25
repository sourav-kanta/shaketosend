package com.example.shaketosend;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class Transmit extends Fragment{

	public Transmit()
	{
		//  empty constructor
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
		View vfrag=inflater.inflate(R.layout.trasmisiion,container,false);
		MainActivity.vfrag=vfrag;
		ImageView img=(ImageView) vfrag.findViewById(R.id.imageView1);
		RotateAnimation rotate = new RotateAnimation(0, 360,
		        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
		        0.5f);

		rotate.setDuration(4000);
		rotate.setRepeatCount(Animation.INFINITE);
		img.setAnimation(rotate);
		return vfrag;		
	}
	
}
