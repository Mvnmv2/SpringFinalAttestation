package com.example.springsecurityproject.services;

import com.example.springsecurityproject.models.Order;
import com.example.springsecurityproject.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order getAllOrders(int id, Order order){
        order.setId(id);
        orderRepository.save(order);
        return order;
    }



    // Данный метод позволяет вернуть все заказы
    public List<Order> getAllOrders(){
        return orderRepository.findAll();
    }

    // Данный метод позволяет обновить данные пользователя
    @Transactional
    public void updateOrder(int id, Order order){
        order.setId(id);
        orderRepository.save(order);

    }
    @Transactional
    public void updateOrderStatus( Order order) {
        orderRepository.save(order);
    }
    //    @Transactional
    public Order getOrderById(int id) {
        Optional<Order> optionalOrder=orderRepository.findById(id);
        return optionalOrder.orElse(null);
    }
}
