package com.readyidu.robot.event;

import com.readyidu.robot.message.base.Message;

/**
 * Created by gx on 2017/11/17.
 */

public class BindEvent {
    public Message resendMessage;

    public BindEvent(Message resendMessage) {
        this.resendMessage = resendMessage;
    }
}
