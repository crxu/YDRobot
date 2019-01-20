package com.readyidu.robot.model.business.tv;

import java.io.Serializable;
import java.util.List;

/**
 * @Autour: wlq
 * @Description: 频道播放详情
 * @Date: 2017/10/23 11:11
 * @Update: 2017/10/23 11:11
 * @UpdateRemark:
 * @Version: V1.0
*/
public class TvDetail implements Serializable {

    /**
     * channel : {"sources":["http://live.aikan.miguvideo.com/wd_r2/cctv/cctv1hd/1200/01.m3u8$1","sourceUri://cctv/cctv_migu/source_migu_cctv_1","http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8","http://ivi.bupt.edu.cn/hls/cctv1.m3u8","http://www.ynbit.cn:1935/CCTV-1/livestream/playlist.m3u8","sourceUri://cctv/cctv_nettv/source_nettv_cctv1","sourceUri://cctv/cctv_fengmi/source_fengmi_cctv_cctv1:1","sourceUri://cctv/cctv_fengmi/source_fengmi_cctv_cctv1:2"],"channel":"CCTV1","typeid":"200","id":1}
     * playBill : {"tommorrowProgram":[{"showTime":"00:00","channelName":"寻宝-我有传家宝2017-37"},{"showTime":"00:48","channelName":"动物世界-2017-163（45分钟）"},{"showTime":"01:33","channelName":"晚间新闻"},{"showTime":"02:03","channelName":"精彩一刻-2017-120完整版航拍中国上海精编"},{"showTime":"02:52","channelName":"精彩一刻-2017-154航拍中国海南山三亚"},{"showTime":"02:58","channelName":"精彩一刻-2017-224中国诗词大会第二季第四场"},{"showTime":"04:27","channelName":"精彩一刻-2017-254航拍中国陕西30分版"},{"showTime":"04:57","channelName":"新闻联播"},{"showTime":"05:27","channelName":"人与自然-2017-287"},{"showTime":"06:00","channelName":"朝闻天下"},{"showTime":"08:36","channelName":"生活早参考-特别节目（生活圈）2017-196"},{"showTime":"09:24","channelName":"海棠依旧23"},{"showTime":"10:12","channelName":"海棠依旧24"},{"showTime":"11:02","channelName":"海棠依旧25"},{"showTime":"12:00","channelName":"新闻30分"},{"showTime":"12:41","channelName":"特别呈现2017-350 超级工程第三季2"},{"showTime":"13:37","channelName":"平凡的世界39"},{"showTime":"14:26","channelName":"平凡的世界40"},{"showTime":"15:17","channelName":"平凡的世界41"},{"showTime":"16:07","channelName":"平凡的世界42"},{"showTime":"17:04","channelName":"第五批中国梦歌曲（30首）-梦想开始的地方"},{"showTime":"17:15","channelName":"第一动画乐园：新大头儿子和小头爸爸第四季（643）"},{"showTime":"17:43","channelName":"第一动画乐园：新大头儿子和小头爸爸第四季（644）"},{"showTime":"18:26","channelName":"第一动画乐园（故事乐园）-2017-77"},{"showTime":"18:36","channelName":"第一动画乐园：新大头儿子和小头爸爸第四季（645）"},{"showTime":"19:00","channelName":"新闻联播"},{"showTime":"19:31","channelName":"广告+天气预报"},{"showTime":"19:39","channelName":"焦点访谈（20171020）（首播）"},{"showTime":"20:04","channelName":"前情提要-电视剧"},{"showTime":"20:10","channelName":"电视剧"},{"showTime":"21:01","channelName":"机智过人（第一季）-5（20171020）（首播）"},{"showTime":"21:59","channelName":"晚间新闻"},{"showTime":"22:44","channelName":"特别呈现2017-351 超级工程第三季3"},{"showTime":"23:41","channelName":"机智过人（第一季）-5"}],"todayProgram":[{"showTime":"00:00","channelName":"特别呈现2017-349 超级工程第三季1"},{"showTime":"00:16","channelName":"魅力中国城"},{"showTime":"01:49","channelName":"生活提示2017-236"},{"showTime":"01:55","channelName":"晚间新闻"},{"showTime":"02:40","channelName":"动物世界-2017-162（45分钟）"},{"showTime":"03:25","channelName":"精彩一刻-2017-169航拍中国陕西精编1"},{"showTime":"03:45","channelName":"精彩一刻-2017-155航拍中国海南灯塔渔村"},{"showTime":"03:55","channelName":"精彩一刻-2017-253航拍中国黑龙江30分版"},{"showTime":"04:25","channelName":"新闻联播"},{"showTime":"05:27","channelName":"人与自然-2017-286"},{"showTime":"06:00","channelName":"朝闻天下"},{"showTime":"09:07","channelName":"海棠依旧20"},{"showTime":"10:00","channelName":"海棠依旧21"},{"showTime":"10:52","channelName":"海棠依旧22"},{"showTime":"11:39","channelName":"精彩一刻-2017-183航拍中国陕西精编2"},{"showTime":"12:00","channelName":"新闻30分"},{"showTime":"13:09","channelName":"特别呈现2017-349 超级工程第三季1"},{"showTime":"14:04","channelName":"平凡的世界35"},{"showTime":"14:54","channelName":"平凡的世界36"},{"showTime":"15:45","channelName":"平凡的世界37"},{"showTime":"16:34","channelName":"平凡的世界38"},{"showTime":"17:31","channelName":"第五批中国梦歌曲（30首）-看山看水看中国"},{"showTime":"17:43","channelName":"第一动画乐园：新大头儿子和小头爸爸第四季（640）"},{"showTime":"18:33","channelName":"第一动画乐园：新大头儿子和小头爸爸第四季（641）"},{"showTime":"18:46","channelName":"第一动画乐园（故事乐园）-2017-76"},{"showTime":"18:46","channelName":"第一动画乐园：新大头儿子和小头爸爸第四季（642）"},{"showTime":"19:00","channelName":"新闻联播"},{"showTime":"19:31","channelName":"广告+天气预报"},{"showTime":"19:40","channelName":"焦点访谈（20171019）（首播）"},{"showTime":"20:02","channelName":"前情提要- 《青恋》第3集"},{"showTime":"20:05","channelName":"青恋3/26"},{"showTime":"20:56","channelName":"前情提要-《青恋》4/26"},{"showTime":"21:01","channelName":"青恋4/26"},{"showTime":"22:00","channelName":"晚间新闻"},{"showTime":"22:42","channelName":"特别呈现2017-350 超级工程第三季2"},{"showTime":"23:36","channelName":"生活提示2017-237"},{"showTime":"23:44","channelName":"寻宝-我有传家宝2017-37"}]}
     */

