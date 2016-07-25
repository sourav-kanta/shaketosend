package com.example.shaketosend;

import java.util.List;

import com.example.connectutils.startConnect;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class WManager {

	public static boolean isWifiOn(Context context)
	{
		WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
		if(wifimanager.isWifiEnabled()) {               
            return true;
        } 
		else
			return false;
	}
	
	public static void enableWifi(Context context)
	{
		if(isWifiOn(context))
			return;
		else
		{
			WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			wifiManager.setWifiEnabled(true);
		}
	}
	
	public static void disableWifi(Context context)
	{
		if(isWifiOn(context))
		{
			WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			wifiManager.setWifiEnabled(false);
		}
	}
	
	
	
	public static boolean connectToServer(Context context)
	{
		WifiConfiguration conf = new WifiConfiguration();		
		conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);		
		WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE); 	
		wifiManager.disconnect();
		List<ScanResult> list =  wifiManager.getScanResults();
		if(list==null)
			return false;
		for( ScanResult i : list ) {
			//Toast.makeText(context, i.SSID, Toast.LENGTH_SHORT).show();
		    if(i.SSID != null && (i.SSID.startsWith("SMSKCM877-") && i.SSID.endsWith("-MSDBP2016"))) {
		    	// Toast.makeText(context, "Found Match", Toast.LENGTH_SHORT);
		    	 conf.SSID="\""+i.SSID+"\"";
		    	 conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		    	 wifiManager.addNetwork(conf);
		    	 int networkID=wifiManager.addNetwork(conf);
		         wifiManager.disconnect();		         
		         wifiManager.enableNetwork(networkID, true);
		         wifiManager.reconnect();    
		         String ipStr="";
		  /*       DhcpInfo sinfo=wifiManager.getDhcpInfo();
		         int ip=sinfo.serverAddress;
		         ipStr =  String.format("%d.%d.%d.%d",
		        		         (ip & 0xff),   
		        		         (ip >> 8 & 0xff),             
		        		         (ip >> 16 & 0xff),    
		        		         (ip >> 24 & 0xff));
		         Log.e("sg", ipStr);	
		         
		         	         		         */
		         ipStr="192.168.43.1";
		        // Toast.makeText(context, ipStr+"", Toast.LENGTH_LONG).show();	         
		         startConnect s=new startConnect(ipStr,context);
		         s.execute(); 
		         return true;
		    }           
		 }
		return false;
	}
	
}
