package com.BGK.reggie.service.impl;

import com.BGK.reggie.common.CustomExpception;
import com.BGK.reggie.mapper.CategoryMapper;
import com.BGK.reggie.pojo.Category;
import com.BGK.reggie.pojo.Dish;
import com.BGK.reggie.pojo.Setmeal;
import com.BGK.reggie.service.CategoryService;
import com.BGK.reggie.service.DishService;
import com.BGK.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    /**
     * 删除分类信息，之前要进行判断
     * @param id
     * @return
     */
    @Transactional
    @Override
    public boolean remove(Long id) {
        //是否关联了菜品
        LambdaQueryWrapper<Dish> DishlambdaQueryWrapper = new LambdaQueryWrapper<>();
        DishlambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(DishlambdaQueryWrapper);
        if(count1 > 0){
            throw new CustomExpception("关联了菜品，不能删除");
        }

        //是否关联了套餐
        LambdaQueryWrapper<Setmeal> SetmeallambdaQueryWrapper = new LambdaQueryWrapper<>();
        SetmeallambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(SetmeallambdaQueryWrapper);
        if(count2 > 0){
            throw new CustomExpception("关联了套餐，不能删除");
        }

        return super.removeById(id);
    }

}
