package com.example.springsecurityproject.security;

import com.example.springsecurityproject.models.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class PersonDetails implements UserDetails {

    private final Person person;

    @Autowired
    public PersonDetails(Person person) {
        this.person = person;
    }


    @Override
    //Создаем коллекцию -> указываем что она сост. из 1-го элемента(singletonList)
    //т.к. у пользователя может быть только одна роль -> и передаем в нее роль пользователя
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(person.getRole()));
    }

    //Метод позволяет получить пароль пользовтеля
    @Override
    public String getPassword() {
        return this.person.getPassword();
    }

    //Метод позволяет получить логин пользовтеля
    @Override
    public String getUsername() {
        return this.person.getLogin();
    }

    //Аккаунт действителен
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //Аккаунт не заблокирован
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //Пароль действительный/валидный
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //Аккаунт активен
    @Override
    public boolean isEnabled() {
        return true;
    }

    //Метод позволяет получить всего пользовтеля
    public Person getPerson(){
        return this.person;
    }


}
