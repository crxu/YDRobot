package com.readyidu.xylottery;

/**
 * File: MessageEvent.java
 * Author: Administrator
 * Version:1.0.2
 * Create: 2018/9/6 0006 14:59
 * describe Evenbus发送消息
 */

public class MessageEvent {
    private String message;
    public  MessageEvent(String message){
        this.message=message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
