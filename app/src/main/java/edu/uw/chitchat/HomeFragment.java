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
    private OnHomeFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home , container, false);
        mCredentials = (Credentials) getArguments()
                .getSerializable(getString(R.string.keys_intent_credentials));
        ((ImageView) v.findViewById(R.id.imageView_home_logout)).setOnClickListener(this::logOut);
        return v;
    }

    public void logOut(View view) {
        mListener.onLogOut();
    }

    @Override
    public void onStart() {
        super.onStart();
        String welcome = "Welcome Back, ";
        if(mCredentials.getFirstName() == null || mCredentials.getFirstName().isEmpty()) {
            String[] emailArr = mCredentials.getEmail().split("@");
            welcome += emailArr[0] + "!";
        } else {
            welcome += mCredentials.getFirstName() + "!";
        }
        ((TextView) getActivity().findViewById(R.id.textView_home_welcome)).setText(welcome);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHomeFragmentInteractionListener) {
            mListener = (OnHomeFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnHomeFragmentInteractionListener {
        void onLogOut();
    }

}
