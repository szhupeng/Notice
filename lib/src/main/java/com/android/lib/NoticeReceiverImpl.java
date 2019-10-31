package com.android.lib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

class NoticeReceiverImpl implements INoticeReceiver {

    private int mScreenWidth, mScreenHeight;

    private ObjectAnimator mShowAnim;
    private ObjectAnimator mHideAnim;

    private long mResidenceTime;

    private View mNoticeView;

    @Override
    public void accept(Activity activity, INotice notice) {
        if (null == notice || null == activity || !activity.getWindow().isActive() || activity.isFinishing()) {
            return;
        }

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
        mResidenceTime = notice.getResidenceTime();

        if (notice.getNoticeView() != null) {
            mNoticeView = notice.getNoticeView().getView(activity, notice);
            if (mNoticeView != null) {
                mNoticeView.setVisibility(View.GONE);
                activity.addContentView(mNoticeView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                if (notice.getNoticeViewListener() != null) {
                    notice.getNoticeViewListener().onViewCreated(mNoticeView, notice);
                }

                showNoticeView();
            } else {
                createDefaultLayout(activity, notice);
            }
        } else {
            createDefaultLayout(activity, notice);
        }
    }

    private void createDefaultLayout(Activity activity, INotice notice) {
        mNoticeView = LayoutInflater.from(activity).inflate(R.layout.layout_notice_view, null, false);
        TextView title = mNoticeView.findViewById(R.id.tv_notice_title);
        setText(title, notice.getTitle());
        TextView content = mNoticeView.findViewById(R.id.tv_notice_content);
        setText(content, notice.getContent());
        ImageView imageView = mNoticeView.findViewById(R.id.iv_notice_icon);
        imageView.setImageResource(activity.getApplicationInfo().icon);
        mNoticeView.setVisibility(View.GONE);
        activity.addContentView(mNoticeView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (notice.getNoticeViewListener() != null) {
            notice.getNoticeViewListener().onViewCreated(mNoticeView, notice);
        }

        showNoticeView();
    }

    public void setText(TextView textView, String text) {
        if (null == textView || TextUtils.isEmpty(text)) {
            return;
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        } else {
            textView.setText(Html.fromHtml(text));
        }
    }

    void showNoticeView() {
        mNoticeView.measure(View.MeasureSpec.makeMeasureSpec(mScreenWidth, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(mScreenHeight, View.MeasureSpec.UNSPECIFIED));
        final int height = mNoticeView.getMeasuredHeight();
        mShowAnim = ObjectAnimator.ofFloat(mNoticeView, "translationY", -height, 0);
        mShowAnim.setDuration(500);
        mShowAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (mNoticeView != null) {
                    mNoticeView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                hideNoticeView();
            }
        });
        mShowAnim.start();
    }

    void hideNoticeView() {
        final int height = mNoticeView.getMeasuredHeight();
        mHideAnim = ObjectAnimator.ofFloat(mNoticeView, "translationY", 0, -height);
        mHideAnim.setDuration(300);
        mHideAnim.setStartDelay(mResidenceTime);
        mHideAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mNoticeView != null && mNoticeView.getParent() != null) {
                    ((ViewGroup) mNoticeView.getParent()).removeView(mNoticeView);
                }
            }
        });
        mHideAnim.start();
    }

    @Override
    public void refuse() {
        if (mShowAnim != null && mShowAnim.isStarted()) {
            mShowAnim.cancel();
        }

        if (mHideAnim != null && mHideAnim.isStarted()) {
            mHideAnim.cancel();
        }
    }
}
