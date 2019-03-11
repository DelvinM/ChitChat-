package edu.uw.chitchat;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.uw.chitchat.Credentials.Credentials;
import edu.uw.chitchat.chat.Chat;
import edu.uw.chitchat.utils.SendPostAsyncTask;


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
    private MyChatRecyclerViewAdapter mAdapter;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCredentials = (Credentials) getArguments().getSerializable("credentials");
            mChats = new ArrayList<Chat>(
                    Arrays.asList((Chat[]) getArguments().getSerializable(ARG_CHAT_LIST)));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_chat_list, container, false);
        getActivity().findViewById(R.id.floatingActionButton_newChat).setOnClickListener(this::newChat);
        if (v instanceof RecyclerView) {
            Context context = v.getContext();
            RecyclerView recyclerView = (RecyclerView) v;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mAdapter = new MyChatRecyclerViewAdapter(mChats, mListener);
            recyclerView.setAdapter(mAdapter);
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

    public void newChat(View view) {
        Log.e("LOGAN", "newchat");
        Uri create = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_chatroom_base))
                .appendPath(getString(R.string.ep_chatroom_createroom))
                .build();
        Uri getroomid = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_chatroom_base))
                .appendPath(getString(R.string.ep_chatroom_getroomid))
                .build();
        Uri addmember = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_chatroom_base))
                .appendPath(getString(R.string.ep_chatroom_addmember))
                .build();

        String newRoomId = "newRoom" + ((Math.random() * 1000 - 1 + 1) + 1);
        JSONObject getJson = new JSONObject();
        try {getJson.put("roomname", newRoomId);
        } catch (JSONException e) {e.printStackTrace();}
        new SendPostAsyncTask.Builder(create.toString(), getJson)
                .onPostExecute(result -> {
                    Log.e("LOGAN", result);
                    JSONObject getRoomJson = new JSONObject();
                    try {getRoomJson.put("roomname", newRoomId);}
                    catch (JSONException e) {e.printStackTrace();}
                    new SendPostAsyncTask.Builder(getroomid.toString(), getRoomJson)
                            .onPostExecute(getRoomResult -> {
                                Log.e("LOGAN", getRoomResult);
                                JSONObject addMemberJson = new JSONObject();
                                try {
                                    addMemberJson.put("chatId", new JSONObject(getRoomResult).get("chatId"));
                                    addMemberJson.put("email", mCredentials.getEmail());
                                } catch (JSONException e) {e.printStackTrace();}
                                Log.e("LOGAN", addMemberJson.toString());
                                new SendPostAsyncTask.Builder(addmember.toString(), addMemberJson)
                                        .onPostExecute(addMemberResult -> {
                                            Log.e("LOGAN", addMemberResult);
                                            mListener.onReloadChatFragment(mAdapter);
                                            mAdapter.notifyDataSetChanged();
                                        })
                                        .build().execute();
                            })
                            .build().execute();
                })
                .build().execute();

        mAdapter.notifyDataSetChanged();
    }

    public interface OnChatFragmentInteractionListener {
        void onChatFragmentInteraction(Chat item, String memberToAdd);
        void onReloadChatFragment(MyChatRecyclerViewAdapter adapter);
    }

}
