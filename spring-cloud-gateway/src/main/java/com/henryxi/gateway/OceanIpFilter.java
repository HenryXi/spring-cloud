package com.henryxi.gateway;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

@Component
public class OceanIpFilter implements GlobalFilter, Ordered {

    private static final Log log = LogFactory.getLog(OceanIpFilter.class);

    @Autowired
    private OceanIPRoundRobinService oceanIPRoundRobinService;

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
        if (route == null) {
            return chain.filter(exchange);
        }
        log.trace("id:"+exchange.getRequest().getId());
        URI uri = exchange.getRequest().getURI();
        URI routeUri = route.getUri();
        String ipHost = getOceanJobIPHost(routeUri.getHost());
        String[] ipHostArray = ipHost.split(":");
        String host = ipHostArray[0];
        String port = ipHostArray[1];
        URI mergedUrl = UriComponentsBuilder.fromUri(uri)
                .scheme("http").host(host)
                .port(port).build().toUri();
        exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, mergedUrl);
        return chain.filter(exchange);
    }

    private String getOceanJobIPHost(String host) {
        return oceanIPRoundRobinService.getHost();
    }
}
