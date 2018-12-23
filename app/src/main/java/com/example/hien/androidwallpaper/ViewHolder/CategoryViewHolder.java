package com.example.hien.androidwallpaper.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hien.androidwallpaper.Interface.ItemClickListener;
import com.example.hien.androidwallpaper.R;

/**
 * Created by Hien on 25/03/2018.
 */

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView category_name;
    public ImageView background_image;

    ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CategoryViewHolder(View itemView) {
        super(itemView);
        background_image = itemView.findViewById(R.id.image);
        category_name = itemView.findViewById(R.id.name);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition());
    }
}
