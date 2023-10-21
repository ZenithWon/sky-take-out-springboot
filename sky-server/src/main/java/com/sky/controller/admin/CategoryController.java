package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminCategoryController")
@RequestMapping("/admin/category")
@Api("category api")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    @ApiOperation("Get category list")
    public Result<List<Category>> list(Integer type){
        List<Category> categoryList=categoryService.list(type);
        return Result.success(categoryList);
    }

    @GetMapping("/page")
    @ApiOperation("Search category by condition and limit")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO){
        PageResult pageResult=categoryService.pageQuery(categoryPageQueryDTO);
        log.info(categoryPageQueryDTO.toString());
        return Result.success(pageResult);
    }


    @GetMapping()
    @ApiOperation("Get a category by id")
    public Result<Category> getCategory(Long id){
        Category category= categoryService.getCategory(id);
        return Result.success(category);
    }


    @PostMapping()
    @ApiOperation("Add a new category")
    public Result save(@RequestBody CategoryDTO categoryDTO){
        categoryService.save(categoryDTO);
        return Result.success();
    }

    @DeleteMapping()
    @ApiOperation("Delete a category")
    public Result delete(Long id){
        categoryService.delete(id);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("Disable or enable a category")
    public Result permission(@PathVariable Integer status,Long id){
        categoryService.permission(status,id);
        return Result.success();
    }

    @PutMapping()
    @ApiOperation("Update category")
    public Result update(@RequestBody CategoryDTO categoryDTO){
        categoryService.update(categoryDTO);
        return Result.success();
    }
}
