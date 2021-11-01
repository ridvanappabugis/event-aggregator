package com.ridvan.eventaggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EventAggregatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventAggregatorApplication.class, args);
    }

}
