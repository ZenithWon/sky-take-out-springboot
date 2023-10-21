package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminSetmealController")
@Slf4j
@Api("Setmeal Api")
@RequestMapping("/admin/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @PostMapping()
    @ApiOperation("Insert a new setmeal with dish info")
    @CacheEvict(cacheNames = "setmealCache",key = "#setmealDTO.categoryId")
    public Result save(@RequestBody SetmealDTO setmealDTO){
        setmealService.save(setmealDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("Get setmeals by condition")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult pageResult= setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("Set setmeal status")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result permission(@PathVariable Integer status, Long id){
        setmealService.permission(status,id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("Get setmeal by setmealId")
    public Result<SetmealVO> getById(@PathVariable Long id){
        SetmealVO setmealVO=setmealService.getByIdWithDish(id);
        return Result.success(setmealVO);
    }

    @PutMapping()
    @ApiOperation("Update a setmeal")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result update(@RequestBody SetmealDTO setmealDTO){
        setmealService.update(setmealDTO);
        return Result.success();
    }

    @DeleteMapping()
    @ApiOperation("Delete setmeal batch")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result delete(@RequestParam List<Long> ids){
        setmealService.deleteBatches(ids);
        return Result.success();
    }
}
