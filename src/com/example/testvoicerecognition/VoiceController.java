package com.example.testvoicerecognition;

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
import static com.example.testvoicerecognition.App.*;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

public class VoiceController implements RecognitionListener {
    public static final String ACTION_VOICECONTROLLER_READY       = "com.example.testvoicerecognition.READY";
    public static final String ACTION_VOICECONTROLLER_PROCESSING  = "com.example.testvoicerecognition.PROCESSING";
    public static final String ACTION_VOICECONTROLLER_ERROR       = "com.example.testvoicerecognition.ERROR";
    public static final String ACTION_VOICECONTROLLER_SHOW_RESULT = "com.example.testvoicerecognition.SHOW.RESULT";
    public static final String EXTRAS_VOICECONTROLLER_VALUE       = "com.example.testvoicerecognition.EXTRAS.VALUE";

    private static final int   MAX_RESULTS                        = 1;
    private SpeechRecognizer   mSpeechRecognizer;
    private Context            mContext;

    public VoiceController( Context _context ) {
        super();
        mContext = _context;
    }

    public void create() {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer( mContext );
        mSpeechRecognizer.setRecognitionListener( this );
        Log.i( TAG, "create" );
    }

    public VoiceController() {
        super();
    }

    public void command() {
        Intent intent = createCommand( App.getInstance().getLanguage() );
        mSpeechRecognizer.startListening( intent );
    }

    public void destory() {
        mSpeechRecognizer.destroy();
        mSpeechRecognizer = null;
        Log.i( TAG, "destroy" );
    }

    private Intent createCommand( String _lang ) {
        Intent intent = new Intent( RecognizerIntent.ACTION_RECOGNIZE_SPEECH );
        intent.putExtra( RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM );
        intent.putExtra( RecognizerIntent.EXTRA_CALLING_PACKAGE, VoiceController.class.getPackage().getName() );
        intent.putExtra( RecognizerIntent.EXTRA_MAX_RESULTS, MAX_RESULTS );
        intent.putExtra( RecognizerIntent.EXTRA_LANGUAGE, _lang );
        return intent;
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i( TAG, "onBeginningOfSpeech" );
        notifyFront( ACTION_VOICECONTROLLER_PROCESSING );
    }

    @Override
    public void onBufferReceived( byte[] _buffer ) {
        Log.i( TAG, "onBufferReceived: " + new String( _buffer ) );

    }

    @Override
    public void onEndOfSpeech() {
        Log.i( TAG, "onEndOfSpeech" );
        notifyFront( ACTION_VOICECONTROLLER_PROCESSING );
    }

    @Override
    public void onError( int _error ) {
        notifyFront( ACTION_VOICECONTROLLER_ERROR, onErrorHandler( _error ) );
    }

    @Override
    public void onEvent( int _eventType, Bundle _params ) {
        Log.i( TAG, "onEvent" );
    }

    @Override
    public void onPartialResults( Bundle _partialResults ) {
        Log.i( TAG, "onPartialResults" );
    }

    @Override
    public void onReadyForSpeech( Bundle _params ) {
        Log.i( TAG, "onReadyForSpeech" );
        notifyFront( ACTION_VOICECONTROLLER_READY );
    }

    @Override
    public void onResults( Bundle _results ) {
        Log.i( TAG, "onResults" );
        List<String> res = _results.getStringArrayList( RESULTS_RECOGNITION );
        String word = null;
        if( res != null ) {
            int sz = res.size();
            if( sz >= 1 ) {
                word = res.get( 0 );
                Log.i( TAG, word );
            }
        }
        notifyFront( ACTION_VOICECONTROLLER_SHOW_RESULT, word );
    }

    @Override
    public void onRmsChanged( float _rmsdB ) {
        // Log.i( TAG, "onRmsChanged" );
    }

    private String onErrorHandler( int _error ) {
        String str = null;
        switch( _error )
        {
            case ERROR_AUDIO:
                Log.i( TAG, str = "Audio recording error." );
            break;
            case ERROR_CLIENT:
                Log.i( TAG, str = "Other client side errors." );
            break;
            case ERROR_INSUFFICIENT_PERMISSIONS:
                Log.i( TAG, str = "Insufficient permissions." );
            break;
            case ERROR_NETWORK:
                Log.i( TAG, str = "Other network related errors." );
            break;
            case ERROR_NETWORK_TIMEOUT:
                Log.i( TAG, str = "Network operation timed out." );
            break;
            case ERROR_NO_MATCH:
                Log.i( TAG, str = "No recognition result matched." );
            break;
            case ERROR_RECOGNIZER_BUSY:
                Log.i( TAG, str = "RecognitionService busy." );
            break;
            case ERROR_SERVER:
                Log.i( TAG, str = "Server sends error status." );
            break;
            case ERROR_SPEECH_TIMEOUT:
                Log.i( TAG, str = "No speech input." );
            break;
            default:
                Log.i( TAG, str = "Unknown error" );

        }
        return str;
    }

    private void notifyFront( String _action ) {
        notifyFront( _action, null );
    }

    private void notifyFront( String _action, String _result ) {
        Intent intentNotify = new Intent( _action );
        intentNotify.putExtra( EXTRAS_VOICECONTROLLER_VALUE, _result );
        mContext.sendBroadcast( intentNotify );
    }

}
