package edu.uw.chitchat;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import edu.uw.chitchat.Credentials.Credentials;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * @author Joe Lu
 * @2/5/2018
 */
                    public class LoginFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
        EditText usernameText = (EditText) getActivity().findViewById(R.id.editText_fragment_login_username);
        EditText passwordText = (EditText) getActivity().findViewById(R.id.editText_fragment_login_password);
        boolean hasError = false;
        String username_Text = usernameText.getText().toString();
        String password =  passwordText.getText().toString();
        Credentials credentials = new Credentials.Builder(usernameText.getText().toString(),
                passwordText.getText().toString()).build();
        Log.v("test", "testte");
        if (mListener != null) {
            switch (view.getId()) {
                case R.id.login_button:
                    if (username_Text.equals("")) {
                    } else {
                        usernameText.setError("Your Username is not valid");
                        hasError = true;
                    }

                    if (password.length() >= 6) {
                        //mListener.onLoginSuccess(credentials, emailText.getText().toString());
                        //build the web service URL
//                            Uri uri = new Uri.Builder()
//                                    .scheme("https")
//                                    .appendPath(getString(R.string.ep_base_url))
//                                    .appendPath(getString(R.string.ep_login))
//                                    .build();
                        //build the JSONObject
//                            JSONObject msg = credentials.asJSONObject();
//                            mCredentials = credentials;
                        //instantiate and execute the AsyncTask.

                    } else {
                        hasError = true;
                        passwordText.setError("Your password is not valid");
                    }
                    if (!hasError) {
//                        Uri uri = new Uri.Builder()
//                                .scheme("https")
//                                .appendPath(getString(R.string.ep_base_url))
//                                .appendPath(getString(R.string.ep_login))
//                                .build();
//                        //build the JSONObject
//                        JSONObject msg = credentials.asJSONObject();
//                        mCredentials = credentials;
//                        new SendPostAsyncTask.Builder(uri.toString(), msg)
//                                .onPreExecute(this::handleLoginOnPre)
//                                .onPostExecute(this::handleLoginOnPost)
//                                .onCancelled(this::handleErrorsInTask)
//                                .build().execute();
                    }

                    break;
                case R.id.register_button:
                    Log.wtf("yohei", "register_button");
                    mListener.onRegisterClicked();;
                    break;
                default:
                    Log.wtf("", "Didn't expect to see me...");
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */


    public interface OnFragmentInteractionListener {
        void onRegisterClicked();
        void onLoginFragmentInteraction(Uri uri);

        void onFragmentInteraction(Uri uri);
    }
}
