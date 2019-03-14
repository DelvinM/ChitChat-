package edu.uw.chitchat.broadcast;

public class Broadcast {

    /** a field that contains info about the temperature for this broadcast object.*/
    private final String mTemperature;

    /** a field that contains info about the unix time for this broadcast object.*/
    private final String mTime;

    /** a field that contains info about the summary for this broadcast object.*/
    private final String mSummary;

    /** a field that contains info about the humidity for this broadcast object.*/
    private final String mHumidity;

    /** a field that contains info about the icon information which can be used later for image icon on the broadcast for this broadcast object.*/
    private final String mIcon;

    /**
     * constructor
     * @param temperature temperature for this broadcast object
     * @param time time for this broadcast object
     * @param summary summay for this broadcast object
     * @param humidity humidity for this broadcast object
     * @param icon icon for this broadcast object
     */
    public Broadcast(String temperature, String time, String summary, String humidity, String icon) {
        this.mTemperature = temperature;
        this.mTime = time;
        this.mSummary = summary;
        this.mHumidity = humidity;
        this.mIcon = icon;
    }

    /**
     * get method for the temperature
     * @return temperature
     */
    public String getTemperature() { return mTemperature; }

    /**
     * get method for the unix time.
     * @return unix time.
     */
    public String getTime() { return mTime; }

    /**
     * get method for the summary.
     * @return summary.
     */
    public String getSummary() { return mSummary; }

    /**
     * get method for the Humidity.
     * @return humidity.
     */
    public String getHumidity() { return mHumidity; }

    /**
     * get method for the icon info.
     * @return icon info.
     */
    public String getIcon() { return mIcon; }
}
