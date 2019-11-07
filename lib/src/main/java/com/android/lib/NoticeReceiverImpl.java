package com.android.lib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

class NoticeReceiverImpl extends AbstractNoticeReceiver {

    private static NoticeReceiverImpl sInstance;

    private final ObjectAnimator mShowAnim;
    private final ObjectAnimator mHideAnim;

    public static NoticeReceiverImpl getInstance() {
        if (null == sInstance) {
            sInstance = new NoticeReceiverImpl();
        }

        return sInstance;
    }

    private NoticeReceiverImpl() {
        super();

        mShowAnim = new ObjectAnimator();
        mShowAnim.setPropertyName("translationY");
        mShowAnim.setDuration(500);

        mHideAnim = new ObjectAnimator();
        mHideAnim.setPropertyName("translationY");
        mHideAnim.setDuration(300);
    }

    @Override
    public void showNotice(Context context, Notice notice) {
        if (null == notice || null == context || !(context instanceof Activity)) {
            return;
        }

        Activity activity = (Activity) context;
        if (!activity.getWindow().isActive() || activity.isFinishing()) {
            return;
        }

        if (mShowing) {
            addNotice(notice);
            return;
        }

        mResidenceTime = notice.getResidenceTime();
        mShowing = true;

        final int viewType = notice.getViewType();
        View noticeView = null;
        if (mNoticeViews.indexOfKey(viewType) >= 0) {
            //缓存视图
            noticeView = mNoticeViews.get(viewType);
        }

        if (null == noticeView) {
            if (notice.getNoticeView() != null) {
                noticeView = notice.getNoticeView();
            } else if (notice.getNoticeViewLayoutId() != 0) {
                noticeView = LayoutInflater.from(activity).inflate(notice.getNoticeViewLayoutId(), null, false);
            } else {
                noticeView = LayoutInflater.from(activity).inflate(R.layout.layout_notice_view, null, false);
            }

            mNoticeViews.put(viewType, noticeView);

            noticeView.setVisibility(View.GONE);
            activity.addContentView(noticeView, new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) noticeView.getLayoutParams();
        if (lp.topMargin != notice.getTopMargin()) {
            lp.setMargins(0, notice.getTopMargin(), 0, 0);
        }

        if (0 == viewType) {
            //默认视图绑定值
            bindDefaultView(activity, noticeView, notice);
        }

        if (notice.getViewBinder() != null) {
            notice.getViewBinder().bindView(noticeView, notice);
        }

        setViewEvent(activity, noticeView);

        animateShow(activity, viewType);
    }

    private void bindDefaultView(Activity activity, View noticeView, Notice notice) {
        TextView title = noticeView.findViewById(R.id.tv_notice_title);
        setText(title, notice.getTitle());
        TextView content = noticeView.findViewById(R.id.tv_notice_content);
        setText(content, notice.getContent());
        ImageView imageView = noticeView.findViewById(R.id.iv_notice_icon);
        imageView.setImageResource(activity.getApplicationInfo().icon);
    }

    private void setViewEvent(final Activity activity, View view) {
        final int touchSlop = ViewConfiguration.get(activity).getScaledTouchSlop();
        view.setOnTouchListener(new View.OnTouchListener() {
            private float mLastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mLastY = event.getY();
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (mLastY - event.getY() > touchSlop) {
                        if (mHideAnim != null) {
                            mHideAnim.cancel();
                        }
                        animateHide(activity, v, true);
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onVisibilityChanged(Context context, boolean visible) {
        if (!visible) {
            hideNotice(context);
        }
    }

    void animateShow(final Activity activity, final int viewType) {
        if (mShowAnim.isStarted() || mShowAnim.isRunning()) {
            return;
        }

        final View view = mNoticeViews.get(viewType);
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
                showSameTypeNotice(activity, viewType);
            }
        });
        mShowAnim.start();
    }

    void animateHide(final Activity activity, final View view, final boolean dismiss) {
        if (mHideAnim.isStarted() || mHideAnim.isRunning()) {
            return;
        }

        final int height = view.getMeasuredHeight();
        mHideAnim.setTarget(view);
        mHideAnim.setFloatValues(0, -height);
        mHideAnim.setStartDelay(!dismiss ? mResidenceTime : 0);
        mHideAnim.removeAllListeners();
        mHideAnim.addListener(new AnimatorListenerAdapter() {
            private boolean mCanceled = false;

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!mCanceled) {
                    if (view != null) {
                        view.setVisibility(View.GONE);
                        view.setOnTouchListener(null);
                    }

                    mShowing = false;

                    if (!dismiss) {
                        showReadyNotice(activity);
                    } else {
                        dismiss(view);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCanceled = true;
            }
        });
        mHideAnim.start();
    }

    private void showSameTypeNotice(final Activity activity, final int viewType) {
        final Notice notice = getNotice(viewType);
        if (notice != null) {
            mResidenceTime = notice.getResidenceTime();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    View noticeView = mNoticeViews.get(viewType);
                    if (0 == viewType) {
                        bindDefaultView(activity, noticeView, notice);
                    }

                    if (notice.getViewBinder() != null) {
                        notice.getViewBinder().bindView(noticeView, notice);
                    }

                    showSameTypeNotice(activity, viewType);
                }
            }, 2000);
        } else {
            animateHide(activity, mNoticeViews.get(viewType), false);
        }
    }

    private void showReadyNotice(final Activity activity) {
        final Notice p = getNotice();
        if (p != null) {
            showNotice(activity, p);
        }
    }

    @Override
    public void hideNotice(Context context) {
        if (mShowAnim != null) {
            mShowAnim.removeAllListeners();
            if (mShowAnim.isStarted()) {
                mShowAnim.cancel();
            }
        }

        if (mHideAnim != null) {
            mHideAnim.removeAllListeners();
            if (mHideAnim.isStarted()) {
                mHideAnim.cancel();
            }
        }

        Activity activity = (Activity) context;
        ViewGroup parent = activity.findViewById(Window.ID_ANDROID_CONTENT);
        if (mNoticeViews != null && mNoticeViews.size() > 0) {
            View view;
            for (int i = 0; i < mNoticeViews.size(); i++) {
                view = mNoticeViews.valueAt(i);
                parent.removeViewInLayout(view);
            }
        }

        mNoticeViews.clear();
        dismiss(null);
    }
}
