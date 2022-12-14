//
////Это рабочая версия кастомной аутентификации
//
//
//package com.example.springsecurityproject.security;
//
//import com.example.springsecurityproject.services.PersonDetailsService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.stereotype.Component;
//
//import java.util.Collections;
//
//@Component
//public class AuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider {
//
//    private final PersonDetailsService personDetailsService;
//
//    @Autowired
//    public AuthenticationProvider(PersonDetailsService personDetailsService) {
//        this.personDetailsService = personDetailsService;
//    }
//
//
//    //Логика по аутентификации в приложении
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        //Получаем логин с формы аутентификации
//        //Spring Security сам возьмеи объект с формы и передаст сюда
//        String login = authentication.getName();
//
//        //Получаем пароль с формы аутентификации
//        String password = authentication.getCredentials().toString();
//
//        //Т.к. данный метод возвращает игтерфейс UserDetails
//        //то и объект мы создаем ч/з интерфейс
//        UserDetails person = personDetailsService.loadUserByUsername(login);
//
//        //Если пароль с формы аутентификации не равен паролю пользователя из БД
//        //который найден по логину
//        if (!password.equals(person.getPassword())){
//            throw new BadCredentialsException("Не верный пароль");
//        }
//
//        //Возвращаем объект аутентификации,
//        //в данном объекте будет лежать пользователь, который аутентифицировался,
//        //его пароль, его права доступа(списое ролей)
//        return new UsernamePasswordAuthenticationToken(person, password, Collections.emptyList());
//
//    }
//
//    //тут указываем когда наша аутентификация будет работать
//    @Override
//    public boolean supports(Class<?> authentication) {
//        return true;
//    }
//}
