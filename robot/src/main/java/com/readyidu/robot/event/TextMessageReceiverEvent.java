package com.readyidu.robot.event;

/**
 * Created by gx on 2017/12/21.
 */
public class TextMessageReceiverEvent {
    public String content;

    public TextMessageReceiverEvent(String content) {
        this.content = content;
    }
}