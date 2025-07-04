package gzx.zxge.controller;

import gzx.zxge.pojo.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult> handleException(Exception e) {
        if (ApiResult.CODE_TOKEN_LOSE.equals(e.getMessage())){
            return ResponseEntity.status(HttpStatus.OK).body(ApiResult.fail("登录信息失效").setData(ApiResult.CODE_TOKEN_LOSE));
        }
        log.error("发生异常",e);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.fail(e.getMessage()));
    }
}
