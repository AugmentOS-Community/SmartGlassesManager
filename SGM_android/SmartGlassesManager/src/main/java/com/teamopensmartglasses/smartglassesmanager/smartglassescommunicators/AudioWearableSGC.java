package com.teamopensmartglasses.smartglassesmanager.smartglassescommunicators;

import android.content.Context;
import android.util.Log;

import com.teamopensmartglasses.smartglassesmanager.eventbusmessages.TextToSpeechEvent;

import org.greenrobot.eventbus.EventBus;

public class AudioWearableSGC extends SmartGlassesCommunicator {
    private static final String TAG = "WearableAi_AndroidWearableSGC";

    private static boolean killme;

    Context context;

    public AudioWearableSGC(Context context){
        super();

        //state information
        killme = false;
        mConnectState = 0;
    }

    public void setFontSizes(){
    }

    public void connectToSmartGlasses(){
        connectionEvent(2);
    }

    public void blankScreen(){
    }

    public void destroy(){
        killme = true;
    }

    public void displayReferenceCardSimple(String title, String body){
        Log.d(TAG, "TTS reference card");
        EventBus.getDefault().post(new TextToSpeechEvent(title + ", " + body));
    }

    public void displayReferenceCardImage(String title, String body, String imgUrl){
        Log.d(TAG, "TTS reference card");
        EventBus.getDefault().post(new TextToSpeechEvent(title + ", " + body));
    }

    public void displayBulletList(String title, String [] bullets){
        displayBulletList(title, bullets, 0);
    }

    public void displayBulletList(String title, String [] bullets, int lingerTime){

    }


    public void stopScrollingTextViewMode() {
    }

    public void startScrollingTextViewMode(String title){
    }

    public void scrollingTextViewIntermediateText(String text){
    }

    public void scrollingTextViewFinalText(String text){
    }

    public void showHomeScreen(){
    }

    public void displayPromptView(String prompt, String [] options){
    }

    public void displayTextLine(String text){
        Log.d(TAG, "displayTextLine: " + text);
        EventBus.getDefault().post(new TextToSpeechEvent(text));
    }

    public void displayCenteredText(String text){
        //TODO: Find a way to add (optional) pauses between lines?
        displayTextLine(text);
    }


    public void showNaturalLanguageCommandScreen(String prompt, String naturalLanguageArgs){
    }

    public void updateNaturalLanguageCommandScreen(String naturalLanguageArgs){
    }
}