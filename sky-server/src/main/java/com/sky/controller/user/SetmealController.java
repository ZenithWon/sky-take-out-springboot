package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
@Slf4j
@Api("user setmeal api")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @GetMapping("/list")
    @ApiOperation("list setmeal by setmealid")
    @Cacheable(cacheNames = "setmealCache",key = "#categoryId")
    public Result<List<Setmeal>> list(Long categoryId){
        Setmeal setmeal =Setmeal.builder().status(StatusConstant.ENABLE).categoryId(categoryId).build();
        List<Setmeal> setmealList=setmealService.list(setmeal);

        return Result.success(setmealList);
    }

    @GetMapping("/dish/{setmealId}")
    @ApiOperation("list dishes in setmeal by setmealid")
    public Result<List<DishItemVO>> dishList(@PathVariable Long setmealId){
        List<DishItemVO> dishItemVOList=setmealService.getDishItemWithId(setmealId);

        return Result.success(dishItemVOList);
    }
}
