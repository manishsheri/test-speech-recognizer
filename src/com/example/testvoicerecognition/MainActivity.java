package com.example.testvoicerecognition;

import static android.speech.RecognizerIntent.EXTRA_PARTIAL_RESULTS;
import static android.speech.SpeechRecognizer.ERROR_AUDIO;
import static android.speech.SpeechRecognizer.ERROR_CLIENT;
import static android.speech.SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS;
import static android.speech.SpeechRecognizer.ERROR_NETWORK;
import static android.speech.SpeechRecognizer.ERROR_NETWORK_TIMEOUT;
import static android.speech.SpeechRecognizer.ERROR_NO_MATCH;
import static android.speech.SpeechRecognizer.ERROR_RECOGNIZER_BUSY;
import static android.speech.SpeechRecognizer.ERROR_SERVER;
import static android.speech.SpeechRecognizer.ERROR_SPEECH_TIMEOUT;
import static android.speech.SpeechRecognizer.RESULTS_RECOGNITION;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

public class MainActivity extends Activity implements RecognitionListener {
    private static final String  TAG         = "TestVoiceRecognizer";
    private static final int     MAX_RESULTS = 1;
    private SpeechRecognizer     mSpeechRecognizer;
    private Spinner              mLanguages;
    private Button               mCommand;
    private List<String>         mResults    = new ArrayList<String>();
    private ArrayAdapter<String> mAdapter;                              ;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        initVoiceRecognizer();
        initViews();
    }

    private void initViews() {
        mLanguages = (Spinner) findViewById( R.id.spinner_language_list );
        mCommand = (Button) findViewById( R.id.btn_command );
        ListView resultsList = (ListView) findViewById( R.id.lv_results );
        mAdapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, mResults );
        resultsList.setAdapter( mAdapter );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate( R.menu.main, menu );
        return true;
    }

    private void initVoiceRecognizer() {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer( getApplicationContext() );
        mSpeechRecognizer.setRecognitionListener( this );
    }

    public void onCommand( View _v ) {
        if( mSpeechRecognizer != null ) {
            mSpeechRecognizer.stopListening();
            Intent intent = createCommand();
            mSpeechRecognizer.startListening( intent );
        }
    }

    private Intent createCommand() {
        Intent intent = new Intent( RecognizerIntent.ACTION_RECOGNIZE_SPEECH );
        intent.putExtra( RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM );
        intent.putExtra( RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName() );
        intent.putExtra( RecognizerIntent.EXTRA_MAX_RESULTS, MAX_RESULTS );
        intent.putExtra( RecognizerIntent.EXTRA_LANGUAGE, getLangCode( mLanguages.getSelectedItemPosition() ) );
        return intent;
    }

    public void onCancel( View _v ) {
        if( mSpeechRecognizer != null ) {
            mSpeechRecognizer.stopListening();
        }
        enableCommand();
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i( TAG, "onBeginningOfSpeech" );
        mCommand.setText( getAction( 1 ) );
    }

    @Override
    public void onBufferReceived( byte[] _buffer ) {

        Log.i( TAG, "onBufferReceived: " + new String( _buffer ) );

    }

    @Override
    public void onEndOfSpeech() {
        Log.i( TAG, "onEndOfSpeech" );
        mCommand.setText( getAction( 2 ) );
    }

    @Override
    public void onError( int _error ) {
        printError( _error );
    }

    @Override
    public void onEvent( int _eventType, Bundle _params ) {
        Log.i( TAG, "onEvent" );
    }

    @Override
    public void onPartialResults( Bundle _partialResults ) {
        printResult( "onPartialResults", _partialResults.getStringArrayList( EXTRA_PARTIAL_RESULTS ) );
    }

    @Override
    public void onReadyForSpeech( Bundle _params ) {
        Log.i( TAG, "onReadyForSpeech" );
        disableCommand();
        mCommand.setText( getAction( 0 ) );
    }

    @Override
    public void onResults( Bundle _results ) {
        printResult( "onResults", _results.getStringArrayList( RESULTS_RECOGNITION ) );
        mCommand.setText( getAction( 3 ) );
        enableCommand();
    }

    @Override
    public void onRmsChanged( float _rmsdB ) {
        // Log.i( TAG, "onRmsChanged" );
    }

    private void enableCommand() {
        mCommand.setEnabled( true );
        mCommand.setText( getString( R.string.voice_start_speaking ) );
    }

    private void disableCommand() {
        mCommand.setEnabled( false );
    }

    private static void printError( int _error ) {
        switch( _error )
        {
            case ERROR_AUDIO:
                Log.i( TAG, "onError: Audio recording error." );
            break;
            case ERROR_CLIENT:
                Log.i( TAG, "onError: Other client side errors." );
            break;
            case ERROR_INSUFFICIENT_PERMISSIONS:
                Log.i( TAG, "onError:   Insufficient permissions." );
            break;
            case ERROR_NETWORK:
                Log.i( TAG, "onError:  Other network related errors." );
            break;
            case ERROR_NETWORK_TIMEOUT:
                Log.i( TAG, "onError:   Network operation timed out." );
            break;
            case ERROR_NO_MATCH:
                Log.i( TAG, "onError:  No recognition result matched." );
            break;
            case ERROR_RECOGNIZER_BUSY:
                Log.i( TAG, "onError:   RecognitionService busy." );
            break;
            case ERROR_SERVER:
                Log.i( TAG, "onError:  Server sends error status." );
            break;
            case ERROR_SPEECH_TIMEOUT:
                Log.i( TAG, "onError:   No speech input." );
            break;
            default:
                Log.i( TAG, "Unknown" );

        }
    }

    private void printResult( String _title, List<String> _results ) {
        if( _results != null ) {
            int sz = _results.size();
            if( sz >= 1 ) {
                String word = _results.get( 0 );
                mResults.add( word );
                mAdapter.notifyDataSetChanged();
                if( word.equalsIgnoreCase( "google" ) ) {
                    openUrl( this, "http://www.google.com" );
                }
            }
        }
    }

    private String getLangCode( int _i ) {
        return getStringInArray( R.array.voice_langauge_list_code, _i );
    }

    private String getAction( int _i ) {
        return getStringInArray( R.array.voice_actions, _i );
    }

    private String getStringInArray( int _resID, int _index ) {
        return (getResources().getStringArray( _resID ))[_index];
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposeVoiceRecognizer();
    }

    private void disposeVoiceRecognizer() {
        if( mSpeechRecognizer != null ) {
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer = null;
        }
    }

    private static void openUrl( Context _context, String _to ) {
        if( _context != null ) {
            Intent i = new Intent( Intent.ACTION_VIEW );
            i.setData( Uri.parse( _to ) );
            _context.startActivity( i );
        }
    }
}