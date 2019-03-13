package edu.uw.chitchat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import edu.uw.chitchat.broadcast.Broadcast;
import edu.uw.chitchat.utils.PrefHelper;
import edu.uw.chitchat.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * @author Logan Jenny
 * @2/9/2018
 */

public class WeatherFragment extends Fragment {

//    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
//
//    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
//
//    private static final int MY_PERMISSIONS_LOCATIONS = 8414;
//    private LocationRequest mLocationRequest;

//    private FusedLocationProviderClient mFusedLocationClient;
//    private LocationCallback mLocationCallback;

    private TextView mTemperature;
    private TextView mHumidity;
    private TextView mDescription;
    Location mCurrentLocation;
    private RecyclerView m24hrRecyclerView;
    private RecyclerView mWeekRecyclerView;
    private EditText mZIPCode;
    //private Button mGetWeather;


    public WeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather , container, false);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            Double lat = (Double) bundle.getSerializable(getString(R.string.keys_weather_latitude));
            Double lng = (Double) bundle.getSerializable(getString(R.string.keys_weather_longtitude));
            mCurrentLocation = new Location("");
            mCurrentLocation.setLatitude(lat);
            mCurrentLocation.setLongitude(lng);
            Log.wtf("world", "afaschv,");
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
        m24hrRecyclerView = view.findViewById(R.id.recyclerView_weather_24hr);
        mWeekRecyclerView = view.findViewById(R.id.recycleView_weather_7days);
        mZIPCode = view.findViewById(R.id.editText_fragment_weather_zipcode);
//        mGetWeather = view.findViewById(R.id.button_fragment_weather_getWeather);
//        mGetWeather.setOnClickListener(new getWeatherButtonClick());
        setZipCodeListener();

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_weather))
                .appendPath(getString(R.string.ep_getWeather))
                .build();

        JSONObject jsonSend = new JSONObject();
        try {
            Log.wtf("world1", mCurrentLocation.getLatitude()+"");
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
                        .onPostExecute(this::handleGetWeatherWithZipPostExecute)
                        .build().execute();
                }
            }

            private void handleGetWeatherWithZipPostExecute(String result) {
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

                            Context context = m24hrRecyclerView.getContext();
                            m24hrRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                            m24hrRecyclerView.setAdapter(new My24BroadcastRecycleViewAdapter(broadcasts));
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


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("ERROR!", e.getMessage());

                }

            }
        });
    }

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

    public void getWeatherFromMap() {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_weather))
                .appendPath(getString(R.string.ep_getWeather))
                .build();

        JSONObject jsonSend = new JSONObject();
        try {
            Log.wtf("world1", mCurrentLocation.getLatitude()+"");
            jsonSend.put("latitude", mCurrentLocation.getLatitude());
            jsonSend.put("longtitude", mCurrentLocation.getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(uri.toString(), jsonSend)
                .onPostExecute(this::handleGetWeatherPostExecute)
                .build().execute();
    }

    @Override
    public void onPause() {
        super.onPause();
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

                    Context context = m24hrRecyclerView.getContext();
                    m24hrRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    m24hrRecyclerView.setAdapter(new My24BroadcastRecycleViewAdapter(broadcasts));
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


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            //onWaitFragmentInteractionHide();
        }

    }
}
