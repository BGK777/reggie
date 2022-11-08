package com.BGK.reggie.controller;

import com.BGK.reggie.common.R;
import com.BGK.reggie.pojo.Category;
import com.BGK.reggie.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        System.out.println(category);
        categoryService.save(category);
        return  R.success("添加分类成功");
    }

    /**
     * 分类信息分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page<Category>> page(Integer page,Integer pageSize){
        //分页构造器
        Page<Category> pageIfo = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件，按sort升序排序
        lambdaQueryWrapper.orderByAsc(Category::getSort);
        //执行查询并返回
        categoryService.page(pageIfo);
        return R.success(pageIfo);
    }

    /**
     * 删除分类信息
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        //    categoryService.removeById(ids);
        categoryService.remove(ids);
        return  R.success("分类信息删除成功");
    }

    /**
     * 修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }

    /**
     * 获取菜品列表
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //创建条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //添加条件
        lambdaQueryWrapper.eq(category.getType()!= null,Category::getType,category.getType());
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByAsc(Category::getCreateTime);

        return R.success(categoryService.list(lambdaQueryWrapper));
    }
}
