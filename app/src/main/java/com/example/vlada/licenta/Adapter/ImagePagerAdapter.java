package com.example.vlada.licenta.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

public class ImagePagerAdapter extends PagerAdapter {
    private ArrayList<String> images;
    private LayoutInflater inflater;

    public ImagePagerAdapter(Context context, Exercise exercise) {
        this.images = new ArrayList<>(Arrays.asList(exercise.getPicsUrl().split(" ")));
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, int position) {
        View myImageLayout = inflater.inflate(R.layout.image_slider, view, false);
        ImageView myImage = myImageLayout.findViewById(R.id.image);
        Picasso.get().load(images.get(position)).into(myImage);
        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }
}
