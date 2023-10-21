package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    @Scheduled(cron = "0 * * * * ?")
//    @Scheduled(cron = "0/5 * * * * ?")
    public void processTimeoutOrder(){
        Orders orders=Orders.builder()
                .status(Orders.PENDING_PAYMENT)
                .orderTime(LocalDateTime.now().plusMinutes(-15))
                .build();

        List<Orders> ordersList=orderMapper.getByStatusAndOrderTime(orders);

        if(ordersList!=null && ordersList.size()>0){
            for(Orders o:ordersList){
                o.setStatus(Orders.CANCELLED);
                o.setCancelReason("订单超时");
                o.setCancelTime(LocalDateTime.now());

                orderMapper.update(o);
            }
            log.info("process Timeout order number:{}",ordersList.size());
        }
        else {
            log.info("No Timeout order");
        }


    }

    @Scheduled(cron = "0 0 1 * * ?")
//    @Scheduled(cron = "0/5 * * * * ?")
    public void processDelivery(){
        Orders orders=Orders.builder()
                .status(Orders.DELIVERY_IN_PROGRESS)
                .orderTime(LocalDateTime.now().plusHours(-1))
                .build();

        List<Orders> ordersList=orderMapper.getByStatusAndOrderTime(orders);

        if(ordersList!=null && ordersList.size()>0){
            for(Orders o:ordersList){
                o.setStatus(Orders.COMPLETED);
                orderMapper.update(o);
            }
            log.info("process deliver timeout order number:{}",ordersList.size());
        }

        log.info("No deliver timeout order");
    }
}
