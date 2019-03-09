package edu.uw.chitchat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import edu.uw.chitchat.chat.Chat;
import edu.uw.chitchat.utils.PushReceiver;
import edu.uw.chitchat.utils.SendPostAsyncTask;

/*
 * @author Logan Jenny
 */
public class FullChatFragment extends Fragment {

    private String mJwToken;
    private String mEmail;
    private static final String TAG = "CHAT_FRAG";
    private String CHAT_ID = "1";
    private TextView mMessageOutputTextView;
    private EditText mMessageInputEditText;
    private String mSendUrl;
    private ArrayList<String> mContents;
    private PushMessageReceiver mPushMessageReciever;


    public FullChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_full_chat, container, false);
        mMessageInputEditText = v.findViewById(R.id.edit_chat_message_input);
        mMessageOutputTextView = v.findViewById(R.id.text_chat_message_display);
        ((ImageButton) v.findViewById(R.id.button_chat_send)).setOnClickListener(this::handleSendClick);

        if (getArguments() != null) {
            mContents = getArguments().getStringArrayList("contents");
            for (int j = mContents.size() - 1; j >= 0; j--) {
                mMessageOutputTextView.append(mContents.get(j));
                mMessageOutputTextView.append(System.lineSeparator());
                mMessageOutputTextView.append(System.lineSeparator());
            }
        }

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            //get the email and JWT from the Activity. Make sure the Keys match what you used
            mEmail = getArguments().getString("email");
            mJwToken = getArguments().getString("jwt");
            CHAT_ID = getArguments().getString("chatId");
        }

        //set notification count for current chat to 0, in shared pref
        removeChatNotificationCount();


        //We will use this url every time the user hits send. Let's only build it once, ya?
        mSendUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_messaging_base))
                .appendPath(getString(R.string.ep_messaging_send))
                .build()
                .toString();
    }

    private void removeChatNotificationCount () {
        //accesses shared pref and removes chat notification count
        //TODO: this could use a local database. refactor? (delvin)

        String prefString = "chat room " + CHAT_ID + " count";

        //compute new global count
        int notification_count_chat = getSharedPreference(prefString);
        int notification_count_global = getSharedPreference(getString(R.string.keys_global_chat_count));
        int global_count = notification_count_global - notification_count_chat;

        putSharedPreference(prefString, 0);
        putSharedPreference(getString(R.string.keys_global_chat_count), global_count);
    }

    //TODO: REFACTOR
    //adds single value to shared preferences
    //refactor later make this a class
    private void putSharedPreference (String key, int value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    private int getSharedPreference (String key) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        return preferences.getInt(key, 0);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mPushMessageReciever == null) {
            mPushMessageReciever = new PushMessageReceiver();
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

    private void handleSendClick(final View theButton) {
        String msg = mMessageInputEditText.getText().toString();
        JSONObject messageJson = new JSONObject();
        try {
            messageJson.put("email", mEmail);
            messageJson.put("message", msg);
            messageJson.put("chatId", CHAT_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(mSendUrl, messageJson)
                .onPostExecute(this::endOfSendMsgTask)
                .onCancelled(error -> Log.e(TAG, error))
                .addHeaderField("authorization", mJwToken)
                .build().execute();
    }
    private void endOfSendMsgTask(final String result) {
        try {
            //This is the result from the web service
            JSONObject res = new JSONObject(result);
            if(res.has("success") && res.getBoolean("success")) {
                //The web service got our message. Time to clear out the input EditText
                mMessageInputEditText.setText("");
                //its up to you to decide if you want to send the message to the output here
                //or wait for the message to come back from the web service.
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * A BroadcastReceiver that listens for messages sent from PushReceiver
     */
    private class PushMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra("SENDER") &&
                    intent.hasExtra("MESSAGE") &&
                    intent.hasExtra("CHATID"))
            {
                //chat id matches current chat_id
                String chatId = intent.getStringExtra("CHATID");
                Log.e("", chatId);
                if (chatId.equals(CHAT_ID)) {
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

                    //keep global counter of in app notifications
                    int global_count = getSharedPreference(getString(R.string.keys_global_chat_count));
                    putSharedPreference(getString(R.string.keys_global_chat_count), global_count + 1);

                    //keep counter for individual chatroom
                    String prefString = "chat room " + chatId + " count";
                    int chat_count = getSharedPreference(prefString);
                    putSharedPreference(prefString, chat_count + 1);

                    //TODO: update UI here for inapp notification
                    //add badge/dot to home activity tab
                    //add badges/dots to chat list

                }
            }
        }
    }



}
