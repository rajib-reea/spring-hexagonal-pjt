package com.csio.hexagonal.infrastructure.rest.router.operation.corpib;

import com.csio.hexagonal.infrastructure.rest.handler.CorpibProcedureHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;

@Configuration
public class CorpibRouter {

    @Bean
    public RouterFunction<ServerResponse> corpibRoute(CorpibProcedureHandler handler) {
        return route()
                .POST("/api/v1/corpib/act-info", handler::dprSrcActInfo,
                        ops -> ops.beanClass(CorpibProcedureHandler.class).beanMethod("dprSrcActInfo"))
                .POST("/api/v1/corpib/cbs-account-info", handler::dprCbsAccountInfo,
                        ops -> ops.beanClass(CorpibProcedureHandler.class).beanMethod("dprCbsAccountInfo"))
                .build();
    }
}
