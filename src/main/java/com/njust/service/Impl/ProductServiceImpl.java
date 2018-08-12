package com.njust.service.Impl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.njust.common.Const;
import com.njust.common.ResponseCode;
import com.njust.common.ServerResponse;
import com.njust.mapper.CategoryMapper;
import com.njust.mapper.ProductMapper;
import com.njust.pojo.Category;
import com.njust.pojo.Product;
import com.njust.service.ICategoryService;
import com.njust.service.IProductService;
import com.njust.utils.DateTimeUtil;
import com.njust.utils.PropertiesUtil;
import com.njust.vo.ProductDetailVO;
import com.njust.vo.productListVO;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;
    @Override
    public ServerResponse<String> saveProduct(Product product) {
        //进行商品的保存与更新
        if(product==null){
            //非法参数
            return ServerResponse.createByErrorMessage("新增或更新商品参数不正确!");
        }
        //进行相应的更新或新增商品,首先判断是否有子图
        if(StringUtils.isNotBlank(product.getSubImages())){
            String[] imageArray = product.getSubImages().split(",");
                    //将第一个图设为主图
            if(imageArray.length>0){
                product.setMainImage(imageArray[0]);
            }
        }
        //进行商品的更新
        if(product.getId()!=null) {
            //表明是进行商品的更新
            int count = productMapper.updateByPrimaryKeySelective(product);
            if (count > 0) {
                //表明商品更新成功
                return ServerResponse.createBySuccessMessage("更新商品成功!");
            }
            return ServerResponse.createByErrorMessage("更新商品失败!");

        }
        //表明进行商品新增
        int count = productMapper.insertSelective(product);
          if(count>0){
              return ServerResponse.createBySuccessMessage("新增商品成功!");
          }
        return ServerResponse.createByErrorMessage("新增商品失败!");
    }

    @Override
    public ServerResponse<String> setProductStatus(Integer productId, Integer productState) {
        //进行商品上下架的设置
        if(productId==null||productState==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //进行商品的上下架
        Product product = new Product();
        product.setId(productId);
        product.setStatus(productState);
        int count = productMapper.updateByPrimaryKeySelective(product);
        if(count>0){
            return ServerResponse.createBySuccessMessage("产品销售状态设置成功!");
        }
        return ServerResponse.createBySuccessMessage("产品销售状态设置失败!");
    }
     //进行商品详细信息的查询
    @Override
    public ServerResponse<ProductDetailVO> getProductDetails(Integer productId) {
        if(productId==null){
            ServerResponse.createByErrorMessage("参数错误!");
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product==null){
            ServerResponse.createByErrorMessage("产品已下架或删除!");
        }
        ProductDetailVO productDetailVO = this.assembleProductDetailInfo(product);
        if(productDetailVO==null){
            return ServerResponse.createByErrorMessage("获取详细信息失败!");
        }
        return ServerResponse.createBySuccess(productDetailVO);
    }

    //首先封装一个方法
    public  ProductDetailVO assembleProductDetailInfo(Product product){
        //进行商品信息的封装

        ProductDetailVO productDetailVO  = new ProductDetailVO();
        productDetailVO.setId(product.getId());
        productDetailVO.setCategorytId(product.getCategoryId());
        productDetailVO.setName(product.getName());
        productDetailVO.setDetail(product.getDetail());
        productDetailVO.setStatus(product.getStatus());
        productDetailVO.setPrice(product.getPrice());
        productDetailVO.setMainImage(product.getMainImage());
        productDetailVO.setSubImages(product.getSubImages());
        productDetailVO.setSubtitle(product.getSubtitle());
        productDetailVO.setStock(product.getStock());
        //parentCategoryId
        //createTime
        //updateTime
        //imageHost
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category.getParentId()==null){
            //默认设置为根节点、
            productDetailVO.setParentCategoryId(0);
        }
        productDetailVO.setParentCategoryId(category.getParentId());
        productDetailVO.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVO.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        productDetailVO.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));//不知是什么,再仔细看下视频
        return  productDetailVO;
    }

    //进行商品的展示,
    // 由于不是所有的信息进行显示，
    // 所以进行信息的封装，
    // 再进行信息的显示
    @Override
    public ServerResponse< PageInfo> showProductList(Integer pageNum, Integer pageSize) {
        //startPage--start
         PageHelper.startPage(pageNum, pageSize);
        //填充自己的sql语句
        List<productListVO> showProductList =new ArrayList<>();
        List<Product> productlist =productMapper.selctproductInfo();
        for (Product product:productlist
             ) {
            productListVO productlistVO = this.assembleproductList(product);
            showProductList.add(productlistVO);
        }
        //根据查询的结果进行相应的计算
        PageInfo pageInfo =new PageInfo(productlist);
        //pageHeler收尾
        //这才是需要显示的内容
        pageInfo.setList(showProductList);

        return ServerResponse.createBySuccess(pageInfo);
    }

  //进行商品的查询，再进行展示
    @Override
    public ServerResponse<PageInfo> searchProductInfo(Integer pageNum, Integer pageSize, String oldproductName, Integer productId) {
        //还是一样的操作
        //startpage--start
        //进行自己的sql语句编写
        //pageHelper进行收尾
        PageHelper.startPage(pageNum,pageSize);
        List<productListVO> searchProductlist = new ArrayList<>();
        //应该进行模糊搜索
        String productName = new StringBuilder().append("%").append(oldproductName).append("%").toString();
        List<Product> productList= productMapper.serchProductList(productName, productId);
        for (Product product:productList) {
            productListVO productlistVO = this.assembleproductList(product);
            searchProductlist.add(productlistVO);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(searchProductlist);
        return ServerResponse.createBySuccess(pageInfo);
    }


    //封装一个可用的方法，进行数据的封装
    public productListVO assembleproductList(Product product){
          productListVO productlistVO = new productListVO();
          productlistVO.setId(product.getId());
          productlistVO.setCategoryId(product.getCategoryId());
          productlistVO.setSubtitle(product.getSubtitle());
          productlistVO.setName(product.getName());
          productlistVO.setMainImage(product.getMainImage());
          productlistVO.setStatus(product.getStatus());
          productlistVO.setPrice(product.getPrice());
           return productlistVO;
    }

    //客户端商品详情显示
    public ServerResponse<ProductDetailVO> showProductDetail(Integer productId){
        if(productId==null){
            ServerResponse.createByErrorMessage("参数错误!");
        }
        //首先根据商品的id进行商品的查询
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product==null){
           return  ServerResponse.createByErrorMessage("产品已下架或删除!");
        }
        //进行商品上下架状态进行判断
        if(!product.getStatus().equals(Const.ProductStatusEnum.ON_SALE.getCode())){
            return ServerResponse.createByErrorMessage("商品已下架，无法获取商品详情!");
        }
        ProductDetailVO productDetailVO = assembleProductDetailInfo(product);
        return ServerResponse.createBySuccess(productDetailVO);
    }
    //进行客户端山商品信息的展示
    @Override
    public ServerResponse<PageInfo> searchProductList(Integer pageNum, Integer pageSize, String keyword, Integer categoryId,String orderBy) {
       //首先进行参数判断
        if(StringUtils.isBlank(keyword) &&categoryId==null){
       return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList = new ArrayList<>();
        List<productListVO> productListVOList = new ArrayList<>();
      if(categoryId !=null) {
          //表明可以进行商品分类的查询

          Category category = categoryMapper.selectByPrimaryKey(categoryId);
          if (category == null && org.apache.commons.lang3.StringUtils.isBlank(keyword)) {
              //表明没有该商品的分类，返回一个空集合但不报错,也是需要显示分页信息
              PageHelper.startPage(pageNum, pageSize);
              List<productListVO> productListVoList = Lists.newArrayList();
              PageInfo pageInfo = new PageInfo(productListVoList);
              //由于返回的是空集合，所以无需设置集合内容
              return ServerResponse.createBySuccess(pageInfo);
          }
          //表明categoryId不为空
          //进行递归子查询查询所有的categoryId
          ServerResponse<List<Integer>> categoryAndDeepCategory = iCategoryService.getCategoryAndDeepCategory(categoryId);
          categoryIdList = categoryAndDeepCategory.getData();
      }
          if(StringUtils.isNotBlank(keyword)) {
              keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
          }
              //进行商品信息的查询,根据keyword模糊查询，以及分类categoryId
        List<Product> productList = productMapper.selectproductList(org.apache.commons.lang3.StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size()==0?null:categoryIdList);
             PageHelper.startPage(pageNum,pageSize);
             if(StringUtils.isNotBlank(orderBy)){
                  if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                      //分页插件进行排序的方式为：important
                      //如使用价格进行排序,字段为price
                      //PageHelper.orderBy(price desc);表明通过价格的降序进行排列,中间是有一个空格的
                      //orderBy的格式应为：price_desc或者为price_asc，实质上是按价格进行排序
                 String[] orderByArray = orderBy.split("_");
                 PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
                  }
              }
              for (Product product:productList
                      ) {
                  productListVO productlistVO = assembleproductList(product);
                  productListVOList.add(productlistVO);
              }
                PageInfo pageInfo = new PageInfo(productList);
                pageInfo.setList(productListVOList);

        return ServerResponse.createBySuccess(pageInfo);
    }


}
