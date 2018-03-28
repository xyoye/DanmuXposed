package com.xyoye.danmuxposed.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;

/**
 *
 * 动画类
 */
public class Animation {
    private AlphaAnimation mHideAnimation;
    private AlphaAnimation mShowAnimation;
    /**
     * 位移动画
     */
    public static void translateAnimation(View view, float xFrom, float xTo,
                                          float yFrom, float yTo, long duration) {

        TranslateAnimation translateAnimation = new TranslateAnimation(
                android.view.animation.Animation.RELATIVE_TO_SELF, xFrom, android.view.animation.Animation.RELATIVE_TO_SELF, xTo,
                android.view.animation.Animation.RELATIVE_TO_SELF, yFrom, android.view.animation.Animation.RELATIVE_TO_SELF, yTo);
        translateAnimation.setFillAfter(false);
        translateAnimation.setDuration(duration);
        view.startAnimation(translateAnimation);
        translateAnimation.startNow();
    }

    public  void setHideAnimation( View view)
    {
        if (null == view )
        {
            return;
        }

        if (null != mHideAnimation)
        {
            mHideAnimation.cancel();
        }
        // 监听动画结束的操作
        mHideAnimation = new AlphaAnimation(1.0f, 0.1f);
        mHideAnimation.setDuration(1000);
        mHideAnimation.setFillAfter(true);
        view.startAnimation(mHideAnimation);
        mHideAnimation = null;
    }

    public  void setShowAnimation( View view)
    {
        if (null == view)
        {
            return;
        }
        if (null != mShowAnimation)
        {
            mShowAnimation.cancel();
        }
        mShowAnimation = new AlphaAnimation(0.1f, 1.0f);
        mShowAnimation.setDuration(500);
        mShowAnimation.setFillAfter(true);
        view.startAnimation(mShowAnimation);
        mShowAnimation = null;
    }
}
