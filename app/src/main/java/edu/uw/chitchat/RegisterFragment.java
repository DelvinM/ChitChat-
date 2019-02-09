package edu.uw.chitchat;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.chitchat.Credentials.Credentials;



/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    public Credentials mCredentials;
    View v;
    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginFragment.OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        //System.out.println("YOHEI!!!!!!!!!!"+ getView());
        EditText usernameText = (EditText) getActivity().findViewById(R.id.editText_fragment_register_username);
        EditText passwordText = (EditText) getActivity().findViewById(R.id.editText_fragment_register_password);
        Credentials credentials = new Credentials.Builder(usernameText.getText().toString(),
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
        EditText emailText = (EditText) getActivity().findViewById(R.id.editText_fragment_register_email);
        EditText passwordText = (EditText) getActivity().findViewById(R.id.editText_fragment_register_password);
        EditText repasswordText = (EditText) getActivity().findViewById(R.id.editText_fragment_register_repassword);
        EditText usernameText = (EditText) getActivity().findViewById(R.id.editText_fragment_register_username);
        EditText firstnameText = (EditText) getActivity().findViewById(R.id.editText_fragment_register_firstname);
        EditText lastnameText = (EditText) getActivity().findViewById(R.id.editText_fragment_register_lastname);
        String email_Text = emailText.getText().toString();
        String password =  passwordText.getText().toString();
        String repassword = repasswordText.getText().toString();
        String username = usernameText.getText().toString();
        String firstname = firstnameText.getText().toString();
        String lastname = lastnameText.getText().toString();
        LoginFragment loginFragment = new LoginFragment();
        Bundle args = new Bundle();

        loginFragment.setArguments(args);
        boolean hasError = true;
        // String repassward = getArguments().getString("repasskey");


//        String emailTextString = getArguments().getString("key");
//        String passward = getArguments().getString("passkey");
        Credentials credentials = new Credentials.Builder(emailText.getText().toString(),
                passwordText.getText().toString(), repasswordText.getText().toString(),
                usernameText.getText().toString(), firstnameText.getText().toString(),
                lastnameText.getText().toString()).build();
        if (mListener != null) {
            if (firstname.length()>=1) {
            } else {
                hasError = false;
                firstnameText.setError("Your firstname is not valid");
            }
            if (lastname.length()>=1) {
            } else {
                hasError = false;
                lastnameText.setError("Your lastname is not valid");
            }
            if (email_Text != null && email_Text.contains("@")) {
            } else {
                hasError = false;
                emailText.setError("Your Email is not valid");
            }
            if (password.length() >= 6) {
            } else {
                hasError = false;
                passwordText.setError("Your password is not valid");
            }
            if (password.equals(repassword)) {
            } else {
                hasError = false;
                repasswordText.setError("Your password and retyped one do not match");
            }
            if (username.length()>=1) {
            } else {
                hasError = false;
                usernameText.setError("Your username is not valid");
            }

            if (hasError) {
                Uri uri = new Uri.Builder()
                        .scheme("https")
                        //.appendPath(getString(R.string.ep_base_url))
                        //.appendPath(getString(R.string.ep_register))
                        .build();
//                switch (view.getId()) {
//                    //case R.id.actualRegisterButton:
//                        //mListener.onRegisterSuccess(credentials);
//                        //build the web service URL
//
//                        //build the JSONObject
//                        JSONObject msg = credentials.asJSONObject();
//                        mCredentials = credentials;
//                        //instantiate and execute the AsyncTask.
//                        new SendPostAsyncTask.Builder(uri.toString(), msg)
//                                .onPreExecute(this::handleLoginOnPre)
//                                .onPostExecute(this::handleLoginOnPost)
//                                .onCancelled(this::handleErrorsInTask)
//                                .build().execute();
//                        Log.v("login", "onClickHere");
//                        break;
//                    default:
//                        Log.wtf("", "Didn't expect to see me...");
//                }
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
        TextView view = (TextView) v.findViewById(R.id.editText_fragment_register_email);
        //mListener.onRegisterSuccess(mCredentials);
        try {
            JSONObject resultsJSON = new JSONObject(result);
            System.out.println("yohei" + resultsJSON);
            boolean success =
                    resultsJSON.getBoolean(
                            getString(R.string.keys_json_register_success));
            if (success) {
                //Login was successful. Switch to the loadSuccessFragment.
                System.out.println("Nyotengu");
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


    public interface OnFragmentInteractionListener {
        void onRegisterSuccess(Credentials a);

        void onWaitFragmentInteractionShow();
        void onLoginSuccess(Credentials mCredentials, String string);

        void onWaitFragmentInteractionHide();
    }
}