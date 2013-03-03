package com.example.testvoicerecognition;

import static com.example.testvoicerecognition.App.*;
import static com.example.testvoicerecognition.VoiceController.*;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

public final class TrayService extends Service {
    public static final String ACTION_STOP_FOREGROUND = "com.example.testvoicerecognition.STOP.TrayService";

    private static final int   LOW_VERSION            = -1;
    public static final int    ID_TRAY_NOTIFICATION   = 0x999990;

    @Override
    public void onCreate() {
    }

    /** some versions of android callback this **/
    @Override
    public void onStart( Intent intent, int startId ) {
        onStart( intent, LOW_VERSION, startId );
    }

    /** some versions of android callback this **/
    @Override
    public int onStartCommand( Intent intent, int flags, int startId ) {
        onStart( intent, flags, startId );
        return START_STICKY;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        stopForeground( true );
        stopSelf();
    }

    @Override
    public IBinder onBind( Intent _arg0 ) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d( TAG, "In Service onDestroy" );
        unbindVoiceEvents();
        stopForeground( true );
    }

    private void onStart( Intent intent, int flags, int startId ) {
        startNotification();
        Log.d( TAG, "In Service onStart" );
        App.getInstance().getVoiceController().create();
        App.getInstance().getVoiceController().command();
        bindVoiceEvents();
    }

    private void startNotification() {
        PendingIntent pendingIntent = PendingIntent.getActivity( this, 0, openMainActivity(), PendingIntent.FLAG_UPDATE_CURRENT );
        Notification notification = new NotificationCompat.Builder( getApplicationContext() )
                .setWhen( System.currentTimeMillis() )
                .setTicker( getString( R.string.voice_start_tray_title ) )
                .setContentText( getString( R.string.voice_start_tray_title ) )
                .setContentIntent( pendingIntent )
                .setSmallIcon( R.drawable.ic_launcher )
                .build();
        startForeground( ID_TRAY_NOTIFICATION, notification );
    }

    private void bindVoiceEvents() {
        IntentFilter ift = new IntentFilter();
        ift.addAction( ACTION_VOICECONTROLLER_READY );
        ift.addAction( ACTION_VOICECONTROLLER_PROCESSING );
        ift.addAction( ACTION_VOICECONTROLLER_ERROR );
        ift.addAction( ACTION_VOICECONTROLLER_SHOW_RESULT );
        registerReceiver( mReceiver, ift );
    }

    private void unbindVoiceEvents() {
        unregisterReceiver( mReceiver );
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
                                            public void onReceive( android.content.Context _context, Intent _intent ) {
                                                handleIntent( _intent );
                                            };
                                        };

    private void handleIntent( Intent _intent ) {
        if( TextUtils.equals( ACTION_VOICECONTROLLER_READY, _intent.getAction() ) ) {
        } else if( TextUtils.equals( ACTION_VOICECONTROLLER_PROCESSING, _intent.getAction() ) ) {
        } else if( TextUtils.equals( ACTION_VOICECONTROLLER_ERROR, _intent.getAction() ) ) {
            App.getInstance().getVoiceController().command();
        } else if( TextUtils.equals( ACTION_VOICECONTROLLER_SHOW_RESULT, _intent.getAction() ) ) {
            handleCommand( _intent.getStringExtra( EXTRAS_VOICECONTROLLER_VALUE ) );
            App.getInstance().getVoiceController().command();
        }

        // else if( TextUtils.equals( ACTION_VOICECONTROLLER_NO_MATCH, _intent.getAction() ) || TextUtils.equals( ACTION_VOICECONTROLLER_NET_ERROR, _intent.getAction() ) || TextUtils.equals( ACTION_VOICECONTROLLER_SERVER_PROBLEM, _intent.getAction() ) ) {
        // bindVoiceEvents();
        // }
    }

    private void handleCommand( String res ) {
        if( !TextUtils.isEmpty( res ) ) {
            if( res.toLowerCase().contains( "open the door" ) ) {
                Log.i( TAG, "Service gets info." );
                startActivity( openMainActivity() );
            }
        }
    }

    private Intent openMainActivity() {
        Intent intentNotify = new Intent( this, MainActivity.class );
        intentNotify.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
        return intentNotify;
    }

}