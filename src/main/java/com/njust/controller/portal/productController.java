package com.njust.controller.portal;


import com.github.pagehelper.PageInfo;
import com.njust.common.ServerResponse;
import com.njust.service.IProductService;
import com.njust.vo.ProductDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/product")
public class productController {
    @Autowired
    private IProductService iProductService;
    //进行商品详情展示，首先要查明商品的在线状态，
    // 如果商品已经下架则不显示商品，或者给出一个错误信息
    //根据商品Id进行商品的详细信息查询,
    @RequestMapping(value="/productDetail.do",method= RequestMethod.POST)
    @ResponseBody
    public ServerResponse<ProductDetailVO> productDetail(@RequestParam("productId") Integer productId){
        //由于是对用户进行展示的所以无需进行管理员身份验证
         //但增加了一个商品上下架状态的判断
        return  iProductService.showProductDetail(productId);
    }


    //客户端进行商品的查询,也是需要进行分页的
    @RequestMapping(value="/productList.do",method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> productList(@RequestParam(value="pageNum",defaultValue = "1") Integer pageNum,
                                                @RequestParam(value="pageSize",defaultValue = "10")Integer pageSize,
                                                @RequestParam(value="keyword",required = false) String keyword,
                                                @RequestParam(value="categoryId",required = false)Integer categoryId,
                                                @RequestParam(value="orderBy",required = false) String orderBy){

                  //进行商品的查询
          return  iProductService.searchProductList(pageNum,pageSize,keyword,categoryId,orderBy);

    }

}
