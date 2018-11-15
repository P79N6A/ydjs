package com.yd.ydsp.biz.scheduler;

import com.alibaba.edas.schedulerX.ProcessResult;
import com.alibaba.edas.schedulerX.ScxSimpleJobContext;
import com.alibaba.edas.schedulerX.ScxSimpleJobProcessor;
import com.yd.ydsp.biz.cp.AgentApplyCacheService;
import com.yd.ydsp.dal.entity.YdPaypointAgentapplyinfo;
import com.yd.ydsp.dal.mapper.YdPaypointAgentapplyinfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * @author zengyixun
 * @date 17/10/17
 * 每天两次同步代理商信息到数据库
 *
 */
public class AgentApplyProcessor implements ScxSimpleJobProcessor {
    /**
     * 在首页填写的代理商信息只是进入了缓存，每个IP只有5条数据，每天早上6点刷一次数据到数据库中,以例及时联系想要进行代理的公司
     * @param scxSimpleJobContext
     * @return
     */
    private static final Logger logger = LoggerFactory.getLogger(AgentApplyProcessor.class);

    @Resource
    AgentApplyCacheService agentApplyCacheService;
    @Resource
    YdPaypointAgentapplyinfoMapper ydPaypointAgentapplyinfoMapper;

    @Override
    public ProcessResult process(ScxSimpleJobContext scxSimpleJobContext)
    {
        ProcessResult result = new ProcessResult(true);
        try {
            List<String> ips = agentApplyCacheService.getAgentipinfo();
            if(ips!=null && ips.size()>0){

                for(String ipStr : ips){
                    List<YdPaypointAgentapplyinfo> paypointAgentapplyinfos = agentApplyCacheService.getAgentApplyInfo(ipStr);
                    if(paypointAgentapplyinfos==null || paypointAgentapplyinfos.size()<=0) {continue;}
                    for(YdPaypointAgentapplyinfo paypointAgentapplyinfo : paypointAgentapplyinfos){
                        /* 将代理商信息入库 */

                        ydPaypointAgentapplyinfoMapper.insertSelective(paypointAgentapplyinfo);

                    }
                    /* 入库成功，删除对应的缓存 */
                    agentApplyCacheService.delAgentApplyInfo(ipStr);
                }

                    /* 清空ip列表 */
                    agentApplyCacheService.setAgentipinfo(null);
                }


        }catch (Exception ex){
            logger.error("AgentApplyProcessor error: ",ex);
        }

        return result;
    }
}
