package edu.uw.chitchat;

import android.app.Activity;
import android.content.Intent;
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
        RegisterFragment.OnFragmentInteractionListener {

    private static final int SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public void onRegisterSuccess(Credentials a) {

    }

    @Override
    public void onLoginSuccess(Credentials credentials, String jwt) {
        //if (findViewById(R.id.frame_main_container) != null) {

            //load chat screen activity from here
            //attach any intent(s) needed here
            Intent i = new Intent(this, HomeActivity.class);
            i.putExtra(getString(R.string.keys_intent_credentials), (Serializable) credentials);
            i.putExtra(getString(R.string.keys_intent_jwt), jwt);
            startActivity(i);
            finish();
        //}
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
