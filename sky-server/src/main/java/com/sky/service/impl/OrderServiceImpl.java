package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderSerive;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderSerive {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private WebSocketServer webSocketServer;

    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        AddressBook addressBook=addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(addressBook==null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        Long userId= BaseContext.getCurrentId();
        ShoppingCart shoppingCart= ShoppingCart.builder().userId(userId).build();
        List<ShoppingCart> shoppingCartList= shoppingCartMapper.list(shoppingCart);
        if(shoppingCartList==null || shoppingCartList.size()==0){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        Orders orders=new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(userId);
        orderMapper.insert(orders);

        List<OrderDetail> orderDetailList=new ArrayList<>();
        for(ShoppingCart cart:shoppingCartList){
            OrderDetail orderDetail=new OrderDetail();
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);

        shoppingCartMapper.deleteByUserId(userId);

        OrderSubmitVO orderSubmitVO=OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(LocalDateTime.now())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();
        return orderSubmitVO;
    }

    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());

        Page<Orders> page= orderMapper.pageQuery(ordersPageQueryDTO);
        List<OrderVO> orderVOList=new ArrayList<>();

        for(Orders orders:page){
            Long orderId=orders.getId();
            List<OrderDetail>orderDetailList=orderDetailMapper.getByOrderId(orderId);
            OrderVO orderVO=new OrderVO();
            BeanUtils.copyProperties(orders,orderVO);
            orderVO.setOrderDetailList(orderDetailList);

            orderVOList.add(orderVO);
        }


        return new PageResult(page.getTotal(),orderVOList);
    }

    @Override
    public OrderVO details(Long id) {
        Orders orders=orderMapper.getById(id);
        List<OrderDetail> orderDetailList=orderDetailMapper.getByOrderId(id);

        OrderVO orderVO=new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    @Override
    public void cancelById(Long id) {
        Orders orders=orderMapper.getById(id);

        if(orders==null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        Integer status=orders.getStatus();
        if(status>2){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders ordersNew=new Orders();
        ordersNew.setId(orders.getId());
        if(status.equals(Orders.TO_BE_CONFIRMED)){
            ordersNew.setPayStatus(Orders.REFUND);
        }

        ordersNew.setStatus(Orders.CANCELLED);
        ordersNew.setCancelReason("用户取消");
        ordersNew.setCancelTime(LocalDateTime.now());
        System.out.println("updating..");
        orderMapper.update(ordersNew);
    }

    @Transactional
    public void repetition(Long id) {
        List<OrderDetail> orderDetailList=orderDetailMapper.getByOrderId(id);

        for(OrderDetail orderDetail:orderDetailList){
            ShoppingCart shoppingCart=new ShoppingCart();
            BeanUtils.copyProperties(orderDetail,shoppingCart);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCartMapper.insert(shoppingCart);
        }

    }

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());

        Page<Orders> page=orderMapper.pageQuery(ordersPageQueryDTO);
        List<OrderVO> orderVOList=new ArrayList<>();
        List<Orders> ordersList=page.getResult();

        if(ordersList!=null && ordersList.size()>0){
            for(Orders orders:ordersList){
                OrderVO orderVO=new OrderVO();
                BeanUtils.copyProperties(orders,orderVO);
                String orderDishes=OrderDishes2Str(orders);
                orderVO.setOrderDishes(orderDishes);

                orderVOList.add(orderVO);
            }
        }


        return new PageResult(page.getTotal(), orderVOList);
    }

    @Override
    public OrderStatisticsVO statistics() {
        OrderStatisticsVO  orderStatisticsVO=new OrderStatisticsVO();

        orderStatisticsVO.setToBeConfirmed(orderMapper.countStatus(Orders.TO_BE_CONFIRMED));
        orderStatisticsVO.setConfirmed(orderMapper.countStatus(Orders.CONFIRMED));
        orderStatisticsVO.setDeliveryInProgress(orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS));

        return orderStatisticsVO;
    }

    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders=Orders.builder().status(Orders.CONFIRMED).id(ordersConfirmDTO.getId()).build();
        orderMapper.update(orders);
    }

    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        Orders orderDB=orderMapper.getById(ordersCancelDTO.getId());
        if(orderDB==null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        Orders orders=Orders.builder()
                .id(ordersCancelDTO.getId())
                .status(Orders.CANCELLED)
                .cancelReason(ordersCancelDTO.getCancelReason())
                .cancelTime(LocalDateTime.now())
                .build();

        Integer payStatus=orderDB.getPayStatus();
        if(payStatus.equals(Orders.PAID)){
            orders.setPayStatus(Orders.REFUND);
        }

        orderMapper.update(orders);
    }

    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {

        Orders orderDB=orderMapper.getById(ordersRejectionDTO.getId());

        if(orderDB==null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        if(!orderDB.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders=Orders.builder()
                .id(ordersRejectionDTO.getId())
                .status(Orders.CANCELLED)
                .rejectionReason(ordersRejectionDTO.getRejectionReason())
                .cancelTime(LocalDateTime.now())
                .build();

        Integer payStatus=orderDB.getPayStatus();
        if(payStatus.equals(Orders.PAID)){
            orders.setPayStatus(Orders.REFUND);
        }

        orderMapper.update(orders);
    }

    @Override
    public void delivery(Long id) {
        Orders orderDB=orderMapper.getById(id);

        if(orderDB==null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        if(!orderDB.getStatus().equals(Orders.CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders=Orders.builder()
                .id(id)
                .status(Orders.DELIVERY_IN_PROGRESS).build();
        orderMapper.update(orders);
    }

    @Override
    public void complete(Long id) {
        Orders orderDB=orderMapper.getById(id);

        if(orderDB==null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        if(!orderDB.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders=Orders.builder()
                .id(id)
                .status(Orders.COMPLETED)
                .deliveryTime(LocalDateTime.now()).build();
        orderMapper.update(orders);
    }

    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
        String orderNumber=ordersPaymentDTO.getOrderNumber();

        Orders ordersDB=orderMapper.getByNumber(orderNumber);
        Orders orders=Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

        Map map=new HashMap();
        map.put("type",1);
        map.put("orderId",ordersDB.getId());
        map.put("content","订单号："+orderNumber);

        String json= JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);

        return new OrderPaymentVO();
    }

    @Override
    public void reminder(Long id) {
        Orders orderDB=orderMapper.getById(id);
        if (orderDB  == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        Map map=new HashMap();
        map.put("type",2);
        map.put("orderId",orderDB.getId());
        map.put("content","订单号："+orderDB.getNumber());

        String json=JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);
    }

    private String OrderDishes2Str(Orders orders){
        String res="";

        List<OrderDetail>orderDetailList=orderDetailMapper.getByOrderId(orders.getId());

        for(OrderDetail orderDetail:orderDetailList){
            res=res+orderDetail.getName()+"*"+orderDetail.getNumber()+";";
        }
        return res;
    }


}
