package com.readyidu.robot.event;

import com.readyidu.robot.message.base.Message;

/**
 * Created by gx on 2017/10/18.
 */
public class SearchXYBrainEvent {
    public String resultsV;
    public boolean isDirect;
    public Message resendMessage;

    public SearchXYBrainEvent(String resultsV, boolean isDirect) {
        this.resultsV = resultsV;
        this.isDirect = isDirect;
    }

    public SearchXYBrainEvent(String resultsV, boolean isDirect, Message resendMessage) {
        this.resultsV = resultsV;
        this.isDirect = isDirect;
        this.resendMessage = resendMessage;
    }

    public void setResendMessage(Message resendMessage) {
        this.resendMessage = resendMessage;
    }
}