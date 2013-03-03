package com.example.testvoicerecognition;

import android.app.Application;

public final class App extends Application {

    public static final String TAG = "TestVoiceRecognizer";
    private static App sInstance;
    private String     mLanguage;

    private VoiceController mVoiceController;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mVoiceController = new VoiceController( this );
        // mVoiceController.create();
        // mVoiceController.command();
    }



    public static App getInstance() {
        return sInstance;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public void setLanguage( String _language ) {
        mLanguage = _language;
    }

    public String getStringInArray( int _resID, int _index ) {
        return (getResources().getStringArray( _resID ))[_index];
    }

    public VoiceController getVoiceController() {
        return mVoiceController;
    }

}