    private TvChannel channel;
    private PlayBillBean playBill;

    public TvChannel getChannel() {
        return channel;
    }

    public void setChannel(TvChannel channel) {
        this.channel = channel;
    }

    public PlayBillBean getPlayBill() {
        return playBill;
    }

    public void setPlayBill(PlayBillBean playBill) {
        this.playBill = playBill;
    }

    public static class PlayBillBean implements Serializable {
        private List<TommorrowProgramBean> tommorrowProgram;
        private List<TodayProgramBean> todayProgram;

        public List<TommorrowProgramBean> getTommorrowProgram() {
            return tommorrowProgram;
        }

        public void setTommorrowProgram(List<TommorrowProgramBean> tommorrowProgram) {
            this.tommorrowProgram = tommorrowProgram;
        }

        public List<TodayProgramBean> getTodayProgram() {
            return todayProgram;
        }

        public void setTodayProgram(List<TodayProgramBean> todayProgram) {
            this.todayProgram = todayProgram;
        }

        public static class TommorrowProgramBean  implements Serializable {
            /**
             * showTime : 00:00
             * channelName : 寻宝-我有传家宝2017-37
             */

            private String showTime;
            private String channelName;

            public String getShowTime() {
                return showTime;
            }

            public void setShowTime(String showTime) {
                this.showTime = showTime;
            }

            public String getChannelName() {
                return channelName;
            }

            public void setChannelName(String channelName) {
                this.channelName = channelName;
            }
        }

        public static class TodayProgramBean implements Serializable {
            /**
             * showTime : 00:00
             * channelName : 特别呈现2017-349 超级工程第三季1
             */

            private String showTime;
            private String channelName;

            public String getShowTime() {
                return showTime;
            }

            public void setShowTime(String showTime) {
                this.showTime = showTime;
            }

            public String getChannelName() {
                return channelName;
            }

            public void setChannelName(String channelName) {
                this.channelName = channelName;
            }
        }
    }
}
