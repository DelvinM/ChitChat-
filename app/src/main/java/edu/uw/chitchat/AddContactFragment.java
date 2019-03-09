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
import edu.uw.chitchat.contactlist.ContactList;
import edu.uw.chitchat.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddContactFragment extends Fragment implements View.OnClickListener{

    private String mEmail;

    private OnAddContactFragmentInteractionListener mListener;
    public Credentials mCredentials;
    public AddContactFragment() {
    }

    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            //get the email and JWT from the Activity. Make sure the Keys match what you used
            mEmail = getArguments().getString("email");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_contact, container, false);
        ((Button) v.findViewById(R.id.button_AddContact)).setOnClickListener(this::addNewContact);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ContactListFragment.OnListFragmentInteractionListener) {
            mListener = (OnAddContactFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }



    public void addNewContact(View view) {
        EditText friendEmailAddress = getActivity().findViewById(R.id.editText_friend_request_email_address);
        String friendemail = friendEmailAddress.getText().toString().trim();
        boolean hasError = false;
        //regex used for names to not allow special characters in text fields
        //allows input anything outside of below characters
        Pattern StringRegex = Pattern.compile("^[^±!@£$%^&*_+§¡~€#¢§¶•ªº«\\[\\]\\/<>?:;|=.,]+$");

        //password regex, cannot start with . or -, has at least 1 cap letter and number
        //allows the listed special characters, with a minimum length of 6
        Pattern passwordRegex = Pattern.compile(
                "^(?=.*[0-9])(?=.*[A-Z])[^.\\-][A-Z0-9a-z!()?_'~;:.\\]\\[\\-!#@$%^&*+=]{6,}$"
        );

        if (TextUtils.isEmpty(friendemail)) {
            hasError = true;
            friendEmailAddress.setError("First E-mail is empty.");
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(friendemail).matches()) {
            hasError = true;
            friendEmailAddress.setError("First E-mail is not valid.");
        }

        if (!hasError) {
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_connection_base))
                    .appendPath(getString(R.string.ep_connection_add))
                    .build();
            //mListener.onRegisterSuccess(credentials);
            //build the web service URL
            //build the JSONObject

            //String emailstored = getSharedPreference (getString(R.string.keys_email_stored_onRegister));

            JSONObject test = new JSONObject();
            try {
                test.put("email_A", mEmail);
                test.put("email_B", friendemail);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //instantiate and execute the AsyncTask.
            new SendPostAsyncTask.Builder(uri.toString(), test)
                    .onPostExecute(this::handleUpDdateNewContact)
                    .build().execute();
        } else {
            return;
        }
        //mListener.onPasswordUpdate();
    }

    //TODO: update this with pushy notification to user receiving connection request
    private void handleUpDdateNewContact(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success =
                    resultsJSON.getBoolean(getString(R.string.keys_json_login_success));
            if (success) {
                Log.wtf("yohei", "success");
                Toast.makeText(getActivity(), "You successfully made the new contact",
                        Toast.LENGTH_LONG).show();
                return;
            }
            mListener.onWaitFragmentInteractionHide();
        } catch (JSONException e) {
            //It appears that the web service did not return a JSON formatted
            // String or it did not have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result + System.lineSeparator() + e.getMessage());
            mListener.onWaitFragmentInteractionHide();
            ((TextView) getView().findViewById(R.id.editText_friend_request_email_address))
                    .setError("Making New contact Unsuccessful");
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
    private String getSharedPreference (String key) {
        SharedPreferences sharedPref =
                getActivity().getSharedPreferences(key, Context.MODE_PRIVATE);
        return sharedPref.getString(key, null);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnAddContactFragmentInteractionListener {
        // TODO: Update argument type and name

        void onListFragmentInteraction(ContactList mItem);

        void onWaitFragmentInteractionHide();
    }
}
