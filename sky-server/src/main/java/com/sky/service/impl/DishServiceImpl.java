package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private SetmealMapper setmealMapper;


    @Transactional
    public void insertWithFlavor(DishDTO dishDTO){
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        dishMapper.insert(dish);

        Long dishId=dish.getId();

        List<DishFlavor> flavors=dishDTO.getFlavors();
        flavors.stream().forEach(dishFlavor -> {
            dishFlavor.setDishId(dishId);
            dishFlavorMapper.insert(dishFlavor);
        });


    }

    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());

        Page<DishVO> page=dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }


    @Transactional
    public void deleteBatch(List<Long> ids) {
        for (Long id :ids){
            Dish dish=dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
            List<Long> setmealIds=setmealDishMapper.getSetmealIdByDishId(id);
            if(setmealIds!=null && setmealIds.size()>0){
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
        }

        for(Long id:ids){
            dishMapper.deleteById(id);
            dishFlavorMapper.deleteByDishId(id);
        }

    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        Dish dish= dishMapper.getById(id);
        List<DishFlavor> flavors =dishFlavorMapper.getByDishId(id);

        DishVO dishVO=new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(flavors);

        return dishVO;
    }

    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Long dishId=dishDTO.getId();

        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        dishMapper.update(dish);
        dishFlavorMapper.deleteByDishId(dishId);

        List<DishFlavor> flavors=dishDTO.getFlavors();
        flavors.stream().forEach(dishFlavor -> {
            dishFlavor.setDishId(dishId);
            dishFlavorMapper.insert(dishFlavor);
        });


    }

    @Override
    public List<Dish> list(Long categoryId) {
        Dish dish=Dish.builder().categoryId(categoryId).status(StatusConstant.ENABLE).build();
        return dishMapper.list(dish);
    }

    @Transactional
    public void permission(Integer status, Long id) {
        if(status==StatusConstant.DISABLE){
            List<Setmeal> setmeals=setmealMapper.getByDishId(id);
            if(setmeals!=null && setmeals.size()>0){
                setmeals.stream().forEach(setmeal -> {
                    if (setmeal.getStatus()==StatusConstant.ENABLE){
                        Setmeal setmealUpdate=Setmeal.builder().status(StatusConstant.DISABLE).id(setmeal.getId()).build();
                        setmeal.setStatus(StatusConstant.DISABLE);
                        setmealMapper.update(setmealUpdate);
                    }
                });
            }
        }

        Dish dish=Dish.builder().status(status).id(id).build();
        dishMapper.update(dish);
    }

    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList=dishMapper.list(dish);
        List<DishVO> dishVOList=new ArrayList<>();

        for(Dish d:dishList){
            DishVO dishVO=new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            List<DishFlavor> dishFlavorList=dishFlavorMapper.getByDishId(d.getId());
            dishVO.setFlavors(dishFlavorList);
            dishVOList.add(dishVO);

        }
        return dishVOList;
    }


}
