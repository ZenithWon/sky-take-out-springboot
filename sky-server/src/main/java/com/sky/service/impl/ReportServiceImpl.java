package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList=new ArrayList<>();
        List<Double> turnoverList=new ArrayList<>();

        for(LocalDate date=begin;!date.isAfter(end);date=date.plusDays(1)){
            dateList.add(date);

            LocalDateTime beginTime=LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime=LocalDateTime.of(date, LocalTime.MAX);

            Map map=new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);

            Double turnover=orderMapper.sumByMap(map);
            if(turnover==null){
                turnover=0.0;
            }
            turnoverList.add(turnover);
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList,','))
                .turnoverList(StringUtils.join(turnoverList,','))
                .build();
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList=new ArrayList<>();
        List<Integer> newUserList= new ArrayList<>();
        List<Integer> totalUserList=new ArrayList<>();

        for(LocalDate date=begin;!date.isAfter(end);date=date.plusDays(1)){
            dateList.add(date);

            LocalDateTime beginTime=LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime=LocalDateTime.of(date, LocalTime.MAX);

            Integer newUser=userMapper.getUserCount(beginTime,endTime);
            Integer totalUser=userMapper.getUserCount(null,endTime);

            if(newUser==null) newUser=0;
            if(totalUser==null) totalUser=0;

            newUserList.add(newUser);
            totalUserList.add(totalUser);
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList,','))
                .newUserList(StringUtils.join(newUserList,','))
                .totalUserList(StringUtils.join(totalUserList,','))
                .build();
    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList=new ArrayList<>();
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();

        Integer totalOrders=0;
        Integer totalValidOrders=0;
        for(LocalDate date=begin;!date.isAfter(end);date=date.plusDays(1)){
            LocalDateTime beginTime=LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime=LocalDateTime.of(date, LocalTime.MAX);

            Integer orderCount = getOrderCount(beginTime, endTime, null);
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);

            totalOrders+=orderCount;
            totalValidOrders+=validOrderCount;

            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }

        Double orderCompleteRate=0.0;
        if(totalOrders!=0){
            orderCompleteRate=totalValidOrders.doubleValue()/totalOrders;
        }

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrders)
                .validOrderCount(totalValidOrders)
                .orderCompletionRate(orderCompleteRate)
                .build();
    }

    private Integer getOrderCount(LocalDateTime beginTime,LocalDateTime endTime,Integer status){
        Map map=new HashMap();
        map.put("beginTime",beginTime);
        map.put("endTime",endTime);
        map.put("status",status);

        return orderMapper.countByMap(map);
    }

    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime=LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime=LocalDateTime.of(end, LocalTime.MAX);

        List<String> nameList=new ArrayList<>();
        List<Integer> numberList=new ArrayList<>();

        List<GoodsSalesDTO> goodsSalesDTOList= orderMapper.getTop10(beginTime,endTime);

        for(GoodsSalesDTO goodsSalesDTO:goodsSalesDTOList){
            nameList.add(goodsSalesDTO.getName());
            numberList.add(goodsSalesDTO.getNumber());
        }


        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList,','))
                .numberList(StringUtils.join(numberList,','))
                .build();
    }
}
