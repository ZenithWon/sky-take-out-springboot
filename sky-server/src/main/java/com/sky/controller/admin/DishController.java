package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController("adminDishController")
@RequestMapping("/admin/dish")
@Api("Dish Api")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping()
    @ApiOperation("Insert a new dish")
    public Result save(@RequestBody DishDTO dishDTO){
        dishService.insertWithFlavor(dishDTO);
        String key="dish_"+dishDTO.getCategoryId();
        cleanCache(key);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("Search dishes with limit")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        PageResult pageResult=dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping()
    @ApiOperation("Delete batch dishes")
    public Result delete(@RequestParam List<Long> ids){
        dishService.deleteBatch(ids);
        cleanCache("dish_*");
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("Get dish by dishId")
    public Result<DishVO> getDishByIdWithFlavor(@PathVariable Long id){
        DishVO dishVO=dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    @PutMapping()
    @ApiOperation("Update a dish info")
    public Result update(@RequestBody DishDTO dishDTO){
        dishService.updateWithFlavor(dishDTO);
        cleanCache("dish_*");
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("Get dishes by categoryId")
    public Result<List<Dish>> list(Long categoryId){
        List<Dish> dishes= dishService.list(categoryId);
        return Result.success(dishes);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("Disable or enable a dish")
    public Result permission(@PathVariable Integer status,Long id){
        dishService.permission(status,id);
        cleanCache("dish_*");
        return Result.success();
    }

    private void cleanCache(String pattern){
        Set keys=redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
