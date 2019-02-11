package edu.uw.chitchat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.io.Serializable;

import edu.uw.chitchat.Credentials.Credentials;

public class MainActivity extends AppCompatActivity implements
        LoginFragment.OnLoginFragmentInteractionListener,
        RegisterFragment.OnRegisterFragmentInteractionListener {

    private static final int SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Credentials credentials = getAllCredentialsPref();
        String email = credentials.getEmail();
        String password = credentials.getPassword();
        String jwt = getString(R.string.keys_intent_jwt);

        //persistant login. If username and password are not empty
        if (!email.isEmpty() && !password.isEmpty() && !jwt.isEmpty()) {

//uncomment once logout button is implemented
/*          Intent i = new Intent(this, HomeActivity.class);
            i.putExtra(getString(R.string.keys_intent_credentials), (Serializable) credentials);
            i.putExtra(getString(R.string.keys_intent_jwt), jwt);
            startActivity(i);
            finish();
*/
            //if sharedpref is corrupt, send user back to login screen
            //could do asynctask for check?

            setUpLoginScreen();//comment this out later after logout button works
        } else {
            setUpLoginScreen();
        }
    }

    private void setUpLoginScreen() {
        this.setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_main_container, new SplashFragment())
                .commit();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_main_container, new LoginFragment())
                        .setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out)
                        .commit();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    public void onRegisterClicked() {
        RegisterFragment registerfragment;
        registerfragment = new RegisterFragment();
        Bundle args = new Bundle();
        //args.putSerializable("key", null);
        // args.putSerializable(getString(R.string.all_Phish_key));
        registerfragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_main_container, registerfragment)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();

    }

    @Override
    public void onRegisterSuccess(Credentials credentials) {

        //put to shared pref here
        putAllCredentialsToPref(credentials);

        Bundle args = new Bundle();
        LoginFragment loginfragment;
        loginfragment = new LoginFragment();

        args.putSerializable(getString(R.string.keys_email_stored_onRegister), credentials.getEmail());
        args.putSerializable(getString(R.string.keys_password_stored_onRegister), credentials.getPassword());
        //refactor all these are not used
        args.putSerializable(getString(R.string.keys_password_stored_onRegister), credentials.getRePassword());
        args.putSerializable(getString(R.string.keys_username_stored_onRegister), credentials.getUsername());
        args.putSerializable(getString(R.string.keys_firstname_stored_onRegister),credentials.getFirstName());
        args.putSerializable(getString(R.string.keys_lastname_stored_onRegister),credentials.getLastName());
        loginfragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_main_container, loginfragment)
                .addToBackStack(null);
        transaction.commit();
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

    //gets shared pref from given key
    //refactor later make this a class
    private String getSharedPreference (String key) {
        SharedPreferences sharedPref =
                getSharedPreferences(key, Context.MODE_PRIVATE);
        return sharedPref.getString(key, null);
    }

    //adds all values from credentials to shared preferences
    //refactor
    private void putAllCredentialsToPref (Credentials credentials) {
        putSharedPreference(getString(R.string.keys_email_stored_onRegister), credentials.getEmail());
        putSharedPreference(getString(R.string.keys_password_stored_onRegister), credentials.getPassword());
        putSharedPreference(getString(R.string.keys_username_stored_onRegister), credentials.getUsername());
        putSharedPreference(getString(R.string.keys_firstname_stored_onRegister), credentials.getFirstName());
        putSharedPreference(getString(R.string.keys_lastname_stored_onRegister), credentials.getLastName());
        putSharedPreference(getString(R.string.keys_repassword_stored_onRegister), credentials.getRePassword());
    }

    //returns credentials from shared pref
    //refactor
    private Credentials getAllCredentialsPref() {
        //retrieve values from shared pref
        String email = getSharedPreference (getString(R.string.keys_email_stored_onRegister));
        String password = getSharedPreference (getString(R.string.keys_password_stored_onRegister));
        String username = getSharedPreference (getString(R.string.keys_username_stored_onRegister));
        String first = getSharedPreference (getString(R.string.keys_firstname_stored_onRegister));
        String last = getSharedPreference (getString(R.string.keys_lastname_stored_onRegister));
        String repassword = getSharedPreference (getString(R.string.keys_repassword_stored_onRegister));

        //build credentials
        Credentials credentials =
                new Credentials.Builder(email, password, repassword, username, first, last)
                .build();

        return credentials;
    }

    @Override
    public void onLoginSuccess(Credentials credentials, String jwt) {
        if (findViewById(R.id.frame_main_container) != null) {

            //add credentials for most recent user
            putAllCredentialsToPref(credentials);
            putSharedPreference(getString(R.string.keys_intent_jwt), jwt);

            //load chat screen activity from here
            //attach any intent(s) needed here
            loadActivityWithCredentials(HomeActivity.class, credentials, jwt);
        }
    }

    //loads activity with intent
    //refactor? class?
    private void loadActivityWithCredentials(Class activity, Credentials credentials, String jwt) {
        Intent i = new Intent(this, activity);
        i.putExtra(getString(R.string.keys_intent_credentials), (Serializable) credentials);
        i.putExtra(getString(R.string.keys_intent_jwt), jwt);
        startActivity(i);
        finish();
    }

    @Override
    public void onWaitFragmentInteractionShow() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_main_container, new WaitFragment(), "WAIT")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onWaitFragmentInteractionHide() {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(getSupportFragmentManager().findFragmentByTag("WAIT"))
                .commit();
    }
}
