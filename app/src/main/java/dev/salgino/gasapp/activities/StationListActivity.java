package dev.salgino.gasapp.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.util.concurrent.TimeoutException;

import dev.salgino.gasapp.R;
import dev.salgino.gasapp.adapter.StationAdapter;
import dev.salgino.gasapp.interfaces.PaginationAdapterCallback;
import dev.salgino.gasapp.interfaces.PaginationScrollListenerLinear;
import dev.salgino.gasapp.interfaces.RecyclerviewClick;
import dev.salgino.gasapp.model.Station;
import dev.salgino.gasapp.utils.GlobalHelper;

import static dev.salgino.gasapp.MyApp.getContext;
import static dev.salgino.gasapp.utils.GlobalVars.BASE_IP;

public class StationListActivity extends AppCompatActivity implements PaginationAdapterCallback {

    private StationAdapter adapter;

    private Gson gson;

    LinearLayoutManager linearLayoutManager;

    private RecyclerView rv;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private Button btnRetry;
    private TextView txtError;
    private LinearLayout parentLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoData;
    private LinearLayout btnAdd;


    private static final int PAGE_START = 1;

    private boolean isLoading = false;
    private boolean isLastPage = false;

    private int TOTAL_PAGES = 1;
    private int currentPage = PAGE_START;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_list);

        gson = new Gson();

        rv = findViewById(R.id.rv);
        progressBar = findViewById(R.id.main_progress);
        errorLayout = findViewById(R.id.error_layout);
        btnRetry = findViewById(R.id.error_btn_retry);
        txtError = findViewById(R.id.error_txt_cause);
        parentLayout = findViewById(R.id.parentLayout);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        tvNoData = findViewById(R.id.tvNoData);
        btnAdd = findViewById(R.id.btnAdd);

        if (GlobalHelper.getRole().equals("sopir")){
            btnAdd.setVisibility(View.GONE);
        }else{
            btnAdd.setVisibility(View.VISIBLE);
        }

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        adapter = new StationAdapter(this, new RecyclerviewClick() {
            @Override
            public void onItemClick(View v, int position) {

            }
        });

        linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
//                linearLayoutManager.getOrientation());
//        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.rv_divider));
//        rv.addItemDecoration(dividerItemDecoration);

        rv.setLayoutManager(linearLayoutManager);

        rv.setItemAnimator(new DefaultItemAnimator());

        rv.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout
                .OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFirstPage();
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                loadFirstPage();
            }
        });

        rv.addOnScrollListener(new PaginationScrollListenerLinear
                (linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage = currentPage + 1;

                loadNextPage();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFirstPage();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentToRegister();
            }
        });
    }

    private void loadFirstPage() {

        if (progressBar!=null && progressBar.getVisibility()==View.GONE)
            progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        hideErrorView();

        if (adapter!=null && !adapter.isEmpty())
            adapter.clearAll();

        currentPage = 1;
        isLastPage = false;

        String url = BASE_IP+"station/readAll.php?page=";

        AndroidNetworking.get(url+PAGE_START)
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
                                String paging = response.getString("paging");
                                JSONObject pageObj = new JSONObject(paging);

                                TOTAL_PAGES = pageObj.getInt("total_pages");

                                JSONArray dataArr = new JSONArray(records);

                                //Log.e("dataArr", dataArr.toString(1));

                                if (dataArr.length()>0){
                                    for (int i = 0; i < dataArr.length(); i++) {
                                        Station fromJson = gson.fromJson(dataArr.getJSONObject(i).toString(), Station.class);
                                        results.add(fromJson);
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

                        //List<Po> results = fetchResults(response);
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        adapter.addAll(results);


                        if (currentPage <= TOTAL_PAGES) {
                            adapter.addLoadingFooter();
                        } else {
                            isLastPage = true;
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        showErrorView(error);
                    }
                });
    }

    private void loadNextPage() {
        hideErrorView();

        String url = BASE_IP+"station/readAll.php?page=";

        AndroidNetworking.get(url+currentPage)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        // do anything with response
                        hideErrorView();

                        List<Station> results = new ArrayList<>();
                        try {
                            String message = response.getString("message");
                            String records = response.getString("records");
                            String paging = response.getString("paging");
                            JSONObject pageObj = new JSONObject(paging);

                            TOTAL_PAGES = pageObj.getInt("total_pages");
                            JSONArray dataArr = new JSONArray(records);

                            if (dataArr.length()>0){
                                for (int i = 0; i < dataArr.length(); i++) {
                                    Station fromJson = gson.fromJson(dataArr
                                            .getJSONObject(i).toString(), Station.class);
                                    results.add(fromJson);
                                }
                            }

                            if (results.isEmpty()){
                                tvNoData.setVisibility(View.VISIBLE);
                                rv.setVisibility(View.GONE);
                            }else{
                                tvNoData.setVisibility(View.GONE);
                                rv.setVisibility(View.VISIBLE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        adapter.removeLoadingFooter();
                        isLoading = false;

                        adapter.addAll(results);

                        if (currentPage < TOTAL_PAGES) {
                            adapter.addLoadingFooter();
                        }
                        else {
                            isLastPage = true;
                        }


                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        showErrorView(error);
                    }
                });
    }

    public void intentToStationData(Station station){
        Intent intent = new Intent(getApplicationContext(), InsertStationActivity.class);
        intent.putExtra("data_station",station);
        startActivity(intent);
        finish();
    }

    private void intentToRegister(){
        Intent intent = new Intent(getApplicationContext(), InsertStationActivity.class);
        startActivity(intent);
        finish();
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
        loadNextPage();
    }

    private void intentToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){
        //super.onBackPressed();
        intentToMain();

    }
}
