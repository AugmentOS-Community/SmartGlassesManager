package com.teamopensmartglasses.smartglassesmanager.smartglassescommunicators;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.teamopensmartglasses.smartglassesmanager.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vuzix.ultralite.Anchor;
import com.vuzix.ultralite.EventListener;
import com.vuzix.ultralite.Layout;
import com.vuzix.ultralite.TextAlignment;
import com.vuzix.ultralite.TextWrapMode;
import com.vuzix.ultralite.UltraliteColor;
import com.vuzix.ultralite.UltraliteSDK;


//communicate with ActiveLook smart glasses
public class UltraliteSGC extends SmartGlassesCommunicator {
    private static final String TAG = "WearableAi_UltraliteSGC";

    UltraliteSDK ultraliteSdk;
    UltraliteSDK.Canvas ultraliteCanvas;
    UltraliteListener ultraliteListener;
    LifecycleOwner lifecycleOwner;
    Context context;

    //handler to turn off screen
    Handler goHomeHandler;

    //handler to disconnect
    Handler killHandler;

    boolean hasUltraliteControl;
    boolean screenIsClear;

    public class UltraliteListener implements EventListener{
        @Override
        public void onTap(int tapCount) {
            Log.d(TAG, "Ultralite go tap n times: " + tapCount);
            tapEvent(tapCount);
        }

        @Override
        public void onDisplayTimeout() {
            Log.d(TAG, "Ultralite display timeout.");
        }

        @Override
        public void onPowerButtonPress(boolean turningOn) {
            Log.d(TAG, "Ultralites power button pressed: " + turningOn);
            if (screenIsClear) {
                displayReferenceCardSimple("SGM Connected.", "Waiting for data...", 2);
            }
        }
    }

    public UltraliteSGC(Context context, LifecycleOwner lifecycleOwner) {
        super();
        this.lifecycleOwner = lifecycleOwner;
        this.context = context;

        mConnectState = 0;
        hasUltraliteControl = false;
        screenIsClear = true;
        goHomeHandler = new Handler();
        killHandler = new Handler();

        ultraliteSdk = UltraliteSDK.get(context);
        ultraliteListener = new UltraliteListener();
        ultraliteSdk.addEventListener(ultraliteListener);

        LiveData<Boolean> ultraliteConnectedLive = ultraliteSdk.getConnected();
        ultraliteConnectedLive.observe(lifecycleOwner, isConnected -> {
            onUltraliteConnectedChange(isConnected);
        });

        LiveData<Boolean> ultraliteControlled = ultraliteSdk.getControlledByMe();
        ultraliteControlled.observe(lifecycleOwner, isControlled -> {
            onUltraliteControlChanged(isControlled);
        });

//        if (ultraliteSdk.isAvailable()){
//            Log.d(TAG, "Ultralite SDK is available.");
//        } else {
//            Log.d(TAG, "Ultralite SDK is NOT available.");
//        }
    }

    private void onUltraliteConnectedChange(boolean isConnected) {
        Log.d(TAG, "Ultralite CONNECT changed to: " + isConnected);
        if (isConnected) {
            Log.d(TAG, "Ultralite requesting control...");
            boolean isControlled = ultraliteSdk.requestControl();
//            if (isControlled){
//                setupUltraliteCanvas();
//            } else {
//                return;
//            }
            Log.d(TAG, "Ultralite RESULT control request: " + isControlled);
            connectionEvent(2);
        } else {
            Log.d(TAG, "Ultralite not connected.");
            connectionEvent(0);
        }
    }

    private void onUltraliteControlChanged(boolean isControlledByMe) {
        Log.d(TAG, "Ultralite CONTROL changed to: " + isControlledByMe);
        if(isControlledByMe) {
            hasUltraliteControl = true;
            setupUltraliteCanvas();
            connectionEvent(2);
            displayReferenceCardSimple("Connected to SGM.", "Authors: TeamOpenSmartGlasses", 5);
        } else {
            hasUltraliteControl = false;
        }
//        mUltraliteControlledByMe = isControlledByMe;
    }

    @Override
    protected void setFontSizes(){
    }

    @Override
    public void connectToSmartGlasses(){
        Log.d(TAG, "connectToSmartGlasses running...");
//        int mCount = 10;
//        while ((mConnectState != 2) && (!hasUltraliteControl) && (mCount > 0)){
//            mCount--;
//            try {
//                Log.d(TAG, "Don't have Ultralite yet, let's wait for it...");
//                Thread.sleep(200);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        Log.d(TAG, "Connected to Ultralites.");
//        Log.d(TAG, "mCOnnectestate: " + mConnectState);
//        Log.d(TAG, "mCOunt: " + mCount);
//        displayReferenceCardSimple("Connected to SGM.", "Authors: TeamOpenSmartGlasses");
//        connectionEvent(mConnectState);
        Log.d(TAG, "connectToSmartGlasses finished");
    }

