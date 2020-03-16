package com.example.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @Description
 * @Author chenduo
 * @Date 2020/3/16 9:12
 **/
@RestController
@RequestMapping("/mqtt")
@Slf4j
public class MqttController {
//	认证执行顺序为  Authentication request —> Superuser request —> ACL request

	/**
	 * 当设备连接服务器的时候，设备连接权限校验
	 * @param clientid
	 * @param username
	 * @param password
	 * @param response
	 * @return ACL失败，API 返回4xx
	 *         ACL成功，API 返回200
	 */
	@PostMapping("/auth")
	public void auth(@RequestParam(value = "clientid")String clientid,
					 @RequestParam(value = "username")String username,
					 @RequestParam(value = "password")String password, HttpServletResponse response){
		log.info("auth,clientid:{},username:{},password:{}",clientid,username,password);
		response.setStatus(HttpStatus.OK.value());
	}

	/**
	 * 验证是否为超级用户
	 * @param clientid
	 * @param username
	 * @param response
	 * @return ACL失败，API 返回4xx
	 *         ACL成功，API 返回200
	 */
	@PostMapping("/superuser")
	public void superuser(@RequestParam(value = "clientid")String clientid,
						 @RequestParam(value = "username")String username,HttpServletResponse response){
		if (username.equals("test")){
			log.info("superuser PASS,clientid:{},username:{}",clientid,username);
			response.setStatus(HttpStatus.OK.value());
		}else {
			log.info("superuser DENY,clientid:{},username:{}",clientid,username);
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
		}
	}

//	当设备订阅主题，亦或是发布内容的时候 会执行ACL request，需要注意的是，如果Superuser request认证通过，则该处不执行，默认通过

	/**
	 * 设备订阅主题、发布内容时执行，当设备为超级用户时 不执行
	 * @param access
	 * @param username
	 * @param ipaddr
	 * @param topic
	 * @param mountpoint
	 * @param response
	 * @return ACL失败，API 返回4xx
	 *         ACL成功，API 返回200
	 */
	@GetMapping("/acl")
	public void acl(@RequestParam(value = "access")String access,
				   @RequestParam(value = "username")String username,
				   @RequestParam(value = "ipaddr")String ipaddr,
				   @RequestParam(value = "topic")String topic,
				   @RequestParam(value = "mountpoint")String mountpoint,HttpServletResponse response){
		if (!ipaddr.equals("127.0.0.1")){
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			log.info("acl DENY,access:{},username:{},ipaddr:{},topic:{},ipaddr:{}",access,username,ipaddr,topic,ipaddr);
		}else {
			log.info("acl PASS,access:{},username:{},ipaddr:{},topic:{},ipaddr:{}",access,username,ipaddr,topic,ipaddr);
			response.setStatus(HttpStatus.OK.value());
		}
	}
}
