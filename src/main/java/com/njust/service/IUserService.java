package com.njust.service;

import com.njust.common.ServerResponse;
import com.njust.pojo.User;

public interface IUserService {
   ServerResponse<User> selectByUsernameAndPassword(String username, String password);
   ServerResponse<String> register(User user);
   ServerResponse<String> isValid(String str ,String type);
   ServerResponse<String> forgetQuestion(String username);
   ServerResponse<String> forgetByAnswer(String username,String question,String answer);
   ServerResponse<String> resetPassword(String username,String newpassword,String token);
   ServerResponse<String> onloadRestPassword(String oldpassword,String newpassword,User user);
   ServerResponse<User> updateUserInfo(User user);
   ServerResponse<User> selectInfoById(Integer userId);
}
