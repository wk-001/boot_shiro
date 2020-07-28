package com.wk.exception;

import com.wk.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * SpringSecurity相关异常
	 */
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler(value = AccessDeniedException.class)
	public Result<String> handleUnauthorizedException(AccessDeniedException e) {
		log.error("权限不足：{}", e.getMessage());
		return Result.fail(403,"权限不足："+e.getMessage(),null);
	}

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(value = AccountExpiredException.class)
	public Result<String> handleUnauthenticatedException(AccountExpiredException e) {
		log.error("认证失败：{}", e.getMessage());
		return Result.fail(401,"认证失败："+e.getMessage(),null);
	}

	/**
	 * 处理Assert的异常
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = IllegalArgumentException.class)
	public Result<String> handler(IllegalArgumentException e) throws IOException {
		log.error("Assert异常：{}",e.getMessage());
		return Result.fail("Assert异常："+e.getMessage());
	}

	/**
	 * @Validated 校验错误异常处理
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public Result<String> handler(MethodArgumentNotValidException e) throws IOException {
		log.error("实体校验异常：{}",e.getMessage());
		BindingResult bindingResult = e.getBindingResult();
		ObjectError objectError = bindingResult.getAllErrors().stream().findFirst().get();
		return Result.fail("实体校验异常："+objectError.getDefaultMessage());
	}

	/**
	 * 普通异常
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)	//返回状态码
	@ExceptionHandler(RuntimeException.class)	//要捕获的异常
	public Result<String> handler(RuntimeException e){
		log.error("运行时异常：{}",e.getMessage());
		return Result.fail("运行时异常："+e.getMessage());
	}

}
