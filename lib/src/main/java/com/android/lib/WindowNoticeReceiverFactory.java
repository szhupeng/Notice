package com.android.lib;

public class WindowNoticeReceiverFactory implements ReceiverFactory {

    @Override
    public AbstractNoticeReceiver create() {
        return WindowNoticeReceiverImpl.getInstance();
    }
}
