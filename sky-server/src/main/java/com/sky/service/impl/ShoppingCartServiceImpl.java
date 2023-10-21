package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        Long userId=BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        List<ShoppingCart> shoppingCartList= shoppingCartMapper.list(shoppingCart);

        if(shoppingCartList!=null && shoppingCartList.size()>0){
            ShoppingCart cart=shoppingCartList.get(0);
            cart.setNumber(cart.getNumber()+1);
            shoppingCartMapper.updateNumberById(cart);
        } else{
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());

            Long dishId=shoppingCartDTO.getDishId();
            Long setmealId=shoppingCartDTO.getSetmealId();
            if (dishId!=null) {
                Dish dish=dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());

            } else {
                Setmeal setmeal=setmealMapper.getById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCartMapper.insert(shoppingCart);
        }

    }

    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        Long userId=BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        List<ShoppingCart> shoppingCartList= shoppingCartMapper.list(shoppingCart);
        shoppingCart=shoppingCartList.get(0);

        if(shoppingCart.getNumber()>1){
            shoppingCart.setNumber(shoppingCart.getNumber()-1);
            shoppingCartMapper.updateNumberById(shoppingCart);
        }
        else{
            shoppingCartMapper.deleteById(shoppingCart.getId());
        }
    }

    @Override
    public List<ShoppingCart> showShoppingCart() {
        ShoppingCart shoppingCart=ShoppingCart.builder().userId(BaseContext.getCurrentId()).build();

        List<ShoppingCart> shoppingCartList=shoppingCartMapper.list(shoppingCart);
        return shoppingCartList;
    }

    @Override
    public void clean() {
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
    }
}
