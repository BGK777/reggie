package com.BGK.reggie.service;

import com.BGK.reggie.dto.SetmealDto;
import com.BGK.reggie.pojo.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void updateStatus(Integer status, List<Long> ids);

    void saveWithDish(SetmealDto setmealDto);

    void deleteWithDish(List<Long> ids);

    SetmealDto getOneSetmealById(Long id);
}
