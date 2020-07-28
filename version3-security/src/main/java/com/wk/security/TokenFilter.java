package com.wk.security;

import com.wk.dto.LoginUser;
import com.wk.utils.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class TokenFilter extends OncePerRequestFilter {

	@Autowired
	private TokenService tokenService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		//从请求头中获取token，再根据token到Redis中获取对应的用户信息
		LoginUser loginUser = tokenService.getLoginUser(request);
		if (loginUser != null) {
			//验证令牌有效期，如果有效期不足10分钟则刷新有效期到30分钟
			tokenService.verifyToken(loginUser);

			//将用户信息和自定义的权限信息放入到全局token中
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
			authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		}
		//如果请求头中没有token则判断是不是可以匿名访问访问的路径，如果不是则报错
		filterChain.doFilter(request, response);
	}
}
