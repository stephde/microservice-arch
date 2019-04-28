package com.dm.zipkinconsumer;

import com.dm.events.DependencyEvent;
import com.dm.zipkinconsumer.events.Publisher;
import com.dm.zipkinconsumer.models.Dependency;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
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

        List<Dependency> response;
        try {

            Dependency[] array = restTemplate.getForObject(url, Dependency[].class);
            response = Arrays.asList(array);
        } catch (HttpStatusCodeException ex) {
            log.error("Error while accessing dependencies endpoint: {}", ex.getMessage());
            log.error(ex.getResponseBodyAsString());
            log.error(ex.getResponseHeaders().toString());
            return Collections.emptyList();
        } catch (Exception ex) {
            log.error("Unkown exception thrown during dependency fetch: {}", ex.getMessage());
            return Collections.emptyList();
        }

        log.info("Received {} dependency objects from zipkin", response);
        return response;
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

    public void setZipkinUrl(String url) {
        this.config.setURL(url);
    }

    public String getZipkinUrl() {
        return this.config.URL;
    }

    public Integer getInterval() {
        return this.config.INTERVAL;
    }

    public void setInterval(Integer interval) {
        this.config.setINTERVAL(interval);
    }

    private DependencyEvent createDependencyEvent(Dependency dependency) {
        DependencyEvent event = new DependencyEvent(dependency.getParent(), DateTime.now(), DateTime.now());
        event.setCallCount(dependency.getCallCount());
        event.setErrorCount(dependency.getErrorCount());
        event.setCalledServiceName(dependency.getChild());
        return event;
    }
}
