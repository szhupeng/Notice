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

    private static NoticeReceiverImpl sInstance;

    private final ObjectAnimator mShowAnim;
    private final ObjectAnimator mHideAnim;

    private long mResidenceTime;
    private boolean mShowing;

    public static NoticeReceiverImpl getInstance() {
        if (null == sInstance) {
            sInstance = new NoticeReceiverImpl();
        }

        return sInstance;
    }

    private NoticeReceiverImpl() {
        super();
        mResidenceTime = 3 * 1000;

        mShowAnim = new ObjectAnimator();
        mShowAnim.setPropertyName("translationY");
        mShowAnim.setDuration(500);

        mHideAnim = new ObjectAnimator();
        mHideAnim.setPropertyName("translationY");
        mHideAnim.setDuration(300);
    }

    @Override
    public void showNotice(Activity activity, Notice notice) {
        if (null == notice || null == activity || !activity.getWindow().isActive() || activity.isFinishing()) {
            return;
        }

        if (mShowing) {
            addNotice(notice);
            return;
        }

        mResidenceTime = notice.getResidenceTime();
        mShowing = true;

        final INoticeView noticeView = notice.getNoticeView();
        if (null == noticeView) {
            //如果没有给出自定义通知视图，则使用默认通知视图
            createDefaultView(activity, notice);
            return;
        }

        final int viewType = noticeView.getViewType(notice);
        if (mNoticeViews.indexOfKey(viewType) < 0) {
            final View createdNoticeView = notice.getNoticeView().createView(activity, notice, viewType);
            if (createdNoticeView != null) {
                createdNoticeView.setVisibility(View.GONE);
                activity.addContentView(createdNoticeView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                if (notice.getNoticeViewListener() != null) {
                    notice.getNoticeViewListener().onViewCreated(createdNoticeView, notice);
                }

                mNoticeViews.put(viewType, createdNoticeView);

                showNoticeView(createdNoticeView);
            } else {
                createDefaultView(activity, notice);
            }
        } else if (mNoticeViews.get(viewType).getVisibility() == View.GONE) {
            mNoticeViews.get(viewType).setVisibility(View.VISIBLE);
            if (notice.getNoticeViewListener() != null) {
                notice.getNoticeViewListener().onViewCreated(mNoticeViews.get(viewType), notice);
            }
        }
    }

    private void createDefaultView(Activity activity, Notice notice) {
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
                mShowing = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        mHideAnim.start();
    }

    private boolean showReadyNotice(final int viewType) {
        final Notice notice = getNotice(viewType);
        if (notice != null) {
            mResidenceTime = notice.getResidenceTime();
        }
        hideNoticeView(mNoticeViews.get(viewType));
        return true;
    }

    @Override
    public void hideNotice(Activity activity) {
        if (mShowAnim != null && mShowAnim.isStarted()) {
            mShowAnim.cancel();
        }

        if (mHideAnim != null && mHideAnim.isStarted()) {
            mHideAnim.cancel();
        }

        mNoticeViews.clear();
        mReadyNotices = null;
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
