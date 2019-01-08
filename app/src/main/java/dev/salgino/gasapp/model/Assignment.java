package dev.salgino.gasapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Assignment implements Parcelable {

    private String id;
    private String station_id;
    private String driver_id;
    private String stationName;
    private String address;
    private String district;
    private String zone_code;
    private String latitude;
    private String longitude;
    private String distance;

    public static final Parcelable.Creator<Assignment> CREATOR = new Parcelable.Creator<Assignment>() {
        public Assignment createFromParcel(Parcel in) {
            return new Assignment(in);
        }

        public Assignment[] newArray(int size) {
            return new Assignment[size];
        }
    };

    protected Assignment(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.station_id = ((String) in.readValue((String.class.getClassLoader())));
        this.driver_id = ((String) in.readValue((String.class.getClassLoader())));
        this.stationName = ((String) in.readValue((String.class.getClassLoader())));
        this.address = ((String) in.readValue((String.class.getClassLoader())));
        this.district = ((String) in.readValue((String.class.getClassLoader())));
        this.zone_code = ((String) in.readValue((String.class.getClassLoader())));
        this.latitude = ((String) in.readValue((String.class.getClassLoader())));
        this.longitude = ((String) in.readValue((String.class.getClassLoader())));
        this.distance = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Assignment() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStation_id() {
        return station_id;
    }

    public void setStation_id(String station_id) {
        this.station_id = station_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getZone_code() {
        return zone_code;
    }

    public void setZone_code(String zone_code) {
        this.zone_code = zone_code;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(station_id);
        dest.writeValue(driver_id);
        dest.writeValue(stationName);
        dest.writeValue(address);
        dest.writeValue(district);
        dest.writeValue(zone_code);
        dest.writeValue(latitude);
        dest.writeValue(longitude);
        dest.writeValue(distance);
    }

    public int describeContents() {
        return 0;
    }

}

