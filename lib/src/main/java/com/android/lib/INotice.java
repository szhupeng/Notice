package com.android.lib;

public interface INotice extends INoticeView {
    /** 获取图标链接 */
    String getIconUrl();

    /** 获取标题 */
    CharSequence getTitle();

    /** 获取内容 */
    CharSequence getContent();
}
