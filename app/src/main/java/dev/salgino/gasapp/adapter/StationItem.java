package dev.salgino.gasapp.adapter;

import android.support.annotation.NonNull;

import dev.salgino.gasapp.model.Station;

public class StationItem extends ListItem {

    @NonNull
    private Station station;

    public StationItem(@NonNull Station station) {
        this.station = station;
    }

    @NonNull
    public Station getStation() {
        return station;
    }

    // here getters and setters
    // for title and so on, built
    // using event

    @Override
    public int getType() {
        return TYPE_EVENT;
    }

}