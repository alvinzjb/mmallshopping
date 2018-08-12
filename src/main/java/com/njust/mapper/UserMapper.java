package com.njust.mapper;

import com.njust.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    //新添加的功能
    int selectByUsername(@Param("username") String username);
    int selectByEmail(@Param("email") String email);
    User selectByUsernameAndPassword(@Param("username") String username,@Param("password")String password);
    String  selectQuestionByUsername(@Param("username")String username);
   int selectByUsernameQuestionAnswer(@Param("username")String username,@Param("question")String question,@Param("answer")String answer);
   int updatePasswordByUsername(@Param("username") String username,@Param("newpassword") String newpassword);

    int checkPassword(@Param("oldpassword") String oldpassword,@Param("userId") Integer userId);
    int selectEmail(@Param("userId")Integer userId,@Param("email")String email);

}