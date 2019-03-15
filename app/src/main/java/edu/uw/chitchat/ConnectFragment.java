package edu.uw.chitchat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.uw.chitchat.Credentials.Credentials;
import edu.uw.chitchat.chat.Chat;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * @author Logan Jenny
 * @author Yohei Sato
 * @2/9/2018
 */


/**
 *
 */
public class ConnectFragment extends Fragment implements View.OnClickListener{

    /**
     * This variavle saves for a credential information
     */
    private Credentials mCredentials;
    /**
     * This is interface field.
     */
    private OnFragmentInteractionListener  mListener;

    /**
     * This is a constructor
     */
    public ConnectFragment() {
        // Required empty public constructor
    }

    /**
     * This method is the method connecting between fragment and activity.
     *
     * @param context that can be connected to activity.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * This method is necessary for making visible of fragment
     *
     * @param inflater   is used in the java class of the corresponding layout(xml) file.
     * @param container  this is the container for the view function
     * @param savedInstanceState that's a bundle information
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connect, container, false);
        Button b1 = (Button) view.findViewById(R.id.button_add_contact);
        b1.setOnClickListener(this);
        Button b2 = (Button) view.findViewById(R.id.button_show_contactlist);
        b2.setOnClickListener(this);
        Button b3 = (Button) view.findViewById(R.id.button_show_receive_requestList);
        b3.setOnClickListener(this);
        Button b4 = (Button) view.findViewById(R.id.button_show_send_requestList);
        b4.setOnClickListener(this);

        return view;
    }

    /**
     * This method will be happened when a certain buttton gets cliecked
     *
     * @param view view information
     */
    @Override
    public void onClick(View view) {
        if (mListener != null) {
            //mListener.onContactListClicked();
            switch (view.getId()) {
                case R.id.button_add_contact:
                    mListener.onAddContactClicked();
                    break;
                case R.id.button_show_contactlist:
                    mListener.onContactListClicked(false, null);
                    break;
                case R.id.button_show_receive_requestList:
                    Log.wtf("test the first receive if", "thest the first receive if");
                    mListener.onConnectionReceiveRequestListClicked();
                    break;
                case R.id.button_show_send_requestList:
                    mListener.onConnectionRequestListClicked();
                    break;
                default:
                    Log.wtf("", "Didn't expect to see me...");
        }
        }
    }

    /**
     * This is the interface
     */
    public interface OnFragmentInteractionListener {
        void onContactListClicked(Boolean addMember, Chat item);
        void onConnectionRequestListClicked();
        void onAddContactClicked();
        void onConnectionReceiveRequestListClicked();
    }

}
