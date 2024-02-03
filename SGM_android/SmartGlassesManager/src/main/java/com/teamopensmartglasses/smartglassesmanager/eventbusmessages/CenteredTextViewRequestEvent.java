package com.teamopensmartglasses.smartglassesmanager.eventbusmessages;

import java.io.Serializable;

public class CenteredTextViewRequestEvent implements Serializable {
    public String text;
    public static final String eventId = "centeredTextViewRequestEvent";

    public CenteredTextViewRequestEvent(String text) {
        this.text = text;
    }
}
