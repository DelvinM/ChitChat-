package edu.uw.chitchat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.uw.chitchat.Credentials.Credentials;
import edu.uw.chitchat.chat.Chat;
import edu.uw.chitchat.contactlist.ContactList;
import edu.uw.chitchat.utils.SendPostAsyncTask;
import me.pushy.sdk.Pushy;

public class HomeActivity extends AppCompatActivity implements
        TabLayout.OnTabSelectedListener,
        ChatFragment.OnChatFragmentInteractionListener,
        HomeFragment.OnHomeFragmentInteractionListener,
        ResetFragment.OnResetFragmentInteractionListener,
        ConnectFragment.OnFragmentInteractionListener,
        ContactListFragment.OnListFragmentInteractionListener,
        AddContactFragment.OnAddContactFragmentInteractionListener{

    private Credentials mCredentials;
    private String mJwToken;
    private String mChatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mJwToken = getIntent().getStringExtra(getString(R.string.keys_intent_jwt));
        mCredentials = (Credentials) getIntent()
                .getSerializableExtra(getString(R.string.keys_intent_credentials));
        mChatId = getIntent().getStringExtra(getString(R.string.keys_intent_current_chat_id));
        //go to full chat fragment if entry point is notification. else load home fragment
        if (getIntent().getBooleanExtra(getString(R.string.keys_intent_notification_msg), false)) {
            goToFullChat();
        } else {
            goToHome();
        }



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
        changeTab(new ResetFragment()).addToBackStack(null).commit();
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
    public FragmentTransaction changeTab(Fragment f) {
        return getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_home, f);
    }

    public void goToChat() {
        //gets current new notifications for each chat room...from sharedpref
        String notificationsCount_1 = Integer.toString(getPrefInt("chat room 1 count"));
        String notificationsCount_2 = Integer.toString(getPrefInt("chat room 2 count"));
        String notificationsCount_3 = Integer.toString(getPrefInt("chat room 3 count"));
        String notificationsCount_4 = Integer.toString(getPrefInt("chat room 4 count"));

        Chat[] chats = {new Chat("Delvin", "2/25/2019", "This is the best app I've ever seen! You get a 4.0.", "1", notificationsCount_1),
                new Chat("Logan", "2/25/2019", "Hey man", "2", notificationsCount_2),
                new Chat("Joe", "2/25/2019", "Whats up", "3", notificationsCount_3),
                new Chat("Yohei", "2/25/2019", "Wow this is really cool", "4", notificationsCount_4),
        };
        ChatFragment chatFragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putSerializable(ChatFragment.ARG_CHAT_LIST, chats);
        chatFragment.setArguments(args);
        changeTab(chatFragment).commit();
    }

    //TODO: refactor
    private int getPrefInt (String key) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getInt(key, 0);
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
        changeTab(fullChatFragment).addToBackStack(null).commit();
    }

    public void goToHome() {
        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable(getString(R.string.keys_intent_credentials), mCredentials);
        homeFragment.setArguments(args);
        changeTab(homeFragment).commit();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch(tab.getPosition()) {
            case 0: //Home
                goToHome();
                break;
            case 1: //Chat
                goToChat();
                break;
            case 2: //Connect
                changeTab(new ConnectFragment()).commit();
                break;
            case 3: //Weather
                changeTab(new WeatherFragment()).commit();
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
        args.putString("email", mCredentials.getEmail());
        args.putString("jwt", mJwToken);
        fullChatFragment.setArguments(args);
        //findViewById(R.id.appbar).setVisibility(View.GONE);
        changeTab(fullChatFragment).addToBackStack(null).commit();
//        Toast.makeText(getBaseContext(),
//                "Display Conversation with " + item.getName(), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onContactListClicked() {

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_contact))
                .appendPath(getString(R.string.ep_getall))
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

    private void handleErrorsInTask(String result) {
            Log.e("ASYNC_TASK_ERROR", result);
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
                changeTab(frag).commit();
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



    @Override
    public void onAddContactClicked() {

        changeTab(new AddContactFragment()).addToBackStack(null).commit();
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

}
