package com.rubicon.Rubicon.Water.service;

import com.rubicon.Rubicon.Water.model.Order;

import java.util.List;

public interface OrderService {

    public List<Order> getAllOrders();

    public Order getOrderById(long id);

    public Order createOrder(Order order);

    public Order cancelOrder(long id);
}
