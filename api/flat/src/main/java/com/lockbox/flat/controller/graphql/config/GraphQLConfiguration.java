package com.lockbox.flat.controller.graphql.config;

import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQLConfiguration {
    @Bean
    public GraphQLScalarType longScalar() {
        return ExtendedScalars.GraphQLLong;
    }

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer(GraphQLScalarType longScalar) {
        return wiringBuilder -> wiringBuilder.scalar(longScalar);
    }
}
