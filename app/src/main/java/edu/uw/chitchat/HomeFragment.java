package edu.uw.chitchat;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import edu.uw.chitchat.Credentials.Credentials;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * @author Logan Jenny
 * @2/5/2018
 */

public class HomeFragment extends Fragment {

    private Credentials mCredentials;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home , container, false);
        mCredentials = (Credentials) getArguments()
                .getSerializable(getString(R.string.keys_intent_credentials));
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        String welcome = "Welcome Back, ";
        String[] emailArr = mCredentials.getEmail().split("@");
        welcome += emailArr[0] + "!";
        if(((TextView) getActivity().findViewById(R.id.textView_home_welcome)) == null) {
            Log.d("--------Logan--------", "NULL");
        }
        ((TextView) getActivity().findViewById(R.id.textView_home_welcome)).setText(welcome);
    }

}