    public void displayTextLine(String text){
        displayReferenceCardSimple("", text);
    }

    public void displayCenteredText(String text){
    }

    public void showNaturalLanguageCommandScreen(String prompt, String naturalLanguageInput){
//        int boxDelta = 3;
//
//        if (connectedGlasses != null) {
//            connectedGlasses.clear();
//            showPromptCircle();
//
//            //show the prompt
//            lastLocNaturalLanguageArgsTextView = displayText(new TextLineSG(prompt, SMALL_FONT), new Point(0, 11), true);
//            lastLocNaturalLanguageArgsTextView = new Point(lastLocNaturalLanguageArgsTextView.x, lastLocNaturalLanguageArgsTextView.y + boxDelta); //margin down a tad
//
//            //show the final "finish command" prompt
//            int finishY = 90;
//            displayLine(new Point(0, finishY), new Point(100, finishY));
//            displayText(new TextLineSG(finishNaturalLanguageString, SMALL_FONT), new Point(0, finishY + 2), true);
//
//            //show the natural language args in a scroll box
////            ArrayList<TextLineSG> nli = new ArrayList<>();
////            nli.add(new TextLineSG(naturalLanguageInput, SMALL_FONT));
////            lastLocNaturalLanguageArgsTextView = scrollTextShow(nli, startScrollBoxY.y + boxDelta, finishY - boxDelta);
//        }
    }

    public void updateNaturalLanguageCommandScreen(String naturalLanguageArgs){
//        Log.d(TAG, "Displaynig: " + naturalLanguageArgs);
//        displayText(new TextLineSG(naturalLanguageArgs, SMALL_FONT), new Point(0, lastLocNaturalLanguageArgsTextView.y));
    }

    public void blankScreen(){
//        if (connectedGlasses != null){
//            connectedGlasses.clear();
//        }
    }

    @Override
    public void destroy(){
       if (ultraliteSdk != null){
//           displayReferenceCardSimple("Disconnecting...", "Disconnecting Smart Glasses from SGM");
//
//           //disconnect after slight delay, so our above text gets a chance to show up
//           killHandler.postDelayed(new Runnable() {
//               @Override
//               public void run() {
//                   ultraliteSdk.releaseControl();
//               }
//           }, 800);
           ultraliteSdk.removeEventListener(ultraliteListener);
           ultraliteSdk.releaseControl();
       }
    }

    public void showHomeScreen(){
        changeUltraliteLayout(Layout.CANVAS);
        ultraliteCanvas.clear();
        screenIsClear = true;
    }

    public void setupUltraliteCanvas(){
        Log.d(TAG, "Setting up ultralite canvas");
        if (ultraliteSdk != null) {
            ultraliteCanvas = ultraliteSdk.getCanvas();
        }
    }

    public void changeUltraliteLayout(Layout chosenLayout) {
        ultraliteSdk.setLayout(chosenLayout, 0, true);

        if (chosenLayout.equals(Layout.CANVAS)){
            if (ultraliteCanvas == null){
                setupUltraliteCanvas();
            }
        }
    }

    public void startScrollingTextViewMode(String title){
        super.startScrollingTextViewMode(title);

        if (ultraliteSdk == null) {
            return;
        }

        //clear the screen
        ultraliteCanvas.clear();
        drawTextOnUltralite(title);
    }

    public String addNewlineEveryNWords(String input, int n) {
        String[] words = input.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            result.append(words[i]);
            if ((i + 1) % n == 0 && i != words.length - 1) {
                result.append("\n");
            } else if (i != words.length - 1) {
                result.append(" ");
            }
        }

