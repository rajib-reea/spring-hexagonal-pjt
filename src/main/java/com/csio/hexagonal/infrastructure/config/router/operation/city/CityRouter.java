package com.csio.hexagonal.infrastructure.config.router.operation.city;


import com.csio.hexagonal.infrastructure.rest.handler.CityHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;

@Configuration
public class CityRouter {

    @Bean
    public RouterFunction<ServerResponse> cityRoute(CityHandler handler) {
        return route()
                .POST("/api/v1/city", handler::createCity,
                        ops -> ops.beanClass(CityHandler.class).beanMethod("createCity"))
                // .GET("/api/v1/city/{uid}", handler::getCity,
                //         ops -> ops.beanClass(CityHandler.class).beanMethod("getCity"))
                // .POST("/api/v1/city/all", handler::getAllCities,
                //         ops -> ops.beanClass(CityHandler.class).beanMethod("getAllCities"))
                // .PUT("/api/v1/city/{uid}", handler::updateCity,
                //         ops -> ops.beanClass(CityHandler.class).beanMethod("updateCity"))
                // .DELETE("/api/v1/city/{uid}", handler::deleteCity,
                //         ops -> ops.beanClass(CityHandler.class).beanMethod("deleteCity"))
                .build();
    }
}