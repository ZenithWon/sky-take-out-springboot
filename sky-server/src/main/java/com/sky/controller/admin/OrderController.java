package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.entity.OrderDetail;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderSerive;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
@Api("admin order api")
public class OrderController {
    @Autowired
    private OrderSerive orderSerive;

    @GetMapping("conditionSearch")
    @ApiOperation("get orders by condition")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO){
        PageResult pageResult=orderSerive.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/statistics")
    @ApiOperation("Get each type order's number")
    public Result<OrderStatisticsVO> statistics(){
        OrderStatisticsVO orderStatisticsVO=orderSerive.statistics();
        return Result.success(orderStatisticsVO);
    }

    @GetMapping("/details/{id}")
    @ApiOperation("get order info")
    public Result<OrderVO> details(@PathVariable Long id){
        OrderVO orderVO=orderSerive.details(id);
        return Result.success(orderVO);
    }

    @PutMapping("/confirm")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        orderSerive.confirm(ordersConfirmDTO);
        return Result.success();
    }

    @PutMapping("/rejection")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        orderSerive.rejection(ordersRejectionDTO);
        return Result.success();
    }

    @PutMapping("/cancel")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
        orderSerive.cancel(ordersCancelDTO);
        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    public Result delivery(@PathVariable Long id) {
        orderSerive.delivery(id);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    public Result complete(@PathVariable Long id) {
        orderSerive.complete(id);
        return Result.success();
    }
}
