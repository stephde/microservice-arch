package com.dm.customconsumer;

import com.dm.customconsumer.events.Publisher;
import com.dm.events.PropertyEvent;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class EventSpammer {

    private Integer INTERVAL = 30;
    private final Publisher publisher;

    private boolean isRunning = false;
    // we can add 'lookback' parameter to specify the timeframe
    // see https://zipkin.io/zipkin-api/#/default/get_dependencies

    @Autowired
    public EventSpammer(Publisher publisher) {
        this.publisher = publisher;

        this.startUpdating();
    }

    private void updateDependencies() {
        Integer value = (int )(Math.random() * 4 + 1);

        publisher.publishEvent(createPropertyEvent(value));
    }

    private void doUpdate() {
        while (this.isRunning) {
            updateDependencies();

            //sleep 5 seconds
            try {
                log.info("Waiting for {} s before making next call", INTERVAL / 1000);
                Thread.sleep(INTERVAL);
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

    public Integer getInterval() {
        return INTERVAL;
    }

    public void setInterval(Integer interval) {
        this.INTERVAL = interval;
    }

    private PropertyEvent createPropertyEvent(Integer value) {
        PropertyEvent event = new PropertyEvent("zipkin", DateTime.now());
        event.setPropertyName("httpExceptionCount");
        event.setValue(value.toString());
        return event;
    }
}
