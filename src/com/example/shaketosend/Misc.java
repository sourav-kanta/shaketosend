package com.example.shaketosend;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

public class Misc {

	public static String getHotName()
	{
		String name="SMSKCM877-"+((int)(Math.random() * ((99999) + 1)))+"-MSDBP2016";
		return name;
	}
	
	public static String getRealPathFromURI(Context context, Uri contentUri) {
		  Cursor cursor = null;
		  try { 
		    String[] proj = { MediaStore.Images.Media.DATA };
		    cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
		    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		    cursor.moveToFirst();
		    return cursor.getString(column_index);
		  } 
		  catch(Exception e)
		  {
			  Toast.makeText(context,contentUri.getPath(), Toast.LENGTH_SHORT).show();;
			  return contentUri.getPath();  
		  }		  
		  finally {
		    if (cursor != null) {
		      cursor.close();
		    }
		  }
		}
}
