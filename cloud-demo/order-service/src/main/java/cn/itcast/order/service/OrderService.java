package cn.itcast.order.service;

import cn.itcast.feign.client.RemoteUserService;
import cn.itcast.feign.pojo.User;
import cn.itcast.order.mapper.OrderMapper;
import cn.itcast.order.pojo.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RemoteUserService remoteUserService;

    public Order queryOrderById(Long orderId) {
        // 1.查询订单
        Order order = orderMapper.findById(orderId);
        User user = remoteUserService.queryById(order.getUserId());
        order.setUser(user);
        // 4.返回
        return order;
    }
}
