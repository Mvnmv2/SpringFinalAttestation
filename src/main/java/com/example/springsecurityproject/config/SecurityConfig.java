package com.example.springsecurityproject.config;

//import com.example.springsecurityapplication.security.AuthenticationProvider;

import com.example.springsecurityproject.services.PersonDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//Основной конфиг для конфигурации безопасности в приложении
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PersonDetailsService personDetailsService;

    @Autowired
    public SecurityConfig(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }


    //Внедряем объект аутентификации в котором прописана вся логика
//    private final AuthenticationProvider authenticationProvider;
//
//    @Autowired
//    public SecurityConfig(AuthenticationProvider authenticationProvider) {
//        this.authenticationProvider = authenticationProvider;
//    }

    //Метод по настройке аутентификации
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        //Производим аутентификацию с помощью сервиса
        authenticationManagerBuilder.userDetailsService(personDetailsService)
        //На этапе аутентификации пароль будет зашифрован для дальнейшей проверки с БД
                .passwordEncoder(getPasswordEncoder());
//        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
    }

    // Конфигурируем сам Spring Security
    // конфигурируем авторизацию
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Отключаем защиту от межсайтовой подделки запросов -> .csrf().disable()
                http
                // Указываем что все страницы должны быть защищены аутентификацией
                .authorizeRequests()
                //Страница /admin доступна пользователю с ролью админа
                //.antMatchers("/admin").hasAnyRole("ADMIN")
                // Указываем что не аутентифицированные пользователи могут заходить на страницу
                // с формой аутентификации и на объект ошибки
                // С помощью permitAll указываем что данные страницы по умолчанию
                // доступны всем пользователям
                .antMatchers("/login", "/auth/login", "/error", "/auth/registration", "/product", "/product/info{id}", "/img/**",
                        "/product/search", "/main").permitAll()
                // Указываем что все остальные страницы доступны как пользователю с ролью USER так и с ADMIN
                .anyRequest().hasAnyRole("USER", "ADMIN")
                // Указываем что для всех остальных страниц необходимо вызывать метод authenticated,
                // который открываем форму аутентификации
                //.anyRequest().authenticated()
                // Указываем что дальше конфигурироваться будет аутентификация
                // и соединяем аутентификацию с настройкой доступа
                .and()
                // Указываем какой url запрос будет отправляться при заходе на закрытые страницы
                .formLogin().loginPage("/auth/login")
                //указываем на акой url будут отправляться данный с формы,
                //Теперь не нужно создавать метод в контроллере, спринг сам обработает пароль и логин
                //с формы и сверит их с БД
                .loginProcessingUrl("/process_login")
                //Указываем на какой url необходимо направить пользователя после успешной аутентификации
                //  Вторым аргументом ставим true чтобы перенаправление
                // на данную страницу шло в любом случае при успешной аутентификации
                .defaultSuccessUrl("/index", true)
                // Указываем куда необходимо перенаправить пользователя при проваленной аутентификации
                // В url будет передан объект. Данный объект мы будем проверять на форме
                // и если он есть будет выводить сообщение "Неправильный логин или пароль"
                .failureForwardUrl("/auth/login")
                .and()
                //Указываем что при переходе на /logout будет очищена сэссиия пользователя
                //и происходит перенаправление на /auth/login"
                .logout().logoutUrl("/logout").logoutSuccessUrl("/auth/login");

    }

    @Override
    public void configure(WebSecurity web) throws Exception{
        web.ignoring()
                .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "img/**", "/icon/**", "/main");
    }

    //Отключаем шифрование паролей ставим после return в PasswordEncoder -> NoOpPasswordEncoder.getInstance();
    @Bean
    public PasswordEncoder getPasswordEncoder() {
    //Указываем что используется шифрование паролей
        return new  BCryptPasswordEncoder();
    }

}
