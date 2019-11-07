package com.android.lib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
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

    private final WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;

    public static WindowNoticeReceiverImpl getInstance() {
        if (null == sInstance) {
            sInstance = new WindowNoticeReceiverImpl();
        }

        return sInstance;
    }

    private WindowNoticeReceiverImpl() {
        super();

        mParams = new WindowManager.LayoutParams();
        mParams.gravity = Gravity.CENTER | Gravity.TOP;
        mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.format = PixelFormat.TRANSPARENT;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        mParams.windowAnimations = R.style.NoticeWindowAnimation;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
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

            noticeView.setFitsSystemWindows(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(activity)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, 1);
            return;
        }

        mParams.y = 40;
        getWindowManager(activity).addView(noticeView, mParams);

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
                        getWindowManager(activity).removeView(v);
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

                    getWindowManager(activity).updateViewLayout(mNoticeViews.get(viewType), mParams);

                    showSameTypeNotice(activity, viewType);
                }
            }, 2000);
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getWindowManager(activity).removeView(mNoticeViews.get(viewType));

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

    protected final WindowManager getWindowManager(Activity activity) {
        if (null == mWindowManager) {
            mWindowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        }

        return mWindowManager;
    }
}
