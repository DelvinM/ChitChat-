package edu.uw.chitchat;

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

import edu.uw.chitchat.Credentials.Credentials;
import edu.uw.chitchat.chat.Chat;

public class HomeActivity extends AppCompatActivity implements
        TabLayout.OnTabSelectedListener,
        ChatFragment.OnChatFragmentInteractionListener,
        HomeFragment.OnHomeFragmentInteractionListener {

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
        //TODO: Implement Logout
        Log.d("Logan", "Logout Button Pressed");
    }

    public void changeTab(Fragment f) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_home, f)
                .commit();
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
        changeTab(chatFragment);
    }

    public void goToHome() {
        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable(getString(R.string.keys_intent_credentials), mCredentials);
        homeFragment.setArguments(args);
        changeTab(homeFragment);
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
                changeTab(new ConnectFragment());
                break;
            case 3: //Weather
                changeTab(new WeatherFragment());
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
