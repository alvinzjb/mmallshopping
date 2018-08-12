package com.njust.service.Impl;

import com.njust.common.Const;
import com.njust.common.ServerResponse;
import com.njust.common.TokenCache;
import com.njust.mapper.UserMapper;
import com.njust.pojo.User;
import com.njust.service.IUserService;
import com.njust.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService{
    @Autowired
    private UserMapper userMapper;
    @Override
    public ServerResponse<User> selectByUsernameAndPassword(String username,String password) {
   int count = userMapper.selectByUsername(username);
        if(count==0){
            //既没有查到相应的用户名
            ServerResponse.createByErrorMessage("您输入的用户名不存在!");
        }
        //继续进行查询
        //加密,因为数据库中存储的是MD5进行加密的，
        // 此处查询也应该先进行加密再进行查询
        String md5password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectByUsernameAndPassword(username,md5password);
        if(user==null){
            //既没有查到相应的用户
            ServerResponse.createByErrorMessage("您输入的密码不正确!");
        }
        //此处即表明用户存在
        //user.setPassword("");//将密码清空

        ServerResponse<User> bySuccess = ServerResponse.createBySuccess("登录成功!" ,user);
          return bySuccess;
    }
        //进行用户注册,还要判断是使用邮箱注册还是使用用户名注册
         //进行用户名校验
        public  ServerResponse<String> register(User user){
        //进行用户名注册
        ServerResponse<String> valid = this.isValid(user.getUsername(), Const.USERNAME);
        if(!valid.isSuccess()){
            //即没有成功
           return valid;
        }
         //进行用户邮箱验证
        ServerResponse<String> valid1 = this.isValid(user.getEmail(), Const.EMAIL);
        if(!valid1.isSuccess()){
            return valid1;
        }

        //表明都不存在即可进行注册,根据user进行注册
        //首先应该对用户密码进行加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        //设置用户级别
        user.setRole(Const.Role.ROLE_CUSTOMER);
        int insert = userMapper.insert(user);
        if(insert==0){
            //表明注册失败，像前端提供信息进行显示
            ServerResponse.createByErrorMessage("注册失败");
        }
      //注册成功，也向前端提供信息
       return  ServerResponse.createBySuccessMessage("注册成功");

    }
       //封装一个通用的验证方法
    public ServerResponse<String> isValid(String str ,String type){
        if(org.apache.commons.lang3.StringUtils.isNotBlank(type)){
            if(Const.USERNAME.equals(type)) {
                //即通过用户名进行验证
                int count = userMapper.selectByUsername(str);
                if (count > 0) {
                    //既查到相应的用户名
                    return ServerResponse.createByErrorMessage("您输入的用户名已存在!");
                }
            }
                if(Const.EMAIL.equals(type)){
                    int email = userMapper.selectByEmail(str);
                    if(email>0){
                        //既查到相应的用户名
                     return   ServerResponse.createByErrorMessage("您输入的邮箱已存在!");
                    }

                }
            }else{
            return ServerResponse.createByErrorMessage("参数错误!");
        }
        return ServerResponse.createBySuccessMessage("验证成功!");
    }


    //忘记密码进行用户问题的获取
    public ServerResponse<String> forgetQuestion(String username){
        //首先要判断用户名是否存在
        ServerResponse<String> valid = this.isValid(username, Const.USERNAME);
        if(valid.isSuccess()){
            //即表明用户不存在
            return ServerResponse.createByErrorMessage("你输入的用户名不存在!");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(question==null){
            //即没有获得相应的问题
            return ServerResponse.createByErrorMessage("没有获得相应的问题");
        }
        return ServerResponse.createBySuccess(question);
    }

    //提交答案进行验证
    public ServerResponse<String> forgetByAnswer(String username,String question,String answer){
        int count = userMapper.selectByUsernameQuestionAnswer(username, question, answer);
          if(count>0){
              //表明输入密码正确，产生一个token送回前端
              String token = UUID.randomUUID().toString();
             TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,token);
           return  ServerResponse.createBySuccess("验证成功",token);
          }
         return ServerResponse.createByErrorMessage("答案错误!");
    }

    //进行用户密码的重置
    public ServerResponse<String> resetPassword(String username,String newpassword,String token) {
        if (org.apache.commons.lang3.StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("参数错误,token需要获取");
        }
         ServerResponse<String> valid = this.isValid(username, Const.USERNAME);
        if(valid.isSuccess()){
            //即表明用户不存在
            return ServerResponse.createByErrorMessage("你输入的用户名不存在!");
        }
            //即在用户设置密码不为空的情况下才可以进行修改
            //获取token,与缓存中进行比较
        String forgottoken = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if(org.apache.commons.lang3.StringUtils.isBlank(forgottoken)) {
           return  ServerResponse.createByErrorMessage("token无效或过期");
        }
        if (org.apache.commons.lang3.StringUtils.equals(forgottoken, token)) {
                //两个token相等表明输入正确
                //对密码进行MD5编码
                String md5Password = MD5Util.MD5EncodeUtf8(newpassword);
                int count = userMapper.updatePasswordByUsername(username, md5Password);
                if (count > 0) {
                    //表明用户更改密码成功
                  return   ServerResponse.createBySuccessMessage("密码修改成功!");
                } else {
                    //表明用户更改密码失败
                   return  ServerResponse.createByErrorMessage("token错误，请重新获取token!");
                }
            }
            return ServerResponse.createByErrorMessage("密码修改失败!");
    }


    //进行登录状态下的密码重置
    public ServerResponse<String> onloadRestPassword(String oldpassword,String newpassword,User user){
        //防止横向越权,要校验一下这个用户的旧密码,一定要指定是这个用户.因为我们会查询一个count(1),如果不指定id,那么结果就是true啦count>0;
        //首先要对输入的密码进行加密才能进行数据库校验
         String MD5oldpassword = MD5Util.MD5EncodeUtf8(oldpassword);
        int count = userMapper.checkPassword(MD5oldpassword,user.getId());
        //输出结果为0，
      System.out.println(count);
        if(count==0){
            //表明旧密码不正确
             return  ServerResponse.createByErrorMessage("旧密码不正确");
        }
            user.setPassword(MD5Util.MD5EncodeUtf8(newpassword));
            int result = userMapper.updateByPrimaryKeySelective(user);
            if(result==0){
               return  ServerResponse.createByErrorMessage("密码更新失败");
            }
             return ServerResponse.createBySuccessMessage("密码更新成功");
        }


        //进行用户信息的更新
      public  ServerResponse<User> updateUserInfo(User user){
          //首先要进行用户邮箱的验证若邮箱数据库中
          // 存在且不是本人则表示邮箱已注册不能使用
          //进行邮箱的验证
          int count = userMapper.selectEmail(user.getId(), user.getEmail());
          if(count>0){
              //表明邮箱已经存在，不能进行更改
             return  ServerResponse.createByErrorMessage("邮箱已经存在，请重新输入");
          }
          //进行数据的封装
          User updateUser = new User();
          updateUser.setId(user.getId());
          updateUser.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
          updateUser.setAnswer(user.getAnswer());
          updateUser.setQuestion(user.getQuestion());
          updateUser.setPhone(user.getPhone());
          int result = userMapper.updateByPrimaryKey(user);
          if(result==0){
              //表明更新不成功
            return ServerResponse.createByErrorMessage("更新失败");
          }
          return   ServerResponse.createBySuccess("更新用户数据成功",updateUser);
        }

        //进行用户信息的获取
       public ServerResponse<User>  selectInfoById(Integer userId){
           User user = userMapper.selectByPrimaryKey(userId);
           if(user==null){
             return   ServerResponse.createByErrorMessage("获取用户信息失败");
           }
           //将密码置空
           user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
            return ServerResponse.createBySuccess(user);
       }

}
