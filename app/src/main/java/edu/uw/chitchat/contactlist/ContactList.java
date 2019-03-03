package edu.uw.chitchat.contactlist;

import java.io.Serializable;

/**
 * Class to encapsulate a https://tcss450-app contact. Building an Object requires username and email.
 *
 * @author Charles Bryan
 * @author Yohei Sato
 * @author Zhou Lu
 */
public class ContactList implements Serializable {

    private final String mUsername;
    private final String mEmailAddress;

    /**
     * Helper class for building contact.
     *
     * @author Zhou Lu
     */
    public static class Builder {
        private final String mUsername;
        private final String mEmailAddress;


        public Builder(String username, String emailAddress) {
            this.mUsername = username;
            this.mEmailAddress = emailAddress;
        }



        public ContactList build() {
            return new ContactList(this);
        }

    }

    public ContactList(final Builder builder) {
        this.mUsername = builder.mUsername;
        this.mEmailAddress = builder.mEmailAddress;
    }


    public String getUsername() {
        return mUsername;
    }

    public String getEmailAddress() {
        return mEmailAddress;
    }



}
