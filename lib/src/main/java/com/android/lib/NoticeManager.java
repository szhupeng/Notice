package com.android.lib;

import android.app.Activity;

public final class NoticeManager {

    private static volatile NoticeManager sInstance;
    private final NoticeDispatcher mDispatcher;

    public static NoticeManager getInstance() {
        if (null == sInstance) {
            synchronized (NoticeManager.class) {
                if (null == sInstance) {
                    sInstance = new NoticeManager();
                }
            }
        }

        return sInstance;
    }

    private NoticeManager() {
        mDispatcher = new NoticeDispatcher();
    }

    /**
     * 注册站内信通知接收者，主要使用场景为Activity的onCreate方法或Fragment的onCreate方法中
     *
     * @param activity
     */
    public void register(Activity activity) {
        mDispatcher.addNoticeReceiver(activity);
    }

    /**
     * 注销站内信通知接收者，主要使用场景为Activity的onDestroy方法或Fragment的onDestroy方法中
     *
     * @param activity
     */
    public void unregister(Activity activity) {
        mDispatcher.removeNoticeReceiver(activity);
    }

    /**
     * 站内信通知接收者可见，主要用于Activity的onResume或者Fragment的onResume方法中
     *
     * @param activity
     */
    public void resume(Activity activity) {
        mDispatcher.setReceiverVisibility(activity, true);
    }

    /**
     * 站内信通知接收者不可见，主要用于Activity的onPause或者Fragment的onPause方法中
     *
     * @param activity
     */
    public void pause(Activity activity) {
        mDispatcher.setReceiverVisibility(activity, false);
    }

    /**
     * 发送站内信通知（使用默认通知接收者）
     *
     * @param notice
     */
    public void send(INotice notice) {
        send(notice, new NoticeReceiverFactory());
    }

    /**
     * 发送站内信通知
     *
     * @param notice
     * @param factory 自定义通知接收者工厂类
     */
    public void send(INotice notice, ReceiverFactory factory) {
        mDispatcher.dispatch(notice, factory);
    }
}
