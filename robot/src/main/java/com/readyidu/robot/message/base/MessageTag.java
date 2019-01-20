package com.readyidu.robot.message.base;

/**
 * Created by gx on 2017/10/18.
 */
@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface MessageTag {
    int NONE = 0;
    int ISPERSISTED = 1;
    int ISCOUNTED = 3;
    int STATUS = 16;

    java.lang.String value();

    int flag() default 0;
}