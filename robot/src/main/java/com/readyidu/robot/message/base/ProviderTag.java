package com.readyidu.robot.message.base;

/**
 * Created by gx on 2017/10/18.
 */
@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface ProviderTag {
    boolean showPortrait() default true;

    boolean centerInHorizontal() default false;

    boolean hide() default false;

    boolean showWarning() default true;

    boolean showProgress() default true;

    boolean showSummaryWithName() default true;

    boolean showReadState() default false;

    java.lang.Class<? extends BaseMessage> messageContent();
}
