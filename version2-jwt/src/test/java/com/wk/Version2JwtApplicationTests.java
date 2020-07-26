package com.wk;

import com.wk.common.RedisConstant;
import com.wk.domain.User;
import com.wk.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Version2JwtApplicationTests {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private UserService userService;

	@Test
	public void contextLoads() {

		Long expire = redisTemplate.boundValueOps(RedisConstant.USER_INFO + 1).getExpire();
		System.out.println("expire = " + expire);

	}

}
