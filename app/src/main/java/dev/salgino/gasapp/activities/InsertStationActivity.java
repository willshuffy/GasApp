package dev.salgino.gasapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import dev.salgino.gasapp.R;
import dev.salgino.gasapp.model.Station;
import dev.salgino.gasapp.model.User;

import static dev.salgino.gasapp.utils.GlobalHelper.convertDate;
import static dev.salgino.gasapp.utils.GlobalHelper.getRole;
import static dev.salgino.gasapp.utils.GlobalVars.BASE_IP;

public class InsertStationActivity extends AppCompatActivity  {

    private int PLACE_PICKER_REQUEST = 1;

    private EditText txtAddress;
    private EditText txtDistrict;
    private EditText txtZoneCode;
    private EditText txtName;
    private EditText txtLat;
    private EditText txtLng;
    private Button btnSave;
    private ImageView ivMaps;

    //private SupportPlaceAutocompleteFragment autocompleteFragment;
    private PlaceAutocompleteFragment autocompleteFragment;

    private Gson gson;
    private Station station;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_station);

        gson = new Gson();

        txtAddress = findViewById(R.id.txtAddress);
        txtDistrict = findViewById(R.id.txtDistrict);
        txtZoneCode = findViewById(R.id.txtZoneCode);
        txtName = findViewById(R.id.txtName);
        txtLat = findViewById(R.id.txtLat);
        txtLng = findViewById(R.id.txtLng);
        btnSave = findViewById(R.id.btnSave);
        ivMaps = findViewById(R.id.ivMaps);

        if (getRole().equals("sopir")){
            btnSave.setVisibility(View.GONE);
        }else{
            btnSave.setVisibility(View.VISIBLE);
        }

        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().
                findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                txtAddress.setText("");
                txtDistrict.setText("");
                txtLat.setText("");
                txtLng.setText("");
                autocompleteFragment.setText("");

                try {
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    List<Address> addresses = geocoder.getFromLocation(place.getLatLng().latitude,place.getLatLng().longitude, 1);
                    autocompleteFragment.setText(place.getName());
                    txtAddress.setText(String.valueOf(addresses.get(0).getAddressLine(0)));
                    txtDistrict.setText(addresses.get(0).getSubAdminArea());
                    txtLat.setText(String.valueOf(place.getLatLng().latitude));
                    txtLng.setText(String.valueOf(place.getLatLng().longitude));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Status status) {
                Log.e("error", "An error occurred: " + status);
            }
        });


        ivMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(InsertStationActivity.this), PLACE_PICKER_REQUEST);

                    // check apabila <a title="Solusi Tidak Bisa Download Google Play Services di Android" href="http://www.twoh.co/2014/11/solusi-tidak-bisa-download-google-play-services-di-android/" target="_blank">Google Play Services tidak terinstall</a> di HP
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        if (getIntent().hasExtra("data_station")) {

            btnSave.setText(getString(R.string.update));

            station = getIntent().getParcelableExtra("data_station");
            id = station.getId();
            txtAddress.setText(station.getAddress());
            txtDistrict.setText(station.getDistrict());
            txtLat.setText(station.getLatitude());
            txtLng.setText(station.getLongitude());
            txtName.setText(station.getName());
            txtZoneCode.setText(station.getZone_code());
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().hasExtra("data_station")) {
                    update();
                }else{
                    insert();
                }
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // menangkap hasil balikan dari Place Picker, dan menampilkannya pada TextView
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, getApplicationContext());
                try {

                    txtAddress.setText("");
                    txtDistrict.setText("");
                    txtLat.setText("");
                    txtLng.setText("");
                    autocompleteFragment.setText("");

                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    List<Address> addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                    //Log.i("Place: ", String.valueOf(place.getName()));
                    autocompleteFragment.setText(place.getName());
                    txtAddress.setText(String.valueOf(addresses.get(0).getAddressLine(0)));
                    txtDistrict.setText(addresses.get(0).getLocality());
                    txtLat.setText(String.valueOf(place.getLatLng().latitude));
                    txtLng.setText(String.valueOf(place.getLatLng().longitude));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void insert(){
        if (txtAddress.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.address_required), Toast.LENGTH_SHORT).show();
        }else if (txtDistrict.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.district_required), Toast.LENGTH_SHORT).show();
        }else if (txtLat.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.latitude_required), Toast.LENGTH_SHORT).show();
        }else if (txtLng.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.longitude_required), Toast.LENGTH_SHORT).show();
        }else if (txtName.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.station_name_required), Toast.LENGTH_SHORT).show();
        }else if (txtZoneCode.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.zone_required), Toast.LENGTH_SHORT).show();
        }else{
            final ProgressDialog progress = new ProgressDialog(this);
            progress.setMessage(getString(R.string.information));
            progress.setTitle(getString(R.string.please_wait));
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.show();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", txtName.getText().toString());
                jsonObject.put("address", txtAddress.getText().toString());
                jsonObject.put("district", txtDistrict.getText().toString());
                jsonObject.put("zone_code", txtZoneCode.getText().toString());
                jsonObject.put("latitude", txtLat.getText().toString());
                jsonObject.put("longitude", txtLng.getText().toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            AndroidNetworking.post(BASE_IP+"station/create.php")
                    .addJSONObjectBody(jsonObject) // posting json
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // do anything with response
                            User resp = gson.fromJson(response.toString(), User.class);
                            if (resp.getMessage().equals("success")){
                                progress.dismiss();
                                Toast.makeText(getApplicationContext(),resp.getMessage(), Toast.LENGTH_SHORT).show();
                                intentToList();
                            }else{
                                progress.dismiss();
                                Toast.makeText(getApplicationContext(),resp.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onError(ANError error) {
                            // handle error
                            progress.dismiss();
                            Toast.makeText(getApplicationContext(),"error " + String.valueOf(error), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void update(){
        if (id==null && id.equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.id_required), Toast.LENGTH_SHORT).show();
        }else if (txtAddress.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.address_required), Toast.LENGTH_SHORT).show();
        }else if (txtDistrict.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.district_required), Toast.LENGTH_SHORT).show();
        }else if (txtLat.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.latitude_required), Toast.LENGTH_SHORT).show();
        }else if (txtLng.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.longitude_required), Toast.LENGTH_SHORT).show();
        }else if (txtName.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.station_name_required), Toast.LENGTH_SHORT).show();
        }else if (txtZoneCode.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.zone_required), Toast.LENGTH_SHORT).show();
        }else{
            final ProgressDialog progress = new ProgressDialog(this);
            progress.setMessage(getString(R.string.information));
            progress.setTitle(getString(R.string.please_wait));
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.show();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", id);
                jsonObject.put("name", txtName.getText().toString());
                jsonObject.put("address", txtAddress.getText().toString());
                jsonObject.put("district", txtDistrict.getText().toString());
                jsonObject.put("zone_code", txtZoneCode.getText().toString());
                jsonObject.put("latitude", txtLat.getText().toString());
                jsonObject.put("longitude", txtLng.getText().toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            AndroidNetworking.post(BASE_IP+"station/update.php")
                    .addJSONObjectBody(jsonObject) // posting json
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // do anything with response
                            User resp = gson.fromJson(response.toString(), User.class);
                            if (resp.getMessage().equals("success")){
                                progress.dismiss();
                                Toast.makeText(getApplicationContext(),resp.getMessage(), Toast.LENGTH_SHORT).show();
                                intentToList();
                            }else{
                                progress.dismiss();
                                Toast.makeText(getApplicationContext(),resp.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onError(ANError error) {
                            // handle error
                            progress.dismiss();
                            Toast.makeText(getApplicationContext(),"error " + String.valueOf(error), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void intentToList(){
        Intent intent = new Intent(this, StationListActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){
        //super.onBackPressed();
        intentToList();

    }
}
