package com.android.lib;

import androidx.appcompat.app.AppCompatActivity;

public interface INoticeRegistry {

    void register(AppCompatActivity activity);

    void unregister(AppCompatActivity activity);
}
