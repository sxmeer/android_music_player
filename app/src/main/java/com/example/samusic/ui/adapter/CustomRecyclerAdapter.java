package com.example.samusic.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.samusic.R;
import com.example.samusic.entity.MusicRecord;
import com.example.samusic.ui.listener.OnMusicRecordClickListener;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.CustomViewHolder> {
    private List<MusicRecord> data;
    /*private int shimmerCount = 15;
    private boolean isShimmer = true;*/
    private OnMusicRecordClickListener onMusicRecordClickListener;
    private MusicRecord currentPlaying;

    public CustomRecyclerAdapter(List<MusicRecord> data, OnMusicRecordClickListener onMusicRecordClickListener) {
        this.data = data;
        this.onMusicRecordClickListener = onMusicRecordClickListener;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, final int position) {
        /*if (isShimmer) {
            holder.shimmerFL.startShimmer();
            holder.musicItemCV.setOnClickListener(null);
        } else */{
            /*holder.shimmerFL.stopShimmer();
            holder.shimmerFL.setShimmer(null);*/
            /*holder.msgTV.setBackground(null);*/
            holder.msgTV.setText(data.get(position).getName());
            /*holder.duration.setBackground(null);*/
            holder.duration.setText(data.get(position).getDurationString());
            if(currentPlaying!=null){
                if(data.get(position).getName().equals(currentPlaying.getName())){
                    holder.playGIV.setVisibility(View.VISIBLE);
                }else{
                    holder.playGIV.setVisibility(View.GONE);
                }
            }else{
                holder.playGIV.setVisibility(View.GONE);
            }
            holder.musicItemCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onMusicRecordClickListener != null) {
                        onMusicRecordClickListener.onClick(view, data.get(position), position);
                    }
                }
            });
        }
    }

    public void setCurrentPlaying(MusicRecord currentPlaying){
        if(currentPlaying!=null){
            this.currentPlaying = currentPlaying;
            notifyDataSetChanged();
        }
    }

    public void refreshData(List<MusicRecord> data) {
        if (data != null) {
            /*isShimmer = false;*/
            this.data.clear();
            this.data.addAll(data);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return /*isShimmer ? shimmerCount :*/ data.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        /*@BindView(R.id.shimmerFL)
        ShimmerFrameLayout shimmerFL;*/
        @BindView(R.id.msgTV)
        TextView msgTV;
        @BindView(R.id.duration)
        TextView duration;
        @BindView(R.id.musicItemCV)
        MaterialCardView musicItemCV;
        @BindView(R.id.playGIV)
        ImageView playGIV;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
