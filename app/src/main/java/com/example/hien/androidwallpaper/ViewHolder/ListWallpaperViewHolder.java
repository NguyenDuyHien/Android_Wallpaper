package com.example.hien.androidwallpaper.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.hien.androidwallpaper.Interface.ItemClickListener;
import com.example.hien.androidwallpaper.R;

/**
 * Created by Hien on 26/03/2018.
 */

public class ListWallpaperViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ItemClickListener itemClickListener;

    public ImageView wallpaper;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ListWallpaperViewHolder(View itemView) {
        super(itemView);
        wallpaper = itemView.findViewById(R.id.imageView);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition());
    }
}
