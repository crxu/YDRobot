package com.readyidu.api.response;

import java.util.List;

/**
 * Created by Jackshao on 2018/6/11.
 */

public class LotteryContent {

  private List<ResultBean> result;

  public List<ResultBean> getResult() {
    return result;
  }

  public void setResult(List<ResultBean> result) {
    this.result = result;
  }

  public static class ResultBean {
    /**
     * number : 09,16,17,19,22,26|10
     * sale : 846907070
     * issue : 18066
     * name : 双色球
     * source : iflytek
     * deadline : 2018-08-09
     * openDate : 2018-06-10
     */

    private String number;
    private String sale;
    private String issue;
    private String name;
    private String source;
    private String deadline;
    private String openDate;

    public String getNumber() {
      return number;
    }

    public void setNumber(String number) {
      this.number = number;
    }

    public String getSale() {
      return sale;
    }

    public void setSale(String sale) {
      this.sale = sale;
    }

    public String getIssue() {
      return issue;
    }

    public void setIssue(String issue) {
      this.issue = issue;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getSource() {
      return source;
    }

    public void setSource(String source) {
      this.source = source;
    }

    public String getDeadline() {
      return deadline;
    }

    public void setDeadline(String deadline) {
      this.deadline = deadline;
    }

    public String getOpenDate() {
      return openDate;
    }

    public void setOpenDate(String openDate) {
      this.openDate = openDate;
    }
  }
}
