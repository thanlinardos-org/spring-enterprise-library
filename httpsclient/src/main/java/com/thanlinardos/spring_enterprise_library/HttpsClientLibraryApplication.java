package com.thanlinardos.spring_enterprise_library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for the HTTPS Client Library Spring Boot application.
 */
@SpringBootApplication
public class HttpsClientLibraryApplication {

    /**
     * Default constructor.
     */
    public HttpsClientLibraryApplication() {
        // Default constructor
    }

    /**
     * The main method to run the Spring Boot application.
     *
     * @param args command line arguments.
     */
    static void main(String[] args) {
        SpringApplication.run(HttpsClientLibraryApplication.class, args);
    }
}
