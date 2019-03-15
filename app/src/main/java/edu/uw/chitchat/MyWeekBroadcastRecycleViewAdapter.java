package edu.uw.chitchat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Date;
import java.util.List;
import edu.uw.chitchat.broadcast.Broadcast;

public class MyWeekBroadcastRecycleViewAdapter extends RecyclerView.Adapter<MyWeekBroadcastRecycleViewAdapter.ViewHolder>{

    /** field to hold the list of broadcast object*/
    private final List<Broadcast> mValues;

    /**
     * contructor
     * @param items refer to broadcast object
     */
    public MyWeekBroadcastRecycleViewAdapter(List<Broadcast> items) {
        mValues = items;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_7daysbroadcast_single, parent, false);
        return new ViewHolder(view);
    }


    /**
     * bind the holder which will has broadcast info
     * @param holder holder object to manipulate on the recycle view.
     * @param position index of the broadcast list
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        StringBuilder sb = new StringBuilder(mValues.get(position).getTemperature() + "\u00b0F");
        holder.mTemperature.setText(sb.toString());

        long unixTime = Long.valueOf(mValues.get(position).getTime());
        Date date = new Date(unixTime * 1000);
        String[] dateParsed = parseDate(date.toString());
        //Sat Mar 09 18:00:00 PST 2019
        sb = new StringBuilder(dateParsed[0]);
        holder.mTime.setText(sb.toString());

        holder.mSummary.setText(mValues.get(position).getSummary());
        sb = new StringBuilder(mValues.get(position).getHumidity() + "%");
        holder.mHumidity.setText(sb.toString());
    }


    /**
     * get method for the list size.
     * @return size of the list object.
     */
    @Override
    public int getItemCount() {
        return mValues.size();
    }



    /**
     *  view holder class for the 48 hr recycle view
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        /** View for one broadcast object */
        public final View mView;

        /** temperature text view for the holder object */
        public final TextView mTemperature;

        /** time text view for the holder object */
        public final TextView mTime;

        /** summary text view for the holder object */
        public final TextView mSummary;

        /** humidity text view for the holder object */
        public final TextView mHumidity;

        /** broad cast object for holder object */
        public Broadcast mItem;

        /**
         * contructor for the view holder.
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTemperature = (TextView) view.findViewById(R.id.fragment_7daysbroadcast_single_temperature);
            mTime = (TextView) view.findViewById(R.id.fragment_7daysbroadcast_single_time);
            mSummary = (TextView) view.findViewById(R.id.fragment_7daysbroadcast_single_summary);
            mHumidity = (TextView) view.findViewById(R.id.fragment_7daysbroadcast_single_humidity);
        }
    }

    /**
     * this will parse the date object to more manipulatable string.
     * @param str date object in string
     * @return return string array for easy manipulation.
     */
    //Sat Mar 09 18:00:00 PST 2019
    private String[] parseDate(String str) {
        String[] result = str.split(" ");
        return result;
    }
}
