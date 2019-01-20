package com.dm.zipkinconsumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZipkinApiConfig {

    @Value("${zipkin.api.url}")
    String URL;

    @Value("${zipkin.api.base-path}")
    String BASE_PATH;

    @Value("${zipkin.api.interval}")
    Integer INTERVAL;

    public String getURL(String path) {
        return String.format("%s/%s/%s", URL, BASE_PATH, path);
    }
}
