package edu.uw.chitchat.Credentials;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Class to encapsulate credentials fields. Building an Object requires a email and password.
 *
 * Optional fields include username, first and last name.
 *
 *
 * @author Charles Bryan
 * @author Yohei Sato
 */
public class Credentials implements Serializable {
    private static final long serialVersionUID = -1634677417576883013L;
    /**
     * the user's name
     */
    private final String mUsername;
    /**
     * the passowrd
     */
    private final String mPassword;
    /**
     * the pass reentured pass word
     */
    private final String mRePassword;
    /**
     * it's user's first name
     */
    private String mFirstName;
    /**
     * it's user's last name
     */
    private String mLastName;
    /**
     * it's user's email address
     */
    private String mEmail;

    /**
     * Helper class for building Credentials.
     *
     * @author Charles Bryan
     */
    public static class Builder {

        private final String mPassword;
        private final String mEmail;
        private final String mRePassword;
        private String mFirstName = "";
        private String mLastName = "";
        private String mUsername = "";


        /**
         * Constructs a new Builder.
         *
         * No validation is performed. Ensure that the argument is a
         * valid email before adding here if you wish to perform validation.
         *
         * @param email the email address
         * @param password the password for users
         */
        public Builder(String email, String password) {
            mEmail = email;
            mPassword = password;
            mRePassword = null;
        }

        /**
         *
         * @param email the email address
         * @param password the password for users
         * @param repassword the retyped for users
         * @param username the user's name
         * @param firstname the user's first name
         * @param lastname the user's last name
         */
        public Builder(String email, String password, String repassword, String username,
                       String firstname, String lastname) {
            mEmail = email;
            mPassword = password;
            mRePassword = repassword;
            mUsername = username;
            mFirstName = firstname;
            mLastName = lastname;

        }


        /**
         * Add an optional first name.
         * @param val an optional first name
         * @return
         */
        public Builder addFirstName(final String val) {
            mFirstName = val;
            return this;
        }

        /**
         * Add an optional last name.
         * @param val an optional last name
         * @return
         */
        public Builder addLastName(final String val) {
            mLastName = val;
            return this;
        }

        /**
         * Add an optional Uuername.
         * @param val an optional Uuername
         * @return
         */
        public Builder addUsername(final String val) {
            mUsername = val;
            return this;
        }

        public Credentials build() {
            return new Credentials(this);
        }
    }

    /**
     * Construct a Credentials internally from a builder.
     *
     * @param builder the builder used to construct this object
     */
    private Credentials(final Builder builder) {
        mUsername = builder.mUsername;
        mPassword = builder.mPassword;
        mFirstName = builder.mFirstName;
        mLastName = builder.mLastName;
        mEmail = builder.mEmail;
        mRePassword = builder.mRePassword;
    }

    /**
     * Get the Username.
     * @return the username
     */
    public String getUsername() {
        return mUsername;
    }

    /**
     * Get the password.
     * @return the password
     */
    public String getPassword() {
        return mPassword;
    }

    public String getRePassword() {
        return mRePassword;
    }

    /**
     * Get the first name or the empty string if no first name was provided.
     * @return the first name or the empty string if no first name was provided.
     */
    public String getFirstName() {
        return mFirstName;
    }

    /**
     * Get the last name or the empty string if no first name was provided.
     * @return the last name or the empty string if no first name was provided.
     */
    public String getLastName() {
        return mLastName;
    }

    /**
     * Get the email or the empty string if no first name was provided.
     * @return the email or the empty string if no first name was provided.
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * Get all of the fields in a single JSON object. Note, if no values were provided for the
     * optional fields via the Builder, the JSON object will include the empty string for those
     * fields.
     *
     * Keys: username, password, first, last, email
     *
     * @return all of the fields in a single JSON object
     */
    public JSONObject asJSONObject() {
        //build the JSONObject
        JSONObject msg = new JSONObject();
        try {
            msg.put("username", getUsername());
            msg.put("password", mPassword);
            msg.put("first", getFirstName());
            msg.put("last", getLastName());
            msg.put("email", getEmail());
        } catch (JSONException e) {
            Log.wtf("CREDENTIALS", "Error creating JSON: " + e.getMessage());
        }
        return msg;
    }

}
