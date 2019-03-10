package edu.uw.chitchat;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import edu.uw.chitchat.ConnectionReceiveRequestListFragment.OnListFragmentInteractionListener;
import edu.uw.chitchat.ConnectionRequestList.ConnectionRequestList;
import edu.uw.chitchat.utils.SendPostAsyncTask;

/**
 * @author Yohei Sato
 *
 */

/**
 * {@link RecyclerView.Adapter} that can display a {@link ConnectionRequestList} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyConnectionReceiveRequestRecyclerViewAdapter extends RecyclerView.Adapter<MyConnectionReceiveRequestRecyclerViewAdapter.ViewHolder> {

    private final List<ConnectionRequestList> mValues;
    private final OnListFragmentInteractionListener mListener;
    private MyInterface mListener2;
    private Context mContext;
    public MyConnectionReceiveRequestRecyclerViewAdapter(List<ConnectionRequestList> items, OnListFragmentInteractionListener listener, MyInterface listener2) {
        mValues = items;
        mListener = listener;
        mListener2 = listener2;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_connection_receive_requestlist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mUserNameView.setText(mValues.get(position).getUsername());
        holder.deleteButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                mValues.remove(position);
                notifyDataSetChanged();
                Log.i("Delete Button Clicked","Delete!");
                //Toast.makeText(context, "Delete button Clicked", Toast.LENGTH_LONG).show();
            }
        });
        holder.acceptButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String myEmail = mListener.getEmail();
                String friendEmail = mValues.get(position).getEmailAddress();


                Uri uri = new Uri.Builder()
                        .scheme("https")
                        .appendPath("tcss450-app.herokuapp.com")
                        .appendPath("connection")
                        .appendPath("confirmRequest")
                        .build();
                //mListener.onRegisterSuccess(credentials);
                //build the web service URL
                //build the JSONObject

                //String emailstored = getSharedPreference (getString(R.string.keys_email_stored_onRegister));

                JSONObject test = new JSONObject();
                try {
                    test.put("email_A", myEmail);
                    test.put("email_B", friendEmail);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONObject test2 = new JSONObject();
                try {
                    test.put("email_A", friendEmail);
                    test.put("email_B", myEmail);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //instantiate and execute the AsyncTask.
                new SendPostAsyncTask.Builder(uri.toString(), test)
                        .build().execute();
                new SendPostAsyncTask.Builder(uri.toString(), test2)
                        .build().execute();

                mValues.remove(position);
                notifyDataSetChanged();
                Log.i("accept Button Clicked","Accept!");
                //Toast.makeText(context, "Delete button Clicked", Toast.LENGTH_LONG).show();
            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onConnectionReceiveRequestListFragmentInteraction(holder.mItem);
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
        public Button deleteButton = null;
        public Button acceptButton = null;
        public ConnectionRequestList mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mUserNameView = (TextView) view.findViewById(R.id.textView_ConnectionReceiveRequestname);
            deleteButton = (Button) view.findViewById(R.id.delete_button);
            acceptButton = (Button) view.findViewById(R.id.accept_button);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mUserNameView.getText() + "'";
        }
    }
    public interface MyInterface{
       String getMyEmail();
    }

}
