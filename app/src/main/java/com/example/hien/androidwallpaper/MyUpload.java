package com.example.hien.androidwallpaper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.hien.androidwallpaper.Common.Common;
import com.example.hien.androidwallpaper.Interface.ItemClickListener;
import com.example.hien.androidwallpaper.Model.WallpaperItem;
import com.example.hien.androidwallpaper.ViewHolder.ListWallpaperViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MyUpload extends AppCompatActivity {

    Query query;
    FirebaseRecyclerOptions<WallpaperItem> options;
    FirebaseRecyclerAdapter<WallpaperItem, ListWallpaperViewHolder> adapter;

    RecyclerView recyclerView;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_upload);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("My Upload");

        recyclerView = findViewById(R.id.recycler_wallpaper);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        loadWallpaperList();
    }

    private void loadWallpaperList() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        query = FirebaseDatabase.getInstance().getReference(Common.STR_WALLPAPER)
                .orderByChild("userID").equalTo(user.getUid());
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
                        Intent intent = new Intent(MyUpload.this, WallpaperDetail.class);
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

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter != null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        if(adapter != null)
            adapter.stopListening();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter != null)
            adapter.startListening();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish(); // Close activity when click back button
        return super.onOptionsItemSelected(item);
    }
}
