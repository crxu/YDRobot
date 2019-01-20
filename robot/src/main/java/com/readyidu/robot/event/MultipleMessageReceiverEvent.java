package com.readyidu.robot.event;

import com.readyidu.robot.message.base.Message;

/**
 * Created by gx on 2017/12/21.
 */
public class MultipleMessageReceiverEvent {
    public Message message;

    public MultipleMessageReceiverEvent(Message message) {
        this.message = message;
    }
}