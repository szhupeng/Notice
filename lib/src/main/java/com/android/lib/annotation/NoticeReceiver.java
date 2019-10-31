package com.android.lib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 只要Activity或者Fragment带有本注解，就能接收到站内信
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface NoticeReceiver {
    String tag();
}
