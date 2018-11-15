package com.yd.ydsp.biz.cp;

import com.yd.ydsp.dal.entity.YdPaypointAgentapplyinfo;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author zengyixun
 * @date 17/10/19
 */
public interface AgentApplyCacheService {

    List<String> getAgentipinfo() throws IOException, ClassNotFoundException;
    List<String> setAgentipinfo(String ipStr) throws IOException, ClassNotFoundException;//设置为null，表明清空所有ip
    List<YdPaypointAgentapplyinfo> getAgentApplyInfo(String ipStr) throws IOException, ClassNotFoundException;
    boolean addAgentApplyInfo(String ipStr , YdPaypointAgentapplyinfo paypointAgentapplyinfo) throws IOException, ClassNotFoundException;
    void delAgentApplyInfo(String ipStr) throws IOException;

}
