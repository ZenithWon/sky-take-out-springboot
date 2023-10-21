package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {

    @Select("select count(id) from dish where category_id=#{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @Select("select * from dish where id=#{id}")
    Dish getById(Long id);

    @Delete("delete from dish where id=#{id}")
    void deleteById(Long id);

    @AutoFill(value=OperationType.INSERT)
    void insert(Dish dish);

    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    List<Dish> list(Dish dish);

    List<Dish> getBySetmealId(Long setmealId);

    Integer countByMap(Map map);
}
