package edu.uw.chitchat;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.chitchat.utils.PushReceiver;
import edu.uw.chitchat.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment {

    private PushMessageReceiver mPushMessageReciever;
    private TextView mMessageOutputTextView;

    //TODO: get args in onStart; no hard code
    private String mEmail;
    private String mChatId;
    private String mMessage;
    private String mSender;

    public UserProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (getArguments() != null) {
            mEmail = getArguments().getString("email");
            mMessage = getArguments().getString("message");
            mChatId = getArguments().getString("chatId");
            mSender = getArguments().getString("sender");
        }
        View v = inflater.inflate(R.layout.fragment_user_profile, container, false);
        mMessageOutputTextView = v.findViewById(R.id.text_connection_request);
        mMessageOutputTextView.append("message: " + mMessage);
        mMessageOutputTextView.append(System.lineSeparator());
        mMessageOutputTextView.append("user: " + mChatId);
        mMessageOutputTextView.append(System.lineSeparator());
        mMessageOutputTextView.append("sender: " + mSender);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPushMessageReciever == null) {
            mPushMessageReciever = new UserProfileFragment.PushMessageReceiver();
        }
        IntentFilter iFilter = new IntentFilter(PushReceiver.RECEIVED_NEW_MESSAGE);
        getActivity().registerReceiver(mPushMessageReciever, iFilter);
    }
    @Override
    public void onPause() {
        super.onPause();
        if (mPushMessageReciever != null){
            getActivity().unregisterReceiver(mPushMessageReciever);
        }
    }

    /**
     * A BroadcastReceiver that listens for messages sent from PushReceiver
     */
    private class PushMessageReceiver extends BroadcastReceiver {
        //TODO: in app pushy not working yet for
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra("SENDER") &&
                    intent.hasExtra("MESSAGE") &&
                    intent.hasExtra("CHATID"))
            {
                //chat id contains users email
                String chatId = intent.getStringExtra("CHATID");

                Log.e("", chatId);
                if (chatId.equals(mEmail)) {
                    String sender = intent.getStringExtra("SENDER");
                    String sendArr[] = {"", ""};
                    try {
                        sendArr = sender.split("@"); //if the message is coming from an email
                    } catch (Exception e) {
                        sendArr[0] = "Pushy Broadcast"; //if its coming from pushy
                    }
                    String messageText = intent.getStringExtra("MESSAGE");
                    mMessageOutputTextView.append(sendArr[0] + ": " + messageText);
                    mMessageOutputTextView.append(System.lineSeparator());
                    mMessageOutputTextView.append(System.lineSeparator());

                } else {
                    //in app notification goes here

                    //keep global counter of in app connection notifications
                 /*   int global_count = getSharedPreference(getString(R.string.keys_global_connection_count));
                    putSharedPreference(getString(R.string.keys_global_connection_count), global_count + 1);
*/
                    //TODO: update UI here for inapp notification
                    //add badge/dot to home activity tab
                    //add badges/dots to chat list

                }
            }
        }
    }

}
