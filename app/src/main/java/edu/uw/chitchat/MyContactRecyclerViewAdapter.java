package edu.uw.chitchat;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import edu.uw.chitchat.ContactListFragment.OnListFragmentInteractionListener;
import edu.uw.chitchat.contactlist.ContactList;
import edu.uw.chitchat.utils.SendPostAsyncTask;

/**
 * @author Yohei Sato
 *
 */

/**
 * {@link RecyclerView.Adapter} that can display a {@link ContactList} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyContactRecyclerViewAdapter extends RecyclerView.Adapter<MyContactRecyclerViewAdapter.ViewHolder> {

    private final List<ContactList> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyContactRecyclerViewAdapter(List<ContactList> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contact_list_item, parent, false);
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
                String myEmail = mListener.getEmail();
                String friendEmail = mValues.get(position).getEmailAddress();

                Uri uri = new Uri.Builder()
                        .scheme("https")
                        .appendPath("tcss450-app.herokuapp.com")
                        .appendPath("connection")
                        .appendPath("deleteFriend")
                        .build();

                JSONObject test = new JSONObject();
                try {
                    test.put("email_A", myEmail);
                    test.put("email_B", friendEmail);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("Delete contact Button Clicked","Delete the contact!");
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Delete the Contact")
                        .setMessage("Are you sure you want to delete this contact?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new SendPostAsyncTask.Builder(uri.toString(), test)
                                        .build().execute();
                                mValues.remove(position);
                                Toast.makeText(v.getContext(), "You deleted the contct",
                                        Toast.LENGTH_LONG).show();
                                notifyDataSetChanged();
                                Log.i("Delete contact Button Clicked","Delete the contact!");
                                //Toast.makeText(context, "Delete button Clicked", Toast.LENGTH_LONG).show();
                            }
                        })
                        .show();
            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onContactListFragmentInteraction(holder.mItem);
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
            public ContactList mItem;
            public Button deleteButton = null;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mUserNameView = (TextView) view.findViewById(R.id.textView_name);
                deleteButton = (Button) view.findViewById(R.id.delete_contact_button);
            }

        @Override
        public String toString() {
            return super.toString() + " '" + mUserNameView.getText() + "'";
        }
    }
}
