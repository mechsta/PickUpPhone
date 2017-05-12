package com.mechsta.pleasepickupthephone;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

public class IncomingCallReceiver extends BroadcastReceiver {
    private static final String TAG = "IncomingCallReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                return;
            }
            String callNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            String number = PhoneNumberUtils.formatNumber(callNumber, Locale.getDefault().getCountry());
            number = number.replaceAll("-","");

            ArrayList<String> numList = Utils.loadCallNumber(context);
            for (String num : numList) {
                if (number.equals(num)) {
                    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    //audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

                    int volume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, volume, 0);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

                    break;
                }
            }
        }
    }
}
