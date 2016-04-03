package com.hackinthenorth.centralperk.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hackinthenorth.centralperk.R;
import com.hackinthenorth.centralperk.helper.MeetInMiddle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    double lat,lon;
    String item, gps;
    Marker[] ml;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mGoogleMap=googleMap;
        mGoogleMap.setMyLocationEnabled(true);
        StringBuilder sbValue = new StringBuilder(sbMethod());
        PlacesTask placesTask = new PlacesTask();
        placesTask.execute(sbValue.toString());



        mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {
                for(Marker marker :ml) {
                    if(Math.abs(marker.getPosition().latitude - latLng.latitude) < 0.05 && Math.abs(marker.getPosition().longitude - latLng.longitude) < 0.05) {
                        //Toast.makeText(MapActivity.this, "got clicked", Toast.LENGTH_SHORT).show(); //do some stuff
                        break;
                    }
                }

            }
        });

    }

    public StringBuilder sbMethod()
    {


        Bundle b = getIntent().getExtras();
        gps = b.getString("gps");
        String centroid = new MeetInMiddle().compute(gps).split (" ")[0];
        lat = Double.parseDouble(centroid.split(",")[0]);
        lon = Double.parseDouble(centroid.split(",")[1]);


        item = b.getString("item");
        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("location=" + lat + "," + lon);
        sb.append("&radius=10000");
        sb.append("&types=" + item);

        sb.append("&key=AIzaSyA1rnvqezxrQ7CfM526erLpPYCs5ZrvepE");
        sb.append("&sensor=true");

        Log.d("Map", "url: " + sb.toString());

        return sb;
    }

    private class PlacesTask extends AsyncTask<String, Integer, String>
    {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            Log.d ("result", result);
            ParserTask parserTask = new ParserTask();

            // Start parsing the Google places in JSON format
            // Invokes the "doInBackground()" method of the class ParserTask
            parserTask.execute(result);
        }
    }

    private String downloadUrl(String strUrl) throws IOException
    {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            Place_JSON placeJson = new Place_JSON();

            try {
                jObject = new JSONObject(jsonData[0]);

                places = placeJson.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }


        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {

            Log.d("Map", "list size: " + list.size());
            // Clears all the existing markers;
            mGoogleMap.clear();

            ml = new Marker[list.size()];
            String locations[] = gps.split(" ");
            for (String location : locations) {
                double latdT = Double.parseDouble(location.split(",")[0]);
                double longiT = Double.parseDouble(location.split(",")[1]);
                MarkerOptions markerOptions = new MarkerOptions();



                // Getting latitude of the place



                // Getting name
                String name = "friend";

                Log.d("Map", "place: " + name);

                // Getting vicinity
              //  String vicinity = hmPlace.get("vicinity");

                LatLng latLng = new LatLng(latdT, longiT);

                // Setting the position for the marker
                markerOptions.position(latLng);

                markerOptions.title(name);

                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                // Placing a marker on the touched position
               mGoogleMap.addMarker(markerOptions);



            }
            for (int i = 0; i < Math.min(5, list.size()); i++) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);


                // Getting latitude of the place
                double latT = Double.parseDouble(hmPlace.get("lat"));

                // Getting longitude of the place
                double lngT = Double.parseDouble(hmPlace.get("lng"));

                // Getting name
                String name = hmPlace.get("place_name");

                Log.d("Map", "place: " + name);

                // Getting vicinity
                String vicinity = hmPlace.get("vicinity");

                LatLng latLng = new LatLng(latT, lngT);

                // Setting the position for the marker
                markerOptions.position(latLng);

                markerOptions.title(name + " : " + vicinity);

                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

                // Placing a marker on the touched position
                Marker m = mGoogleMap.addMarker(markerOptions);

                ml[i] = m;
/*
                CameraUpdate center=
                        CameraUpdateFactory.newLatLng(new LatLng(lat,
                                lng));
                CameraUpdate zoom=CameraUpdateFactory.zoomTo(10);

                mGoogleMap.moveCamera(center);
                mGoogleMap.animateCamera(zoom);
                */

            }
            MarkerOptions markerOptions = new MarkerOptions();

            // Getting a place from the places list


            // Getting latitude of the place

            // Getting longitude of the place

            // Getting name
            String name = "centroid";

            Log.d("Map", "place: " + name);

            // Getting vicinity

            LatLng latLng = new LatLng(lat, lon);

            // Setting the position for the marker
            markerOptions.position(latLng);

            markerOptions.title(name);

            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

            // Placing a marker on the touched position
            Marker m = mGoogleMap.addMarker(markerOptions);

            CameraUpdate center=
                    CameraUpdateFactory.newLatLng(new LatLng(lat,
                            lon));
            CameraUpdate zoom=CameraUpdateFactory.zoomTo(12);

            mGoogleMap.moveCamera(center);
            mGoogleMap.animateCamera(zoom);

        }

    }
    public class Place_JSON {

        /**
         * Receives a JSONObject and returns a list
         */
        public List<HashMap<String, String>> parse(JSONObject jObject) {

            JSONArray jPlaces = null;
            try {
                /** Retrieves all the elements in the 'places' array */
                jPlaces = jObject.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /** Invoking getPlaces with the array of json object
             * where each json object represent a place
             */
            return getPlaces(jPlaces);
        }

        private List<HashMap<String, String>> getPlaces(JSONArray jPlaces) {
            int placesCount = jPlaces.length();
            List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> place = null;

            /** Taking each place, parses and adds to list object */
            for (int i = 0; i < placesCount; i++) {
                try {
                    /** Call getPlace with place JSON object to parse the place */
                    place = getPlace((JSONObject) jPlaces.get(i));
                    placesList.add(place);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return placesList;
        }

        /**
         * Parsing the Place JSON object
         */
        private HashMap<String, String> getPlace(JSONObject jPlace)
        {

            HashMap<String, String> place = new HashMap<String, String>();
            String placeName = "-NA-";
            String vicinity = "-NA-";
            String latitude = "";
            String longitude = "";
            String reference = "";

            try {
                // Extracting Place name, if available
                if (!jPlace.isNull("name")) {
                    placeName = jPlace.getString("name");
                }

                // Extracting Place Vicinity, if available
                if (!jPlace.isNull("vicinity")) {
                    vicinity = jPlace.getString("vicinity");
                }

                latitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lat");
                longitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lng");
                reference = jPlace.getString("reference");

                place.put("place_name", placeName);
                place.put("vicinity", vicinity);
                place.put("lat", latitude);
                place.put("lng", longitude);
                place.put("reference", reference);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return place;
        }
    }

}
