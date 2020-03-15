package com.changgou.order.service;

import com.changgou.entity.PageResult;
import com.changgou.entity.UnrateResult;
import com.changgou.order.pojo.Order;
import com.changgou.order.pojo.OrderItem;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

/**
 * @author cxy
 * 2020年3月10日
 */


public interface OrderService {

    /***
     * 查询所有
     * @return
     */
    List<Order> findAll();

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    Order findById(String id);

    /***
     * 新增
     * @param order
     */
    String add(Order order);

    /***
     * 修改
     * @param order
     */
    void update(Order order);

    /***
     * 删除
     * @param id
     */
    void delete(String id);

    /***
     * 多条件搜索
     * @param searchMap
     * @return
     */
    List<Order> findList(Map<String, Object> searchMap);

    /***
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    Page<Order> findPage(int page, int size);

    /***
     * 多条件分页查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    Page<Order> findPage(Map<String, Object> searchMap, int page, int size);

    //修改订单的支付状态,并记录日志
    void updatePayStatus(String orderId, String transactionId);

    void closeOrder(String message);

    void batchSend(List<Order> orders);

    //手动确认收货
    void confirmTask(String orderId,String operator);

    void autoTack();






    //展示当前用户待收货列表
    /**
     * cxy
     * 2020年3月10日
     */
    List<OrderItem> findUnreceivedOrder(String username);

    //1.1.3 展示当前用户待发货列表，并实现提醒发货发送短信
    /**
     * cxy
     * 2020年3月11日
     */
    List<OrderItem> findUnshippedOrder(String username);

    //1.1.4 待收货     展示当前用户待收货列表
    /**
     * cxy
     * 2020年3月10日
     */
    UnrateResult<OrderItem> findUnrateOrder(String username, int currentPage, int pageSize);

    Map findOrderDetailById(String orderId);


    //根据用户查询所有订单项
    List<Map<String,Object>> findAllOrder(String username);

    //待付款
    public List<Map<String,Object>> waitPay(String username);

    //取消订单
    void cancelOrder(String orderId);

    //根据用户查询所有订单
    //List<Order> findOrder(String username);


    boolean collet(String username, String skuId);

    List<String> findCollet(String username);

}
