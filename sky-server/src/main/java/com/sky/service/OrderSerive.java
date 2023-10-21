package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderSerive {
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderVO details(Long id);

    void cancelById(Long id);

    void repetition(Long id);

    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO statistics();

    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    void cancel(OrdersCancelDTO ordersCancelDTO);

    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    void delivery(Long id);

    void complete(Long id);

    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO);

    void reminder(Long id);
}
