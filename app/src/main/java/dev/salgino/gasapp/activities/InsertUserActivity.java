package dev.salgino.gasapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import dev.salgino.gasapp.R;
import dev.salgino.gasapp.model.User;
import dev.salgino.gasapp.slidedatetimepicker.SlideDateTimeListener;
import dev.salgino.gasapp.slidedatetimepicker.SlideDateTimePicker;

import static dev.salgino.gasapp.utils.GlobalHelper.convertDate;
import static dev.salgino.gasapp.utils.GlobalHelper.convertStringToDateFormat;
import static dev.salgino.gasapp.utils.GlobalHelper.getPassword;
import static dev.salgino.gasapp.utils.GlobalHelper.getSpinnerIndex;
import static dev.salgino.gasapp.utils.GlobalVars.BASE_IP;
import static dev.salgino.gasapp.utils.GlobalVars.userHash;

public class InsertUserActivity extends AppCompatActivity {

    private LinearLayout layoutContent;
    private LinearLayout layoutEmail;
    private RelativeLayout layoutSpin;
    private EditText txtBirthday;
    private EditText txtName;
    private EditText txtEmail;
    private EditText txtMobile;
    private EditText txtAddress;
    private EditText txtPassword;
    private RadioGroup rgGender;
    private Button btnRegister;
    private TextView tvLogin;

    private Spinner spin;

    private SimpleDateFormat mFormatter = new SimpleDateFormat("dd MMM yyyy");

    private Gson gson;
    User user;

    List<User> userList = new ArrayList<>();
    //private boolean isDriver = false;
    private int spinPos = 0;
    private String role = "";

    private int rgPos = -1;
    private String gender = "";
    private String isActive = "FALSE";

    private String userId;
    private String email;

    List<String> stringlist;
    ArrayAdapter<String> arrayadapter;
    String[] spinnerItems = new String[]{
            "Posisi karyawan",
            "manager",
            "sekretaris",
            "pengawasan",
            "sopir"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_user);

        gson = new Gson();

