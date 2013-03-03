package com.example.testvoicerecognition;

import static com.example.testvoicerecognition.VoiceController.ACTION_VOICECONTROLLER_ERROR;
import static com.example.testvoicerecognition.VoiceController.ACTION_VOICECONTROLLER_PROCESSING;
import static com.example.testvoicerecognition.VoiceController.ACTION_VOICECONTROLLER_READY;
import static com.example.testvoicerecognition.VoiceController.ACTION_VOICECONTROLLER_SHOW_RESULT;
import static com.example.testvoicerecognition.VoiceController.EXTRAS_VOICECONTROLLER_VALUE;
import static com.example.testvoicerecognition.App.*;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemSelectedListener {
    private List<String>         mResults = new ArrayList<String>();
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        initViews();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d( TAG, "In Activty onResume" );
        if( stopService( new Intent( this, TrayService.class ) ) ) {
            App.getInstance().getVoiceController().destory();
        }
        App.getInstance().getVoiceController().create();
        App.getInstance().getVoiceController().command();
        bindVoiceEvents();
    }

    private void bindVoiceEvents() {
        IntentFilter ift = new IntentFilter();
        ift.addAction( ACTION_VOICECONTROLLER_READY );
        ift.addAction( ACTION_VOICECONTROLLER_PROCESSING );
        ift.addAction( ACTION_VOICECONTROLLER_ERROR );
        ift.addAction( ACTION_VOICECONTROLLER_SHOW_RESULT );
        registerReceiver( mReceiver, ift );
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindVoiceEvents();
    }

    private void unbindVoiceEvents() {
        unregisterReceiver( mReceiver );
    }

    private void initViews() {
        ListView resultsList = (ListView) findViewById( R.id.lv_results );
        mAdapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, mResults );
        resultsList.setAdapter( mAdapter );

        Spinner spinner = (Spinner) findViewById( R.id.spinner_language_list );
        spinner.setOnItemSelectedListener( this );
    }

    private String getLangCode( int _i ) {
        return App.getInstance().getStringInArray( R.array.voice_langauge_list_code, _i );
    }

    private String getLangName( int _i ) {
        return App.getInstance().getStringInArray( R.array.voice_langauge_list, _i );
    }

    @Override
    public void onItemSelected( AdapterView<?> _arg0, View _v, int _index, long _arg3 ) {
        App.getInstance().setLanguage( getLangCode( _index ) );
        Toast.makeText( getApplicationContext(), getLangName( _index ) + "-" + App.getInstance().getLanguage(), Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onNothingSelected( AdapterView<?> _arg0 ) {

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
                                            public void onReceive( android.content.Context _context, Intent _intent ) {
                                                handleIntent( _intent );
                                            };
                                        };

    private void handleIntent( Intent _intent ) {
        if( TextUtils.equals( ACTION_VOICECONTROLLER_READY, _intent.getAction() ) ) {
            TextView console = (TextView) findViewById( R.id.tv_console );
            console.setText( getAction( 0 ) );
            findViewById( R.id.pb_searching ).setVisibility( View.INVISIBLE );
        } else if( TextUtils.equals( ACTION_VOICECONTROLLER_PROCESSING, _intent.getAction() ) ) {
            findViewById( R.id.pb_searching ).setVisibility( View.VISIBLE );
            ((TextView) findViewById( R.id.tv_console )).setText( getAction( 1 ) );
        } else if( TextUtils.equals( ACTION_VOICECONTROLLER_ERROR, _intent.getAction() ) ) {
            ((TextView) findViewById( R.id.tv_console )).setText( _intent.getStringExtra( EXTRAS_VOICECONTROLLER_VALUE ) );
            findViewById( R.id.pb_searching ).setVisibility( View.INVISIBLE );
            App.getInstance().getVoiceController().command();
        } else if( TextUtils.equals( ACTION_VOICECONTROLLER_SHOW_RESULT, _intent.getAction() ) ) {
            findViewById( R.id.pb_searching ).setVisibility( View.INVISIBLE );
            String res = _intent.getStringExtra( EXTRAS_VOICECONTROLLER_VALUE );
            addToListView( res );
            handleCommand( res );
            App.getInstance().getVoiceController().command();
        }

        // else if( TextUtils.equals( ACTION_VOICECONTROLLER_NO_MATCH, _intent.getAction() ) || TextUtils.equals( ACTION_VOICECONTROLLER_NET_ERROR, _intent.getAction() ) || TextUtils.equals( ACTION_VOICECONTROLLER_SERVER_PROBLEM, _intent.getAction() ) ) {
        // String res = _intent.getStringExtra( EXTRAS_VOICECONTROLLER_VALUE );
        // TextView console = (TextView) findViewById( R.id.tv_console );
        // console.setText( res );
        // View v = findViewById( R.id.pb_searching );
        // v.setVisibility( View.INVISIBLE );
        // bindVoiceEvents();
        // }
    }

    private void handleCommand( String res ) {
        if( !TextUtils.isEmpty( res ) ) {
            if( res.equalsIgnoreCase( "google" ) ) {
                openUrl( this, "http://www.google.com" );
            } else if( res.toLowerCase().contains( "bye bye" ) ) {
                finish();
            }
        }
    }

    private void addToListView( String res ) {
        if( !TextUtils.isEmpty( res ) ) {
            mResults.add( res );
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d( TAG, "In Activity onDestroy" );
        App.getInstance().getVoiceController().destory();
        startService( new Intent( this, TrayService.class ) );
    }

    private String getAction( int _i ) {
        return App.getInstance().getStringInArray( R.array.voice_actions, _i );
    }

    private static void openUrl( Context _context, String _to ) {
        if( _context != null ) {
            Intent i = new Intent( Intent.ACTION_VIEW );
            i.setData( Uri.parse( _to ) );
            _context.startActivity( i );
        }
    }

}