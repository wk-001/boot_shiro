package com.wk.security;

import com.wk.common.Result;
import com.wk.dto.LoginUser;
import com.wk.utils.ResponseUtil;
import com.wk.utils.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SecurityHandler {

	@Autowired
	private TokenService tokenService;


	/**
	 * 未登录，返回401
	 */
	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return new AuthenticationEntryPoint() {

			@Override
			public void commence(HttpServletRequest request, HttpServletResponse response,
								 AuthenticationException exception) throws IOException, ServletException {
				String msg;
				if (exception instanceof BadCredentialsException) {
					msg = "账号或密码错误";
				} else {
					msg = "认证失败，无法访问系统资源："+exception.getMessage();
				}
				Result<Object> fail = Result.fail(HttpStatus.UNAUTHORIZED.value(), msg);
				ResponseUtil.responseJson(response, HttpStatus.UNAUTHORIZED.value(), fail);
			}
		};
	}

	/**
	 * 退出处理
	 */
	@Bean
	public LogoutSuccessHandler logoutSussHandler() {
		return new LogoutSuccessHandler() {

			@Override
			public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
										Authentication authentication) throws IOException, ServletException {
				LoginUser loginUser = tokenService.getLoginUser(request);
				tokenService.delLoginUser(loginUser.getToken());

				Result<Object> ok = Result.ok("退出成功");
				ResponseUtil.responseJson(response, HttpStatus.OK.value(), ok);
			}
		};

	}

}
