package com.dm.customconsumer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceApiConfig {

    @Value("${service.api.url}")
    @Setter
    String URL;

    @Getter
    String service = "inventoryservice";

    @Value("${service.api.interval}")
    @Setter
    Integer INTERVAL;

    public String getURL(String path) {
        return String.format("http://%s.%s/%s", service, URL, path);
    }
}
