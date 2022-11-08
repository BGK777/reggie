package com.BGK.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 添加已存在的信息异常处理方法
     * @param ex
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry")){
            String[] msgs = ex.getMessage().split(" ");
            String msg = msgs[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    /**
     * 自定义异常处理方法
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomExpception.class)
    public R<String> customExpception(CustomExpception ex){
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }
}
