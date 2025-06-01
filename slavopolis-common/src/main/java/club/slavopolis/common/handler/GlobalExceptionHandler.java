package club.slavopolis.common.handler;

import club.slavopolis.common.constant.CommonConstants;
import club.slavopolis.common.enums.ResultCode;
import club.slavopolis.common.exception.BusinessException;
import club.slavopolis.common.exception.SystemException;
import club.slavopolis.common.exception.ThirdPartyException;
import club.slavopolis.common.exception.ValidationException;
import club.slavopolis.common.response.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * slavopolis-boot
 *
 * @author slavopolis
 * @date: 2025/6/1
 * @description: 全局异常处理器, 统一处理所有Controller层抛出的异常
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Object> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常: {} - {}", e.getCode(), e.getMessage());
        return buildErrorResult(e.getCode(), e.getMessage(), e.getData(), request);
    }

    /**
     * 处理系统异常
     */
    @ExceptionHandler(SystemException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Object> handleSystemException(SystemException e, HttpServletRequest request) {
        log.error("系统异常: {} - {}", e.getCode(), e.getMessage(), e);
        return buildErrorResult(e.getCode(), e.getMessage(), null, request);
    }

    /**
     * 处理验证异常
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleValidationException(ValidationException e, HttpServletRequest request) {
        log.warn("验证异常: {}", e.getMessage());
        return buildErrorResult(e.getCode(), e.getMessage(), e.getFieldErrors(), request);
    }

    /**
     * 处理第三方服务异常
     */
    @ExceptionHandler(ThirdPartyException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Result<Object> handleThirdPartyException(ThirdPartyException e, HttpServletRequest request) {
        log.error("第三方服务异常: [{}] {}", e.getServiceName(), e.getMessage(), e);
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("service", e.getServiceName());
        errorData.put("thirdPartyCode", e.getThirdPartyCode());
        errorData.put("thirdPartyMessage", e.getThirdPartyMessage());
        return buildErrorResult(e.getCode(), e.getMessage(), errorData, request);
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.warn("方法参数绑定异常: {}", e.getMessage());
        Map<String, String> errors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value",
                        (existing, replacement) -> existing
                ));
        return buildErrorResult(ResultCode.PARAMETER_ERROR.getCode(), "参数验证失败", errors, request);
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleBindException(BindException e, HttpServletRequest request) {
        log.warn("参数绑定异常: {}", e.getMessage());
        String message = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(CommonConstants.COMMA));
        return buildErrorResult(ResultCode.PARAMETER_ERROR.getCode(), message, null, request);
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        log.warn("约束违反异常: {}", e.getMessage());
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String message = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(CommonConstants.COMMA));
        return buildErrorResult(ResultCode.PARAMETER_ERROR.getCode(), message, null, request);
    }

    /**
     * 处理请求参数缺失异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleMissingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
        log.warn("请求参数缺失异常: {}", e.getParameterName());
        String message = String.format("缺少必要参数: %s", e.getParameterName());
        return buildErrorResult(ResultCode.PARAMETER_ERROR.getCode(), message, null, request);
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.warn("参数类型不匹配异常: {}", e.getMessage());
        String message = String.format("参数类型错误: %s", e.getName());
        return buildErrorResult(ResultCode.PARAMETER_ERROR.getCode(), message, null, request);
    }

    /**
     * 处理HTTP消息不可读异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.warn("HTTP消息不可读异常: {}", e.getMessage());
        return buildErrorResult(ResultCode.BAD_REQUEST.getCode(), "请求体格式错误", null, request);
    }

    /**
     * 处理请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<Object> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.warn("请求方法不支持异常: {}", e.getMethod());
        String message = String.format("不支持%s请求方法", e.getMethod());
        return buildErrorResult(ResultCode.METHOD_NOT_ALLOWED.getCode(), message, null, request);
    }

    /**
     * 处理媒体类型不支持异常
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public Result<Object> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e, HttpServletRequest request) {
        log.warn("媒体类型不支持异常: {}", e.getContentType());
        return buildErrorResult(ResultCode.NOT_ACCEPTABLE.getCode(), "不支持的媒体类型", null, request);
    }

    /**
     * 处理文件上传大小超限异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public Result<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e, HttpServletRequest request) {
        log.warn("文件上传大小超限异常: {}", e.getMaxUploadSize());
        return buildErrorResult(ResultCode.BAD_REQUEST.getCode(), "文件大小超过限制", null, request);
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Object> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.warn("404异常: {} {}", e.getHttpMethod(), e.getRequestURL());
        return buildErrorResult(ResultCode.NOT_FOUND.getCode(), "请求的资源不存在", null, request);
    }

    /**
     * 处理其他未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Object> handleException(Exception e, HttpServletRequest request) {
        log.error("未捕获的异常", e);
        // 生产环境不暴露具体错误信息
        String message = "服务器内部错误，请稍后重试";
        return buildErrorResult(ResultCode.INTERNAL_SERVER_ERROR.getCode(), message, null, request);
    }

    /**
     * 构建错误响应
     */
    private Result<Object> buildErrorResult(int code, String message, Object data, HttpServletRequest request) {
        Result<Object> result = new Result<>(code, message, data);

        // 设置追踪ID
        String traceId = MDC.get(CommonConstants.TRACE_ID);
        if (traceId == null) {
            traceId = request.getHeader(CommonConstants.TRACE_ID);
        }
        if (traceId != null) {
            result.setTraceId(traceId);
        }

        // 记录请求信息
        log.info("Error response - Path: {}, Method: {}, Code: {}, Message: {}",
                request.getRequestURI(), request.getMethod(), code, message);

        return result;
    }
}
