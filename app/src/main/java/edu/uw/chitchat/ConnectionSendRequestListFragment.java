package edu.uw.chitchat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.uw.chitchat.ConnectionRequestList.ConnectionRequestList;


/**
 * @author Yohei Sato
 *
 */

public class ConnectionSendRequestListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private List<ConnectionRequestList> mConnectionRequestList;
    private static final String ARG_SEND_LIST = "sending requests lists";
    //private static final String ARG_RECIEVE_LIST = "receiving requests lists";
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ConnectionSendRequestListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ConnectionSendRequestListFragment newInstance(int columnCount) {
        ConnectionSendRequestListFragment fragment = new ConnectionSendRequestListFragment();
        Bundle args = new Bundle();
        args.putInt( ARG_COLUMN_COUNT, columnCount );
        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        if (getArguments() != null) {
            mConnectionRequestList = new ArrayList<ConnectionRequestList>( Arrays.asList((ConnectionRequestList[]) getArguments().getSerializable(ARG_SEND_LIST)));
            //mConnectionRequestList2 = new ArrayList<ConnectionRequestList>( Arrays.asList((ConnectionRequestList[]) getArguments().getSerializable(ARG_RECIEVE_LIST)));
            for (ConnectionRequestList temp : mConnectionRequestList) {
                Log.d("joe", temp.getEmailAddress());
            }
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
//        if (getArguments() != null) {
//            mConnectionRequestList2 = new ArrayList<ConnectionRequestList>( Arrays.asList((ConnectionRequestList[]) getArguments().getSerializable(ARG_RECIEVE_LIST)));
//            for (ConnectionRequestList temp : mConnectionRequestList2) {
//                Log.d("yohei", temp.getEmailAddress());
//            }
//            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
//        }
//        else {
//            mConnectionRequestList = Arrays.asList( ContactListGenerator.mContacts);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_connection_send_request_list, container, false );

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager( new LinearLayoutManager( context ) );
            } else {
                recyclerView.setLayoutManager( new GridLayoutManager( context, mColumnCount ) );
            }
            recyclerView.setAdapter( new MyConnectionSendRequestsRecyclerViewAdapter(mConnectionRequestList, mListener ) );
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach( context );
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException( context.toString()
                    + " must implement OnListFragmentInteractionListener" );
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onConnectionSendRequestListFragmentInteraction(ConnectionRequestList item);
    }
}
