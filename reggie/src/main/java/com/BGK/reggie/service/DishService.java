package com.BGK.reggie.service;

import com.BGK.reggie.dto.DishDto;
import com.BGK.reggie.pojo.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface DishService extends IService<Dish> {

    void saveWithFlavor(DishDto dishDto);

    /**
     * 根据id获取菜品信息和对应得口味
     * @param id
     * @return
     */
    DishDto getDisnWithFlavorById(Long id);

    void updateWithFlavor(DishDto dishDto);

    void updateStatus(Integer status, List<Long> ids);

    void deleteDishWithFlavor(List<Long> ids);
}
