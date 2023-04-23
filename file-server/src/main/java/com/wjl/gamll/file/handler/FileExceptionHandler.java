package com.wjl.gamll.file.handler;

import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.common.result.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
@Slf4j
@RestControllerAdvice
public class FileExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result fail(Exception e){
        log.error("文件系统异常：{}",e.getMessage(),e);
        return Result.build(null, ResultCodeEnum.FAIL);
    }

}
