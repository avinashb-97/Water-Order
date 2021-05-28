package com.rubicon.Rubicon.Water.controller;

import com.rubicon.Rubicon.Water.dto.OrderDTO;
import com.rubicon.Rubicon.Water.model.Order;
import com.rubicon.Rubicon.Water.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders()
    {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(OrderDTO.convertEntityListToOrderDTOList(orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable long id)
    {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(OrderDTO.convertEntityToOrderDTO(order));
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO)
    {
        Order order = orderService.createOrder(OrderDTO.convertOrderDTOToEntity(orderDTO));
        orderDTO = OrderDTO.convertEntityToOrderDTO(order);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(order.getId())
                .toUri();
        return ResponseEntity.created(location).body(orderDTO);
    }

    @PostMapping("/{id}/cancel")
    public void createOrder(@PathVariable long id)
    {
        orderService.cancelOrder(id);
    }
}
