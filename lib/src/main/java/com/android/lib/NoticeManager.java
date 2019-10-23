package com.android.lib;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class NoticeManager implements INoticeRegistry {

    private final List<INoticeView> mNoticeList = new ArrayList<>();

    @Override
    public void register(AppCompatActivity activity) {

    }

    @Override
    public void unregister(AppCompatActivity activity) {

    }

    public void dispatch(INotice notice) {
        NoticeDispatcher dispatcher = new NoticeDispatcher();
        dispatcher.dispatch(notice);
    }
}
