package edu.uw.chitchat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.uw.chitchat.contactlist.ContactList;


/**
 * @author Yohei Sato
 *
 */
public class ContactListFragment extends Fragment implements View.OnClickListener{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private List<ContactList> mContactlist;
    private ContactList myData = new ContactList.Builder("yohei", "yohei03@uw.edu").build();
    public static final String CONTACT_LIST_LIST = "contact lists";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContactListFragment() {
        //mContactlist.add(myData);
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ContactListFragment newInstance(int columnCount) {

        ContactListFragment fragment = new ContactListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact_list, container, false);
        //mContactlist.add(myData);
        if (v instanceof RecyclerView) {
            Context context = v.getContext();
            RecyclerView recyclerView = (RecyclerView) v;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            //recyclerView.setAdapter(new MyContactRecyclerViewAdapter(mContactlist, mListener));
        }
        return v;
    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.wtf("setlist", "setlist");
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

//        if (getArguments() != null) {
//
//            mContactlist = new ArrayList<ContactList>(
//                    Arrays.asList((ContactList[]) getArguments().getSerializable(CONTACT_LIST_LIST)));
//        } else {
//
//            //mSetList = Arrays.asList(SetListGenerator.SETLISTS);
//        }
    }





    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

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

        void onListFragmentInteraction(ContactList mItem);
    }


}
