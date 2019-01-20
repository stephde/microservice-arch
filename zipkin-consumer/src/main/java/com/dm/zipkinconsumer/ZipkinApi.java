package com.dm.zipkinconsumer;

import com.dm.events.DependencyEvent;
import com.dm.zipkinconsumer.events.Publisher;
import com.dm.zipkinconsumer.models.DependeciesResponse;
import com.dm.zipkinconsumer.models.Dependency;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ZipkinApi {

    @Autowired
    ZipkinApiConfig config;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    Publisher publisher;

    private boolean isRunning = false;
    // we can add 'lookback' parameter to specify the timeframe
    // see https://zipkin.io/zipkin-api/#/default/get_dependencies
    private final String DEPENDENCIES_PATH_TEMPLATE = "dependencies?endTs=%s";

    private List<Dependency> fetchDependencies(DateTime endTime) {
        String pathWithArgs = String.format(DEPENDENCIES_PATH_TEMPLATE, endTime.getMillis());
        String url = config.getURL(pathWithArgs);

        ResponseEntity<DependeciesResponse> response = restTemplate.getForEntity(url, DependeciesResponse.class);
        return response.getBody().getDependencies();
    }

    private void updateDependencies() {
        DateTime endTime = DateTime.now();
        List<Dependency> dependencies = fetchDependencies(endTime);

        dependencies.stream()
                .map(this::createDependencyEvent)
                .forEach(publisher::publishEvent);
    }

    private void doUpdate() {
        while (isRunning) {
            updateDependencies();

            //sleep 5 seconds
            try {
                Thread.sleep(config.INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startUpdating() {
        this.isRunning = true;

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
