package dev.salgino.gasapp.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import dev.salgino.gasapp.R;
import dev.salgino.gasapp.fragment.GraphFragment;
import dev.salgino.gasapp.graphing.Controller;
import dev.salgino.gasapp.model.Assignment;
import dev.salgino.gasapp.model.Station;
import dev.salgino.gasapp.model.User;
import dev.salgino.gasapp.utils.DirectionsJSONParser;

import static dev.salgino.gasapp.utils.GlobalVars.BASE_IP;
import static dev.salgino.gasapp.utils.GlobalVars.PALU_CITY_LATLNG;
import static dev.salgino.gasapp.utils.GlobalVars.START_LOCATION_DISTRICT;
import static dev.salgino.gasapp.utils.GlobalVars.START_LOCATION_LAT;
import static dev.salgino.gasapp.utils.GlobalVars.START_LOCATION_LNG;
import static dev.salgino.gasapp.utils.GlobalVars.START_LOCATION_NAME;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Gson gson;

    private GoogleMap mMap;
    private User user;
    private List<Assignment> list;
    private List<Station> stationList;

    private FloatingActionButton fab_route;
    private FloatingActionButton fab_graph;
    private FloatingActionButton fab_return_route;

    private int controllerLog, previous;

    private  String start;
    private  String end;
    private Assignment assignment;

    private MarkerOptions markerOptions = new MarkerOptions();
    private LatLng latLng;

    List<Station> tempList = new ArrayList<>();

    List<Assignment> assignmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        previous = i.getIntExtra("previous", -1);
        if ((previous==0)||(previous==1)||(previous==2)) {
            controllerLog = Controller.main(i);
            switch (controllerLog) {
                case 1:
                    //Toast.makeText(this, TextsEN.getErrorByPosition(0), Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    //Toast.makeText(this, TextsEN.getInsertionByPosition(0), Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    //Toast.makeText(this, TextsEN.getErrorByPosition(1), Toast.LENGTH_LONG).show();
                    break;
                case 4:
                    //Toast.makeText(this, TextsEN.getErrorByPosition(2), Toast.LENGTH_LONG).show();
                    break;
                case 5:
                    //Toast.makeText(this, TextsEN.getInsertionByPosition(1), Toast.LENGTH_LONG).show();
                    break;
                case 6:
                    //Toast.makeText(this, TextsEN.getErrorByPosition(6), Toast.LENGTH_LONG).show();
                    break;
            }
        }

        setContentView(R.layout.activity_maps);

        gson = new Gson();

        fab_route = findViewById(R.id.fab_route);
        fab_graph = findViewById(R.id.fab_graph);
        fab_return_route = findViewById(R.id.fab_return_route);

        list = new ArrayList<>();
        stationList = new ArrayList<>();

        Assignment assignment = null;

        fab_graph.setVisibility(View.VISIBLE);
        fab_return_route.setVisibility(View.VISIBLE);

        if (getIntent().hasExtra("data_assignment")) {
            user = getIntent().getParcelableExtra("data_assignment");

            assignmentList = user.getAssignment();

            assignment = new Assignment();
            assignment.setId("-1");
            assignment.setLatitude(String.valueOf(START_LOCATION_LAT));
            assignment.setLongitude(String.valueOf(START_LOCATION_LNG));
            assignment.setStationName(String.valueOf(START_LOCATION_NAME));
            list.add(assignment);

            for (Assignment task: assignmentList){
                list.add(task);
            }

        }else{
            fab_graph.setVisibility(View.GONE);
            loadDataAllLocation();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fab_graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphFragment dialog = new GraphFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                dialog.show(ft, GraphFragment.TAG);
            }
        });



        fab_return_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                if (tempList!=null && !tempList.isEmpty()){

                    String destLat = "0";
                    String destLng = "0";

                    Station maxDist = Collections.max(tempList, new DistComp());
                    //System.out.println("Station with max dist: "+maxDist.getDestination()+"-"+maxDist.getDistance());

                    for (Assignment assignment1:assignmentList){

                        if (assignment1.getStationName().equals(maxDist.getDestination())){
                            destLat = assignment1.getLatitude();
                            destLng = assignment1.getLongitude();

                            createMarker(Double.parseDouble(destLat), Double.parseDouble(destLng), maxDist.getDestination(), maxDist.getDistrict());

                        }
                    }

                    createMarker(START_LOCATION_LAT, START_LOCATION_LNG, START_LOCATION_NAME, START_LOCATION_DISTRICT);


                    String url = getDirectionsUrl(Double.parseDouble(destLat),  Double.parseDouble(destLng),
                            START_LOCATION_LAT, START_LOCATION_LNG);
                    DownloadReturn downloadTask = new DownloadReturn(start, end);

                    downloadTask.execute(url);
                }
            }
        });

        fab_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                if (getIntent().hasExtra("data_assignment")) {
                    if (list != null && !list.isEmpty()) {

                        for (int i = 0; i < list.size(); i++) {

                            createMarker(Double.parseDouble(list.get(i).getLatitude()), Double.parseDouble(list.get(i).getLongitude()), list.get(i).getStationName(), list.get(i).getDistrict());

                            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                            intent.putExtra("previous", 3);
                            intent.putExtra("vertex", String.valueOf(i) + ". " + list.get(i).getStationName());
                            Controller.main(intent);

                            for (int x = i + 1; x < list.size(); x++) {
                                start = list.get(i).getStationName();
                                end = list.get(x).getStationName();

                                String url = getDirectionsUrl(
                                        Double.parseDouble(list.get(i).getLatitude()), Double.parseDouble(list.get(i).getLongitude()),
                                        Double.parseDouble(list.get(x).getLatitude()), Double.parseDouble(list.get(x).getLongitude()));
                                DownloadTask downloadTask = new DownloadTask(start, end);

                                downloadTask.execute(url);

                            }
                        }
                    }

                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        float zoomLevel = 12.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PALU_CITY_LATLNG, zoomLevel));

        if (getIntent().hasExtra("data_assignment")) {
            if (list!=null && !list.isEmpty()){

                for (int i=0;i<list.size();i++){

                    createMarker(Double.parseDouble(list.get(i).getLatitude()), Double.parseDouble(list.get(i).getLongitude()), list.get(i).getStationName(), list.get(i).getDistrict());

                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    intent.putExtra("previous", 3);
                    intent.putExtra("vertex", String.valueOf(i)+". "+list.get(i).getStationName());
                    Controller.main(intent);

                    for (int x = i + 1; x < list.size(); x++) {
                        start = list.get(i).getStationName();
                        end = list.get(x).getStationName();

                        String url = getDirectionsUrl(
                                Double.parseDouble(list.get(i).getLatitude()), Double.parseDouble(list.get(i).getLongitude()),
                                Double.parseDouble(list.get(x).getLatitude()), Double.parseDouble(list.get(x).getLongitude()));
                        DownloadTask downloadTask = new DownloadTask(start, end);

                        downloadTask.execute(url);

                    }
                }
            }
        }else{
            loadDataAllLocation();
        }

    }

    private void addMarker(LatLng latlng, final String title, final String snippet) {
        markerOptions.position(latlng);
        markerOptions.title(title);
        markerOptions.snippet(snippet);
        mMap.addMarker(markerOptions);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.showInfoWindow();
            }
        });
    }

    protected Marker createMarker(double latitude, double longitude, String title, String snippet) {

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        String a;
        String b;

        DownloadTask(String a, String b) {
            this.a = a;
            this.b = b;
        }

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);

                //Log.e("data", data);

                try {
                    JSONObject jObject = new JSONObject(data);
                    DirectionsJSONParser parser = new DirectionsJSONParser();

                    JSONArray arrRoute = jObject.getJSONArray("routes");

                    //Log.e("arrRoute", arrRoute.toString(1));

                    JSONObject objRoute = arrRoute.getJSONObject(0);
                    JSONArray arrLog = objRoute.getJSONArray("legs");
                    JSONObject objDistance = arrLog.getJSONObject(0);
                    String distance = objDistance.getString("distance");
                    JSONObject objFinalDistance = new JSONObject(distance);
                    distanceValue = objFinalDistance.getString("value");

                    //Log.e("a", a);
                    //Log.e("b", b);
                    //Log.e("distanceValue1", distanceValue);

                    setNode(a,b,distanceValue);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);

        }
    }

    private class DownloadReturn extends AsyncTask<String, Void, String> {

        String a;
        String b;

        DownloadReturn(String a, String b) {
            this.a = a;
            this.b = b;
        }

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);

                try {
                    JSONObject jObject = new JSONObject(data);
                    DirectionsJSONParser parser = new DirectionsJSONParser();

                    JSONArray arrRoute = jObject.getJSONArray("routes");

                    //Log.e("arrRoute", arrRoute.toString(1));

                    JSONObject objRoute = arrRoute.getJSONObject(0);
                    JSONArray arrLog = objRoute.getJSONArray("legs");
                    JSONObject objDistance = arrLog.getJSONObject(0);
                    String distance = objDistance.getString("distance");
                    JSONObject objFinalDistance = new JSONObject(distance);
                    distanceValue = objFinalDistance.getString("value");

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserReturn parserTask = new ParserReturn();
            parserTask.execute(result);

        }
    }

    private void setNode(String a, String b, String distanceValue){
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("previous", 2);
        intent.putExtra("weight", Integer.parseInt(distanceValue));
        intent.putExtra("start", a);
        intent.putExtra("end", b);
        Controller.main(intent);

        Station station = new Station();

        if (a.equals("#PT. Kolamtraco")){
            station.setId(String.valueOf(System.currentTimeMillis()));
            station.setName(a);
            station.setDestination(b);
            station.setDistance(distanceValue);

            tempList.add(station);
        }
    }
    /**
     * A class to parse the Google Places in JSON format
     */

    String distanceValue;

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                JSONArray arrRoute = jObject.getJSONArray("routes");

                //Log.e("arrRoute", arrRoute.toString(1));

                JSONObject objRoute = arrRoute.getJSONObject(0);
                JSONArray arrLog = objRoute.getJSONArray("legs");
                JSONObject objDistance = arrLog.getJSONObject(0);
                String distance = objDistance.getString("distance");
                JSONObject objFinalDistance = new JSONObject(distance);
                distanceValue = objFinalDistance.getString("value");

                routes = parser.parse(jObject);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            if (result!=null && !result.isEmpty()){
                ArrayList points = null;
                PolylineOptions lineOptions = null;
                MarkerOptions markerOptions = new MarkerOptions();

                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList();
                    lineOptions = new PolylineOptions();

                    List<HashMap<String, String>> path = result.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    lineOptions.addAll(points);
                    lineOptions.width(16);
                    lineOptions.color(Color.RED);
                    lineOptions.geodesic(true);
                }
                // Drawing polyline in the Google Map for the i-th route
                mMap.addPolyline(lineOptions);
            }
        }
    }


    private class ParserReturn extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                JSONArray arrRoute = jObject.getJSONArray("routes");

                //Log.e("arrRoute", arrRoute.toString(1));

                JSONObject objRoute = arrRoute.getJSONObject(0);
                JSONArray arrLog = objRoute.getJSONArray("legs");
                JSONObject objDistance = arrLog.getJSONObject(0);
                String distance = objDistance.getString("distance");
                JSONObject objFinalDistance = new JSONObject(distance);
                distanceValue = objFinalDistance.getString("value");

                //Log.e("arrLog", arrLog.toString(1));
                //Log.e("distanceValue", distanceValue);

                //setNode();

                //setNode(distanceValue);

                routes = parser.parse(jObject);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            if (result!=null && !result.isEmpty()){
                ArrayList points = null;
                PolylineOptions lineOptions = null;
                MarkerOptions markerOptions = new MarkerOptions();

                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList();
                    lineOptions = new PolylineOptions();

                    List<HashMap<String, String>> path = result.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    lineOptions.addAll(points);
                    lineOptions.width(6);
                    lineOptions.color(Color.GREEN);
                    lineOptions.geodesic(true);
                }
                // Drawing polyline in the Google Map for the i-th route
                mMap.addPolyline(lineOptions);
            }
        }
    }

    private String getDirectionsUrl(Double latOrigin, Double lngOrigig, Double latDest, Double lngDest) {

        //Log.e("latLngs0", String.valueOf(latLngs.get(0).latitude));
        //Log.e("latLngs1", String.valueOf(latLngs.get(1).latitude));

        String srcAdd = "origin=" + latOrigin+ "," + lngOrigig;
        String desAdd = "destination=" +latDest + "," + lngDest;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = srcAdd + "&" + desAdd + "&" + sensor + "&" + mode + "&";

        // Output format
        String output = "json";

        String key = "&key="+"AIzaSyCCQX25nTed_-OEjUngdmKyO3mGTwFeeKQ";
        //String key = "&key="+"AIzaSyCM7OGrlz85f0g_6dn-PvpxGBhHTuacOGY";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters+key;
        //String url = "https://maps.googleapis.com/maps/api/directions/json?origin=-0.8597643,119.8809325&destination=-0.859829,119.8804728&waypoints=-0.876584,119.8716663|-0.872043,119.8702123&key=AIzaSyCCQX25nTed_-OEjUngdmKyO3mGTwFeeKQ";
        //String url = "https://maps.googleapis.com/maps/api/directions/json?origin=-0.8597643,119.8809325&destination=-0.859829,119.8804728&key=AIzaSyCCQX25nTed_-OEjUngdmKyO3mGTwFeeKQ";

        //Log.e("url", url);

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

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

    private void loadDataAllLocation() {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.information));
        progress.setTitle(getString(R.string.please_wait));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
        String url = BASE_IP+"station/readAllNoPage.php";
        AndroidNetworking.get(url)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("message").equals("success")){
                                String records = response.getString("records");

                                JSONArray dataArr = new JSONArray(records);

                                //Log.e("dataArr", dataArr.toString(1));
                                if (dataArr.length()>0){
                                    for (int i = 0; i < dataArr.length(); i++) {
                                        Station fromJson = gson.fromJson(dataArr.getJSONObject(i).toString(), Station.class);
                                        //stationList.add(fromJson);
                                        latLng = new LatLng(Double.parseDouble(fromJson.getLatitude()), Double.parseDouble(fromJson.getLongitude()));
                                        addMarker(latLng, fromJson.getName(), fromJson.getDistrict());
                                    }
                                }


                            }else{
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "JSONException " + String.valueOf(e), Toast.LENGTH_SHORT).show();

                        }

                        progress.dismiss();

                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        progress.dismiss();
                        Toast.makeText(getApplicationContext(), "ANError " + String.valueOf(error), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void intentToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){
        intentToMain();
    }

    class DistComp implements Comparator<Station>{

        @SuppressLint("NewApi")
        @Override
        public int compare(Station e1, Station e2) {
//            return e1.getDistance().compareTo(e2.getDistance());
            return Integer.compare(Integer.parseInt(e1.getDistance()), Integer.parseInt(e2.getDistance()));
        }
    }
}
