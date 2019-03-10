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

    private final List<Broadcast> mValues;


    public MyWeekBroadcastRecycleViewAdapter(List<Broadcast> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_7daysbroadcast_single, parent, false);
        return new ViewHolder(view);
    }

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

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTemperature;
        public final TextView mTime;
        public final TextView mSummary;
        public final TextView mHumidity;
        public Broadcast mItem;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTemperature = (TextView) view.findViewById(R.id.fragment_7daysbroadcast_single_temperature);
            mTime = (TextView) view.findViewById(R.id.fragment_7daysbroadcast_single_time);
            mSummary = (TextView) view.findViewById(R.id.fragment_7daysbroadcast_single_summary);
            mHumidity = (TextView) view.findViewById(R.id.fragment_7daysbroadcast_single_humidity);
        }
    }

    //Sat Mar 09 18:00:00 PST 2019
    private String[] parseDate(String str) {
        String[] result = str.split(" ");
        return result;
    }
}
