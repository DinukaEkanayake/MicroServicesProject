package com.university.OrderService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

//configurations about webclient class using spring webflux
@Configuration
public class WebClientConfig {

    //create a bean of type webclient.@bean creates a bean with the method name
    @Bean
    public WebClient webClient(){
        return WebClient.builder().build();
    }

}
