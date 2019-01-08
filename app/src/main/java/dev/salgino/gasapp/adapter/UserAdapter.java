package dev.salgino.gasapp.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dev.salgino.gasapp.R;
import dev.salgino.gasapp.activities.UserListActivity;
import dev.salgino.gasapp.interfaces.PaginationAdapterCallback;
import dev.salgino.gasapp.interfaces.RecyclerviewClick;
import dev.salgino.gasapp.model.User;


/**
 * Created by Suleiman on 19/10/16.
 */

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // View Types
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<User> list;
    private Context context;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    private PaginationAdapterCallback mCallback;

    private String errorMsg;

    RecyclerviewClick listener;

    public UserAdapter(Context context, RecyclerviewClick listener) {
        this.context = context;
        this.mCallback = (PaginationAdapterCallback) context;
        list = new ArrayList<>();
        this.listener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.item_user, parent, false);

                viewHolder = new ViewHolder(viewItem);

                final RecyclerView.ViewHolder finalViewHolder = viewHolder;
                viewItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(v, finalViewHolder.getAdapterPosition());

                    }
                });

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
        final User result = list.get(position);

        switch (getItemViewType(position)) {

            case ITEM:
                final ViewHolder viewHolder = (ViewHolder) holder;

                //String id = "SA"+result.getId()+result.getCreated_at().replaceAll("[^\\d]", "");

                viewHolder.tvTitle.setText(result.getName());
                viewHolder.tvSubtitle.setText(result.getRole());

                viewHolder.tvSubtitle.setTextIsSelectable(false);

                viewHolder.tvSubtitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.cvRoot.performClick();
                    }
                });

                viewHolder.cvRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((UserListActivity)context).intentToAccount(result);
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
        return (position == list.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    public void add(User r) {
        list.add(r);
        notifyItemInserted(list.size() - 1);
    }

    public void addAll(List<User> moveResults) {
        for (User result : moveResults) {
            add(result);
        }
    }

    public void remove(User r) {
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
        add(new User());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = list.size() - 1;
        User result = getItem(position);

        if (result != null) {
            list.remove(position);
            notifyItemRemoved(position);
        }
    }

    public User getItem(int position) {

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
        private CardView cvRoot;
        private TextView tvTitle;
        private TextView tvSubtitle;


        public ViewHolder(View itemView) {
            super(itemView);
            cvRoot = itemView.findViewById(R.id.cvRoot);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);

            tvSubtitle.setPaintFlags(0);
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

}
