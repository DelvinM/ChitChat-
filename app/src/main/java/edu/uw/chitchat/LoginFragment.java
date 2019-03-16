package edu.uw.chitchat;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.chitchat.Credentials.Credentials;
import edu.uw.chitchat.utils.SendPostAsyncTask;
import me.pushy.sdk.Pushy;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnLoginFragmentInteractionListener} interface
 * to handle interaction events.
 * @author Joe Lu
 * @author Delvin Mackenzie
 * @3/5/2019
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private OnLoginFragmentInteractionListener mListener;
    private Credentials mCredentials;
    private String mJwt;

    //for lockout mechanism
    private final int mMaxLoginAttempts = 6; //set maximum lock out attempts until user is locked out
    private int mLockOutCount = mMaxLoginAttempts;

    //store system time
    private long mLockOutStart;
    private long lockoutEnd;
    private long mLockoutDuration = 900; //lock out duration in seconds
    private String emailstored;


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load lockout end time from keys.xml
        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                getString(R.string.keys_lock_out_end), Context.MODE_PRIVATE);
        lockoutEnd = Long.parseLong(sharedPref.getString(getString(R.string.keys_lock_out_end), "0"));
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login , container, false);
        Button b = (Button) v.findViewById(R.id.login_button);
        b.setOnClickListener(this);
        b = (Button) v.findViewById(R.id.register_button);
        b.setOnClickListener(this);
        ((TextView) v.findViewById(R.id.textView_login_recover)).setOnClickListener(this::recover);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //retrieve the stored credentials from SharedPrefs
        if (prefs.contains(getString(R.string.keys_prefs_email)) &&
                prefs.contains(getString(R.string.keys_prefs_password))) {
            final String email = prefs.getString(getString(R.string.keys_prefs_email), "");
            final String password = prefs.getString(getString(R.string.keys_prefs_password), "");
            //Load the two login EditTexts with the credentials found in SharedPrefs
            EditText emailEdit = getActivity().findViewById(R.id.editText_fragment_login_username);
            emailEdit.setText(email);
            EditText passwordEdit = getActivity().findViewById(R.id.editText_fragment_login_password);
            passwordEdit.setText(password);

            doLogin(new Credentials.Builder(
                    emailEdit.getText().toString(),
                    passwordEdit.getText().toString())
                    .build());

        }
    }
    /**
     * does login procedure
     * @param credentials user credentials
     */
    private void doLogin(Credentials credentials) {
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_account_base))
                .appendPath(getString(R.string.ep_account_login))
                .build();
        //build the JSONObject
        JSONObject msg = credentials.asJSONObject();
        mCredentials = credentials;
        Log.d("JSON Credentials", msg.toString());
        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPreExecute(this::handleLoginOnPre)
                .onPostExecute(this::handleLoginOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    /**
     * used for password recovery
     * @param view
     */
    private void recover(View view) {
        Log.d("Logan", "recover password clicked");
        EditText emailEdit = getActivity().findViewById(R.id.editText_fragment_login_username);
        boolean hasError = false;
        if(emailEdit.getText().length() == 0) {
            hasError = true;
            emailEdit.setError("Field must not be empty.");
        }  else if (emailEdit.getText().toString().chars().filter(ch -> ch == '@').count() != 1) {
            hasError = true;
            emailEdit.setError("Field must contain a valid email address.");
        }
        if(!hasError) {
            Credentials credentials =
                    new Credentials.Builder(
                            emailEdit.getText().toString(),
                            null)
                            .build();

            //build the web service URL
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_account_base))
                    .appendPath(getString(R.string.ep_account_confirm))
                    .build();

            //build the JSONObject
            JSONObject msg = credentials.asJSONObject();
            mCredentials = credentials;
            //instantiate and execute the AsyncTask.
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPostExecute(this::handleRecoverOnPost)
                    .build()
                    .execute();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginFragmentInteractionListener) {
            mListener = (OnLoginFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public String getEmailAddress(){
        return emailstored;
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) {
            switch (view.getId()) {
                case R.id.login_button:

                    //login validation
                    attemptLogin(view);
                    break;
                case R.id.register_button:
                    mListener.onRegisterClicked();
                    break;
                default:
                    Log.wtf("", "Didn't expect to see me...");
            }
        }
    }



    private void handleRecoverOnPost(String result) {
        try {
                JSONObject resultsJSON = new JSONObject(result);
                boolean success =
                        resultsJSON.getBoolean(getString(R.string.keys_json_login_success));
                if (success) {
                    Toast.makeText(getActivity(), "Sent the reset email to your email address",
                            Toast.LENGTH_LONG).show();
//                    mListener.onLoginSuccess(mCredentials,
//                            resultsJSON.getString(
//                                    getString(R.string.keys_json_login_jwt)));
                    return;
                }
            mListener.onWaitFragmentInteractionHide();
        } catch (JSONException e) {
            //It appears that the web service did not return a JSON formatted
            // String or it did not have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result + System.lineSeparator() + e.getMessage());
            mListener.onWaitFragmentInteractionHide();
            ((TextView) getView().findViewById(R.id.editText_fragment_login_username))
                    .setError("Recovering password Unsuccessful");
        }
    }

    /**
     * Checks validity of login credentials
     * @author Delvin Mackenzie
     * @param theButton
     */
    private void attemptLogin(final View theButton) {
        EditText emailEdit = getActivity().findViewById(R.id.editText_fragment_login_username);
        EditText passwordEdit = getActivity().findViewById(R.id.editText_fragment_login_password);
        boolean hasError = false;
        if (emailEdit.getText().length() == 0) {
            hasError = true;
            emailEdit.setError("Field must not be empty.");
        }
        else if (emailEdit.getText().toString().chars().filter(ch -> ch == '@').count() != 1) {
            hasError = true;
            emailEdit.setError("Field must contain a valid email address.");
        }
        if (passwordEdit.getText().length() == 0) {
            hasError = true;
            passwordEdit.setError("Field must not be empty.");
        }
        if (!hasError) {
            doLogin(new Credentials.Builder(
                    emailEdit.getText().toString(),
                    passwordEdit.getText().toString())
                    .build());
        }
    }

    /**
     * * Handle errors that may occur during the AsyncTask.*
     * * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNC_TASK_ERROR", result);
    }

    /**
     * * Handle the setup of the UI before the HTTP call to the webservice.
     */
    private void handleLoginOnPre() {
        mListener.onWaitFragmentInteractionShow();
    }

    /**
     * * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * * a JSON formatted String. Parse it for success or failure.
     *
     * @param result the JSON formatted String response from the web service
     */
    private void handleLoginOnPost(String result) {

            try {
                long currentTime = System.currentTimeMillis()/1000;
                if(lockoutEnd > currentTime) { //if user lockout duration hasn't expired

                    TextView loginView = ((TextView) getView().findViewById(R.id.editText_fragment_login_username));
                    loginView.setError("User locked out for 15 minutes for too many attempts");
                    loginView.requestFocus();

                } else {
                    JSONObject resultsJSON = new JSONObject(result);
                    boolean success =
                            resultsJSON.getBoolean(getString(R.string.keys_json_login_success));
                    if (success) {
                        mLockOutCount = mMaxLoginAttempts; //reset count
                        //Login was successful. Switch to the loadSuccessFragment.
                        mJwt = resultsJSON.getString(
                                getString(R.string.keys_json_login_jwt));

                        new RegisterForPushNotificationsAsync().execute();

                        return;
                    } else {
                        //check to see how many login attempt there have been
                        lockOutLogin();
                    }
                }
                mListener.onWaitFragmentInteractionHide();
            } catch (JSONException e) {
                //It appears that the web service did not return a JSON formatted
                // String or it did not have what we expected in it.
                Log.e("JSON_PARSE_ERROR", result + System.lineSeparator() + e.getMessage());
                mListener.onWaitFragmentInteractionHide();
                ((TextView) getView().findViewById(R.id.editText_fragment_login_username))
                        .setError("Login Unsuccessful");
            }

    }

    /**
     * called each time an invalid login attempt is made
     * locks out user for 15 minutes after 6 attempts
     * @author Delvin Mackenzie
     *
     */
    private void lockOutLogin() {
        mLockOutCount -= 1;

        //Login was unsuccessful. Don’t switch fragments and
        //inform user of remaining attempts of login until lockout
        TextView loginView = ((TextView) getView().findViewById(R.id.editText_fragment_login_username));
                loginView.setError("Login Unsuccessful " + mLockOutCount + " remaining Attempts");
                loginView.requestFocus();

        //after 6 attempts lockout user
        if (mLockOutCount <= 0) {
            mLockOutCount = mMaxLoginAttempts; //reset to 6
            mLockOutStart = System.currentTimeMillis()/1000; //current system time
            lockoutEnd = mLockOutStart + mLockoutDuration; // start + 900 seconds

            //get sharedpref & log lockout end time in keys.xml
            SharedPreferences sharedPref = getActivity().getSharedPreferences(
                    getString(R.string.keys_lock_out_end), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            String stringLockout = Long.toString(lockoutEnd);
            editor.putString(getString(R.string.keys_lock_out_end), stringLockout);
            editor.commit();
        }
    }

    public interface OnLoginFragmentInteractionListener extends WaitFragment.OnFragmentInteractionListener {
        void onRegisterClicked();
        void onLoginSuccess(Credentials credentials, String jwt);
    }

    private void saveCredentials(final Credentials credentials) {
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //Store the credentials in SharedPrefs
        prefs.edit().putString(getString(R.string.keys_prefs_email), credentials.getEmail()).apply();
        prefs.edit().putString(getString(R.string.keys_prefs_password), credentials.getPassword()).apply();
    }

    /**
     * Registers for async notification from pushy service
     *
     */
    private class RegisterForPushNotificationsAsync extends AsyncTask<Void, String, String>
    {
        protected String doInBackground(Void... params) {
            String deviceToken = "";
            try {
                // Assign a unique token to this device
                deviceToken = Pushy.register(getActivity().getApplicationContext());
                //subscribe to a topic (this is a Blocking call)
                Pushy.subscribe("all", getActivity().getApplicationContext());
            }
            catch (Exception exc) {
                cancel(true);
                // Return exc to onCancelled
                return exc.getMessage();
            }
            // Success
            return deviceToken;
        }
        @Override
        protected void onCancelled(String errorMsg) {
            super.onCancelled(errorMsg);
            Log.d("ChitChat", "Error getting Pushy Token: " + errorMsg);
        }
        @Override
        protected void onPostExecute(String deviceToken) {
            // Log it for debugging purposes
            Log.d("ChitChat", "Pushy device token: " + deviceToken);
            //build the web service URL
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_pushy))
                    .appendPath(getString(R.string.ep_token))
                    .build();
            //build the JSONObject
            JSONObject msg = mCredentials.asJSONObject();
            try {
                msg.put("token", deviceToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //instantiate and execute the AsyncTask.
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPostExecute(LoginFragment.this::handlePushyTokenOnPost)
                    .onCancelled(LoginFragment.this::handleErrorsInTask)
                    .addHeaderField("authorization", mJwt)
                    .build().execute();
        }
    }


    private void handlePushyTokenOnPost(String result) {
        try {
            Log.d("JSON result",result);
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            if (success) {
                saveCredentials(mCredentials);
                mListener.onLoginSuccess(mCredentials, mJwt);
                return;
            } else {
                //Saving the token wrong. Don’t switch fragments and inform the user
                ((TextView) getView().findViewById(R.id.editText_fragment_login_username))
                        .setError("Login Unsuccessful");
            }
            mListener.onWaitFragmentInteractionHide();
        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
            mListener.onWaitFragmentInteractionHide();
            ((TextView) getView().findViewById(R.id.editText_fragment_login_username))
                    .setError("Login Unsuccessful");
        }
    }

}
