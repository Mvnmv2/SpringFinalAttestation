package com.example.springsecurityproject.repositories;

import com.example.springsecurityproject.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    //Получаем запись из БД по логину
    Optional<Person> findByLogin(String login);

    Optional<Person> findById(int id);

    List<Person> findAll();


}
