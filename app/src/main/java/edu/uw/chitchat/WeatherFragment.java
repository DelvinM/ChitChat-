package edu.uw.chitchat;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import edu.uw.chitchat.broadcast.Broadcast;
import edu.uw.chitchat.utils.PrefHelper;
import edu.uw.chitchat.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * @author Zhou Lu
 * @2/9/2019
 */

public class WeatherFragment extends Fragment {


    /**a field for the textView to display temperature*/
    private TextView mTemperature;

    /**a field for the textView to display humidity*/
    private TextView mHumidity;

    /**a field for the textView to display summary about the weather*/
    private TextView mDescription;

    /**a field to store the location*/
    Location mCurrentLocation;

    /**a class field to referrence the recycle view about 48hr*/
    private RecyclerView m48hrRecyclerView;

    /**a class field to referrence the recycle view for the week broadcast*/
    private RecyclerView mWeekRecyclerView;

    /**a class field to display the EditText for zip code*/
    private EditText mZIPCode;

    /**a class field to display the city from the current location*/
    private TextView mCurrentCity;


    /**
     * Required empty public constructor
     */
    public WeatherFragment() {
        // Required empty public constructor
    }

    /**
     * some async task happen here, so user no need to do anything, when switch into the weather fragment.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather , container, false);
        if (getArguments() != null) {
            // get the bundle pass from the home activity
            Bundle bundle = getArguments();
            Double lat = (Double) bundle.getSerializable(getString(R.string.keys_weather_latitude));
            Double lng = (Double) bundle.getSerializable(getString(R.string.keys_weather_longtitude));
            mCurrentLocation = new Location("");
            mCurrentLocation.setLatitude(lat);
            mCurrentLocation.setLongitude(lng);

        }

        FloatingActionButton fab = view.findViewById(R.id.fab_weather);
        fab.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentLocation == null) {
                    Snackbar.make(view, "Please wait for location to enable", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Intent i = new Intent(getActivity(), MapsActivity.class);
                    //pass the current location on to the MapActivity when it is loaded
                    i.putExtra("LOCATION", mCurrentLocation);
                    startActivity(i);
                }
            }
        }));


        mTemperature = view.findViewById(R.id.tv_weather_temperature);
        mHumidity = view.findViewById(R.id.tv_weather_humidity);
        mDescription = view.findViewById(R.id.tv_weather_description);
        m48hrRecyclerView = view.findViewById(R.id.recyclerView_weather_24hr);
        mWeekRecyclerView = view.findViewById(R.id.recycleView_weather_7days);
        mZIPCode = view.findViewById(R.id.editText_fragment_weather_zipcode);
        mCurrentCity = view.findViewById(R.id.tv_weather_Current);

        //this method is set the zip code edit text listner for event when edit text lost focus
        setZipCodeListener();


        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_weather))
                .appendPath(getString(R.string.ep_getWeather))
                .build();

        JSONObject jsonSend = new JSONObject();
        try {

            jsonSend.put("latitude", mCurrentLocation.getLatitude());
            jsonSend.put("longtitude", mCurrentLocation.getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(uri.toString(), jsonSend)
                .onPostExecute(this::handleGetWeatherPostExecute)
                .build().execute();

        return view;

    }

    /**
     * this method will create async task to get the city name and set the city name.
     */
    protected void getCityName() {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_weather))
                .appendPath(getString(R.string.ep_getCity))
                .build();

        JSONObject jsonSend = new JSONObject();
        try {
            jsonSend.put("latitude", mCurrentLocation.getLatitude());
            jsonSend.put("longtitude", mCurrentLocation.getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(uri.toString(), jsonSend)
                .onPostExecute(this::handleGetCityPostExecute)
                .build().execute();
    }


    /**
     * this method is to set up the listener for zip code edit text.
     */
    public void setZipCodeListener() {
        mZIPCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                /* When focus is lost check that the text field
                 * has valid values.
                 */
                if (!hasFocus) {
                    Uri uri = new Uri.Builder()
                        .scheme("https")
                        .appendPath(getString(R.string.ep_base_url))
                        .appendPath(getString(R.string.ep_weather))
                        .appendPath(getString(R.string.ep_getLatlong))
                        .build();

                JSONObject jsonSend = new JSONObject();
                try {
                    jsonSend.put("zip", mZIPCode.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new SendPostAsyncTask.Builder(uri.toString(), jsonSend)
                        .onPostExecute(this::handleGetLatLngFromZipPostExecute)
                        .build().execute();
                }
            }

            /**
             * this will handle for async task to get the lat long from a zip code and
             * make an get weather async call.
             * @param result json string contains lat long
             */
            private void handleGetLatLngFromZipPostExecute(String result) {
                try {
                    JSONObject root = new JSONObject(result);
                    if (root.has("results")) {
                        JSONArray results = root.getJSONArray("results");
                        Log.wtf("result test", result);
                        JSONObject containsAll = results.getJSONObject(0);
                        JSONObject geometry = containsAll.getJSONObject("geometry");
                        JSONObject location = geometry.getJSONObject("location");
                        mCurrentLocation.setLatitude(Double.valueOf(location.getString("lat")));
                        mCurrentLocation.setLongitude(Double.valueOf(location.getString("lng")));

                        Uri uri = new Uri.Builder()
                                .scheme("https")
                                .appendPath(getString(R.string.ep_base_url))
                                .appendPath(getString(R.string.ep_weather))
                                .appendPath(getString(R.string.ep_getWeather))
                                .build();

                        JSONObject jsonSend = new JSONObject();
                        try {
                            jsonSend.put("latitude", mCurrentLocation.getLatitude());
                            jsonSend.put("longtitude", mCurrentLocation.getLongitude());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new SendPostAsyncTask.Builder(uri.toString(), jsonSend)
                                .onPostExecute(this::handleGetWeatherPostExecute)
                                .build().execute();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("ERROR!", e.getMessage());
                }
            }

            /**
             * this will make get async call to get weather from lat long.
             * @param result json string contains weather info.
             */
            private void handleGetWeatherPostExecute(String result) {

                try {
                    JSONObject root = new JSONObject(result);
                    if (root.has(getString(R.string.keys_weather_currently))) {
                        JSONObject currently = root.getJSONObject(getString(R.string.keys_weather_currently));
                        StringBuilder sb = new StringBuilder("Temperature: " + currently.getString(getString(R.string.keys_weather_temperature)) + "\u00b0F");
                        mTemperature.setText(sb.toString());
                        sb = new StringBuilder("Humidity: " + currently.getString(getString(R.string.keys_weather_humidity)) + "%");
                        mHumidity.setText(sb.toString());
                        sb = new StringBuilder("Summary: " + currently.getString(getString(R.string.keys_weather_summary)));
                        mDescription.setText(sb.toString());
                    } else {
                        Log.e("ERROR!", "No response");
                        //notify user
                    }

                    if (root.has(getString(R.string.keys_weather_hourly))) {
                        JSONObject hourly = root.getJSONObject(getString(R.string.keys_weather_hourly));
                        if (hourly.has(getString(R.string.keys_weather_data))) {
                            JSONArray data = hourly.getJSONArray(getString(R.string.keys_weather_data));
                            List<Broadcast> broadcasts = new LinkedList<>();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject broadcast = data.getJSONObject(i);
                                broadcasts.add(new Broadcast(
                                        broadcast.getString(getString(R.string.keys_weather_temperature)),
                                        broadcast.getString(getString(R.string.keys_weather_time)),
                                        broadcast.getString(getString(R.string.keys_weather_summary)),
                                        broadcast.getString(getString(R.string.keys_weather_humidity)),
                                        broadcast.getString(getString(R.string.keys_weather_icon))));
                            }

                            Context context = m48hrRecyclerView.getContext();
                            m48hrRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                            m48hrRecyclerView.setAdapter(new My24BroadcastRecycleViewAdapter(broadcasts));
                        }
                    }

                    if (root.has(getString(R.string.keys_weather_daily))) {
                        JSONObject daily = root.getJSONObject(getString(R.string.keys_weather_daily));
                        if (daily.has(getString(R.string.keys_weather_data))){
                            JSONArray data = daily.getJSONArray(getString(R.string.keys_weather_data));
                            List<Broadcast> broadcasts = new LinkedList<>();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject broadcast = data.getJSONObject(i);
                                broadcasts.add(new Broadcast(
                                        broadcast.getString(getString(R.string.keys_weather_temperatureLow)),
                                        broadcast.getString(getString(R.string.keys_weather_time)),
                                        broadcast.getString(getString(R.string.keys_weather_summary)),
                                        broadcast.getString(getString(R.string.keys_weather_humidity)),
                                        broadcast.getString(getString(R.string.keys_weather_icon))));
                            }

                            Context context = mWeekRecyclerView.getContext();
                            mWeekRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                            mWeekRecyclerView.setAdapter(new MyWeekBroadcastRecycleViewAdapter(broadcasts));
                        }
                    }

                    getCityName();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("ERROR!", e.getMessage());

                }

            }

            /**
             * this is a method to get the city name from lat lng.
             */
            protected void getCityName() {
                Uri uri = new Uri.Builder()
                        .scheme("https")
                        .appendPath(getString(R.string.ep_base_url))
                        .appendPath(getString(R.string.ep_weather))
                        .appendPath(getString(R.string.ep_getCity))
                        .build();

                JSONObject jsonSend = new JSONObject();
                try {
                    jsonSend.put("latitude", mCurrentLocation.getLatitude());
                    jsonSend.put("longtitude", mCurrentLocation.getLongitude());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new SendPostAsyncTask.Builder(uri.toString(), jsonSend)
                        .onPostExecute(this::handleGetCityPostExecute)
                        .build().execute();
            }

            /**
             * this will handle the result from async task to get city name.
             * @param result json string contain address
             */
            private void handleGetCityPostExecute(String result) {
                try {
                    JSONObject root = new JSONObject(result);
                    if (root.has("results")) {
                        JSONArray results = root.getJSONArray("results");

                        JSONObject address_components = results.getJSONObject(0);

                        String formatted_address = address_components.getString(getString(R.string.keys_formatted_address));
                        String city = parseLocation(formatted_address);
                        mCurrentCity.setText(city);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("ERROR!", e.getMessage());
                }
            }
        });
    }

    /**
     * the on resume will check does user come from map activity
     */
    @Override
    public void onResume() {
        super.onResume();
        //startLocationUpdates();
        if (PrefHelper.getStringPreference("latitude", getActivity()) != null && mCurrentLocation != null && PrefHelper.getStringPreference("FromWhere", getActivity()) == "map") {
            String lat = PrefHelper.getStringPreference("latitude", getActivity());
            String lng = PrefHelper.getStringPreference("longtitude", getActivity());
            mCurrentLocation.setLatitude(Double.valueOf(lat));
            mCurrentLocation.setLongitude(Double.valueOf(lng));
            PrefHelper.putStringPreference("FromWhere", "", getActivity());
            getWeatherFromMap();
            Log.wtf("world", lat);
        }
    }

    /**
     * method to make async call to get weather info
     */
    public void getWeatherFromMap() {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_weather))
                .appendPath(getString(R.string.ep_getWeather))
                .build();

        JSONObject jsonSend = new JSONObject();
        try {
            jsonSend.put("latitude", mCurrentLocation.getLatitude());
            jsonSend.put("longtitude", mCurrentLocation.getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(uri.toString(), jsonSend)
                .onPostExecute(this::handleGetWeatherPostExecute)
                .build().execute();
    }


    /**
     * handle the post execute method
     * @param result the json result from the web service api
     */
    private void handleGetWeatherPostExecute(String result) {

        try {
            JSONObject root = new JSONObject(result);
            if (root.has(getString(R.string.keys_weather_currently))) {
                JSONObject currently = root.getJSONObject(getString(R.string.keys_weather_currently));
                StringBuilder sb = new StringBuilder("Temperature: " + currently.getString(getString(R.string.keys_weather_temperature)) + "\u00b0F");
                mTemperature.setText(sb.toString());
                sb = new StringBuilder("Humidity: " + currently.getString(getString(R.string.keys_weather_humidity)) + "%");
                mHumidity.setText(sb.toString());
                sb = new StringBuilder("Summary: " + currently.getString(getString(R.string.keys_weather_summary)));
                mDescription.setText(sb.toString());
            } else {
                Log.e("ERROR!", "No response");
                //notify user
            }

            if (root.has(getString(R.string.keys_weather_hourly))) {
                JSONObject hourly = root.getJSONObject(getString(R.string.keys_weather_hourly));
                if (hourly.has(getString(R.string.keys_weather_data))) {
                    JSONArray data = hourly.getJSONArray(getString(R.string.keys_weather_data));
                    List<Broadcast> broadcasts = new LinkedList<>();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject broadcast = data.getJSONObject(i);
                        broadcasts.add(new Broadcast(
                                broadcast.getString(getString(R.string.keys_weather_temperature)),
                                broadcast.getString(getString(R.string.keys_weather_time)),
                                broadcast.getString(getString(R.string.keys_weather_summary)),
                                broadcast.getString(getString(R.string.keys_weather_humidity)),
                                broadcast.getString(getString(R.string.keys_weather_icon))));
                    }

                    Context context = m48hrRecyclerView.getContext();
                    m48hrRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    m48hrRecyclerView.setAdapter(new My24BroadcastRecycleViewAdapter(broadcasts));
                }
            }

            if (root.has(getString(R.string.keys_weather_daily))) {
                JSONObject daily = root.getJSONObject(getString(R.string.keys_weather_daily));
                if (daily.has(getString(R.string.keys_weather_data))){
                    JSONArray data = daily.getJSONArray(getString(R.string.keys_weather_data));
                    List<Broadcast> broadcasts = new LinkedList<>();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject broadcast = data.getJSONObject(i);
                        broadcasts.add(new Broadcast(
                                broadcast.getString(getString(R.string.keys_weather_temperatureLow)),
                                broadcast.getString(getString(R.string.keys_weather_time)),
                                broadcast.getString(getString(R.string.keys_weather_summary)),
                                broadcast.getString(getString(R.string.keys_weather_humidity)),
                                broadcast.getString(getString(R.string.keys_weather_icon))));
                    }

                    Context context = mWeekRecyclerView.getContext();
                    mWeekRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                    mWeekRecyclerView.setAdapter(new MyWeekBroadcastRecycleViewAdapter(broadcasts));
                }
            }

            getCityName();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            //onWaitFragmentInteractionHide();
        }

    }

    /**
     * this handle when get result from getting address info.
     * @param result json string ocntains address info.
     */
    private void handleGetCityPostExecute(String result) {
        try {
            JSONObject root = new JSONObject(result);
            if (root.has("results")) {
                JSONArray results = root.getJSONArray("results");

                JSONObject address_components = results.getJSONObject(0);

                String formatted_address = address_components.getString(getString(R.string.keys_formatted_address));
                String city = parseLocation(formatted_address);
                mCurrentCity.setText(city);

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
        }
    }


    /**
     * parse the address to return a city name
     * @param address contains full address
     * @return city name
     */
    private String parseLocation(String address) {
       StringBuilder sb = new StringBuilder();
       String[] arr = address.split(",");
       sb.append(arr[1]);
       return sb.toString();
    }
}
