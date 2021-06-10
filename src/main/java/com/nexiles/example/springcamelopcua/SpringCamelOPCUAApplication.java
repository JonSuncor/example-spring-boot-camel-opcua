/*
 * Copyright (c) 2019 nexiles GmbH.
 */

package com.nexiles.example.springcamelopcua;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.boot.CamelSpringBootApplicationController;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties
public class SpringCamelOPCUAApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringCamelOPCUAApplication.class, args);
        CamelSpringBootApplicationController controller = context.getBean(CamelSpringBootApplicationController.class);
        controller.run();
    }

}

@Data
@Configuration
@ConfigurationProperties(prefix = "nexiles")
class Config {

    /**
     * OPC endpoint
     */
    String endpoint;

}

@Slf4j
@Component
class OPCUARouteBuilder extends RouteBuilder {

    private final Config config;

    OPCUARouteBuilder(Config config) {
        this.config = config;
        log.debug("Config: {}", config);
    }

    /**
     * <b>Called on initialization to build the routes using the fluent builder syntax.</b>
     * <p/>
     * This is a central method for RouteBuilder implementations to implement
     * the routes using the Java fluent builder syntax.
     */
    @Override
    public void configure() {
        log.debug("Configuring route for endpoint: {}", config.getEndpoint());

        List<String> nodeHeaders = new ArrayList<>();
//        nodeHeaders.add("node=RAW(i=2258)");

        nodeHeaders.add("node=RAW(ns=12;s=DipperLoad)");
        nodeHeaders.add("node=RAW(ns=13;s=DipperLoad)");
        nodeHeaders.add("node=RAW(ns=14;s=DipperLoad)");
        nodeHeaders.add("node=RAW(ns=15;s=DipperLoad)");
        nodeHeaders.add("node=RAW(ns=16;s=DipperLoad)");
        nodeHeaders.add("node=RAW(ns=19;s=DipperLoad)");
        nodeHeaders.add("node=RAW(ns=20;s=DipperLoad)");

        log.info("Adding route: ");
        from(config.getEndpoint()).routeId("Test Route ")
                .setHeader("CamelMiloNodeIds", constant(Arrays.asList(nodeHeaders)))
                .process(exchange -> {
                    String routeId = exchange.getFromRouteId();
                    DataValue data = exchange.getIn().getBody(DataValue.class);
                    log.info("Route '{}': Status: {}, Value: {}",
                            routeId,
                            data.getStatusCode().toString(), data.getValue().getValue());
                });
    }
}
