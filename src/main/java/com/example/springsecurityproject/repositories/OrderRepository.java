package com.example.springsecurityproject.repositories;


import com.example.springsecurityproject.models.Order;
import com.example.springsecurityproject.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByPerson(Person person);

    List<Order> findAll();

    Optional<Order> findById(int id);

    //Поиск по последним сомволам номера заказа
    List<Order> findByNumberEndingWith(String endingWith);


}
