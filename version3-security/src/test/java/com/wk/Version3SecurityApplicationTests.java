package com.wk;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class Version3SecurityApplicationTests {

	@Test
	void contextLoads() {
		String encode = new BCryptPasswordEncoder().encode("1");
		System.out.println("encode = " + encode);
	}

}
