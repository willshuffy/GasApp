package dev.salgino.gasapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dev.salgino.gasapp.R;
import dev.salgino.gasapp.interfaces.PaginationAdapterCallback;
import dev.salgino.gasapp.model.Station;


/**
 * Created by Suleiman on 19/10/16.
 */

public class StationMultiselectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // View Types
    //private static final int HEADER = 0;
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<Station> list;
    private List<Station> headerList;
    private Context context;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    private PaginationAdapterCallback mCallback;

    private String errorMsg;

    public StationMultiselectAdapter(Context context) {
        this.context = context;
        this.mCallback = (PaginationAdapterCallback) context;
        list = new ArrayList<>();
        headerList = new ArrayList<>();
    }

    public List<Station> getHeaderList() {

        Set<Station> collection=new HashSet<Station>();
        for (Station contact : headerList) {
            collection.add(contact);
        }

        return headerList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {

//            case HEADER:
//                View itemView = inflater.inflate(R.layout.view_item_header, parent, false);
//                return new HeaderViewHolder(itemView);

            case ITEM:
                View viewItem = inflater.inflate(R.layout.item_station_multiselect, parent, false);

                viewHolder = new ViewHolder(viewItem);


                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(viewLoading);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Station result = list.get(position);


        //Log.e("zona", result.getZone_code());

        switch (getItemViewType(position)) {

//            case HEADER:
//                final HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
//                //Log.e("zona", result.getZone_code());
//                headerHolder.tvHeader.setText(context.getString(R.string.zone) + " " +result.getZone_code());
//                break;

            case ITEM:
                final ViewHolder viewHolder = (ViewHolder) holder;


                viewHolder.tvName.setText(result.getName());
                viewHolder.tvDistrict.setText(result.getDistrict());

                viewHolder.itemView.setBackgroundColor(result.isSelected() ? ContextCompat.getColor(context, R.color.md_orange_700) : Color.WHITE);
                viewHolder.tvName.setTextColor(result.isSelected() ? ContextCompat.getColor(context, R.color.md_white_1000) : ContextCompat.getColor(context, R.color.md_grey_700));
                viewHolder.tvDistrict.setTextColor(result.isSelected() ? ContextCompat.getColor(context, R.color.md_white_1000) : ContextCompat.getColor(context, R.color.md_grey_700));
                viewHolder.tvDivider.setTextColor(result.isSelected() ? ContextCompat.getColor(context, R.color.md_white_1000) : ContextCompat.getColor(context, R.color.md_grey_700));

                viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        result.setSelected(!result.isSelected());
                        viewHolder.itemView.setBackgroundColor(result.isSelected() ? ContextCompat.getColor(context, R.color.md_orange_700) : Color.WHITE);
                        viewHolder.tvName.setTextColor(result.isSelected() ? ContextCompat.getColor(context, R.color.md_white_1000) : ContextCompat.getColor(context, R.color.md_grey_700));
                        viewHolder.tvDistrict.setTextColor(result.isSelected() ? ContextCompat.getColor(context, R.color.md_white_1000) : ContextCompat.getColor(context, R.color.md_grey_700));
                        viewHolder.tvDivider.setTextColor(result.isSelected() ? ContextCompat.getColor(context, R.color.md_white_1000) : ContextCompat.getColor(context, R.color.md_grey_700));
                    }
                });

                break;

            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;

                if (retryPageLoad) {
                    loadingVH.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.mProgressBar.setVisibility(View.GONE);

                    loadingVH.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    context.getString(R.string.error));

                } else {
                    loadingVH.mErrorLayout.setVisibility(View.GONE);
                    loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public int getItemViewType(int position) {

//        if (position == 0 && isLoadingAdded){
//            return HEADER;
//        }
//
//        if (position==1 && isLoadingAdded){
//            return ITEM;
//        }else{
//            return LOADING;
//        }

//        if (position == 0) {
//            return HEADER;
//        } else {
//            return (position == list.size() - 1 && isLoadingAdded) ? LOADING: ITEM;
//        }

        return (position == list.size() - 1 && isLoadingAdded) ? LOADING: ITEM;
    }


    public void add(Station r) {
        list.add(r);
        notifyItemInserted(list.size() - 1);
    }

    public void addAll(List<Station> moveResults) {
        for (Station result : moveResults) {
            add(result);
        }
    }

    public void remove(Station r) {
        int position = list.indexOf(r);
        if (position > -1) {
            list.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public void clearAll() {
        isLoadingAdded = false;
        if (!list.isEmpty()){
            list.clear();
            notifyDataSetChanged();
        }

    }


    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Station());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = list.size() - 1;
        Station result = getItem(position);

        if (result != null) {
            list.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Station getItem(int position) {

        if (list !=null){
            return list.get(position);
        }

        return null;

    }

    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show
     * @param errorMsg to display if page load fails
     */
    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(list.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }

    /**
     * Main list's content ViewHolder
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDistrict;
        private TextView tvName;
        private TextView tvDivider;
        private LinearLayout mainLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            tvDistrict = itemView.findViewById(R.id.tvDistrict);
            tvName = itemView.findViewById(R.id.tvName);
            tvDivider = itemView.findViewById(R.id.tvDivider);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        public LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = itemView.findViewById(R.id.loadmore_errorlayout);

            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:

                    showRetry(false, null);
                    mCallback.retryPageLoad();

                    break;
            }
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView tvHeader;

        HeaderViewHolder(View itemView) {
            super(itemView);
//            tvHeader = itemView.findViewById(R.id.tvHeader);
        }

    }

}
