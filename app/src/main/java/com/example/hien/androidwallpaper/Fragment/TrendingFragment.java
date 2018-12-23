package com.example.hien.androidwallpaper.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hien.androidwallpaper.Common.Common;
import com.example.hien.androidwallpaper.Interface.ItemClickListener;
import com.example.hien.androidwallpaper.Model.WallpaperItem;
import com.example.hien.androidwallpaper.R;
import com.example.hien.androidwallpaper.ViewHolder.ListWallpaperViewHolder;
import com.example.hien.androidwallpaper.WallpaperDetail;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrendingFragment extends Fragment {

    RecyclerView recyclerView;

    FirebaseDatabase database;
    DatabaseReference category;

    FirebaseRecyclerOptions<WallpaperItem> options, reverseOptions;
    FirebaseRecyclerAdapter<WallpaperItem, ListWallpaperViewHolder> adapter;

    private static TrendingFragment INSTANCE = null;

    public TrendingFragment() {
        //Init FirebaseDatabase
        database = FirebaseDatabase.getInstance();
        category = database.getReference(Common.STR_WALLPAPER);

        Query query = category.orderByChild("viewCount")
                .limitToFirst(20); //Limit Number of Trending Wallpaper
        options = new FirebaseRecyclerOptions.Builder<WallpaperItem>()
                .setQuery(query, WallpaperItem.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<WallpaperItem, ListWallpaperViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ListWallpaperViewHolder holder, int position, @NonNull final WallpaperItem model) {
                Picasso.get()
                        .load(model.getImageUrl())
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(holder.wallpaper, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(model.getImageUrl())
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
                        Intent intent = new Intent(getActivity(), WallpaperDetail.class);
                        Common.selected_wallpaper = model;
                        Common.selected_wallpaper_key = adapter.getRef(position).getKey();
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ListWallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_wallpaper_item, parent, false);
                int height = parent.getMeasuredHeight()/2;
                itemView.setMinimumHeight(height);
                return  new ListWallpaperViewHolder(itemView);
            }
        };
    }

    public static TrendingFragment getInstance(){
        if (INSTANCE == null)
            INSTANCE = new TrendingFragment();
        return INSTANCE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trending, container, false);

        recyclerView = view.findViewById(R.id.recycler_trending);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        //gridLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        //linearLayoutManager.setStackFromEnd(true);
        //linearLayoutManager.setReverseLayout(true);
        //recyclerView.setLayoutManager(linearLayoutManager);

        loadTrendingList();

        return view;
    }



    private void loadTrendingList() {
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        if(adapter != null)
            adapter.stopListening();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter != null)
            adapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter != null)
            adapter.startListening();
    }
}
