package dev.salgino.gasapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import dev.salgino.gasapp.R;
import dev.salgino.gasapp.model.Station;
import dev.salgino.gasapp.model.StationHeader;
import dev.salgino.gasapp.utils.GlobalHelper;

import static dev.salgino.gasapp.utils.GlobalHelper.isSelectedStation;
import static dev.salgino.gasapp.utils.GlobalVars.selectedStationHash;

public class MultiSelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvSection;
        HeaderViewHolder(View itemView) {
            super(itemView);
            tvSection = itemView.findViewById(R.id.tvSection);
        }
    }

    private static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvDistrict;
        TextView tvName;
        TextView tvDivider;
        LinearLayout mainLayout;

        EventViewHolder(View itemView) {
            super(itemView);
            tvDistrict = itemView.findViewById(R.id.tvDistrict);
            tvName = itemView.findViewById(R.id.tvName);
            tvDivider = itemView.findViewById(R.id.tvDivider);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }

    @NonNull
    private List<ListItem> items = Collections.emptyList();
    private Context context;

    public MultiSelectAdapter(Context context, @NonNull List<ListItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ListItem.TYPE_HEADER: {
                View itemView = inflater.inflate(R.layout.view_item_header, parent, false);
                return new HeaderViewHolder(itemView);
            }
            case ListItem.TYPE_EVENT: {
                View itemView = inflater.inflate(R.layout.item_station_multiselect, parent, false);
                return new EventViewHolder(itemView);
            }
            default:
                throw new IllegalStateException("unsupported item type");
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ListItem.TYPE_HEADER: {
                StationHeader header = (StationHeader) items.get(position);
                HeaderViewHolder holder = (HeaderViewHolder) viewHolder;

                holder.tvSection.setText(context.getString(R.string.zone)+" "+header.getZone_code());
                break;
            }
            case ListItem.TYPE_EVENT: {
                final StationItem item = (StationItem) items.get(position);
                final EventViewHolder holder = (EventViewHolder) viewHolder;

                holder.tvDistrict.setText(item.getStation().getDistrict());
                holder.tvName.setText(item.getStation().getName());


                holder.itemView.setBackgroundColor(item.getStation().isSelected() ? ContextCompat.getColor(context, R.color.md_orange_700) : Color.WHITE);
                holder.tvName.setTextColor(item.getStation().isSelected() ? ContextCompat.getColor(context, R.color.md_white_1000) : ContextCompat.getColor(context, R.color.md_grey_700));
                holder.tvDistrict.setTextColor(item.getStation().isSelected() ? ContextCompat.getColor(context, R.color.md_white_1000) : ContextCompat.getColor(context, R.color.md_grey_700));
                holder.tvDivider.setTextColor(item.getStation().isSelected() ? ContextCompat.getColor(context, R.color.md_white_1000) : ContextCompat.getColor(context, R.color.md_grey_700));

//                List<Station> stationList = selectedStationHash.getAllValues();
//                if (stationList!=null && !stationList.isEmpty()){
//                    for (Station station:stationList){
//                        if (station.getId().equals(item.getStation().getId())){
//                            item.getStation().setSelected(true);
//                        }else{
//                            item.getStation().setSelected(false);
//                        }
//                    }
//                }

                holder.mainLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        item.getStation().setSelected(!item.getStation().isSelected());
                        holder.itemView.setBackgroundColor(item.getStation().isSelected() ? ContextCompat.getColor(context, R.color.md_orange_700) : Color.WHITE);
                        holder.tvName.setTextColor(item.getStation().isSelected() ? ContextCompat.getColor(context, R.color.md_white_1000) : ContextCompat.getColor(context, R.color.md_grey_700));
                        holder.tvDistrict.setTextColor(item.getStation().isSelected() ? ContextCompat.getColor(context, R.color.md_white_1000) : ContextCompat.getColor(context, R.color.md_grey_700));
                        holder.tvDivider.setTextColor(item.getStation().isSelected() ? ContextCompat.getColor(context, R.color.md_white_1000) : ContextCompat.getColor(context, R.color.md_grey_700));

                        if (item.getStation().isSelected()){
                            selectedStationHash.put(item.getStation().getId(), item.getStation());
                        }else{
                            selectedStationHash.remove(item.getStation().getId());
                        }
                    }
                });

                break;
            }
            default:
                throw new IllegalStateException("unsupported item type");
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

}