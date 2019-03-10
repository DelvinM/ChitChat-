package edu.uw.chitchat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import edu.uw.chitchat.chat.Chat;

public class LoadHistoryAsyncTask extends AsyncTask<Void, Void, Chat[]> {

    private final String mUrl;
    private final Context mContext;
    private final ArrayList<Chat> mChats;
    private final ArrayList<String> mChatIds;
    private final Runnable mOnPre;
    private final Consumer<Chat[]> mOnPost;
    private final Map<String, String> mHeaders;

    public static class Builder {
        private final String mUrl;
        private final Context mContext;
        private final ArrayList<String> mChatIds;
        private Consumer<Chat[]> onPost = x -> {};
        private Runnable onPre = () -> {};
        private Map<String, String> headers;

        public Builder(final String url, ArrayList<String> chatIds, Context context) {
            mContext = context;
            mChatIds = chatIds;
            mUrl = url;
            headers = new HashMap<>();
        }
        public Builder onPreExecute(final Runnable val) {
            onPre = val;
            return this;
        }
        public Builder onPostExecute(final Consumer<Chat[]> val) {
            onPost = val;
            return this;
        }
        public Builder addHeaderField(final String key, final String value) {
            headers.put(key, value);
            return this;
        }

        public LoadHistoryAsyncTask build() {
            return new LoadHistoryAsyncTask(this);
        }
    }

    private LoadHistoryAsyncTask(final Builder builder) {
        mUrl = builder.mUrl;
        mContext = builder.mContext;
        mChatIds = builder.mChatIds;
        mOnPre = builder.onPre;
        mOnPost = builder.onPost;
        mHeaders = builder.headers;
        mChats = new ArrayList<Chat>();
    }

    @Override
    protected void onPreExecute() {
        for(int i = 0; i < mChatIds.size(); i++) {
            JSONObject getJson = new JSONObject();
            try {
                getJson.put("chatId", mChatIds.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int index = i;
            new SendPostAsyncTask.Builder(mUrl, getJson)
                    .onPostExecute(result -> {
                        ArrayList<String> contents = endOfDoGetAll(result);
                        String mostRecent = contents.get(contents.size() - 1);
                        contents.remove(contents.size() - 1);
                        mChats.add(new Chat(contents.get(index).split(":")[0], mostRecent,
                                contents.get(index).split(":")[1], mChatIds.get(index),
                                Integer.toString(getPrefInt("chat room " + mChatIds.get(index) + " count")), contents));
                        preHelper();
                    })
                    .onCancelled(error -> Log.e("LOADASYNC", "Problem"))
                    .addHeaderField("authorization", mHeaders.get("authorization"))
                    .build().execute();
        }
    }

    private void preHelper() {
        if(mChats.size() == mChatIds.size()) {
            super.onPreExecute();
            mOnPre.run();
        }
    }

    @Override
    protected Chat[] doInBackground(Void... voids) {
        while(mChats.size() < mChatIds.size()) {}
        Chat[] arr = new Chat[mChatIds.size()];
        return mChats.toArray(arr);
    }

    private ArrayList<String> endOfDoGetAll(final String result) {
        ArrayList<String> formattedMessages = new ArrayList<String>();
        String mostRecent = "";
        boolean mostRecentRecorded = false;
        try {
            JSONObject res = new JSONObject(result);
            if(res.has("messages")) {
                String messages = res.getString("messages");
                String currString = "";
                int count = 1;
                int quoteCount = 0;
                for (int i = 0; i < messages.length(); i++) {
                    if (count == 1) {
                        if (messages.charAt(i) == '@') {
                            count++;
                            quoteCount = 0;
                            currString += ": ";
                        } else if (messages.charAt(i) == '"') {
                            quoteCount++;
                        } else if (quoteCount == 3) {
                            currString += messages.charAt(i);
                        }
                    } else if (count == 2) {
                        if (messages.charAt(i) == '"') {
                            quoteCount++;
                            if (quoteCount == 5) {
                                count++;
                                quoteCount = 0;
                            }
                        } else if (quoteCount == 4) {
                            currString += messages.charAt(i);
                        }
                    } else if (count == 3) {
                        if (messages.charAt(i) == '"') {
                            quoteCount++;
                            if (quoteCount == 4) {
                                count = 1;
                                quoteCount = 0;
                                formattedMessages.add(currString);
                                mostRecentRecorded = true;
                                currString = "";
                            }
                        } else if (quoteCount == 3 && !mostRecentRecorded) {
                            mostRecent += messages.charAt(i);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        formattedMessages.add(mostRecent.split(" ")[0]);
        return formattedMessages;
    }

    private int getPrefInt (String key) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        return preferences.getInt(key, 0);
    }

    @Override
    protected void onPostExecute(Chat[] result) {
        mOnPost.accept(result);
    }
}