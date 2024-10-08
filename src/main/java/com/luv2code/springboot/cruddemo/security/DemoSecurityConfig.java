package com.luv2code.springboot.cruddemo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class DemoSecurityConfig {


    //add support for JDBC ... no more hardcoded users  :-)

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource){

        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

        //define a query to retrieve user by userName
        jdbcUserDetailsManager.setUsersByUsernameQuery("select user_id,pw,active from members where user_id=?");
        //define a query to retrieve the authorities/roles by userName
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("select user_id,role from roles where user_id=?" );

        return jdbcUserDetailsManager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{  //SecurityFilterChain is an Interface

        http.authorizeHttpRequests (configurer->
                configurer
                        .requestMatchers(HttpMethod.GET,"/api/employees").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.GET,"/api/employees/**").hasRole("EMPLOYEE") //** is used like a wild card
                        .requestMatchers(HttpMethod.POST,"/api/employees").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT,"/api/employees").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE,"/api/employees/**").hasRole("ADMIN")

        );

        //use HTTP Basic authentication

        http.httpBasic(Customizer.withDefaults());

        //disable Cross Site request Forgery (CSRF)

        //in general , not required for stateless REST APIs that use POST, DELETE, PUT and /or PATCH

        http.csrf(csrf -> csrf.disable());

        return http.build();
    }

     /* @Bean
    public InMemoryUserDetailsManager userDetailsManager(){  //InMemoryUserDetailsManager implements UserDetailsManager interface

        UserDetails john = User.builder()  //User implements UserDetails Interface
                .username("john")
                .password("{noop}test123")
                .roles("EMPLOYEE")
                .build();

        UserDetails mary =User.builder()
                .username("mary")
                .password("{noop}test123")
                .roles("EMPLOYEE","MANAGER")
                .build();

        UserDetails susan = User.builder()

                    .username("susan")
                    .password("{noop}test123")
                    .roles("EMPLOYEE","MANAGER","ADMIN")
                    .build();


        return new InMemoryUserDetailsManager(john,mary,susan);
    }
*/
}


