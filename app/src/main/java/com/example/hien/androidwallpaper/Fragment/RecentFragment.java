package com.example.hien.androidwallpaper.Fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hien.androidwallpaper.Adapter.RecyclerViewAdapter;
import com.example.hien.androidwallpaper.Database.DataSource.RecentRepository;
import com.example.hien.androidwallpaper.Database.LocalDatabase.LocalDatabase;
import com.example.hien.androidwallpaper.Database.LocalDatabase.RecentsDataSource;
import com.example.hien.androidwallpaper.Database.Recent;
import com.example.hien.androidwallpaper.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class RecentFragment extends Fragment {

    private static RecentFragment INSTANCE = null;

    RecyclerView recyclerView;
    List<Recent> recentsList;
    RecyclerViewAdapter recyclerViewAdapter;
    Context context;

    //Room Database
    CompositeDisposable compositeDisposable;
    RecentRepository recentRepository;

    @SuppressLint("ValidFragment")
    public RecentFragment(Context context) {
        // Required empty public constructor
        this.context = context;

        // Init Room Database
        compositeDisposable = new CompositeDisposable();
        LocalDatabase database = LocalDatabase.getInstance(context);
        recentRepository = RecentRepository.getInstance(RecentsDataSource.getInstance(database.recentsDAO()));
    }

    public static RecentFragment getInstance(Context context){
        if (INSTANCE == null)
            INSTANCE = new RecentFragment(context);
        return INSTANCE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recents, container, false);
        recentsList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_recents);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(context, recentsList);
        recyclerView.setAdapter(recyclerViewAdapter);

        loadRecentsList();

        return view;
    }

    private void loadRecentsList() {
        Disposable disposable = recentRepository.getAllRencents()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Recent>>() {
                    @Override
                    public void accept(List<Recent> recents) throws Exception {
                        onGetAllRecentsSuccess(recents);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("ERROR", throwable.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void onGetAllRecentsSuccess(List<Recent> recents) {
        recentsList.clear();
        recentsList.addAll(recents);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
