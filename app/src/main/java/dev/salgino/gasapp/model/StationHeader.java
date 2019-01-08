package dev.salgino.gasapp.model;

import android.support.annotation.NonNull;


import dev.salgino.gasapp.adapter.ListItem;

public class StationHeader extends ListItem {

    @NonNull
    private String zone_code;

    public StationHeader(@NonNull String zone_code) {
        this.zone_code = zone_code;
    }

    @NonNull
    public String getZone_code() {
        return zone_code;
    }

    public void setZone_code(@NonNull String zone_code) {
        this.zone_code = zone_code;
    }

    @Override
    public int getType() {
        return TYPE_HEADER;
    }
}