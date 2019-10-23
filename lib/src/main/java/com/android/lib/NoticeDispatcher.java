package com.android.lib;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import java.lang.ref.WeakReference;

/**
 * 通知调度器
 */
public class NoticeDispatcher implements LifecycleObserver {

    private WeakReference<AppCompatActivity> mActivityRef;
    private final NoticeReceiver mReceiver;
    private final INoticeRegistry mNoticeRegistry;

    public NoticeDispatcher(AppCompatActivity activity, NoticeReceiver receiver, INoticeRegistry registry) {
        if (activity != null) {
            activity.getLifecycle().addObserver(this);
        }
        this.mActivityRef = new WeakReference<>(activity);
        this.mReceiver = receiver;
        this.mNoticeRegistry = registry;

        this.mNoticeRegistry.register(activity);
    }

    public void dispatch(INotice notice) {
        if (null == notice || null == mActivityRef || null == mActivityRef.get() || null == mReceiver) {
            return;
        }

        mReceiver.accept(mActivityRef.get(), notice);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStart() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStop() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy() {
        if (mActivityRef != null && mActivityRef.get() != null) {
            mActivityRef.get().getLifecycle().removeObserver(this);
            if (mNoticeRegistry != null) {
                mNoticeRegistry.unregister(mActivityRef.get());
            }
        }
    }
}
