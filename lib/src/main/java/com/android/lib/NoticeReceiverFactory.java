package com.android.lib;

public class NoticeReceiverFactory implements ReceiverFactory {
    @Override
    public INoticeReceiver create() {
        return new NoticeReceiverImpl();
    }
}
