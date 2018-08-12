package com.njust.mapper;

import com.njust.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);
    List<Product> selctproductInfo();

   List<Product> serchProductList(@Param("productName") String productName, @Param("productId") Integer productId);

    List<Product>  selectproductList(@Param("keyword") String keyword,@Param("categoryIdList")List<Integer> categoryIdList);

}