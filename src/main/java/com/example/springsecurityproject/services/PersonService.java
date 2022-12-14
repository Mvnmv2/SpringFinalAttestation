package com.example.springsecurityproject.services;


//Сервис, отыечающий за работу с моделью

import com.example.springsecurityproject.models.Person;
import com.example.springsecurityproject.models.Product;
import com.example.springsecurityproject.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PersonService {
    private final PersonRepository personRepository;

    //Внедряем PasswordEncoder для шифрования паролей
    final PasswordEncoder passwordEncoder;

    @Autowired
    public PersonService(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //Метод по получению всех пользователей
    public List<Person> getAllPerson(){
        return personRepository.findAll();
    }

    //Метод для получения пользователя по логину
    public Person getPersonFindByLogin(Person person){
        Optional<Person> person_db = personRepository.findByLogin(person.getLogin());
        return person_db.orElse(null);
    }

    public Person getPersonById(int id){
        Optional<Person> person_id = personRepository.findById(id);
        return person_id.orElse(null);
    }

    //Данный метод позволяет обновить пользовател по id
    @Transactional
    public void updatePerson(int id, Person person){
        person.setId(id);
        personRepository.save(person);
    }

    //Данный метод позволяет удалить пользовател по id
    @Transactional
    public void deletePerson(int id){
        personRepository.deleteById(id);
    }


    //Метод по регистрации
    @Transactional
    public void register(Person person){
        //Получаем пароль из person -> шифруем его с помощью passwordEncoder.encode
        //-> и устанавливаем его в защифпрванном виде
        //Т.о. при регистрации в БД попадет пользователь с зашифрованным паролем
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setRole("ROLE_USER");
        personRepository.save(person);
    }


}
