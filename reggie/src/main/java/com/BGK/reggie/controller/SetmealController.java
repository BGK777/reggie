package com.BGK.reggie.controller;

import com.BGK.reggie.common.R;
import com.BGK.reggie.dto.SetmealDto;
import com.BGK.reggie.pojo.Category;
import com.BGK.reggie.pojo.Setmeal;
import com.BGK.reggie.pojo.SetmealDish;
import com.BGK.reggie.service.CategoryService;
import com.BGK.reggie.service.SetmealDishService;
import com.BGK.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐和相关联菜品信息
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐详情===>{}", setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    public R<Page<SetmealDto>> page(Integer page, Integer pageSize, String name) {
        //分页构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> SetmealDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //添加条件
        lambdaQueryWrapper.eq(name != null, Setmeal::getName, name);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        //执行查询
        setmealService.page(pageInfo, lambdaQueryWrapper);

        //pageInfo拷贝基本信息给SetmealDtoPage，除了records
        BeanUtils.copyProperties(pageInfo, SetmealDtoPage, "records");

        //取出records，为DtoRecords赋值且为其中每一个套餐赋上套餐分类名称
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> DtoRecords = new ArrayList<>();
        for (Setmeal setmeal : records) {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);

            Long categoryId = setmeal.getCategoryId();

            Category category = categoryService.getById(categoryId);
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }
            DtoRecords.add(setmealDto);
        }
        SetmealDtoPage.setRecords(DtoRecords);
        return R.success(SetmealDtoPage);
    }

    /**
     * 删除套餐和相关联菜品信息
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        setmealService.deleteWithDish(ids);
        return R.success("删除套餐成功");
    }

    /**
     * 修改套餐在售状态
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable("status") Integer status, @RequestParam List<Long> ids) {
        log.info("status,id====>{}{}", status, ids);
        setmealService.updateStatus(status, ids);
        return R.success("修改套餐在售状态成功");
    }

    /**
     * 根据id获取套餐SetmealDto信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getOneById(@PathVariable("id") Long id) {
        SetmealDto setmealDto = setmealService.getOneSetmealById(id);
        return R.success(setmealDto);
    }

    /**
     * 修改套餐信息
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> edit(@RequestBody SetmealDto setmealDto){

        if (setmealDto==null){
            return R.error("请求异常");
        }

        if (setmealDto.getSetmealDishes()==null){
            return R.error("套餐没有菜品,请添加套餐");
        }
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        Long setmealId = setmealDto.getId();

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealId);
        setmealDishService.remove(queryWrapper);

        //为setmeal_dish表填充相关的属性
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }
        //批量把setmealDish保存到setmeal_dish表
        setmealDishService.saveBatch(setmealDishes);
        setmealService.updateById(setmealDto);

        return R.success("套餐修改成功");
    }

    /**
     * 根据套餐分类id获取套餐列表
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        //条件构造器
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //添加条件
        lambdaQueryWrapper.eq(setmeal.getCategoryId()!= null,Setmeal::getCategoryId,setmeal.getCategoryId());
        lambdaQueryWrapper.orderByAsc(Setmeal::getUpdateTime);
        //添加查询，status为1（起售）的套餐
        lambdaQueryWrapper.eq(Setmeal::getStatus,1);
        //执行查询
        List<Setmeal> list = setmealService.list(lambdaQueryWrapper);
        return R.success(list);
    }
}
