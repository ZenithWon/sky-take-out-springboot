package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userShoppingCart")
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api("user shoppingCart api")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    @ApiOperation("add in shopingCart")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    @PostMapping("/sub")
    @ApiOperation("sub in shopingCart")
    public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info(shoppingCartDTO.toString());
        shoppingCartService.subShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("Get all shopping cart")
    public Result<List<ShoppingCart>> list(){
        List<ShoppingCart> shoppingCartList= shoppingCartService.showShoppingCart();
        return Result.success(shoppingCartList);
    }

    @DeleteMapping("/clean")
    @ApiOperation("delete all shoppcarts")
    public Result delete(){
        shoppingCartService.clean();
        return Result.success();
    }
}
