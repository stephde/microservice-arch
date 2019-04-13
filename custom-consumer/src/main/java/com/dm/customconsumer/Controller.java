package com.dm.customconsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://fb14srv7.hpi.uni-potsdam.de:1801", maxAge = 3600)
public class Controller {

    @Autowired
    private ServiceApi serviceApi;
    @Autowired
    private EventSpammer eventSpammer;

    @GetMapping("/metrics")
    public Integer getMetrics() {
        return serviceApi.fetchMetrics();
    }

    @PostMapping("/start")
    public void startLoop() {
        serviceApi.startUpdating();
    }

    @PostMapping("/stop")
    public void stopLoop() {
        serviceApi.stopUpdating();
    }

    @GetMapping("/serviceurl")
    public String getMetricsUrl() {
        return serviceApi.getServiceUrl();
    }

    @PostMapping("/serviceurl")
    public void setMetricsUrl(@RequestBody String url) {
        serviceApi.setServiceUrl(url);
    }

    @GetMapping("/interval")
    public Integer getInterval() {
        return serviceApi.getInterval();
    }

    @PostMapping("/interval")
    public void setInterval(@RequestBody Integer interval) {
        serviceApi.setInterval(interval);
    }
    @GetMapping("/spam/interval")
    public Integer getSpamInterval() {
        return eventSpammer.getInterval();
    }

    @PostMapping("/spam/interval")
    public void setSpamInterval(@RequestBody Integer interval) {
        eventSpammer.setInterval(interval);
    }

    @PostMapping("/spam/start")
    public void startSpamLoop() {
        eventSpammer.startUpdating();
    }

    @PostMapping("/spam/stop")
    public void stopSpamLoop() {
        eventSpammer.stopUpdating();
    }
}
