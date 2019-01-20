package com.readyidu.robot.event;

import com.readyidu.robot.message.base.Message;

/**
 * Created by gx on 2017/10/13.
 */
public class DeleteMessageEvent {
    public Message baseMessage;
    public int id;

    public DeleteMessageEvent(Message baseMessage, int id) {
        this.baseMessage = baseMessage;
        this.id = id;
    }

    public DeleteMessageEvent(Message baseMessage) {
        this.baseMessage = baseMessage;
    }
}