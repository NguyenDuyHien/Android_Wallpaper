package com.example.hien.androidwallpaper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hien.androidwallpaper.Common.Common;
import com.example.hien.androidwallpaper.Interface.ItemClickListener;
import com.example.hien.androidwallpaper.Model.WallpaperItem;
import com.example.hien.androidwallpaper.ViewHolder.ListWallpaperViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MyFavorite extends AppCompatActivity {

    Query query;
    FirebaseRecyclerOptions<WallpaperItem> options;
    FirebaseRecyclerAdapter<WallpaperItem, ListWallpaperViewHolder> adapter;

    RecyclerView recyclerView;

    int firstToast = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorite);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("My Favorite");

        recyclerView = findViewById(R.id.recycler_wallpaper);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        loadWallpaperList();
    }

    private void loadWallpaperList() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        query = FirebaseDatabase.getInstance().getReference("UserFav")
                .child(user.getUid());
        options = new FirebaseRecyclerOptions.Builder<WallpaperItem>()
                .setQuery(query, WallpaperItem.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<WallpaperItem, ListWallpaperViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ListWallpaperViewHolder holder, final int position, @NonNull final WallpaperItem model) {
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
                                        .error(R.drawable.no_image)
                                        .into(holder.wallpaper, new Callback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                adapter.getRef(position).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (firstToast == 0) {
                                                            Toast.makeText(MyFavorite.this, "Some wallpapers were deleted by uploader", Toast.LENGTH_SHORT).show();
                                                            firstToast = 1;
                                                        }
                                                    }
                                                });
                                            }
                                        });
                            }
                        });

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Intent intent = new Intent(MyFavorite.this, WallpaperDetail.class);
                        Common.selected_wallpaper = model;
                        Common.selected_wallpaper_key = adapter.getRef(position).getKey();
                        intent.putExtra("FROM_ACTIVITY", "MyFavorite");
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
