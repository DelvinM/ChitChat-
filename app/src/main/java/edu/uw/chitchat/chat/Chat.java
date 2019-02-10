package edu.uw.chitchat.chat;

import java.io.Serializable;

public class Chat implements Serializable {
    private final String mName;
    private final String mDate;
    private final String mTeaser;

    public Chat(String name, String date, String teaser) {
        this.mName = name;
        this.mDate = date;
        this.mTeaser = teaser;
    }

    public String getName() { return mName; }
    public String getDate() { return mDate; }
    public String getTeaser() { return mTeaser; }
}
