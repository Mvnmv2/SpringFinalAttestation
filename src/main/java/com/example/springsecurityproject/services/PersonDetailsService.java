package com.example.springsecurityproject.services;

import com.example.springsecurityproject.models.Person;
import com.example.springsecurityproject.repositories.PersonRepository;
import com.example.springsecurityproject.security.PersonDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

//Сервис, отвечающий за работу с валидацией
@Service
public class PersonDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //Получаем пользовател из таблицы person по логину с формы аутентификации
        Optional<Person> person = personRepository.findByLogin(username);
        if (person.isEmpty()){
            //Выбрасываем исключение что данный пользователь не найден
            //исключение булет поймано Spring Security
            // и сообщение будет выведено на страницу
            throw new UsernameNotFoundException("Пользователь не найден");
        }
        //Возвращаем объект пользователя
        return new PersonDetails(person.get());
    }
}

