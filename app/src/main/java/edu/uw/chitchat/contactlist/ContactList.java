package edu.uw.chitchat.contactlist;

import java.io.Serializable;

/**
 * Class to encapsulate a Phish.net Blog Post. Building an Object requires a publish date and title.
 *
 * Optional fields include URL, teaser, and Author.
 *
 *
 * @author Charles Bryan
 * @author Yohei Sato
 */
public class ContactList implements Serializable {

    private final String mUsername;
    private final String mEmailAddress;

    /**
     * Helper class for building Credentials.
     *
     * @author Charles Bryan
     */
    public static class Builder {
        private final String mUsername;
        private final String mEmailAddress;





        public Builder(String username, String description) {
            this.mUsername = username;
            this.mEmailAddress = description;
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

    public String getDescription() {
        return mEmailAddress;
    }



}
