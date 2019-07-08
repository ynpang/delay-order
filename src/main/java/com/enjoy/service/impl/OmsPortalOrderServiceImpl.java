package com.enjoy.service.impl;

import com.enjoy.component.CancelOrderSender;
import com.enjoy.service.OmsPortalOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class OmsPortalOrderServiceImpl implements OmsPortalOrderService{

    @Autowired
    private CancelOrderSender cancelOrderSender;

    @Override
    public void generateOrder() {
        //下单完成后开启一个延迟消息，用于当用户没有付款时取消订单（orderId应该在下单后生成）
        sendDelayMessageCancelOrder(1l);
    }

    @Override
    public void cancelOrder(Long orderId) {
        log.info("处理取消订单的业务");
    }

    @Override
    public void sendDelayMessageCancelOrder(Long orderId) {
        //获取订单超时时间
        long delayTimes = 15  * 60 * 1000;
        //发送延迟消息
        cancelOrderSender.sendMessage(orderId, delayTimes);

    }
}
