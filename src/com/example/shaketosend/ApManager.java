 package com.example.shaketosend;

import android.content.*;
import android.net.wifi.*;
import java.lang.reflect.*;

public class ApManager {

//check whether wifi hotspot on or off
public static boolean isApOn(Context context) {
    WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);     
    try {
        Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
        method.setAccessible(true);
        return (Boolean) method.invoke(wifimanager);
    }
    catch (Throwable ignored) {}
    return false;
}

public static WifiConfiguration setHotspotName(String newName, Context context) { 
    try {
    	
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled()) {               
            wifiManager.setWifiEnabled(false);
        } 
  //      Method getConfigMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
  //      WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiManager);
        WifiConfiguration wifiConfig=new WifiConfiguration();
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wifiConfig.SSID = newName;

   //     Method setConfigMethod = wifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
   //     setConfigMethod.invoke(wifiManager, wifiConfig);

        return wifiConfig;
    }
    catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}

// toggle wifi hotspot on or off
public static boolean ApStateOn(Context context,WifiConfiguration hcon) {
    WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
    WifiConfiguration wificonfiguration = hcon;
    try {  
        // if WiFi is on, turn it off
        if(wifimanager.isWifiEnabled()) {               
            wifimanager.setWifiEnabled(false);
        }               
        Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);                   
        method.invoke(wifimanager, wificonfiguration, true);
        return true;
    } 
    catch (Exception e) {
        e.printStackTrace();
    }       
    return false;
}

public static boolean ApStateOff(Context context) {
    WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
    WifiConfiguration wificonfiguration = null;
    try {              
        Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);                   
        method.invoke(wifimanager, wificonfiguration, false);
        return true;
    } 
    catch (Exception e) {
        e.printStackTrace();
    }       
    return false;
}

} // end of class