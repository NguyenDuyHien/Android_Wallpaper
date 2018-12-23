package com.example.hien.androidwallpaper;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hien.androidwallpaper.Adapter.FragmentAdapter;
import com.example.hien.androidwallpaper.Common.Common;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ViewPager viewPager;
    TabLayout tabLayout;
    FragmentAdapter fragmentAdapter;
    DrawerLayout drawer;
    NavigationView navigationView;
    View headerLayout;
    TextView txtEmail, txtUserName;
    Button btnLogout, btnSignin;
    ImageView imgAvatar;
    boolean checkOnPause = false;

    final String PREFS_NAME = "MyPrefsFile";
    SharedPreferences firstRun;

    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build());

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case Common.REQUEST_CODE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public static void changeTabsFont(TabLayout tabLayout, String fontName) {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    CalligraphyUtils.applyFontToTextView(tabLayout.getContext(), (TextView) tabViewChild, fontName);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Android Wallpaper");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPaper);
        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);

        changeTabsFont(tabLayout, this.getString(R.string.fontPath));

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.ic_menu_menu);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                   drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        headerLayout = navigationView.getHeaderView(0);
        txtEmail = headerLayout.findViewById(R.id.txtEmail);
        txtUserName = headerLayout.findViewById(R.id.txtUserName);
        imgAvatar = headerLayout.findViewById(R.id.imgAvatar);
        btnLogout = headerLayout.findViewById(R.id.btnLogout);
        btnSignin = headerLayout.findViewById(R.id.btnSignin);

        //Request permission
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Common.REQUEST_CODE);
            }
        }

        //check login status
        if(isLogin()){
            Snackbar.make(drawer, new StringBuilder("Welcome ").append(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString()), Snackbar.LENGTH_SHORT).show();
            getUserInfo();
        }

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).setTheme(R.style.FireBaseLoginTheme).build(),
                        Common.SIGN_IN_REQUEST_CODE);
            }
        });

        //check first run
        firstRun = getSharedPreferences(PREFS_NAME, 0);

        if (firstRun.getBoolean("first_run", true)) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).setTheme(R.style.FireBaseLoginTheme).build(),
                    Common.SIGN_IN_REQUEST_CODE);
            firstRun.edit().putBoolean("first_run", false).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Common.SIGN_IN_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Snackbar.make(drawer, new StringBuilder("Welcome ").append(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString()), Snackbar.LENGTH_SHORT).show();

                //Request permission
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Common.REQUEST_CODE);
                    }
                }

                tabLayout = findViewById(R.id.tabLayout);
                viewPager = findViewById(R.id.viewPaper);
                fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), this);
                viewPager.setAdapter(fragmentAdapter);
                tabLayout.setupWithViewPager(viewPager);

                getUserInfo();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkOnPause = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if((checkOnPause == true) && !isLogin()){
            setViewNotLogin();
        }else if ((checkOnPause == true) && isLogin()){
            setViewLogin();
        }
        checkOnPause = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem btnUpload = menu.findItem(R.id.action_upload);
        btnUpload.setVisible(isLogin());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_upload) {
            startActivity(new Intent(HomeActivity.this, UploadWallpaper.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_uploads) {
            Intent intent = new Intent(HomeActivity.this, MyUpload.class);
            startActivity(intent);
        } else if (id == R.id.nav_fav){
            Intent intent = new Intent(HomeActivity.this, MyFavorite.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this, "Not available", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share) {
            Toast.makeText(this, "Not available", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_send) {
            Toast.makeText(this, "Not available", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getUserInfo() {
        if(isLogin()) {
            setViewLogin();
            txtEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            txtUserName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            Picasso.get()
                    .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                    .into(imgAvatar);

            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                    builder.setTitle("");
                    builder.setMessage("Are you sure?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseAuth fAuth = FirebaseAuth.getInstance();
                            fAuth.signOut();
                            Toast.makeText(HomeActivity.this, "You have been successfully logged out", Toast.LENGTH_SHORT).show();
                            setViewNotLogin();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }
    }

    public boolean isLogin(){
        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            return false;
        else return true;
    }

    public void setViewLogin(){
        btnSignin.setVisibility(View.GONE);
        imgAvatar.setVisibility(View.VISIBLE);
        txtEmail.setVisibility(View.VISIBLE);
        txtUserName.setVisibility(View.VISIBLE);
        btnLogout.setVisibility(View.VISIBLE);
        navigationView.getMenu().findItem(R.id.nav_uploads).setVisible(true);
        navigationView.getMenu().findItem(R.id.nav_fav).setVisible(true);
        invalidateOptionsMenu();
    }

    public void setViewNotLogin(){
        btnSignin.setVisibility(View.VISIBLE);
        imgAvatar.setVisibility(View.GONE);
        txtEmail.setVisibility(View.GONE);
        txtUserName.setVisibility(View.GONE);
        btnLogout.setVisibility(View.GONE);
        navigationView.getMenu().findItem(R.id.nav_uploads).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_fav).setVisible(false);
        invalidateOptionsMenu();
    }
}
