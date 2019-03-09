package edu.uw.chitchat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;

import edu.uw.chitchat.contactlist.ContactList;
import edu.uw.chitchat.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * @author Logan Jenny
 * @2/9/2018
 */

public class WeatherFragment extends Fragment {

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private static final int MY_PERMISSIONS_LOCATIONS = 8414;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;


    private TextView mLocationTextView;

    private TextView mTemperature;
    private TextView mHumidity;
    private TextView mDescription;

    public WeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather , container, false);

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

        mLocationTextView = view.findViewById(R.id.tv_weather_location);
        mTemperature = view.findViewById(R.id.tv_weather_temperature);
        mHumidity = view.findViewById(R.id.tv_weather_humidity);
        mDescription = view.findViewById(R.id.tv_weather_description);



        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());


        if (ActivityCompat.checkSelfPermission(getActivity(),  Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(getActivity(),  Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_LOCATIONS);
        } else {
            requestLocation();
        }


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    setLocation(location);

                    Log.d("LOCATION UPDATE!", location.toString());
                }
            };
        };

        createLocationRequest();


        return view;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // locations-related task you need to do.
                    requestLocation();
                } else {
                    Log.d("PERMISSION DENIED", "Nothing to see or do here.");
                    getActivity().finishAndRemoveTask();
                }

                return;
            }
        }
    }


    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(getActivity(),  Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            Log.d("REQUEST LOCATION", "User did NOT allow permission to request location!");
        } else {
            Log.d("Joe test","it reaches here1");
            Task<Location> location = mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            Log.d("Joe test","it reaches here");
                            if (location != null) {
                                Log.d("Joe test","it never reaches here");
                                setLocation(location);
                                Log.d("LOCATION", location.toString());
                            }
                        }
                    });
        }
    }


    /**
     * Create and configure a Location Request used when retrieving location updates
     */
    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public  void onResume() {
        super.onResume();
        startLocationUpdates();
    }
    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }


    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        }
    }


    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void setLocation(final Location location) {
        mCurrentLocation = location;
        mLocationTextView.setText(mCurrentLocation.getLatitude() + " " +
                mCurrentLocation.getLongitude());


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
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            //onWaitFragmentInteractionHide();
        }

    }

}
