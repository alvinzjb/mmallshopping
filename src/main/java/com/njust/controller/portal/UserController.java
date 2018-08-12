package com.njust.controller.portal;

import com.njust.common.Const;
import com.njust.common.ResponseCode;
import com.njust.common.ServerResponse;
import com.njust.pojo.User;
import com.njust.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
    //进行自动注入
    @Autowired
    private IUserService iUserService;
    @RequestMapping(value="/login.do", method = RequestMethod.POST)
    @ResponseBody
    public  ServerResponse<User>  selectByUsernameAndPassword(String username, String password, HttpSession session){
        ServerResponse<User> userServerResponse = iUserService.selectByUsernameAndPassword(username, password);
         //放入session域中
          if(userServerResponse.isSuccess()){
              session.setAttribute(Const.CURRENT_USER,userServerResponse.getData());
          }
        return userServerResponse;
    }
    //进行用户的登出
    @RequestMapping(value="/logout.do", method = RequestMethod.POST)
    @ResponseBody
       public  ServerResponse<String> logout(HttpSession session){
               session.removeAttribute(Const.CURRENT_USER);
               ServerResponse<String> bySuccessMessage = ServerResponse.createBySuccessMessage("登出成功!");
               return bySuccessMessage;
       }

      //进行新用户注册
  @RequestMapping(value="/register.do", method = RequestMethod.POST)
  @ResponseBody
     public ServerResponse<String> register(User user){
      return  iUserService.register(user);
  }

  //根据用户是选择用户名还是邮箱进行注册进行判断
  @RequestMapping(value="/check_valid.do", method = RequestMethod.POST)
  @ResponseBody
    public ServerResponse<String> isValid(String str ,String type){
           return iUserService.isValid(str, type);
  }
      //获取用户登录信息
      @RequestMapping(value="/get_user_info.do", method = RequestMethod.POST)
      @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        //获取当前登录用户的信息
       User user = (User) session.getAttribute(Const.CURRENT_USER);
       if(user==null){
           return ServerResponse.createByErrorMessage("获取用户信息失败!");
       }
         return  ServerResponse.createBySuccess(user);
    }

    //忘记密码进行用户问题的获取
    @RequestMapping(value="/get_user_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetRequestion(String username){
        return  iUserService.forgetQuestion(username);
    }

    //提交答案，获取token放入guava缓存中
    @RequestMapping(value="/user_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetByAnswer(String username,String question,String answer){
        return  iUserService.forgetByAnswer(username,question,answer);
    }

    //进行token验证进行用户密码的重置
    @RequestMapping(value="/re_set_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(String username,String newpassword,String token){
      return  iUserService.resetPassword(username,newpassword,token);
    }

    //登录状态下的重置密码
    @RequestMapping(value="/onload_set_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> onloadResetPassword(HttpSession session ,String oldpassword,String newpassword){
          //获取当前登录用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
           return  ServerResponse.createByErrorMessage("用户未登录");
        }
         //用户登录的情况
        return  iUserService.onloadRestPassword(oldpassword,newpassword,user);
    }

    //更新用户信息,user用来封装修改后的数据
    @RequestMapping(value="/update_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateUserInfo(HttpSession session,User user){
            //获取当前用户
            User currentUser =(User) session.getAttribute(Const.CURRENT_USER);
            if(currentUser==null){
                ServerResponse.createByErrorMessage("用户未登录");
            }
           //进行用户信息的更改,保持用户的id以及用户名保持不变,并将用户信息放入session中
              user.setId(currentUser.getId());//保持用户id不变
              user.setUsername(currentUser.getUsername());//保持用户名称不变
          //将封装好的数据传递给service层进行下昂行的数据处理
            ServerResponse<User> userServerResponse = iUserService.updateUserInfo(user);
             if(userServerResponse.isSuccess()){
             //表明更新数据成功，放回session
               userServerResponse.getData().setUsername(currentUser.getUsername());
               session.setAttribute(Const.CURRENT_USER, userServerResponse.getData());
         }
          return  userServerResponse ;
    }


        //获取用户的详细信息
        @RequestMapping(value="/get_user_detailInfo.do", method = RequestMethod.POST)
        @ResponseBody
      public ServerResponse<User> getUserDetailInfo(HttpSession session) {
        //如果用户么没有登录则进行强制登录
          User currentuser = (User) session.getAttribute(Const.CURRENT_USER);
          if(currentuser==null){
              //进行用户的强制登录
              return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录status=10");
          }

           return  iUserService.selectInfoById(currentuser.getId());
    }
}
