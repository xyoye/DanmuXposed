package com.xyoye.danmuxposed.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xyoye.danmuxposed.R;
import com.xyoye.danmuxposed.bean.FolderChooserInfo;
import com.xyoye.danmuxposed.ui.activities.FolderChooserActivity;

import java.util.List;

public class FolderChooserAdapter extends RecyclerView.Adapter<FolderChooserAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<FolderChooserInfo> mData;
    private FolderChooserActivity.ItemClickCallback callback;

    public FolderChooserAdapter(Context mContext, List<FolderChooserInfo> mData, FolderChooserActivity.ItemClickCallback callback) {
        this.mContext = mContext;
        this.mData = mData;
        this.callback = callback;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_folder_chooser, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final FolderChooserInfo info = mData.get(position);
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
            image = (ImageView) view.findViewById(R.id.image);
            name = (TextView) view.findViewById(R.id.name);
            v = (LinearLayout) view.findViewById(R.id.view);
        }
    }
}
