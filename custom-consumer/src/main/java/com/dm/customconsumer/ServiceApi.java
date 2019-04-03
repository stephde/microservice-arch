package com.dm.customconsumer;

import com.dm.customconsumer.events.Publisher;
import com.dm.events.PropertyEvent;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class ServiceApi {

    private final ServiceApiConfig config;
    private final RestTemplate restTemplate;
    private final Publisher publisher;

    private boolean isRunning = false;
    // we can add 'lookback' parameter to specify the timeframe
    // see https://zipkin.io/zipkin-api/#/default/get_dependencies

    @Autowired
    public ServiceApi(ServiceApiConfig config, RestTemplate restTemplate, Publisher publisher) {
        this.config = config;
        this.restTemplate = restTemplate;
        this.publisher = publisher;

        this.startUpdating();
    }

    public Integer fetchMetrics() {
        log.info("Requesting: {}", config.getURL("metrics"));

        ResponseEntity<Integer> response = restTemplate.exchange(
                config.getURL("metrics"),
                HttpMethod.GET,
                null,
                Integer.class);

        log.info("Received response from inventory service: {}", response.getBody());
        return response.getBody();
    }

    private void updateDependencies() {
        DateTime endTime = DateTime.now();
        Integer value = fetchMetrics();

        publisher.publishEvent(createPropertyEvent(value));
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

    public void setServiceUrl(String url) {
        this.config.setURL(url);
    }

    public String getServiceUrl() {
        return this.config.URL;
    }

    public Integer getInterval() {
        return this.config.INTERVAL;
    }

    public void setInterval(Integer interval) {
        this.config.setINTERVAL(interval);
    }

    private PropertyEvent createPropertyEvent(Integer value) {
        PropertyEvent event = new PropertyEvent(config.getService(), DateTime.now());
        event.setPropertyName("httpExceptionCount");
        event.setValue(value.toString());
        return event;
    }
}
