package com.xyoye.danmuxposed.ui.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xyoye.danmuxposed.R;
import com.xyoye.danmuxposed.bean.SmbInfo;
import com.xyoye.danmuxposed.ui.activities.SmbActivity;

import java.util.List;

public class SmbAdapter extends RecyclerView.Adapter<SmbAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<SmbInfo> mData;
    private SmbActivity.ItemClickCallback callback;

    public SmbAdapter(Context mContext, List<SmbInfo> mData, SmbActivity.ItemClickCallback callback) {
        this.mData = mData;
        this.callback = callback;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_folder_chooser, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final SmbInfo info = mData.get(position);
        holder.name.setText(info.getName() == null ? "" : info.getName());

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(v, position, info);
            }
        });
        holder.image.setImageResource(info.getImage());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;
        LinearLayout v;

        ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            name = view.findViewById(R.id.name);
            v = view.findViewById(R.id.view);
        }
    }
}
