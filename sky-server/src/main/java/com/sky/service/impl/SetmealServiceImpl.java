package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.controller.admin.SetmealController;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Transactional
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);

        setmealMapper.insert(setmeal);

        Long semealId=setmeal.getId();

        List<SetmealDish> setmealDishList=setmealDTO.getSetmealDishes();
        setmealDishList.stream().forEach(setmealDish -> {
            setmealDish.setSetmealId(semealId);
            setmealDishMapper.insert(setmealDish);
        });


    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        int pageNum = setmealPageQueryDTO.getPage();
        int pageSize = setmealPageQueryDTO.getPageSize();

        PageHelper.startPage(pageNum, pageSize);

        Page<SetmealVO> page=setmealMapper.pageQuery(setmealPageQueryDTO);

        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void permission(Integer status, Long id) {
        if(status == StatusConstant.ENABLE){
            List<Dish> dishes=dishMapper.getBySetmealId(id);
            if(dishes!=null &&dishes.size()>0){
                dishes.stream().forEach(dish -> {
                    if(dish.getStatus()==StatusConstant.DISABLE){
                        throw  new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                });
            }
        }

        Setmeal setmeal= Setmeal.builder().status(status).id(id).build();
        setmealMapper.update(setmeal);
    }

    @Override
    public SetmealVO getByIdWithDish(Long id) {
        Setmeal setmeal=setmealMapper.getById(id);
        List<SetmealDish> setmealDishes=setmealDishMapper.getBySetmealId(id);

        SetmealVO setmealVO=new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    @Override
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);

        Long setmealId=setmeal.getId();
        setmealDishMapper.deleteBySetmealId(setmealId);

        List<SetmealDish> setmealDishes=setmealDTO.getSetmealDishes();
        setmealDishes.stream().forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
            setmealDishMapper.insert(setmealDish);
        });
    }

    @Transactional
    public void deleteBatches(List<Long> ids) {
        ids.forEach(id->{
            Setmeal setmeal=setmealMapper.getById(id);
            if(StatusConstant.ENABLE==setmeal.getStatus()){
                throw  new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
            setmealMapper.deleteById(id);
            setmealDishMapper.deleteBySetmealId(id);
        });
    }

    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> setmealList=setmealMapper.list(setmeal);
        return setmealList;
    }

    @Override
    public List<DishItemVO> getDishItemWithId(Long setmealId) {
        return setmealMapper.getDishItemBySetmealId(setmealId);
    }
}
