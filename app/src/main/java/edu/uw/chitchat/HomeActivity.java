package edu.uw.chitchat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

import edu.uw.chitchat.Credentials.Credentials;
import edu.uw.chitchat.chat.Chat;

public class HomeActivity extends AppCompatActivity implements
        TabLayout.OnTabSelectedListener,
        ChatFragment.OnChatFragmentInteractionListener,
        HomeFragment.OnHomeFragmentInteractionListener,
        ResetFragment.OnResetFragmentInteractionListener {

    private Credentials mCredentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mCredentials = (Credentials) getIntent()
                .getSerializableExtra(getString(R.string.keys_intent_credentials));

        goToHome();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(this);
    }

    @Override
    public void onLogOut() {
        //putSharedPreference(getString(R.string.keys_persistent_login), "false");
        putSharedPreference(getString(R.string.keys_persistent_login), "false");

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        //finish();
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
        Chat[] chats = {new Chat("Charles", "now", "This is the best app I've ever seen! You get a 4.0."),
                new Chat("Marquez", "yesterday", "Hey man"),
                new Chat("Lara", "2/10/2019", "Whats up"),
                new Chat("Brenna", "2/7/2019", "Wow this is really cool"),
                new Chat("Joe", "2/1/2019", ":)"),
                new Chat("Amir", "1/28/2019", "Logan is the best"),
                new Chat("Yohei", "1/28/2019", "Testing 123"),
                new Chat("Hannah", "1/28/2019", "Cool app dude!"),
                new Chat("Delvin", "1/28/2019", "Hows it going"),
        };
        ChatFragment chatFragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putSerializable(ChatFragment.ARG_CHAT_LIST, chats);
        chatFragment.setArguments(args);
        changeTab(chatFragment).commit();
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
        Toast.makeText(getBaseContext(),
                "Display Conversation with " + item.getName(), Toast.LENGTH_SHORT).show();
    }


}
