package com.example.hien.androidwallpaper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hien.androidwallpaper.Common.Common;
import com.example.hien.androidwallpaper.Model.CategoryItem;
import com.example.hien.androidwallpaper.Model.ComputerVision.ComputerVision;
import com.example.hien.androidwallpaper.Model.ComputerVision.URLUpload;
import com.example.hien.androidwallpaper.Model.WallpaperItem;
import com.example.hien.androidwallpaper.Remote.IComputerVision;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UploadWallpaper extends AppCompatActivity {

    ImageView imgPreview;
    Button btnUpload, btnBrowser;
    MaterialSpinner spinner;
    Map<String, String> spinnerData = new HashMap<>();
    IComputerVision iComputerVision;

    private Uri filePath;

    String categoryIdSelect = "", filename = "", imageURL = "";

    //FirebaseStorage
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_wallpaper);

        imgPreview = findViewById(R.id.imgPreview);
        btnBrowser = findViewById(R.id.btnBrowser);
        btnUpload = findViewById(R.id.btnUpload);
        spinner = findViewById(R.id.spinner);
        btnUpload.setEnabled(false);

        //Firebase Storage Init
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        iComputerVision = Common.getComputerVisionAPI();

        getSpinnerData();

        btnBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browserWallpaper();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(spinner.getSelectedIndex() == 0) //Not select
                    Toast.makeText(UploadWallpaper.this, "Please select category", Toast.LENGTH_SHORT).show();
                else {
                    uploadWallpaper();
                }
            }
        });

    }

    private void uploadWallpaper() {
        if(filePath != null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            filename = UUID.randomUUID().toString();
            StorageReference reference = storageReference.child(new StringBuilder("userupload/").append(filename).toString());

            reference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            imageURL = taskSnapshot.getDownloadUrl().toString();
                            detectAdultContent(imageURL);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UploadWallpaper.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded: " + (int) progress + "%");
                        }
                    });
        }
    }

    private void detectAdultContent(final String imageURL) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (imageURL.isEmpty())
            Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show();
        else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Analyzing...");
            progressDialog.show();

            iComputerVision.analyzeImage(Common.getAPIAdultEndPoint(), new URLUpload(imageURL))
                    .enqueue(new Callback<ComputerVision>() {
                        @Override
                        public void onResponse(Call<ComputerVision> call, Response<ComputerVision> response) {
                            if(response.isSuccessful()){
                                if(!response.body().getAdult().isAdultContent()){
                                    //No adult content
                                    progressDialog.dismiss();
                                    saveUriToCategory(categoryIdSelect, imageURL, user.getUid(), 0);
                                }else {
                                    //Adult content
                                    progressDialog.dismiss();
                                    storageReference.child("userupload/" + filename)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(UploadWallpaper.this, "Upload failed! Your wallpaper has adult content", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ComputerVision> call, Throwable t) {
                            Toast.makeText(UploadWallpaper.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void saveUriToCategory(String categoryIdSelect, String imageUrl, String userID, long viewCount) {
        FirebaseDatabase.getInstance()
                .getReference(Common.STR_WALLPAPER)
                .push()
                .setValue(new WallpaperItem(categoryIdSelect, imageUrl, userID, viewCount))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UploadWallpaper.this, "Upload succeed", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void browserWallpaper() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image: "), Common.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imgPreview.setImageBitmap(bitmap);
                btnUpload.setEnabled(true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void getSpinnerData() {
        FirebaseDatabase.getInstance()
                .getReference(Common.STR_CATEGORY_WALLPAPER)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapShot:dataSnapshot.getChildren()){
                            CategoryItem item = postSnapShot.getValue(CategoryItem.class);
                            final String key = postSnapShot.getKey();

                            spinnerData.put(key, item.getName());

                            //Spinner hint
                            Object[] valueArray = spinnerData.values().toArray();
                            List<Object> valueList = new ArrayList<>();
                            valueList.add(0, "Category"); //First item is hint
                            valueList.addAll(Arrays.asList(valueArray));
                            spinner.setItems(valueList);
                            spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                                    Object[] keyArray = spinnerData.keySet().toArray();
                                    List<Object> keyList = new ArrayList<>();
                                    keyList.add(0, "Category_Key");
                                    keyList.addAll(Arrays.asList(keyArray));
                                    categoryIdSelect = keyList.get(position).toString(); //Assign key
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (!imageURL.isEmpty()){
            storageReference.child("userupload/" + filename)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(UploadWallpaper.this, "Upload cancelled", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        super.onBackPressed();
    }
}
