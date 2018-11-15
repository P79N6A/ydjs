package com.yd.ydsp.biz.cp.impl;

import com.yd.ydsp.biz.cp.AgentApplyCacheService;
import com.yd.ydsp.common.redis.RedisManager;
import com.yd.ydsp.common.redis.SerializeUtils;
import com.yd.ydsp.dal.entity.YdPaypointAgentapplyinfo;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengyixun on 17/10/19.
 */
public class AgentApplyCacheServiceImpl implements AgentApplyCacheService {

    private String ipsKey ="agentipinfo";

    @Resource
    RedisManager redisManager;

    @Override
    public List<String> getAgentipinfo() throws IOException, ClassNotFoundException {
        byte[] bToken = redisManager.get(SerializeUtils.serialize(ipsKey));
        if (bToken != null) {
            return (List<String>) SerializeUtils.deserialize(bToken);
        }
        return null;
    }

    @Override
    public synchronized List<String> setAgentipinfo(String ipStr) throws IOException, ClassNotFoundException {
        //ipStr设置为null，表明清空所有ip
        List<String> ips = this.getAgentipinfo();
        if(ipStr==null){
            redisManager.del(SerializeUtils.serialize(ipsKey));
            return ips;
        }
        if(ips!=null) {
            if (ips.contains(ipStr)) {
                return ips;
            }else{
                ips.add(ipStr);
            }
        }else{
            ips = new ArrayList<>();
            ips.add(ipStr);
        }
        redisManager.set(SerializeUtils.serialize(ipsKey),SerializeUtils.serialize(ips),0);
        return ips;
    }

    @Override
    public List<YdPaypointAgentapplyinfo> getAgentApplyInfo(String ipStr) throws IOException, ClassNotFoundException {
        byte[] bToken = redisManager.get(SerializeUtils.serialize(ipStr));
        if (bToken != null) {
            return (List<YdPaypointAgentapplyinfo>) SerializeUtils.deserialize(bToken);
        }
        return null;
    }

    @Override
    public synchronized boolean addAgentApplyInfo(String ipStr, YdPaypointAgentapplyinfo paypointAgentapplyinfo) throws IOException, ClassNotFoundException {
        List<YdPaypointAgentapplyinfo> paypointAgentapplyinfos = getAgentApplyInfo(ipStr);
        if(paypointAgentapplyinfos==null){
            paypointAgentapplyinfos = new ArrayList<>();
        }
        if(paypointAgentapplyinfos.size()>=15){
            return false;
        }
        paypointAgentapplyinfos.add(paypointAgentapplyinfo);
        /* 保存3小时 */
        redisManager.set(SerializeUtils.serialize(ipStr),SerializeUtils.serialize(paypointAgentapplyinfos),10800);
        return true;
    }

    @Override
    public void delAgentApplyInfo(String ipStr) throws IOException {

        redisManager.del(SerializeUtils.serialize(ipStr));

    }
}