        layoutContent = findViewById(R.id.layoutContent);
        layoutEmail = findViewById(R.id.layoutEmail);
        layoutSpin = findViewById(R.id.layoutSpin);
        txtBirthday = findViewById(R.id.txtBirthday);
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtMobile = findViewById(R.id.txtMobile);
        txtAddress = findViewById(R.id.txtAddress);
        txtPassword = findViewById(R.id.txtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        spin = findViewById(R.id.spin);
        rgGender = findViewById(R.id.rgGender);

        userList = userHash.getAllValues();

        if (getIntent().hasExtra("data_user")) {
            btnRegister.setText(getString(R.string.update));
            user = getIntent().getParcelableExtra("data_user");
            userId = user.getId();
            email = user.getEmail();
            role = user.getRole();
            txtPassword.setVisibility(View.GONE);
            txtName.setText(user.getName());

            if (user.getBirthday()!=null) {
                String newDate = convertDate(user.getBirthday(), "yyyy-MM-dd", "dd MMM yyyy");
                txtBirthday.setText(newDate);
            }


            txtMobile.setText(user.getMobile());

            if (user.getGender().toLowerCase().equals("male")){
                rgGender.check(R.id.radioMale);
                gender = "male";
                rgPos = 0;
            }else{
                rgGender.check(R.id.radioFemale);
                gender = "female";
                rgPos = 1;
            }

            txtAddress.setText(user.getAddress());
            txtEmail.setText(user.getEmail());

        }else if (userList!=null && !userList.isEmpty() && getIntent().hasExtra("for")){
            btnRegister.setText(getString(R.string.update));
            final String password = userList.get(0).getPassword();
            userId = userList.get(0).getId();
            role = userList.get(0).getRole();

            txtPassword.setText(password);
            txtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_right_black_24dp, 0);
            txtPassword.setFocusable(false);
            txtPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (password != null && !password.equals(""))
                        CustomDialog();
                    else
                        Toast.makeText(getApplicationContext(), getString(R.string.only_be_changed_by_the_owner), Toast.LENGTH_SHORT).show();
                }
            });

            txtName.setText(userList.get(0).getName());

            if (userList.get(0).getBirthday()!=null) {
                String newDate = convertDate(userList.get(0).getBirthday(), "yyyy-MM-dd", "dd MMM yyyy");
                txtBirthday.setText(newDate);
            }

            txtMobile.setText(userList.get(0).getMobile());

            if (userList.get(0).getGender().toLowerCase().equals("male")){
                rgGender.check(R.id.radioMale);
                gender = "male";
                rgPos = 0;
            }else{
                rgGender.check(R.id.radioFemale);
                gender = "female";
                rgPos = 1;
            }
            txtAddress.setText(userList.get(0).getAddress());
            txtEmail.setText(userList.get(0).getEmail());

        }

        stringlist = new ArrayList<>(Arrays.asList(spinnerItems));
        arrayadapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, stringlist){
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

        spin.setAdapter(arrayadapter);

        if (role!=null && role.equals("superadmin")){
            layoutSpin.setVisibility(View.GONE);
            spin.setSelection(spinPos);
        }else{
            spinPos = getSpinnerIndex(spin, role);
            spin.setSelection(spinPos);
        }

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinPos = position;

                if (role.equals("superadmin")){
                    isActive = "TRUE";
                    role = "superadmin";
                }else{
                    if (position>0)
                        role = stringlist.get(position).toLowerCase().toString();
                    else
                        role = "";

                    if (role.equals("sopir")){
                        isActive = "TRUE";
                    }else{
                        isActive = "FALSE";
                    }
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        txtBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newDate = "";
                if (txtBirthday.getText().toString().equals("")){
                    newDate = convertDate(mFormatter.format(new Date()), "dd MMM yyyy", "EEE MMM HH:mm:ss z yyyy");
                }else{
                    newDate = convertDate(txtBirthday.getText().toString(), "dd MMM yyyy", "EEE MMM HH:mm:ss z yyyy");
                }

                Date date = convertStringToDateFormat(newDate, "EEE MMM HH:mm:ss z yyyy");

                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(date)
                        //.setMinDate(minDate)
                        // .setMaxDate(maxDate)
                        .setIs24HourTime(true)
                        //.setTheme(SlideDateTimePicker.HOLO_DARK)
                        // .setIndicatorColor(Color.parseColor("#990000"))
                        .build()
                        .show();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ((userList!=null && !userList.isEmpty() && getIntent().hasExtra("for") || (user!=null && getIntent().hasExtra("data_user"))))
                    update();
                else
                    insert();
            }
        });

        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rgPos = checkedId;
                if (checkedId==0)
                    gender = "male";
                else if (checkedId==1)
                    gender = "female";
            }
        });
    }

    private SlideDateTimeListener listener = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {
            txtBirthday.setText(mFormatter.format(date));
        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel() {
        }
    };


    private void resetData(){
        txtEmail.setText("");
        txtPassword.setText("");
    }

    private void insert(){
        if (spinPos==0){
            Toast.makeText(getApplicationContext(), getString(R.string.position_required), Toast.LENGTH_SHORT).show();
        }  else if (txtName.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.name_required), Toast.LENGTH_SHORT).show();
        }else if (txtBirthday.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.birthday_required), Toast.LENGTH_SHORT).show();
        }else if (rgPos==-1){
            Toast.makeText(getApplicationContext(), getString(R.string.gender_required), Toast.LENGTH_SHORT).show();
        }else if (txtAddress.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.address_required), Toast.LENGTH_SHORT).show();
        }else if (txtMobile.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.mobile_required), Toast.LENGTH_SHORT).show();
        }else if (txtEmail.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.email_required), Toast.LENGTH_SHORT).show();
        }else if (txtPassword.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.password_required), Toast.LENGTH_SHORT).show();
        }else{
            final ProgressDialog progress = new ProgressDialog(this);
            progress.setMessage(getString(R.string.information));
            progress.setTitle(getString(R.string.please_wait));
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.show();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("role", role);
                jsonObject.put("name", txtName.getText().toString());
                jsonObject.put("birthday", convertDate(txtBirthday.getText().toString(), "dd MMM yyyy", "yyyy-MM-dd HH:mm:ss"));
                jsonObject.put("gender", gender);
                jsonObject.put("address", txtAddress.getText().toString());
                jsonObject.put("mobile", txtMobile.getText().toString());
                jsonObject.put("email", txtEmail.getText().toString());
                jsonObject.put("password", txtPassword.getText().toString());
                jsonObject.put("isActive", isActive);

                //Log.e("jsonObject", jsonObject.toString(1));

                //write(getApplicationContext(), "aaaa.txt",jsonObject.toString(1));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            AndroidNetworking.post(BASE_IP+"user/create.php")
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
        if (role!=null && !role.equals("superadmin") && spinPos==0){
            Toast.makeText(getApplicationContext(), getString(R.string.position_required), Toast.LENGTH_SHORT).show();
        }  else if (txtName.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.name_required), Toast.LENGTH_SHORT).show();
        }else if (txtBirthday.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.birthday_required), Toast.LENGTH_SHORT).show();
        }else if (rgPos==-1){
            Toast.makeText(getApplicationContext(), getString(R.string.gender_required), Toast.LENGTH_SHORT).show();
        }else if (txtAddress.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.address_required), Toast.LENGTH_SHORT).show();
        }else if (txtMobile.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.mobile_required), Toast.LENGTH_SHORT).show();
        }else if (txtEmail.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.email_required), Toast.LENGTH_SHORT).show();
        }else{
            final ProgressDialog progress = new ProgressDialog(this);
            progress.setMessage(getString(R.string.information));
            progress.setTitle(getString(R.string.please_wait));
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.show();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", userId);
                jsonObject.put("role", role);
                jsonObject.put("name", txtName.getText().toString());
                jsonObject.put("birthday", convertDate(txtBirthday.getText().toString(), "dd MMM yyyy", "yyyy-MM-dd HH:mm:ss"));
                jsonObject.put("gender", gender);
                jsonObject.put("address", txtAddress.getText().toString());
                jsonObject.put("mobile", txtMobile.getText().toString());
                jsonObject.put("email", txtEmail.getText().toString());
                jsonObject.put("isActive", isActive);

                //Log.e("jsonObject", jsonObject.toString(1));

                //write(getApplicationContext(), "aaaa.txt",jsonObject.toString(1));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            AndroidNetworking.post(BASE_IP+"user/update.php")
                    .addJSONObjectBody(jsonObject) // posting json
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // do anything with response
                            User resp = gson.fromJson(response.toString(), User.class);
                            if (resp.getMessage().equals("success")){
                                if (getIntent().hasExtra("for")){

                                    resp.setPassword(userList.get(0).getPassword());
                                    userHash.put(resp.getId(), resp);

                                    progress.dismiss();
                                    Toast.makeText(getApplicationContext(),resp.getMessage(), Toast.LENGTH_SHORT).show();
                                }else{
                                    intentToList();
                                    progress.dismiss();
                                    Toast.makeText(getApplicationContext(),resp.getMessage(), Toast.LENGTH_SHORT).show();
                                }


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

    private void CustomDialog(){
        LayoutInflater li = LayoutInflater.from(InsertUserActivity.this);
        View promptsView = li.inflate(R.layout.view_change_password, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InsertUserActivity.this);

        alertDialogBuilder
                .setTitle(getString(R.string.change_password))
                .setIcon(ContextCompat.getDrawable(InsertUserActivity.this, R.drawable.ic_error_outline_black_24dp));

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText txtPassword =promptsView.findViewById(R.id.txtPassword);
        final EditText txtNewPassword =promptsView.findViewById(R.id.txtNewPassword);
        final EditText txtNewPasswordAgain =promptsView.findViewById(R.id.txtNewPasswordAgain);
        Button btnUpdate =  promptsView.findViewById(R.id.btnUpdate);
        TextView tvCancel =  promptsView.findViewById(R.id.tvCancel);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!txtPassword.getText().toString().equals(getPassword())){
                    Toast.makeText(getApplicationContext(), getString(R.string.wrong_password), Toast.LENGTH_SHORT).show();
                } else if (txtPassword.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), getString(R.string.password_required), Toast.LENGTH_SHORT).show();
                }else if (txtNewPassword.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), getString(R.string.new_password_required), Toast.LENGTH_SHORT).show();
                }else if (txtNewPasswordAgain.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), getString(R.string.new_password_again_required), Toast.LENGTH_SHORT).show();
                }else if (txtPassword.getText().toString().equals(txtNewPassword.getText().toString())){
                    Toast.makeText(getApplicationContext(), getString(R.string.new_password_is_same), Toast.LENGTH_SHORT).show();
                }else if (!txtNewPassword.getText().toString().equals(txtNewPasswordAgain.getText().toString())){
                    Toast.makeText(getApplicationContext(), getString(R.string.new_password_did_not_match), Toast.LENGTH_SHORT).show();
                }else{
                    alertDialog.cancel();
                    updatePassword(txtNewPasswordAgain.getText().toString());
                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
    }

    private void updatePassword(final String password){
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.information));
        progress.setTitle(getString(R.string.please_wait));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", userId);
            jsonObject.put("password", password);

            //write(getApplicationContext(), "aaaa.txt",jsonObject.toString(1));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(BASE_IP+"user/updatePassword.php")
                .addJSONObjectBody(jsonObject) // posting json
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {
                            if (response.getString("message").equals("success")){
                                progress.dismiss();


                                for (User user: userList){
                                    user.setPassword(password);
                                    userHash.put(user.getId(), user);
                                    txtPassword.setText(password);
                                }
                                Toast.makeText(getApplicationContext(),response.getString("message"), Toast.LENGTH_SHORT).show();
                                //intentToList();
                            }else{
                                progress.dismiss();
                                Toast.makeText(getApplicationContext(),response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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

    private void intentToList(){
        Intent intent = new Intent(this, UserListActivity.class);
        startActivity(intent);
        finish();
    }

    private void intentToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){
        if (getIntent().hasExtra("for")){
            intentToMain();
        }else{
            intentToList();
        }

    }
}
