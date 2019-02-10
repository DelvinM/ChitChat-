package edu.uw.chitchat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.uw.chitchat.Credentials.Credentials;
import edu.uw.chitchat.chat.Chat;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * @author Logan Jenny
 * @2/9/2018
 */

public class ChatFragment extends Fragment {

    private Credentials mCredentials;
    private OnChatFragmentInteractionListener mListener;
    private int mColumnCount = 1;
    public static final String ARG_CHAT_LIST = "chat list";
    private List<Chat> mChats;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mChats = new ArrayList<Chat>(
                    Arrays.asList((Chat[]) getArguments().getSerializable(ARG_CHAT_LIST)));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_chat_list, container, false);

        if (v instanceof RecyclerView) {
            Context context = v.getContext();
            RecyclerView recyclerView = (RecyclerView) v;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyChatRecyclerViewAdapter(mChats, mListener));
        }

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChatFragmentInteractionListener) {
            mListener = (OnChatFragmentInteractionListener) context;
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

    public interface OnChatFragmentInteractionListener {
        void onChatFragmentInteraction(Chat item);
    }

}
