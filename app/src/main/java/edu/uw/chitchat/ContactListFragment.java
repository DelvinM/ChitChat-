package edu.uw.chitchat;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.uw.chitchat.contactlist.ContactList;
import edu.uw.chitchat.contactlist.ContactListGenerator;


/**
 * @author Yohei Sato
 *
 */
public class ContactListFragment extends Fragment implements View.OnClickListener{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    public static final String ARG_CONTACT_LIST = "contact lists";
    private List<ContactList> mContactlist;
    private ContactList myData = new ContactList.Builder("yohei", "yohei03@uw.edu").build();
    public static final String CONTACT_LIST_LIST = "contact lists";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContactListFragment() {
        //mContactlist.add(myData);
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ContactListFragment newInstance(Bundle b) {

        ContactListFragment fragment = new ContactListFragment();
        b.putSerializable(ARG_CONTACT_LIST, 2);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.wtf("yohei200", "Yohei2000000");
            mContactlist = new ArrayList<ContactList>(
                    mContactlist = Arrays.asList((ContactList[]) getArguments().getSerializable(ARG_CONTACT_LIST)));

        } else {
            Log.wtf("Yohei500", "hello world?");
            mContactlist = Arrays.asList( ContactListGenerator.BLOGS );
        }

//        if (getArguments() != null) {
//            Log.wtf("Yohei300", "yohei300");
//            mContactlist = new ArrayList<ContactList>(
//                    Arrays.asList((ContactList[]) getArguments().getSerializable(ARG_CONTACT_LIST)));
//        } else {
//            mContactlist = Arrays.asList( ContactListGenerator.BLOGS );
//        }

//        if (getArguments() != null) {
//
//            mContactlist = new ArrayList<ContactList>(
//                    Arrays.asList((ContactList[]) getArguments().getSerializable(CONTACT_LIST_LIST)));
//        } else {
//
//            //mSetList = Arrays.asList(SetListGenerator.SETLISTS);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact_list, container, false);
        Log.wtf("yohei100", "test view");
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_getall))
                .build();
        String emailstored = getSharedPreference (getString(R.string.keys_email_stored_onRegister));

        JSONObject test = new JSONObject();
        try {
            test.put("email", emailstored);
        } catch (JSONException e) {
            e.printStackTrace();
        }


//        new SendPostAsyncTask.Builder(uri.toString(), test)
//                .onPreExecute(this::handleLoginOnPre)
//                .onPostExecute(this::handleContactlistGetOnPostExecute)
//                .onCancelled(this::handleErrorsInTask)
//               .addHeaderField("authorization", mJwToken)
//                .build().execute();



//        try {
//            JSONArray json = readJsonFromUrl(uri.toString());
//            JSONObject obj = (JSONObject)json.get(5);
//            System.out.println(obj.get("title"));
//            List<String> list = new ArrayList<String>();
//            for(int i = 0; i < json.length(); i++){
//                list.add(json.getJSONObject(i).getString("name"));
//            }
//            Log.wtf("yohei", list.get(0));
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }



        //String json = getJSON(uri.toString(), 10);


//        try {
//            json = readJsonFromUrl(uri.toString());
//            Log.wtf(json.toString(), json.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        //Log.wtf("YOHEI", json);
        // Map<String, String> query_pairs = new LinkedHashMap<String, String>();
//        String query = uri.getQuery();
//        String[] pairs = query.split("&");
//        for (String pair : pairs) {
//            int idx = pair.indexOf("=");
//            try {
//                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
//        for ( Map.Entry<String, String> entry : query_pairs.entrySet()) {
//            Log.wtf(entry.getKey(),entry.getValue());
//            //String key = entry.getKey();
//            //String value = entry.getValue();
//            // do something with key and/or tab
//        }

        if (v instanceof RecyclerView) {
            Log.wtf("yohei100", "first if");
            Context context = v.getContext();
            RecyclerView recyclerView = (RecyclerView) v;
            Log.wtf("yohei100", "JOe");

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            recyclerView.setAdapter(new MyContactRecyclerViewAdapter(mContactlist, mListener));
        }
        return v;
    }

    private void handleLoginOnPre() {
            mListener.onWaitFragmentInteractionShow();

    }

    private void handleErrorsInTask(String result) {
        Log.e("ASYNC_TASK_ERROR", result);
    }

    private String getSharedPreference (String key) {
        SharedPreferences sharedPref =
                getActivity().getSharedPreferences(key, Context.MODE_PRIVATE);
        return sharedPref.getString(key, null);
    }

