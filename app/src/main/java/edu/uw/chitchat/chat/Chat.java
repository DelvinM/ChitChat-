package edu.uw.chitchat.chat;

import java.io.Serializable;

public class Chat implements Serializable {
    private final String mName;
    private final String mDate;
    private final String mTeaser;
    private final String mChatId;
    private final String mNotification;

    public Chat(String name, String date, String teaser, String id, String notification) {
        this.mName = name;
        this.mDate = date;
        this.mTeaser = teaser;
        this.mChatId = id;
        this.mNotification = notification;
    }

    public String getName() { return mName; }
    public String getDate() { return mDate; }
    public String getTeaser() { return mTeaser; }
    public String getChatId() { return mChatId; }
    public String getNotification() { return mNotification; }
}
