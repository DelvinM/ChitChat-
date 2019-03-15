package edu.uw.chitchat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.uw.chitchat.chat.Chat;
import edu.uw.chitchat.utils.PrefHelper;
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
    private String mGetUrl;
    private ArrayList<String> mContents;
    private OnFullChatFragmentInteractionListener mListener;
    private PushMessageReceiver mPushMessageReciever;
    private NestedScrollView mScrollView;


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
        mScrollView = v.findViewById(R.id.scrollview_chat);
        ((ImageButton) v.findViewById(R.id.button_chat_send)).setOnClickListener(this::handleSendClick);
        ((ImageButton) v.findViewById(R.id.button_chat_addmember)).setOnClickListener(this::selectMember);

        if (getArguments() != null) {
            mContents = getArguments().getStringArrayList("contents");
            Log.e("LOGAN", "Showing Persistent Messages!");
            for (int j = mContents.size() - 1; j >= 0; j--) {
                mMessageOutputTextView.append(mContents.get(j));
                mMessageOutputTextView.append(System.lineSeparator());
                mMessageOutputTextView.append(System.lineSeparator());
            }
        }

        scrollDown();

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFullChatFragmentInteractionListener) {
            mListener = (OnFullChatFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            //get the email and JWT from the Activity. Make sure the Keys match what you used
            mEmail = getArguments().getString("email");
            mJwToken = getArguments().getString("jwt");
            CHAT_ID = getArguments().getString("chatId");
            if(getArguments().getString("memberToAdd") != null && !getArguments().getString("memberToAdd").equals("")) {
                handleAddMember(getArguments().getString("memberToAdd"));
            }
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
        mGetUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_chatroom_base))
                .appendPath(getString(R.string.ep_chatroom_getall_messages))
                .build()
                .toString();

        updateMessages();
    }

    /**
     * Runs an asyncTask to retrieve all message data for the given chat and update it
     * @author Logan Jenny
     */
    private void updateMessages() {
        JSONObject getJson = new JSONObject();
        try {getJson.put("chatId", CHAT_ID);
        } catch (JSONException e) {e.printStackTrace();}
        new SendPostAsyncTask.Builder(mGetUrl, getJson)
            .onPostExecute(result -> {
                final ArrayList<String> contents = endOfDoGetAll(result);
                if(contents.size() > 2) {
                    Log.e("LOGAN", "Updated Messages!");
                    contents.remove(contents.size() - 1);
                    contents.remove(contents.size() - 1);
                    mMessageOutputTextView.setText("");
                    for (int j = contents.size() - 1; j >= 0; j--) {
                        mMessageOutputTextView.append(contents.get(j));
                        mMessageOutputTextView.append(System.lineSeparator());
                        mMessageOutputTextView.append(System.lineSeparator());
                    }
                }
            })
            .onCancelled(error -> Log.e("UPDATEMESSAGES", "Problem"))
            .addHeaderField("authorization", mJwToken)
            .build().execute();
    }

    /**
     * Parses the result string returned by the backend and breaks it into messages
     * @author Logan Jenny
     * @param result the result string to be parsed
     * @return the ArrayList of messages in a given result string
     */
    private ArrayList<String> endOfDoGetAll(final String result) {
        ArrayList<String> formattedMessages = new ArrayList<String>();
        String mostRecent = "";
        String members = "";
        Set<String> memberSet = new HashSet<String>();
        boolean mostRecentRecorded = false;
        try {
            JSONObject res = new JSONObject(result);
            if(res.has("messages")) {
                String messages = res.getString("messages");
                String currString = "";
                String currMember = "";
                int count = 1;
                int quoteCount = 0;
                for (int i = 0; i < messages.length(); i++) {
                    if (count == 1) {
                        if (messages.charAt(i) == '@') {
                            count++;
                            quoteCount = 0;
                            currString += ": ";
                        } else if (messages.charAt(i) == '"') {
                            quoteCount++;
                        } else if (quoteCount == 3) {
                            currString += messages.charAt(i);
                        }
                    } else if (count == 2) {
                        if (messages.charAt(i) == '"') {
                            quoteCount++;
                            if (quoteCount == 5) {
                                count++;
                                quoteCount = 0;
                            }
                        } else if (quoteCount == 4) {
                            currString += messages.charAt(i);
                        }
                    } else if (count == 3) {
                        if (messages.charAt(i) == '"') {
                            quoteCount++;
                            if (quoteCount == 4) {
                                count = 1;
                                quoteCount = 0;
                                currMember = currString.split(":")[0];
                                if(!memberSet.contains(currMember)) {
                                    memberSet.add(currMember);
                                    members += currMember + " ";
                                }
                                formattedMessages.add(currString);
                                mostRecentRecorded = true;
                                currString = "";
                            }
                        } else if (quoteCount == 3 && !mostRecentRecorded) {
                            mostRecent += messages.charAt(i);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        formattedMessages.add(members);
        formattedMessages.add(mostRecent.split(" ")[0]);
        return formattedMessages;
    }

    /**
     * Auto-scrolls the messages to the bottom
     * @author Logan Jenny
     */
    private void scrollDown() {
        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    /**
     * Removes the chat notification count
     * @author Logan Jenny
     */
    private void removeChatNotificationCount () {
        //accesses shared pref and removes chat notification count
        //TODO: this could use a local database. refactor? (delvin)

        String prefString = "chat room " + CHAT_ID + " count";

        //compute new global count
        int notification_count_chat = PrefHelper.getIntPreference(prefString, this.getActivity());
        int notification_count_global = PrefHelper.getIntPreference(getString(R.string.keys_global_chat_count), this.getActivity());
        int global_count = notification_count_global - notification_count_chat;

        PrefHelper.putIntPreference(prefString, 0, this.getActivity());
        PrefHelper.putIntPreference(getString(R.string.keys_global_chat_count), global_count, this.getActivity());
    }

    //TODO: REFACTOR
    //adds single value to shared preferences
    //refactor later make this a class
    //
    //
    //I left these in when merging, delete putSharedPreference and getSharedPreference if this crashes - Logan
    //
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

    /**
     * Tells the homeActivity to return to the chat fragment having added the member
     * @author Logan Jenny
     * @param view is the 'add' button of a given member
     */
    private void selectMember(View view) {
        mListener.onAddMember((Chat) getArguments().getSerializable("item"));
    }

    /**
     * handles a request to add member by accepting their email and communicating with the backend
     * to do so
     * @author Logan Jenny
     * @param email is the email of the member to be added
     */
    public void handleAddMember(String email) {
        Log.e("LOGAN", email);
        String addMemberUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_chatroom_base))
                .appendPath(getString(R.string.ep_chatroom_addmember))
                .build()
                .toString();
        JSONObject getJson = new JSONObject();
        try {
            getJson.put("chatId", CHAT_ID);
            getJson.put("email", email);
        } catch (JSONException e) {e.printStackTrace();}
        new SendPostAsyncTask.Builder(addMemberUrl, getJson)
                .onPostExecute(result -> {
                    Log.e("LOGAN", result);
                    sendMsg("JOINED THE CHAT", email);
                } )
                .onCancelled(error -> Log.e("LOADASYNC", "Problem"))
                .addHeaderField("authorization", mJwToken)
                .build().execute();
    }

    /**
     * calls a helper method to send your message
     * @author Logan Jenny
     * @param theButton is the view of the send button
     */
    private void handleSendClick(final View theButton) {
        sendMsg(mMessageInputEditText.getText().toString(), mEmail);
    }

    /**
     * handles the functionality of sending a message to the chatroom
     * @author Logan Jenny
     * @param msg the string of the message to be sent
     * @param email the email of the sender of the message
     */
    private void sendMsg(String msg, String email) {
        scrollDown();
        JSONObject messageJson = new JSONObject();
        try {
            messageJson.put("email", email);
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

    /**
     * sets the 'enter a message' edittext to empty on message sent
     * @author Logan Jenny
     * @param result is the response from the server as a string
     */
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

                    /*//keep global counter of in app notifications
                    int global_count = getSharedPreference(getString(R.string.keys_global_chat_count));
                    putSharedPreference(getString(R.string.keys_global_chat_count), global_count + 1);

                    //keep counter for individual chatroom
                    String prefString = "chat room " + chatId + " count";
                    int chat_count = getSharedPreference(prefString);
                    putSharedPreference(prefString, chat_count + 1);*/



                }
            }
        }
    }

    public interface OnFullChatFragmentInteractionListener {
        void onAddMember(Chat item);
    }



}
