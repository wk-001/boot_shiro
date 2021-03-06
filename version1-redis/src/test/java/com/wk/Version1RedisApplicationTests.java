package com.wk;

import com.wk.entity.User;
import com.wk.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Version1RedisApplicationTests {

	@Autowired
	private UserService userService;

	@Test
	public void contextLoads() {
		User user = userService.getById(1);
		System.out.println("user = " + user);
	}

}
