package com.njust.controller.backend;

import com.njust.common.Const;
import com.njust.common.ServerResponse;
import com.njust.pojo.Category;
import com.njust.pojo.User;
import com.njust.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/manager/category")
public class ManagerCategoryController {
    //分类管理模块
    //1.增加分类
    /**1.首先要进行用户是否登录进行验证
     * 2.进行用户校验，看登录用户是否为管理员身份
     */
    @Autowired
    private ICategoryService iCategoryService;
    @RequestMapping(value="/addcategory.do" ,method= RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> addcategory(HttpSession session, @RequestParam(value ="parentId",defaultValue = "0") int parentId, String categoryName ){
          //@Requestparam主要是进行参数限定的默认值设置为0为根节点,
          // @ResponseBody主要用于jason对象序列化
         //默认值为0表明为根节点
         //获取用户
        User user  = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            //表明用户没有进行登录
            return ServerResponse.createByErrorMessage("您还没有登录!");
        }
        //表明用户存在，需要判断用户是否为管理员身份
        if(user.getRole().equals(Const.Role.ROLE_ADMIN)){
            //表明用户是管理员的身份,可以进行相应的操作，增加分类
            return iCategoryService.addcategory(parentId,categoryName);
        }
        //表明不是管理员的身份
        return ServerResponse.createByErrorMessage("您没有相应的权限!");
    }

    //更新categoryName的接口
    @RequestMapping(value="/updatecategoryName.do" ,method= RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> updateCategoryName(HttpSession session,String categoryName,@RequestParam(value="categoryId",defaultValue = "0") Integer categoryId){

        User user  = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            //表明用户没有进行登录
            return ServerResponse.createByErrorMessage("您还没有登录!");
        }
        //表明用户存在，需要判断用户是否为管理员身份
        if(user.getRole().equals(Const.Role.ROLE_ADMIN)){
            //表明用户是管理员的身份,可以进行相应的操作，进行categoryName的更改,调用service层即可
            return iCategoryService.updateCategoryName(categoryName, categoryId);
        }
        //表明不是管理员的身份
        return ServerResponse.createByErrorMessage("您没有相应的权限!");
    }


    //获取子节点的所有信息,且不通过递归保持平级
    @RequestMapping(value="/get_child_categoryInfo.do" ,method= RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Category>> getAllChildCategoryInfo(HttpSession session ,@RequestParam(value ="parentId",defaultValue = "0") Integer parentId){

        User user  = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            //表明用户没有进行登录
            return ServerResponse.createByErrorMessage("您还没有登录!");
        }
        //表明用户存在，需要判断用户是否为管理员身份
        if(user.getRole().equals(Const.Role.ROLE_ADMIN)){
            //表明用户是管理员的身份,可以进行相应的操作，获取子节点的所有信息
            //根据父节点获取所有的子节点
             return iCategoryService.getAllChildCategoryInfo(parentId);
        }
        //表明不是管理员的身份
        return ServerResponse.createByErrorMessage("您没有相应的权限!");
    }
    //获取当前categoryId并且递归查询某子节点的categoryId
             //递归就是自己调用自己
            //0->1000->10000,若当前id为0则获得其子节点1000，并获得子节点的子节点10000
           //若当前子节点为1000则获得其子节点为10000而且还要去重
    @RequestMapping(value="/get_andDeepId.do" ,method= RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Integer>> getCategoryAndDeepCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
             //categoryId表示当前的category
        User user  = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            //表明用户没有进行登录
            return ServerResponse.createByErrorMessage("您还没有登录!");
        }
        //表明用户存在，需要判断用户是否为管理员身份
        if(user.getRole().equals(Const.Role.ROLE_ADMIN)){
            //表明用户是管理员的身份,可以进行相应的操作，获取子节点的所有信息
            //根据父节点获取所有的子节点
            return iCategoryService.getCategoryAndDeepCategory(categoryId);
        }
        //表明不是管理员的身份
        return ServerResponse.createByErrorMessage("您没有相应的权限!");

    }

}
