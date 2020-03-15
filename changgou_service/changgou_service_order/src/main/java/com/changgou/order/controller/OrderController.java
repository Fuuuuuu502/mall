package com.changgou.order.controller;
import com.aliyuncs.exceptions.ClientException;
import com.changgou.entity.PageResult;
import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.entity.UnrateResult;
import com.changgou.order.config.TokenDecode;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.OrderItemService;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.order.config.TokenDecode;

import com.changgou.order.pojo.OrderItem;

import com.changgou.order.pojo.Collet;

import com.changgou.order.service.OrderService;
import com.changgou.order.pojo.Order;
import com.changgou.util.SMSUtils;
import com.github.pagehelper.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import java.util.Map;
@RestController
@CrossOrigin
@RequestMapping("/order")
public class OrderController {


    @Autowired
    private OrderService orderService;
    @Autowired
    private SkuFeign skuFeign;

    /**
     * 查询全部数据
     * @return
     */
    @GetMapping
    public Result findAll(){
        List<Order> orderList = orderService.findAll();
        return new Result(true, StatusCode.OK,"查询成功",orderList) ;
    }

    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Order> findById(@PathVariable("id") String id){
        Order order = orderService.findById(id);
        return new Result(true,StatusCode.OK,"查询成功",order);
    }

    @Autowired
    private TokenDecode tokenDecode;

    /***
     * 新增数据
     * @param order
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Order order){
        //获取登录人名称
        String username = tokenDecode.getUserInfo().get("username");
        order.setUsername(username);
        String orderId = orderService.add(order);
        return new Result(true,StatusCode.OK,"添加成功",orderId);
    }


    /***
     * 修改数据
     * @param order
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    public Result update(@RequestBody Order order,@PathVariable String id){
        order.setId(id);
        orderService.update(order);
        return new Result(true,StatusCode.OK,"修改成功");
    }


    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable String id){
        orderService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 多条件搜索品牌数据
     * @param searchMap
     * @return
     */
    @GetMapping(value = "/search" )
    public Result findList(@RequestParam Map searchMap){
        List<Order> list = orderService.findList(searchMap);
        return new Result(true,StatusCode.OK,"查询成功",list);
    }


    /***
     * 分页搜索实现
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    public Result findPage(@RequestParam Map searchMap, @PathVariable  int page, @PathVariable  int size){
        Page<Order> pageList = orderService.findPage(searchMap, page, size);
        PageResult pageResult=new PageResult(pageList.getTotal(),pageList.getResult());
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    @PostMapping("/batchSend")
    public Result batchSend(@RequestBody List<Order> orders){
        orderService.batchSend(orders);
        return new Result(true,StatusCode.OK,"发货成功");
    }


    //1.1.3 展示当前用户待发货列表，并实现提醒发货发送短信
    /**
     * cxy
     * 2020年3月11日
     */
    @GetMapping("/unshippeDorder")
    public Result findUnshippedOrder(){
        String username = tokenDecode.getUserInfo().get("username");
        List<OrderItem> orderItemLists = orderService.findUnshippedOrder(username);
        if (orderItemLists==null){
            return new Result(false,StatusCode.ERROR,"您当前没有待发货订单，快去下单吧");
        }
        return new Result(true,StatusCode.OK,"查询成功",orderItemLists);
    }

