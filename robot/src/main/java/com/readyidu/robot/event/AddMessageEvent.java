package com.readyidu.robot.event;

import com.readyidu.robot.message.base.Message;

/**
 * Created by gx on 2017/10/20.
 */
public class AddMessageEvent {
    public Message message;

    public AddMessageEvent(Message message) {
        this.message = message;
    }
}