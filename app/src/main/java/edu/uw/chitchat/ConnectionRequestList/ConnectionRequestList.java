package edu.uw.chitchat.contactlist;

import java.io.Serializable;

/**
 * Class to encapsulate a https://tcss450-app contact. Building an Object requires username and email.
 *
 * @author Charles Bryan
 * @author Yohei Sato
 * @author Zhou Lu
 */
public class ConnectionRequestList implements Serializable {

    private final String mUsername;
    private final boolean mPending;

    /**
     * Helper class for building contact.
     *
     * @author Zhou Lu
     */
    public static class Builder {
        private final String mUsername;
        private final boolean mPending;


        public Builder(String username, boolean pending) {
            this.mUsername = username;
            this.mPending = pending;
        }



        public ConnectionRequestList build() {
            return new ConnectionRequestList(this);
        }

    }

    public ConnectionRequestList(final Builder builder) {
        this.mUsername = builder.mUsername;
        this.mPending = builder.mPending;
    }


    public String getUsername() {
        return mUsername;
    }

    public boolean getPending() {
        return mPending;
    }



}
