package com.BGK.reggie.service;

import com.BGK.reggie.pojo.Category;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CategoryService extends IService<Category> {
    /**
     * 删除分类信息
     * @param id
     * @return
     */
    boolean remove(Long id);
}
