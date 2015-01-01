package com.mzar.phonefinder;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.widget.Toast;

/**
 * Created by Mattin on 1/1/2015.
 */
public class TextManager extends Service
{
    private final BroadcastReceiver textReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            SmsMessage incomingSMS = SmsMessage.createFromPdu((byte[])pdus[0]);
            String sender = incomingSMS.getOriginatingAddress();
            String message = incomingSMS.getMessageBody();

            Toast.makeText(context, sender+": "+message, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(textReceiver, filter);

        Toast.makeText(this, "Phone Finder Activated.", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        unregisterReceiver(textReceiver);

        Toast.makeText(this, "Phone Finder Deactivated.", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}
