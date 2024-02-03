package com.teamopensmartglasses.smartglassesmanager.eventbusmessages;

import java.io.Serializable;

public class BulletPointListViewRequestEvent implements Serializable {
    public String title;
    public String [] bullets;
    public static final String eventId = "bulletPointListViewRequestEvent";

    public BulletPointListViewRequestEvent(String title, String [] bullets) {
        this.title = title;
        this.bullets = bullets;
    }
}
