package dev.salgino.gasapp.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dev.salgino.gasapp.R;
import dev.salgino.gasapp.adapter.BannerSliderAdapter;
import dev.salgino.gasapp.adapter.MenuAdapter;
import dev.salgino.gasapp.interfaces.RecyclerviewClick;
import dev.salgino.gasapp.model.Assignment;
import dev.salgino.gasapp.model.MainMenu;
import dev.salgino.gasapp.model.Station;
import dev.salgino.gasapp.model.User;
import ss.com.bannerslider.Slider;
import ss.com.bannerslider.indicators.IndicatorShape;

import static dev.salgino.gasapp.utils.GlobalHelper.getRole;
import static dev.salgino.gasapp.utils.GlobalHelper.getUserId;
import static dev.salgino.gasapp.utils.GlobalVars.BASE_IP;
import static dev.salgino.gasapp.utils.GlobalVars.userHash;

public class MainActivity extends AppCompatActivity {

    private Gson gson;

    List<MainMenu> list =new ArrayList<>();

    private RecyclerView rv;
    private MenuAdapter adapter;
    private LinearLayout parentLayout;

    private MainMenu menu;
    private Intent intent;
    private Slider bannerSlider;

    private boolean doubleBackToExitPressedOnce = false;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        gson = new Gson();

        rv = findViewById(R.id.rv);
        parentLayout = findViewById(R.id.parentLayout);
        bannerSlider = findViewById(R.id.bannerSlider);
        bannerSlider.setAdapter(new BannerSliderAdapter());
        bannerSlider.setIndicatorStyle(IndicatorShape.DASH);

        addMenu();
    }

    private void addMenu(){

        if (getRole().equals("sopir")){
//0
            menu = new MainMenu(getString(R.string.maps), R.drawable.ic_google_maps);
            list.add(menu);
            //1
            menu = new MainMenu(getString(R.string.location), R.drawable.ic_server);
            list.add(menu);

            adapter = new MenuAdapter(this, list, new RecyclerviewClick() {
                @Override
                public void onItemClick(View v, int position) {
                    if (position==0){
                        intentToMaps();
                    }else if (position==1){
                        intentToStationList();
                    }

                }
            });

            loadDataByDriverId();
        }else{
            //0
            menu = new MainMenu(getString(R.string.maps), R.drawable.ic_google_maps);
            list.add(menu);
            //1
            menu = new MainMenu(getString(R.string.location), R.drawable.ic_server);
            list.add(menu);

            //2
            menu = new MainMenu(getString(R.string.employee), R.drawable.ic_employee);
            list.add(menu);
            //3
            menu = new MainMenu(getString(R.string.assignment), R.drawable.ic_list);
            list.add(menu);

            adapter = new MenuAdapter(this, list, new RecyclerviewClick() {
                @Override
                public void onItemClick(View v, int position) {

                    if (position==0){
                        intentToMaps();
                    }else if (position==1){
                        intentToStationList();
                    } else if (position==2){
                        intentToUserList();
                    } else if (position==3){
                        intentToAssignmentList();
                    }

                }
            });
        }

        rv.setHasFixedSize(true);
        rv.setAdapter(adapter);

        rv.setLayoutManager(new GridLayoutManager(this, 2));
        rv.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu); // inflate your menu resource

        MenuItem item = menu.findItem(R.id.menu_more);
//        LayerDrawable icon = (LayerDrawable) item.getIcon();

        MenuItem logout = menu.findItem(R.id.menu_item_logout);
        MenuItem profile = menu.findItem(R.id.menu_item_profile);
        MenuItem help = menu.findItem(R.id.menu_item_help);
        logout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                exitDialog();
                return false;
            }
        });

        profile.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                intentToAccount();
                return false;
            }
        });

        help.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                intentToHelp();
                return false;
            }
        });

        return true;
    }

    private void loadDataByDriverId() {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.information));
        progress.setTitle(getString(R.string.please_wait));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
        String url = BASE_IP+"assignment/readByDriverId.php";
        AndroidNetworking.post(url)
                .addBodyParameter("keyword", getUserId())
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("message").equals("success")){

                                user = new User();

                                user = gson.fromJson(response.toString(), User.class);
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

    private void exitDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.exit_question));
        alertDialogBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                userHash.flush();
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton(getString(R.string.no),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void intentToUserList(){
        Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
        startActivity(intent);
        finish();
    }

    private void intentToAssignmentList(){
        Intent intent = new Intent(getApplicationContext(), AssignmentListActivity.class);
        startActivity(intent);
        finish();
    }

    private void intentToStationList(){
        Intent intent = new Intent(getApplicationContext(), StationListActivity.class);
        startActivity(intent);
        finish();
    }

    private void intentToMaps(){
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("previous",0);
        intent.putExtra("directed",true);
        intent.putExtra("random", false);

        if (getRole().equals("sopir")){
            intent.putExtra("data_assignment", user);
        }

        startActivity(intent);
        finish();
    }

    private void intentToAccount(){
        Intent intent = new Intent(getApplicationContext(), InsertUserActivity.class);
        intent.putExtra("for","edit_account");
        startActivity(intent);
        finish();
    }

    private void intentToHelp(){
        Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        doubleBackToExitPressedOnce = true;
        Toast.makeText(getApplicationContext(), getString(R.string.press_twice), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
