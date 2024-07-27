package com.example.samusic.ui.activity;

import android.Manifest;
import android.app.Notification;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.example.samusic.PlayerViewModel;
import com.example.samusic.ui.fragment.ListFragment;
import com.example.samusic.entity.MusicRecord;
import com.example.samusic.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.samusic.base.MusicApplication.CHANNEL_ID;

public class MusicActivity extends AppCompatActivity {
    @BindView(R.id.containerFL)
    FrameLayout containerFL;

    private MusicRecord musicRecord;
    @BindView(R.id.songNameTV)
    TextView songNameTV;
    @BindView(R.id.songArtistTV)
    TextView songArtistTV;
    @BindView(R.id.currentTimeTV)
    TextView currentTimeTV;
    @BindView(R.id.totalTimeTV)
    TextView totalTimeTV;
    @BindView(R.id.favIV)
    ImageView favIV;
    @BindView(R.id.playIV)
    ImageView playIV;
    @BindView(R.id.playingSongTV)
    TextView playingSongIV;
    @BindView(R.id.songProgressSB)
    SeekBar songProgressSK;
    @BindView(R.id.bottomSheetCL)
    ConstraintLayout bottomSheetCL;
    private BottomSheetBehavior bottomSheetBehavior;
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private NotificationManagerCompat notificationManagerCompat;
    private PlayerViewModel playerViewModel;
    private ListFragment listFragment;


    public static final int MY_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        ButterKnife.bind(this);
        playerViewModel = ViewModelProviders.of(this).get(PlayerViewModel.class);
        handler = new Handler();
        notificationManagerCompat = NotificationManagerCompat.from(this);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetCL);
        checkPermission();
    }

    private void viewListFragment() {
        listFragment = new ListFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.containerFL, listFragment).commit();
    }

    public void setMusicRecord(MusicRecord musicRecord) {
        playerViewModel.setCurrentFromMusicRecord(musicRecord);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        playMusic();
    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_REQUEST_CODE);
        } else {
            viewListFragment();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewListFragment();
                } else {

                }
        }
    }

    private void playMusic() {
        this.musicRecord = playerViewModel.getCurrentMusic();
        if (this.musicRecord != null) {
            sendNotification();
            initView();
            listFragment.updateRecyclerView(musicRecord);
            playIV.setImageResource(R.drawable.ic_pause);
            playIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    togglePlay();
                }
            });
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(this, Uri.parse(musicRecord.getLocation()));
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    songProgressSK.setMax(mediaPlayer.getDuration());
                    mediaPlayer.start();
                    handler.post(runnable);
                }
            });
            songProgressSK.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        mediaPlayer.seekTo(progress);
                        setCurrentTimeTV(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    playNextMusic();
                }
            });
        }
    }

    private void setCurrentTimeTV(int progress) {
        if (currentTimeTV != null) {
            currentTimeTV.setText(MusicRecord.getTime(progress));
            songProgressSK.setProgress(progress);
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null) {
                setCurrentTimeTV(mediaPlayer.getCurrentPosition());
            }
            if (mediaPlayer.isPlaying())
                handler.postDelayed(this, 1000);
            else
                handler.removeCallbacks(this);
        }
    };

    private void initView() {
        songNameTV.setText(musicRecord.getName());
        totalTimeTV.setText(musicRecord.getDurationString());
        songArtistTV.setText("Artist");
        favIV.setImageResource(musicRecord.isFavourite()?R.drawable.ic_heart_filled:R.drawable.ic_heart);
    }

    @OnClick({R.id.favIV, R.id.prevIV, R.id.nextIV})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.favIV:
                if(musicRecord!=null){
                    musicRecord.setFavourite(!musicRecord.isFavourite());
                    favIV.setImageResource(musicRecord.isFavourite()?R.drawable.ic_heart_filled:R.drawable.ic_heart);
                }
                break;
            case R.id.prevIV:
                if(musicRecord!=null){
                    playPreviousMusic();
                }
                break;
            case R.id.nextIV:
                if(musicRecord!=null){
                    playNextMusic();
                }
                break;
        }
    }

    private void togglePlay() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playIV.setImageResource(R.drawable.ic_play);
            } else {
                mediaPlayer.start();
                handler.post(runnable);
                playIV.setImageResource(R.drawable.ic_pause);
            }
        }
    }

    private void playNextMusic(){
        if(playerViewModel.hasNextMusic()){
            playerViewModel.setNextMusic();
            playMusic();
        }else{
            playerViewModel.setCurrent(0);
            playMusic();
        }

    }

    private void playPreviousMusic(){
        if(playerViewModel.hasPreviousMusic()){
            playerViewModel.setPreviousMusic();
            playMusic();
        }else{
            setCurrentTimeTV(0);
            mediaPlayer.seekTo(0);
        }
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            releaseResources();
            super.onBackPressed();
        }
    }

    private void sendNotification(){
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setContentTitle(musicRecord.getName())
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_music)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                .setOnlyAlertOnce(true)
                .build();
        notificationManagerCompat.notify(1, notification);
    }

    private void releaseResources(){
        handler.removeCallbacks(runnable);
        notificationManagerCompat.cancelAll();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    @Override
    protected void onDestroy() {
        releaseResources();
        super.onDestroy();
    }
}