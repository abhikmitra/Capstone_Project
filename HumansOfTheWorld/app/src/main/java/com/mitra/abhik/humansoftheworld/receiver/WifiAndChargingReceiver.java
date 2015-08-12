package com.mitra.abhik.humansoftheworld.receiver;

import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;

import com.mitra.abhik.humansoftheworld.data.PostsContract;
import com.mitra.abhik.humansoftheworld.Utility;

/**
 * Created by abmitra on 7/31/2015.
 */
public class WifiAndChargingReceiver extends BroadcastReceiver {
    private String Tag = WifiAndChargingReceiver.class.getName();
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conMngr = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = conMngr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifi = wifi != null && wifi.isConnectedOrConnecting();
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        Log.d(Tag,"Battery Status: is charging "+ String.valueOf(isCharging));
        Log.d(Tag,"Network Status: is wifi "+ String.valueOf(isWifi));
        if(isWifi){
            Account mAccount=  Utility.CreateSyncAccount(context);
            Bundle settingsBundle = new Bundle();
            settingsBundle.putBoolean(
                    ContentResolver.SYNC_EXTRAS_MANUAL, true);
            settingsBundle.putBoolean(
                    ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            Log.d(Tag, "Requesting sync ");
            ContentResolver.requestSync(mAccount, PostsContract.CONTENT_AUTHORITY, settingsBundle);
        }
    }
}
