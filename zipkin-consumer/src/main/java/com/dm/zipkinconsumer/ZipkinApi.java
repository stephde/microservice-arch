package com.dm.zipkinconsumer;

import com.dm.events.DependencyEvent;
import com.dm.zipkinconsumer.events.Publisher;
import com.dm.zipkinconsumer.models.Dependency;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class ZipkinApi {

    private final ZipkinApiConfig config;
    private final RestTemplate restTemplate;
    private final Publisher publisher;

    private boolean isRunning = false;
    // we can add 'lookback' parameter to specify the timeframe
    // see https://zipkin.io/zipkin-api/#/default/get_dependencies
    private final String DEPENDENCIES_PATH_TEMPLATE = "dependencies?endTs=%s";

    @Autowired
    public ZipkinApi(ZipkinApiConfig config, RestTemplate restTemplate, Publisher publisher) {
        this.config = config;
        this.restTemplate = restTemplate;
        this.publisher = publisher;

        this.startUpdating();
    }

    public List<Dependency> fetchDependencies(DateTime endTime) {
        log.info("Fetching dependencies from zipkin...");
        String pathWithArgs = String.format(DEPENDENCIES_PATH_TEMPLATE, endTime.getMillis());
        String url = config.getURL(pathWithArgs);

        log.info("Requesting: {}", url);

        ResponseEntity<List<Dependency>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Dependency>>() {});

        log.info("Received {} dependecy objects from zipkin", response.getBody().size());
        return response.getBody();
    }

    private void updateDependencies() {
        DateTime endTime = DateTime.now();
        List<Dependency> dependencies = fetchDependencies(endTime);

        dependencies.stream()
                .map(this::createDependencyEvent)
                .forEach(publisher::publishEvent);
    }

    private void doUpdate() {
        while (this.isRunning) {
            updateDependencies();

            //sleep 5 seconds
            try {
                log.info("Waiting for {} s before making next call", config.INTERVAL / 1000);
                Thread.sleep(config.INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startUpdating() {
        this.isRunning = true;

        log.info("Starting the updating loop...");
        CompletableFuture.runAsync(this::doUpdate);
    }

    public void stopUpdating() {
        this.isRunning = false;
    }

    private DependencyEvent createDependencyEvent(Dependency dependency) {
        DependencyEvent event = new DependencyEvent(dependency.getParent(), DateTime.now(), DateTime.now());
        event.setCallCount(dependency.getCallCount());
        event.setErrorCount(dependency.getErrorCount());
        event.setCalledServiceName(dependency.getChild());
        return event;
    }
}
