package com.rubicon.Rubicon.Water.model.repository;

import com.rubicon.Rubicon.Water.model.Order;
import com.rubicon.Rubicon.Water.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    List<Order> findAllByFarmIdAndStatusIn(long farmId, List<OrderStatus> statuses);

}
