package com.android.lib;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import java.lang.ref.WeakReference;

/**
 * 通知调度器
 */
class NoticeDispatcher implements LifecycleObserver, IReceiverObservable {

    private AbstractNoticeReceiver mReceiver;
    private WeakReference<Activity> mCurrentActivityRef;

    @Override
    public void addNoticeReceiver(@NonNull Activity activity) {
        if (activity != null && activity instanceof LifecycleOwner) {
            ((LifecycleOwner) activity).getLifecycle().addObserver(this);
        }
    }

    @Override
    public void removeNoticeReceiver(@NonNull Activity activity) {
        if (mReceiver != null) {
            mReceiver.hideNotice(activity);
        }

        if (activity != null && activity instanceof LifecycleOwner) {
            ((LifecycleOwner) activity).getLifecycle().removeObserver(this);
        }
    }

    @Override
    public void setReceiverVisibility(@NonNull Activity activity, boolean visible) {
        if (visible) {
            mCurrentActivityRef = new WeakReference<>(activity);
        } else {
            mCurrentActivityRef.clear();
        }
    }

    public void dispatch(final INotice notice, final ReceiverFactory factory) {
        if (null == mCurrentActivityRef || null == mCurrentActivityRef.get()) {
            return;
        }

        if (factory != null) {
            mReceiver = factory.create();
        }

        if (mReceiver != null) {
            mReceiver.showNotice(mCurrentActivityRef.get(), notice);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume(@NonNull LifecycleOwner owner) {
        if (owner != null && owner instanceof Activity) {
            setReceiverVisibility((Activity) owner, true);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause(@NonNull LifecycleOwner owner) {
        if (owner != null && owner instanceof Activity) {
            setReceiverVisibility((Activity) owner, false);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy(@NonNull LifecycleOwner owner) {
        if (owner != null && owner instanceof Activity) {
            removeNoticeReceiver((Activity) owner);
        }
    }
}
