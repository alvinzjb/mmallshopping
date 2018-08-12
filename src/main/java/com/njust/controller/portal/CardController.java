package com.njust.controller.portal;

import com.njust.common.Const;
import com.njust.common.ResponseCode;
import com.njust.common.ServerResponse;
import com.njust.pojo.User;
import com.njust.service.ICartService;
import com.njust.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CardController {
    //购物车模块,首先进行购物车的添加
    @Autowired
    private ICartService iCartService;
    @RequestMapping(value="/add.do",method= RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> add(HttpSession session, @RequestParam(value ="count",defaultValue = "1")  Integer count,@RequestParam(value ="productId")Integer productId ){
        //首先判断用户是否登录
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            //即用户没有登陆
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //用户已登录,进行相应的业务处理
        //获取用户id

        return iCartService.add(user.getId(),productId,count);
    }


}
