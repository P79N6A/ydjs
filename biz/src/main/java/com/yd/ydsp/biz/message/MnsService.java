package com.yd.ydsp.biz.message;

import java.util.Map;

/**
 * @author zengyixun
 * @date 17/12/1
 */
public interface MnsService {

    /**
     *
     * @param queueType GlobalConstant中SMS开头的类型
     * @return id:消息id;handle;body:消息体
     */
    public Map<String,String> receiveMsg(String queueType);

    /**
     *
     * @param queueType GlobalConstant中SMS开头的类型
     * @param handle 接收消息时返回在map中的handle字段
     */
    public void deleteMsg(String queueType,String handle);

    /**
     *
     * @param queueType GlobalConstant中SMS开头的类型
     * @param body 消息体
     * @return 消息id
     */
    public String sendMsg(String queueType,String body);

}
