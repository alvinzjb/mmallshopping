package com.njust.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.njust.common.Const;
import com.njust.common.ServerResponse;
import com.njust.pojo.Product;
import com.njust.pojo.User;
import com.njust.service.IFileService;
import com.njust.service.IProductService;
import com.njust.utils.PropertiesUtil;
import com.njust.vo.ProductDetailVO;
import com.njust.vo.productListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/manager/product")
public class ProductManageController {
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;
    //后台商品管理模块
    //1.保存商品
    @RequestMapping(value = "/save.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> saveProduct(HttpSession session, Product product) {
        System.out.println(product.getId());
        //首先判断用户是不是管理员的身份
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        // System.out.println( user.getRole());
        if (user == null) {
            //表明用户没有登录
            return ServerResponse.createByErrorMessage("请先进行登录!");
        }
        //表明用户已经进行登录,现判断是否为管理员的身份
        if (user.getRole() != Const.Role.ROLE_ADMIN) {
            //表明用户不是管理员，不能进行相应的操作
            return ServerResponse.createByErrorMessage("您不是管理员，没有相应的权限");
        }
        //表明为管理员的身份,进行相应的业务的操作,进行商品的保存
        return iProductService.saveProduct(product);
    }

    //产品的上下架
    @RequestMapping(value = "/sale_state.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> setProductStatus(HttpSession session, Integer productId, Integer productState) {
        //同样只有管理员才能进行操作，所以此处同样要进行管理员的验证
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            //表明用户没有登录
            return ServerResponse.createByErrorMessage("请先进行登录!");
        }
        //表明用户已经进行登录,现判断是否为管理员的身份
        if (user.getRole() != Const.Role.ROLE_ADMIN) {
            //表明用户不是管理员，不能进行相应的操作
            return ServerResponse.createByErrorMessage("您不是管理员，没有相应的权限");
        }
        //表明为管理员的身份,进行相应的业务的操作,进行商品的上下架

        return iProductService.setProductStatus(productId, productState);
    }

    //获取商品的详情
    @RequestMapping("/get_product_detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVO> getProductDetails(HttpSession session, Integer productId) {
        //进行商品详细信息的查询,需要是管理员的身份
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            //表明用户没有登录
            return ServerResponse.createByErrorMessage("请先进行登录!");
        }
        //表明用户已经进行登录,现判断是否为管理员的身份
        if (user.getRole() != Const.Role.ROLE_ADMIN) {
            //表明用户不是管理员，不能进行相应的操作
            return ServerResponse.createByErrorMessage("您不是管理员，没有相应的权限");
        }
        //表明为管理员的身份,进行相应的业务的操作,进行商品详细信息的查询,
        // 因为可能点的是图片，可能点的是名字，所以用product进行前端信息的接受??
        return iProductService.getProductDetails(productId);
    }


    //后台商品展示，需要进行分页
    @RequestMapping(value="/productList.do",method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo>  productList(HttpSession session, @RequestParam(value="pageNum" ,defaultValue = "1",required = false) Integer pageNum, @RequestParam(value="pageSize" ,defaultValue = "10",required=false) Integer pageSize){
       //同样首先要进行管理员身份的验证
        //获取当前登录用户
       User user = (User) session.getAttribute(Const.CURRENT_USER);
       if(user==null){
           //表明用户没有登录
           return ServerResponse.createByErrorMessage("您还没有登录！");
       }
       //表明用户已经登录，现要看用户是否为管理员
        if(!user.getRole().equals(Const.Role.ROLE_ADMIN)){
           //表明用户身份不为管理员
            return ServerResponse.createByErrorMessage("您不是管理员，没有相应的操作权限！");
        }
           //表明用户身份为管理员的身份刻印进行相应的业务操作
        return  iProductService.showProductList(pageNum,pageSize);

    }
    //进行商品的搜索,根据商品名称，商品id进行商品的搜索
    @RequestMapping(value="searchProductList.do",method=RequestMethod.POST)
    @ResponseBody
    public  ServerResponse<PageInfo> seachProduct(HttpSession session ,@RequestParam(value="pageNum" ,defaultValue = "1") Integer pageNum,@RequestParam(value="pageSize" ,defaultValue = "10")Integer pageSize,@RequestParam(value="productName",required = false) String productName,@RequestParam(value="productId",required = false) Integer productId){
      //同样要进行用户权限的判断
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            //表明用户没有登录
            return ServerResponse.createByErrorMessage("您还没有登录！");
        }
        //表明用户已经登录，现要看用户是否为管理员
        if(!user.getRole().equals(Const.Role.ROLE_ADMIN)){
            //表明用户身份不为管理员
            return ServerResponse.createByErrorMessage("您不是管理员，没有相应的操作权限！");
        }
         //表明用户身份为管理员的身份刻印进行相应的业务操作
         //在service层进行相关信息的查询
        return iProductService.searchProductInfo(pageNum,pageSize,productName,productId);
    }


    //文件上传
    @RequestMapping("/onload.do")
    @ResponseBody
    public ServerResponse fileUpload(HttpSession session, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request){
        //由于还是后台管理，所以还需要进行管理员身份的验证
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            //表明用户没有登录
            return ServerResponse.createByErrorMessage("您还没有登录！");
        }
        //表明用户已经登录，现要看用户是否为管理员
        if(!user.getRole().equals(Const.Role.ROLE_ADMIN)){
            //表明用户身份不为管理员
            return ServerResponse.createByErrorMessage("您不是管理员，没有相应的操作权限！");
        }
        //表明用户身份为管理员的身份刻印进行相应的业务操作,文件上传
         //相当于新建一个临时文件夹，与index.jsp同级
        String path = request.getSession().getServletContext().getRealPath("onload");
        String targetFileName = iFileService.upload(file,path);
        //用于前端进行显示的url
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;

        Map fileMap = Maps.newHashMap();
        //返回给前端的uri和url
        fileMap.put("uri",targetFileName);
        fileMap.put("url",url);
        return ServerResponse.createBySuccess(fileMap);

    }
    //富文本上传,不知道在说什么，等查阅资料以后再写

}
