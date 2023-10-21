package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    @Select("select * from orders where  id=#{id}")
    Orders getById(Long id);

    @Select("select count(*) from orders where status=#{status}")
    Integer countStatus(Integer status);

    @Select("select * from orders where status=#{status} and order_time<#{orderTime}")
    List<Orders> getByStatusAndOrderTime(Orders orders);

    @Select("select * from orders where number=#{number}")
    Orders getByNumber(String number);

    void update(Orders orders);

    Double sumByMap(Map map);

    void insert(Orders orders);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    List<GoodsSalesDTO> getTop10(LocalDateTime beginTime, LocalDateTime endTime);

    Integer countByMap(Map map);
}
