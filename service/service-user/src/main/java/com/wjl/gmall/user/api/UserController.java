package com.wjl.gmall.user.api;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/27
 * @description
 */

import com.wjl.gmall.user.model.entity.UserAddress;
import com.wjl.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/user/inner")
public class UserController {

   @Autowired
   private UserService userService;

   /**
    *  获取用户地址列表
    * @param userId
    * @return
    */
   @GetMapping("/findUserAddressListByUserId/{userId}")
   public List<UserAddress> findUserAddressListByUserId(@PathVariable("userId") Long userId){
      return userService.findUserAddressList(userId);
   }


}
