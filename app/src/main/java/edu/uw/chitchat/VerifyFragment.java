package edu.uw.chitchat;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * @author Logan Jenny
 */
public class VerifyFragment extends Fragment {

    private OnVerifyFragmentInteractionListener mListener;

    public VerifyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_verify, container, false);
        v.findViewById(R.id.imageButton_verify_next).setOnClickListener(this::nextClicked);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnVerifyFragmentInteractionListener) {
            mListener = (OnVerifyFragmentInteractionListener) context;
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

    public void nextClicked(View view) {
        mListener.onNextClicked();
    }

    public interface OnVerifyFragmentInteractionListener {
        void onNextClicked();
    }

}
