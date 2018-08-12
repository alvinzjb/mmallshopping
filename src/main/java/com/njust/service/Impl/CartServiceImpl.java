package com.njust.service.Impl;

import com.njust.common.Const;
import com.njust.common.ResponseCode;
import com.njust.common.ServerResponse;
import com.njust.mapper.CartMapper;
import com.njust.mapper.ProductMapper;
import com.njust.pojo.Cart;
import com.njust.pojo.Product;
import com.njust.service.ICartService;
import com.njust.utils.BigDecimalUtil;
import com.njust.utils.PropertiesUtil;
import com.njust.vo.CartProductVo;
import com.njust.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Override
    public ServerResponse<CartVo> add(Integer userId, Integer productId,Integer count) {
      //进行业务的书写
        if(productId==null || count ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
       //参数没有问题的情况下,进行信息的查询,查询购物车
         Cart cart = cartMapper.selectProductCount(userId,productId);
        if(cart==null){
            //表明购物车内没有该商品,进行添加
            Cart cartItem =  new Cart();
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setQuantity(count);
             cartMapper.insertSelective(cartItem);
        }else{
            //表明该商品购物车内是有的需要对商品信息进行更改
            count = cart.getQuantity()+count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }

      return ServerResponse.createBySuccess(this.CartVoLimit(userId));
    }

     //封装一个方法
    public CartVo CartVoLimit(Integer userId){
        //根据用户id进行信息的查询封装
        //1.首先根据用户id进行购物车的查询
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = new ArrayList<>();
        CartVo cartVo = new CartVo();
        BigDecimal cartTotalPrice = new BigDecimal("0");
        //查询到用户购物车的信息,进行遍历数据的封装
        for (Cart cartItem: cartList) {
            CartProductVo cartProductVo = new CartProductVo();
            //进行数据的封装
            cartProductVo.setId(cartItem.getId());
            cartProductVo.setUserId(cartItem.getUserId());
            cartProductVo.setProductId(cartItem.getProductId());
            //根据productId可以查出该商品的详细信息
            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
           if(product!=null) {
               //查询到该商品的详细信息,继续进行信息的封装
               cartProductVo.setProductStatus(product.getStatus());
               cartProductVo.setProductName(product.getName());
               cartProductVo.setProductPrice(product.getPrice());
               cartProductVo.setProductMainImage(product.getMainImage());
               cartProductVo.setProductSubtitle(product.getSubtitle());
               cartProductVo.setProductStock(product.getStock());
               /**
                *  private Integer productChecked;//此商品是否勾选
                private BigDecimal productTotalPrice;//购物车商品数量与价格的积
                已做 private Integer quantity;//购物车中此商品的数量,要与库存进行比较才能进行封装
                已做 private String limitQuantity;//限制数量的一个返回结果
                还有这些信息没有进行封装
                */
               //首先进行此购物车商品数量和限制数量的一个结果返回
               int limitQuantity=0;
               if(product.getStock()>cartItem.getQuantity()){
                   //库存大于购买的数量
                   limitQuantity=cartItem.getQuantity();
                   cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
               }else{
                   //库存数量小于商品的购买数量,要对商品的购买数量进行自动更改
                   Cart cartforQuantity = new Cart();
                   cartforQuantity.setId(cartItem.getId());
                   cartforQuantity.setQuantity(product.getStock());
                   cartMapper.updateByPrimaryKeySelective(cartforQuantity);
                   limitQuantity=product.getStock();
                   cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
               }
               cartProductVo.setQuantity(limitQuantity);
               //进行购物车商品总价的封装
               cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(cartProductVo.getProductPrice().doubleValue(),cartProductVo.getQuantity().doubleValue()));
               //最后进行判断该商品是否进行勾选
               cartProductVo.setProductChecked(cartItem.getChecked());
           }

            //产品的相关信息已经封装好
            if(cartItem.getChecked()==Const.Cart.CHECKED){
                cartTotalPrice= BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
            }
            cartProductVoList.add(cartProductVo);
        }


        //进行购物车信息的封装
        /**
         *  private List<CartProductVo>  cartProductVoList;
            private BigDecimal cartTotalPrice;
            private boolean allChecked;
            private String imgHost;
         */
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setImgHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setAllChecked(this.isAllChecked(userId));
        return  cartVo;
    }

    //再封装一个方法
    private boolean isAllChecked(Integer userId){
        boolean ischecked = false ;
        ischecked =(cartMapper.selectCartProductCheckedStatusByUserId(userId)==0);
        return ischecked;
    }
}
