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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import dev.salgino.gasapp.adapter.AssignmentAdapter;
import dev.salgino.gasapp.graphing.Controller;
import dev.salgino.gasapp.interfaces.PaginationAdapterCallback;
import dev.salgino.gasapp.interfaces.PaginationScrollListenerLinear;
import dev.salgino.gasapp.interfaces.RecyclerviewClick;
import dev.salgino.gasapp.model.User;

import static dev.salgino.gasapp.utils.GlobalVars.BASE_IP;
import static dev.salgino.gasapp.utils.GlobalVars.selectedStationHash;

public class AssignmentListActivity extends AppCompatActivity implements PaginationAdapterCallback {

    //adapter berfungsi untuk menghubungkan semua data yang ditampilan dalam bentuk list
    //misal recyclerview atau listview
    private AssignmentAdapter adapter;

    //gson berfungsi untuk mengubah object menjadi model
    private Gson gson;

    //inisialisi komponenen yang digunakan
    private RecyclerView rv;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private Button btnRetry;
    private TextView txtError;
    private LinearLayout parentLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoData;
    private LinearLayout btnAdd;

    //inisialisasi halaman pertama sebagai parameter awal untuk digunakan pada pagination
    private static final int PAGE_START = 1;

    //parameter yang digunakan untuk mengetahui apakah masih proses loading
    private boolean isLoading = false;

    //parameter yang digunakan untuk mengetahui apakah sudah halaman terakhir atau belum
    private boolean isLastPage = false;

    //parameter yang digunakan untuk menentukan berapa banyak halaman yang akan di-load
    private int TOTAL_PAGES = 1;

