package edu.uw.chitchat.broadcast;

public class Broadcast {

    private final String mTemperature;
    private final String mTime;
    private final String mSummary;
    private final String mHumidity;
    private final String mIcon;

    public Broadcast(String temperature, String time, String summary, String humidity, String icon) {
        this.mTemperature = temperature;
        this.mTime = time;
        this.mSummary = summary;
        this.mHumidity = humidity;
        this.mIcon = icon;
    }

    public String getTemperature() { return mTemperature; }
    public String getTime() { return mTime; }
    public String getSummary() { return mSummary; }
    public String getHumidity() { return mHumidity; }
    public String getIcon() { return mIcon; }
}
