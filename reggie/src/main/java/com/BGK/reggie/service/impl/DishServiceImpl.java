package com.BGK.reggie.service.impl;

import com.BGK.reggie.common.CustomExpception;
import com.BGK.reggie.dto.DishDto;
import com.BGK.reggie.mapper.DishMapper;
import com.BGK.reggie.pojo.Dish;
import com.BGK.reggie.pojo.DishFlavor;
import com.BGK.reggie.pojo.Setmeal;
import com.BGK.reggie.pojo.SetmealDish;
import com.BGK.reggie.service.DishFlavorService;
import com.BGK.reggie.service.DishService;
import com.BGK.reggie.service.SetmealDishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增菜品
     *
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品基本信息到菜品表
        this.save(dishDto);
        //保存菜品口味到菜品口味表
        //设置菜品id
        Long id = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor dishFlavor : flavors) {
            dishFlavor.setDishId(id);
        }
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id获取菜品信息和对应得口味
     *
     * @param id
     * @return
     */
    @Override
    public DishDto getDisnWithFlavorById(Long id) {
        //查询菜品，在Dish表查询
        Dish dish = this.getById(id);

        //创建dishDto，并dish赋值给dishDto
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        //根据菜品Id查询菜品口味列表并赋值给DishDto，在DishFlavor表查询
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> dishFlavorsList = dishFlavorService.list(lambdaQueryWrapper);
        dishDto.setFlavors(dishFlavorsList);

        return dishDto;
    }

    /**
     * 修改菜品和对应的口味列表
     *
     * @param dishDto
     */
    @Transactional
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新Dish表基本信息
        this.saveOrUpdate(dishDto);

        //清除菜品相关口味列表 -- 对DishFlavor表的delete操作
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(lambdaQueryWrapper);

        //新增修改后的菜品相关口味列表 -- 对DishFlavor表的insert操作
        List<DishFlavor> flavorList = dishDto.getFlavors();
        for (DishFlavor dishFlavor : flavorList) {
            dishFlavor.setDishId(dishDto.getId());
        }
        dishFlavorService.saveBatch(flavorList);
    }

    /**
     * 判断是否关联套餐且修改菜品销售状态
     * @param status
     * @param ids
     */
    @Transactional
    @Override
    public void updateStatus(Integer status, List<Long> ids) {
        //条件构造器
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getDishId, ids);
        //判断是否关联套餐
        int count = setmealDishService.count(setmealDishLambdaQueryWrapper);
        if (count > 0) {
            //若关联则不能删除
            throw new CustomExpception("有菜品关联了套餐,不能停售");
        }

        //根据传入的status,停售或起售菜品
        LambdaUpdateWrapper<Dish> setmealLambdaQueryWrapper = new LambdaUpdateWrapper<>();
        setmealLambdaQueryWrapper.in(Dish::getId,ids);
        setmealLambdaQueryWrapper.eq(Dish::getStatus,1-status);
        setmealLambdaQueryWrapper.set(Dish::getStatus,status);
        this.update(setmealLambdaQueryWrapper);
    }

    /**
     * 判断是否在售，若停售删除菜品和相关口味
     * @param ids
     */
    @Override
    public void deleteDishWithFlavor(List<Long> ids) {
        //先判断在售情况
        LambdaQueryWrapper<Dish> DishlambdaQueryWrapper = new LambdaQueryWrapper<>();
        DishlambdaQueryWrapper.in(Dish::getId,ids);
        DishlambdaQueryWrapper.eq(Dish::getStatus,1);
        int count = this.count(DishlambdaQueryWrapper);
        if(count > 0){
            throw new CustomExpception("有菜品在售，不能删除！");
        }

        //删除菜品
        this.removeByIds(ids);

        //在删除相关口味
        LambdaQueryWrapper<DishFlavor> DishFlavorlambdaQueryWrapper = new LambdaQueryWrapper<>();
        DishFlavorlambdaQueryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(DishFlavorlambdaQueryWrapper);

    }
}
