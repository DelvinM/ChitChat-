package edu.uw.chitchat;

import android.app.Activity;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import edu.uw.chitchat.Credentials.Credentials;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener, RegisterFragment.OnFragmentInteractionListener {

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
    public void onLoginFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    public void onRegisterSuccess(Credentials a) {

    }

    @Override
    public void onWaitFragmentInteractionShow() {

    }

    @Override
    public void onLoginSuccess(Credentials mCredentials, String string) {

    }

    @Override
    public void onWaitFragmentInteractionHide() {

    }
}
