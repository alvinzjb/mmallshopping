package com.njust.service;

import com.github.pagehelper.PageInfo;
import com.njust.common.ServerResponse;
import com.njust.pojo.Product;
import com.njust.vo.ProductDetailVO;
import com.njust.vo.productListVO;

public interface IProductService {
    //进行商品的更新或增加
   ServerResponse<String> saveProduct(Product product);
   //进行商品的上下架
    ServerResponse<String> setProductStatus(Integer productId,Integer productState);
    //进行商品详细信息的查询
    ServerResponse<ProductDetailVO> getProductDetails(Integer productId);

    //进行商品的展示
    ServerResponse<PageInfo> showProductList(Integer pageNum, Integer pageSize);

    //进行商品信息的查询
    ServerResponse<PageInfo> searchProductInfo(Integer pageNum,Integer pageSize,String oldproductName,Integer productId);


    //客户端

    ServerResponse<ProductDetailVO> showProductDetail(Integer productId);
    ServerResponse<PageInfo>  searchProductList(Integer pageNum,Integer pageSize,String keyword,Integer categoryId,String orderBy);
}
