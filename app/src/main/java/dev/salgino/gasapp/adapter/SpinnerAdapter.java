package dev.salgino.gasapp.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import dev.salgino.gasapp.R;
import dev.salgino.gasapp.model.User;

public class SpinnerAdapter extends ArrayAdapter<User> {

    // Your sent context
    private Context context;
    // Your custom values for the spinner (User)
    private User[] values;

    public SpinnerAdapter(Context context, int textViewResourceId,
                       User[] values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public int getCount(){
        return values.length;
    }

    @Override
    public User getItem(int position){
        return values[position];
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView tvSpinner = (TextView) super.getView(position, convertView, parent);
        if (position==0){
            tvSpinner.setTextColor(ContextCompat.getColor(context, R.color.md_black_1000));
        }

        tvSpinner.setText(values[position].getRole());

        return tvSpinner;

    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView tvSpinner = (TextView) super.getView(position, convertView, parent);


        if (position==0){
            tvSpinner.setTextColor(ContextCompat.getColor(context, R.color.md_black_1000));
        }

        tvSpinner.setText(values[position].getRole());
        return tvSpinner;
    }
}

