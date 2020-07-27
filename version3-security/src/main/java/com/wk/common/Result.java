package com.wk.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class Result<T> implements Serializable {

	/**
	 * 响应状态码
	 */
	private Integer code;

	/**
	 * 提示消息
	 */
	private String msg;

	/**
	 * 响应数据
	 */
	private T data;

	/**
	 * 响应成功状态码
	 */
	private static final int SUCCESS_CODE = 200;

	/**
	 * 响应成功信息
	 */
	private static final String SUCCESS_MESSAGE = "操作成功";

	/**
	 * 响应失败状态码
	 */
	private static final int FAIL_CODE = 400;

	public static<T> Result<T> ok() {
		return ok(SUCCESS_CODE, SUCCESS_MESSAGE, null);
	}

	public static<T> Result<T> ok(T data) {
		return ok(SUCCESS_CODE, SUCCESS_MESSAGE, data);
	}

	public static<T> Result<T> ok(int code, String msg, T data) {
		Result<T> r = new Result<T>();
		r.setCode(code);
		r.setMsg(msg);
		r.setData(data);
		return r;
	}

	public static<T> Result<T> fail(String msg) {
		return fail(FAIL_CODE, msg, null);
	}

	public static<T> Result<T> fail(int code, String msg) {
		return fail(code, msg, null);
	}

	public static<T> Result<T> fail(String msg, T data) {
		return fail(FAIL_CODE, msg, data);
	}

	public static<T> Result<T> fail(int code, String msg, T data) {
		Result<T> r = new Result<T>();
		r.setCode(code);
		r.setMsg(msg);
		r.setData(data);
		return r;
	}
}
