package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user/shop")
@Slf4j
@RestController("userShopController")
@Api("Shop Api")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/status")
    public Result<Integer> getStatus(){
        log.info("GETRequesting: user/shop/status");
        Integer status = (Integer) redisTemplate.opsForValue().get("SHOP_STATUS");
        log.info("Result:{}",Result.success(status));
        return Result.success(status);
    }
}
