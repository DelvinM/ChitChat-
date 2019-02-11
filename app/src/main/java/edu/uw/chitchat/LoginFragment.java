package edu.uw.chitchat;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnLoginFragmentInteractionListener} interface
 * to handle interaction events.
 * @author Joe Lu
 * @author Delvin Mackenzie
 * @2/5/2018
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private OnLoginFragmentInteractionListener mListener;
    private Credentials mCredentials;

    //for lockout mechanism
    private final int mMaxLoginAttempts = 6; //set maximum lock out attempts until user is locked out
    private int mLockOutCount = mMaxLoginAttempts;

    //store system time
    private long mLockOutStart;
    private long lockoutEnd;
    private long mLockoutDuration = 900; //lock out duration in seconds


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
    public void onStart() {
        super.onStart();

        EditText emailText = (EditText) getActivity().findViewById(R.id.editText_fragment_login_username);
        EditText passwordText = (EditText) getActivity().findViewById(R.id.editText_fragment_login_password);
        String email;
        String password;
        if (getArguments() != null) {//loads email/password arguments from registration success
            email = getArguments().getString(getString(R.string.keys_email_stored_onRegister));
            password = getArguments().getString(getString(R.string.keys_password_stored_onRegister));
        }
        else {//loads email/password from keys.xml
            email = getSharedPreference(getString(R.string.keys_email_stored_onRegister));
            password = getSharedPreference(getString(R.string.keys_password_stored_onRegister));
        }
        emailText.setText(email);
        passwordText.setText(password);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login , container, false);
        Button b = (Button) v.findViewById(R.id.login_button);
        b.setOnClickListener(this);
        b = (Button) v.findViewById(R.id.register_button);
        b.setOnClickListener(this);

        return v;
    }

    //refactor later make this a class
    private String getSharedPreference (String key) {
        SharedPreferences sharedPref =
                getActivity().getSharedPreferences(key, Context.MODE_PRIVATE);
        return sharedPref.getString(key, null);
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
            Credentials credentials =
                    new Credentials.Builder(
                            emailEdit.getText().toString(),
                            passwordEdit.getText().toString())
                            .build();

            //build the web service URL
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_login))
                    .build();

            //build the JSONObject
            JSONObject msg = credentials.asJSONObject();
            mCredentials = credentials;
            //instantiate and execute the AsyncTask.
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPreExecute(this::handleLoginOnPre)
                    .onPostExecute(this::handleLoginOnPost)
                    .onCancelled(this::handleErrorsInTask)
                    .build()
                    .execute();
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
                        mListener.onLoginSuccess(mCredentials,
                                resultsJSON.getString(
                                        getString(R.string.keys_json_login_jwt)));
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

    //called each time an invalid login attempt is made
    //locks out user for 15 minutes after 6 attempts
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
}
