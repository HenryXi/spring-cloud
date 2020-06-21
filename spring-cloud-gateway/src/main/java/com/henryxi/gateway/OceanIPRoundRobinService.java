package com.henryxi.gateway;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Iterator;

@Component
public class OceanIPRoundRobinService {
    private Iterator<String> oceanIPCircular;

    @PostConstruct
    private void init() {
        oceanIPCircular = Iterables.cycle(Lists.newArrayList("192.168.56.6:8081", "192.168.56.6:8080")).iterator();
    }

    public String getHost() {
        return oceanIPCircular.next();
    }
}
