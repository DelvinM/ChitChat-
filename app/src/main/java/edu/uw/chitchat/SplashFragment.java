package edu.uw.chitchat;


import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class SplashFragment extends Fragment {

    public SplashFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        ObjectAnimator animation = ObjectAnimator.ofFloat(
                getActivity().findViewById(R.id.editText_fragment_login_username),
                "translationX", 100f);
        animation.setDuration(2000);
        animation.start();
    }
}
