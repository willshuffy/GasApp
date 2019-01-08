package dev.salgino.gasapp.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dev.salgino.gasapp.R;
import dev.salgino.gasapp.model.User;
import dev.salgino.gasapp.utils.AppPermissions;

import static dev.salgino.gasapp.utils.GlobalHelper.createFolder;
import static dev.salgino.gasapp.utils.GlobalHelper.isLoggedIn;
import static dev.salgino.gasapp.utils.GlobalVars.BASE_IP;
import static dev.salgino.gasapp.utils.GlobalVars.KEY_EMAIL;
import static dev.salgino.gasapp.utils.GlobalVars.KEY_PASSWORD;
import static dev.salgino.gasapp.utils.GlobalVars.userHash;

public class LoginActivity extends AppCompatActivity {

    private static final String[] ALL_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final int WRITE_EXTERNAL_STORAGE_CODE = 901;
    private static final int READ_EXTERNAL_STORAGE_CODE = 902;
    private static final int ACCESS_FINE_LOCATION_CODE = 905;
    private static final int ALL_REQUEST_CODE = 999;
    private AppPermissions mRuntimePermission;

    private EditText txtEmail;
    private EditText txtPassword;
    private Button btnLogin;
    private TextView tvForgotPassord;
    private TextView tvHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mRuntimePermission = new AppPermissions(this);

        if (mRuntimePermission.hasPermission(ALL_PERMISSIONS)) {
            createFolder();
            if (isLoggedIn() == true){
                intentToMain();
            }

        }else{
            mRuntimePermission.requestPermission(this, ALL_PERMISSIONS, ALL_REQUEST_CODE);
        }

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassord = findViewById(R.id.tvForgotPassord);
        tvHelp = findViewById(R.id.tvHelp);

        tvForgotPassord.setVisibility(View.GONE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtEmail.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), getString(R.string.email_required), Toast.LENGTH_SHORT).show();
                }else if (txtPassword.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), getString(R.string.password_required), Toast.LENGTH_SHORT).show();
                }else{
                    login();
                }
            }
        });

        tvHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentToHelp();
            }
        });
    }

    private void login(){
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.fetch_data_from_server));
        progress.setTitle(getString(R.string.please_wait));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();

        userHash.flush();

        AndroidNetworking.post(BASE_IP + "user/login.php")
                .addBodyParameter(KEY_EMAIL, txtEmail.getText().toString())
                .addBodyParameter(KEY_PASSWORD, txtPassword.getText().toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //do anything with response

                        Gson gson = new Gson();
                        User user = gson.fromJson(String.valueOf(response), User.class);
                        user.setPassword(txtPassword.getText().toString());

                        progress.dismiss();

                        if (user.getId() != null && user.getEmail() != null && user.getEmail() != null){
                            //Toast.makeText(getApplicationContext(),String.valueOf(user.getMessage()), Toast.LENGTH_SHORT).show();
                            userHash.put(user.getId(), user);
                            intentToMain();

                        }else{
                            Toast.makeText(getApplicationContext(), getString(R.string.wrong_username_password),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        progress.dismiss();
                        Toast.makeText(getApplicationContext(), String.valueOf(error), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ALL_REQUEST_CODE:
                List<Integer> permissionResults = new ArrayList<>();
                for (int grantResult : grantResults) {
                    permissionResults.add(grantResult);
                }
                if (permissionResults.contains(PackageManager.PERMISSION_DENIED)) {
                    Toast.makeText(this, getString(R.string.all_permission_not_granted), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, getString(R.string.all_permission_granted), Toast.LENGTH_SHORT).show();
                }
                break;
            case WRITE_EXTERNAL_STORAGE_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, getString(R.string.write_external_permission_not_granted), Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(this, "Write External Storage Permissions granted", Toast.LENGTH_SHORT).show();
                }
                finish();
                break;
            case READ_EXTERNAL_STORAGE_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, getString(R.string.read_external_permission_not_granted), Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(this, "Read External Storage Permissions granted", Toast.LENGTH_SHORT).show();
                }
                finish();
                break;
            case ACCESS_FINE_LOCATION_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, getString(R.string.location_permission_not_granted), Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(this, "Access Location Permissions granted", Toast.LENGTH_SHORT).show();
                }
                finish();
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.help_menu, menu); // inflate your menu resource

        MenuItem help = menu.findItem(R.id.menu_item_help);

        help.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                intentToHelp();
                return false;
            }
        });

        return true;
    }

    private void intentToHelp(){
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra("from", "login");
        startActivity(intent);
        finish();
    }

    private void intentToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