    //1.1.3 提醒发货发送短信
    /**
     * cxy
     * 2020年3月11日
     */
    @RequestMapping("/reminderShip")
    public Result reminderShip(){
        String name = tokenDecode.getUserInfo().get("username");
        try {
            String code = "547412";
            String phone = "17695857959";
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,phone,code);
        } catch (ClientException e) {
            e.printStackTrace();
            return new Result(false,StatusCode.ERROR,"短信发送失败");
        }
        return new Result(true,StatusCode.OK,"查询成功");
    }


    //1.1.4 待收货     展示当前用户待收货列表
    /**
     * cxy
     * 2020年3月10日
     */
    @GetMapping("/unreceivedOrder")
    public Result findUnreceivedOrder(){
        String username = tokenDecode.getUserInfo().get("username");
        List<OrderItem> orderItemLists = orderService.findUnreceivedOrder(username);
        if (orderItemLists==null){
            return new Result(false,StatusCode.ERROR,"您当前没有待收货订单，快去下单吧");
        }
        return new Result(true,StatusCode.OK,"查询成功",orderItemLists);
    }


    //1.1.4 确认收货
    /**
     * cxy
     * 2020年3月10日
     */
    @PostMapping("/confirmTask/{orderId}")
    public Result confirmTask(@PathVariable String orderId){
        String username = tokenDecode.getUserInfo().get("username");
        orderService.confirmTask(orderId,username);
        return new Result(true,StatusCode.OK,"确认收货成功");
    }


    //1.1.5 待评价    用户购买成功后，商品是待评价状态，用户可以评价相关的商品。
    /**
     * cxy
     * 2020年3月10日
     */
    @GetMapping(value = "/unrateOrder/{currentPage}/{pageSize}" )
    public Result findUnrateOrder(@PathVariable  Integer currentPage, @PathVariable  Integer pageSize){
        if (currentPage==null || currentPage < 1){      //默认为第1页
            currentPage = 1;
        }
        if (pageSize==null || pageSize < 1){            //默认页面数据为5条
            pageSize = 5;
        }
        String username = tokenDecode.getUserInfo().get("username");
        UnrateResult<OrderItem> unrateOrder = orderService.findUnrateOrder(username,currentPage,pageSize);
//        List<OrderItem> page1 = page(unrateOrder, pageSize, currentPage);
        if (unrateOrder==null){
            return new Result(false,StatusCode.ERROR,"您当前没有待评价订单");
        }
        return new Result(true,StatusCode.OK,"查询成功",unrateOrder);
    }

    @GetMapping(value = "/orderDetail/{orderId}")
    public Result orderDetail(@PathVariable String orderId){
        if (orderId==null){
            return new Result(false,StatusCode.ERROR,"当前订单不存在");
        }
        return new Result(true,StatusCode.OK,"查询成功",orderService.findOrderDetailById(orderId));
    }
//    /**
//     * 循环截取某页列表进行分页
//     * @param dataList 分页数据
//     * @param pageSize  页面大小
//     * @param currentPage   当前页面
//     */
//    public static List<OrderItem> page(List<OrderItem> dataList, int pageSize,int currentPage) {
//
//        List<OrderItem> currentPageList = new ArrayList<>();
//        if (dataList != null && dataList.size() > 0) {
//            int currIdx = (currentPage > 1 ? (currentPage - 1) * pageSize : 0);
//            for (int i = 0; i < pageSize && i < dataList.size() - currIdx; i++) {
//                OrderItem data = dataList.get(currIdx + i);
//                currentPageList.add(data);
//            }
//        }
//        return currentPageList;
//    }


    //根据用户查询所有订单
    @GetMapping("/allOrder")
    public Result findAllOrder(){
        String username = tokenDecode.getUserInfo().get("username");
        List<Map<String,Object>> orderItemList = orderService.findAllOrder(username);
        //如果orderItemList为空
        if (orderItemList==null){
            return new Result(false,StatusCode.ERROR,"您当前还没有订单，快去下单吧");
        }
        return new Result(true,StatusCode.OK,"查询成功",orderItemList);
    }
    //待付款
    @GetMapping("/waitPay")
    public Result waitPay(){
        String username = tokenDecode.getUserInfo().get("username");
        List<Map<String, Object>> orderList = orderService.waitPay(username);
        if (orderList==null || orderList.size()<=0){
            return new Result(false,StatusCode.ERROR,"当前没有待付款商品");
        }
        return new Result(true,StatusCode.OK,"查询成功",orderList);
    }
    //取消订单
    @GetMapping("/cancel")
    public Result cancelOrder(String orderId) {
        orderService.cancelOrder(orderId);
//        String username = tokenDecode.getUserInfo().get("username");
//        List<Map<String, Object>> orderItemList = orderService.findAllOrder(username);
        return new Result(true, StatusCode.OK, "取消订单成功");
    }
        @GetMapping("/collet/{skuId}")
        public Result collet (@PathVariable("skuId") String skuId){
            String username = tokenDecode.getUserInfo().get("username");
            if (StringUtils.isEmpty(username)) {
                return new Result(false, StatusCode.ERROR, "请先登录，再进行收藏");
            }
            boolean collet = orderService.collet(username, skuId);
            if (collet) {
                return new Result(true, StatusCode.OK, "已收藏成功");
            } else {
                return new Result(false, StatusCode.ERROR, "收藏失败");
            }
        }

        @GetMapping("/collet")
        public Result findCollet (){
            ArrayList<Sku> list = new ArrayList<>();
            String username = tokenDecode.getUserInfo().get("username");
            List<String> colletSkuIds = orderService.findCollet(username);
            if (colletSkuIds != null && colletSkuIds.size() > 0) {
                for (String colletSkuId : colletSkuIds) {
                    list.add(skuFeign.findById(colletSkuId).getData());
                }
            }
            return new Result(true, StatusCode.OK, "查询成功", list);
        }
}
