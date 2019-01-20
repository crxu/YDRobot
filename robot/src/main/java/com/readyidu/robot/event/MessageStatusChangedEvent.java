package com.readyidu.robot.event;

/**
 * Created by gx on 2017/12/20.
 */
public class MessageStatusChangedEvent {
    public SearchXYBrainEvent event;
    public int status;

    public MessageStatusChangedEvent(SearchXYBrainEvent event, int status) {
        this.event = event;
        this.status = status;
    }
}