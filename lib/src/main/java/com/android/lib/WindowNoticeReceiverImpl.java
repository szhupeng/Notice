package com.android.lib;

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
    public void showNotice(Context context, Notice notice) {
        if (null == notice || null == context) {
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
                noticeView = LayoutInflater.from(context).inflate(notice.getNoticeViewLayoutId(), null, false);
            } else {
                noticeView = LayoutInflater.from(context).inflate(R.layout.layout_notice_view, null, false);
            }

            mNoticeViews.put(viewType, noticeView);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
            return;
        }

        mParams.y = notice.getTopMargin();
        getWindowManager(context).addView(noticeView, mParams);

        if (0 == viewType) {
            //默认视图绑定值
            bindDefaultView(context, noticeView, notice);
        }

        if (notice.getViewBinder() != null) {
            notice.getViewBinder().bindView(noticeView, notice);
        }

        setViewEvent(context.getApplicationContext(), noticeView);

        showSameTypeNotice(context.getApplicationContext(), viewType);
    }

    private void bindDefaultView(Context context, View noticeView, Notice notice) {
        TextView title = noticeView.findViewById(R.id.tv_notice_title);
        setText(title, notice.getTitle());
        TextView content = noticeView.findViewById(R.id.tv_notice_content);
        setText(content, notice.getContent());
        ImageView imageView = noticeView.findViewById(R.id.iv_notice_icon);
        imageView.setImageResource(context.getApplicationInfo().icon);
    }

    private void setViewEvent(final Context context, View view) {
        final int touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        view.setOnTouchListener(new View.OnTouchListener() {
            private float mLastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mLastY = event.getY();
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (mLastY - event.getY() > touchSlop) {
                        dismiss(v);
                        getWindowManager(context).removeViewImmediate(v);
                    }
                }
                return true;
            }
        });
    }

    private void showSameTypeNotice(final Context context, final int viewType) {
        final Notice notice = getNotice(viewType);
        if (notice != null) {
            mResidenceTime = notice.getResidenceTime();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    View noticeView = mNoticeViews.get(viewType);
                    if (0 == viewType) {
                        bindDefaultView(context, noticeView, notice);
                    }

                    if (notice.getViewBinder() != null) {
                        notice.getViewBinder().bindView(noticeView, notice);
                    }

                    getWindowManager(context).updateViewLayout(mNoticeViews.get(viewType), mParams);

                    showSameTypeNotice(context, viewType);
                }
            }, 2000);
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getWindowManager(context).removeViewImmediate(mNoticeViews.get(viewType));

                    mShowing = false;

                    showReadyNotice(context);
                }
            }, mResidenceTime);
        }
    }

    private void showReadyNotice(final Context context) {
        final Notice p = getNotice();
        if (p != null) {
            showNotice(context, p);
        }
    }

    @Override
    public void hideNotice(Context context) {
    }

    protected final WindowManager getWindowManager(Context context) {
        if (null == mWindowManager) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }

        return mWindowManager;
    }
}
