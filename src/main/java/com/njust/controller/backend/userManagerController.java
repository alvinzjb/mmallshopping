package com.njust.controller.backend;

import com.njust.common.Const;
import com.njust.common.ServerResponse;
import com.njust.pojo.User;
import com.njust.service.IUserService;
import com.njust.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manager/user")
public class userManagerController {
    //后台管理员登录
    @Autowired
    private IUserService iUserService;
    @RequestMapping(value = "/managerLogin.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User>  login(HttpSession session,String username, String password){

        ServerResponse<User> userServerResponse = iUserService.selectByUsernameAndPassword(username,password);
       if(userServerResponse.isSuccess()){
           if(userServerResponse.getData().getRole().equals(Const.Role.ROLE_ADMIN)) {
               //将用户信息放入session中
               User user = userServerResponse.getData();
               session.setAttribute(Const.CURRENT_USER,user);
               return  userServerResponse;//将数据返回给前端
           }else{
               //表明用户不是管理员身份不可以进行登录
               ServerResponse.createByErrorMessage("您不是管理员");
           }
       }

        return  userServerResponse;
    }

}
