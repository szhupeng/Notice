package com.android.lib;

import android.app.Activity;

import androidx.annotation.NonNull;

interface IReceiverObservable {
    void addNoticeReceiver(@NonNull Activity activity);

    void removeNoticeReceiver(@NonNull Activity activity);

    void setReceiverVisibility(@NonNull Activity activity, boolean visible);
}
