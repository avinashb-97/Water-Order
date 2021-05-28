package com.rubicon.Rubicon.Water.service;

import com.rubicon.Rubicon.Water.exception.CannotCancelException;
import com.rubicon.Rubicon.Water.exception.InvalidDataException;
import com.rubicon.Rubicon.Water.exception.OrderNotFoundException;
import com.rubicon.Rubicon.Water.exception.SlotAlreadyBookedException;
import com.rubicon.Rubicon.Water.model.Order;
import com.rubicon.Rubicon.Water.model.OrderStatus;
import com.rubicon.Rubicon.Water.model.repository.OrderRepository;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.DateTimeException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

    private Map<Long, ScheduledFuture> startSchedulesCache = new HashMap<>();
    private Map<Long, ScheduledFuture> stopSchedulesCache = new HashMap<>();

    Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found, farmId -> "+id));
    }

    private List<Order> findInCompleteOrdersByFarmId(long farmId)
    {
        return orderRepository.findAllByFarmIdAndStatusIn(farmId, Arrays.asList(OrderStatus.REQUESTED, OrderStatus.INPROGRESS));
    }

    private Date getEndTime(Date startTime, int duration)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        calendar.add(Calendar.MINUTE, duration);
        return calendar.getTime();
    }

    private boolean isOverLappingWithExistingOrder(long farmId, Date currOrderStartTime, int duration)
    {
        List<Order> farmOrders = findInCompleteOrdersByFarmId(farmId);
        Date currOrderEndTime = getEndTime(currOrderStartTime, duration);
        for(Order order : farmOrders)
        {
            Date orderStartTime = order.getStartDateTime();
            Date orderEndTime = getEndTime(orderStartTime, order.getDuration());
            if(orderStartTime.before(currOrderEndTime) && currOrderStartTime.before(orderEndTime))
                return true;
        }
        return false;
    }

    @Override
    public Order createOrder(Order order) {
        if(isOverLappingWithExistingOrder(order.getFarmId(), order.getStartDateTime(), order.getDuration()))
        {
            throw new SlotAlreadyBookedException("Slot already exits in given time, farmId -> "+order.getFarmId());
        }
        if(order.getStartDateTime().before(new Date()))
        {
            throw new InvalidDataException("Start time should not be in past");
        }
        order.setStatus(OrderStatus.REQUESTED);
        order = orderRepository.save(order);
        logger.info("New water order for farm "+order.getFarmId()+" created, OrderId: "+order.getId());
        scheduleTaskForOrder(order, true);
        return order;
    }

    private void scheduleTaskForOrder(Order order, boolean isStartTask)
    {
        Runnable task = isStartTask ? () -> startWaterDelivery(order) : () -> endWaterDelivery(order);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        Date initialTime = isStartTask ? order.getStartDateTime() : getEndTime(order.getStartDateTime(), order.getDuration());
        Long timeDifference = initialTime.getTime() - new Date().getTime();
        ScheduledFuture scheduledFuture = executorService.schedule(task, timeDifference, TimeUnit.MILLISECONDS);
        if (isStartTask) {
            startSchedulesCache.put(order.getId(), scheduledFuture);
        } else {
            stopSchedulesCache.put(order.getId(), scheduledFuture);
        }
    }

    private void startWaterDelivery(Order order)
    {
        logger.info("Water delivery for farm "+order.getFarmId()+" started, OrderId: "+order.getId());
        updateOrderStatus(order, OrderStatus.INPROGRESS);
        scheduleTaskForOrder(order, false);
    }

    private void endWaterDelivery(Order order)
    {
        logger.info("Water delivery for farm "+order.getFarmId()+" stopped, OrderId : "+order.getId());
        updateOrderStatus(order, OrderStatus.DELIVERED);
    }

    private Order updateOrderStatus(Order order, OrderStatus orderStatus)
    {
        order.setStatus(orderStatus);
        return orderRepository.save(order);
    }

    @Override
    public Order cancelOrder(long id) {
        Order order = getOrderById(id);
        if(order.getStatus().equals(OrderStatus.DELIVERED) || order.getStatus().equals(OrderStatus.CANCELLED) )
        {
            throw new CannotCancelException("Order has been already Delivered/Cancelled");
        }
        else if(order.getStatus().equals(OrderStatus.REQUESTED))
        {
            startSchedulesCache.get(id).cancel(false);
            logger.info("Water order cancelled for farm "+order.getFarmId()+", OrderId: "+order.getId());
        }
        else if(order.getStatus().equals(OrderStatus.INPROGRESS))
        {
            stopSchedulesCache.get(id).cancel(false);
            logger.info("Water delivery for farm "+order.getFarmId()+" stopped, OrderId : "+order.getId());
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        return order;
    }
}
