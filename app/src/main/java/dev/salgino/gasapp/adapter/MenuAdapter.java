package dev.salgino.gasapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import dev.salgino.gasapp.R;
import dev.salgino.gasapp.interfaces.RecyclerviewClick;
import dev.salgino.gasapp.model.MainMenu;

/**
 * Created by ELTE on 5/1/2018.
 */

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    Context context;

    private List<MainMenu> list;
    RecyclerviewClick listener;

    public MenuAdapter(Context context, List<MainMenu> boardingList, RecyclerviewClick listener) {
        this.context = context;
        this.list = boardingList;
        this.listener = listener;
    }

    @Override
    public MenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        final MenuAdapter.ViewHolder holder=new MenuAdapter.ViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, holder.getAdapterPosition());
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MainMenu menu = list.get(holder.getAdapterPosition());
        holder.tvMenu.setText(menu.getMenu());
        holder.ivMenu.setImageResource(menu.getImage());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvMenu;
        public ImageView ivMenu;

        public ViewHolder(View itemView) {
            super(itemView);
            tvMenu = itemView.findViewById(R.id.tvMenu);
            ivMenu = itemView.findViewById(R.id.ivMenu);
        }
    }
}


