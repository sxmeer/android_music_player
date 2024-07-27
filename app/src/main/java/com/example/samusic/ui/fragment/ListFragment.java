package com.example.samusic.ui.fragment;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.samusic.PlayerViewModel;
import com.example.samusic.ui.adapter.CustomRecyclerAdapter;
import com.example.samusic.R;
import com.example.samusic.entity.MusicRecord;
import com.example.samusic.ui.activity.MusicActivity;
import com.example.samusic.ui.listener.OnMusicRecordClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ListFragment extends Fragment {
    @BindView(R.id.listRV)
    RecyclerView listRV;
    private CustomRecyclerAdapter customRecyclerAdapter;
    private PlayerViewModel playerViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        playerViewModel = ViewModelProviders.of(getActivity()).get(PlayerViewModel.class);
        initRecylerView();
        callable();
    }

    private void initRecylerView() {
        listRV.setHasFixedSize(true);
        listRV.setLayoutManager(new LinearLayoutManager(getContext()));
        customRecyclerAdapter = new CustomRecyclerAdapter(new ArrayList<MusicRecord>(), new OnMusicRecordClickListener() {
            @Override
            public void onClick(View view, MusicRecord musicRecord, int pos) {
                ((MusicActivity)getActivity()).setMusicRecord(musicRecord);
            }
        });
        listRV.setAdapter(customRecyclerAdapter);
    }

    public void updateRecyclerView(MusicRecord musicRecord){
        customRecyclerAdapter.setCurrentPlaying(musicRecord);
    }

    private List<MusicRecord> getMusic() {
        List<MusicRecord> musicRecords = new ArrayList<>();
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri songsUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(songsUri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do{
                MusicRecord musicRecord = new MusicRecord();
                musicRecord.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
                musicRecord.setLocation(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                musicRecord.setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                musicRecord.setDurationString(musicRecord.getDuration());
                musicRecords.add(musicRecord);
            }while (cursor.moveToNext());
        }
        cursor.close();
        cursor = null;
        return musicRecords;
    }

    private void callable(){
        Single.just(getMusic()).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<List<MusicRecord>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<MusicRecord> musicRecords) {
                        playerViewModel.setMusicRecords(musicRecords);
                        customRecyclerAdapter.refreshData(musicRecords);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }
}
