package dev.salgino.gasapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeoutException;

import dev.salgino.gasapp.R;
import dev.salgino.gasapp.adapter.ListItem;
import dev.salgino.gasapp.adapter.MultiSelectAdapter;
import dev.salgino.gasapp.adapter.StationItem;
import dev.salgino.gasapp.interfaces.PaginationAdapterCallback;
import dev.salgino.gasapp.model.Assignment;
import dev.salgino.gasapp.model.Station;
import dev.salgino.gasapp.model.StationHeader;
import dev.salgino.gasapp.model.User;
import dev.salgino.gasapp.utils.GlobalHelper;

import static dev.salgino.gasapp.utils.GlobalHelper.getDate;
import static dev.salgino.gasapp.utils.GlobalHelper.isSelectedStation;
import static dev.salgino.gasapp.utils.GlobalVars.BASE_IP;
import static dev.salgino.gasapp.utils.GlobalVars.selectedStationHash;

public class InsertAssignmentActivity extends AppCompatActivity implements PaginationAdapterCallback {

    private Gson gson;

    private RecyclerView rv;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private Button btnRetry;
    private TextView txtError;
    private LinearLayout parentLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoData;

    private Button btnUpdate;

    private Spinner spin;
    private ProgressBar spin_progress;

    private String driverId;
    private List<Station> selectedStation;
    private JSONObject objSelectedStationObj;

