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
import android.util.SparseArray;
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

    private SparseArray<View> mNoticeViews = new SparseArray<>(1);

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
            final int viewType = notice.getNoticeView().getViewType(notice);
            final View noticeView = notice.getNoticeView().createView(activity, notice, viewType);
            if (noticeView != null) {
                noticeView.setVisibility(View.GONE);
                activity.addContentView(noticeView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                if (notice.getNoticeViewListener() != null) {
                    notice.getNoticeViewListener().onViewCreated(noticeView, notice);
                }

                if (mNoticeViews.indexOfKey(viewType) < 0) {
                    mNoticeViews.put(viewType, noticeView);
                }

                showNoticeView(noticeView);
            } else {
                createDefaultView(activity, notice);
            }
        } else {
            createDefaultView(activity, notice);
        }
    }

    private void createDefaultView(Activity activity, INotice notice) {
        View noticeView = LayoutInflater.from(activity).inflate(R.layout.layout_notice_view, null, false);
        TextView title = noticeView.findViewById(R.id.tv_notice_title);
        setText(title, notice.getTitle());
        TextView content = noticeView.findViewById(R.id.tv_notice_content);
        setText(content, notice.getContent());
        ImageView imageView = noticeView.findViewById(R.id.iv_notice_icon);
        imageView.setImageResource(activity.getApplicationInfo().icon);
        noticeView.setVisibility(View.GONE);
        activity.addContentView(noticeView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (notice.getNoticeViewListener() != null) {
            notice.getNoticeViewListener().onViewCreated(noticeView, notice);
        }

        if (mNoticeViews.indexOfKey(0) < 0) {
            mNoticeViews.put(0, noticeView);
        }

        showNoticeView(noticeView);
    }

    void showNoticeView(final View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(mScreenWidth, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(mScreenHeight, View.MeasureSpec.UNSPECIFIED));
        final int height = view.getMeasuredHeight();
        mShowAnim = ObjectAnimator.ofFloat(view, "translationY", -height, 0);
        mShowAnim.setDuration(500);
        mShowAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                hideNoticeView(view);
            }
        });
        mShowAnim.start();
    }

    void hideNoticeView(final View view) {
        final int height = view.getMeasuredHeight();
        mHideAnim = ObjectAnimator.ofFloat(view, "translationY", 0, -height);
        mHideAnim.setDuration(300);
        mHideAnim.setStartDelay(mResidenceTime);
        mHideAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (view != null && view.getParent() != null) {
                    ((ViewGroup) view.getParent()).removeView(view);
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

    protected void setText(TextView textView, String text) {
        if (null == textView || TextUtils.isEmpty(text)) {
            return;
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        } else {
            textView.setText(Html.fromHtml(text));
        }
    }
}
