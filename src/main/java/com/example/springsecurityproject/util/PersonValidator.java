package com.example.springsecurityproject.util;

import com.example.springsecurityproject.models.Person;
import com.example.springsecurityproject.services.PersonService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class PersonValidator implements Validator {

    private final PersonService personService;

    public PersonValidator(PersonService personService) {
        this.personService = personService;
    }


    //В данном методе указываем для какой модели предназначен
    //данный валидатор
    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;
        //Если метод по поиску пользователя по логину не равен нулю,
        //тогда такой логин уже существует -> выводим ошибку
        if (personService.getPersonFindByLogin(person) != null){
            errors.rejectValue("login", "", "Логин занят!");
        }
    }
}