        return result.toString();
    }

    public void drawTextOnUltralite(String text){
        //edit the text to add new lines to it because ultralite wrapping doesn't work
        String wrappedText = addNewlineEveryNWords(text, 6);

        //display the title at the top of the screen
        UltraliteColor ultraliteColor = UltraliteColor.WHITE;
        Anchor ultraliteAnchor = Anchor.TOP_LEFT;
        TextAlignment ultraliteAlignment = TextAlignment.LEFT;
        changeUltraliteLayout(Layout.CANVAS);
        ultraliteCanvas.clear();
        ultraliteCanvas.clearBackground(UltraliteColor.DIM);
//        ultraliteCanvas.createText(text, ultraliteAlignment, ultraliteColor, ultraliteAnchor, true);
//        ultraliteCanvas.createText(text, ultraliteAlignment, ultraliteColor, Anchor.BOTTOM_LEFT, 0, 0, -1, 80, TextWrapMode.WRAP, true);
        ultraliteCanvas.createText(wrappedText, ultraliteAlignment, ultraliteColor, ultraliteAnchor, true); //, 0, 0, -1, -1, TextWrapMode.WRAP, true);
        ultraliteCanvas.commit();
        screenIsClear = false;
    }

    public Bitmap getBitmapFromDrawable(Resources res) {
        return BitmapFactory.decodeResource(res, R.drawable.vuzix_shield);
    }

    public void displayReferenceCardSimple(String title, String body){
       displayReferenceCardSimple(title, body, 40);
    }

    public void displayReferenceCardSimple(String title, String body, int lingerTime){
        if (!isConnected()) {
            Log.d(TAG, "Not showing reference card because not connected to Ultralites...");
            return;
        }

//        String [] bulletPoints = {"first one", "second one", "dogs and cats"};
//        displayBulletList("Cool Bullets:", bulletPoints, 15);

            Log.d(TAG, "Sending text to Ultralite SDK: " + title + "     " + body);
//            ultraliteSdk.sendText("hello world"); //this is BROKEN in Vuzix ultralite 0.4.2 SDK - crashes Vuzix OEM Platform android app

        //edit the text to add new lines to it because ultralite wrapping doesn't work
//        String titleWrapped = addNewlineEveryNWords(title, 6);
//        String bodyWrapped = addNewlineEveryNWords(body, 6);

        //display the title at the top of the screen
        UltraliteColor ultraliteColor = UltraliteColor.WHITE;
        Anchor ultraliteAnchor = Anchor.TOP_LEFT;
        TextAlignment ultraliteAlignment = TextAlignment.LEFT;
        changeUltraliteLayout(Layout.CANVAS);
        ultraliteCanvas.clear();
        ultraliteCanvas.createText(title, TextAlignment.AUTO, UltraliteColor.WHITE, Anchor.TOP_LEFT, 0, 120, 640, -1, TextWrapMode.WRAP, true);
        ultraliteCanvas.createText(body, TextAlignment.AUTO, UltraliteColor.WHITE, Anchor.MIDDLE_LEFT, 0, 0, 640, -1, TextWrapMode.WRAP, true);
        ultraliteCanvas.commit();
        screenIsClear = false;

        homeScreenInNSeconds(lingerTime);
    }

    public void displayBulletList(String title, String [] bullets){
        displayBulletList(title, bullets, 14);
    }

    public void displayBulletList(String title, String [] bullets, int lingerTime){
        if (!isConnected()) {
            Log.d(TAG, "Not showing bullet point list because not connected to Ultralites...");
            return;
        }

        Log.d(TAG, "Sending bullets to Ultralite SDK: " + title);

        //display the title at the top of the screen
        UltraliteColor ultraliteColor = UltraliteColor.WHITE;
        Anchor ultraliteAnchor = Anchor.TOP_LEFT;
        TextAlignment ultraliteAlignment = TextAlignment.LEFT;
        changeUltraliteLayout(Layout.CANVAS);
        ultraliteCanvas.clear();

        ultraliteCanvas.createText(title, TextAlignment.AUTO, UltraliteColor.WHITE, Anchor.TOP_LEFT, 0, 0, 640, -1, TextWrapMode.WRAP, true);
        int displaceY = 80;
        int displaceX = 35;
        for (String bullet : bullets){
            ultraliteCanvas.createText("⬤ " + bullet, TextAlignment.AUTO, UltraliteColor.WHITE, Anchor.TOP_LEFT, displaceX, displaceY, 640 - displaceX, -1, TextWrapMode.WRAP, true);
            displaceY += 80;
        }

        ultraliteCanvas.commit();
        screenIsClear = false;

        if (lingerTime > 0){
            homeScreenInNSeconds(lingerTime);
        }
    }

    public void homeScreenInNSeconds(int n){
       //disconnect after slight delay, so our above text gets a chance to show up
       goHomeHandler.removeCallbacksAndMessages(this);
       goHomeHandler.postDelayed(new Runnable() {
           @Override
           public void run() {
                           showHomeScreen();
                                            }
       }, n * 1000);
    }

    //don't show images on activelook (screen is too low res)
    public void displayReferenceCardImage(String title, String body, String imgUrl){
        changeUltraliteLayout(Layout.CANVAS);
        ultraliteCanvas.clear();

        //make image
        //below works, but only for very, very low res/size images
        Anchor ultraliteImageAnchor = Anchor.CENTER;
        Picasso.get()
                .load(imgUrl)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        // Use the bitmap
//                        LVGLImage ultraliteImage = LVGLImage.fromBitmap(getBitmapFromDrawable(context.getResources()), CF_INDEXED_2_BIT);
//                        LVGLImage ultraliteImage = LVGLImage.fromBitmap(bitmap, CF_INDEXED_2_BIT);
                        changeUltraliteLayout(Layout.CANVAS);

                        //send text first, cuz this is fast
                        ultraliteCanvas.createText(title, TextAlignment.AUTO, UltraliteColor.WHITE, Anchor.TOP_LEFT, 0, 0, 640, -1, TextWrapMode.WRAP, true);
                        ultraliteCanvas.createText(body, TextAlignment.AUTO, UltraliteColor.WHITE, Anchor.BOTTOM_LEFT, 0, 0, 640, -1, TextWrapMode.WRAP, true);
                        ultraliteCanvas.commit();
                        screenIsClear = false;

                        Log.d(TAG, "Sending image to Ultralite");
//                        ultraliteCanvas.createImage(ultraliteImage, ultraliteImageAnchor, 0, 0, true);
                        ultraliteCanvas.drawBackground(bitmap, 50, 80);

                        //sending text again to ultralite in case image overwrote it
//                        ultraliteCanvas.createText(title + "2", TextAlignment.AUTO, UltraliteColor.WHITE, Anchor.BOTTOM_LEFT, 0, 0, 640, -1, TextWrapMode.WRAP, true);
//                        ultraliteCanvas.createText(body + "2", TextAlignment.AUTO, UltraliteColor.WHITE, Anchor.MIDDLE_LEFT, 0, 0, 640, -1, TextWrapMode.WRAP, true);
//                        ultraliteCanvas.commit();

//                        //display the title at the top of the screen
//                        UltraliteColor ultraliteColor = UltraliteColor.WHITE;
//                        TextAlignment ultraliteAlignment = TextAlignment.LEFT;
//                //        ultraliteCanvas.clearBackground(UltraliteColor.DIM);
//                        ultraliteCanvas.createText(titleWrapped, ultraliteAlignment, ultraliteColor, Anchor.TOP_LEFT, true); //, 0, 0, -1, -1, TextWrapMode.WRAP, true);
//                        ultraliteCanvas.createText(bodyWrapped, ultraliteAlignment, ultraliteColor, Anchor.BOTTOM_LEFT, true); //, 0, 0, -1, -1, TextWrapMode.WRAP, true);
//                        ultraliteCanvas.commit();



                        homeScreenInNSeconds(14);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        // Handle the error
                        Log.d(TAG, "Bitmap failed");
                        e.printStackTrace();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        // Called before the image is loaded. You can set a placeholder if needed.
                    }
                });

            //edit the text to add new lines to it because ultralite wrapping doesn't work
