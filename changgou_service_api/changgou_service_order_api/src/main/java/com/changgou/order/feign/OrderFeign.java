package com.changgou.order.feign;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.order.pojo.Order;
import com.changgou.order.pojo.OrderItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@FeignClient(name = "order")
public interface OrderFeign {

    @PostMapping("/order")
    public Result add(@RequestBody Order order);

    @GetMapping("/order/{id}")
    public Result<Order> findById(@PathVariable("id") String id);

    //根据用户查询所有订单
    @PostMapping("/order/allOrder")
    public Result findAllOrder();

    //待付款
    @GetMapping("/order/waitPay")
    public Result waitPay();

    //取消订单
    @GetMapping("/order/cancel")
    public Result cancelOrder(String orderId);

    @GetMapping("/order/collet")
    public Result findCollet();

    @PutMapping("/order/collet/{skuId}")
    public Result collet(@PathVariable("skuId") String skuId);

    @GetMapping("/order/unshippeDorder")
    public Result findUnshippedOrder();

    @GetMapping("/order/unreceivedOrder")
    public Result findUnreceivedOrder();

    @GetMapping("/order/unrateOrder/{currentPage}/{pageSize}")
    public Result findUnrateOrder(@PathVariable  Integer currentPage, @PathVariable  Integer pageSize);

}