    @NonNull
    private List<ListItem> items = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_assignment);

        gson = new Gson();
        selectedStation = new ArrayList<>();

        rv = findViewById(R.id.rv);
        progressBar = findViewById(R.id.main_progress);
        errorLayout = findViewById(R.id.error_layout);
        btnRetry = findViewById(R.id.error_btn_retry);
        txtError = findViewById(R.id.error_txt_cause);
        parentLayout = findViewById(R.id.parentLayout);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        tvNoData = findViewById(R.id.tvNoData);
        spin = findViewById(R.id.spin);
        spin_progress = findViewById(R.id.spin_progress);
        btnUpdate = findViewById(R.id.btnUpdate);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAssigmentByDriverId(driverId);
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getAssigmentByDriverId(driverId);
                getDriver();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (driverId ==null || driverId.equals("0")){
                    Toast.makeText(getApplicationContext(), getString(R.string.driver_required), Toast.LENGTH_SHORT).show();
                }else{
                    //Toast.makeText(getApplicationContext(), driverId, Toast.LENGTH_SHORT).show();

                    final ProgressDialog progress = new ProgressDialog(InsertAssignmentActivity.this);
                    progress.setMessage(getString(R.string.information));
                    progress.setTitle(getString(R.string.please_wait));
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.show();

                    JSONArray arrSelectedStation = new JSONArray();

                    objSelectedStationObj = null;

                    selectedStation = selectedStationHash.getAllValues();

                    for (Station station:selectedStation){
                        try {
                            objSelectedStationObj = new JSONObject();
                            objSelectedStationObj.put("id", station.getId());
                            objSelectedStationObj.put("driver_id", driverId);
                            objSelectedStationObj.put("station_id", station.getId());
                            objSelectedStationObj.put("created_at", getDate(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
                            objSelectedStationObj.put("status", "aktif");
                            arrSelectedStation.put(objSelectedStationObj);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

//                    try {
//                        Log.e("arrSelectedStation", arrSelectedStation.toString(1));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }

                    AndroidNetworking.post(BASE_IP+"assignment/create.php")
                            .addJSONArrayBody(arrSelectedStation) // posting json
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // do anything with response
                                    try {
                                        Log.e("response", response.toString(1));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    progress.dismiss();
                                    getAssigmentByDriverId(driverId);
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
        });

    }

    private void loadData() {

        if (progressBar!=null && progressBar.getVisibility()==View.GONE)
            progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        hideErrorView();

        String url = BASE_IP+"station/readAllNoPage.php";

        AndroidNetworking.get(url)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideErrorView();
                        List<Station> results = new ArrayList<>();
                        try {
                            if (response.getString("message").equals("success")){
                                String records = response.getString("records");

                                JSONArray dataArr = new JSONArray(records);
                                if (dataArr.length()>0){
                                    for (int i = 0; i < dataArr.length(); i++) {
                                        Station fromJson = gson.fromJson(dataArr.getJSONObject(i).toString(), Station.class);

                                        results.add(fromJson);
                                    }
                                }

                                Map<String, List<Station>> events = toMap(results);

                                SortedSet<String> keys = new TreeSet<>(events.keySet());

                                for (String zone_code : keys) {

                                    StationHeader header = new StationHeader(zone_code);

                                    items.add(header);
                                    for (Station event : events.get(zone_code)) {


                                        if (isSelectedStation(event.getId())){
                                            event.setSelected(true);
                                        }else{
                                            event.setSelected(false);
                                        }


                                        StationItem item = new StationItem(event);

                                        items.add(item);


                                        RecyclerView recyclerView = findViewById(R.id.rv);
                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(InsertAssignmentActivity.this, LinearLayoutManager.VERTICAL, false);
                                        rv.setLayoutManager(linearLayoutManager);
                                        rv.setItemAnimator(new DefaultItemAnimator());

                                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                                                linearLayoutManager.getOrientation());

                                        if (rv.getItemDecorationCount()>0)
                                            rv.removeItemDecorationAt(0);

                                        rv.addItemDecoration(dividerItemDecoration);

                                        MultiSelectAdapter multiSelectAdapter = new MultiSelectAdapter(InsertAssignmentActivity.this, items);

                                        recyclerView.setAdapter(multiSelectAdapter);
                                        multiSelectAdapter.notifyDataSetChanged();
                                    }
                                }

                                if (results.isEmpty()){
                                    tvNoData.setVisibility(View.VISIBLE);
                                    rv.setVisibility(View.GONE);
                                }else{
                                    tvNoData.setVisibility(View.GONE);
                                    rv.setVisibility(View.VISIBLE);
                                }
                            }else{
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        //adapter.addAll(results);

                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        showErrorView(error);
                    }
                });
    }

    private void getDriver() {

        if (spin_progress!=null && spin_progress.getVisibility()==View.GONE)
            spin_progress.setVisibility(View.VISIBLE);

        String url = BASE_IP+"user/readDriver.php";

        AndroidNetworking.get(url)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideErrorView();
                        final List<User> results = new ArrayList<>();
                        try {
                            if (response.getString("message").equals("success")){
                                String records = response.getString("records");

                                JSONArray dataArr = new JSONArray(records);

                                if (!results.isEmpty())
                                    results.clear();

                                final User user = new User();
                                user.setId("0");
                                user.setName(getString(R.string.select_driver));

                                results.add(user);

                                if (dataArr.length()>0){
                                    for (int i = 0; i < dataArr.length(); i++) {
                                        User fromJson = gson.fromJson(dataArr.getJSONObject(i).toString(), User.class);
                                        results.add(fromJson);
                                    }

                                    ArrayAdapter<User> adapter = new ArrayAdapter<User>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, results){
                                        @Override
                                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                            TextView textView = (TextView) super.getDropDownView(position, convertView, parent);

                                            if (position==0){
                                                textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.md_grey_500));
                                            }else{
                                                textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.md_grey_700));
                                            }

                                            return  textView;
                                        }

                                        @Override
                                        public View getView(int position, View convertView, ViewGroup parent) {
                                            TextView textView = (TextView) super.getView(position, convertView, parent);
                                            if (position==0){
                                                textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.md_grey_500));
                                            }else{
                                                textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.md_grey_700));
                                            }
                                            return textView;
                                        }
                                    };

                                    spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            driverId = results.get(position).getId();
                                            if (position>0)
                                                getAssigmentByDriverId(driverId);
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });

                                    spin.setAdapter(adapter);

                                }


                            }else{
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        spin_progress.setVisibility(View.GONE);

                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Toast.makeText(getApplicationContext(), "ANError" + String.valueOf(error), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getAssigmentByDriverId(String driverId) {

        String url = BASE_IP+"assignment/readByDriverId.php";

        AndroidNetworking.post(url)
                .addBodyParameter("keyword", driverId)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideErrorView();
                        try {
                            if (response.getString("message").equals("success")){
                                User fromJson = gson.fromJson(response.toString(), User.class);
                                List<Assignment> assignmentList = new ArrayList<>();

                                if (fromJson.getAssignment().size()>0){
                                    assignmentList = fromJson.getAssignment();

                                    Station station = null;
                                    selectedStationHash.flush();

                                    GlobalHelper.getSpinnerIndex(spin, fromJson.getId());

                                    if (assignmentList!=null && !assignmentList.isEmpty()){

                                        for (Assignment assignment:assignmentList){

                                            station = new Station();
                                            station.setId(assignment.getStation_id());
                                            station.setName(assignment.getStationName());
                                            station.setAddress(assignment.getAddress());
                                            station.setDistrict(assignment.getDistrict());
                                            station.setZone_code(assignment.getZone_code());
                                            station.setLatitude(assignment.getLatitude());
                                            station.setLongitude(assignment.getLongitude());
                                            station.setSelected(true);
                                            selectedStationHash.put(station.getId(), station);
                                        }
                                        items.clear();
                                    }
                                }

                                loadData();

                            }else{
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Toast.makeText(getApplicationContext(), "ANError" + String.valueOf(error), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showErrorView(Throwable throwable) {

        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            txtError.setText(fetchErrorMessage(throwable));
        }
    }

    /**
     * @param throwable to identify the type of error
     * @return appropriate error message
     */
    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = getResources().getString(R.string.error_msg_unknown);

        if (!isNetworkConnected()) {
            errorMsg = getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = getResources().getString(R.string.error_msg_timeout);
        }

        return errorMsg;
    }

    // Helpers -------------------------------------------------------------------------------------

    private void hideErrorView() {
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Remember to add android.permission.ACCESS_NETWORK_STATE permission.
     *
     * @return
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void retryPageLoad() {
        getAssigmentByDriverId(driverId);
    }

    @NonNull
    private Map<String, List<Station>> toMap(@NonNull List<Station> events) {

        Map<String, List<Station>> map = new TreeMap<>();
        for (Station event : events) {
            List<Station> value = map.get(event.getZone_code());
            if (value == null) {
                value = new ArrayList<>();

                map.put(event.getZone_code(), value);
            }
            value.add(event);
        }
        return map;
    }


    private void intentToList(){
        Intent intent = new Intent(this, AssignmentListActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){
        intentToList();
    }
}
