package edu.uw.chitchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.uw.chitchat.ConnectionRequestList.ConnectionRequestList;
import edu.uw.chitchat.Credentials.Credentials;
import edu.uw.chitchat.chat.Chat;
import edu.uw.chitchat.contactlist.ContactList;
import edu.uw.chitchat.utils.LoadHistoryAsyncTask;
import edu.uw.chitchat.utils.PushReceiver;
import edu.uw.chitchat.utils.SendPostAsyncTask;
import me.pushy.sdk.Pushy;

public class HomeActivity extends AppCompatActivity implements
        TabLayout.OnTabSelectedListener,
        ChatFragment.OnChatFragmentInteractionListener,
        HomeFragment.OnHomeFragmentInteractionListener,
        ResetFragment.OnResetFragmentInteractionListener,
        ConnectFragment.OnFragmentInteractionListener,
        ContactListFragment.OnListFragmentInteractionListener,
        AddContactFragment.OnAddContactFragmentInteractionListener,
        ConnectionSendRequestListFragment.OnListFragmentInteractionListener,
        ConnectionReceiveRequestListFragment.OnListFragmentInteractionListener{

    private Credentials mCredentials;
    private String mJwToken;
    private String mChatId;
    private ConnectionRequestList[] sendRequestAsArray = null;
    private ConnectionRequestList[] receiveRequestAsArray = null;
    private ArrayList<String> mChatIds;
    private Chat[] mChats;
    private String mMessage;
    private String mSender;
    private MyChatRecyclerViewAdapter mChatAdapter;
    private String mEmail;

    private PushMessageReceiver mPushMessageReciever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (getIntPreference(getString(R.string.keys_global_chat_count)) == 0) {
            findViewById(R.id.imageView_home_chatNotification).setVisibility(View.GONE);
        }

        if (getIntPreference(getString(R.string.keys_global_connection_count)) == 0) {
            findViewById(R.id.imageView_home_connectNotification).setVisibility(View.GONE);
        }

        mJwToken = getIntent().getStringExtra(getString(R.string.keys_intent_jwt));
        mCredentials = (Credentials) getIntent()
                .getSerializableExtra(getString(R.string.keys_intent_credentials));
        mChatId = getIntent().getStringExtra(getString(R.string.keys_intent_current_chat_id));
        mMessage = getIntent().getStringExtra(getString(R.string.keys_intent_current_message));
        mSender = getIntent().getStringExtra(getString(R.string.keys_intent_current_sender));
        mEmail = mCredentials.getEmail();

        //go to full chat fragment or notifications list if entry point is notification.
        //else load home fragment
        if (getIntent().getBooleanExtra(getString(R.string.keys_intent_notification_msg), false)) {

            if (Patterns.EMAIL_ADDRESS.matcher(mChatId).matches()) { //chatId contains email, so load notifications list
                goToNotificationList();
            } else { //chatroom case
                goToFullChat();
            }
        } else {
            goToHome();
        }

        getIds(false, false);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(this);
    }

    @Override
    public void onLogOut() {

        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //remove the saved credentials from StoredPrefs
        prefs.edit().remove(getString(R.string.keys_prefs_password)).apply();
        prefs.edit().remove(getString(R.string.keys_prefs_email)).apply();

        putSharedPreference(getString(R.string.keys_persistent_login), "false");
        //putSharedPreference(getString(R.string.keys_persistent_login), "false");
        new DeleteTokenAsyncTask().execute();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    //adds single value to shared preferences
    //refactor later make this a class
    private void putSharedPreference (String key, String value) {
        SharedPreferences sharedPref = getSharedPreferences(
                key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    @Override
    public void onResetClicked() {
        //TODO: Implement Reset Password
        Log.d("Logan", "Reset Password Button Pressed");
        changeTab(new ResetFragment(), "RESET").addToBackStack(null).commit();
        findViewById(R.id.appbar).setVisibility(View.GONE);
    }

    @Override
    public void onPasswordUpdate() {
        Log.d("Logan", "Password Updated");

        showTabs();
    }

    @Override
    public void onResetCancel() {
        showTabs();
    }

    @Override
    public void onWaitFragmentInteractionHide() {

    }


    public void showTabs() {
        ((TabLayout) findViewById(R.id.tabs)).getTabAt(0).select();
        findViewById(R.id.appbar).setVisibility(View.VISIBLE);
    }

    /*
     * changeTab compacts the code for fragment swapping.
     * Usage is changeTab(fragment).commit();
     * Alternatively, changeTab(fragment).addToBackStack(null).commit();
     * @param f is the fragment of the tab to swap to
     * @return the fragment transaction for committing.
     * @author Logan Jenny
     */
    public FragmentTransaction changeTab(Fragment f, String tag) {
        findViewById(R.id.floatingActionButton_newChat).setVisibility(View.GONE);
        return getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_home, f, tag);
    }

    public void getIds(Boolean manualSelected, Boolean reloadFlag) {
        String getAllIdsUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_chatroom_base))
                .appendPath(getString(R.string.ep_chatroom_getall_ids))
                .build()
                .toString();
        JSONObject getJson = new JSONObject();
        try { getJson.put("email", mCredentials.getEmail()); }
        catch (JSONException e) { Log.e("LOGAN", "cantput"); }
        new SendPostAsyncTask.Builder(getAllIdsUrl, getJson)
                .onPostExecute(result -> {
                    mChatIds = endOfDoGetIds(result);
                    goToChat(manualSelected, reloadFlag);
                })
                .onCancelled(error -> Log.e("", error))
                .addHeaderField("authorization", mJwToken)
                .build().execute();
    }
    private ArrayList<String> endOfDoGetIds(final String result) {
        ArrayList<String> formattedChatIds = new ArrayList<>();
        try {
            JSONObject res = new JSONObject(result);
            if (res.has("ids")) {
                String ids = res.getString("ids");
                String currString = "";
                int colonCount = 0;
                for (int i = 0; i < ids.length(); i++) {
                    if (ids.charAt(i) == ':') {
                        colonCount++;
                    } else if (ids.charAt(i) == '}') {
                        formattedChatIds.add(currString);
                        colonCount = 0;
                        currString = "";
                    } else if (colonCount == 1) {
                        currString += ids.charAt(i);
                    }
                }
            } else {
                Log.e("LOGAN", "No Chatrooms Found For User");
            }
        } catch (Exception e) {
            Log.e("LOGAN", "Catch: endOfDoGetIds");
            e.printStackTrace();
        }
        return formattedChatIds;
    }

    public void goToChat(Boolean manualAccess, Boolean reloadFlag) {

        findViewById(R.id.imageView_home_chatNotification).setVisibility(View.GONE);
        //putIntPreference(getString(R.string.keys_global_chat_count), 0);

        String getAllUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_chatroom_base))
                .appendPath(getString(R.string.ep_chatroom_getall_messages))
                .build()
                .toString();
        String getMembersUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_chatroom_base))
                .appendPath(getString(R.string.ep_chatroom_getall_members))
                .build()
                .toString();

        ChatFragment chatFragment = new ChatFragment();
        Bundle args = new Bundle();
        if(manualAccess && mChats != null && reloadFlag == false) {
            args.putSerializable(ChatFragment.ARG_CHAT_LIST, mChats);
            args.putSerializable("credentials", mCredentials);
            chatFragment.setArguments(args);
            changeTab(chatFragment, "CHAT").commit();
            findViewById(R.id.floatingActionButton_newChat).setVisibility(View.VISIBLE);
        }
        new LoadHistoryAsyncTask.Builder(getAllUrl, getMembersUrl, mChatIds, mChatAdapter, this.getBaseContext())
                .onPostExecute( result -> {
                    if(manualAccess) {
                        args.putSerializable(ChatFragment.ARG_CHAT_LIST, result);
                        args.putSerializable("credentials", mCredentials);
                        chatFragment.setArguments(args);
                        changeTab(chatFragment, "CHAT").commit();
                        findViewById(R.id.floatingActionButton_newChat).setVisibility(View.VISIBLE);
                    }
                    mChats = result;
                })
                .addHeaderField("authorization", mJwToken)
                .build().execute();
    }

    public void goToNotificationList () {

        findViewById(R.id.imageView_home_connectNotification).setVisibility(View.GONE);
        //reset global connection count since user is viewing requests now
        putIntPreference(getString(R.string.keys_global_connection_count), 0);

        //TODO: once yohei creates notification's list, call it from here
        ConnectionReceiveRequestListFragment userProfileFragment = new ConnectionReceiveRequestListFragment();
        Bundle args = new Bundle();
        args.putString("chatId", mChatId);
        args.putString("email", mEmail);
        args.putString("message", mMessage);
        args.putString("sender", mSender);
        userProfileFragment.setArguments(args);
        //findViewById(R.id.appbar).setVisibility(View.GONE);
        changeTab(userProfileFragment, "USER_PROFILE").addToBackStack(null).commit();
    }


    public void goToFullChat() {
        //TODO: update to enter correct chat... currently static so doesn't matter
        FullChatFragment fullChatFragment = new FullChatFragment();
        Bundle args = new Bundle();
        args.putString("chatId", mChatId);
        args.putString("email", mCredentials.getEmail());
        args.putString("jwt", mJwToken);
        fullChatFragment.setArguments(args);
        //findViewById(R.id.appbar).setVisibility(View.GONE);

        //TODO:CHANGE TAB TO CHAT SO LOOKS VISUALLY BETTER
        changeTab(fullChatFragment, "FULL_CHAT").addToBackStack(null).commit();
    }

    public void goToHome() {
        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable(getString(R.string.keys_intent_credentials), mCredentials);
        homeFragment.setArguments(args);
        changeTab(homeFragment, "HOME").commit();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        findViewById(R.id.floatingActionButton_newChat).setVisibility(View.GONE);
        switch(tab.getPosition()) {
            case 0: //Home
                goToHome();
                break;
            case 1: //Chat
                goToChat(true, false);
                break;
            case 2: //Connect
                changeTab(new ConnectFragment(), "CONNECT").commit();
                break;
            case 3: //Weather
                changeTab(new WeatherFragment(), "CONNECT").commit();
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) { }

    @Override
    public void onTabReselected(TabLayout.Tab tab) { onTabSelected(tab); }

    @Override
    public void onChatFragmentInteraction(Chat item) {
        FullChatFragment fullChatFragment = new FullChatFragment();
        Bundle args = new Bundle();
        args.putString("chatId", item.getChatId());
        args.putStringArrayList("contents", item.getContents());
        args.putString("email", mCredentials.getEmail());
        args.putString("jwt", mJwToken);
        fullChatFragment.setArguments(args);
        //findViewById(R.id.appbar).setVisibility(View.GONE);
        changeTab(fullChatFragment, "FULL_CHAT").addToBackStack(null).commit();
    }

    @Override
    public void onReloadChatFragment(MyChatRecyclerViewAdapter adapter) {
        mChatAdapter = adapter;
        getIds(true, true);
    }


    @Override
    public void onContactListClicked() {

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_connection_base))
                .appendPath(getString(R.string.ep_connection_getall))
                .build();
        String email = mCredentials.getEmail();

        JSONObject jsonSend = new JSONObject();
        try {
            jsonSend.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        new SendPostAsyncTask.Builder(uri.toString(), jsonSend)
                .onPostExecute(this::handleContactlistGetOnPostExecute)
                .addHeaderField("authorization", mJwToken)
                .build().execute();

    }



    @Override
    public void onConnectionRequestListClicked() {

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_connection))
                .appendPath(getString(R.string.ep_getRequestList))
                .build();
        String email = mCredentials.getEmail();
        JSONObject jsonSend = new JSONObject();
        try {
            jsonSend.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        new SendPostAsyncTask.Builder(uri.toString(), jsonSend)
                .onPostExecute(this::handleConnectionSendRequestslistGetOnPostExecute )
                .addHeaderField("authorization", mJwToken)
                .build().execute();
    }

    @Override
    public void onConnectionReceiveRequestListClicked() {
        Log.wtf("test the second receive last if", "thest the second last if");
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_connection))
                .appendPath(getString(R.string.ep_getRequestList))
                .build();
        String email = mCredentials.getEmail();
        JSONObject jsonSend = new JSONObject();
        try {
            jsonSend.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        new SendPostAsyncTask.Builder(uri.toString(), jsonSend)
                .onPostExecute(this::handleConnectionReceiveRequestslistGetOnPostExecute )
                .addHeaderField("authorization", mJwToken)
                .build().execute();
    }

    private void handleErrorsInTask(String result) {
            Log.e("ASYNC_TASK_ERROR", result);
    }

    private void handleAcceptGetOnPostExecute(String result){
        try {
            JSONObject root = new JSONObject(result);
            if (root.has(getString(R.string.keys_json_contactlist_response))) {
                JSONArray response = root.getJSONArray(getString(R.string.keys_json_contactlist_response));
                List<ContactList> contacts = new ArrayList<>();
                for(int i = 0; i < response.length(); i++) {
                    JSONObject jsonContact = response.getJSONObject(i);
                    contacts.add(new ContactList.Builder(
                            jsonContact.getString(getString(R.string.keys_json_contactlist_username)),
                            jsonContact.getString(getString(R.string.keys_json_contactlist_email)))
                            .build());
                }

            } else {
                Log.e("ERROR!", "No response");
                //notify user
                onWaitFragmentInteractionHide();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            //onWaitFragmentInteractionHide();
        }

    }

    private void handleContactlistGetOnPostExecute(String result) {
        try {
            JSONObject root = new JSONObject(result);
            if (root.has(getString(R.string.keys_json_contactlist_response))) {
                JSONArray response = root.getJSONArray(getString(R.string.keys_json_contactlist_response));
                List<ContactList> contacts = new ArrayList<>();
                for(int i = 0; i < response.length(); i++) {
                    JSONObject jsonContact = response.getJSONObject(i);
                    contacts.add(new ContactList.Builder(
                            jsonContact.getString(getString(R.string.keys_json_contactlist_username)),
                            jsonContact.getString(getString(R.string.keys_json_contactlist_email)))
                            .build());
                }

                ContactList[] contactsAsArray = new ContactList[contacts.size()];
                contactsAsArray = contacts.toArray(contactsAsArray);
                Bundle args = new Bundle();
                args.putSerializable("contact lists", contactsAsArray);
                ContactListFragment frag = new ContactListFragment();
                frag.setArguments(args);
                onWaitFragmentInteractionHide();
                changeTab(frag, "CONTACT_LIST").commit();
            } else {
                Log.e("ERROR!", "No response");
                //notify user
                onWaitFragmentInteractionHide();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            //onWaitFragmentInteractionHide();
        }
    }


    public void handleConnectionSendRequestslistGetOnPostExecute(String result) {

        try {
            JSONObject root = new JSONObject(result);
            if (root.has(getString(R.string.keys_json_sending_request))) {
                JSONArray response = root.getJSONArray(getString(R.string.keys_json_sending_request));
                List<ConnectionRequestList> sendrequest = new ArrayList<>();
                for(int i = 0; i < response.length(); i++) {
                    JSONObject jsonContact = response.getJSONObject(i);
                    sendrequest.add(new ConnectionRequestList.Builder(
                            jsonContact.getString(getString(R.string.keys_json_contactlist_username)),
                            jsonContact.getString(getString(R.string.keys_json_contactlist_email)))
                            .build());
                }
                Log.wtf("sendtest","sendtest");
                sendRequestAsArray = new ConnectionRequestList[sendrequest.size()];
                sendRequestAsArray = sendrequest.toArray(sendRequestAsArray);
                Bundle args = new Bundle();
                args.putSerializable("sending requests lists", sendRequestAsArray);
                //args.putSerializable("receiving requests lists", receiveRequestAsArray);
                ConnectionSendRequestListFragment frag = new ConnectionSendRequestListFragment();
                frag.setArguments(args);
                onWaitFragmentInteractionHide();
                changeTab(frag, "CONNECTION_SEND").commit();
            }


            else {
                Log.e("ERROR!", "No response");
                //notify user
                onWaitFragmentInteractionHide();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            //onWaitFragmentInteractionHide();
        }
    }

    public void handleConnectionReceiveRequestslistGetOnPostExecute(String result) {
        Log.wtf("test the second receive last if", "thest the second last if");
        try {
            JSONObject root = new JSONObject(result);
            if (root.has(getString(R.string.keys_json_receiving_request))) {
                JSONArray response = root.getJSONArray(getString(R.string.keys_json_receiving_request));
                List<ConnectionRequestList> receiverequest = new ArrayList<>();
                for(int i = 0; i < response.length(); i++) {
                    JSONObject jsonContact = response.getJSONObject(i);
                    receiverequest.add(new ConnectionRequestList.Builder(
                            jsonContact.getString(getString(R.string.keys_json_contactlist_username)),
                            jsonContact.getString(getString(R.string.keys_json_contactlist_email)))
                            .build());
                }
                Log.wtf("recievetest","recievetest");
                receiveRequestAsArray = new ConnectionRequestList[receiverequest.size()];
                receiveRequestAsArray = receiverequest.toArray(receiveRequestAsArray);
                Bundle args = new Bundle();
                args.putSerializable("receiving requests lists", receiveRequestAsArray);
                //args.putSerializable("receiving requests lists", receiveRequestAsArray);
                ConnectionReceiveRequestListFragment frag = new ConnectionReceiveRequestListFragment();
                frag.setArguments(args);
                onWaitFragmentInteractionHide();
                changeTab(frag, "CONNECTION_RECEIVE").commit();
            }

            else {
                Log.e("ERROR!", "No response");
                //notify user
                onWaitFragmentInteractionHide();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            //onWaitFragmentInteractionHide();
        }
    }


    public String getMyEmail(){
        return mCredentials.getEmail();
    }


    @Override
    public void onAddContactClicked() {
        AddContactFragment addContactFragment = new AddContactFragment();
        Bundle args = new Bundle();
        args.putString("email", mCredentials.getEmail());
        addContactFragment.setArguments(args);
        changeTab(addContactFragment, "CONTACT").addToBackStack(null).commit();
    }

    @Override
    public void onListFragmentInteraction(ContactList mItem) {

    }

    private String getSharedPreference (String key) {
        SharedPreferences sharedPref =
                getSharedPreferences(key, Context.MODE_PRIVATE);
        return sharedPref.getString(key, null);
    }


    @Override
    public void onContactListFragmentInteraction(ContactList contact) {

    }

    @Override
    public void onConnectionSendRequestListFragmentInteraction(ConnectionRequestList item) {

    }

    @Override
    public void onConnectionReceiveRequestListFragmentInteraction(ConnectionRequestList item) {

    }

    @Override
    public String getEmail() {
        return mCredentials.getEmail();
    }

    // Deleting the Pushy device token must be done asynchronously. Good thing
    // we have something that allows us to do that.
    class DeleteTokenAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected Void doInBackground(Void... voids) {
            //since we are already doing stuff in the background, go ahead
            //and remove the credentials from shared prefs here.
            SharedPreferences prefs =
                    getSharedPreferences(
                            getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);
            prefs.edit().remove(getString(R.string.keys_prefs_password)).apply();
            prefs.edit().remove(getString(R.string.keys_prefs_email)).apply();
            //unregister the device from the Pushy servers
            Pushy.unregister(HomeActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //close the app
            finishAndRemoveTask();
            //or close this activity and bring back the Login
        // Intent i = new Intent(this, MainActivity.class);
        // startActivity(i);
        // //Ends this Activity and removes it from the Activity back stack.
        // finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPushMessageReciever == null) {
            mPushMessageReciever = new PushMessageReceiver();
        }
        IntentFilter iFilter = new IntentFilter(PushReceiver.RECEIVED_NEW_MESSAGE);
        this.registerReceiver(mPushMessageReciever, iFilter);
    }
    @Override
    public void onPause() {
        super.onPause();
        if (mPushMessageReciever != null){
            this.unregisterReceiver(mPushMessageReciever);
        }
    }

    //TODO:REFACTOR
    private int getIntPreference (String key) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getInt(key, 0);
    }

    //TODO: REFACTOR
    //adds single value to shared preferences
    //refactor later make this a class
    private void putIntPreference (String key, int value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
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
                //chat id contains users email
                String chatId = intent.getStringExtra("CHATID");

                if (Patterns.EMAIL_ADDRESS.matcher(chatId).matches()) { // increase connection request global counter
                    if(chatId.equals(mEmail)) {

                        findViewById(R.id.imageView_home_connectNotification).setVisibility(View.VISIBLE);

                        int global_count = getIntPreference(getString(R.string.keys_global_connection_count));
                        putIntPreference(getString(R.string.keys_global_connection_count), global_count + 1);

                        //TODO: make icon light up or something
                    }
                } else { // increase chat room global counter

                    findViewById(R.id.imageView_home_chatNotification).setVisibility(View.VISIBLE);

                    int global_count = getIntPreference(getString(R.string.keys_global_chat_count));
                    putIntPreference(getString(R.string.keys_global_chat_count), global_count + 1);

                    //keep counter for individual chatroom
                    String prefString = "chat room " + chatId + " count";
                    int chat_count = getIntPreference(prefString);
                    putIntPreference(prefString, chat_count + 1);

                    //TODO: make icon light up or something
                }
            }
        }
    }

}
