package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public List<Category> list(Integer type) {
        return categoryMapper.list(type);
    }

    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        List<Category> categoryList=categoryMapper.pageQuery(categoryPageQueryDTO);

        Integer total=categoryList.size();
        PageResult pageResult=new PageResult();
        pageResult.setTotal(total);

        Integer pageSize=categoryPageQueryDTO.getPageSize();
        Integer start=(categoryPageQueryDTO.getPage()-1)* pageSize;
        Integer end=start+pageSize;

        if (total<=start+pageSize)
            end= total;

        pageResult.setRecords(categoryList.subList(start,end));

        return pageResult;
    }

    @Override
    public void save(CategoryDTO categoryDTO) {
        Category category=new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        category.setStatus(StatusConstant.DISABLE);

        categoryMapper.insert(category);
    }

    @Transactional
    public void delete(Long id) {
        Integer dishCount=dishMapper.countByCategoryId(id);
        Integer setmealCount=setmealMapper.countByCategoryId(id);

        if(dishCount>0 ){
            throw  new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }
        else if(setmealCount>0){
            throw  new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }
        else {
            categoryMapper.deleteById(id);
        }
    }

    @Override
    public Category getCategory(Long id) {
        return categoryMapper.getById(id);
    }

    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category=new Category();
        BeanUtils.copyProperties(categoryDTO,category);

        categoryMapper.update(category);
    }

    @Override
    public void permission(Integer status, Long id) {
        Category category=Category.builder().id(id).status(status).build();

        categoryMapper.update(category);
    }

}
