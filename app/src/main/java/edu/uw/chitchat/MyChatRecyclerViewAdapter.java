package edu.uw.chitchat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.uw.chitchat.chat.Chat;

public class MyChatRecyclerViewAdapter extends RecyclerView.Adapter<edu.uw.chitchat.MyChatRecyclerViewAdapter.ViewHolder> {

    private final List<Chat> mValues;
    private final ChatFragment.OnChatFragmentInteractionListener mListener;

    public MyChatRecyclerViewAdapter(List<Chat> items, ChatFragment.OnChatFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_chat_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).getName());
        holder.mDateView.setText(mValues.get(position).getDate());
        holder.mTeaserView.setText(mValues.get(position).getTeaser());
        holder.mNotificationView.setText("new " +mValues.get(position).getNotification());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onChatFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mDateView;
        public final TextView mTeaserView;
        public final TextView mNotificationView;
        public Chat mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.textView_name);
            mDateView = (TextView) view.findViewById(R.id.textView_date);
            mTeaserView = (TextView) view.findViewById(R.id.textView_teaser);
            mNotificationView = (TextView) view.findViewById(R.id.textView_notification);
        }
    }
}