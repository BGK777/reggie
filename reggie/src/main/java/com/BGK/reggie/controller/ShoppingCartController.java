package com.BGK.reggie.controller;

import com.BGK.reggie.common.BaseContext;
import com.BGK.reggie.common.R;
import com.BGK.reggie.pojo.ShoppingCart;
import com.BGK.reggie.service.ShoppingCartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        return shoppingCartService.addDishOrSetmeal(shoppingCart);
    }

    /**
     * 获取购物车信息列表
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);
        lambdaQueryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(lambdaQueryWrapper);
        return R.success(shoppingCartList);
    }

    /**
     * 减少购物车中的菜品或者套餐数量
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        if(dishId != null){
            lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
            ShoppingCart shoppingCart1 = shoppingCartService.getOne(lambdaQueryWrapper);

            shoppingCart1.setNumber(shoppingCart1.getNumber()-1);
            Integer laterNumber = shoppingCart1.getNumber();

            if(laterNumber > 0){
                //数量大于0，进行修改
                shoppingCartService.updateById(shoppingCart1);
            } else if (laterNumber == 0) {
                //数量等于0,删除购物车订单
                shoppingCartService.removeById(shoppingCart1.getId());
            } else {
                return R.error("操作异常");
            }
            return R.success(shoppingCart1);
        }

        Long setmealId = shoppingCart.getSetmealId();
        if(setmealId != null){
            lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
            ShoppingCart shoppingCart2 = shoppingCartService.getOne(lambdaQueryWrapper);

            shoppingCart2.setNumber(shoppingCart2.getNumber()-1);
            Integer laterNumber = shoppingCart2.getNumber();

            if(laterNumber > 0){
                //数量大于0，进行修改
                shoppingCartService.updateById(shoppingCart2);
            } else if (laterNumber == 0) {
                //数量等于0,删除购物车订单
                shoppingCartService.removeById(shoppingCart2.getId());
            } else {
                return R.error("操作异常");
            }
            return R.success(shoppingCart2);
        }
        return R.error("操作异常");
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(lambdaQueryWrapper);
        return R.success("清空购物车成功");
    }
}
