package com.box.user.filter;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Log4j2
public class LoggingFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("Request path: {}", exchange.getRequest().getPath());
        log.info("Request headers: {}", exchange.getRequest().getHeaders());
        return chain.filter(exchange);
    }
}
