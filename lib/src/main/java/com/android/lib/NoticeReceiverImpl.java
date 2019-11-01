package com.android.lib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

class NoticeReceiverImpl extends AbstractNoticeReceiver {

    private final ObjectAnimator mShowAnim;
    private final ObjectAnimator mHideAnim;

    private long mResidenceTime;

    public NoticeReceiverImpl() {
        super();
        mShowAnim = new ObjectAnimator();
        mShowAnim.setPropertyName("translationY");
        mShowAnim.setDuration(500);

        mHideAnim = new ObjectAnimator();
        mHideAnim.setPropertyName("translationY");
        mHideAnim.setDuration(300);
    }

    @Override
    public void accept(Activity activity, INotice notice) {
        if (null == notice || null == activity || !activity.getWindow().isActive() || activity.isFinishing()) {
            return;
        }

        mResidenceTime = notice.getResidenceTime();

        if (notice.getNoticeView() != null) {
            final int viewType = notice.getNoticeView().getViewType(notice);
            if (mNoticeViews.indexOfKey(viewType) < 0) {
                final View noticeView = notice.getNoticeView().createView(activity, notice, viewType);
                if (noticeView != null) {
                    noticeView.setVisibility(View.GONE);
                    activity.addContentView(noticeView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    if (notice.getNoticeViewListener() != null) {
                        notice.getNoticeViewListener().onViewCreated(noticeView, notice);
                    }

                    mNoticeViews.put(viewType, noticeView);

                    showNoticeView(noticeView);
                } else {
                    createDefaultView(activity, notice);
                }
            } else if (mNoticeViews.get(viewType).getVisibility() == View.GONE) {
                mNoticeViews.get(viewType).setVisibility(View.VISIBLE);
                if (notice.getNoticeViewListener() != null) {
                    notice.getNoticeViewListener().onViewCreated(mNoticeViews.get(viewType), notice);
                }
            } else {
                mCachedNotices.add(notice);
            }
        } else {
            createDefaultView(activity, notice);
        }
    }

    private void createDefaultView(Activity activity, INotice notice) {
        if (mNoticeViews.indexOfKey(0) < 0) {
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

            mNoticeViews.put(0, noticeView);

            showNoticeView(noticeView);
        } else if (mNoticeViews.get(0).getVisibility() == View.GONE) {
            mNoticeViews.get(0).setVisibility(View.VISIBLE);
            if (notice.getNoticeViewListener() != null) {
                notice.getNoticeViewListener().onViewCreated(mNoticeViews.get(0), notice);
            }
        } else {
            mCachedNotices.add(notice);
        }
    }

    void showNoticeView(final View view) {
        if (mShowAnim.isStarted() || mShowAnim.isRunning()) {
            return;
        }

        view.measure(View.MeasureSpec.makeMeasureSpec(mScreenWidth, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(mScreenHeight, View.MeasureSpec.UNSPECIFIED));
        final int height = view.getMeasuredHeight();
        mShowAnim.setTarget(view);
        mShowAnim.setFloatValues(-height, 0);
        mShowAnim.removeAllListeners();
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

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        mShowAnim.start();
    }

    void hideNoticeView(final View view) {
        if (mHideAnim.isStarted() || mHideAnim.isRunning()) {
            return;
        }

        final int height = view.getMeasuredHeight();
        mHideAnim.setTarget(view);
        mHideAnim.setFloatValues(0, -height);
        mHideAnim.setStartDelay(mResidenceTime);
        mHideAnim.removeAllListeners();
        mHideAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        mHideAnim.start();
    }

    @Override
    public void refuse(Activity activity) {
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
