package com.changgou.web.order.controller;

import com.changgou.entity.Result;
import com.changgou.entity.UnrateResult;
import com.changgou.order.feign.CartFeign;
import com.changgou.order.feign.OrderFeign;
import com.changgou.order.pojo.Order;
import com.changgou.order.pojo.OrderItem;
import com.changgou.user.feign.AddressFeign;
import com.changgou.user.pojo.Address;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/worder")
public class OrderController {

    @Autowired
    private AddressFeign addressFeign;

    @Autowired
    private CartFeign cartFeign;



    @RequestMapping("/ready/order")
    public String readyOrder(Model model){
        //收件人的地址信息
        List<Address> addressList = addressFeign.list().getData();
        model.addAttribute("address",addressList);

        //购物车信息
        Map map = cartFeign.list();
        List<OrderItem> orderItemList = (List<OrderItem>) map.get("orderItemList");
        Integer totalMoney = (Integer) map.get("totalMoney");
        Integer totalNum = (Integer) map.get("totalNum");

        model.addAttribute("carts",orderItemList);
        model.addAttribute("totalMoney",totalMoney);
        model.addAttribute("totalNum",totalNum);

        //默认收件人信息
        for (Address address : addressList) {
            if ("1".equals(address.getIsDefault())){
                //默认收件人
                model.addAttribute("deAddr",address);
                break;
            }
        }
        return "order";
    }

    @Autowired
    private OrderFeign orderFeign;

    @PostMapping("/add")
    @ResponseBody
    public Result add(@RequestBody Order order){
        Result result = orderFeign.add(order);
        return result;
    }

    @GetMapping("/toPayPage")
    public String toPayPage(String orderId,Model model){
        //获取到订单的相关信息
        Order order = orderFeign.findById(orderId).getData();
        model.addAttribute("orderId",orderId);
        model.addAttribute("payMoney",order.getPayMoney());
        return "pay";
    }


    //根据用户查询所有订单
    @GetMapping("/allOrder")
    public String findAllOrder(Model model){
        List<Map<String,Object>> orders = (List<Map<String,Object>>)orderFeign.findAllOrder().getData();

        model.addAttribute("orders",orders);
        return "center-index";
    }

    @GetMapping("/unshippeDorder")
    public String findUnshippedOrder(Model model) {
        Result result = orderFeign.findUnshippedOrder();
        ArrayList<OrderItem> data = (ArrayList<OrderItem>) result.getData();
        model.addAttribute( "orderItems", data );
        return "center-order-send";
    }

    @GetMapping("/unreceivedOrder")
    public String findUnreceivedOrder(Model model) {
        Result result = orderFeign.findUnreceivedOrder();
        ArrayList<OrderItem> data = (ArrayList<OrderItem>) result.getData();
        model.addAttribute( "orderItems", data );
        return "center-order-receive";
    }

    @GetMapping("/unrateOrder/{currentPage}/{pageSize}")
    public String findUnrateOrder(Model model) {
        Result result = orderFeign.findUnreceivedOrder();
        UnrateResult<OrderItem> unrateResult = (UnrateResult<OrderItem>)result.getData();
        Long total = unrateResult.getTotal();
        List<HashMap<String, Object>> data = unrateResult.getRows();
        model.addAttribute( "orderList", data );
        model.addAttribute( "total", total );
        return "center-order-evaluate";
    }



    //待付款
    @GetMapping("/waitPay")
    public String waitPay(Model model){
        List<Map<String, Object>> orderList = (List<Map<String, Object>>) orderFeign.waitPay().getData();
        model.addAttribute("orderList",orderList);
        return "center-order-pay";
    }

    //取消订单
    @GetMapping("/cancel")
    public String cancelOrder(String orderId){
        orderFeign.cancelOrder(orderId);
        return "center-index";
    }

}

