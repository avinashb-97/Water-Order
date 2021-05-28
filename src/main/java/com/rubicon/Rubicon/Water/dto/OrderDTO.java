package com.rubicon.Rubicon.Water.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rubicon.Rubicon.Water.model.Order;
import com.rubicon.Rubicon.Water.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.RepresentationModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO{

    private long id;

    private long farmId;

    @JsonFormat(timezone = JsonFormat.DEFAULT_TIMEZONE)
    private Date startDateTime;

    private OrderStatus status;

    private int duration;

    public static OrderDTO convertEntityToOrderDTO(Order order)
    {
        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(order, orderDTO);
        return orderDTO;
    }

    public static List<OrderDTO> convertEntityListToOrderDTOList(List<Order> orders)
    {
        List<OrderDTO> orderDTOS = new ArrayList<>();
        for(Order order : orders)
        {
            orderDTOS.add(convertEntityToOrderDTO(order));
        }
        return orderDTOS;
    }

    public static Order convertOrderDTOToEntity(OrderDTO orderDTO)
    {
        Order order = new Order();
        BeanUtils.copyProperties(orderDTO, order);
        return order;
    }

}
