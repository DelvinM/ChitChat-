package edu.uw.chitchat;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import edu.uw.chitchat.Credentials.Credentials;
import edu.uw.chitchat.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    private OnRegisterFragmentInteractionListener mListener;
    public Credentials mCredentials;
    View v;
    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRegisterFragmentInteractionListener) {
            mListener = (OnRegisterFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        //System.out.println("YOHEI!!!!!!!!!!"+ getView());
        EditText emailText = (EditText) getActivity().findViewById(R.id.editText_fragment_reset_password);
        EditText passwordText = (EditText) getActivity().findViewById(R.id.editText_fragment_register_password);
        Credentials credentials = new Credentials.Builder(emailText.getText().toString(),
                passwordText.getText().toString()).build();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v = inflater.inflate(R.layout.fragment_register, container, false);
        Button b = (Button) v.findViewById(R.id.actual_register_button);
        b.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        EditText emailText = (EditText) getActivity().findViewById(R.id.editText_fragment_reset_password);
        EditText passwordText = (EditText) getActivity().findViewById(R.id.editText_fragment_register_password);
        EditText repasswordText = (EditText) getActivity().findViewById(R.id.editText_fragment_register_repassword);
        EditText usernameText = (EditText) getActivity().findViewById(R.id.editText_fragment_register_username);
        EditText firstnameText = (EditText) getActivity().findViewById(R.id.editText_fragment_register_firstname);
        EditText lastnameText = (EditText) getActivity().findViewById(R.id.editText_fragment_register_lastname);
        EditText reemailText = (EditText) getActivity().findViewById(R.id.editText_fragment_register_email_verification);
        String email_Text = emailText.getText().toString().trim();
        String password =  passwordText.getText().toString().trim();
        String repassword = repasswordText.getText().toString().trim();
        String username = usernameText.getText().toString().trim();
        String firstname = firstnameText.getText().toString().trim();
        String lastname = lastnameText.getText().toString().trim();
        String reemail = reemailText.getText().toString().trim();

        LoginFragment loginFragment = new LoginFragment();
        Bundle args = new Bundle();

        loginFragment.setArguments(args);
        boolean hasError = false;
        // String repassward = getArguments().getString("repasskey");


//        String emailTextString = getArguments().getString("key");
//        String passward = getArguments().getString("passkey");
        Credentials credentials = new Credentials.Builder(emailText.getText().toString(),
                passwordText.getText().toString(), repasswordText.getText().toString(),
                usernameText.getText().toString(), firstnameText.getText().toString(),
                lastnameText.getText().toString()).build();

        //regex used for names to not allow special characters in text fields
        //allows input anything outside of below characters
        Pattern StringRegex = Pattern.compile("^[^±!@£$%^&*_+§¡~€#¢§¶•ªº«\\[\\]\\/<>?:;|=.,]+$");

        //password regex, cannot start with . or -, has at least 1 cap letter and number
        //allows the listed special characters, with a minimum length of 6
        Pattern passwordRegex = Pattern.compile(
                "^(?=.*[0-9])(?=.*[A-Z])[^.\\-][A-Z0-9a-z!()?_'~;:.\\]\\[\\-!#@$%^&*+=]{6,}$"
                );

        //TODO: refactor
        //refactor
        if (mListener != null) {
            if (TextUtils.isEmpty(lastname)) {
                hasError = true;
                emailText.setError("Last name is empty.");
            }
            if (TextUtils.isEmpty(firstname)) {
                hasError = true;
                emailText.setError("First name is empty.");
            }
            if (TextUtils.isEmpty(username)) {
                hasError = true;
                emailText.setError("Username is empty.");
            }
            if (!StringRegex.matcher(firstname).matches()) {
                hasError = true;
                firstnameText.setError("First name has certain characters that aren't allowed.");
            }
            if (!StringRegex.matcher(lastname).matches()) {
                hasError = true;
                lastnameText.setError("Last name has certain characters that aren't allowed.");
            }
            //email check
            if (!Patterns.EMAIL_ADDRESS.matcher(email_Text).matches()) {
                hasError = true;
                emailText.setError("Email is not valid.");
            }
            if (TextUtils.isEmpty(email_Text)) {
                hasError = true;
                emailText.setError("Email is empty.");
            }
            if (!email_Text.equals(reemail)){
                hasError = true;
                reemailText.setError("Your email and retyped one do not match.");
            }

            if (!passwordRegex.matcher(password).matches()) {
                hasError = true;
                passwordText.setError("Please enter a minimum of 6 characters with 1 upper case and 1 digit.");
            }
            if (!password.equals(repassword)) {
                hasError = true;
                repasswordText.setError("Your password and retyped one do not match.");
            }
            if (!(username.length()>=1)) {
                hasError = true;
                usernameText.setError("Username has certain characters that aren't allowed.");
            }

            if (!hasError) {
                Uri uri = new Uri.Builder()
                        .scheme("https")
                        .appendPath(getString(R.string.ep_base_url))
                        .appendPath(getString(R.string.ep_register))
                        .build();
                switch (view.getId()) {
                    case R.id.actual_register_button:
                        //mListener.onRegisterSuccess(credentials);
                        //build the web service URL
                        //build the JSONObject
                        JSONObject msg = credentials.asJSONObject();
                        mCredentials = credentials;
                        //instantiate and execute the AsyncTask.
//                        Toast.makeText(getActivity(), "Check your email to verify account",
//                                Toast.LENGTH_LONG).show();
                        new SendPostAsyncTask.Builder(uri.toString(), msg)
                                .onPreExecute(this::handleLoginOnPre)
                                .onPostExecute(this::handleLoginOnPost)
                                .onCancelled(this::handleErrorsInTask)
                                .build().execute();
                        Log.v("login", "onClickHere");
                        break;
                    default:
                        Log.wtf("", "Didn't expect to see me...");
                }
            } else {
                return;
            }
        }
    }

    private void handleLoginOnPre() {
        mListener.onWaitFragmentInteractionShow();
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNC_TASK_ERROR", result);
    }




    //@RequiresApi(api = Build.VERSION_CODES.KITKAT)
    //@TargetApi(Build.VERSION_CODES.KITKAT)
    private void handleLoginOnPost(String result) {
        TextView view = (TextView) v.findViewById(R.id.editText_fragment_reset_password);
        //mListener.onRegisterSuccess(mCredentials);
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success =
                    resultsJSON.getBoolean(
                            getString(R.string.keys_json_register_success));
            if (success) {
                //Login was successful. Switch to the loadSuccessFragment.
                mListener.onRegisterSuccess(mCredentials);
                return;
            } else {
//                Login was unsuccessful. Don’t switch fragments and
//                 inform the user
                view.setError("Register Unsuccessful");
            }
            mListener.onWaitFragmentInteractionHide();
        } catch (JSONException e) {
            //It appears that the web service did not return a JSON formatted
            //String or it did not have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
            mListener.onWaitFragmentInteractionHide();
            view.setError("Register Unsuccessful");
        }
    }


    public interface OnRegisterFragmentInteractionListener {
        void onRegisterSuccess(Credentials a);
        void onWaitFragmentInteractionShow();
        void onWaitFragmentInteractionHide();
    }
}