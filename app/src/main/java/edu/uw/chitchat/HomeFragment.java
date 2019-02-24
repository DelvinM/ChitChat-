package edu.uw.chitchat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import edu.uw.chitchat.Credentials.Credentials;
import edu.uw.chitchat.contactlist.ContactList;


/**
 * @author Logan Jenny
 * @2/5/2018
 */

public class HomeFragment extends Fragment implements PopupMenu.OnMenuItemClickListener{

    private Credentials mCredentials;
    private OnHomeFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.wtf("Yohei","test");
        View v = inflater.inflate(R.layout.fragment_home , container, false);
        mCredentials = (Credentials) getArguments()
                .getSerializable(getString(R.string.keys_intent_credentials));
        ((ImageView) v.findViewById(R.id.imageView_home_settings)).setOnClickListener(this::showSettings);
        ((ImageView) v.findViewById(R.id.imageView_home_logout)).setOnClickListener(this::logOut);
        return v;
    }

    public void showSettings(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_home);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_reset_password:
                mListener.onResetClicked();
                return true;
            default:
                return false;
        }
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
        void onResetClicked();
    }

}
