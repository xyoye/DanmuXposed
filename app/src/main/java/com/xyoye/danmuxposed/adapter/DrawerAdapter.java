package com.xyoye.danmuxposed.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xyoye.danmuxposed.R;

import java.util.List;

/**
 * Created by xyy on 2018-03-22 下午 2:21
 */


public class DrawerAdapter extends BaseAdapter {
    private List<Integer> image;
    private List<String> text;
    private Context context;

    public DrawerAdapter(List<String> text, List<Integer> image, Context context) {
        this.text = text;
        this.image = image;
        this.context = context;
    }

    @Override
    public int getCount() {
        return text.size();
    }

    @Override
    public Object getItem(int position) {
        return text.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView != null){
            view = convertView;
        }else {
            view = View.inflate(context, R.layout.item_drawer, null);
        }

        ImageView imageView = view.findViewById(R.id.image);
        TextView textView = view.findViewById(R.id.text);

        if(image.size() > position)
            imageView.setImageResource(image.get(position));
        textView.setText(text.get(position));

        if (image.get(position) == 0)
            view.setVisibility(View.INVISIBLE);

        return view;
    }
}
