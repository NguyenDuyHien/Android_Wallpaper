package com.example.hien.androidwallpaper;

import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.hien.androidwallpaper.Common.Common;
import com.example.hien.androidwallpaper.Database.DataSource.RecentRepository;
import com.example.hien.androidwallpaper.Database.LocalDatabase.LocalDatabase;
import com.example.hien.androidwallpaper.Database.LocalDatabase.RecentsDataSource;
import com.example.hien.androidwallpaper.Database.Recent;
import com.example.hien.androidwallpaper.Helper.SaveHelper;
import com.example.hien.androidwallpaper.Model.WallpaperItem;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dmax.dialog.SpotsDialog;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WallpaperDetail extends AppCompatActivity {

    Button btnWallpaper, btnDownload;
    ImageButton btnFacebook, btnTwitter;
    ImageView imageView;
    CoordinatorLayout coordinatorLayout;
    Toolbar toolbar;
    BottomSheetBehavior bottomSheetBehavior;
    boolean isBottomSheetOpen = false;

    //Room Database
    CompositeDisposable compositeDisposable;
    RecentRepository recentRepository;

    //Facebook
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    String filename = UUID.randomUUID().toString() + ".png";
    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    File file = new File(new StringBuilder(path + "/temp/").append(filename)
            .toString());

    FirebaseUser user;

    List<String> fav_key_list;
    int isFav = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case Common.DOWNLOAD_REQUEST_CODE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AlertDialog alertDialog = new SpotsDialog(WallpaperDetail.this);
                    alertDialog.show();
                    alertDialog.setMessage("Downloading...");

                    Picasso.get()
                            .load(Common.selected_wallpaper.getImageUrl())
                            .into(new SaveHelper(getBaseContext(),
                                    alertDialog,
                                    getApplicationContext().getContentResolver(),
                                    filename,
                                    "Android Wallpaper"));
                }
                else {
                    Toast.makeText(this, "You need accept this permission to download image", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case Common.TWITTER_REQUEST_CODE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    shareToTwitter();
                }
                else {
                    Toast.makeText(this, "You need accept this permission to share image", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    private com.squareup.picasso.Target target = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
            try {
                if (android.os.Build.VERSION.SDK_INT < 24) {
                    wallpaperManager.setBitmap(bitmap);
                    Snackbar.make(coordinatorLayout, "Wallpaper was set", Snackbar.LENGTH_SHORT).show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(WallpaperDetail.this);

                    // Set a title for alert dialog
                    builder.setTitle("Set as wallpaper...");

                    // Initializing an array of colors
                    final String[] wallpaperset = new String[]{
                            "Home screen",
                            "Lock screen",
                            "Home and lock screens"
                    };

                    // Set the list of items for alert dialog
                    builder.setItems(wallpaperset, new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0:
                                    try {
                                        wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Snackbar.make(coordinatorLayout, "Wallpaper was set", Snackbar.LENGTH_SHORT).show();
                                    break;
                                case 1:
                                    try {
                                        wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Snackbar.make(coordinatorLayout, "Wallpaper was set", Snackbar.LENGTH_SHORT).show();
                                    break;
                                case 2:
                                    try {
                                        wallpaperManager.setBitmap(bitmap);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Snackbar.make(coordinatorLayout, "Wallpaper was set", Snackbar.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    });

                    AlertDialog dialog = builder.create();
                    // Display the alert dialog on interface
                    dialog.show();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    private Target wallpaperConvertToBitMap = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            SharePhoto sharePhoto = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if(ShareDialog.canShow(SharePhotoContent.class)){
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(sharePhoto)
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_detail);

        //getUser
        user = FirebaseAuth.getInstance().getCurrentUser();

        fav_key_list = new ArrayList<>();
        if (user != null) {
            FirebaseDatabase.getInstance().getReference().child("UserFav").child(user.getUid()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    fav_key_list.add(dataSnapshot.getKey());
                    if (isFavorite(Common.selected_wallpaper_key, fav_key_list)){
                        isFav = 1;
                        invalidateOptionsMenu();
                    }

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    fav_key_list.remove(dataSnapshot.getKey());
                    isFav = 0;
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        //Init Twitter
        String TWITTER_API = getResources().getString(R.string.twitter_api);
        String TWITTER_API_SECRET = getResources().getString(R.string.twitter_api_secret);

        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(TWITTER_API, TWITTER_API_SECRET))
                .debug(true)
                .build();
        Twitter.initialize(config);

        //Init Facebook
        callbackManager = CallbackManager.Factory.create();
        shareDialog= new ShareDialog(this);

        //Init Room Database
        compositeDisposable = new CompositeDisposable();
        LocalDatabase database = LocalDatabase.getInstance(this);
        recentRepository = RecentRepository.getInstance(RecentsDataSource.getInstance(database.recentsDAO()));

        //Init views
        coordinatorLayout = findViewById(R.id.coordinator);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        View bottomSheet = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        imageView = findViewById(R.id.imageViewWallpaper);
        Picasso.get()
                .load(Common.selected_wallpaper.getImageUrl())
                .into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBottomSheetOpen){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    isBottomSheetOpen = false;
                }
            }
        });

        btnFacebook = findViewById(R.id.btnFacebook);
        btnTwitter = findViewById(R.id.btnTwitter);
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPackageInstalled(WallpaperDetail.this, "com.facebook.katana")) {
                    //Create Callback
                    shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                        @Override
                        public void onSuccess(Sharer.Result result) {
                            Toast.makeText(WallpaperDetail.this, "Share succeed", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancel() {
                            Toast.makeText(WallpaperDetail.this, "Share cancelled", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(FacebookException error) {
                            Toast.makeText(WallpaperDetail.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    //Get wallpaper
                    Picasso.get()
                            .load(Common.selected_wallpaper.getImageUrl())
                            .into(wallpaperConvertToBitMap);
                } else {
                    Toast.makeText(WallpaperDetail.this, "Facebook isn't installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPackageInstalled(WallpaperDetail.this, "com.twitter.android")) {
                    //Check permission
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ActivityCompat.checkSelfPermission(WallpaperDetail.this,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, Common.TWITTER_REQUEST_CODE);
                        } else {
                            shareToTwitter();
                        }
                    if (Build.VERSION.SDK_INT < 23){
                        shareToTwitter();
                    }
                }
                }else {
                    Toast.makeText(WallpaperDetail.this, "Twitter isn't installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getCacheDir();

        //Check prviousActivity
        Intent intent = getIntent();
        String previousActivity = intent.getStringExtra("FROM_ACTIVITY");
        if (previousActivity == null) {
            //Add to recent
            addToRecent();

            //Increase View Count
            increaseViewCount();
        }

        btnWallpaper = findViewById(R.id.btnWallpaper);
        btnDownload = findViewById(R.id.btnDownload);

        btnWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Picasso.get()
                        .load(Common.selected_wallpaper.getImageUrl())
                        .into(target);
            }
        });
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check permission
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ActivityCompat.checkSelfPermission(WallpaperDetail.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, Common.DOWNLOAD_REQUEST_CODE);

                    }
                    else {
                        AlertDialog alertDialog = new SpotsDialog(WallpaperDetail.this);
                        alertDialog.show();
                        alertDialog.setMessage("Downloading...");

                        Picasso.get()
                                .load(Common.selected_wallpaper.getImageUrl())
                                .into(new SaveHelper(getBaseContext(),
                                        alertDialog,
                                        getApplicationContext().getContentResolver(),
                                        filename,
                                        "Android Wallpaper"));
                    }
                }

                if (Build.VERSION.SDK_INT < 23){
                    AlertDialog alertDialog = new SpotsDialog(WallpaperDetail.this);
                    alertDialog.show();
                    alertDialog.setMessage("Downloading...");

                    Picasso.get()
                            .load(Common.selected_wallpaper.getImageUrl())
                            .into(new SaveHelper(getBaseContext(),
                                    alertDialog,
                                    getApplicationContext().getContentResolver(),
                                    filename,
                                    "Android Wallpaper"));
                }
            }
        });

    }

    void makeShare(Uri imagePath){
        if (imagePath != null){
            TweetComposer.Builder builder = new TweetComposer.Builder(WallpaperDetail.this)
                    .text("")
                    .image(imagePath);
            builder.show();
        }else {
            TweetComposer.Builder builder = new TweetComposer.Builder(WallpaperDetail.this)
                    .text("");
            builder.show();
        }
    }

    void shareToTwitter(){
        Glide.with(WallpaperDetail.this)
                .asBitmap()
                .load(Common.selected_wallpaper.getImageUrl())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        //creating new file and getting its uri
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());

                        if (!file.getParentFile().exists())
                            file.getParentFile().mkdirs();
                        try {

                            if (file.exists())
                                file.delete();
                            file.createNewFile();

                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            resource.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                            fileOutputStream.flush();
                            fileOutputStream.close();
                        } catch (Exception e) {
                            Log.e("Glide_Error", e.getMessage());
                        }

                        if (file != null) {
                            makeShare(Uri.fromFile(file));
                        } else
                            makeShare(null);
                    }
                });
    }

    private void increaseViewCount() {
        FirebaseDatabase.getInstance()
                .getReference(Common.STR_WALLPAPER)
                .child(Common.selected_wallpaper_key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("viewCount")){
                            WallpaperItem wallpaperItem = dataSnapshot.getValue(WallpaperItem.class);
                            long count = wallpaperItem.getViewCount() - 1;
                            //Update View Count
                            Map<String, Object> updateViewCount = new HashMap<>();
                            updateViewCount.put("viewCount", count);

                            FirebaseDatabase.getInstance()
                                    .getReference(Common.STR_WALLPAPER)
                                    .child(Common.selected_wallpaper_key)
                                    .updateChildren(updateViewCount)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(WallpaperDetail.this, "Can't update view count", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
//                        else {
//                            Map<String, Object> updateViewCount = new HashMap<>();
//                            updateViewCount.put("viewCount", Long.valueOf(0));
//
//                            FirebaseDatabase.getInstance()
//                                    .getReference(Common.STR_WALLPAPER)
//                                    .child(Common.selected_wallpaper_key)
//                                    .updateChildren(updateViewCount)
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Toast.makeText(WallpaperDetail.this, "Can't set default view count", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void addToRecent() {
        Disposable disposable = Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                Recent recents = new Recent(
                        Common.selected_wallpaper.getImageUrl(),
                        Common.selected_wallpaper.getCategoryID(),
                        String.valueOf(System.currentTimeMillis()),
                        Common.selected_wallpaper_key);
                recentRepository.insertRecents(recents);
                e.onComplete();
            }
        }).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new Consumer<Object>() {
                @Override
                public void accept(Object o) throws Exception {

                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Log.e("ERROR", throwable.getMessage());
                }
            }, new Action() {
                @Override
                public void run() throws Exception {

                }
            });

        compositeDisposable.add(disposable);
    }

    public static boolean isPackageInstalled(Context c, String targetPackage) {
        PackageManager pm = c.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    public void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }

    public boolean isFavorite(String key, List<String> fav_list){
        return fav_list.contains(key);
    }

    @Override
    protected void onDestroy() {
        Picasso.get().cancelRequest(target);
        deleteRecursive(new File(new StringBuilder(path + "/temp/").toString()));
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wallpaper_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem btnDelete = menu.findItem(R.id.btnDelete);
        MenuItem cbFav = menu.findItem(R.id.checkboxFav);
        if (user == null){
            btnDelete.setVisible(false);
            cbFav.setVisible(false);
        }else {
            if (isFav == 0)
                cbFav.setIcon(R.drawable.ic_fav);
            else
                cbFav.setIcon(R.drawable.ic_fav_checked);

            if (user.getUid().equals(Common.selected_wallpaper.getUserID()))
                btnDelete.setVisible(true);
            else
                btnDelete.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.btnShare) {
            if (isBottomSheetOpen == false) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                isBottomSheetOpen = true;
            }else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                isBottomSheetOpen = false;
            }
        } else if (id == R.id.btnDelete){
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(WallpaperDetail.this);
            builder.setTitle("");
            builder.setMessage("Are you sure?");
            builder.setCancelable(false);
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Delete from database
                    FirebaseDatabase.getInstance()
                            .getReference(Common.STR_WALLPAPER)
                            .child(Common.selected_wallpaper_key)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    dataSnapshot.getRef().removeValue();
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {

                                                                }
                                                            });

                    //Delete from favorite list
                    if (isFav == 1){
                        FirebaseDatabase.getInstance()
                                .getReference("UserFav")
                                .child(user.getUid())
                                .child(Common.selected_wallpaper_key)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        dataSnapshot.getRef().removeValue();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }

                    //Delete from storage
                    StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(Common.selected_wallpaper.getImageUrl());
                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            
                        }
                    });

                    finish();
                    Toast.makeText(WallpaperDetail.this, "Deleted", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            android.support.v7.app.AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else if (id == R.id.checkboxFav){
            if (isFav == 0) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference().child("UserFav").child(user.getUid()).child(Common.selected_wallpaper_key);
                reference.child("categoryID").setValue(Common.selected_wallpaper.getCategoryID());
                reference.child("imageUrl").setValue(Common.selected_wallpaper.getImageUrl());
                reference.child("userID").setValue(Common.selected_wallpaper.getUserID());
                Toast.makeText(WallpaperDetail.this, "Added to favorite", Toast.LENGTH_SHORT).show();
            }else {
                FirebaseDatabase.getInstance().getReference().child("UserFav").child(user.getUid()).child(Common.selected_wallpaper_key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeValue();
                        isFav = 0;
                        Toast.makeText(WallpaperDetail.this, "Removed from favorite", Toast.LENGTH_SHORT).show();
                        invalidateOptionsMenu();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

        if(item.getItemId() == android.R.id.home)
            finish(); // Close activity when click back button

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isBottomSheetOpen) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            isBottomSheetOpen = false;
        }
        else
            super.onBackPressed();
    }
}