//            String titleWrapped = addNewlineEveryNWords(title, 6);
//            String bodyWrapped = addNewlineEveryNWords(body, 6);
//
//            //display the title at the top of the screen
//            UltraliteColor ultraliteColor = UltraliteColor.WHITE;
//            TextAlignment ultraliteAlignment = TextAlignment.LEFT;
//            //ultraliteCanvas.clearBackground(UltraliteColor.DIM);
//            ultraliteCanvas.createText(titleWrapped, ultraliteAlignment, ultraliteColor, Anchor.TOP_LEFT, true); //, 0, 0, -1, -1, TextWrapMode.WRAP, true);
//            ultraliteCanvas.createText(bodyWrapped, ultraliteAlignment, ultraliteColor, Anchor.BOTTOM_LEFT, true); //, 0, 0, -1, -1, TextWrapMode.WRAP, true);
//            ultraliteCanvas.commit();
//            screenIsClear = false;
    }

    //handles text wrapping, returns final position of last line printed
//    private Point displayText(TextLineSG textLine, Point percentLoc, boolean centered){
//        if (!isConnected()){
//            return null;
//        }
//
//        //get info about the wrapping
//        Pair wrapInfo = computeStringWrapInfo(textLine);
//        int numWraps = (int)wrapInfo.first;
//        int wrapLenNumChars = (int)wrapInfo.second;
//
//        //loop through the text, writing out individual lines to the glasses
//        ArrayList<String> chunkedText = new ArrayList<>();
//        Point textPoint = percentLoc;
//        int textMarginY = computeMarginPercent(textLine.getFontSizeCode()); //(fontToSize.get(textLine.getFontSize()) * 1.3)
//        for (int i = 0; i <= numWraps; i++){
//            int startIdx = wrapLenNumChars * i;
//            int endIdx = Math.min(startIdx + wrapLenNumChars, textLine.getText().length());
//            String subText = textLine.getText().substring(startIdx, endIdx).trim();
//            chunkedText.add(subText);
//            TextLineSG thisTextLine = new TextLineSG(subText, textLine.getFontSizeCode());
//            if (!centered) {
//                sendTextToGlasses(thisTextLine, textPoint);
//            } else {
//                int xPercentLoc = computeStringCenterInfo(thisTextLine);
//                sendTextToGlasses(thisTextLine, new Point(xPercentLoc, textPoint.y));
//            }
//            textPoint = new Point(textPoint.x, textPoint.y + pixelToPercent(displayHeightPixels, fontToSize.get(textLine.getFontSizeCode())) + textMarginY); //lower our text for the next loop
//        }
//
//        return textPoint;
//    }

    public void stopScrollingTextViewMode() {
//        if (connectedGlasses == null) {
//            return;
//        }
//
//        //clear the screen
//        connectedGlasses.clear();
    }

    public void scrollingTextViewIntermediateText(String text){
    }

    public void scrollingTextViewFinalText(String text){
        if (!isConnected()){
            return;
        }

//        //save to our saved list of final scrolling text strings
//        finalScrollingTextStrings.add(text);
//
//        //get the max number of wraps allows
//        float allowedTextRows = computeAllowedTextRows(fontToSize.get(scrollingTextTitleFontSize), fontToSize.get(scrollingTextTextFontSize), percentToPixel(displayHeightPixels, computeMarginPercent(scrollingTextTextFontSize)));
//
//        //figure out the maximum we can display
//        int totalRows = 0;
//        ArrayList<String> finalTextToDisplay = new ArrayList<>();
//        boolean hitBottom = false;
//        for (int i = finalScrollingTextStrings.toArray().length - 1; i >= 0; i--){
//            String finalText = finalScrollingTextStrings.get(i);
//            //convert to a TextLine type with small font
//            TextLineSG tlString = new TextLineSG(finalText, SMALL_FONT);
//            //get info about the wrapping of this string
//            Pair wrapInfo = computeStringWrapInfo(tlString);
//            int numWraps = (int)wrapInfo.first;
//            int wrapLenNumChars = (int)wrapInfo.second;
//            totalRows += numWraps + 1;
//
//            if (totalRows > allowedTextRows){
//                finalScrollingTextStrings = finalTextToDisplay;
//                lastLocScrollingTextView = belowTitleLocScrollingTextView;
//                //clear the glasses as we hit our limit and need to redraw
//                connectedGlasses.color((byte)0x00);
//                connectedGlasses.rectf(percentScreenToPixelsLocation(belowTitleLocScrollingTextView.x, belowTitleLocScrollingTextView.y), percentScreenToPixelsLocation(100, 100));
//                //stop looping, as we've ran out of room
//                hitBottom = true;
//            } else {
//                finalTextToDisplay.add(0, finalText);
//            }
//        }
//
//        //display all of the text that we can
//        if (hitBottom) { //if we ran out of room, we need to redraw all the text
//            for (String finalString : finalTextToDisplay) {
//                TextLineSG tlString = new TextLineSG(finalString, scrollingTextTextFontSize);
//                //write this text at the last location + margin
//                Log.d(TAG, "Writing string: " + tlString.getText() + finalTextToDisplay.size());
//                lastLocScrollingTextView = displayText(tlString, new Point(0, lastLocScrollingTextView.y));
//            }
//        } else { //if we didn't hit the bottom, and there's room, we can just display the next line
//            TextLineSG tlString = new TextLineSG(text, scrollingTextTextFontSize);
//            lastLocScrollingTextView = displayText(tlString, new Point(0, lastLocScrollingTextView.y));
//        }

    }

    public void displayPromptView(String prompt, String [] options){
        if (!isConnected()){
            return;
        }

//        ultraliteCanvas.clear();
//        connectedGlasses.clear();
//        showPromptCircle();
//
//        //show the prompt and options, if any
//        ArrayList<Object> promptPageElements = new ArrayList<>();
//        promptPageElements.add(new TextLineSG(prompt, LARGE_FONT));
//        if (options != null) {
//            //make an array list of options
//            for (String s : options){
//               promptPageElements.add(new TextLineSG(s, SMALL_FONT));
//            }
//        }
//        displayLinearStuff(promptPageElements, new Point(0, 11), true);
    }
}
