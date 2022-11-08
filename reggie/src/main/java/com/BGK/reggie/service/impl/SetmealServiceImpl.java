package com.BGK.reggie.service.impl;

import com.BGK.reggie.common.CustomExpception;
import com.BGK.reggie.dto.SetmealDto;
import com.BGK.reggie.mapper.SetmealMapper;
import com.BGK.reggie.pojo.Setmeal;
import com.BGK.reggie.pojo.SetmealDish;
import com.BGK.reggie.service.CategoryService;
import com.BGK.reggie.service.SetmealDishService;
import com.BGK.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 修改套餐在售状态
     *
     * @param status
     * @param ids
     */
    @Override
    public void updateStatus(Integer status, List<Long> ids) {
        //根据传入的ids,停售或起售菜品
        LambdaUpdateWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaUpdateWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId, ids);
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus, 1 - status);
        setmealLambdaQueryWrapper.set(Setmeal::getStatus, status);
        this.update(setmealLambdaQueryWrapper);
    }

    /**
     * 保存套餐和相应的菜品
     *
     * @param setmealDto
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息，操作setmeal表
        this.save(setmealDto);

        //获得套餐id
        Long setmealId = setmealDto.getId();

        //设置套餐关联菜品表的套餐id
        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishList) {
            setmealDish.setSetmealId(setmealId);
        }

        //设置套餐和菜品关联信息，操作setmeal_dish表
        setmealDishService.saveBatch(setmealDishList);
    }

    /**
     * 删除套餐和相关菜品关联
     *
     * @param ids
     */
    @Transactional
    @Override
    public void deleteWithDish(List<Long> ids) {
        //条件构造器
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Setmeal::getId, ids);
        lambdaQueryWrapper.eq(Setmeal::getStatus, 1);
        //查询套餐是否在售状态
        int count = this.count(lambdaQueryWrapper);
        if (count > 0) {
            //套餐在售不可删除
            throw new CustomExpception("套餐在售，不可删除");
        }
        //先删除套餐信息
        this.removeByIds(ids);

        //再删除套餐和菜品关联信息
        LambdaQueryWrapper<SetmealDish> SetmealDishlambdaQueryWrapper = new LambdaQueryWrapper<>();
        SetmealDishlambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(SetmealDishlambdaQueryWrapper);

    }

    /**
     * 根据id回显Stemeal信息
     * @param id
     * @return
     */
    @Override
    public SetmealDto getOneSetmealById(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);

        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishList = setmealDishService.list(lambdaQueryWrapper);

        setmealDto.setSetmealDishes(setmealDishList);

        String categoryName = categoryService.getById(setmeal.getCategoryId()).getName();

        setmealDto.setCategoryName(categoryName);

        return setmealDto;
    }
}
