package com.android.lib;

public class WindowNoticeReceiverFactory implements ReceiverFactory {
    @Override
    public INoticeReceiver create() {
        return new WindowNoticeReceiverImpl();
    }
}
