package dev.salgino.gasapp.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Spinner;

import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dev.salgino.gasapp.model.Station;
import dev.salgino.gasapp.model.User;

import static dev.salgino.gasapp.utils.GlobalVars.BASE_DIR;
import static dev.salgino.gasapp.utils.GlobalVars.EXTERNAL_DIR_FILES;
import static dev.salgino.gasapp.utils.GlobalVars.IMAGES_DIR;
import static dev.salgino.gasapp.utils.GlobalVars.PICTURES_DIR_FILES;
import static dev.salgino.gasapp.utils.GlobalVars.selectedStationHash;
import static dev.salgino.gasapp.utils.GlobalVars.userHash;


public class GlobalHelper {

    public static final String SELECT_USER = "selectUser";
    public static final String SELECT_STATION = "selectStation";
    public static final String SELECT_RETURN = "selectReturn";

    public static String getVersion(Context context){
        PackageManager manager = context.getPackageManager();
        String version = "";
        try {
            PackageInfo info = manager.getPackageInfo(
                    context.getPackageName(), 0);
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "V"+ version;
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        //dd/MM/yyyy hh:mm:ss.SSS
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String convertDate(String currentDate, String currentDateFormat, String newDateFormar) {
        String strCurrentDate = currentDate;
        SimpleDateFormat format = new SimpleDateFormat(currentDateFormat);
        Date newDate = null;
        try {
            newDate = format.parse(strCurrentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        format = new SimpleDateFormat(newDateFormar);
        String date = format.format(newDate);

        return date;
    }

    public static Date convertStringToDateFormat(String dateString, String format){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return convertedDate;
    }


    public static void createFolder() {
        File folder = new File(BASE_DIR + EXTERNAL_DIR_FILES);
        File pictures = new File(BASE_DIR + PICTURES_DIR_FILES);
        File imageFolder = new File(IMAGES_DIR);
        if (!folder.exists()) {
            folder.mkdir();
        }

        if (!imageFolder.exists()) {
            imageFolder.mkdir();
        }

        if (!pictures.exists()) {
            pictures.mkdir();
        }
    }

    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    public static void write(Context context, String fileName, String data) {
        Writer writer = null;
        File root = Environment.getExternalStorageDirectory();
        File outDir = new File(root.getAbsolutePath() + File.separator + "AAAA");
        if (!outDir.isDirectory()) {
            outDir.mkdir();
        }
        try {
            if (!outDir.isDirectory()) {
                throw new IOException(
                        "Unable to create directory. Maybe the SD card is mounted?");
            }
            File outputFile = new File(outDir, fileName);
            writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(data);
//            Toast.makeText(context.getApplicationContext(),
//                    "Report successfully saved to: " + outputFile.getAbsolutePath(),
//                    Toast.LENGTH_LONG).show();
            writer.close();
        } catch (IOException e) {
            Log.w("eztt", e.getMessage(), e);
//            Toast.makeText(context, e.getMessage() + " Unable to write to external storage.",
//                    Toast.LENGTH_LONG).show();
        }

    }

    public static SoundPool playSound(Context context, int soundInRaw){
        SoundPool soundPool;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(attributes)
                    .build();
        }
        else {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        }
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(sampleId, 1.0f, 1.0f, 0, 0, 1.0f);
            }
        });
        soundPool.load(context, soundInRaw, 1);


        return soundPool;
    }

    public static boolean isLoggedIn(){
        List<User> list = new ArrayList<>();
        list = userHash.getAllValues();

        if (list != null && !list.isEmpty()){
            for (User user:list){
                if (user.getId()!=null && !user.getId().equals("")){
                    return true;
                }else{
                    return false;
                }
            }
        }

        return false;
    }

    public static String getRole(){
        List<User> list = new ArrayList<>();
        list = userHash.getAllValues();

        if (!list.isEmpty()){
            for (User user:list){
                if (user.getId()!=null && !user.getId().equals("")){
                    return user.getRole();
                }
            }
        }

        return "";
    }


    public static String getUserId(){
        List<User> list = new ArrayList<>();
        list = userHash.getAllValues();

        if (!list.isEmpty()){
            for (User user:list){
                if (user.getId()!=null && !user.getId().equals("")){
                    return user.getId();
                }
            }
        }

        return "";
    }

    public static String getUserEmail(){
        List<User> list = new ArrayList<>();
        list = userHash.getAllValues();

        if (!list.isEmpty()){
            for (User user:list){
                if (user.getEmail()!=null && !user.getEmail().equals("")){
                    return user.getEmail();
                }
            }
        }

        return "";
    }

    public static String getAddress(){
        List<User> list = new ArrayList<>();
        list = userHash.getAllValues();

        if (!list.isEmpty()){
            for (User user:list){
                if (user.getAddress()!=null && !user.getAddress().equals("")){
                    return user.getAddress();
                }
            }
        }

        return "";
    }

    public static String getPassword(){
        List<User> list = new ArrayList<>();
        list = userHash.getAllValues();

        if (!list.isEmpty()){
            for (User user:list){
                if (user.getPassword()!=null && !user.getPassword().equals("")){
                    return user.getPassword();
                }
            }
        }

        return "";
    }

    public static int getSpinnerIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().toLowerCase().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }

    public static boolean isSelectedStation(String id){
        boolean b = false;
        List<Station> stationList = selectedStationHash.getAllValues();
        if (stationList!=null && !stationList.isEmpty()){
            for (Station station:stationList){
                if (station.getId().equals(id)){
                    b = true;
                }
            }
        }

        return b;
    }
}
