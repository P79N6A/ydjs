package com.yd.ydsp.biz.scheduler;

import com.alibaba.edas.schedulerX.ProcessResult;
import com.alibaba.edas.schedulerX.ScxSimpleJobContext;
import com.alibaba.edas.schedulerX.ScxSimpleJobProcessor;
import com.yd.ydsp.biz.message.MnsService;
import com.yd.ydsp.biz.message.SmsMessageService;
import com.yd.ydsp.common.constants.GlobalConstant;
import com.yd.ydsp.common.enums.OrderStatusEnum;
import com.yd.ydsp.common.enums.StatusEnum;
import com.yd.ydsp.common.utils.DateUtils;
import com.yd.ydsp.dal.entity.YdPaypointCpOrder;
import com.yd.ydsp.dal.mapper.YdPaypointCpOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Time;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * @author zengyixun
 * @date 17/12/1
 */
public class CloseCpOrderByTimeOutProcessor implements ScxSimpleJobProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CloseCpOrderByTimeOutProcessor.class);

    @Resource
    private YdPaypointCpOrderMapper ydPaypointCpOrderMapper;

    @Override
    public ProcessResult process(ScxSimpleJobContext scxSimpleJobContext) {

        ProcessResult result = new ProcessResult(true);
        try {
            /**
             * 100，如果全部超时，再取100个
             */
            boolean isContinue = true;
            while (isContinue) {
                List<YdPaypointCpOrder> cpOrderList = ydPaypointCpOrderMapper.selectByStatus(OrderStatusEnum.NEW.getStatus(), 0, 100);
                if (cpOrderList != null && cpOrderList.size() > 0) {
                    for (YdPaypointCpOrder cpOrder : cpOrderList) {
                        if (!this.changeCpOrderStatus(cpOrder.getOrderid())) {
                            isContinue = false;
                            break;
                        }
                    }
                    Thread.sleep(5000);
                }else{
                    isContinue = false;
                }
            }
        }catch (Exception ex){
            logger.error("",ex);
        }catch (Throwable throwable){
            logger.error("",throwable);
        }

        return result;
    }

    /**
     * 关闭订单
     * @param orderid
     * @return 返回是否超时,如果没超时，就不用再捞新订单了
     */
    @Transactional(rollbackFor = Exception.class)
    protected boolean changeCpOrderStatus(String orderid){

        YdPaypointCpOrder order = ydPaypointCpOrderMapper.selectByOrderIdRowLock(orderid);
        boolean result = true;
        long orderTime = order.getCreateDate().getTime()/1000;
        long nowTime = Instant.now().getEpochSecond();
        if((nowTime-orderTime)>7200){
            result = true;
        }else{
            result = false;
        }

        if(order.getStatus().intValue()== OrderStatusEnum.NEW.getStatus().intValue()){
            order.setStatus(OrderStatusEnum.OVER.getStatus());
            ydPaypointCpOrderMapper.updateByPrimaryKeySelective(order);
        }
        return result;

    }


}
