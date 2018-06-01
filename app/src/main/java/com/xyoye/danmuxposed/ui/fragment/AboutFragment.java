package com.xyoye.danmuxposed.ui.fragment;

import android.app.Dialog;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xyoye.danmuxposed.R;

import butterknife.BindView;

/**
 * Created by YE on 2018/5/30.
 */


public class AboutFragment extends BaseFragment {
    public static final int READ_ABOUT = 0;
    public static final int READ_INTRO = 1;

    @BindView(R.id.about_text)
    TextView aboutText;
    @BindView(R.id.donation)
    TextView donation;

    private boolean donation_type = true;
    private Dialog dialog;

    public static AboutFragment newInstance(){
        return new AboutFragment();
    }

    @Override
    protected int initLayoutId() {
        return R.layout.fragment_about;
    }

    public void setReadType(int type){
        switch (type){
            case READ_INTRO:
                donation.setVisibility(View.GONE);
                aboutText.setText(getResources().getString(R.string.about_use));
                break;
            case READ_ABOUT:
                donation.setVisibility(View.VISIBLE);
                aboutText.setText(getResources().getString(R.string.about_model));
                break;
        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        donation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder_donation = new AlertDialog.Builder(getContext());
                View dialogView = View.inflate(getContext(),R.layout.dialog_donation,null);
                final ImageView donationIv = dialogView.findViewById(R.id.donation_image);
                donationIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (donation_type){
                            donationIv.setImageResource(R.mipmap.wechat);
                            donation_type=false;
                        }else {
                            donationIv.setImageResource(R.mipmap.alipay);
                            donation_type=true;
                        }
                    }
                });
                donationIv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        dialog.cancel();
                        return false;
                    }
                });
                builder_donation.setView(dialogView);
                dialog = builder_donation.show();
            }
        });
    }
}
