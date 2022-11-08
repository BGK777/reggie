package com.BGK.reggie.service.impl;

import com.BGK.reggie.common.BaseContext;
import com.BGK.reggie.common.R;
import com.BGK.reggie.mapper.ShoppingCartMapper;
import com.BGK.reggie.pojo.ShoppingCart;
import com.BGK.reggie.service.ShoppingCartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

import java.sql.DatabaseMetaData;
import java.time.LocalDateTime;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    /**
     * 添加购物车
     *
     * @param shoppingCart
     */
    @Override
    public R<ShoppingCart> addDishOrSetmeal(ShoppingCart shoppingCart) {
        //设置用户id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        //判断是套餐还是菜品
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());

        if (dishId != null) {
            //菜品
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);

        } else {
            //套餐
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart shoppingCartOne = this.getOne(lambdaQueryWrapper);
        //判断是否存在，不存在则新增（默认为一），存在则加一
        if(shoppingCartOne != null){
            Integer number = shoppingCartOne.getNumber();
            shoppingCartOne.setNumber(number+1);
            this.updateById(shoppingCartOne);
        }else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            this.save(shoppingCart);
            shoppingCartOne = shoppingCart;
        }

        return R.success(shoppingCartOne);

    }
}
