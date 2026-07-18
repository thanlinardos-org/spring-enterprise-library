package com.thanlinardos.spring_enterprise_library;

import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;

/**
 * Entry point for the Spring Cloud Security Library application.
 */
@NoArgsConstructor
public class SpringCloudSecurityLibraryApplication {

    /**
     * The main method to run the Spring Boot application.
     *
     * @param args command line arguments.
     */
    static void main(String[] args) {
        SpringApplication.run(HttpsClientLibraryApplication.class, args);
    }
}
