package com.teamopensmartglasses.smartglassesmanager.speechrecognition;

import android.content.Context;

public abstract class SpeechRecFramework {
    private ASR_FRAMEWORKS asrFramework;
    private Context mContext;
    public boolean pauseAsrFlag = false;

    public abstract void start();
    public abstract void destroy();
    public abstract void ingestAudioChunk(byte [] audioChunk);

    public void pauseAsr(boolean pauseAsrFlag){
        this.pauseAsrFlag = pauseAsrFlag;
    }
}
