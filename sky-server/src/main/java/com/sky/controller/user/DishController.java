package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api("User dish api")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/list")
    @ApiOperation("Get dish with categoryId")
    public Result<List<DishVO>> list(Long categoryId){
        String key="dish_"+categoryId;
        List<DishVO> dishVOList=(List<DishVO>) redisTemplate.opsForValue().get(key);

        if(dishVOList!=null&&dishVOList.size()>0){
            return Result.success(dishVOList);
        }
        else{
            Dish dish= Dish.builder().categoryId(categoryId).status(StatusConstant.ENABLE).build();
            dishVOList=dishService.listWithFlavor(dish);
            redisTemplate.opsForValue().set(key,dishVOList);
            return Result.success(dishVOList);
        }


    }
}
