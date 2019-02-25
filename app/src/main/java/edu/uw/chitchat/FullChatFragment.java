package edu.uw.chitchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.ArrayList;

import edu.uw.chitchat.utils.PushReceiver;
import edu.uw.chitchat.utils.SendPostAsyncTask;

/*
 * @author Logan Jenny
// */
//public class FullChatFragment extends Fragment {
//
//    public FullChatFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_full_chat, container, false);
//    }
//}

public class FullChatFragment extends Fragment {

    private String mJwToken;
    private String mEmail;
    private static final String TAG = "CHAT_FRAG";
    private String CHAT_ID = "1";
    private TextView mMessageOutputTextView;
    private EditText mMessageInputEditText;
    private String mSendUrl;
    private String mGetAllUrl;
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
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            //get the email and JWT from the Activity. Make sure the Keys match what you used
            mEmail = getArguments().getString("email");
            mJwToken = getArguments().getString("jwt");
        }
        //We will use this url every time the user hits send. Let's only build it once, ya?
        mSendUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_messaging_base))
                .appendPath(getString(R.string.ep_messaging_send))
                .build()
                .toString();

        mGetAllUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_messaging_base))
                .appendPath(getString(R.string.ep_messaging_getall))
                .build()
                .toString();

        doGetAll();
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

    private void doGetAll() {
        Log.e("Logan", "test do get all");
        JSONObject getJson = new JSONObject();
        try {
            getJson.put("chatId", CHAT_ID);
            Log.e("Logan", "test do get all2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(mGetAllUrl, getJson)
                .onPostExecute(this::endOfDoGetAll)
                .onCancelled(error -> Log.e(TAG, error))
                .addHeaderField("authorization", mJwToken)
                .build().execute();
        Log.e("Logan", "test do get all4");
    }

    private void endOfDoGetAll(final String result) {
        Log.e("Logan", "test do get all3");
        try {
            //This is the result from the web service
            JSONObject res = new JSONObject(result);
            if(res.has("messages")) {

                String messages = res.getString("messages");
                ArrayList<String> formattedMessages = new ArrayList();
                String currString = "";
                int count = 1;
                int quoteCount = 0;
                for(int i = 0; i < messages.length(); i++) {
                    if (count == 1) {
                        if(messages.charAt(i) == '@') {
                            count++;
                            quoteCount = 0;
                            currString += ": ";
                        } else if(messages.charAt(i) == '"') {
                            quoteCount++;
                        } else if(quoteCount == 3) {
                            currString += messages.charAt(i);
                        }
                    } else if (count == 2) {
                        if(messages.charAt(i) == '"') {
                            quoteCount++;
                            if(quoteCount == 5) {
                                count++;
                                quoteCount = 0;
                            }
                        } else if (quoteCount == 4) {
                            currString += messages.charAt(i);
                        }
                    } else if (count == 3) {
                        if(messages.charAt(i) == '"') {
                            quoteCount++;
                        } else if (quoteCount == 4) {
                            count = 1;
                            quoteCount = 0;
                            formattedMessages.add(currString);
                            currString = "";
                        }
                    }
                }

                for(int j = formattedMessages.size()-1; j >= 0; j--) {
                    mMessageOutputTextView.append(formattedMessages.get(j));
                    mMessageOutputTextView.append(System.lineSeparator());
                    mMessageOutputTextView.append(System.lineSeparator());
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
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
                    //TODO: in app notification goes here
                }
            }
        }
    }



}
