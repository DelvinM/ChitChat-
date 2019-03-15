package edu.uw.chitchat.chat;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * This is a Chat object containing the members of a chatroom, the messages, and the date and
 * teaser of the most recent messages (as well as the number of unread messages).
 * @author Logan Jenny
 */

public class Chat implements Serializable {
    private final String mName;
    private final String mDate;
    private final String mTeaser;
    private final String mChatId;
    private String mNotification;
    private final ArrayList<String> mContents;

    /**
     * This is a constructor for Chat
     * @author Logan Jenny
     * @param name is the string containing the members of the chat
     * @param date is the date of the most recent message in the chat
     * @param teaser is 2 lines of the most recent chat message
     * @param id is the internal id of the chat on the backend
     * @param notification is the number of unread messages
     * @param contents is the ArrayList of messages contained in the chat
     */
    public Chat(String name, String date, String teaser, String id, String notification, ArrayList contents) {
        this.mName = name;
        this.mDate = date;
        this.mTeaser = teaser;
        this.mChatId = id;
        this.mNotification = notification;
        this.mContents = contents;
    }


    /**
     * Bulk getters and setters
     * @author Logan Jenny
     */
    public String getName() { return mName; }
    public String getDate() { return mDate; }
    public String getTeaser() { return mTeaser; }
    public String getChatId() { return mChatId; }
    public String getNotification() { return mNotification; }
    public void setNotification(String value) {mNotification = value;}
    public ArrayList<String> getContents() { return mContents; }
}
