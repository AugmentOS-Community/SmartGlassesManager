package com.smartglassesmanager.androidsmartphone;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;
import androidx.preference.PreferenceManager;

import com.smartglassesmanager.androidsmartphone.comms.MessageTypes;
import com.smartglassesmanager.androidsmartphone.eventbusmessages.ReferenceCardSimpleViewRequestEvent;
import com.smartglassesmanager.androidsmartphone.eventbusmessages.SmartGlassesConnectionEvent;
import com.smartglassesmanager.androidsmartphone.speechrecognition.ASR_FRAMEWORKS;
import com.smartglassesmanager.androidsmartphone.speechrecognition.SpeechRecSwitchSystem;
import com.smartglassesmanager.androidsmartphone.supportedglasses.SmartGlassesDevice;
import com.smartglassesmanager.androidsmartphone.texttospeech.TextToSpeechSystem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import io.reactivex.rxjava3.subjects.PublishSubject;

/** Main service of Smart Glasses Manager, that starts connections to smart glasses and talks to third party apps (3PAs) */
public class WearableAiAspService extends LifecycleService {
    private static final String TAG = "WearableAi_ASP_Service";

    // Service Binder given to clients
    private final IBinder binder = new LocalBinder();
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";

    //Text to Speech
    private TextToSpeechSystem textToSpeechSystem;

    //observables to send data around app
    PublishSubject<JSONObject> dataObservable;

    //representatives of the other pieces of the system
    SmartGlassesRepresentative smartGlassesRepresentative;

    //speech rec
    SpeechRecSwitchSystem speechRecSwitchSystem;

    //connection handler
    public Handler connectHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        //setup connection handler
        connectHandler = new Handler();

        //start speech rec
        speechRecSwitchSystem = new SpeechRecSwitchSystem(this.getApplicationContext());
        ASR_FRAMEWORKS asrFramework = getChosenAsrFramework(this.getApplicationContext());
        speechRecSwitchSystem.startAsrFramework(asrFramework);

        //setup data observable which passes information (transcripts, commands, etc. around our app using mutlicasting
        dataObservable = PublishSubject.create();

        //start text to speech
        textToSpeechSystem = new TextToSpeechSystem(this);
        textToSpeechSystem.setup();

        //setup event bus subscribers
        setupEventBusSubscribers();
    }

    private void setupEventBusSubscribers() {
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void handleConnectionEvent(SmartGlassesConnectionEvent event) {
        sendUiUpdate();
    }

    public void connectToSmartGlasses(SmartGlassesDevice device) {
        //this represents the smart glasses - it handles the connection, sending data to them, etc
        LifecycleService currContext = this;
        connectHandler.post(new Runnable() {
            @Override
            public void run() {
                 Log.d(TAG, "CONNECTING TO SMART GLASSES");
                smartGlassesRepresentative = new SmartGlassesRepresentative(currContext, device, currContext, dataObservable);
                smartGlassesRepresentative.connectToSmartGlasses();
            }
        });
    }

    //service stuff
    private Notification updateNotification() {
        Context context = getApplicationContext();

        PendingIntent action = PendingIntent.getActivity(context,
                0, new Intent(context, MainActivity.class),
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE); // Flag indicating that if the described PendingIntent already exists, the current one should be canceled before generating a new one.

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;

        String CHANNEL_ID = "wearable_ai_service_channel";

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Smart Glasses Manager",
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Running smart glasses middleware...");
        manager.createNotificationChannel(channel);

        builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        return builder.setContentIntent(action)
                .setContentTitle("Smart Glasses Manager")
                .setContentText("Communicating with Smart Glasses")
                .setSmallIcon(R.mipmap.sgm_launcher)
                .setTicker("...")
                .setContentIntent(action)
                .setOngoing(true).build();
    }

    //service stuffs
    public class LocalBinder extends Binder {
        WearableAiAspService getService() {
            // Return this instance of LocalService so clients can call public methods
            return WearableAiAspService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "WearableAI Service onStartCommand");
        if (intent != null) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_START_FOREGROUND_SERVICE:
                    // start the service in the foreground
                    startForeground(1234, updateNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE);
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForeground(true);
                    stopSelf();
                    break;
            }
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "WearableAiAspService killing itself and all its children");

        EventBus.getDefault().unregister(this);

        //kill asg connection
        if (smartGlassesRepresentative != null) {
            smartGlassesRepresentative.destroy();
            smartGlassesRepresentative = null;
        }

        //kill data transmitters
        if (dataObservable != null) {
            dataObservable.onComplete();
        }

        //kill speech rec
        if (speechRecSwitchSystem != null){
            speechRecSwitchSystem.destroy();
        }

        //kill textToSpeech
        textToSpeechSystem.destroy();

        //call parent destroy
        super.onDestroy();
        Log.d(TAG, "WearableAiAspService destroy complete");
    }

    public void sendTestCard(String title, String body, String img) {
        Log.d(TAG, "SENDING TEST CARD FROM WAIService");
        EventBus.getDefault().post(new ReferenceCardSimpleViewRequestEvent(title, body));
    }

    public int getSmartGlassesConnectState() {
        if (smartGlassesRepresentative != null) {
            return smartGlassesRepresentative.getConnectionState();
        } else {
            return 0;
        }
    }

    public void sendUiUpdate() {
        Intent intent = new Intent();
        intent.setAction(MessageTypes.GLASSES_STATUS_UPDATE);
        // Set the optional additional information in extra field.
        int connectionState;
        if (smartGlassesRepresentative != null) {
            connectionState = smartGlassesRepresentative.getConnectionState();
            intent.putExtra(MessageTypes.CONNECTION_GLASSES_GLASSES_OBJECT, smartGlassesRepresentative.smartGlassesDevice);
        } else {
            connectionState = 0;
        }
        intent.putExtra(MessageTypes.CONNECTION_GLASSES_STATUS_UPDATE, connectionState);
        sendBroadcast(intent);
    }

    /** Saves the chosen ASR framework in user shared preference. */
    public static void saveChosenAsrFramework(Context context, ASR_FRAMEWORKS asrFramework) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(context.getResources().getString(R.string.SHARED_PREF_ASR_KEY), asrFramework.name())
                .apply();
    }

    /** Gets the chosen ASR framework from shared preference. */
    public static ASR_FRAMEWORKS getChosenAsrFramework(Context context) {
        String asrString = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.SHARED_PREF_ASR_KEY), "");
        if (asrString.equals("")){
            saveChosenAsrFramework(context, ASR_FRAMEWORKS.VOSK_ASR_FRAMEWORK);
            asrString = ASR_FRAMEWORKS.VOSK_ASR_FRAMEWORK.name();
        }
        return ASR_FRAMEWORKS.valueOf(asrString);
    }

    public void changeChosenAsrFramework(ASR_FRAMEWORKS asrFramework){
        saveChosenAsrFramework(getApplicationContext(), asrFramework);
        if (speechRecSwitchSystem != null) {
            speechRecSwitchSystem.startAsrFramework(asrFramework);
        }
    }

    /** Gets the API key from shared preference. */
    public static String getApiKey(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.SHARED_PREF_KEY), "");
    }

    /** Saves the API Key in user shared preference. */
    public static void saveApiKey(Context context, String key) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(context.getResources().getString(R.string.SHARED_PREF_KEY), key)
                .apply();
    }
}
