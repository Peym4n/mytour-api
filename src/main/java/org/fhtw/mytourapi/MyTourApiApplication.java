package org.fhtw.mytourapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class MyTourApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyTourApiApplication.class, args);
    }

}
