package com.BGK.reggie.controller;

import com.BGK.reggie.common.R;
import com.BGK.reggie.dto.DishDto;
import com.BGK.reggie.pojo.Category;
import com.BGK.reggie.pojo.Dish;
import com.BGK.reggie.pojo.DishFlavor;
import com.BGK.reggie.service.CategoryService;
import com.BGK.reggie.service.DishFlavorService;
import com.BGK.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(Integer page,Integer pageSize,String name){
        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id获取菜品信息和对应得口味
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getOneById(@PathVariable("id") Long id){
        DishDto dishDto = dishService.getDisnWithFlavorById(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品信息成功");
    }

//    /**
//     * 根据菜品分类id获取菜品列表
//     * @param dish
//     * @return
//     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        //条件构造器
//        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//
//        //添加条件
//        lambdaQueryWrapper.eq(dish.getCategoryId()!= null,Dish::getCategoryId,dish.getCategoryId());
//        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        //添加查询，status为1（起售）的菜品
//        lambdaQueryWrapper.eq(Dish::getStatus,1);
//        //执行查询
//        List<Dish> list = dishService.list(lambdaQueryWrapper);
//        return R.success(list);
//    }

    /**
     * 根据菜品分类id获取菜品列表
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //条件构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //添加条件
        lambdaQueryWrapper.eq(dish.getCategoryId()!= null,Dish::getCategoryId,dish.getCategoryId());
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //添加查询，status为1（起售）的菜品
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        //执行查询
        List<Dish> DishList = dishService.list(lambdaQueryWrapper);

        //把DishList中的各项赋上新值后在赋给dishDtoList
        List<DishDto> dishDtoList = new ArrayList<>();

        for (Dish dishInfo: DishList) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dishInfo,dishDto);

            Long categoryId = dishInfo.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId,dishInfo.getId());
            List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper);
            dishDto.setFlavors(dishFlavorList);
            dishDtoList.add(dishDto);
        }
        return R.success(dishDtoList);
    }

    /**
     * 修改菜品在售状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable("status") Integer status,@RequestParam List<Long> ids){
        log.info("status,id====>{}{}",status,ids);
       dishService.updateStatus(status,ids);
        return R.success("修改菜品在售状态成功");
    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping()
    public R<String> delete(@RequestParam List<Long> ids){
        dishService.deleteDishWithFlavor(ids);
        return  R.success("删除菜品成功！");
    }
}
