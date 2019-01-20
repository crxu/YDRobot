package com.readyidu.robot.component.router.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 语音识别结果
 */
public class SpeechInfo {

    public int xFtype;
    public String srcText;//原始文本
    public String speechText;//语音播报的话
    public String keyword;//关键字
    public int goRouterId;
    public String goRouter = "";
    public List<String> tvList = new ArrayList<>();

    public SpeechInfo() {
    }

    public SpeechInfo(String content) {
        this.xFtype = -1;//只播报语音
        this.speechText = content;
    }

}
