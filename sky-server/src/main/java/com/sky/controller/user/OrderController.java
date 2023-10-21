package com.sky.controller.user;


import com.github.pagehelper.Page;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderSerive;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Slf4j
@Api("User order api")
public class OrderController {

    @Autowired
    private OrderSerive orderSerive;


    @PostMapping("/submit")
    @ApiOperation("submit a order")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        OrderSubmitVO orderSubmitVO= orderSerive.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    @PutMapping("/payment")
    @ApiOperation("wechatpay simulation")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO){
        OrderPaymentVO orderPaymentVO=orderSerive.payment(ordersPaymentDTO);
        return Result.success(orderPaymentVO);
    }

    @GetMapping("/historyOrders")
    @ApiOperation("Get history orders")
    public Result<PageResult> page(OrdersPageQueryDTO ordersPageQueryDTO){
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        PageResult pageResult=orderSerive.pageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/orderDetail/{id}")
    @ApiOperation("get order detail")
    public Result<OrderVO> details(@PathVariable Long id){
        OrderVO orderVO=orderSerive.details(id);

        log .info(orderVO.toString());
        return Result.success(orderVO);
    }

    @PutMapping("/cancel/{id}")
    @ApiOperation("cancel order")
    public Result cancel(@PathVariable Long id){
        orderSerive.cancelById(id);
        return Result.success();
    }

    @PostMapping("/repetition/{id}")
    @ApiOperation("another one")
    public Result repetition(@PathVariable Long id) {
        orderSerive.repetition(id);
        return Result.success();
    }

    @GetMapping("/reminder/{id}")
    @ApiOperation("remind admin about the order")
    public Result reminder(@PathVariable Long id){
        orderSerive.reminder(id);
        return Result.success();
    }
}
