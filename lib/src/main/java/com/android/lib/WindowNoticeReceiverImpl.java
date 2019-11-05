package com.android.lib;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

class WindowNoticeReceiverImpl extends AbstractNoticeReceiver {

    private static WindowNoticeReceiverImpl sInstance;

    public static WindowNoticeReceiverImpl getInstance() {
        if (null == sInstance) {
            sInstance = new WindowNoticeReceiverImpl();
        }

        return sInstance;
    }

    private WindowNoticeReceiverImpl() {
        super();
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

            WindowManager manager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.gravity = Gravity.CENTER | Gravity.TOP;
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.windowAnimations = R.style.NoticeWindowAnimation;
            manager.addView(noticeView, params);
        }

        if (0 == viewType) {
            //默认视图绑定值
            bindDefaultView(activity, noticeView, notice);
        }

        if (notice.getViewBinder() != null) {
            notice.getViewBinder().bindView(noticeView, notice);
        }

        setViewEvent(activity, noticeView);

        showSameTypeNotice(activity, viewType);
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
                        v.setOnTouchListener(null);
                        WindowManager manager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
                        manager.removeView(v);
                    }
                }
                return true;
            }
        });
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
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    WindowManager manager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
                    manager.removeView(mNoticeViews.get(viewType));

                    mShowing = false;

                    showReadyNotice(activity);
                }
            }, mResidenceTime);
        }
    }

    private void showReadyNotice(final Activity activity) {
        final Notice p = getNotice();
        if (p != null) {
            showNotice(activity, p);
        }
    }

    @Override
    public void hideNotice(Activity activity) {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
