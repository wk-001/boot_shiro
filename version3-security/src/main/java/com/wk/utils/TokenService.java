package com.wk.utils;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.wk.common.RedisConstant;
import com.wk.common.Result;
import com.wk.dto.LoginUser;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class TokenService {

	// 令牌自定义标识
	@Value("${token.header}")
	private String header;

	// 令牌秘钥
	@Value("${token.secret}")
	private String secret;

	// 令牌有效期（默认30分钟）
	@Value("${token.expireTime}")
	private int expireTime;

	private static final Long MILLIS_MINUTE_TEN = 10 * 60 * 1000L;

	@Autowired
	private StringRedisTemplate redisTemplate;

	public LoginUser getLoginUser(HttpServletRequest request) {
		//从请求头中获取token
		String token = request.getHeader(header);
		if (StringUtils.isNotBlank(token)) {
			//解析token，获取token载荷中的uuid
			String uuid = parseToken(token);
			String loginUserStr = redisTemplate.boundValueOps(getTokenKey(uuid)).get();
			return JSON.parseObject(loginUserStr, LoginUser.class);
		}
		return null;
	}

	/**
	 * 从令牌中获取载荷中存放的数据
	 */
	private String parseToken(String token){
		try {
			Map<String, Object> jwtClaims  = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
			//获取载荷中的uuid
			return jwtClaims.get(RedisConstant.LOGIN_TOKEN).toString();
		} catch (ExpiredJwtException e) {
			log.error("token:{}已过期", token);
			e.printStackTrace();
		}   catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 存放用户信息的key
	 */
	private String getTokenKey(String uuid) {
		return RedisConstant.LOGIN_USER + uuid;
	}

	/**
	 * 验证令牌有效期，相差不足10分钟，自动刷新缓存
	 */
	public void verifyToken(LoginUser loginUser)
	{
		long expireTime = loginUser.getExpireTime();
		long currentTime = System.currentTimeMillis();
		if (expireTime - currentTime <= MILLIS_MINUTE_TEN)
			refreshToken(loginUser);
	}

	/**
	 * 刷新令牌有效期
	 *
	 * @param loginUser 登录信息
	 */
	public void refreshToken(LoginUser loginUser){
		long now = System.currentTimeMillis();
		loginUser.setLoginTime(now);
		loginUser.setExpireTime(now + expireTime * 1000);
		// 根据uuid将loginUser缓存
		String userKey = getTokenKey(loginUser.getToken());
		redisTemplate.boundValueOps(userKey).set(JSON.toJSONString(loginUser),expireTime,TimeUnit.SECONDS);
	}

	/**
	 * 删除用户身份信息
	 */
	public void delLoginUser(String token) {
		if (StringUtils.isNotEmpty(token)) {
			String userKey = getTokenKey(token);
			redisTemplate.delete(userKey);
		}
	}

	/**
	 * 创建令牌
	 */
	public String createToken(LoginUser loginUser) {
		String token = IdUtil.fastSimpleUUID();
		loginUser.setToken(token);
		refreshToken(loginUser);

		Map<String, Object> claims = new HashMap<>();
		claims.put(RedisConstant.LOGIN_TOKEN, token);

		return Jwts.builder()
				.setClaims(claims)
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

}