//    private void handleContactlistGetOnPostExecute(String result) {
//        //parse JSON
//        try {
//            JSONObject root = new JSONObject(result);
//            if (root.has(getString(R.string.keys_json_blogs_response))) {
//                JSONObject response = root.getJSONObject(
//                        getString(R.string.keys_json_blogs_response));
//                if (response.has(getString(R.string.keys_json_blogs_data))) {
//                    JSONArray data = response.getJSONArray(
//                            getString(R.string.keys_json_blogs_data));
//                    List<ContactList> contactlistthing = new ArrayList<>();
//                    for(int i = 0; i < data.length(); i++) {
//                        JSONObject CL = data.getJSONObject(i);
//
//                        contactlistthing.add(new ContactList.Builder(
//                                CL.getString(
//                                        getString(R.string.keys_json_blogs_pubdate)),
//                                CL.getString(
//                                        getString(R.string.keys_json_blogs_title)))
//                                .build());
//                    }
//                    ContactList[] blogsAsArray = new ContactList[contactlistthing.size()];
//                    blogsAsArray = contactlistthing.toArray(blogsAsArray);
//                    Bundle args = new Bundle();
//                    args.putSerializable(ContactListFragment.ARG_CONTACT_LIST, blogsAsArray);
//                    Fragment frag = new ContactListFragment();
//                    frag.setArguments(args);
//                    onWaitFragmentInteractionHide();
//                    loadFragment(frag);
//                } else {
//                    Log.e("ERROR!", "No data array");
//                    //notify user
//                    onWaitFragmentInteractionHide();
//                }
//            } else {
//                Log.e("ERROR!", "No response");
//                //notify user
//                //onWaitFragmentInteractionHide();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.e("ERROR!", e.getMessage());
//            //notify user
//            //onWaitFragmentInteractionHide();
//        }
//    }

    private void onWaitFragmentInteractionHide() {
    }


//    private void loadFragment(Fragment frag) {
//        FragmentTransaction transaction = getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.frame_home_fragmentcontainer, frag)
//                .addToBackStack(null);
//        // Commit the transaction
//        transaction.commit();
//    }
    public String getJSON(String url, int timeout) {
        HttpURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log( Level.SEVERE, null, ex);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }

    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONArray readJsonFromUrl(String url) throws IOException {
        // String s = URLEncoder.encode(url, "UTF-8");
        // URL url = new URL(s);
        InputStream is = new URL(url).openStream();
        JSONArray json = null;
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            json = new JSONArray(jsonText);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            is.close();
        }
        return json;
    }



//    protected String doInBackground(String... params) {
//
//        HttpClient client = new DefaultHttpClient();
//        HttpGet request = new HttpGet(params[0]);
//        HttpResponse response;
//        String result = null;
//        try {
//            response = client.execute(request);
//            HttpEntity entity = response.getEntity();
//
//            if (entity != null) {
//
//                // A Simple JSON Response Read
//                InputStream instream = entity.getContent();
//                result = convertStreamToString(instream);
//                // now you have the string representation of the HTML request
//                System.out.println("RESPONSE: " + result);
//                instream.close();
//                if (response.getStatusLine().getStatusCode() == 200) {
//                    netState.setLogginDone(true);
//                }
//
//            }
//            // Headers
//            org.apache.http.Header[] headers = response.getAllHeaders();
//            for (int i = 0; i < headers.length; i++) {
//                System.out.println(headers[i]);
//            }
//        } catch (ClientProtocolException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        } catch (IOException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
//        return result;
//    }





//    private static String readAll(Reader rd) throws IOException {
//        StringBuilder sb = new StringBuilder();
//        int cp;
//        while ((cp = rd.read()) != -1) {
//            sb.append((char) cp);
//        }
//        return sb.toString();
//    }
//
//    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
//        InputStream is = new URL(url).openStream();
//        try {
//            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
//            String jsonText = readAll(rd);
//            JSONObject json = new JSONObject(jsonText);
//            return json;
//        } finally {
//            is.close();
//        }
//    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name

        void onListFragmentInteraction(ContactList mItem);

        void onWaitFragmentInteractionShow();
    }


}
