package com.njust.service;

import com.njust.common.ServerResponse;
import com.njust.vo.CartVo;

public interface ICartService {
    //进行购物车商品的添加
    ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count);
}
