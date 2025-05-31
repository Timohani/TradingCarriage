package org.timowa.megabazar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity(securedEnabled = true)
public class MegaBazarApplication {

    public static void main(String[] args) {
        SpringApplication.run(MegaBazarApplication.class, args);
    }

}
