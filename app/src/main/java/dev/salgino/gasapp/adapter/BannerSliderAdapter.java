package dev.salgino.gasapp.adapter;

import dev.salgino.gasapp.R;
import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class BannerSliderAdapter extends SliderAdapter {

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder viewHolder) {
        switch (position) {
            case 0:
                viewHolder.bindImageSlide(R.drawable.banner1);
                break;
            case 1:
                viewHolder.bindImageSlide(R.drawable.banner1);
                break;
            case 2:
                viewHolder.bindImageSlide(R.drawable.banner1);
                break;
        }
    }
}
