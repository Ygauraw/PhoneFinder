package com.mzar.phonefinder;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.widget.Toast;

/**
 * Created by Mattin on 1/1/2015.
 */
public class TextManager extends Service
{
    private static final String KEY = "Ring!!"; //TEMPORARY!!

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private int prevVolume;
    private boolean isRinging = false;

    private final BroadcastReceiver textReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String intentAction = intent.getAction();
            if(intentAction.equals("android.provider.Telephony.SMS_RECEIVED"))
            {
                Object[] pdus = (Object[]) intent.getExtras().get("pdus");
                SmsMessage incomingSMS = SmsMessage.createFromPdu((byte[]) pdus[0]); //This may or may not get the entire text. Look into it further.
                String sender = incomingSMS.getOriginatingAddress();
                String message = incomingSMS.getMessageBody();

                if (message.equals(KEY)) {
                    //Stores the current ringer volume, then sets the ringer volume to the device's max.
                    audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    prevVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
                    audioManager.setStreamVolume(AudioManager.STREAM_RING,
                            audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),
                            AudioManager.FLAG_ALLOW_RINGER_MODES);

                    //Determines the device's ringtone, then plays it on loop.
                    Uri ringtoneURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), ringtoneURI);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                    isRinging = true;
                }
            }
            else if(intentAction.equals(MainActivity.INTENT_STOP_RINGING))
            {
                if(isRinging)
                {
                    mediaPlayer.stop();
                    audioManager.setStreamVolume(AudioManager.STREAM_RING,
                            prevVolume, AudioManager.FLAG_ALLOW_RINGER_MODES);
                    isRinging = false;

                    Toast.makeText(context, "Stopping.", Toast.LENGTH_SHORT).show();
                }
            }
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
        filter.addAction(MainActivity.INTENT_STOP_RINGING);
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
