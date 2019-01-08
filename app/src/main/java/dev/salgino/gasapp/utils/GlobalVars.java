package dev.salgino.gasapp.utils;

import android.os.Environment;

import com.google.android.gms.maps.model.LatLng;

import net.rehacktive.waspdb.WaspDb;
import net.rehacktive.waspdb.WaspFactory;
import net.rehacktive.waspdb.WaspHash;

import java.io.File;

import static dev.salgino.gasapp.utils.GlobalHelper.SELECT_RETURN;
import static dev.salgino.gasapp.utils.GlobalHelper.SELECT_STATION;
import static dev.salgino.gasapp.utils.GlobalHelper.SELECT_USER;


public class GlobalVars {
    public static final File BASE_DIR = Environment.getExternalStorageDirectory();
    public static final String EXTERNAL_DIR_FILES = "/gas_app";
    public static final String PICTURES_DIR_FILES = "/Pictures";
    public static final String IMAGES_DIR = BASE_DIR + EXTERNAL_DIR_FILES + "/images/";
    //public static final String BASE_IP = "192.168.100.31/api_gas/";
    public static final String BASE_IP = "http://willstore19.000webhostapp.com/api_gas/";

    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_USER_ROLE = "user_role";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_ID = "id";
    public static final String KEY_TRUE = "TRUE";
    public static final String KEY_FALSE = "FALSE";
    public static int GPS_INTERVAL = 10*1000;
    public static LatLng PALU_CITY_LATLNG = new LatLng(-0.900591,119.8781782);
    public static double START_LOCATION_LAT = -0.8597643;
    public static double START_LOCATION_LNG = 119.8809325;
    public static double END_LOCATION_LAT = -0.859829;
    public static double END_LOCATION_LNG = 119.8804728;
    public static String START_LOCATION_NAME = "#PT. Kolamtraco";
    public static String END_LOCATION_NAME = "PT. Kolamtraco#";
    public static String START_LOCATION_DISTRICT = "Talise";

    public static final String DB_MASTER = "DB_CORE_LOGIN_APPS";
    public static final String DB_PASS = "CORELOGIN12344321";
    public static final WaspDb db = WaspFactory.openOrCreateDatabase(getDatabasePath(), DB_MASTER, getDatabasePass());
    public static final WaspHash userHash = db.openOrCreateHash(SELECT_USER);
    public static final WaspHash selectedStationHash = db.openOrCreateHash(SELECT_STATION);
    public static final WaspHash selectedReturn = db.openOrCreateHash(SELECT_RETURN);

    public static String getDatabasePath(){
        File file = new File(Environment.getExternalStorageDirectory() + EXTERNAL_DIR_FILES +"/db/" + Encrypts.encrypt("POST"));
        if(!file.exists())file.mkdirs();
        return Environment.getExternalStorageDirectory() + EXTERNAL_DIR_FILES +"/db/" + Encrypts.encrypt("POST");
    }

    public static String getDatabasePass(){
        return Encrypts.encrypt(DB_PASS);
    }
    public static WaspHash getTableHash(String tableName){
        WaspDb db = WaspFactory.openOrCreateDatabase(GlobalVars.getDatabasePath(),
                GlobalVars.DB_MASTER,
                GlobalVars.getDatabasePass());
        WaspHash returnedTable = db.openOrCreateHash(tableName);
        return returnedTable;
    }
}
