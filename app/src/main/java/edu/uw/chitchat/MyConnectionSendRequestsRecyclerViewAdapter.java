package edu.uw.chitchat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.uw.chitchat.ConnectionRequestList.ConnectionRequestList;
import edu.uw.chitchat.ConnectionSendRequestListFragment.OnListFragmentInteractionListener;

/**
 * @author Yohei Sato
 *
 */

/**
 * {@link RecyclerView.Adapter} that can display a {@link ConnectionRequestList} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyConnectionSendRequestsRecyclerViewAdapter extends RecyclerView.Adapter<MyConnectionSendRequestsRecyclerViewAdapter.ViewHolder> {

    private final List<ConnectionRequestList> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyConnectionSendRequestsRecyclerViewAdapter(List<ConnectionRequestList> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_connection_send_request_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mUserNameView.setText(mValues.get(position).getUsername());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onConnectionSendRequestListFragmentInteraction(holder.mItem);
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
        public final TextView mUserNameView;
        public ConnectionRequestList mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mUserNameView = (TextView) view.findViewById(R.id.textView_ConnectionSendRequestname);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mUserNameView.getText() + "'";
        }
    }
}
