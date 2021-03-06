package com.wk.controller;

import com.wk.common.Result;
import com.wk.entity.User;
import com.wk.service.ResourceService;
import com.wk.service.RoleService;
import com.wk.shiro.ShiroUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

	@Autowired
	private RoleService roleService;

	@Autowired
	private ResourceService resourceService;

	@PostMapping("login")
	public Result<User> login(@Validated @RequestBody User user){

		UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(),user.getPassword());

		try {
			SecurityUtils.getSubject().login(token);
			User userInfo = ShiroUtils.getUserInfo();
			//返回前端需要的用户数据
			userInfo.setToken(ShiroUtils.getSession().getId().toString());
			return Result.ok(userInfo);
		} catch (IncorrectCredentialsException e) {
			e.printStackTrace();
			return Result.fail("账号或密码错误!");
		}  catch (AuthenticationException e) {
			e.printStackTrace();
			return Result.fail("认证失败！原因："+e.getMessage());
		}
	}

	@GetMapping("unLogin")
	public Result<Void> unLogin(){
		return Result.fail("未登录！");
	}

	@RequiresAuthentication
	@GetMapping("logout")
	public void logout(){
		ShiroUtils.logout();
	}
}
