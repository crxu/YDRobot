package com.readyidu.api.response;

import java.util.List;

/**
 * 参数信息
 */
public class LotteryInfo {


    /**
     * data : {"arg":{"result":[{"number":"09,16,17,19,22,26|10","sale":"846907070","issue":"18066","name":"双色球","source":"iflytek","openDate":"2018-06-10","deadline":"2018-08-09"},{"number":"01,04,06,14,28,33|01","sale":"847267386","issue":"18065","name":"双色球","source":"iflytek","openDate":"2018-06-07","deadline":"2018-08-06"},{"number":"02,05,10,11,17,21|05","sale":"821423304","issue":"18064","name":"双色球","source":"iflytek","openDate":"2018-06-05","deadline":"2018-08-04"},{"number":"05,06,10,16,20,25|12","sale":"841729671","issue":"18063","name":"双色球","source":"iflytek","openDate":"2018-06-03","deadline":"2018-08-02"},{"number":"02,18,19,24,25,33|11","sale":"955101072","issue":"18062","name":"双色球","source":"iflytek","openDate":"2018-05-31","deadline":"2018-07-30"},{"number":"09,10,11,12,18,23|07","sale":"931563495","issue":"18061","name":"双色球","source":"iflytek","openDate":"2018-05-29","deadline":"2018-07-28"},{"number":"04,08,13,25,30,31|10","sale":"968808575","issue":"18060","name":"双色球","source":"iflytek","openDate":"2018-05-27","deadline":"2018-07-26"},{"number":"04,06,08,13,22,32|11","sale":"951572015","issue":"18059","name":"双色球","source":"iflytek","openDate":"2018-05-24","deadline":"2018-07-23"},{"number":"07,12,13,16,26,31|07","sale":"936339437","issue":"18058","name":"双色球","source":"iflytek","openDate":"2018-05-22","deadline":"2018-07-21"},{"number":"05,15,17,19,20,30|13","sale":"944921232","issue":"18057","name":"双色球","source":"iflytek","openDate":"2018-05-20","deadline":"2018-07-19"}]},"content":"双色球第18066期开奖号码是09,16,17,19,22,26|10","question":"我要看双色球彩票","semantic":[{"slots":[{"name":"name","value":"双色球"}],"intent":"NUMBER_QUERY"}],"service":"lottery","type":"xunfei"}
     * errorCode : 200
     * errorMessage : 操作成功
     */

    private DataBean data;
    private int errorCode;
    private String errorMessage;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static class DataBean {
        /**
         * arg : {"result":[{"number":"09,16,17,19,22,26|10","sale":"846907070","issue":"18066","name":"双色球","source":"iflytek","openDate":"2018-06-10","deadline":"2018-08-09"},{"number":"01,04,06,14,28,33|01","sale":"847267386","issue":"18065","name":"双色球","source":"iflytek","openDate":"2018-06-07","deadline":"2018-08-06"},{"number":"02,05,10,11,17,21|05","sale":"821423304","issue":"18064","name":"双色球","source":"iflytek","openDate":"2018-06-05","deadline":"2018-08-04"},{"number":"05,06,10,16,20,25|12","sale":"841729671","issue":"18063","name":"双色球","source":"iflytek","openDate":"2018-06-03","deadline":"2018-08-02"},{"number":"02,18,19,24,25,33|11","sale":"955101072","issue":"18062","name":"双色球","source":"iflytek","openDate":"2018-05-31","deadline":"2018-07-30"},{"number":"09,10,11,12,18,23|07","sale":"931563495","issue":"18061","name":"双色球","source":"iflytek","openDate":"2018-05-29","deadline":"2018-07-28"},{"number":"04,08,13,25,30,31|10","sale":"968808575","issue":"18060","name":"双色球","source":"iflytek","openDate":"2018-05-27","deadline":"2018-07-26"},{"number":"04,06,08,13,22,32|11","sale":"951572015","issue":"18059","name":"双色球","source":"iflytek","openDate":"2018-05-24","deadline":"2018-07-23"},{"number":"07,12,13,16,26,31|07","sale":"936339437","issue":"18058","name":"双色球","source":"iflytek","openDate":"2018-05-22","deadline":"2018-07-21"},{"number":"05,15,17,19,20,30|13","sale":"944921232","issue":"18057","name":"双色球","source":"iflytek","openDate":"2018-05-20","deadline":"2018-07-19"}]}
         * content : 双色球第18066期开奖号码是09,16,17,19,22,26|10
         * question : 我要看双色球彩票
         * semantic : [{"slots":[{"name":"name","value":"双色球"}],"intent":"NUMBER_QUERY"}]
         * service : lottery
         * type : xunfei
         */

        private ArgBean arg;
        private String content;
        private String question;
        private String service;
        private String type;
        private List<SemanticBean> semantic;

        public ArgBean getArg() {
            return arg;
        }

        public void setArg(ArgBean arg) {
            this.arg = arg;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<SemanticBean> getSemantic() {
            return semantic;
        }

        public void setSemantic(List<SemanticBean> semantic) {
            this.semantic = semantic;
        }

        public static class ArgBean {
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
                 * openDate : 2018-06-10
                 * deadline : 2018-08-09
                 */

                private String number;
                private String sale;
                private String issue;
                private String name;
                private String source;
                private String openDate;
                private String deadline;

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

                public String getOpenDate() {
                    return openDate;
                }

                public void setOpenDate(String openDate) {
                    this.openDate = openDate;
                }

                public String getDeadline() {
                    return deadline;
                }

                public void setDeadline(String deadline) {
                    this.deadline = deadline;
                }
            }
        }

        public static class SemanticBean {
            /**
             * slots : [{"name":"name","value":"双色球"}]
             * intent : NUMBER_QUERY
             */

            private String intent;
            private List<SlotsBean> slots;

            public String getIntent() {
                return intent;
            }

            public void setIntent(String intent) {
                this.intent = intent;
            }

            public List<SlotsBean> getSlots() {
                return slots;
            }

            public void setSlots(List<SlotsBean> slots) {
                this.slots = slots;
            }

            public static class SlotsBean {
                /**
                 * name : name
                 * value : 双色球
                 */

                private String name;
                private String value;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }
        }
    }
}