    //parameter yang digunakan untuk menentukan halaman saat ini
    private int currentPage = PAGE_START;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_list);

        //memanggil object gson
        gson = new Gson();

        //reset data pangkalan yang sudah dipilih
        selectedStationHash.flush();

        //memanggil object dari komponen-komponen yang digunakan
        //kiri adalah variabel di java, kanan adalah variabel di xml
        rv = findViewById(R.id.rv);
        progressBar = findViewById(R.id.main_progress);
        errorLayout = findViewById(R.id.error_layout);
        btnRetry = findViewById(R.id.error_btn_retry);
        txtError = findViewById(R.id.error_txt_cause);
        parentLayout = findViewById(R.id.parentLayout);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        tvNoData = findViewById(R.id.tvNoData);
        btnAdd = findViewById(R.id.btnAdd);

        //fungsi warna pada swipe refresh
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        //memanggil object adapter
        adapter = new AssignmentAdapter(this, new RecyclerviewClick() {
            @Override
            public void onItemClick(View v, int position) {

            }
        });

        //membuat layout recyclerview menjadi vertikal
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);

        //membuat animasi pada recyclerview
        rv.setItemAnimator(new DefaultItemAnimator());

        //set adapter ke recyclerview
        rv.setAdapter(adapter);

        //load data saat swipe refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFirstPage();
            }
        });

        //load data halaman tampil
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                loadFirstPage();
            }
        });

        //load data halaman selanjutnya saat scroll
        rv.addOnScrollListener(new PaginationScrollListenerLinear(linearLayoutManager) {
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
                intentToAssignment();
            }
        });
    }

    //memanggil data halaman pertama
    private void loadFirstPage() {

        //rule untuk menampilkan progress bar
        if (progressBar!=null && progressBar.getVisibility()==View.GONE)
            progressBar.setVisibility(View.VISIBLE);

        //fungsi untuk menyembunyikan swipe refresh
        swipeRefreshLayout.setRefreshing(false);

        //fungsi untuk menyembunyikan tampilan error
        hideErrorView();

        //fungsi untuk mereset adapter
        if (adapter!=null && !adapter.isEmpty())
            adapter.clearAll();

        //halaman saat ini
        currentPage = 1;

        //bukan halaman terakhir
        isLastPage = false;

        //url untuk memanggil data penugasan halaman 1
        String url = BASE_IP+"assignment/readAll.php?page=";

        //memanggil data penugasan halaman 1 menggunakan AndroidNetworking
        AndroidNetworking.get(url+PAGE_START)
                .setPriority(Priority.LOW)
                .build()

                //nilai kembalian dari API adalah json object
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        //fungsi untuk menyembunyikan tampilan error
                        hideErrorView();

                        //variabel untuk menampung data dari API
                        List<User> results = new ArrayList<>();
                        try {

                            //rule jika respon dari API sukses
                            if (response.getString("message").equals("success")){

                                //mengubah json object dengan key records menjadi string
                                String records = response.getString("records");

                                //mengubah json object dengan key paging menjadi string
                                String paging = response.getString("paging");

                                //mengubah string menjadi json object
                                JSONObject pageObj = new JSONObject(paging);

                                //mengubah json object pageObj, mengambil value dari key total_pages menjadi int
                                //untuk mengetahui total halaman
                                TOTAL_PAGES = pageObj.getInt("total_pages");

                                //mengubah string records menjadi json array
                                JSONArray dataArr = new JSONArray(records);

                                //rule jika penjang json array lebih dari nol maka dilakukan parsing menjadi model
                                if (dataArr.length()>0){

                                    //membuat perulangan dari json array untuk mendapatkan json object
                                    for (int i = 0; i < dataArr.length(); i++) {
                                        //mengubah json object menjadi string dan diparsing menjadi model
                                        User user = gson.fromJson(dataArr.getJSONObject(i).toString(), User.class);

                                        //menampung data hasil parsing ke dalam list
                                        results.add(user);
                                    }

                                }

                                //rule jika list kosong atau ada isinya
                                if (results.isEmpty()){
                                    tvNoData.setVisibility(View.VISIBLE);
                                    rv.setVisibility(View.GONE);
                                }else{
                                    tvNoData.setVisibility(View.GONE);
                                    rv.setVisibility(View.VISIBLE);
                                }
                            }else{
                                tvNoData.setVisibility(View.GONE);
                                rv.setVisibility(View.VISIBLE);
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

    //memanggil data halaman selanjutnya
    private void loadNextPage() {
        hideErrorView();

        String url = BASE_IP+"assignment/readAll.php?page=";

        AndroidNetworking.get(url+currentPage)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        // do anything with response
                        hideErrorView();

                        List<User> results = new ArrayList<>();
                        try {

                            if (response.getString("message").equals("success")){
                                String message = response.getString("message");
                                String records = response.getString("records");
                                String paging = response.getString("paging");
                                JSONObject pageObj = new JSONObject(paging);

                                TOTAL_PAGES = pageObj.getInt("total_pages");
                                JSONArray dataArr = new JSONArray(records);

                                if (dataArr.length()>0){
                                    for (int i = 0; i < dataArr.length(); i++) {
                                        User notification = gson.fromJson(dataArr.getJSONObject(i).toString(), User.class);
                                        results.add(notification);
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

    public void intentToMaps(User user){
        Controller.destroy();
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("data_assignment", user);
        intent.putExtra("previous",0);
        intent.putExtra("directed",true);
        intent.putExtra("random", false);
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

    public void CustomDialog(User notification){
        LayoutInflater li = LayoutInflater.from(AssignmentListActivity.this);
        View promptsView = li.inflate(R.layout.view_detail_user, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AssignmentListActivity.this);

        alertDialogBuilder
                .setTitle(getString(R.string.employee_detail))
                .setIcon(ContextCompat.getDrawable(AssignmentListActivity.this, R.drawable.ic_employee));

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        TextView tvTitle =promptsView.findViewById(R.id.tvTitle);
        TextView tvSender =promptsView.findViewById(R.id.tvSender);
        TextView tvMessage =promptsView.findViewById(R.id.tvSubtitle);
        TextView tvTransaction =promptsView.findViewById(R.id.tvTransaction);
        TextView tvCancel =promptsView.findViewById(R.id.tvCancel);
        tvMessage.setPaintFlags(0);

        if (notification!=null){
            tvTitle.setText(notification.getName());
            tvSender.setText(notification.getBirthday());
            tvMessage.setText(notification.getRole());
        }

        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();

        tvTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
    }

    private void intentToAssignment(){
        Intent intent = new Intent(this, InsertAssignmentActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){
        //super.onBackPressed();
        intentToMain();

    }
}
