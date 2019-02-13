package edu.uw.chitchat;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import edu.uw.chitchat.Credentials.Credentials;
import edu.uw.chitchat.utils.SendPostAsyncTask;


/**
 * @author Logan Jenny
 */
public class ResetFragment extends Fragment implements View.OnClickListener{

    private OnResetFragmentInteractionListener mListener;
    public Credentials mCredentials;
    public ResetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reset, container, false);
        ((Button) v.findViewById(R.id.button_reset_update)).setOnClickListener(this::updatePassword);
        return v;
    }

    public void updatePassword(View view) {
        EditText passwordText = getActivity().findViewById(R.id.editText_fragment_reset_password);
        EditText repasswordText = getActivity().findViewById(R.id.editText_fragment_register_email_verification);
        String email = getSharedPreference(getString(R.string.keys_email_stored_onRegister));
        String password = passwordText.getText().toString().trim();
        String repassword = repasswordText.getText().toString().trim();

        boolean hasError = false;
        //regex used for names to not allow special characters in text fields
        //allows input anything outside of below characters
        Pattern StringRegex = Pattern.compile("^[^±!@£$%^&*_+§¡~€#¢§¶•ªº«\\[\\]\\/<>?:;|=.,]+$");

        //password regex, cannot start with . or -, has at least 1 cap letter and number
        //allows the listed special characters, with a minimum length of 6
        Pattern passwordRegex = Pattern.compile(
                "^(?=.*[0-9])(?=.*[A-Z])[^.\\-][A-Z0-9a-z!()?_'~;:.\\]\\[\\-!#@$%^&*+=]{6,}$"
        );

        if (!passwordRegex.matcher(password).matches()) {
            hasError = true;
            passwordText.setError("Please enter a minimum of 6 characters with 1 upper case and 1 digit.");
        }
        if (!password.equals(repassword)) {
            hasError = true;
            repasswordText.setError("Your password and retyped one do not match.");
        }

        if (!hasError) {
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_reset))
                    .build();
                    //mListener.onRegisterSuccess(credentials);
                    //build the web service URL
                    //build the JSONObject
                    Credentials credentials = new Credentials.Builder(email,password).build();
                    JSONObject msg = credentials.asJSONObject();

                    mCredentials = credentials;
                    //instantiate and execute the AsyncTask.
                    new SendPostAsyncTask.Builder(uri.toString(), msg)
                            .onPostExecute(this::handleUpDatePasswordOnPost)
                            .build().execute();
        } else {
            return;
        }

    }

    private void handleUpDatePasswordOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success =
                    resultsJSON.getBoolean(getString(R.string.keys_json_login_success));
            if (success) {
                Toast.makeText(getActivity(), "You successfully changed the new password",
                        Toast.LENGTH_LONG).show();
                mListener.onLogOut();
                return;
            }
            mListener.onWaitFragmentInteractionHide();
        } catch (JSONException e) {
            //It appears that the web service did not return a JSON formatted
            // String or it did not have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result + System.lineSeparator() + e.getMessage());
            mListener.onWaitFragmentInteractionHide();
            ((TextView) getView().findViewById(R.id.editText_fragment_reset_password))
                    .setError("Changing password Unsuccessful");
        }
    }

    private String getSharedPreference (String key) {
        SharedPreferences sharedPref =
                getActivity().getSharedPreferences(key, Context.MODE_PRIVATE);
        return sharedPref.getString(key, null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnResetFragmentInteractionListener) {
            mListener = (OnResetFragmentInteractionListener) context;
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
    public void onClick(View v) {

    }

    public interface OnResetFragmentInteractionListener {
        void onPasswordUpdate();
        void onResetCancel();

        void onWaitFragmentInteractionHide();

        void onLogOut();
    }
}
