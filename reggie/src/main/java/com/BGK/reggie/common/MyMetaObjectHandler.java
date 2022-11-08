package com.BGK.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 元数据对象处理器,填充公共字段
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
        metaObject.setValue("updateTime", LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {

        Long id = Thread.currentThread().getId();
        log.info("线程id===={}",id);

        metaObject.setValue("updateUser", BaseContext.getCurrentId());
        metaObject.setValue("updateTime", LocalDateTime.now());
    }
}
