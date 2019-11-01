package com.android.lib;

public class NoticeReceiverFactory implements ReceiverFactory {
    @Override
    public AbstractNoticeReceiver create() {
        return new NoticeReceiverImpl();
    }
}
