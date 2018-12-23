package com.example.hien.androidwallpaper.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hien.androidwallpaper.Common.Common;
import com.example.hien.androidwallpaper.Database.Recent;
import com.example.hien.androidwallpaper.Interface.ItemClickListener;
import com.example.hien.androidwallpaper.Model.WallpaperItem;
import com.example.hien.androidwallpaper.R;
import com.example.hien.androidwallpaper.ViewHolder.ListWallpaperViewHolder;
import com.example.hien.androidwallpaper.WallpaperDetail;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Hien on 27/03/2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<ListWallpaperViewHolder> {

    private Context context;
    private List<Recent> recentsList;

    public RecyclerViewAdapter() {
    }

    public RecyclerViewAdapter(Context context, List<Recent> recentsList) {
        this.context = context;
        this.recentsList = recentsList;
    }

    @Override
    public ListWallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_wallpaper_item, parent, false);
        int height = parent.getMeasuredHeight()/2;
        itemView.setMinimumHeight(height);
        return  new ListWallpaperViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListWallpaperViewHolder holder, final int position) {
        Picasso.get()
                .load(recentsList.get(position).getImageUrl())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.wallpaper, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get()
                                .load(recentsList.get(position).getImageUrl())
                                .error(R.drawable.ic_terrain_black_24dp)
                                .into(holder.wallpaper, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Log.e("WALLPAPER_ERROR", "Couldn't load wallpaper");
                                    }
                                });
                    }
                });

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(context, WallpaperDetail.class);
                WallpaperItem wallpaperItem = new WallpaperItem();
                wallpaperItem.setCategoryID(recentsList.get(position).getCategoryID());
                wallpaperItem.setImageUrl(recentsList.get(position).getImageUrl());
                Common.selected_wallpaper = wallpaperItem;
                Common.selected_wallpaper_key = recentsList.get(position).getKey();
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recentsList.size();
    }

}
