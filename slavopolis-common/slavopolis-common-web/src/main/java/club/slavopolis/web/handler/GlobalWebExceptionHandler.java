package club.slavopolis.web.handler;

import club.slavopolis.base.enums.ResponseCode;
import club.slavopolis.base.exception.BizException;
import club.slavopolis.base.exception.SystemException;
import club.slavopolis.web.vo.Result;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

/**
 * Web 全局异常处理
 *
 * @author slavopolis
 * @version 1.0.0
 * @since 2025/7/2
 * <p>
 * Copyright (c) 2025 slavopolis-cloud-initializr
 * All rights reserved.
 */
@ControllerAdvice
public class GlobalWebExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalWebExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.warn("参数验证失败: {}", ex.getMessage());
        Map<String, String> errors = Maps.newHashMapWithExpectedSize(1);
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result exceptionHandler(BizException bizException) {
        logger.error("业务异常: {}", bizException.getMessage());

        Result result = new Result();
        result.setCode(bizException.getErrorCode().getCode());

        if (bizException.getMessage() == null) {
            result.setMessage(bizException.getErrorCode().getMessage());
        } else {
            result.setMessage(bizException.getMessage());
        }

        result.setSuccess(false);
        return result;
    }

    @ExceptionHandler(SystemException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result systemExceptionHandler(SystemException systemException) {
        logger.error("系统异常: {}", systemException.getMessage());
        Result result = new Result();
        result.setCode(systemException.getErrorCode().getCode());
        if (systemException.getMessage() == null) {
            result.setMessage(systemException.getErrorCode().getMessage());
        } else {
            result.setMessage(systemException.getMessage());
        }

        result.setSuccess(false);
        return result;
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result throwableHandler(Throwable throwable) {
        logger.error("系统异常: {}", throwable.getMessage());
        Result result = new Result();
        result.setCode(ResponseCode.SYSTEM_ERROR.getCode());
        result.setMessage(ResponseCode.SYSTEM_ERROR.getMessage());
        result.setSuccess(false);
        return result;
    }
}
