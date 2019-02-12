package edu.uw.chitchat;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * @author Logan Jenny
 */
public class ResetFragment extends Fragment {

    private OnResetFragmentInteractionListener mListener;

    public ResetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reset, container, false);
        ((Button) v.findViewById(R.id.button_reset_update)).setOnClickListener(this::updatePassword);
        return v;
    }

    public void updatePassword(View view) {
        mListener.onPasswordUpdate();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnResetFragmentInteractionListener) {
            mListener = (OnResetFragmentInteractionListener) context;
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

    public interface OnResetFragmentInteractionListener {
        void onPasswordUpdate();
    }
}
