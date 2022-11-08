package com.BGK.reggie.service;

import com.BGK.reggie.common.R;
import com.BGK.reggie.pojo.ShoppingCart;
import com.baomidou.mybatisplus.extension.service.IService;


public interface ShoppingCartService extends IService<ShoppingCart> {

    R<ShoppingCart> addDishOrSetmeal(ShoppingCart shoppingCart);
}
