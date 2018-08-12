package com.njust.service.Impl;

import com.njust.common.ServerResponse;
import com.njust.mapper.CategoryMapper;
import com.njust.pojo.Category;
import com.njust.service.ICategoryService;
import com.njust.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;


@Service("iCategoryService")
public class CategotryServiceImpl implements ICategoryService {
   private Logger logger = LoggerFactory.getLogger(CategotryServiceImpl.class);
 //进行userService注入
    @Autowired
    private IUserService iUserService;
    @Autowired
    private CategoryMapper categoryMapper;
    public ServerResponse<String>  addcategory(Integer parentId,String categoryName ){
        //增加相应的分类信息,首先要判断id和name是否为空
        // 其中任意一个参数为空的话表明参数有误,无法进行分类添加
        if( parentId!=null || StringUtils.isNotBlank(categoryName)) {
            //进行数据的封装
            return ServerResponse.createByErrorMessage("参数错误!");
        }
            Category category = new Category();
            category.setId(parentId);
            category.setName(categoryName);
            category.setStatus(true);//表明该分类是可用的
            //根据id进行商品的分类的添加
            int count = categoryMapper.insertSelective(category);
            if(count>0){
                //表明分类添加成功
                return ServerResponse.createBySuccessMessage("添加分类成功!");
            }

             return ServerResponse.createByErrorMessage("添加分类失败!");
        }


        //进行categoryName的更改
      public ServerResponse<String>  updateCategoryName(String categoryName,Integer categoryId){
        //根据主键进行categoryName的更改
          if(categoryId ==null||StringUtils.isBlank(categoryName)){
              //可以进行categoryName的修改
              return ServerResponse.createByErrorMessage("参数错误!");
          }
              Category category = new Category();
              category.setName(categoryName);
              category.setId(categoryId);
              int count = categoryMapper.updateByPrimaryKeySelective(category);
              if(count>0){
                  //表明更新名称成功
                  return ServerResponse.createBySuccessMessage("更新名称成功!");
              }
              //表明跟心名字不成功
              return  ServerResponse.createByErrorMessage("更新名称失败!");
          }


      //获取子节点的所有信息
      public ServerResponse<List<Category>> getAllChildCategoryInfo(Integer parentId){
          if(parentId!=null){
              //表明父节点存在进行子节点的查询
              List<Category> categories = categoryMapper.selectChildCategoryInfo(parentId);
              if(CollectionUtils.isEmpty(categories)){
                  //CollectionUtils.isEmpty(categories)不仅判断里面是不是空的而且判断其是不是一个空的集合
                  //此处不反回一个错误的信息，而是打印日志,
                   logger.info("未能找到当前类的分类!");

              }
              return ServerResponse.createBySuccess(categories);
          }
              return ServerResponse.createByErrorMessage("参数错误!");
      }

      //获取当前categoryId并且递归查询其子节点的categoryId
      public ServerResponse<List<Integer>> getCategoryAndDeepCategory(Integer categoryId){
       //进行集合的声明，用来装递归查询的数据
          if(categoryId!=null){
              Set<Category> categorySet = new HashSet<Category>();
              List<Integer> categoryIdList = new ArrayList<>();
              this.getChildCategoryId(categorySet,categoryId);
              //获取set集合里的category的id
              for (Category category:categorySet){
                  categoryIdList.add(category.getId());//获取所有的categoryId
              }
              return  ServerResponse.createBySuccess(categoryIdList) ;//将查询到的id集合进行返回
          }
          return ServerResponse.createByErrorMessage("参数错误!");
      }

      //封装一个递归查询的方法
    //其中set是用来将查询的子节点进行保存的
     public Set<Category> getChildCategoryId(Set<Category> categorySet ,Integer categoryId){
        //首先获取当前的Category
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category!=null){
            //表明当前分类存在,将当前分类放入集合当中
            categorySet.add(category);
        }
        //进行当前分类的子节点查询
        List<Category> categories = categoryMapper.selectChildCategoryInfo(categoryId);
        //进行遍历获取下一个子节点
        for ( Category categoryItem:categories){
            //子节点调用调用递归方法查询下一级子节点的查询
           this.getChildCategoryId(categorySet,categoryItem.getId());
        }
        return categorySet;
    }

}
