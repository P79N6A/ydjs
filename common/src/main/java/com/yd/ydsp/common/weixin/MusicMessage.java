package com.yd.ydsp.common.weixin;

public class MusicMessage extends ReturnBaseMsg {

    /**
     * 通过素材管理中的接口上传多媒体文件，得到的id
     */
    private String Voice;

    public String getVoice() {
        return Voice;
    }

    public void setVoice(String voice) {
        Voice = voice;
    }
}
